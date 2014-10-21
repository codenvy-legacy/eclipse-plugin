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
package com.codenvy.eclipse.core.team;

import static com.codenvy.eclipse.core.team.CodenvyProvider.PROVIDER_ID;
import static org.eclipse.core.resources.IResource.ROOT;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.team.core.RepositoryProvider;

import com.codenvy.client.CodenvyAPI;
import com.codenvy.client.auth.CodenvyAuthenticationException;
import com.codenvy.client.model.ProjectReference;
import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.core.CodenvyProjectMetadata;

/**
 * The Codenvy resource mapping class.
 * 
 * @author Kevin Pollet
 */
public final class CodenvyMetaResource {
    private final IResource resource;
    private boolean         tracked;

    public CodenvyMetaResource(IResource resource) {
        this.resource = resource;
        this.tracked = false;

        if (resource.getType() != ROOT) {
            final CodenvyProvider codenvyProvider = (CodenvyProvider)RepositoryProvider.getProvider(resource.getProject(), PROVIDER_ID);

            if (codenvyProvider != null) {
                final CodenvyProjectMetadata projectMetadata = codenvyProvider.getProjectMetadata();

                if (projectMetadata != null) {
                    final ProjectReference codenvyProject = CodenvyAPI.getClient().newProjectBuilder().withName(projectMetadata.projectName)
                                                                      .withWorkspaceId(projectMetadata.workspaceId)
                                                                      .build();

                    try {

                        if (resource instanceof IFile) {
                            this.tracked = CodenvyPlugin.getDefault()
                                                        .getCodenvyBuilder(projectMetadata.url, projectMetadata.username)
                                                        .build()
                                                        .project()
                                                        .hasFile(codenvyProject, resource.getProjectRelativePath().toString())
                                                        .execute();
                        } else {
                            this.tracked = CodenvyPlugin.getDefault()
                                                        .getCodenvyBuilder(projectMetadata.url, projectMetadata.username)
                                                        .build()
                                                        .project()
                                                        .hasFolder(codenvyProject, resource.getProjectRelativePath().toString())
                                                        .execute();
                        }

                    } catch (CodenvyAuthenticationException e) {
                        this.tracked = false;
                    }
                }
            }
        }
    }

    /**
     * Returns the underlying {@link IResource}.
     * 
     * @return the underlying {@link IResource}.
     */
    public IResource getResource() {
        return resource;
    }

    /**
     * Returns if this resource is tracked.
     * 
     * @return {@code true} if the resource is tracked, {@code false} otherwise.
     */
    public boolean isTracked() {
        return tracked;
    }
}
