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
package com.codenvy.eclipse.ui.team;

import static com.codenvy.eclipse.core.utils.EclipseProjectHelper.updateIResource;

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

import com.codenvy.client.Codenvy;
import com.codenvy.client.model.Project;
import com.codenvy.eclipse.core.CodenvyPlugin;
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

            try {

                PlatformUI.getWorkbench()
                          .getProgressService().run(true, false, new IRunnableWithProgress() {
                              @Override
                              public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                                  monitor.beginTask("Update resources", resources.size());

                                  try {

                                      for (IResource oneResource : resources) {
                                          final Project codenvyProject = new Project.Builder().withName(metaProject.projectName)
                                                                                              .withWorkspaceId(metaProject.workspaceId)
                                                                                              .build();

                                          final Codenvy codenvy = CodenvyPlugin.getDefault()
                                                                               .getCodenvyBuilder(metaProject.url, metaProject.username)
                                                                               .build();

                                          updateIResource(codenvyProject, oneResource, codenvy, monitor);
                                          monitor.worked(1);
                                      }

                                  } finally {
                                      monitor.done();
                                  }
                              }
                          });

            } catch (InterruptedException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
