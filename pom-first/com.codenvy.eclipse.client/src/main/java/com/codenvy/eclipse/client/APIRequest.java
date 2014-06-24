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
package com.codenvy.eclipse.client;

import com.codenvy.eclipse.client.exceptions.CodenvyException;

/**
 * The API request contract returned by the Codenvy client API.
 * 
 * @author Kevin Pollet
 * @param <T> the API request return {@linkplain java.lang.reflect.Type Type}
 */
public interface APIRequest<T> {
    /**
     * Executes the Codenvy API request.
     * 
     * @return the API request result.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    T execute() throws CodenvyException;
}
