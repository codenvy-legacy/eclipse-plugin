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
package com.codenvy.eclipse.ui;

import static org.eclipse.jface.window.Window.OK;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;

import com.codenvy.client.CodenvyAPI;
import com.codenvy.client.auth.Credentials;
import com.codenvy.client.auth.CredentialsProvider;
import com.codenvy.eclipse.ui.widgets.CredentialsDialog;

/**
 * {@link CredentialsProvider} implementation with a prompt UI.
 * 
 * @author Kevin Pollet
 */
public final class CredentialsProviderWithPrompt implements CredentialsProvider {
    private final ExecutorService                            executorService;
    private final ConcurrentMap<String, Future<Credentials>> credentialsMap;

    public CredentialsProviderWithPrompt() {
        this.executorService = Executors.newCachedThreadPool();
        this.credentialsMap = new ConcurrentHashMap<>();
    }

    @Override
    public Credentials getCredentials(final String username) {
        try {

            Future<Credentials> credentials = credentialsMap.get(username);
            if (credentials == null) {
                credentials = executorService.submit(new Callable<Credentials>() {
                    @Override
                    public Credentials call() throws Exception {
                        return showCredentialsDialog(username);
                    }
                });

                Future<Credentials> prevCredentials = credentialsMap.putIfAbsent(username, credentials);
                if (prevCredentials != null) {
                    credentials = prevCredentials;
                }
            }

            final Credentials result = credentials.get();
            if (result == null) {
                credentialsMap.remove(username);
            }

            return result;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shows the {@link CredentialsDialog} used to collect user credentials.
     * 
     * @param username the user name to ask {@link Credentials} for.
     * @return the {@link Credentials} entered by the user or {@code null} if none.
     */
    private Credentials showCredentialsDialog(final String username) {
        final AtomicReference<Credentials> credentials = new AtomicReference<>();

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                final CredentialsDialog credentialsDialog = new CredentialsDialog(username, Display.getDefault().getActiveShell());
                if (credentialsDialog.open() == OK) {
                    credentials.set(CodenvyAPI.getClient().newCredentialsBuilder().withUsername(credentialsDialog.getUsername())
                                                             .withPassword(credentialsDialog.getPassword())
                                                             .storeOnlyToken(!credentialsDialog.isStoreUserCredentials())
                                                             .build());
                }
            }
        });

        return credentials.get();
    }
}
