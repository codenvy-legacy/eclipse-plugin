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
package com.codenvy.eclipse.core.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.Test;

import com.google.common.io.ByteStreams;

/**
 * {@link EclipseProjectHelper} test.
 *
 * @author Stéphane Daviet
 */
public class EclipseProjectHelperTest {
    @Test(expected = NullPointerException.class)
    public void testExportIProjectToZipStreamWithNullProject() throws CoreException, IOException {
        EclipseProjectHelper.exportIProjectToZipStream(null, null);
    }

    @Test
    public void testExportIProjectToZipStream() throws CoreException, IOException {
        IProgressMonitor monitor = mock(IProgressMonitor.class);

        IFile subFile = mock(IFile.class);
        when(subFile.getProjectRelativePath()).thenReturn(new Path("/aFolder/aSubFile"));
        when(subFile.getContents()).thenReturn(new ByteArrayInputStream("Some content".getBytes()));
        when(subFile.getLocationURI()).thenReturn(File.createTempFile("aSubFile", "temp").toURI());

        IFolder rootFolder = mock(IFolder.class);
        when(rootFolder.getProjectRelativePath()).thenReturn(new Path("/aFolder"));
        when(rootFolder.members(anyInt())).thenReturn(new IResource[]{subFile});

        IFile rootFile = mock(IFile.class);
        when(rootFile.getProjectRelativePath()).thenReturn(new Path("/aRootFile"));
        when(rootFile.getContents()).thenReturn(new ByteArrayInputStream("Some other content".getBytes()));
        when(rootFile.getLocationURI()).thenReturn(File.createTempFile("aRootFile", "temp").toURI());

        IProject project = mock(IProject.class);
        when(project.members(anyInt())).thenReturn(new IResource[]{rootFolder, rootFile});
        when(project.getProjectRelativePath()).thenReturn(new Path(""));

        ZipInputStream zipInputStream = new ZipInputStream(EclipseProjectHelper.exportIProjectToZipStream(project, monitor));

        List<ZipEntry> zipEntrys = new ArrayList<>();
        ZipEntry zipEntry = null;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            zipEntrys.add(zipEntry);
            if ("/aFolder/aSubFile".equals(zipEntry.getName())) {
                ByteArrayOutputStream fileContent = new ByteArrayOutputStream();
                ByteStreams.copy(zipInputStream, fileContent);
                assertEquals("Some content", fileContent.toString());
            }
            if ("/aRootFile".equals(zipEntry.getName())) {
                ByteArrayOutputStream fileContent = new ByteArrayOutputStream();
                ByteStreams.copy(zipInputStream, fileContent);
                assertEquals("Some other content", fileContent.toString());
            }
        }
        assertEquals(4, zipEntrys.size());
        assertThat(zipEntrys, hasItem(HasPropertyWithValue.<ZipEntry> hasProperty("name", equalTo("/"))));
        assertThat(zipEntrys, hasItem(HasPropertyWithValue.<ZipEntry> hasProperty("name", equalTo("/aFolder/"))));
        assertThat(zipEntrys, hasItem(HasPropertyWithValue.<ZipEntry> hasProperty("name", equalTo("/aFolder/aSubFile"))));
        assertThat(zipEntrys, hasItem(HasPropertyWithValue.<ZipEntry> hasProperty("name", equalTo("/aRootFile"))));
    }
}
