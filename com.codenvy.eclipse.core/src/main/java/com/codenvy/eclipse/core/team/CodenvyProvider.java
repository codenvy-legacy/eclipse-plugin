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
package com.codenvy.eclipse.core.team;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.team.core.RepositoryProvider;

/**
 * The Codenvy team provider.
 * 
 * @author Kevin Pollet
 */
public class CodenvyProvider extends RepositoryProvider {
    public static final String      PROVIDER_ID = "com.codenvy.eclipse.core.team.codenvyprovider";

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
     * @param isTeamPrivate {@code true} if the '.codenvy' folder is team private, {@code false} otherwise.
     * @throws CoreException if this method fails.
     */
    private void setCodenvyFolderAsTeamPrivate(boolean isTeamPrivate) throws CoreException {
        final IFolder codenvyFolder = getProject().getFolder(".codenvy");
        codenvyFolder.setTeamPrivateMember(isTeamPrivate);
    }
}
