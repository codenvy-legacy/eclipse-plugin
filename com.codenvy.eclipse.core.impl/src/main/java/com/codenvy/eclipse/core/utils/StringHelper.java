package com.codenvy.eclipse.core.utils;

import com.google.common.base.Strings;

/**
 * Little helper class for String utils like isNullOrEmpty
 * 
 * @author Stéphane Daviet
 */

public class StringHelper {
    public static boolean isNullOrEmpty(String string) {
        return Strings.nullToEmpty(string).isEmpty();
    }
}
