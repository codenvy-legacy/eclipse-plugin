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

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.EclipseProjectHelper;
import com.codenvy.eclipse.core.ProjectService;
import com.codenvy.eclipse.core.RestServiceFactory;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.team.CodenvyProvider;

/**
 * Handler updating resources from Codenvy.
 * 
 * @author Kevin Pollet
 */
public class UpdateHandler extends AbstractResourceHandler {
    @Override
    public Object execute(final List<IResource> resources, ExecutionEvent event) throws ExecutionException {
        if (!resources.isEmpty()) {
            final IProject project = resources.get(0).getProject();
            final CodenvyProvider codenvyProvider = (CodenvyProvider)RepositoryProvider.getProvider(project);
            final CodenvyMetaProject metaProject = codenvyProvider.getMetaProject();
            final BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
            final ServiceReference<RestServiceFactory> serviceReference = bundleContext.getServiceReference(RestServiceFactory.class);

            try {

                if (serviceReference != null) {
                    final RestServiceFactory restServiceFactory = bundleContext.getService(serviceReference);
                    
                    if (restServiceFactory != null) {
                        final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
                        final ProjectService projectService = restServiceFactory.newRestServiceWithAuth(ProjectService.class, metaProject.url, new CodenvyToken(metaProject.codenvyToken));                        
                        
                        progressService.run(true, false, new IRunnableWithProgress() {
                            @Override
                            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                                monitor.beginTask("Update resources", resources.size());
                                
                                try {
                                
                                    for (IResource oneResource : resources) {
                                        if (oneResource.getType() == IResource.FILE) {
                                            final CodenvyProject codenvyProject = new CodenvyProject(null, null, null, null, null, metaProject.projectName, null, null, null, null, null);
                                            final InputStream fileInputStream = projectService.getFile(codenvyProject, metaProject.workspaceId, oneResource.getProjectRelativePath().toString());
                                            
                                            EclipseProjectHelper.updateIFile(fileInputStream, (IFile) oneResource, monitor);
                                        }
                                        
                                        monitor.worked(1);
                                    }
                                    
                                } finally {
                                    monitor.done();
                                }
                            }
                        });
                    }
                }

            } catch (InvocationTargetException | InterruptedException e) {
                throw new RuntimeException(e);
                
            } finally {
                bundleContext.ungetService(serviceReference);
            }
        }

        return null;
    }
}
