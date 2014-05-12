package com.codenvy.eclipse.core;

import java.net.URI;

import org.eclipse.equinox.security.storage.StorageException;

import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyToken;

public interface CodenvySecureStorageService {
    void storeCredentials(URI url, CodenvyCredentials credentials, CodenvyToken token) throws StorageException;

    String getPassword(URI url, String username) throws StorageException;

    String getToken(URI url, String username) throws StorageException;

    void deleteCredentials(URI url, String username) throws StorageException;
}
