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

import static org.eclipse.core.resources.IResource.PROJECT;
import static org.eclipse.core.resources.IResource.ROOT;
import static org.eclipse.team.core.RepositoryProvider.getProvider;
import static org.eclipse.team.ui.ISharedImages.IMG_CHECKEDIN_OVR;
import static org.eclipse.ui.ide.ResourceUtil.getAdapter;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.team.ui.TeamImages;
import org.eclipse.ui.ide.ResourceUtil;

import com.codenvy.eclipse.core.team.CodenvyMetaResource;
import com.codenvy.eclipse.core.team.CodenvyProvider;

/**
 * {@link ILightweightLabelDecorator} implementation used to decorate {@link IResource} of a project linked to Codenvy.
 * 
 * @author Kevin Pollet
 * @see CodenvyProvider
 */
public class CodenvyLightweightLabelDecorator extends LabelProvider implements ILightweightLabelDecorator {
    private final ImageDescriptor trackedImageDescriptor;

    public CodenvyLightweightLabelDecorator() {
        trackedImageDescriptor = TeamImages.getImageDescriptor(IMG_CHECKEDIN_OVR);
    }

    @Override
    public void decorate(Object element, IDecoration decoration) {
        final IResource resource = ResourceUtil.getResource(element);

        if (resource != null && resource.getType() != ROOT) {
            final CodenvyProvider provider = (CodenvyProvider)getProvider(resource.getProject(), CodenvyProvider.PROVIDER_ID);

            if (provider != null) {
                final CodenvyMetaResource metaResource = (CodenvyMetaResource)getAdapter(resource, CodenvyMetaResource.class, true);

                if (metaResource != null && metaResource.isTracked()) {
                    decoration.addOverlay(trackedImageDescriptor);

                    if (resource.getType() == PROJECT) {
                        decoration.addSuffix(" [codenvy: " + provider.getMetaProject().url + "]");
                    }
                }
            }
        }
    }
}
