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
package com.codenvy.eclipse.ui.team;

import static com.codenvy.eclipse.core.utils.EclipseProjectHelper.updateCodenvyProjectResource;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.PlatformUI;

import com.codenvy.eclipse.core.exceptions.ServiceUnavailableException;
import com.codenvy.eclipse.core.model.Project;
import com.codenvy.eclipse.core.services.ProjectService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.team.CodenvyProvider;
import com.codenvy.eclipse.core.utils.ServiceHelper;
import com.codenvy.eclipse.core.utils.ServiceHelper.ServiceInvoker;

/**
 * Handler pushing resources to Codenvy.
 * 
 * @author Kevin Pollet
 */
public class PushHandler extends AbstractResourceHandler {
    @Override
    public Object execute(final List<IResource> resources, ExecutionEvent event) throws ExecutionException {
        if (!resources.isEmpty()) {
            final IProject project = resources.get(0).getProject();
            final CodenvyProvider codenvyProvider = (CodenvyProvider)RepositoryProvider.getProvider(project);
            final CodenvyMetaProject metaProject = codenvyProvider.getMetaProject();

            try {

                ServiceHelper.forService(RestServiceFactory.class)
                             .invoke(new ServiceInvoker<RestServiceFactory, Void>() {
                                 @Override
                                 public Void run(RestServiceFactory factory) {
                                     final ProjectService projectService =
                                                                           factory.newRestServiceWithAuth(ProjectService.class,
                                                                                                          metaProject.url,
                                                                                                          metaProject.username);

                                     try {

                                         PlatformUI.getWorkbench()
                                                   .getProgressService()
                                                   .run(true, false, new IRunnableWithProgress() {
                                                       @Override
                                                       public void run(IProgressMonitor monitor) throws InvocationTargetException,
                                                                                                InterruptedException {

                                                           monitor.beginTask("Update resources", resources.size());

                                                           try {

                                                               for (IResource oneResource : resources) {
                                                                   final Project codenvyProject =
                                                                                                  new Project.Builder().withName(metaProject.projectName)
                                                                                                                       .withWorkspaceId(metaProject.workspaceId)
                                                                                                                       .build();

                                                                   updateCodenvyProjectResource(codenvyProject, oneResource,
                                                                                                projectService, monitor);

                                                                   monitor.worked(1);
                                                               }

                                                           } finally {
                                                               monitor.done();
                                                           }
                                                       }
                                                   });


                                     } catch (InvocationTargetException | InterruptedException e) {
                                         throw new RuntimeException(e);
                                     }

                                     return null;
                                 }
                             });

            } catch (ServiceUnavailableException e) {
                // TODO do something if service is unavailable
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
