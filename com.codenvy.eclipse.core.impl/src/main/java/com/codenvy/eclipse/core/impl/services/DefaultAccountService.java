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
package com.codenvy.eclipse.core.impl.services;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.core.GenericType;

import com.codenvy.eclipse.core.model.Account;
import com.codenvy.eclipse.core.services.AbstractRestServiceWithAuth;
import com.codenvy.eclipse.core.services.AccountService;

/**
 * The Codenvy account client service.
 * 
 * @author Kevin Pollet
 */
public class DefaultAccountService extends AbstractRestServiceWithAuth implements AccountService {
    /**
     * Constructs an instance of {@linkplain DefaultAccountService}.
     * 
     * @param url the Codenvy platform url.
     * @param username the username.
     * @throws NullPointerException if url or username is {@code null}.
     * @throws IllegalArgumentException if url or username are empty {@linkplain String}.
     */
    public DefaultAccountService(String url, String username) {
        super(url, username, "api/account");
    }

    @Override
    public List<Account> getCurrentUserAccounts() {
        return getWebTarget().request()
                             .accept(APPLICATION_JSON)
                             .get(new GenericType<List<Account>>() {
                             });
    }
}
