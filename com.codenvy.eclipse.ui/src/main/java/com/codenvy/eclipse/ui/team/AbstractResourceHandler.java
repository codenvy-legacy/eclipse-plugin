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
