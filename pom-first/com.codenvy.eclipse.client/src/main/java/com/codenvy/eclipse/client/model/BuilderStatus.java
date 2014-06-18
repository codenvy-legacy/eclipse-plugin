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
package com.codenvy.eclipse.client.model;

import static com.codenvy.eclipse.client.model.Link.DOWNLOAD_LINK_REL_ATTRIBUTE_VALUE;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * The codenvy runner object model.
 * 
 * @author Kevin Pollet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuilderStatus {
    public final long       taskId;
    public final long       startTime;
    public final Status     status;
    public final List<Link> links;

    @JsonCreator
    public BuilderStatus(@JsonProperty("taskId") long taskId,
                                @JsonProperty("startTime") long startTime,
                                @JsonProperty("status") Status status,
                                @JsonProperty("links") List<Link> links) {
        this.taskId = taskId;
        this.startTime = startTime;
        this.status = status;
        this.links = ImmutableList.copyOf(links == null ? new ArrayList<Link>() : links);
    }

    /**
     * Gets the build result download {@link Link}.
     * 
     * @return the download {@link Link}.
     */
    public Link getDownloadLink() {
        for (Link oneLink : links) {
            if (DOWNLOAD_LINK_REL_ATTRIBUTE_VALUE.equals(oneLink.rel)) {
                return oneLink;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CodenvyBuilderStatus [taskId=" + taskId + ", startTime=" + startTime + ", status=" + status + ", links=" + links + "]";
    }

    public enum Status {
        IN_QUEUE,
        IN_PROGRESS,
        SUCCESSFUL,
        FAILED,
        CANCELLED
    }
}
