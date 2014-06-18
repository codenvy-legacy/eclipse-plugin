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
package com.codenvy.eclipse.ui.test.mocks;

import static org.eclipse.core.resources.IResource.PROJECT;
import static org.eclipse.core.resources.IResource.ROOT;

import org.eclipse.core.resources.IResource;
import org.eclipse.team.core.RepositoryProvider;

import com.codenvy.eclipse.core.client.ProjectClient;
import com.codenvy.eclipse.core.client.model.Project;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.team.CodenvyMetaResource;
import com.codenvy.eclipse.core.team.CodenvyProvider;

/**
 * The default Codenvy resource mapping class implementation.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class CodenvyMetaResourceMock implements CodenvyMetaResource {
    private final IResource resource;
    private boolean         tracked;

    public CodenvyMetaResourceMock(IResource resource) {
        this.resource = resource;
        this.tracked = false;

        if (resource.getType() != ROOT) {
            final CodenvyProvider codenvyProvider =
                                                    (CodenvyProvider)RepositoryProvider.getProvider(resource.getProject(),
                                                                                                    CodenvyProvider.PROVIDER_ID);

            if (codenvyProvider != null) {
                final CodenvyMetaProject metaProject = codenvyProvider.getMetaProject();

                if (metaProject != null) {
                    final ProjectClient projectService =
                                                          new ProjectServiceMock(metaProject.url, metaProject.username);
                    final Project codenvyProject = new Project.Builder().withName(metaProject.projectName)
                                                                        .withWorkspaceId(metaProject.workspaceId)
                                                                        .build();

                    this.tracked =
                                   resource.getType() == PROJECT ? true
                                       : projectService.isResource(codenvyProject, resource.getProjectRelativePath().toString());
                }
            }
        }
    }

    @Override
    public IResource getResource() {
        return resource;
    }

    @Override
    public boolean isTracked() {
        return tracked;
    }
}
