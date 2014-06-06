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

import static org.eclipse.equinox.security.storage.EncodingUtils.encodeSlashes;

import java.util.List;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.model.Credentials;
import com.codenvy.eclipse.core.model.Token;
import com.codenvy.eclipse.core.services.SecureStorageService;

/**
 * {@link SecureStorageService} tests.
 * 
 * @author Kevin Pollet
 */
public class SecureStorageServiceIT {
    private static final String             DUMMY_URL                            = "http://www.dummy.com";
    private static final String             DUMMY_URL_ENCODED                    = encodeSlashes(DUMMY_URL);
    private static final Credentials DUMMY_CREDENTIALS                    = new Credentials("dummyUsername", "dummyPassword");
    private static final Token       DUMMY_TOKEN                          = new Token("dummyToken");
    private static final String             CODENVY_PREFERENCE_STORAGE_NODE_NAME = "Codenvy";
    private static final String             CODENVY_PASSWORD_KEY_NAME            = "password";
    private static final String             CODENVY_TOKEN_KEY_NAME               = "token";

    private static SecureStorageService     secureStorageService;
    private static ISecurePreferences       codenvyNode;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(WorkspaceServiceIT.class).getBundleContext();
        final ServiceReference<SecureStorageService> secureStorageServiceRef = context.getServiceReference(SecureStorageService.class);
        Assert.assertNotNull(secureStorageServiceRef);

        secureStorageService = context.getService(secureStorageServiceRef);
        Assert.assertNotNull(secureStorageService);

        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        Assert.assertNotNull(root);

