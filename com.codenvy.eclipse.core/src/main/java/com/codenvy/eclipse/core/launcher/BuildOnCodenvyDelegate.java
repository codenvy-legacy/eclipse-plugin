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

import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.core.exceptions.ServiceUnavailableException;
import com.codenvy.eclipse.core.model.Project;
import com.codenvy.eclipse.core.services.BuilderService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.utils.ServiceHelper;
import com.codenvy.eclipse.core.utils.ServiceHelper.ServiceInvoker;

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

            try {

                ServiceHelper.forService(RestServiceFactory.class)
                             .invoke(new ServiceInvoker<RestServiceFactory, Void>() {
                                 @Override
                                 public Void run(RestServiceFactory service) {
                                     final BuilderService builderService =
                                                                           service.newRestServiceWithAuth(BuilderService.class,
                                                                                                          metaProject.url,
                                                                                                          metaProject.username);
                                     new CodenvyBuilderProcess(launch, builderService, codenvyProject);
                                     return null;
                                 }
                             });

            } catch (ServiceUnavailableException e) {
                // TODO do something if service is unavailable
                throw new RuntimeException(e);
            }
        }
    }
}
