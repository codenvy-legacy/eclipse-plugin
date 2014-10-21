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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Codenvy project meta data.
 *
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public final class CodenvyProjectMetadata {
    private static final String                                          PROJECT_METADATA_FILE_NAME = "projectMetadata.json";
    private static final ConcurrentMap<IProject, CodenvyProjectMetadata> PROJECT_METADATA_CACHE     = new ConcurrentHashMap<>();

    public static CodenvyProjectMetadata get(IProject project) {
        CodenvyProjectMetadata projectMetadata = PROJECT_METADATA_CACHE.get(project);
        if (projectMetadata == null) {
            final IFile projectMetadataFile = project.getFolder(CODENVY_FOLDER_NAME).getFile(PROJECT_METADATA_FILE_NAME);
            if (projectMetadataFile.exists()) {
                final ObjectMapper mapper = new ObjectMapper();
                try {

                    projectMetadata = mapper.readValue(projectMetadataFile.getContents(), CodenvyProjectMetadata.class);

                } catch (CoreException | IOException e) {
                    throw new RuntimeException(e);
                }

                final CodenvyProjectMetadata currentProviderMetaData = PROJECT_METADATA_CACHE.putIfAbsent(project, projectMetadata);
                if (currentProviderMetaData != null) {
                    projectMetadata = currentProviderMetaData;
                }
            }
        }

        return projectMetadata;
    }

    public static void create(IProject project, CodenvyProjectMetadata projectMetadata) {
        PROJECT_METADATA_CACHE.put(project, projectMetadata);

        final IFile projectMetadataFile = project.getFolder(CODENVY_FOLDER_NAME).getFile(PROJECT_METADATA_FILE_NAME);
        final ObjectMapper mapper = new ObjectMapper();
        try {

            final byte[] projectMetadataBytes = mapper.writeValueAsBytes(projectMetadata);
            if (!projectMetadataFile.exists()) {
                projectMetadataFile.create(new ByteArrayInputStream(projectMetadataBytes), true, new NullProgressMonitor());
            } else {
                projectMetadataFile.setContents(new ByteArrayInputStream(projectMetadataBytes), IResource.FORCE,
                                                new NullProgressMonitor());
            }

        } catch (JsonProcessingException | CoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(IProject project) {
        final CodenvyProjectMetadata projectMetadata = PROJECT_METADATA_CACHE.get(project);
        if (projectMetadata != null) {
            final IFile projectMetadataFile = project.getFolder(CODENVY_FOLDER_NAME).getFile(PROJECT_METADATA_FILE_NAME);
            try {

                projectMetadataFile.delete(true, new NullProgressMonitor());

            } catch (CoreException e) {
                throw new RuntimeException(e);

            } finally {
                PROJECT_METADATA_CACHE.remove(projectMetadata);
            }
        }
    }

    public final String url;
    public final String username;
    public final String projectName;
    public final String workspaceId;

    @JsonCreator
    public CodenvyProjectMetadata(@JsonProperty(value = "url", required = true) String url,
                                  @JsonProperty(value = "username", required = true) String username,
                                  @JsonProperty(value = "projectName", required = true) String projectName,
                                  @JsonProperty(value = "workspaceId", required = true) String workspaceId) {
        this.url = url;
        this.username = username;
        this.projectName = projectName;
        this.workspaceId = workspaceId;
    }
}
