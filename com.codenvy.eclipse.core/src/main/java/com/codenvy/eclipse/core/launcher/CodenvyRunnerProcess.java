/*
 * CODENVY CONFIDENTIAL
 * ________________
 * 
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.eclipse.core.launcher;

import static com.codenvy.eclipse.core.CodenvyPlugin.PLUGIN_ID;
import static com.codenvy.eclipse.core.model.RunnerStatus.Status.CANCELLED;
import static com.codenvy.eclipse.core.model.RunnerStatus.Status.FAILED;
import static com.codenvy.eclipse.core.model.RunnerStatus.Status.STOPPED;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.eclipse.core.runtime.IStatus.ERROR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
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

import com.codenvy.eclipse.core.exceptions.APIException;
import com.codenvy.eclipse.core.model.Link;
import com.codenvy.eclipse.core.model.Project;
import com.codenvy.eclipse.core.model.RunnerStatus;
import com.codenvy.eclipse.core.services.RunnerService;

/**
 * The codenvy runner process.
 * 
 * @author Kevin Pollet
 */
public class CodenvyRunnerProcess implements IProcess {
    private static final int                TICK_DELAY     = 500;
    private static final TimeUnit           TICK_TIME_UNIT = MILLISECONDS;

    private final ILaunch                   launch;
    private final RunnerService             runnerService;
    private final Project                   project;
    private final Map<String, String>       attributes;
    private volatile RunnerStatus.Status    status;
    private long                            processId;
    private final ScheduledExecutorService  executorService;
    private Link                            webLink;
    private final StringBufferStreamMonitor outputStream;
    private final StringBufferStreamMonitor errorStream;
    private int                             exitValue;
    private final Object                    webLinkLock;

    /**
     * Constructs an instance of {@link CodenvyRunnerProcess}.
     * 
     * @param launch the {@link ILaunch} object.
     * @param runnerService the {@link RunnerService}.
     * @param project the {@link Project} to run.
     * @throws NullPointerException if launch, runnerService or project parameter is {@code null}.
     */
    public CodenvyRunnerProcess(ILaunch launch, RunnerService runnerService, Project project) {
        this.launch = launch;
        this.runnerService = runnerService;
        this.project = project;
        this.attributes = new HashMap<>();
        this.executorService = Executors.newScheduledThreadPool(4);
        this.outputStream = new StringBufferStreamMonitor();
        this.errorStream = new StringBufferStreamMonitor();
        this.exitValue = 0;
        this.webLinkLock = new Object();

        this.attributes.put(ATTR_PROCESS_TYPE, getClass().getName());
        launch.addProcess(this);

        try {

            final RunnerStatus runnerStatus = this.runnerService.run(project);
            this.processId = runnerStatus.processId;
            this.status = runnerStatus.status;
            synchronized (webLinkLock) {
                this.webLink = runnerStatus.getWebLink();
            }

            executorService.scheduleAtFixedRate(new CodenvyRunnerStatusThread(), 0, TICK_DELAY, TICK_TIME_UNIT);

        } catch (APIException e) {
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
        return status == STOPPED || status == CANCELLED || status == FAILED;
    }

    @Override
    public void terminate() throws DebugException {
        try {

            runnerService.stop(project, processId);
            status = STOPPED;

            stopProcess();

        } catch (APIException e) {
            terminateWithAnError(e);
        }
    }

    private void terminateWithAnError(APIException exception) {
        errorStream.append("Error: " + exception.getMessage());
        exitValue = exception.getStatus();
        status = FAILED;

        stopProcess();
    }

    private void stopProcess() {
        executorService.shutdownNow();
        fireDebugEvent(DebugEvent.TERMINATE);
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
        synchronized (attributes) {
            attributes.put(key, value);
        }

        fireDebugEvent(DebugEvent.CHANGE);
    }

    @Override
    public String getAttribute(String key) {
        synchronized (attributes) {
            return attributes.get(key);
        }
    }

    @Override
    public int getExitValue() throws DebugException {
        if (!isTerminated()) {
            throw new DebugException(new Status(ERROR, PLUGIN_ID, "Process not yet terminated"));
        }
        return exitValue;
    }

    public Link getWebLink() {
        synchronized (webLinkLock) {
            return webLink;
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
    class CodenvyRunnerStatusThread implements Runnable {
        private static final String WAITING_FOR_RUNNER_MESSAGE = "Waiting for available runner";

        @Override
        public void run() {
            try {

                final RunnerStatus runnerStatus = runnerService.status(project, processId);
                status = runnerStatus.status;
                synchronized (webLinkLock) {
                    webLink = runnerStatus.getWebLink();
                }

                switch (status) {
                    case NEW: {
                        final String waitingString = outputStream.getContents().isEmpty() ? WAITING_FOR_RUNNER_MESSAGE : ".";
                        outputStream.append(waitingString);
                    }
                        break;

                    case RUNNING: {
                        final String outputStreamContent = outputStream.getContents();

                        if (outputStreamContent.matches("^" + WAITING_FOR_RUNNER_MESSAGE + "[.]*$")) {
                            outputStream.append("\n");
                        }

                        final String logs = runnerService.logs(project, processId).trim();
                        final BufferedReader logsReader = new BufferedReader(new StringReader(logs));

                        String line;
                        while ((line = logsReader.readLine()) != null) {
                            if (!outputStreamContent.contains(line)) {
                                outputStream.append(line + "\n");
                            }
                        }
                    }
                        break;

                    default: {
                        stopProcess();
                    }
                }

            } catch (APIException e) {
                terminateWithAnError(e);

            } catch (IOException e) {
                // ignore we read a string
            }
        }
    }
}