        codenvyNode = root.node(CODENVY_PREFERENCE_STORAGE_NODE_NAME);
        Assert.assertNotNull(codenvyNode);
    }

    @Before
    public void beforeTest() {
        codenvyNode.node(DUMMY_URL_ENCODED).removeNode();
    }

    @Test(expected = NullPointerException.class)
    public void testStoreCredentialsWithNullUrl() {
        secureStorageService.storeCredentials(null, DUMMY_CREDENTIALS, DUMMY_TOKEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreCredentialsWithEmptyUrl() {
        secureStorageService.storeCredentials("", DUMMY_CREDENTIALS, DUMMY_TOKEN);
    }

    @Test(expected = NullPointerException.class)
    public void testStoreCredentialsWithNullCredentials() {
        secureStorageService.storeCredentials(DUMMY_URL, null, DUMMY_TOKEN);
    }

    @Test(expected = NullPointerException.class)
    public void testStoreCredentialsWithNullToken() {
        secureStorageService.storeCredentials(DUMMY_URL, DUMMY_CREDENTIALS, null);
    }

    @Test
    public void testStoreCredentials() throws StorageException {
        secureStorageService.storeCredentials(DUMMY_URL, DUMMY_CREDENTIALS, DUMMY_TOKEN);

        Assert.assertTrue(codenvyNode.nodeExists(DUMMY_URL_ENCODED));
        Assert.assertTrue(codenvyNode.node(DUMMY_URL_ENCODED).nodeExists(DUMMY_CREDENTIALS.username));
        Assert.assertEquals(DUMMY_CREDENTIALS.password,
                            codenvyNode.node(DUMMY_URL_ENCODED).node(DUMMY_CREDENTIALS.username).get(CODENVY_PASSWORD_KEY_NAME, null));
        Assert.assertEquals(DUMMY_TOKEN.value,
                            codenvyNode.node(DUMMY_URL_ENCODED).node(DUMMY_CREDENTIALS.username).get(CODENVY_TOKEN_KEY_NAME, null));
    }

    @Test(expected = NullPointerException.class)
    public void testGetPasswordWithNullUrl() {
        secureStorageService.getPassword(null, DUMMY_CREDENTIALS.username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPasswordWithEmptyUrl() {
        secureStorageService.getPassword("", DUMMY_CREDENTIALS.username);
    }

    @Test(expected = NullPointerException.class)
    public void testGetPasswordWithNullUsername() {
        secureStorageService.getPassword(DUMMY_URL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPasswordWithEmptyUsername() {
        secureStorageService.getPassword(DUMMY_URL, "");
    }

    @Test
    public void testGetPassword() throws StorageException {
        final ISecurePreferences dummyNode = codenvyNode.node(DUMMY_URL_ENCODED).node(DUMMY_CREDENTIALS.username);
        dummyNode.put(CODENVY_PASSWORD_KEY_NAME, DUMMY_CREDENTIALS.password, true);
        dummyNode.put(CODENVY_TOKEN_KEY_NAME, DUMMY_TOKEN.value, true);

        final String password = secureStorageService.getPassword(DUMMY_URL, DUMMY_CREDENTIALS.username);

        Assert.assertNotNull(password);
        Assert.assertEquals(DUMMY_CREDENTIALS.password, password);
    }

    @Test(expected = NullPointerException.class)
    public void testGetTokenWithNullUrl() {
        secureStorageService.getToken(null, DUMMY_CREDENTIALS.username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTokenWithEmptyUrl() {
        secureStorageService.getToken("", DUMMY_CREDENTIALS.username);
    }

    @Test(expected = NullPointerException.class)
    public void testGetTokenWithNullUsername() {
        secureStorageService.getToken(DUMMY_URL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTokenWithEmptyUsername() {
        secureStorageService.getToken(DUMMY_URL, "");
    }

    @Test
    public void testGetToken() throws StorageException {
        final ISecurePreferences dummyNode = codenvyNode.node(DUMMY_URL_ENCODED).node(DUMMY_CREDENTIALS.username);
        dummyNode.put(CODENVY_PASSWORD_KEY_NAME, DUMMY_CREDENTIALS.password, true);
        dummyNode.put(CODENVY_TOKEN_KEY_NAME, DUMMY_TOKEN.value, true);

        final Token token = secureStorageService.getToken(DUMMY_URL, DUMMY_CREDENTIALS.username);

        Assert.assertNotNull(token);
        Assert.assertEquals(DUMMY_TOKEN.value, token.value);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteCredentialsWithNullUrl() {
        secureStorageService.deleteCredentials(null, DUMMY_CREDENTIALS.username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteCredentialsWithEmptyUrl() {
        secureStorageService.deleteCredentials("", DUMMY_CREDENTIALS.username);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteCredentialsWithNullUsername() {
        secureStorageService.deleteCredentials(DUMMY_URL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteCredentialsWithEmptyUsername() {
        secureStorageService.deleteCredentials(DUMMY_URL, "");
    }

    @Test
    public void testDeleteCredentials() throws StorageException {
        final ISecurePreferences dummyNode = codenvyNode.node(DUMMY_URL_ENCODED).node(DUMMY_CREDENTIALS.username);
        dummyNode.put(CODENVY_PASSWORD_KEY_NAME, DUMMY_CREDENTIALS.password, true);
        dummyNode.put(CODENVY_TOKEN_KEY_NAME, DUMMY_TOKEN.value, true);

        secureStorageService.deleteCredentials(DUMMY_URL, DUMMY_CREDENTIALS.username);

        Assert.assertTrue(codenvyNode.nodeExists(DUMMY_URL_ENCODED));
        Assert.assertFalse(codenvyNode.node(DUMMY_URL_ENCODED).nodeExists(DUMMY_CREDENTIALS.username));
    }

    @Test(expected = NullPointerException.class)
    public void testDeletePasswordWithNullUrl() {
        secureStorageService.deletePassword(null, DUMMY_CREDENTIALS.username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeletePasswordWithEmptyUrl() {
        secureStorageService.deletePassword("", DUMMY_CREDENTIALS.username);
    }

    @Test(expected = NullPointerException.class)
    public void testDeletePasswordWithNullUsername() {
        secureStorageService.deletePassword(DUMMY_URL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeletePasswordWithEmptyUsername() {
        secureStorageService.deletePassword(DUMMY_URL, "");
    }

    @Test
    public void testDeletePassword() throws StorageException {
        final ISecurePreferences dummyNode = codenvyNode.node(DUMMY_URL_ENCODED).node(DUMMY_CREDENTIALS.username);
        dummyNode.put(CODENVY_PASSWORD_KEY_NAME, DUMMY_CREDENTIALS.password, true);
        dummyNode.put(CODENVY_TOKEN_KEY_NAME, DUMMY_TOKEN.value, true);

        secureStorageService.deletePassword(DUMMY_URL, DUMMY_CREDENTIALS.username);

        Assert.assertTrue(codenvyNode.nodeExists(DUMMY_URL_ENCODED));
        Assert.assertNull(codenvyNode.node(DUMMY_URL_ENCODED).get(CODENVY_PASSWORD_KEY_NAME, null));
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteTokenWithNullUrl() {
        secureStorageService.deleteToken(null, DUMMY_CREDENTIALS.username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTokenWithEmptyUrl() {
        secureStorageService.deleteToken("", DUMMY_CREDENTIALS.username);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteTokenWithNullUsername() {
        secureStorageService.deleteToken(DUMMY_URL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTokenWithEmptyUsername() {
        secureStorageService.deleteToken(DUMMY_URL, "");
    }

    @Test
    public void testDeleteToken() throws StorageException {
        final ISecurePreferences dummyNode = codenvyNode.node(DUMMY_URL_ENCODED).node(DUMMY_CREDENTIALS.username);
        dummyNode.put(CODENVY_PASSWORD_KEY_NAME, DUMMY_CREDENTIALS.password, true);
        dummyNode.put(CODENVY_TOKEN_KEY_NAME, DUMMY_TOKEN.value, true);

        secureStorageService.deleteToken(DUMMY_URL, DUMMY_CREDENTIALS.username);

        Assert.assertTrue(codenvyNode.nodeExists(DUMMY_URL_ENCODED));
        Assert.assertNull(codenvyNode.node(DUMMY_URL_ENCODED).get(CODENVY_TOKEN_KEY_NAME, null));
    }

    @Test
    public void getUrls() throws StorageException {
        final ISecurePreferences dummyNode = codenvyNode.node(DUMMY_URL_ENCODED).node(DUMMY_CREDENTIALS.username);
        dummyNode.put(CODENVY_PASSWORD_KEY_NAME, DUMMY_CREDENTIALS.password, true);
        dummyNode.put(CODENVY_TOKEN_KEY_NAME, DUMMY_TOKEN.value, true);

        final List<String> urls = secureStorageService.getURLs();

        Assert.assertNotNull(urls);
        Assert.assertFalse(urls.isEmpty());
        Assert.assertTrue(urls.contains(DUMMY_URL));
    }

    @Test(expected = NullPointerException.class)
    public void testGetUsernamesForURLWithNullUrl() {
        secureStorageService.getUsernamesForURL(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsernamesForURLWithEmptyUrl() {
        secureStorageService.getUsernamesForURL("");
    }

    @Test
    public void testGetUsernamesForURL() throws StorageException {
        final ISecurePreferences dummyNode = codenvyNode.node(DUMMY_URL_ENCODED).node(DUMMY_CREDENTIALS.username);
        dummyNode.put(CODENVY_PASSWORD_KEY_NAME, DUMMY_CREDENTIALS.password, true);
        dummyNode.put(CODENVY_TOKEN_KEY_NAME, DUMMY_TOKEN.value, true);

        final List<String> usernames = secureStorageService.getUsernamesForURL(DUMMY_URL);

        Assert.assertNotNull(usernames);
        Assert.assertFalse(usernames.isEmpty());
        Assert.assertTrue(usernames.contains(DUMMY_CREDENTIALS.username));
    }

    @Test(expected = NullPointerException.class)
    public void testGetCredentialsWithNullUrl() {
        secureStorageService.getCredentials(null, DUMMY_CREDENTIALS.username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCredentialsWithEmptyUrl() {
        secureStorageService.getCredentials("", DUMMY_CREDENTIALS.username);
    }

    @Test(expected = NullPointerException.class)
    public void testGetCredentialsWithNullUsername() {
        secureStorageService.getCredentials(DUMMY_URL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCredentialsWithEmptyUsername() {
        secureStorageService.getCredentials(DUMMY_URL, "");
    }

    @Test
    public void testGetCredentials() throws StorageException {
        final ISecurePreferences dummyNode = codenvyNode.node(DUMMY_URL_ENCODED).node(DUMMY_CREDENTIALS.username);
        dummyNode.put(CODENVY_PASSWORD_KEY_NAME, DUMMY_CREDENTIALS.password, true);
        dummyNode.put(CODENVY_TOKEN_KEY_NAME, DUMMY_TOKEN.value, true);

        Credentials credentials = secureStorageService.getCredentials(DUMMY_URL, DUMMY_CREDENTIALS.username);

        Assert.assertEquals(DUMMY_CREDENTIALS.username, credentials.username);
        Assert.assertEquals(DUMMY_CREDENTIALS.password, credentials.password);
    }
}