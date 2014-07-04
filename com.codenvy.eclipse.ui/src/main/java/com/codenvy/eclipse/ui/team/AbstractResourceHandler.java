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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.ResourceUtil;

/**
 * Handler working with {@link IResource}.
 * 
 * @author Kevin Pollet
 */
public abstract class AbstractResourceHandler extends AbstractHandler {
    @Override
    public final Object execute(ExecutionEvent event) throws ExecutionException {
        final List<IResource> resources = new ArrayList<>();

        final ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            final IStructuredSelection structuredSelection = (IStructuredSelection)selection;

            for (Object oneObject : structuredSelection.toArray()) {
                final IResource oneResource = ResourceUtil.getResource(oneObject);
                if (oneResource != null) {
                    resources.add(oneResource);
                }
            }
        }

        // if resource are selected the active editor input is null
        final IEditorInput activeEditorInput = HandlerUtil.getActiveEditorInput(event);
        if (activeEditorInput != null) {
            final IResource editorResource = ResourceUtil.getResource(activeEditorInput);

            if (editorResource != null) {
                resources.add(editorResource);
            }
        }

        return execute(resources, event);
    }

    /**
     * Executes this handler on the selected resources.
     * 
     * @param resources the selected resources.
     * @param event the event containing all the information about the current state of the application; must not be null
     * @return the result of the execution. Reserved for future use, must be {@code null}.
     * @throws ExecutionException if an exception occurred during execution.
     */
    public abstract Object execute(List<IResource> resources, ExecutionEvent event) throws ExecutionException;
}
