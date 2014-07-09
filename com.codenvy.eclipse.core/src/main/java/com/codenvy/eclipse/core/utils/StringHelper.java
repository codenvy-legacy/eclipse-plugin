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
package com.codenvy.eclipse.core.utils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper class providing utility methods for {@link String}.
 * 
 * @author Stéphane Daviet
 * @author Kevin Pollet
 */
public final class StringHelper {
    /**
     * Returns if the given {@link String} is {@code null} or empty. The {@link String#trim()} method is applied to the given {@link String}
     * .
     * 
     * @param string the {@link String} to check.
     * @return {@code true} if the given {@link String} is {@code null} or empty, {@code false} otherwise.
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null ? true : isEmpty(string);
    }

    /**
     * Returns if the given {@link String} is empty. The {@link String#trim()} method is applied to the given {@link String}.
     * 
     * @param string the {@link String} to check.
     * @return {@code true} if the given {@link String} is empty, {@code false} otherwise.
     * @throws NullPointerException if string parameter is {@code null}.
     */
    public static boolean isEmpty(String string) {
        return checkNotNull(string).trim().isEmpty();
    }

    /**
     * Disable instantiation.
     */
    private StringHelper() {
    }
}
