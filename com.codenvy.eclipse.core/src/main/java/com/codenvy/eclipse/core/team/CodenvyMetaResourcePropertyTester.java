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
package com.codenvy.eclipse.core.team;

import org.eclipse.core.expressions.PropertyTester;

/**
 * The property tester for {@link CodenvyMetaResource}.
 * 
 * @author Kevin Pollet
 */
public class CodenvyMetaResourcePropertyTester extends PropertyTester {
    private static final String TRACKED_PROPERTY_NAME = "tracked";
    
    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (receiver instanceof CodenvyMetaResource) {
            final CodenvyMetaResource metaResource = (CodenvyMetaResource)receiver;
            
            if (TRACKED_PROPERTY_NAME.equals(property) && expectedValue instanceof Boolean) {
                return metaResource.isTracked() == (boolean) expectedValue;
            }
        }
        return false;
    }

}
