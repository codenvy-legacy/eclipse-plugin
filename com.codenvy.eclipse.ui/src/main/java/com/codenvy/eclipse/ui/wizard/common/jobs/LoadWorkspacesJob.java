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
package com.codenvy.eclipse.ui.wizard.common.jobs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.codenvy.eclipse.client.Codenvy;
import com.codenvy.eclipse.client.auth.Credentials;
import com.codenvy.eclipse.client.model.Workspace;
import com.codenvy.eclipse.client.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.ui.wizard.common.CredentialsProvider;

/**
 * @author stephane
 */
public abstract class LoadWorkspacesJob implements IRunnableWithProgress {
    private final CredentialsProvider credentialsProvider;

    public LoadWorkspacesJob(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    @Override
    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    final String platformURL = credentialsProvider.getUrl();
                    final String username = credentialsProvider.getUsername();
                    final String password = credentialsProvider.getPassword();

                    final Credentials credentials = new Credentials.Builder().withUsername(username)
                                                                             .withPassword(password)
                                                                             .storeOnlyToken(!credentialsProvider.isStoreUserCredentials())
                                                                             .build();

                    final Codenvy codenvy = CodenvyPlugin.getDefault()
                                                         .getCodenvyBuilder(platformURL, username)
                                                         .withCredentials(credentials)
                                                         .build();

                    final List<Workspace> workspaces = codenvy.workspace()
                                                              .all()
                                                              .execute();

                    monitor.beginTask("Fetch workspaces from Codenvy", workspaces.size());

                    final List<WorkspaceRef> workspaceRefs = new ArrayList<>();
                    for (Workspace workspace : workspaces) {
                        workspaceRefs.add(codenvy.workspace().withName(workspace.workspaceRef.name).execute());
                        monitor.worked(1);
                    }


                    postLoadCallback(workspaceRefs);
                }
            });

        } finally {
            monitor.done();
        }
    }

    public abstract void postLoadCallback(List<WorkspaceRef> workspaceRefs);
}