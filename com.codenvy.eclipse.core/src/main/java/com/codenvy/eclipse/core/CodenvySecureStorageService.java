package com.codenvy.eclipse.core;

import org.eclipse.equinox.security.storage.StorageException;

import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyToken;

public interface CodenvySecureStorageService {
    void storeCredentials(String url, CodenvyCredentials credentials, CodenvyToken token) throws StorageException;

    String getPassword(String url, String username) throws StorageException;

    String getToken(String url, String username) throws StorageException;

    void deleteCredentials(String url, String username) throws StorageException;

    String[] getURLs() throws StorageException;

    String[] getUsernamesForURL(String url) throws StorageException;

}
