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

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.codenvy.eclipse.core.launcher.CodenvyRunnerProcess;
import com.codenvy.eclipse.core.launcher.CodenvyRunnerProcess.WebApplicationListener;

/**
 * Track the run on Codenvy console output.
 * 
 * @author Kevin Pollet
 */
public final class RunOnCodenvyConsoleLineTracker implements IConsoleLineTracker {
    @Override
    public void init(IConsole console) {
        final CodenvyRunnerProcess runnerProcess = (CodenvyRunnerProcess)console.getProcess();

        runnerProcess.addWebApplicationListener(new WebApplicationListener() {
            private IWebBrowser      webApplicationBrowser;
            private final IWorkbench workbench = PlatformUI.getWorkbench();

            @Override
            public void webApplicationStarted(final WebApplicationEvent event) {
                workbench.getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            webApplicationBrowser = workbench.getBrowserSupport()
                                                             .createBrowser(NAVIGATION_BAR | LOCATION_BAR, null, null, null);

                            webApplicationBrowser.openURL(new URL(event.webApplicationLink.href()));

                        } catch (PartInitException | MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            @Override
            public void webApplicationStopped() {
                workbench.getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (webApplicationBrowser != null) {
                            webApplicationBrowser.close();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void lineAppended(IRegion line) {
    }

    @Override
    public void dispose() {
    }
}
