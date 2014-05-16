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

import java.net.URI;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

/**
 * Abstract rest service implementation.
 * 
 * @author Kevin Pollet
 * @see RestServiceFactory
 */
public abstract class AbstractRestService {
    private final String    url;
    private final WebTarget webTarget;

    /**
     * Constructs an instance of {@linkplain RestService}.
     * 
     * @param url the Codenvy platform url.
     * @param rootPath the rest service root path
     * @throws NullPointerException if url or rootPath parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public AbstractRestService(String url, String rootPath) {
        checkNotNull(url);
        checkArgument(!isNullOrEmpty(url));
        checkNotNull(rootPath);

        this.url = url;

        final URI uri = UriBuilder.fromUri(url).path(rootPath).build();
        this.webTarget = ClientBuilder.newClient().target(uri);
    }

    public String getUrl() {
        return url;
    }

    public WebTarget getWebTarget() {
        return webTarget;
    }
}
