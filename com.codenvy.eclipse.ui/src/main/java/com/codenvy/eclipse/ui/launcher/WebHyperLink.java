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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.console.IHyperlink;

/**
 * A Web hyper link implementation.
 * 
 * @author Kevin Pollet
 */
public class WebHyperLink implements IHyperlink {
    private final String url;

    public WebHyperLink(String url) {
        this.url = url;
    }

    @Override
    public void linkEntered() {
    }

    @Override
    public void linkExited() {
    }

    @Override
    public void linkActivated() {
        final IWorkbenchBrowserSupport workbenchBrowserSupport = PlatformUI.getWorkbench().getBrowserSupport();
        try {

            final IWebBrowser browser = workbenchBrowserSupport.getExternalBrowser();
            browser.openURL(new URL(url));

        } catch (PartInitException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
