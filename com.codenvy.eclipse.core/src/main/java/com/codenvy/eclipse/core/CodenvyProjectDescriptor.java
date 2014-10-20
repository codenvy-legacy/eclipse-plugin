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
package com.codenvy.eclipse.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Codenvy project descriptor.
 *
 * @author Kevin Pollet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodenvyProjectDescriptor {
    public static final String       DEFAULT_BUILDER = "default";

    public final Type                type;
    public final Map<String, String> builders;

    public static CodenvyProjectDescriptor load(InputStream inputStream) {
        final ObjectMapper mapper = new ObjectMapper();
        try {

            return mapper.readValue(inputStream, CodenvyProjectDescriptor.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonCreator
    public CodenvyProjectDescriptor(@JsonProperty("type") Type type, @JsonProperty("builders") Map<String, String> builders) {
        this.type = checkNotNull(type);
        this.builders = checkNotNull(builders);
    }

    public String getBuilder(String name) {
        return builders.get(name);
    }

    public enum Type {
        ANGULARJS,
        MAVEN,
        UNKNOWN;

        @JsonCreator
        static Type forValue(String value) {
            Type projectType;

            try {

                projectType = Enum.valueOf(Type.class, value.toUpperCase());

            } catch (NullPointerException | IllegalArgumentException e) {
                projectType = UNKNOWN;
            }

            return projectType;
        }
    }
}
