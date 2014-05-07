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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.team.core.RepositoryProvider;

/**
 * The Codenvy team provider.
 * 
 * @author Kevin Pollet
 */
public class CodenvyProvider extends RepositoryProvider {
    public static final String PROVIDER_ID = "com.codenvy.eclipse.core.team.codenvyprovider";

    private CodenvyProviderMetaData providerMetaData;
    
    @Override
    public void configureProject() throws CoreException {
    }

    @Override
    public void deconfigure() throws CoreException {
        CodenvyProviderMetaData.delete(getProject());
    }

    @Override
    public String getID() {
        return PROVIDER_ID;
    }
    
    public CodenvyProviderMetaData getProviderMetaData() {
        if (providerMetaData == null) {
            providerMetaData = CodenvyProviderMetaData.get(getProject());
        }
        return providerMetaData;
    }
}
