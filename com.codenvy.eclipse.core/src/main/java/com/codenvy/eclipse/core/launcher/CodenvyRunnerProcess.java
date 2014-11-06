/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.core.launcher;

import static com.codenvy.client.model.RunnerState.CANCELLED;
import static com.codenvy.client.model.RunnerState.FAILED;
import static com.codenvy.client.model.RunnerState.STOPPED;
import static com.codenvy.eclipse.core.CodenvyPlugin.PLUGIN_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.eclipse.core.runtime.IStatus.ERROR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;

import com.codenvy.client.Codenvy;
import com.codenvy.client.CodenvyAPI;
import com.codenvy.client.CodenvyErrorException;
import com.codenvy.client.model.Link;
import com.codenvy.client.model.ProjectReference;
import com.codenvy.client.model.RunnerState;
import com.codenvy.client.model.RunnerStatus;
import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.core.CodenvyProjectMetadata;
import com.codenvy.eclipse.core.launcher.CodenvyRunnerProcess.WebApplicationListener.WebApplicationEvent;

/**
 * The codenvy runner process.
 * 
 * @author Kevin Pollet
 */
public final class CodenvyRunnerProcess implements IProcess {
    private static final int                  STATUS_CHECKER_INTERVAL        = 500;
    private static final TimeUnit             STATUS_CHECKER_TIME_UNIT       = MILLISECONDS;
    private static final int                  URL_CHECKER_INTERVAL           = 1000;
    private static final int                  URL_CHECKER_NUMBER_OF_ATTEMPTS = 30;

    private final ILaunch                     launch;
    private final Codenvy                     codenvy;
    private final ProjectReference            project;
    private final Map<String, String>         attributes;
    private volatile RunnerState              status;
    private final Object                      statusLock;
    private long                              processId;
    private final ScheduledExecutorService    executorService;
    private final StringBufferStreamMonitor   outputStream;
    private final StringBufferStreamMonitor   errorStream;
    private int                               exitValue;
    private final Set<WebApplicationListener> listeners;
    private volatile boolean                  webApplicationStarted;
    private final Object                      webApplicationStartedLock;

    /**
     * Constructs an instance of {@link CodenvyRunnerProcess}.
     *
     * @param launch the {@link ILaunch} object.
     * @param projectMetadata the {@link CodenvyProjectMetadata}.
     * @throws NullPointerException if launch or codenvyMetaProject parameter is {@code null}.
     */
    public CodenvyRunnerProcess(ILaunch launch, CodenvyProjectMetadata projectMetadata) {
        checkNotNull(projectMetadata);

        this.launch = checkNotNull(launch);
        this.project = CodenvyAPI.getClient().newProjectBuilder().withName(projectMetadata.projectName)
                                 .withWorkspaceId(projectMetadata.workspaceId)
                                 .build();

        this.codenvy = CodenvyPlugin.getDefault()
                                    .getCodenvyBuilder(projectMetadata.url, projectMetadata.username)
                                    .build();

        this.attributes = new HashMap<>();
        this.executorService = Executors.newScheduledThreadPool(4);
        this.outputStream = new StringBufferStreamMonitor();
        this.errorStream = new StringBufferStreamMonitor();
        this.exitValue = 0;
        this.listeners = new HashSet<>();
        this.webApplicationStarted = false;
        this.statusLock = new Object();
        this.webApplicationStartedLock = new Object();

        this.attributes.put(ATTR_PROCESS_TYPE, getClass().getName());
        launch.addProcess(this);

        try {

            final RunnerStatus runnerStatus = codenvy.runner()
                                                     .run(project)
                                                     .execute();

            this.processId = runnerStatus.processId();
            this.status = runnerStatus.status();

            executorService.scheduleAtFixedRate(new RunnerStatusChecker(), 0, STATUS_CHECKER_INTERVAL, STATUS_CHECKER_TIME_UNIT);

        } catch (CodenvyErrorException e) {
            terminateWithAnError(e);
        }
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        if (ILaunch.class.equals(adapter)) {
            return getLaunch();
        }
        return null;
    }

