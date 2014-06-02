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

import java.util.List;

import com.codenvy.eclipse.core.model.Account;

/**
 * Codenvy account service contract.
 * 
 * @author Kevin Pollet
 */
public interface AccountService extends RestServiceWithAuth {
    /**
     * Returns the accounts of the current user.
     * 
     * @return the accounts of the current user.
     */
    List<Account> getCurrentUserAccounts();
}
