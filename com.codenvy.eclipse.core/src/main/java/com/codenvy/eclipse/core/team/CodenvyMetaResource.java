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

import org.eclipse.core.resources.IResource;

/**
 * The Codenvy resource mapping interface.
 * 
 * @author Kevin Pollet
 */
public interface CodenvyMetaResource {
    /**
     * Returns the underlying {@link IResource}.
     * 
     * @return the underlying {@link IResource}.
     */
    IResource getResource();

    /**
     * Returns if this resource is tracked.
     * 
     * @return {@code true} if the resource is tracked, {@code false} otherwise.
     */
    boolean isTracked();
}
