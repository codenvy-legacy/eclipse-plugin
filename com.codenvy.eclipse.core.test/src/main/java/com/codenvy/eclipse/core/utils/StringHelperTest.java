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

import org.junit.Assert;
import org.junit.Test;

import com.codenvy.eclipse.core.utils.StringHelper;

/**
 * {@link StringHelper} test.
 * 
 * @author Kevin Pollet
 */
public class StringHelperTest {
    @Test
    public void testIsNullOrEmptyWithNull() {
        final boolean result = StringHelper.isNullOrEmpty(null);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsNullOrEmptyWithEmptyString() {
        final boolean result = StringHelper.isNullOrEmpty("");

        Assert.assertTrue(result);
    }

    @Test
    public void testIsNullOrEmptyWithEmptyStringWithWhiteSpaces() {
        final boolean result = StringHelper.isNullOrEmpty("  ");

        Assert.assertTrue(result);
    }

    @Test
    public void testIsNullOrEmpty() {
        final boolean result = StringHelper.isNullOrEmpty("not null or empty");

        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void testIsEmptyWithNull() {
        StringHelper.isEmpty(null);
    }

    @Test
    public void testIsEmptyWithEmptyString() {
        final boolean result = StringHelper.isEmpty("");

        Assert.assertTrue(result);
    }

    @Test
    public void testIsEmptyWithEmptyStringWithWhiteSpaces() {
        final boolean result = StringHelper.isEmpty("   ");

        Assert.assertTrue(result);
    }

    @Test
    public void testIsEmpty() {
        final boolean result = StringHelper.isEmpty("not empty");

        Assert.assertFalse(result);
    }
}
