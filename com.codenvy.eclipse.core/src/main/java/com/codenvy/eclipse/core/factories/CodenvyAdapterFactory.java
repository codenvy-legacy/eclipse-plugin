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
package com.codenvy.eclipse.core.factories;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;

import com.codenvy.eclipse.core.team.CodenvyMetaResource;

/**
 * The Codenvy adapter factory.
 * 
 * @author Kevin Pollet
 */
public class CodenvyAdapterFactory implements IAdapterFactory {
    @Override
    public synchronized Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
        if (CodenvyMetaResource.class == adapterType && adaptableObject instanceof IResource) {
            return new CodenvyMetaResource((IResource)adaptableObject);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
        return new Class[]{CodenvyMetaResource.class};
    }
}