    @Override
    public boolean canTerminate() {
        return !isTerminated();
    }

    @Override
    public boolean isTerminated() {
        synchronized (statusLock) {
            return status == STOPPED || status == CANCELLED || status == FAILED;
        }
    }

    @Override
    public void terminate() throws DebugException {
        try {

            final RunnerStatus runnerStatus = codenvy.runner()
                                                     .stop(project, processId)
                                                     .execute();
            synchronized (statusLock) {
                status = runnerStatus.status();
            }

            stopProcess();

        } catch (CodenvyErrorException e) {
            terminateWithAnError(e);
        }
    }

    private void terminateWithAnError(CodenvyErrorException exception) {
        errorStream.append("Error: " + exception.getMessage());
        exitValue = exception.getStatus();

        synchronized (statusLock) {
            status = FAILED;
        }

        stopProcess();
    }

    private void stopProcess() {
        executorService.shutdownNow();
        fireDebugEvent(DebugEvent.TERMINATE);

        synchronized (webApplicationStartedLock) {
            if (webApplicationStarted) {
                fireWebApplicationStoppedEvent();
            }
        }
    }

    @Override
    public String getLabel() {
        return "Running project on Codenvy";
    }

    @Override
    public ILaunch getLaunch() {
        return launch;
    }

    @Override
    public IStreamsProxy getStreamsProxy() {
        return new IStreamsProxy() {
            @Override
            public void write(String input) throws IOException {
            }

            @Override
            public IStreamMonitor getErrorStreamMonitor() {
                return errorStream;
            }

            @Override
            public IStreamMonitor getOutputStreamMonitor() {
                return outputStream;
            }
        };
    }

    @Override
    public void setAttribute(String key, String value) {
        attributes.put(key, value);
        fireDebugEvent(DebugEvent.CHANGE);
    }

    @Override
    public String getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public int getExitValue() throws DebugException {
        if (!isTerminated()) {
            throw new DebugException(new Status(ERROR, PLUGIN_ID, "Process not yet terminated"));
        }
        return exitValue;
    }

    /**
     * Adds an {@link WebApplicationListener}.
     *
     * @param listener the {@link WebApplicationListener} to add.
     * @return {@code true} if the {@link WebApplicationListener} is not already added, {@code false} otherwise.
     */
    public boolean addWebApplicationListener(WebApplicationListener listener) {
        synchronized (listeners) {
            return listeners.add(listener);
        }
    }

    /**
     * Removes an {@link WebApplicationListener}.
     *
     * @param listener the {@link WebApplicationListener} to remove.
     * @return {@code true} if the {@link WebApplicationListener} is removed, {@code false} otherwise.
     */
    public boolean removeWebApplicationListener(WebApplicationListener listener) {
        synchronized (listeners) {
            return listeners.remove(listener);
        }
    }

    private void fireWebApplicationStartedEvent(Link webApplicationLink) {
        synchronized (listeners) {
            for (WebApplicationListener oneListener : listeners) {
                oneListener.webApplicationStarted(new WebApplicationEvent(webApplicationLink));
            }
        }
    }

    private void fireWebApplicationStoppedEvent() {
        synchronized (listeners) {
            for (WebApplicationListener oneListener : listeners) {
                oneListener.webApplicationStopped();
            }
        }
    }

