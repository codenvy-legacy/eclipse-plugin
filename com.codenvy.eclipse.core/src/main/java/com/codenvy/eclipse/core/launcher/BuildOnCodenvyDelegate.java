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

import static com.codenvy.eclipse.core.launcher.LaunchConstants.CODENVY_PROJECT_NAME_ATTRIBUTE_NAME;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import com.codenvy.eclipse.client.Codenvy;
import com.codenvy.eclipse.client.model.Project;
import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;

/**
 * The build on codenvy delegate.
 * 
 * @author Kevin Pollet
 */
// TODO push project modifications before run
public final class BuildOnCodenvyDelegate implements ILaunchConfigurationDelegate {
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, final ILaunch launch, IProgressMonitor monitor) throws CoreException {
        // this delegate handle only the run mode
        if (RUN_MODE.equalsIgnoreCase(mode)) {
            final String projectName = configuration.getAttribute(CODENVY_PROJECT_NAME_ATTRIBUTE_NAME, (String)null);
            if (projectName == null) {
                throw new CoreException(new Status(IStatus.ERROR, CodenvyPlugin.PLUGIN_ID, "No project to run"));
            }

            final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
            if (project == null) {
                throw new CoreException(new Status(IStatus.ERROR, CodenvyPlugin.PLUGIN_ID, "No project named " + projectName
                                                                                           + " in the workspace"));
            }


            final CodenvyMetaProject metaProject = CodenvyMetaProject.get(project);
            final Project codenvyProject = new Project.Builder().withName(metaProject.projectName)
                                                                .withWorkspaceId(metaProject.workspaceId)
                                                                .build();

            final Codenvy codenvy = CodenvyPlugin.getDefault()
                                                 .getCodenvyBuilder(metaProject.url, metaProject.username)
                                                 .build();

            new CodenvyBuilderProcess(launch, codenvy, codenvyProject);
        }
    }
}
