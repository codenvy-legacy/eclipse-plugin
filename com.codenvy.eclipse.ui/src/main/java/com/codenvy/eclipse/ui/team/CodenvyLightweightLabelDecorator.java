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
public final class CodenvyLightweightLabelDecorator extends LabelProvider implements ILightweightLabelDecorator {
    public static final String    DECORATOR_ID = "com.codenvy.eclipse.ui.team.codenvyLightweightLabelDecorator";

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
