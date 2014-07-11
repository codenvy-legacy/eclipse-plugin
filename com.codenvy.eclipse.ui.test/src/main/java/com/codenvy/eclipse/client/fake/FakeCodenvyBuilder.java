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

import com.codenvy.client.Codenvy;
import com.codenvy.client.CodenvyBuilder;
import com.codenvy.client.auth.Credentials;
import com.codenvy.client.auth.CredentialsProvider;
import com.codenvy.client.store.DataStoreFactory;

public class FakeCodenvyBuilder implements CodenvyBuilder {

	@Override
	public Codenvy build() {
		return new FakeCodenvy();
	}

	@Override
	public CodenvyBuilder withCredentials(Credentials arg0) {
		return this;
	}

	@Override
	public CodenvyBuilder withCredentialsProvider(CredentialsProvider arg0) {
		return this;
	}

	@Override
	public CodenvyBuilder withCredentialsStoreFactory(
			DataStoreFactory<String, Credentials> arg0) {
		return this;
	}

}
