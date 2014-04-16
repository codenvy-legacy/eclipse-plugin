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
package com.codenvy.eclipse.core.service.api.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the workspace resource on Codenvy.
 * 
 * @author Kevin Pollet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Workspace {
    public final Workspace.WorkspaceRef workspaceRef;

    /**
     * Constructs an instance of {@linkplain Workspace}.
     * 
     * @param workspaceRef the workspace reference.
     * @throws NullPointerException if workspaceRef parameter is {@code null}.
     */
    @JsonCreator
    public Workspace(@JsonProperty("workspaceRef") Workspace.WorkspaceRef workspaceRef) {
        checkNotNull(workspaceRef);

        this.workspaceRef = workspaceRef;
    }

    /**
     * This class represents the workspace reference resource on Codenvy.
     * 
     * @author Kevin Pollet
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(NON_NULL)
    public static class WorkspaceRef {
        public final String id;
        public final String name;
        public final String organizationId;

        /**
         * Constructs an instance of {@linkplain WorkspaceRef}.
         * 
         * @param id the workspace reference id.
         * @param name the workspace reference name.
         * @param organizationId the workspace organization.
         * @throws NullPointerException if name parameter is {@code null}.
         */
        @JsonCreator
        public WorkspaceRef(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("organizationId") String organizationId) {
            checkNotNull(name);

            this.id = id;
            this.name = name;
            this.organizationId = organizationId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((organizationId == null) ? 0 : organizationId.hashCode());
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            WorkspaceRef other = (WorkspaceRef)obj;
            if (organizationId == null) {
                if (other.organizationId != null)
                    return false;
            } else if (!organizationId.equals(other.organizationId))
                return false;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((workspaceRef == null) ? 0 : workspaceRef.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Workspace other = (Workspace)obj;
        if (workspaceRef == null) {
            if (other.workspaceRef != null)
                return false;
        } else if (!workspaceRef.equals(other.workspaceRef))
            return false;
        return true;
    }
}
