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
package com.codenvy.eclipse.core.team;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Codenvy provider meta data.
 * 
 * @author Kevin Pollet
 */
public class CodenvyProviderMetaData {
    private static final ConcurrentMap<IProject, CodenvyProviderMetaData> providerMetaDataCache = new ConcurrentHashMap<>();

    public static CodenvyProviderMetaData create(IProject project, CodenvyProviderMetaData providerMetaData) {
        final CodenvyProviderMetaData currentProviderMetaData = providerMetaDataCache.putIfAbsent(project, providerMetaData);
        if (currentProviderMetaData == null) {
            final IFile providerMetaDataFile = project.getFolder(".codenvy").getFile("team");
            if (!providerMetaDataFile.exists()) {
                final ObjectMapper mapper = new ObjectMapper();
                try {

                    final byte[] providerMetaDataBytes = mapper.writeValueAsBytes(providerMetaData);
                    providerMetaDataFile.create(new ByteArrayInputStream(providerMetaDataBytes), true, new NullProgressMonitor());

                } catch (JsonProcessingException | CoreException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return currentProviderMetaData;
    }

    public static CodenvyProviderMetaData get(IProject project) {
        CodenvyProviderMetaData providerMetaData = providerMetaDataCache.get(project);
        if (providerMetaData == null) {
            final IFile providerMetaDataFile = project.getFolder(".codenvy").getFile("team");
            if (providerMetaDataFile.exists()) {
                final ObjectMapper mapper = new ObjectMapper();
                try {

                    providerMetaData = mapper.readValue(providerMetaDataFile.getContents(), CodenvyProviderMetaData.class);

                } catch (CoreException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            final CodenvyProviderMetaData currentProviderMetaData = providerMetaDataCache.putIfAbsent(project, providerMetaData);
            if (currentProviderMetaData != null) {
                providerMetaData = currentProviderMetaData;
            }
        }

        return providerMetaData;
    }

    public static void delete(IProject project) {
        final CodenvyProviderMetaData currentProviderMetaData = providerMetaDataCache.get(project);
        if (currentProviderMetaData != null) {
            final IFile providerMetaDataFile = project.getFolder(".codenvy").getFile("team");
            try {
                
                providerMetaDataFile.delete(true, new NullProgressMonitor());
                
            } catch (CoreException e) {
                throw new RuntimeException(e);
            }
            
            providerMetaDataCache.remove(currentProviderMetaData);
        }
    }

    public final String url;
    public final String projectName;
    public final String workspaceId;
    public final String codenvyToken;

    @JsonCreator
    public CodenvyProviderMetaData(@JsonProperty("url") String url,
                                   @JsonProperty("projectName") String projectName,
                                   @JsonProperty("workspaceId") String workspaceId,
                                   @JsonProperty("codenvyToken") String codenvyToken) {

        this.url = url;
        this.projectName = projectName;
        this.workspaceId = workspaceId;
        this.codenvyToken = codenvyToken;
    }
}
