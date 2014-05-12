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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
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
import com.codenvy.eclipse.core.team.CodenvyProvider;
import com.codenvy.eclipse.core.team.CodenvyProviderMetaData;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

/**
 * Handler pushing resource data to Codenvy.
 * 
 * @author Kevin Pollet
 */
public class PushHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getCurrentSelection(event);
        List<IResource> resources = Collections.emptyList();

        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            final IStructuredSelection structuredSelection = (IStructuredSelection)selection;
            resources = FluentIterable.from(newArrayList(structuredSelection.toArray()))
                                      .transform(new Function<Object, IResource>() {
                                          @Override
                                          public IResource apply(Object adaptable) {
                                              return (IResource)((IAdaptable)adaptable).getAdapter(IResource.class);
                                          }
                                      })
                                      .filter(Predicates.notNull())
                                      .toList();
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
            final CodenvyProviderMetaData codenvyProviderMetaData = codenvyProvider.getProviderMetaData();
            final BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
            final ServiceReference<RestServiceFactory> serviceReference = bundleContext.getServiceReference(RestServiceFactory.class);

            try {

                if (serviceReference != null) {
                    final RestServiceFactory restServiceFactory = bundleContext.getService(serviceReference);
                    final ProjectService projectService = restServiceFactory.newRestServiceWithAuth(ProjectService.class, codenvyProviderMetaData.url, new CodenvyToken(codenvyProviderMetaData.codenvyToken));

                    for (IResource oneResource : resources) {
                        final CodenvyProject codenvyProject = new CodenvyProject(null, null, null, null, null, codenvyProviderMetaData.projectName, null, null, null, null, null);
                        projectService.updateCodenvyResource(codenvyProject, codenvyProviderMetaData.workspaceId, oneResource);
                    }
                }

            } finally {
                bundleContext.ungetService(serviceReference);
            }

        }
        return null;
    }
}
