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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.team.ui.ISharedImages;
import org.eclipse.team.ui.TeamImages;
import org.eclipse.ui.ide.ResourceUtil;

import com.codenvy.eclipse.core.team.CodenvyMetaResource;

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
        final IResource resource = ResourceUtil.getResource(element);
        if (resource.getType() != IResource.ROOT) {
            final CodenvyMetaResource codenvyResource = (CodenvyMetaResource)ResourceUtil.getAdapter(resource, CodenvyMetaResource.class, true);

            if (codenvyResource != null && codenvyResource.isTracked()) {
                decoration.addOverlay(trackedImageDescriptor);
            }
        }
    }
}
