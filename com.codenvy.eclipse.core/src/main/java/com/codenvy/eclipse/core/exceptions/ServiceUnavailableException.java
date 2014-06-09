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
package com.codenvy.eclipse.core.exceptions;


/**
 * Exception thrown when the OSGi service invoked with the {@linkplain com.codenvy.eclipse.core.utils.ServiceHelper ServiceHelper} is not
 * available.
 * 
 * @author Kevin Pollet
 */
public class ServiceUnavailableException extends Exception {
    private static final long serialVersionUID = 726825005098377503L;

    /**
     * Constructs an instance of {@link ServiceUnavailableException} with a default message for the given OSGi service {@link Class}.
     */
    public ServiceUnavailableException(Class< ? > clazz) {
        super("The OSGi service " + clazz.getName() + " is not available");
    }

    /**
     * Constructs an instance of {@link ServiceUnavailableException} with the given message.
     * 
     * @param message the exception message.
     */
    public ServiceUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs an instance of {@link ServiceUnavailableException} with the given cause.
     * 
     * @param cause the cause.
     */
    public ServiceUnavailableException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an instance of {@link ServiceUnavailableException} with the given message and cause.
     * 
     * @param message the exception message.
     * @param cause the cause.
     */
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
