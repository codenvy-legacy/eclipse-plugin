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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.ResourceUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.ProjectService;
import com.codenvy.eclipse.core.RestServiceFactory;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.team.CodenvyProvider;

/**
 * Handler pushing resource data to Codenvy.
 * 
 * @author Kevin Pollet
 */
public class PushHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getCurrentSelection(event);
        List<IResource> resources = new ArrayList<>();

        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            final IStructuredSelection structuredSelection = (IStructuredSelection)selection;
            
            for (Object oneObject : structuredSelection.toArray()) {
                final IResource oneResource = ResourceUtil.getResource(oneObject);
                if (oneResource != null) {
                    resources.add(oneResource);
                }
            }
        }
        else {
            final IEditorInput editorInput = HandlerUtil.getActiveEditorInput(event);
            final IResource resource = ResourceUtil.getResource(editorInput);
            if (resource != null) {
                resources = Collections.singletonList(resource);
            }
        }

        if (!resources.isEmpty()) {
            final IProject project = resources.get(0).getProject();
            final CodenvyProvider codenvyProvider = (CodenvyProvider)RepositoryProvider.getProvider(project);
            final CodenvyMetaProject metaProject = codenvyProvider.getMetaProject();
            final BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
            final ServiceReference<RestServiceFactory> serviceReference = bundleContext.getServiceReference(RestServiceFactory.class);

            try {

                if (serviceReference != null) {
                    final RestServiceFactory restServiceFactory = bundleContext.getService(serviceReference);
                    final ProjectService projectService = restServiceFactory.newRestServiceWithAuth(ProjectService.class, metaProject.url, new CodenvyToken(metaProject.codenvyToken));

                    for (IResource oneResource : resources) {
                        final CodenvyProject codenvyProject = new CodenvyProject(null, null, null, null, null, metaProject.projectName, null, null, null, null, null);
                        projectService.updateProjectResource(codenvyProject, metaProject.workspaceId, oneResource);
                    }
                }

            } finally {
                bundleContext.ungetService(serviceReference);
            }

        }
        return null;
    }
}
