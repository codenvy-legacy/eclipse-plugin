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
package com.codenvy.eclipse.core.services;

import static com.codenvy.eclipse.core.utils.StringHelper.isNullOrEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.client.WebTarget;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.TokenRenewalFilter;
import com.codenvy.eclipse.core.model.CodenvyToken;

/**
 * Abstract rest service with authentication implementation.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 * @see RestServiceFactory
 */
public class AbstractRestServiceWithAuth extends AbstractRestService {
    private static final String TOKEN_PARAMETER_NAME = "token";

    private final String        username;

    /**
     * Constructs an instance of {@linkplain AbstractRestServiceWithAuth}.
     * 
     * @param url the Codenvy platform url.
     * @param username the username.
     * @param rootPath the rest service root path
     * @throws NullPointerException if url, rootPath or codenvyToken parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public AbstractRestServiceWithAuth(String url, String username, String rootPath) {
        super(url, rootPath);

        checkNotNull(username);
        checkArgument(!isNullOrEmpty(username));
        this.username = username;

        super.getWebTarget().register(new TokenRenewalFilter(getUrl(), getUsername()));
    }

    /**
     * Get the {@link CodenvyToken} from the secure storage through {@link SecureStorageService#getToken(String, String)} based on
     * {@link #getUrl()} and {@link #getUsername()} for parameters.
     * 
     * @return the {@link CodenvyToken} or {@code null} is none is stored for URL and username.
     */
    public CodenvyToken getCodenvyToken() {
        final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

        final ServiceReference<SecureStorageService> codenvySecureStorageServiceRef =
                                                                                      context.getServiceReference(SecureStorageService.class);
        if (codenvySecureStorageServiceRef != null) {
            try {
                final SecureStorageService codenvySecureStorageService =
                                                                         context.getService(codenvySecureStorageServiceRef);
                if (codenvySecureStorageService != null) {
                    return codenvySecureStorageService.getToken(getUrl(), getUsername());
                }
            } finally {
                context.ungetService(codenvySecureStorageServiceRef);
            }
        }
        return null;
    }

    @Override
    public WebTarget getWebTarget() {
        CodenvyToken token = getCodenvyToken();
        if (token == null) {
            throw new RuntimeException("Token not found in secure storage.");
        }
        return super.getWebTarget().queryParam(TOKEN_PARAMETER_NAME, token.value);
    }

    /**
     * Get the username used to authenticate.
     * 
     * @return the username.
     */
    public String getUsername() {
        return username;
    }
}
