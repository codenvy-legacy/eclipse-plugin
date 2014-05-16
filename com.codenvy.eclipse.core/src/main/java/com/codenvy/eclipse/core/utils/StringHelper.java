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
package com.codenvy.eclipse.core.utils;


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
     * @return {@code true} if the given {@link String}, {@code false} otherwise.
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null ? true : string.trim().isEmpty();
    }

    /**
     * Disable instantiation.
     */
    private StringHelper() {
    }
}
