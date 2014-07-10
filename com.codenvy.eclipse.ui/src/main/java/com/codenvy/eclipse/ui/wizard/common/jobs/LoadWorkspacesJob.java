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
package com.codenvy.eclipse.ui.wizard.common.jobs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.codenvy.client.Codenvy;
import com.codenvy.client.auth.Credentials;
import com.codenvy.client.model.Workspace;
import com.codenvy.client.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.ui.wizard.common.CredentialsProviderWizard;

/**
 * Job to load workspaces list from a remote Codenvy repository.
 * 
 * @author Stéphane Daviet
 */
public abstract class LoadWorkspacesJob implements IRunnableWithProgress {
    private final CredentialsProviderWizard credentialsProvider;

    public LoadWorkspacesJob(CredentialsProviderWizard credentialsProvider) {
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
