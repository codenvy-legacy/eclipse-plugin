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

import static java.util.Arrays.asList;

import java.util.List;

import com.codenvy.eclipse.core.model.CodenvyAccount;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.services.AccountService;

/**
 * {@link AccountService} mock.
 * 
 * @author Kevin Pollet
 */
public class AccountServiceMock implements AccountService {
    public static final String MOCK_ACCOUNT_ID = "account-codenvy-id";

    public AccountServiceMock(String url, CodenvyToken codenvyToken) {
    }

    @Override
    public List<CodenvyAccount> getCurrentUserAccounts() {
        return asList(new CodenvyAccount(MOCK_ACCOUNT_ID));
    }
}
