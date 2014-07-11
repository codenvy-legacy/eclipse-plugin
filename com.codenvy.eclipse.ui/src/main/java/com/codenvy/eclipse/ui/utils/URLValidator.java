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
package com.codenvy.eclipse.ui.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import com.codenvy.eclipse.core.utils.StringHelper;

/**
 * Validator used to validate that a {@link String} is well formed URL.
 * 
 * @author Kevin Pollet
 */
public final class URLValidator {
    private final Set<String> protocols;

    /**
     * Constructs an instance of {@link URLValidator}.
     * 
     * @param protocols the valid URL protocols.
     */
    public URLValidator(Set<String> protocols) {
        this.protocols = protocols;
    }

    /**
     * Validates that the given {@link String} is a valid URL.
     * 
     * @param url the URL to validate.
     * @return {@code true} if the given URL is valid, {@code false} otherwise.
     */
    public boolean isValid(String url) {
        if (StringHelper.isNullOrEmpty(url)) {
            return false;
        }

        try {

            final URL codenvyRepositoryURL = new URL(url);
            final String protocol = codenvyRepositoryURL.getProtocol();
            final String host = codenvyRepositoryURL.getHost();

            if (protocols == null || !protocols.contains(protocol)) {
                return false;
            }

            if (host == null || host.length() <= 0) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }
}
