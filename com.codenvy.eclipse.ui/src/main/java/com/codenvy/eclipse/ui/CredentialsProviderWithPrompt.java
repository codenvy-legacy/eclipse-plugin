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

import com.codenvy.eclipse.client.auth.Credentials;
import com.codenvy.eclipse.client.auth.CredentialsProvider;
import com.codenvy.eclipse.ui.widgets.CredentialsDialog;

/**
 * {@link CredentialsProvider} implementation with a prompt UI.
 * 
 * @author Kevin Pollet
 */
public class CredentialsProviderWithPrompt implements CredentialsProvider {
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

    private Credentials showCredentialsDialog(final String username) {
        final AtomicReference<Credentials> credentials = new AtomicReference<>();

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                final CredentialsDialog credentialsDialog = new CredentialsDialog(username, Display.getDefault().getActiveShell());
                if (credentialsDialog.open() == OK) {
                    credentials.set(new Credentials.Builder().withUsername(credentialsDialog.getUsername())
                                                             .withPassword(credentialsDialog.getPassword())
                                                             .storeOnlyToken(!credentialsDialog.isStoreUserCredentials())
                                                             .build());
                }
            }
        });

        return credentials.get();
    }
}
