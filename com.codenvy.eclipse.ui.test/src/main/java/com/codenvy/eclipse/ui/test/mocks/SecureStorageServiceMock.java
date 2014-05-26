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
package com.codenvy.eclipse.ui.test.mocks;

import java.util.ArrayList;
import java.util.List;

import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.services.SecureStorageService;

/**
 * {@link SecureStorageService} mock.
 * 
 * @author Kevin Pollet
 */
public class SecureStorageServiceMock implements SecureStorageService {
    @Override
    public void storeCredentials(String url, CodenvyCredentials credentials, CodenvyToken token) {
    }

    @Override
    public String getPassword(String url, String username) {
        return null;
    }

    @Override
    public CodenvyToken getToken(String url, String username) {
        return null;
    }

    @Override
    public void deleteCredentials(String url, String username) {
    }

    @Override
    public List<String> getURLs() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getUsernamesForURL(String url) {
        return new ArrayList<>();
    }
}
