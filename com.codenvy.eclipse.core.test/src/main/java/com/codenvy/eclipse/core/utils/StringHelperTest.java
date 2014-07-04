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
