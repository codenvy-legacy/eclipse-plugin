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
public class BuildOnCodenvyConsoleLineTracker implements IConsoleLineTracker {
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
