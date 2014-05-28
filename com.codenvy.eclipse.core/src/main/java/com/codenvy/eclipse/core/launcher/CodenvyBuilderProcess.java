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
import static com.codenvy.eclipse.core.model.CodenvyBuilderStatus.Status.CANCELLED;
import static com.codenvy.eclipse.core.model.CodenvyBuilderStatus.Status.FAILED;
import static com.codenvy.eclipse.core.model.CodenvyBuilderStatus.Status.IN_PROGRESS;
import static com.codenvy.eclipse.core.model.CodenvyBuilderStatus.Status.SUCCESSFUL;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.eclipse.core.runtime.IStatus.ERROR;

import java.io.IOException;
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
import com.codenvy.eclipse.core.model.CodenvyBuilderStatus;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.Link;
import com.codenvy.eclipse.core.services.BuilderService;

/**
 * The codenvy builder process.
 * 
 * @author Kevin Pollet
 */
public class CodenvyBuilderProcess implements IProcess {
    private static final int                     TICK_DELAY     = 500;
    private static final TimeUnit                TICK_TIME_UNIT = MILLISECONDS;

    private final ILaunch                        launch;
    private final BuilderService                 builderService;
    private final CodenvyProject                 project;
    private final Map<String, String>            attributes;
    private long                                 taskId;
    private final ScheduledExecutorService       executorService;
    private final CodenvyBuilderLogsThread       codenvyBuilderLogsThread;
    private final StringBufferStreamMonitor      outputStream;
    private final StringBufferStreamMonitor      errorStream;
    private int                                  exitValue;
    private volatile CodenvyBuilderStatus.Status status;
    private volatile Link                        downloadLink;

    /**
     * Constructs an instance of {@link CodenvyBuilderProcess}.
     * 
     * @param launch the {@link ILaunch} object.
     * @param builderService the {@link BuilderService}.
     * @param project the {@link CodenvyProject} to run.
     * @throws NullPointerException if launch, builderService or project parameter is {@code null}.
     */
    public CodenvyBuilderProcess(ILaunch launch, BuilderService builderService, CodenvyProject project) {
        this.launch = launch;
        this.builderService = builderService;
        this.project = project;
        this.attributes = new HashMap<>();
        this.executorService = Executors.newScheduledThreadPool(4);
        this.codenvyBuilderLogsThread = new CodenvyBuilderLogsThread();
        this.outputStream = new StringBufferStreamMonitor();
        this.errorStream = new StringBufferStreamMonitor();
        this.exitValue = 0;

        this.attributes.put(ATTR_PROCESS_TYPE, getClass().getName());
        launch.addProcess(this);

        try {

            final CodenvyBuilderStatus builderStatus = builderService.build(project);
            this.taskId = builderStatus.taskId;
            this.status = builderStatus.status;
            this.downloadLink = builderStatus.getDownloadLink();

            executorService.scheduleAtFixedRate(new CodenvyBuilderStatusThread(), 0, TICK_DELAY, TICK_TIME_UNIT);
            executorService.scheduleAtFixedRate(codenvyBuilderLogsThread, 0, TICK_DELAY, TICK_TIME_UNIT);

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
        return status == CANCELLED || status == SUCCESSFUL || status == FAILED;
    }

    @Override
    public void terminate() throws DebugException {
        try {

            builderService.cancel(project, taskId);
            status = CANCELLED;

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
        if (status == SUCCESSFUL) {
            synchronized (this) {
                outputStream.append("\n\nLink to download build result: " + downloadLink.href + "\n");
            }
        }

        executorService.shutdownNow();
        fireDebugEvent(DebugEvent.TERMINATE);
    }

    @Override
    public String getLabel() {
        return "Building project on Codenvy";
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
                outputStream.append(input);
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

    private void fireDebugEvent(int kind) {
        DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[]{new DebugEvent(this, kind)});
    }

    /**
     * {@link Runnable} polling the builder status.
     * 
     * @author Kevin Pollet
     */
    class CodenvyBuilderStatusThread implements Runnable {
        @Override
        public void run() {
            try {

                final CodenvyBuilderStatus builderStatus = builderService.status(project, taskId);
                status = builderStatus.status;

                synchronized (CodenvyBuilderProcess.this) {
                    downloadLink = builderStatus.getDownloadLink();
                }

                if (isTerminated()) {
                    stopProcess();
                }

            } catch (APIException e) {
                terminateWithAnError(e);
            }
        }
    }

    /**
     * {@link Runnable} polling the builder logs.
     * 
     * @author Kevin Pollet
     */
    class CodenvyBuilderLogsThread implements Runnable {
        @Override
        public void run() {
            if (status == IN_PROGRESS) {
                try {

                    final String fullLogs = builderService.logs(project, taskId).trim();
                    final String logsDiff = fullLogs.substring(outputStream.getContents().length());

                    if (!logsDiff.isEmpty()) {
                        outputStream.append(logsDiff);
                    }

                } catch (APIException e) {
                    terminateWithAnError(e);
                }
            }
        }
    }
}
