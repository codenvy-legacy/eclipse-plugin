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
