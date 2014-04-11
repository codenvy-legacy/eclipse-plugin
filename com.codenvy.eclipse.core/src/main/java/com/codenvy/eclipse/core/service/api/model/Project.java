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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the project resource on Codenvy.
 * 
 * @author Kevin Pollet
 */
public class Project {
    public final String url;
    public final String visibility;
    public final String projectTypeId;
    public final String workspace;
    public final String projectTypeName;
    public final String name;
    public final String description;

    /**
     * Constructs an instance of {@linkplain Project}.
     * 
     * @param url the project url.
     * @param visibility the project visibility (private or public).
     * @param projectTypeId the project type id (e.g. spring, java, ...).
     * @param workspace the project workspace id.
     * @param projectTypeName the project type name (e.g. Spring application, ...).
     * @param name the project name.
     * @param description the project description.
     */
    @JsonCreator
    public Project(
                   @JsonProperty("url") String url,
                   @JsonProperty("visibility") String visibility,
                   @JsonProperty("projectTypeId") String projectTypeId,
                   @JsonProperty("workspace") String workspace,
                   @JsonProperty("projectTypeName") String projectTypeName,
                   @JsonProperty("name") String name,
                   @JsonProperty("description") String description) {

        this.url = url;
        this.visibility = visibility;
        this.projectTypeId = projectTypeId;
        this.workspace = workspace;
        this.projectTypeName = projectTypeName;
        this.name = name;
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                 + ((workspace == null) ? 0 : workspace.hashCode());
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
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (workspace == null) {
            if (other.workspace != null)
                return false;
        } else if (!workspace.equals(other.workspace))
            return false;
        return true;
    }
}
