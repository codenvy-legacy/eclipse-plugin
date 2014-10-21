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

import static com.codenvy.eclipse.core.CodenvyConstants.CODENVY_FOLDER_NAME;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

/**
 * The Codenvy project descriptor. The "project.json" file.
 *
 * @author Kevin Pollet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class CodenvyProjectDescriptor {
    public static final String       PROJECT_DESCRIPTOR_FILE_NAME = "project.json";
    public static final String       DEFAULT_PROJECT_BUILDER      = "default";

    public final Type                type;
    public final Map<String, String> builders;

    public static CodenvyProjectDescriptor load(IProject project) {
        final IFile projectDescriptor = project.getFolder(CODENVY_FOLDER_NAME).getFile(PROJECT_DESCRIPTOR_FILE_NAME);
        if (projectDescriptor.exists()) {
            final ObjectMapper mapper = new ObjectMapper();
            try {

                return mapper.readValue(projectDescriptor.getContents(), CodenvyProjectDescriptor.class);

            } catch (IOException | CoreException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @JsonCreator
    public CodenvyProjectDescriptor(@JsonProperty(value = "type", required = true) Type type,
                                    @JsonProperty(value = "builders", required = true) Map<String, String> builders) {
        this.type = checkNotNull(type);
        this.builders = new ImmutableMap.Builder<String, String>().putAll(checkNotNull(builders)).build();
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
