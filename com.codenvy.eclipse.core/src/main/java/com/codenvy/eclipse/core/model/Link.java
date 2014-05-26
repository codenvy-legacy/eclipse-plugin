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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The link model class.
 * 
 * @author Kevin Pollet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {
    public static final String WEB_LINK_REL_ATTRIBUTE_VALUE = "web url";

    public final String        href;
    public final String        rel;
    public final String        produces;
    public final String        consumes;
    public final String        method;

    @JsonCreator
    public Link(@JsonProperty("href") String href,
                @JsonProperty("rel") String rel,
                @JsonProperty("produces") String produces,
                @JsonProperty("consumes") String consumes,
                @JsonProperty("method") String method) {

        this.href = href;
        this.rel = rel;
        this.produces = produces;
        this.consumes = consumes;
        this.method = method;
    }

    @Override
    public String toString() {
        return "Link [href=" + href + ", rel=" + rel + ", produces=" + produces + ", consumes=" + consumes + ", method=" + method + "]";
    }
}
