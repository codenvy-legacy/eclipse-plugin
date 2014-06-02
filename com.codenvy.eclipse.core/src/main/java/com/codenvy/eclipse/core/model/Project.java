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
package com.codenvy.eclipse.core.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the project resource on Codenvy.
 * 
 * @author Kevin Pollet
 */
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    public final String url;
    public final String visibility;
    public final String projectTypeId;
    public final String workspaceId;
    public final String projectTypeName;
    public final String name;
    public final String description;
    public final String workspaceName;
    public final Date   modificationDate;
    public final Date   creationDate;
    public final String ideUrl;

    /**
     * Constructs an instance of {@linkplain Project}.
     * 
     * @param url the project url.
     * @param visibility the project visibility (private or public).
     * @param projectTypeId the project type id (e.g. spring, java, ...).
     * @param workspaceId the project workspace id.
     * @param projectTypeName the project type name (e.g. Spring application, ...).
     * @param name the project name.
     * @param description the project description.
     * @param workspaceName the project workspace name.
     * @param modificationDate the project modification date.
     * @param creationDate the project creation date.
     * @param ideUrl the project ide url.
     */
    @JsonCreator
    public Project(
                          @JsonProperty("url") String url,
                          @JsonProperty("visibility") String visibility,
                          @JsonProperty("projectTypeId") String projectTypeId,
                          @JsonProperty("workspaceId") String workspaceId,
                          @JsonProperty("projectTypeName") String projectTypeName,
                          @JsonProperty("name") String name,
                          @JsonProperty("description") String description,
                          @JsonProperty("workspaceName") String workspaceName,
                          @JsonProperty("modificationDate") Date modificationDate,
                          @JsonProperty("creationDate") Date creationDate,
                          @JsonProperty("ideUrl") String ideUrl) {

        this.url = url;
        this.visibility = visibility;
        this.projectTypeId = projectTypeId;
        this.workspaceId = workspaceId;
        this.projectTypeName = projectTypeName;
        this.name = name;
        this.description = description;
        this.workspaceName = workspaceName;
        this.modificationDate = modificationDate;
        this.creationDate = creationDate;
        this.ideUrl = ideUrl;
    }


    /**
     * The Codenvy project builder.
     * 
     * @author Kevin Pollet
     */
    public static class Builder {
        private String url;
        private String visibility;
        private String projectTypeId;
        private String workspaceId;
        private String projectTypeName;
        private String name;
        private String description;
        private String workspaceName;
        private String ideUrl;

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withVisibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder withProjectTypeId(String projectTypeId) {
            this.projectTypeId = projectTypeId;
            return this;
        }

        public Builder withWorkspaceId(String workspaceId) {
            this.workspaceId = workspaceId;
            return this;
        }

        public Builder withProjectTypeName(String projectTypeName) {
            this.projectTypeName = projectTypeName;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withWorkspaceName(String workspaceName) {
            this.workspaceName = workspaceName;
            return this;
        }

        public Builder withIdeUrl(String ideUrl) {
            this.ideUrl = ideUrl;
            return this;
        }

        public Project build() {
            return new Project(url, visibility, projectTypeId, workspaceId, projectTypeName, name, description, workspaceName,
                                      null, new Date(), ideUrl);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((projectTypeId == null) ? 0 : projectTypeId.hashCode());
        result = prime * result + ((workspaceId == null) ? 0 : workspaceId.hashCode());
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
        Project other = (Project)obj;
        if (creationDate == null) {
            if (other.creationDate != null)
                return false;
        } else if (!creationDate.equals(other.creationDate))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (projectTypeId == null) {
            if (other.projectTypeId != null)
                return false;
        } else if (!projectTypeId.equals(other.projectTypeId))
            return false;
        if (workspaceId == null) {
            if (other.workspaceId != null)
                return false;
        } else if (!workspaceId.equals(other.workspaceId))
            return false;
        return true;
    }
}
