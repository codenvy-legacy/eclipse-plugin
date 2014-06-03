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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;

/**
 * Implementation of an {@link IStreamMonitor} backed by a {@link StringBuffer}.
 * 
 * @author Kevin Pollet
 */
class StringBufferStreamMonitor implements IStreamMonitor {
    private final StringBuffer         stream;
    private final Set<IStreamListener> listeners;

    public StringBufferStreamMonitor() {
        this.stream = new StringBuffer();
        this.listeners = new HashSet<>();
    }

    @Override
    public void addListener(IStreamListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public String getContents() {
        return stream.toString();
    }

    @Override
    public void removeListener(IStreamListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void append(String text) {
        stream.append(text);
        fireStreamAppend(text);
    }

    private void fireStreamAppend(String text) {
        synchronized (listeners) {
            for (IStreamListener oneListener : listeners) {
                oneListener.streamAppended(text, this);
            }
        }
    }
}