    private void fireDebugEvent(int kind) {
        DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[]{new DebugEvent(this, kind)});
    }

    /**
     * {@link Runnable} polling the runner status and logs.
     *
     * @author Kevin Pollet
     */
    private class RunnerStatusChecker implements Runnable {
        private static final String WAITING_FOR_RUNNER_MESSAGE = "Waiting for available runner";

        @Override
        public void run() {
            try {

                final RunnerStatus runnerStatus = codenvy.runner()
                                                         .status(project, processId)
                                                         .execute();

                final Link webApplicationURL = runnerStatus.getWebLink();
                synchronized (webApplicationStartedLock) {
                    if (webApplicationURL != null && !webApplicationStarted) {
                        new WebApplicationURLChecker(URL_CHECKER_INTERVAL, URL_CHECKER_NUMBER_OF_ATTEMPTS, webApplicationURL).start();
                        webApplicationStarted = true;
                    }
                }

                synchronized (statusLock) {
                    status = runnerStatus.status();

                    switch (status) {
                        case NEW: {
                            final String waitingString = outputStream.getContents().isEmpty() ? WAITING_FOR_RUNNER_MESSAGE : ".";
                            outputStream.append(waitingString);
                        }
                            break;

                        case RUNNING: {
                            appendRunnerLogs();
                        }
                            break;

                        default: {
                            appendRunnerLogs();
                            stopProcess();
                        }
                    }
                }

            } catch (CodenvyErrorException e) {
                terminateWithAnError(e);

            } catch (IOException e) {
                // ignore we read a string
            }
        }

        private void appendRunnerLogs() throws IOException {
            final String outputStreamContent = outputStream.getContents();

            if (outputStreamContent.matches("^" + WAITING_FOR_RUNNER_MESSAGE + "[.]*$")) {
                outputStream.append("\n");
            }

            final String logs = codenvy.runner()
                                       .logs(project, processId)
                                       .execute()
                                       .trim();

            final BufferedReader logsReader = new BufferedReader(new StringReader(logs));

            String line;
            while ((line = logsReader.readLine()) != null) {
                if (!outputStreamContent.contains(line)) {
                    outputStream.append(line + "\n");
                }
            }
        }
    }

    /**
     * Thread checking when the web application is started.
     *
     * @author Kevin Pollet
     */
    private class WebApplicationURLChecker extends Thread {
        private final long interval;
        private long       numberOfAttemps;
        private final Link webApplicationURL;

        /**
         * Constructs an instance of {@link WebApplicationURLChecker}.
         *
         * @param interval the checking interval.
         * @param numberOfAttempts the number of attempts.
         * @param webApplicationURL the web application {@link Link} to check.
         * @throws NullPointerException if webApplicationURL parameter is {@code null}.
         */
        public WebApplicationURLChecker(long interval, long numberOfAttempts, Link webApplicationURL) {
            this.interval = interval;
            this.numberOfAttemps = numberOfAttempts;
            this.webApplicationURL = checkNotNull(webApplicationURL);
        }

        @Override
        public void run() {
            for (int i = 0; i < numberOfAttemps && !Thread.interrupted() && !isTerminated(); i++) {
                try {

                    final URL url = new URL(webApplicationURL.href());
                    final HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                    connection.setRequestMethod("HEAD");
                    connection.setConnectTimeout(1000);
                    connection.setReadTimeout(1000);

                    try {

                        Thread.sleep(interval);

                    } catch (InterruptedException e) {
                        return;
                    }

                    if (connection.getResponseCode() == 200) {
                        fireWebApplicationStartedEvent(webApplicationURL);
                        return;
                    }

                } catch (IOException e) {
                    // ignore the exception
                }
            }
        }
    }

    /**
     * The {@link WebApplicationListener} interface.
     *
     * @author Kevin Pollet
     */
    public static interface WebApplicationListener {
        /**
         * Called when the web application is started.
         *
         * @param event the {@link WebApplicationEvent} instance.
         */
        void webApplicationStarted(WebApplicationEvent event);

        /**
         * Called when the web application is stopped.
         */
        void webApplicationStopped();

        /**
         * The {@link WebApplicationEvent} class.
         *
         * @author Kevin Pollet
         */
        public static class WebApplicationEvent {
            public final Link webApplicationLink;

            /**
             * Constructs an instance of {@link WebApplicationEvent}.
             *
             * @param webApplicationLink the web application {@link Link}.
             */
            private WebApplicationEvent(Link webApplicationLink) {
                this.webApplicationLink = webApplicationLink;
            }
        }
    }
}
