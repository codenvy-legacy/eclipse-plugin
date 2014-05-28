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
package com.codenvy.eclipse.ui.launcher;

import static org.eclipse.ui.browser.IWorkbenchBrowserSupport.LOCATION_BAR;
import static org.eclipse.ui.browser.IWorkbenchBrowserSupport.NAVIGATION_BAR;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.codenvy.eclipse.core.launcher.CodenvyRunnerProcess;

/**
 * Track the run on codenvy console output.
 * 
 * @author Kevin Pollet
 */
public class RunOnCodenvyConsoleLineTracker implements IConsoleLineTracker {
    private static final Pattern serverStartedPattern = Pattern.compile("^Infos: Server startup in [0-9]+ ms$");

    private IConsole             console;

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
                final CodenvyRunnerProcess codenvyRunnerProcess = (CodenvyRunnerProcess)console.getProcess();

                workbench.getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (codenvyRunnerProcess.getWebLink() != null) {
                                workbench.getBrowserSupport()
                                         .createBrowser(NAVIGATION_BAR | LOCATION_BAR, null, null, null)
                                         .openURL(new URL(codenvyRunnerProcess.getWebLink().href));
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
