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
package com.codenvy.eclipse.core.services;


/**
 * The service REST factory contract.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public interface RestServiceFactory {
    /**
     * Constructs an instance of a REST service with the given type.
     * 
     * @param clazz the REST service type.
     * @param url the REST service url.
     * @return the new REST service instance never {@code null}.
     * @throws NullPointerException if clazz parameter is {@code null}.
     */
    <T extends RestService, S extends T> T newRestService(Class<T> clazz, String url);

    /**
     * Constructs an instance of a REST service with the given type.
     * 
     * @param clazz the REST service type.
     * @param url the REST service url.
     * @param username the username.
     * @return the new REST service instance never {@code null}.
     * @throws NullPointerException if clazz parameter is {@code null}.
     */
    <T extends RestServiceWithAuth, S extends T> T newRestServiceWithAuth(Class<T> clazz, String url, String username);
}
