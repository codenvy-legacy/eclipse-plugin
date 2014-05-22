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

import static org.eclipse.core.resources.IResource.ROOT;
import static org.eclipse.team.core.RepositoryProvider.getProvider;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.team.ui.ISharedImages;
import org.eclipse.team.ui.TeamImages;
import org.eclipse.ui.ide.ResourceUtil;

import com.codenvy.eclipse.core.team.CodenvyMetaResource;
import com.codenvy.eclipse.core.team.CodenvyProvider;

/**
 * The Codenvy label decorator.
 * 
 * @author Kevin Pollet
 */
public class CodenvyLightweightLabelDecorator implements ILightweightLabelDecorator {
    private final ImageDescriptor trackedImageDescriptor;

    public CodenvyLightweightLabelDecorator() {
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
        final IResource resource = ResourceUtil.getResource(element);
        if (resource != null && resource.getType() != ROOT) {
            final CodenvyProvider provider = (CodenvyProvider)getProvider(resource.getProject(), CodenvyProvider.PROVIDER_ID);
            if (provider != null) {
                final CodenvyMetaResource metaResource = (CodenvyMetaResource)ResourceUtil.getAdapter(resource, CodenvyMetaResource.class, true);
                if (metaResource != null && metaResource.isTracked()) {
                    decoration.addOverlay(trackedImageDescriptor);
                }
            }
        }
    }
}
