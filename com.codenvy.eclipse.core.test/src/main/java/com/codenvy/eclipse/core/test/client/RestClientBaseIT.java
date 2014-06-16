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
package com.codenvy.eclipse.core.test.client;

import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;

import com.codenvy.eclipse.core.client.Codenvy;
import com.codenvy.eclipse.core.client.store.secure.SecureStorageDataStoreFactory;


/**
 * REST API base test.
 * 
 * @author Kevin Pollet
 */
public abstract class RestClientBaseIT {
    private static final String   REST_API_URL_PROPERTY_NAME = "rest.api.url";

    protected static final String DUMMY_USERNAME             = "dummyUsername";
    protected static final String DUMMY_PASSWORD             = "dummyPassword";
    protected static final String SDK_TOKEN_VALUE            = "123123";
    protected static final String SDK_WORKSPACE_NAME         = "default";
    protected static Codenvy      codenvy;
    protected static String       REST_API_URL;

    @BeforeClass
    public static void loadRestApiUrl() {
        final Properties codenvySdkProperties = new Properties();
        try {
            codenvySdkProperties.load(RestClientBaseIT.class.getResourceAsStream("/codenvy-sdk.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        REST_API_URL = System.getProperty(REST_API_URL_PROPERTY_NAME, codenvySdkProperties.getProperty(REST_API_URL_PROPERTY_NAME));
        Assert.assertNotNull(REST_API_URL);

        codenvy = new Codenvy.Builder(REST_API_URL, DUMMY_USERNAME, SecureStorageDataStoreFactory.INSTANCE).build();
    }
}
