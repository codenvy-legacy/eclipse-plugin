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
package com.codenvy.eclipse.ui.launcher;

import static org.eclipse.ui.browser.IWorkbenchBrowserSupport.LOCATION_BAR;
import static org.eclipse.ui.browser.IWorkbenchBrowserSupport.NAVIGATION_BAR;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.codenvy.eclipse.core.launcher.CodenvyRunnerProcess;

/**
 * Track the run on Codenvy console output.
 * 
 * @author Kevin Pollet
 */
public final class RunOnCodenvyConsoleLineTracker implements IConsoleLineTracker {
    private static final Pattern serverStartedPattern = Pattern.compile("(.)*(Server startup|Started connect web server)(.)*");

    private IConsole             console;
    private final DebugPlugin    debugPlugin;

    public RunOnCodenvyConsoleLineTracker() {
        this.debugPlugin = DebugPlugin.getDefault();
    }

    @Override
    public void init(IConsole console) {
        this.console = console;
    }

    @Override
    public void lineAppended(IRegion line) {
        try {
            final Matcher matcher = serverStartedPattern.matcher(console.getDocument().get(line.getOffset(), line.getLength()));

            if (matcher.find()) {
                final IWorkbench workbench = PlatformUI.getWorkbench();
                final CodenvyRunnerProcess runnerProcess = (CodenvyRunnerProcess)console.getProcess();

                workbench.getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (runnerProcess.getWebLink() != null) {
                                final IWebBrowser browser = workbench.getBrowserSupport()
                                                                     .createBrowser(NAVIGATION_BAR | LOCATION_BAR, null, null, null);

                                browser.openURL(new URL(runnerProcess.getWebLink().href()));

                                debugPlugin.addDebugEventListener(new IDebugEventSetListener() {
                                    @Override
                                    public void handleDebugEvents(DebugEvent[] events) {
                                        for (DebugEvent oneEvent : events) {
                                            if (oneEvent.getKind() == DebugEvent.TERMINATE && runnerProcess.equals(oneEvent.getSource())) {
                                                browser.close();
                                                debugPlugin.removeDebugEventListener(this);
                                            }
                                        }
                                    }
                                });
                            }

                        } catch (PartInitException | MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

        } catch (BadLocationException e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public void dispose() {
    }
}
