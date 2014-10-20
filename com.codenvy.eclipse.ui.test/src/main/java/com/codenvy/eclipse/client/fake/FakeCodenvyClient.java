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
package com.codenvy.eclipse.client.fake;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.codenvy.client.CodenvyBuilder;
import com.codenvy.client.CodenvyClient;
import com.codenvy.client.auth.Credentials;
import com.codenvy.client.auth.CredentialsBuilder;
import com.codenvy.client.auth.Token;
import com.codenvy.client.auth.TokenBuilder;
import com.codenvy.client.model.Project;
import com.codenvy.client.model.ProjectBuilder;
import com.codenvy.client.model.runner.RunOptionsBuilder;

public class FakeCodenvyClient implements CodenvyClient {

    class DummyCredentialsBuilder implements CredentialsBuilder {

        private boolean storeOnlyToken;
        private String  password;
        private Token   token;
        private String  username;

        @Override
        public Credentials build() {
            return new DummyCredentials(username, password, token, storeOnlyToken);
        }

        @Override
        public CredentialsBuilder storeOnlyToken(boolean storeOnlyToken) {
            this.storeOnlyToken = storeOnlyToken;
            return this;
        }

        @Override
        public CredentialsBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        @Override
        public CredentialsBuilder withToken(Token token) {
            this.token = token;
            return this;
        }

        @Override
        public CredentialsBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

    }

    class DummyCredentials implements Credentials {

        private String  password;
        private Token   token;
        private String  username;
        private boolean storeOnly;

        public DummyCredentials(String username, String password, Token token, boolean storeOnly) {
            this.username = username;
            this.password = password;
            this.token = token;
            this.storeOnly = storeOnly;
        }

        @Override
        public boolean isStoreOnlyToken() {
            return storeOnly;
        }

        @Override
        public String password() {
            return password;
        }

        @Override
        public Token token() {
            return token;
        }

        @Override
        public String username() {
            return username;
        }

    }

    @Override
    public CodenvyBuilder newCodenvyBuilder(String arg0, String arg1) {
        return new FakeCodenvyBuilder();
    }

    @Override
    public CredentialsBuilder newCredentialsBuilder() {
        return new DummyCredentialsBuilder();
    }

    @Override
    public ProjectBuilder newProjectBuilder() {
        final ProjectBuilder projectBuilder = mock(ProjectBuilder.class);
        final Project project = mock(Project.class);
        doReturn(project).when(projectBuilder).build();
        return projectBuilder;
    }

    @Override
    public TokenBuilder newTokenBuilder(String value) {
        final TokenBuilder tokenBuilder = mock(TokenBuilder.class);
        final Token token = mock(Token.class);
        doReturn(value).when(token).value();
        doReturn(token).when(tokenBuilder).build();
        return tokenBuilder;
    }

    @Override
    public RunOptionsBuilder newRunOptionsBuilder() {
        final RunOptionsBuilder runOptionsBuilder = mock(RunOptionsBuilder.class);
        return runOptionsBuilder;
    }

}
