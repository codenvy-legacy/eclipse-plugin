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
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.eclipse.core.runtime.IStatus.ERROR;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;

import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyRunnerStatus;
import com.codenvy.eclipse.core.model.CodenvyRunnerStatus.Link;
import com.codenvy.eclipse.core.services.RunnerService;

/**
 * The codenvy runner process.
 * 
 * @author Kevin Pollet
 */
public class CodenvyRunnerProcess implements IProcess {
    private static final int               TICK_DELAY     = 500;
    private static final TimeUnit          TICK_TIME_UNIT = MILLISECONDS;

    private final ILaunch                  launch;
    private final RunnerService            runnerService;
    private final CodenvyProject           project;
    private final Map<String, String>      attributes;
    private final AtomicBoolean            terminated;
    private final AtomicBoolean            running;
    private final long                     processId;
    private final ScheduledExecutorService executorService;
    private final CodenvyRunnerLogsThread  codenvyRunnerLogsThread;
    private Link                           webLink;

    public CodenvyRunnerProcess(ILaunch launch, RunnerService runner, CodenvyProject project) {
        this.launch = launch;
        this.runnerService = runner;
        this.project = project;
        this.attributes = new HashMap<>();
        this.terminated = new AtomicBoolean(false);
        this.running = new AtomicBoolean(false);
        this.executorService = Executors.newScheduledThreadPool(4);
        this.codenvyRunnerLogsThread = new CodenvyRunnerLogsThread();

        this.attributes.put(IProcess.ATTR_PROCESS_TYPE, getClass().getName());
        launch.addProcess(this);

        final CodenvyRunnerStatus runnerStatus = runnerService.run(project);
        this.processId = runnerStatus.processId;

        executorService.scheduleAtFixedRate(new CodenvyRunnerStatusThread(), 0, TICK_DELAY, TICK_TIME_UNIT);
        executorService.scheduleAtFixedRate(codenvyRunnerLogsThread, 0, TICK_DELAY, TICK_TIME_UNIT);
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
        return terminated.get();
    }

    @Override
    public void terminate() throws DebugException {
        runnerService.stop(project, processId);
        terminated.set(true);

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
                return null;
            }

            @Override
            public IStreamMonitor getOutputStreamMonitor() {
                return codenvyRunnerLogsThread;
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
        return 0;
    }

    public Link getWebLink() {
        return webLink;
    }

    private void fireDebugEvent(int kind) {
        DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[]{new DebugEvent(this, kind)});
    }

    /**
     * {@link Runnable} polling the codenvy runner status.
     * 
     * @author Kevin Pollet
     */
    class CodenvyRunnerStatusThread implements Runnable {
        @Override
        public void run() {
            final CodenvyRunnerStatus runnerStatus = runnerService.status(project, processId);
            terminated.set(runnerStatus.status == CodenvyRunnerStatus.Status.STOPPED);

            if (runnerStatus.status == CodenvyRunnerStatus.Status.RUNNING) {
                webLink = runnerStatus.getWebLink();
                running.set(true);
            }
        }
    }

    /**
     * {@link Runnable} polling the codenvy runner logs.
     * 
     * @author Kevin Pollet
     */
    class CodenvyRunnerLogsThread implements Runnable, IStreamMonitor {
        private final Set<IStreamListener> listeners;
        private final StringBuffer         logs;
        private boolean                    flushed;

        public CodenvyRunnerLogsThread() {
            this.listeners = new HashSet<>();
            this.logs = new StringBuffer();
            this.flushed = false;
        }

        @Override
        public void run() {
            if (running.get()) {
                final String fullLogs = runnerService.logs(project, processId).trim();
                final String logsDiff = fullLogs.substring(logs.length());

                if (!logsDiff.isEmpty()) {
                    append(logsDiff);
                    flushed = false;
                } else if (!flushed && fullLogs.equals(logs.toString())) {
                    flush();
                }
            }
        }

        @Override
        public void addListener(IStreamListener listener) {
            synchronized (listeners) {
                listeners.add(listener);
            }
        }

        @Override
        public String getContents() {
            return logs.toString();
        }

        private void append(String text) {
            logs.append(text);
            fireStreamAppend(text);
        }

        private void flush() {
            fireStreamAppend("\n");
            flushed = true;
        }

        private void fireStreamAppend(String text) {
            for (IStreamListener oneListener : listeners) {
                oneListener.streamAppended(text, this);
            }
        }

        @Override
        public void removeListener(IStreamListener listener) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }
    }
}
