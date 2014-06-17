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
package com.codenvy.eclipse.core.team;

import static org.eclipse.core.resources.IResource.PROJECT;
import static org.eclipse.core.resources.IResource.ROOT;

import org.eclipse.core.resources.IResource;
import org.eclipse.team.core.RepositoryProvider;

import com.codenvy.eclipse.client.Codenvy;
import com.codenvy.eclipse.client.model.Project;
import com.codenvy.eclipse.core.CodenvyPlugin;

/**
 * The Codenvy resource mapping class.
 * 
 * @author Kevin Pollet
 */
public class CodenvyMetaResource {
    private final IResource resource;
    private boolean         tracked;

    public CodenvyMetaResource(IResource resource) {
        this.resource = resource;
        this.tracked = false;

        if (resource.getType() != ROOT) {
            final CodenvyProvider codenvyProvider =
                                                    (CodenvyProvider)RepositoryProvider.getProvider(resource.getProject(),
                                                                                                    CodenvyProvider.PROVIDER_ID);

            if (codenvyProvider != null) {
                final CodenvyMetaProject metaProject = codenvyProvider.getMetaProject();

                if (metaProject != null) {
                    final Project codenvyProject = new Project.Builder().withName(metaProject.projectName)
                                                                        .withWorkspaceId(metaProject.workspaceId)
                                                                        .build();

                    final Codenvy codenvy = CodenvyPlugin.getDefault()
                                                         .getCodenvyBuilder(metaProject.url, metaProject.username)
                                                         .build();

                    this.tracked =
                                   resource.getType() == PROJECT ? true : codenvy.project()
                                                                                 .isResource(codenvyProject,
                                                                                             resource.getProjectRelativePath().toString())
                                                                                 .execute();
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
