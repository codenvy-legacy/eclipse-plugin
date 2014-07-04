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
package com.codenvy.eclipse.core.team;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.codenvy.eclipse.core.utils.EclipseProjectHelper;
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
public class CodenvyMetaProject {
    private static final String                                      CODENVY_FOLDER_NAME     = ".codenvy";
    private static final String                                      CODENVY_TEAM_FILE_NAME  = "team";
    private static final ConcurrentMap<IProject, CodenvyMetaProject> repositoryProviderCache = new ConcurrentHashMap<>();

    public static void create(IProject project, CodenvyMetaProject providerMetaData) {
        repositoryProviderCache.put(project, providerMetaData);

        final IFile providerMetaDataFile = project.getFolder(CODENVY_FOLDER_NAME).getFile(CODENVY_TEAM_FILE_NAME);
        final ObjectMapper mapper = new ObjectMapper();
        try {

            final byte[] providerMetaDataBytes = mapper.writeValueAsBytes(providerMetaData);
            if (!providerMetaDataFile.exists()) {
                providerMetaDataFile.create(new ByteArrayInputStream(providerMetaDataBytes), true, new NullProgressMonitor());
            } else {
                providerMetaDataFile.setContents(new ByteArrayInputStream(providerMetaDataBytes), IResource.FORCE,
                                                 new NullProgressMonitor());
            }

        } catch (JsonProcessingException | CoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static CodenvyMetaProject get(IProject project) {
        EclipseProjectHelper.checkCodenvyProjectLayout(project);

        CodenvyMetaProject providerMetaData = repositoryProviderCache.get(project);
        if (providerMetaData == null) {
            final IFile providerMetaDataFile = project.getFolder(CODENVY_FOLDER_NAME).getFile(CODENVY_TEAM_FILE_NAME);
            if (providerMetaDataFile.exists()) {
                final ObjectMapper mapper = new ObjectMapper();
                try {

                    providerMetaData = mapper.readValue(providerMetaDataFile.getContents(), CodenvyMetaProject.class);

                } catch (CoreException | IOException e) {
                    throw new RuntimeException(e);
                }

                final CodenvyMetaProject currentProviderMetaData = repositoryProviderCache.putIfAbsent(project, providerMetaData);
                if (currentProviderMetaData != null) {
                    providerMetaData = currentProviderMetaData;
                }
            }
        }

        return providerMetaData;
    }

    public static void delete(IProject project) {
        final CodenvyMetaProject currentProviderMetaData = repositoryProviderCache.get(project);
        if (currentProviderMetaData != null) {
            final IFile providerMetaDataFile = project.getFolder(CODENVY_FOLDER_NAME).getFile(CODENVY_TEAM_FILE_NAME);
            try {

                providerMetaDataFile.delete(true, new NullProgressMonitor());

            } catch (CoreException e) {
                throw new RuntimeException(e);
            }

            repositoryProviderCache.remove(currentProviderMetaData);
        }
    }

    public final String url;
    public final String username;
    public final String projectName;
    public final String workspaceId;

    @JsonCreator
    public CodenvyMetaProject(@JsonProperty("url") String url,
                              @JsonProperty("username") String username,
                              @JsonProperty("projectName") String projectName,
                              @JsonProperty("workspaceId") String workspaceId) {

        this.url = url;
        this.username = username;
        this.projectName = projectName;
        this.workspaceId = workspaceId;
    }
}
