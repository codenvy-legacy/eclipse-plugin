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

import static com.codenvy.eclipse.core.team.CodenvyProvider.PROVIDER_ID;
import static com.codenvy.eclipse.core.utils.EclipseProjectHelper.updateProjectOnCodenvy;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.PlatformUI;

import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.team.CodenvyProvider;

/**
 * Handler pushing resources to Codenvy.
 * 
 * @author Kevin Pollet
 */
public class PushProjectHandler extends AbstractProjectHandler {
    @Override
    public Object execute(final Set<IProject> projects, ExecutionEvent event) throws ExecutionException {
        if (!projects.isEmpty()) {
            try {

                PlatformUI.getWorkbench()
                          .getProgressService()
                          .run(true, false, new IRunnableWithProgress() {
                              @Override
                              public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                                  monitor.beginTask("Push projects", projects.size());

                                  try {

                                      for (IProject oneProject : projects) {
                                          final CodenvyProvider codenvyProvider = (CodenvyProvider)RepositoryProvider.getProvider(oneProject, PROVIDER_ID);
                                          final CodenvyMetaProject codenvyMetaProject = codenvyProvider.getMetaProject();

                                          updateProjectOnCodenvy(oneProject, codenvyMetaProject, monitor);
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
        }

        return null;
    }
}
