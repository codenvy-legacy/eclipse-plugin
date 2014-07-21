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

import static com.google.common.base.Predicates.notNull;
import static org.eclipse.core.runtime.IProgressMonitor.UNKNOWN;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.codenvy.client.Codenvy;
import com.codenvy.client.CodenvyAPI;
import com.codenvy.client.auth.Credentials;
import com.codenvy.client.model.Workspace;
import com.codenvy.client.model.WorkspaceReference;
import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.ui.wizard.common.CredentialsProviderWizard;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * Job to load workspaces list from a remote Codenvy repository.
 * 
 * @author Stéphane Daviet
 * @author Kevin Pollet
 */
public abstract class LoadWorkspacesJob implements IRunnableWithProgress {
    private final CredentialsProviderWizard credentialsProvider;

    public LoadWorkspacesJob(CredentialsProviderWizard credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    @Override
    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            monitor.beginTask("Fetch workspaces from Codenvy", UNKNOWN);

            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    final String platformURL = credentialsProvider.getUrl();
                    final String username = credentialsProvider.getUsername();
                    final String password = credentialsProvider.getPassword();

                    final Credentials credentials = CodenvyAPI.getClient().newCredentialsBuilder().withUsername(username)
                                                              .withPassword(password)
                                                              .storeOnlyToken(!credentialsProvider.isStoreUserCredentials())
                                                              .build();

                    final Codenvy codenvy = CodenvyPlugin.getDefault()
                                                         .getCodenvyBuilder(platformURL, username)
                                                         .withCredentials(credentials)
                                                         .build();

                    final List< ? extends Workspace> workspaces = codenvy.workspace()
                                                                         .all()
                                                                         .execute();

                    final List< ? extends WorkspaceReference> workspaceReferences =
                                                                                    FluentIterable.from(workspaces)
                                                                                                  .transform(new Function<Workspace, WorkspaceReference>() {
                                                                                                                 @Override
                                                                                                                 public WorkspaceReference apply(Workspace workspace) {
                                                                                                                     return workspace != null
                                                                                                                         ? workspace.workspaceReference()
                                                                                                                         : null;
                                                                                                                 }
                                                                                                             })
                                                                                                  .filter(notNull())
                                                                                                  .toList();


                    postLoadCallback(workspaceReferences);
                }
            });

        } finally {
            monitor.done();
        }
    }

    /**
     * Callback called when the {@link WorkspaceReference} list has been fetched from Codenvy.
     *
     * @param workspaceReferences the {@link WorkspaceReference} list, never {@code null}.
     */
    public abstract void postLoadCallback(List< ? extends WorkspaceReference> workspaceReferences);
}
