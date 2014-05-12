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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.ui.ISharedImages;
import org.eclipse.team.ui.TeamImages;

import com.codenvy.eclipse.core.team.CodenvyProvider;

/**
 * The Codenvy provider label decorator.
 * 
 * @author Kevin Pollet
 */
public class CodenvyProviderLabelDecorator implements ILightweightLabelDecorator {
    private final ImageDescriptor trackedImageDescriptor;

    public CodenvyProviderLabelDecorator() {
        trackedImageDescriptor = TeamImages.getImageDescriptor(ISharedImages.IMG_CHECKEDIN_OVR);
    }

    @Override
    public void addListener(ILabelProviderListener listener) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {

    }

    @Override
    public void decorate(Object element, IDecoration decoration) {
        final IResource resource = getResource(element);
        if (resource != null) {
            if (isTracked(resource)) {
                decoration.addOverlay(trackedImageDescriptor);
            }
        }
    }

    /**
     * Converts the given element to an {@link IResource}.
     * 
     * @param element the element to convert.
     * @return the {@link IResource} corresponding to the given element or {@code null}.
     */
    private IResource getResource(Object element) {
        IResource resource = null;
        if (element instanceof IResource) {
            resource = (IResource)element;
        }
        else if (element instanceof IAdaptable) {
            final IAdaptable adaptable = (IAdaptable)element;
            resource = (IResource)adaptable.getAdapter(IResource.class);
        }
        return resource;
    }

    /**
     * Returns the tacked state of the given resource.
     * 
     * @param resource the resource.
     * @return {@code true} if the given resource is tracked, {@code false} otherwise.
     * @throws NullPointerException if resource parameter is {@code null}.
     */
    private boolean isTracked(IResource resource) {
        checkNotNull(resource);

        if (resource.getType() != IResource.ROOT) {
            final IProject project = resource.getProject();
            final RepositoryProvider repositoryProvider = RepositoryProvider.getProvider(project, CodenvyProvider.PROVIDER_ID);
            return repositoryProvider != null;
        }

        return false;
    }
}
