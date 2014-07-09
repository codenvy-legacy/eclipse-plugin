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
final class StringBufferStreamMonitor implements IStreamMonitor {
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
