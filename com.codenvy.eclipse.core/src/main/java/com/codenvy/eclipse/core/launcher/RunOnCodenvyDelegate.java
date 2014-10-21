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

import static com.codenvy.eclipse.core.CodenvyPlugin.PLUGIN_ID;
import static com.codenvy.eclipse.core.launcher.LaunchConstants.CODENVY_PROJECT_NAME_ATTRIBUTE_NAME;
import static com.codenvy.eclipse.core.team.CodenvyProvider.PROVIDER_ID;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.team.core.RepositoryProvider;

import com.codenvy.eclipse.core.team.CodenvyProvider;

/**
 * The run on Codenvy delegate.
 * 
 * @author Kevin Pollet
 */
public final class RunOnCodenvyDelegate implements ILaunchConfigurationDelegate {
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, final ILaunch launch, IProgressMonitor monitor) throws CoreException {
        if (RUN_MODE.equalsIgnoreCase(mode)) {
            final String projectName = configuration.getAttribute(CODENVY_PROJECT_NAME_ATTRIBUTE_NAME, (String)null);
            if (projectName == null) {
                throw new CoreException(new Status(ERROR, PLUGIN_ID, "No project to run"));
            }

            final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
            if (project == null) {
                throw new CoreException(new Status(ERROR, PLUGIN_ID, "No project named " + projectName + " in the workspace"));
            }

            final CodenvyProvider codenvyProvider = (CodenvyProvider)RepositoryProvider.getProvider(project, PROVIDER_ID);
            if (codenvyProvider == null) {
                throw new CoreException(new Status(ERROR, PLUGIN_ID, "Project named " + projectName + " isn't a Codenvy project"));
            }

            new CodenvyRunnerProcess(launch, codenvyProvider.getProjectMetadata());
        }
    }
}
