package com.codenvy.eclipse.core.exceptions;

import org.eclipse.equinox.security.storage.StorageException;

/**
 * Unchecked exception to wrap {@link StorageException}: this is technical exception and we don't want to catch it everytime.
 * 
 * @author Stéphane Daviet
 */
public class SecureStorageRuntimException extends RuntimeException {
    private static final long   serialVersionUID = 6691712539898716100L;

    private static final String DEFAULT_MESSAGE  = "Failure when trying to perform operation on secure storage.";

    public SecureStorageRuntimException() {
        super(DEFAULT_MESSAGE);
    }

    public SecureStorageRuntimException(String message) {
        super(message);
    }

    public SecureStorageRuntimException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public SecureStorageRuntimException(String message, Throwable cause) {
        super(message, cause);
    }
}
