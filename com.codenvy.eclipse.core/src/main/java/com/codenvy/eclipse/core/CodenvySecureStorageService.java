package com.codenvy.eclipse.core;

import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyToken;

/**
 * Convenient OSGI service that provides all the operations related to secure storage for Codenvy credentials.
 * 
 * @author Stéphane Daviet
 */
public interface CodenvySecureStorageService {
    void storeCredentials(String url, CodenvyCredentials credentials, CodenvyToken token);

    String getPassword(String url, String username);

    String getToken(String url, String username);

    void deleteCredentials(String url, String username);

    String[] getURLs();

    String[] getUsernamesForURL(String url);
}
