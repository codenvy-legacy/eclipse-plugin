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
import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;

import com.codenvy.eclipse.core.CodenvyNature.CodenvyProjectDescriptor.ProjectType;
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
    public static final String        NATURE_ID            = "com.codenvy.eclipse.core.codenvyNature";

    private static final String       MAVEN_NATURE_ID      = "org.eclipse.m2e.core.maven2Nature";
    private static final String       SPRING_NATURE_ID     = "org.springframework.ide.eclipse.core.springnature";
    private static final String       JAVASCRIPT_NATURE_ID = "org.eclipse.wst.jsdt.core.jsNature";

    private static final String       BUILDER_NAME_KEY     = "builder.name";
    private static final String       FRAMEWORK_KEY        = "framework";

    private static final String       MAVEN_BUILDER_NAME   = "maven";

    private IProject                  codenvyProject;
    private Map<String, List<String>> natureMappings;
    private Map<String, List<String>> builderMappings;

    public CodenvyNature() {
        natureMappings = new HashMap<>();
        natureMappings.put("spring", newArrayList(JavaCore.NATURE_ID, SPRING_NATURE_ID));
        natureMappings.put("javastandaloneapp", newArrayList(JavaCore.NATURE_ID));
        natureMappings.put("javawebapplication", newArrayList(JavaCore.NATURE_ID));
        natureMappings.put("angularjs", newArrayList(JAVASCRIPT_NATURE_ID));

        builderMappings = new HashMap<>();
        builderMappings.put("spring", newArrayList(JavaCore.BUILDER_ID));
        builderMappings.put("javastandaloneapp", newArrayList(JavaCore.BUILDER_ID));
        builderMappings.put("javawebapplication", newArrayList(JavaCore.BUILDER_ID));
    }

    @Override
    public void configure() throws CoreException {
        final IFolder codenvyDesciptorFolder = codenvyProject.getFolder(".codenvy");
        final IFile codenvyDesciptorFile = codenvyDesciptorFolder.getFile("project");

        if (codenvyDesciptorFile.exists()) {

            final Job job = new Job("Configure project") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    monitor.beginTask("Configure project natures and builders", IProgressMonitor.UNKNOWN);

                    try (InputStream inputStream = codenvyDesciptorFile.getContents()) {
                        final CodenvyProjectDescriptor descriptor;
                        final IProjectDescription codenvyProjectDescription = codenvyProject.getDescription();

                        final ObjectMapper mapper = new ObjectMapper();
                        descriptor = mapper.readValue(codenvyDesciptorFile.getContents(), CodenvyProjectDescriptor.class);

                        final List<String> naturesToAdd = natureMappings.get(getMappingKey(descriptor));
                        if (naturesToAdd != null) {
                            final List<String> natures = new ArrayList<>(asList(codenvyProjectDescription.getNatureIds()));
                            for (String oneNature : naturesToAdd) {
                                if (isNatureWithId(oneNature)) {
                                    natures.add(oneNature);
                                }
                            }

                            codenvyProjectDescription.setNatureIds(natures.toArray(new String[0]));
                        }

                        final List<String> buildersToAdd = builderMappings.get(getMappingKey(descriptor));
                        if (buildersToAdd != null) {
                            final List<ICommand> builders = new ArrayList<>();
                            for (String oneBuilder : buildersToAdd) {
                                final ICommand command = codenvyProjectDescription.newCommand();
                                command.setBuilderName(oneBuilder);
                                builders.add(command);
                            }

                            codenvyProjectDescription.setBuildSpec(builders.toArray(new ICommand[0]));
                        }

                        // save nature and builders added to the project
                        codenvyProject.setDescription(codenvyProjectDescription, monitor);

                        final String builderName = descriptor.properties.get(BUILDER_NAME_KEY);
                        if (MAVEN_BUILDER_NAME.equals(builderName)) {
                            codenvyProjectDescription.setNatureIds(ObjectArrays.concat(codenvyProjectDescription.getNatureIds(),
                                                                                       MAVEN_NATURE_ID));
                            codenvyProject.setDescription(codenvyProjectDescription, monitor);
                            MavenPlugin.getProjectConfigurationManager().updateProjectConfiguration(codenvyProject, monitor);
                        }

                    } catch (CoreException | IOException e) {
                        throw new RuntimeException(e);

                    } finally {
                        monitor.done();
                    }

                    return Status.OK_STATUS;
                }
            };

            job.schedule();
        }

    }

    @Override
    public void deconfigure() throws CoreException {
        // Nothing to do
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
     * Checks if the given nature exists in the workspace.
     * 
     * @param natureId the id of the nature.
     * @return {@code true} if the given nature exists, {@code false} otherwise.
     */
    private boolean isNatureWithId(String natureId) {
        final IProjectNatureDescriptor projectNatureDescriptor = ResourcesPlugin.getWorkspace().getNatureDescriptor(natureId);
        return projectNatureDescriptor != null;
    }

    /**
     * Returns the mapping key in function of the {@link ProjectType}.
     * 
     * @param descriptor the project descriptor.
     * @return the mapping key or {@code null} if none.
     */
    private String getMappingKey(CodenvyProjectDescriptor descriptor) {
        switch (descriptor.type) {
            case ANGULARJS:
                return descriptor.type.name().toLowerCase();
            case MAVEN:
                return descriptor.properties.get(FRAMEWORK_KEY).toLowerCase();
            default:
                return null;
        }
    }

    /**
     * The Codenvy project descriptor.
     * 
     * @author Kevin Pollet
     */
    public static class CodenvyProjectDescriptor {
        public final ProjectType         type;
        public final Map<String, String> properties;

        public CodenvyProjectDescriptor(@JsonProperty("type") ProjectType type, @JsonProperty("properties") List<Property> properties) {
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
                this.value = value.isEmpty() ? null : value.get(0);  
            }
        }

        enum ProjectType {
            ANGULARJS,
            MAVEN,
            UNKNOWN;

            @JsonCreator
            static ProjectType forValue(String value) {
                ProjectType projectType;
                
                try {
                
                    projectType = Enum.valueOf(ProjectType.class, value);
                    
                } catch (NullPointerException | IllegalArgumentException e) {
                    projectType = UNKNOWN;
                }
                
                return projectType;
            }
        }
    }
}
