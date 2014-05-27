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
package com.codenvy.eclipse.core;

import static com.codenvy.eclipse.core.utils.StringHelper.isNullOrEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.services.SecureStorageService;
import com.codenvy.eclipse.core.services.TokenProvider;

/**
 * Automatically reauthenticate user for the stored URL using the password retrieved through
 * {@link SecureStorageService#getPassword(String, String)}.
 * 
 * @author Stéphane Daviet
 */
@Provider
@Priority(Priorities.USER)
// TODO Stéphane Daviet - 2014/05/27: Renewal on need with ClientResponseFilter or something like that
public class TokenRenewalFilter implements ClientRequestFilter {
    private final String url;
    private final String username;

    /**
     * Default constructor.
     * 
     * @param url the Codenvy repository URL.
     * @param username the username.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@linkplain String}.
     */
    public TokenRenewalFilter(String url, String username) {
        checkNotNull(url);
        checkArgument(!isNullOrEmpty(url));
        checkNotNull(username);
        checkArgument(!isNullOrEmpty(username));

        this.url = url;
        this.username = username;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

        final ServiceReference<TokenProvider> tokenProviderRef = context.getServiceReference(TokenProvider.class);
        if (tokenProviderRef != null) {
            TokenProvider tokenProvider = context.getService(tokenProviderRef);
            if (tokenProvider != null) {
                tokenProvider.renewToken(url, username);
            }
        }
    }
}
