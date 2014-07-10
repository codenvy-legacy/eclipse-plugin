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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;

/**
 * Track the build on codenvy console output.
 * 
 * @author Kevin Pollet
 */
public final class BuildOnCodenvyConsoleLineTracker implements IConsoleLineTracker {
    private static final String  DOWNLOAD_LINK_PREFIX = "Link to download build result: ";
    private static final Pattern downloadLinkPattern  = Pattern.compile("^" + DOWNLOAD_LINK_PREFIX + "(.+)$");

    private IConsole             console;

    @Override
    public void init(IConsole console) {
        this.console = console;
    }

    @Override
    public void lineAppended(IRegion line) {
        try {

            final Matcher matcher = downloadLinkPattern.matcher(console.getDocument().get(line.getOffset(), line.getLength()));
            if (matcher.find()) {
                final String downloadLink = matcher.group(1);
                console.addLink(new WebHyperLink(downloadLink), line.getOffset() + DOWNLOAD_LINK_PREFIX.length(), downloadLink.length());
            }

        } catch (BadLocationException e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public void dispose() {
    }
}
