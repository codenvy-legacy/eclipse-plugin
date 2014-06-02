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

import static com.codenvy.eclipse.core.model.Link.WEB_LINK_REL_ATTRIBUTE_VALUE;

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
public class RunnerStatus {
    public final long       stopTime;
    public final String     debugHost;
    public final long       debugPort;
    public final long       processId;
    public final long       startTime;
    public final Status     status;
    public final List<Link> links;

    @JsonCreator
    public RunnerStatus(@JsonProperty("stopTime") long stopTime,
                               @JsonProperty("debugHost") String debugHost,
                               @JsonProperty("debugPort") long debugPort,
                               @JsonProperty("processId") long processId,
                               @JsonProperty("startTime") long startTime,
                               @JsonProperty("status") Status status,
                               @JsonProperty("links") List<Link> links) {

        this.stopTime = stopTime;
        this.debugHost = debugHost;
        this.debugPort = debugPort;
        this.processId = processId;
        this.startTime = startTime;
        this.status = status;
        this.links = ImmutableList.copyOf(links == null ? new ArrayList<Link>() : links);
    }

    /**
     * Returns the web {@link Link}.
     * 
     * @return the web {@link Link} or {@code null}.
     */
    public Link getWebLink() {
        for (Link oneLink : links) {
            if (WEB_LINK_REL_ATTRIBUTE_VALUE.equalsIgnoreCase(oneLink.rel)) {
                return oneLink;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CodenvyRunnerStatus [stopTime=" + stopTime + ", debugHost=" + debugHost + ", debugPort=" + debugPort + ", processId="
               + processId + ", startTime=" + startTime + ", status=" + status + ", links=" + links + "]";
    }

    public enum Status {
        NEW,
        RUNNING,
        CANCELLED,
        STOPPED
    }
}
