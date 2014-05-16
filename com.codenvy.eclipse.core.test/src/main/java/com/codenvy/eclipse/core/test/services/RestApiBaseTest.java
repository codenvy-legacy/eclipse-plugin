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
package com.codenvy.eclipse.core.test.services;

import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;


/**
 * The REST API base test.
 * 
 * @author Kevin Pollet
 */
public abstract class RestApiBaseTest {
    private static final String REST_API_URL_PROPERTY_NAME = "rest.api.url";
    protected static String     REST_API_URL;

    @BeforeClass
    public static void loadRestApiUrl() {
        final Properties codenvySdkProperties = new Properties();
        try {

            codenvySdkProperties.load(RestApiBaseTest.class.getResourceAsStream("/codenvy-sdk.properties"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        REST_API_URL = codenvySdkProperties.getProperty(REST_API_URL_PROPERTY_NAME);
        Assert.assertNotNull(REST_API_URL);
    }
}
