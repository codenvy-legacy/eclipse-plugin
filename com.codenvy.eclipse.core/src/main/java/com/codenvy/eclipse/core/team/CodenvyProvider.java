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
package com.codenvy.eclipse.core.team;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.team.core.RepositoryProvider;

/**
 * The Codenvy team provider.
 * 
 * @author Kevin Pollet
 */
public final class CodenvyProvider extends RepositoryProvider {
    public static final String PROVIDER_ID = "com.codenvy.eclipse.core.team.codenvyProvider";

    private CodenvyMetaProject metaProject;

    @Override
    public void configureProject() throws CoreException {
        setCodenvyFolderAsTeamPrivate(true);
    }

    @Override
    public void deconfigure() throws CoreException {
        CodenvyMetaProject.delete(getProject());
        setCodenvyFolderAsTeamPrivate(false);
    }

    @Override
    public String getID() {
        return PROVIDER_ID;
    }

    public CodenvyMetaProject getMetaProject() {
        if (metaProject == null) {
            metaProject = CodenvyMetaProject.get(getProject());
        }
        return metaProject;
    }

    /**
     * Defines the '.codenvy' folder as team private.
     * 
     * @param isTeamPrivate {@code true} if '.codenvy' folder is team private, {@code false} otherwise.
     * @throws CoreException if this method fails.
     */
    private void setCodenvyFolderAsTeamPrivate(boolean isTeamPrivate) throws CoreException {
        final IFolder codenvyFolder = getProject().getFolder(".codenvy");
        codenvyFolder.setTeamPrivateMember(isTeamPrivate);
    }
}
