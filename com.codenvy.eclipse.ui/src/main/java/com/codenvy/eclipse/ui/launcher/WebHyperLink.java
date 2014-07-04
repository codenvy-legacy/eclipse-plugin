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
