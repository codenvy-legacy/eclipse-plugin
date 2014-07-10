/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.ui.wizard.common;

import org.eclipse.jface.wizard.IWizard;

/**
 * Interface for {@link IWizard} providing Codenvy credentials.
 * 
 * @author Stéphane Daviet
 * @author Kevin Pollet
 */
public interface CredentialsProviderWizard extends IWizard {
    /**
     * Returns the Codenvy platform URL.
     * 
     * @return the Codenvy platform URL.
     */
    String getUrl();

    /**
     * Returns the Codenvy username.
     * 
     * @return the Codenvy username.
     */
    String getUsername();

    /**
     * Returns the Codenvy user password.
     * 
     * @return the Codenvy user password.
     */
    String getPassword();

    /**
     * Returns if the user {@linkplain com.codenvy.client.auth.Credentials Credentials} must be stored.
     * 
     * @return {@code true} if the user {@linkplain com.codenvy.client.auth.Credentials Credentials} must be stored, {@code false}
     *         otherwise.
     */
    boolean isStoreUserCredentials();
}
