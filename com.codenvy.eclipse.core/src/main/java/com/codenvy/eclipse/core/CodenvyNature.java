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
package com.codenvy.eclipse.core;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ObjectArrays;

/**
 * The Codenvy project nature.
 * 
 * @author Kevin Pollet
 */
public class CodenvyNature implements IProjectNature {
    public static final String        NATURE_ID          = "com.codenvy.eclipse.core.codenvyNature";
    private static final String       BUILDER_NAME_KEY   = "builder.name";
    private static final String       MAVEN_BUILDER_NAME = "maven";

    private IProject                  codenvyProject;
    private Map<String, List<String>> natureMappings;

    public CodenvyNature() {
        natureMappings = new HashMap<>();
        natureMappings.put("spring", newArrayList(JavaCore.NATURE_ID));
        natureMappings.put("jar", newArrayList(JavaCore.NATURE_ID));
        natureMappings.put("war", newArrayList(JavaCore.NATURE_ID));
    }

    @Override
    public void configure() throws CoreException {
        final CodenvyProjectDescriptor codenvyProjectDescriptor;
        final IFolder codenvyDesciptorFolder = codenvyProject.getFolder(".codenvy");
        final IFile codenvyDesciptorFile = codenvyDesciptorFolder.getFile("project");
        final IProjectDescription codenvyProjectDescription = codenvyProject.getDescription();

        if (codenvyDesciptorFile.exists()) {
            try (InputStream inputStream = codenvyDesciptorFile.getContents()) {

                final ObjectMapper mapper = new ObjectMapper();
                codenvyProjectDescriptor = mapper.readValue(codenvyDesciptorFile.getContents(), CodenvyProjectDescriptor.class);

                final List<String> naturesToAdd = natureMappings.get(codenvyProjectDescriptor.type);
                if (naturesToAdd != null) {
                    codenvyProjectDescription.setNatureIds(ObjectArrays.concat(codenvyProjectDescription.getNatureIds(), naturesToAdd.toArray(new String[0]), String.class));
                    codenvyProject.setDescription(codenvyProjectDescription, new NullProgressMonitor());
                }

                final String builderName = codenvyProjectDescriptor.properties.get(BUILDER_NAME_KEY);
                if (MAVEN_BUILDER_NAME.equals(builderName)) {
                    codenvyProjectDescription.setNatureIds(ObjectArrays.concat(codenvyProjectDescription.getNatureIds(), "org.eclipse.m2e.core.maven2Nature"));
                    codenvyProject.setDescription(codenvyProjectDescription, new NullProgressMonitor());
                    MavenPlugin.getProjectConfigurationManager().updateProjectConfiguration(codenvyProject, new NullProgressMonitor());
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deconfigure() throws CoreException {

    }

    @Override
    public IProject getProject() {
        return codenvyProject;
    }

    @Override
    public void setProject(IProject codenvyProject) {
        this.codenvyProject = codenvyProject;
    }

    /**
     * The Codenvy project descriptor.
     * 
     * @author Kevin Pollet
     */
    public static class CodenvyProjectDescriptor {
        public final String              type;
        public final Map<String, String> properties;

        public CodenvyProjectDescriptor(@JsonProperty("type") String type, @JsonProperty("properties") List<Property> properties) {
            this.type = type;
            this.properties = new HashMap<>();

            if (properties != null) {
                for (Property oneProperty : properties) {
                    this.properties.put(oneProperty.name, oneProperty.value);
                }
            }
        }

        public static class Property {
            public final String name;
            public final String value;

            @JsonCreator
            public Property(@JsonProperty("name") String name, @JsonProperty("value") List<String> value) {
                this.name = name;

                if (!value.isEmpty()) {
                    this.value = value.get(0);
                } else {
                    this.value = null;
                }
            }
        }
    }
}
