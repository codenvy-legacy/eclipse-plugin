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
package com.codenvy.eclipse.core.impl.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.Response.Status.OK;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.exceptions.AuthenticationException;
import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.services.AbstractRestService;
import com.codenvy.eclipse.core.services.AuthenticationService;
import com.codenvy.eclipse.core.services.SecureStorageService;

/**
 * The Codenvy authentication client service.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class DefaultAuthenticationService extends AbstractRestService implements AuthenticationService {
    /**
     * Constructs an instance of {@linkplain DefaultAuthenticationService}.
     * 
     * @param url the Codenvy platform url.
     * @throws NullPointerException if url parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public DefaultAuthenticationService(String url) {
        super(url, "api/auth");
    }

    @Override
    public CodenvyToken login(CodenvyCredentials credentials) {
        return login(credentials, true);
    }

    @Override
    public CodenvyToken login(CodenvyCredentials credentials, boolean storeCredentials) {
        checkNotNull(credentials);

        final Response response = getWebTarget().path("login")
                                                .request(MediaType.APPLICATION_JSON)
                                                .post(json(credentials));

        if (OK.getStatusCode() != response.getStatus()) {
            throw new AuthenticationException("Authentication failed : Wrong username or password");
        }

        CodenvyToken token = response.readEntity(CodenvyToken.class);

        if (storeCredentials) {
            final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
            final ServiceReference<SecureStorageService> codenvySecureStorageServiceRef =
                                                                                          context.getServiceReference(SecureStorageService.class);
            if (codenvySecureStorageServiceRef != null) {
                final SecureStorageService codenvySecureStorageService =
                                                                         context.getService(codenvySecureStorageServiceRef);
                codenvySecureStorageService.storeCredentials(getUrl(), credentials, token);
            }
        }

        return token;
    }
}
