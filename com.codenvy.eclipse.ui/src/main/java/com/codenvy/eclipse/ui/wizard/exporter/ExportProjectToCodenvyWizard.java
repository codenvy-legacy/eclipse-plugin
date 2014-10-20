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
package com.codenvy.eclipse.ui.wizard.exporter;

import static com.codenvy.eclipse.core.utils.EclipseProjectHelper.createOrUpdateResourcesFromZip;
import static com.codenvy.eclipse.core.utils.EclipseProjectHelper.exportIProjectToZipStream;
import static com.google.common.base.Predicates.notNull;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.codenvy.client.Codenvy;
import com.codenvy.client.CodenvyAPI;
import com.codenvy.client.model.ProjectReference;
import com.codenvy.client.model.WorkspaceReference;
import com.codenvy.eclipse.core.CodenvyNature;
import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.core.CodenvyProjectDescriptor;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.team.CodenvyProvider;
import com.codenvy.eclipse.ui.team.CodenvyLightweightLabelDecorator;
import com.codenvy.eclipse.ui.wizard.common.CredentialsProviderWizard;
import com.codenvy.eclipse.ui.wizard.common.pages.AuthenticationWizardPage;
import com.codenvy.eclipse.ui.wizard.exporter.pages.ProjectWizardPage;
import com.codenvy.eclipse.ui.wizard.exporter.pages.WorkspaceWizardPage;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ObjectArrays;

/**
 * Export project to Codenvy wizard.
 *
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class ExportProjectToCodenvyWizard extends Wizard implements IExportWizard, CredentialsProviderWizard {
    private ProjectWizardPage              exportToCodenvyProjectsSelectionPage;
    private final AuthenticationWizardPage authenticationWizardPage;
    private final WorkspaceWizardPage      workspaceWizardPage;

    public ExportProjectToCodenvyWizard() {
        this.authenticationWizardPage = new AuthenticationWizardPage();
        this.workspaceWizardPage = new WorkspaceWizardPage();

        setWindowTitle("Export Projects to Codenvy");
        setNeedsProgressMonitor(true);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        @SuppressWarnings("unchecked")
        final Set<IProject> selectedProjects = FluentIterable.from((List<Object>)selection.toList())
                                                             .transform(new Function<Object, IProject>() {
                                                                 @Override
                                                                 public IProject apply(Object input) {
                                                                     return (IProject)(input instanceof IAdaptable
                                                                         ? ((IAdaptable)input).getAdapter(IProject.class) : null);
                                                                 }

                                                             })
                                                             .filter(notNull())
                                                             .copyInto(new HashSet<IProject>());

        this.exportToCodenvyProjectsSelectionPage = new ProjectWizardPage(selectedProjects);
    }

    @Override
    public void addPages() {
        addPage(exportToCodenvyProjectsSelectionPage);
        addPage(authenticationWizardPage);
        addPage(workspaceWizardPage);
    }

    @Override
    public void createPageControls(Composite pageContainer) {
        exportToCodenvyProjectsSelectionPage.createControl(pageContainer);
        authenticationWizardPage.createControl(pageContainer);
        // workspace page is created lazily
    }

    @Override
    public void setContainer(IWizardContainer wizardContainer) {
        super.setContainer(wizardContainer);

        if (wizardContainer != null) {
            final WizardDialog wizardDialog = (WizardDialog)wizardContainer;
            wizardDialog.addPageChangingListener(authenticationWizardPage);
            wizardDialog.addPageChangedListener(workspaceWizardPage);
        }
    }

    @Override
    public boolean performFinish() {
        final String platformURL = getUrl();
        final String username = getUsername();
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final Set<IProject> projects = exportToCodenvyProjectsSelectionPage.getSelectedProjects();
        final WorkspaceReference workspaceReference = workspaceWizardPage.getSelectedWorkspace();

        try {
            workbench.getProgressService()
                     .run(true, true, new IRunnableWithProgress() {
                         @Override
                         public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                             monitor.beginTask("Exporting projects", projects.size());

                             try {

                                 final Codenvy codenvy = CodenvyPlugin.getDefault()
                                                                      .getCodenvyBuilder(platformURL, username)
                                                                      .build();

                                 final List<ProjectReference> remoteWorkspaceProjects =
                                                                                        codenvy.project()
                                                                                               .getWorkspaceProjects(workspaceReference.id())
                                                                                               .execute();

                                 for (final IProject oneProject : projects) {

                                     if (!remoteWorkspaceProjects.contains(projects)) {

                                         CodenvyProjectDescriptor.Type codenvyProjectType = null;
                                         for (String natureId : oneProject.getDescription().getNatureIds()) {
                                             codenvyProjectType = CodenvyNature.ECLIPSE_NATURE_MAPPINGS.inverse().get(natureId);
                                             if (codenvyProjectType != null) {
                                                 break;
                                             }
                                         }

                                         final ProjectReference projectToExport =
                                                                                  CodenvyAPI.getClient()
                                                                                            .newProjectBuilder()
                                                                                            .withType(codenvyProjectType.name()
                                                                                                                        .toLowerCase())
                                                                                            .withName(oneProject.getName())
                                                                                            .withWorkspaceId(workspaceReference.id())
                                                                                            .withWorkspaceName(workspaceReference.name())
                                                                                            .build();

                                         codenvy.project()
                                                .create(projectToExport)
                                                .execute();

                                         final InputStream archiveInputStream = exportIProjectToZipStream(oneProject, monitor);
                                         codenvy.project()
                                                .importArchive(workspaceReference.id(), projectToExport, archiveInputStream)
                                                .execute();

                                         final IFolder codenvyFolder = oneProject.getFolder(new Path(".codenvy"));
                                         if (!codenvyFolder.exists()) {
                                             codenvyFolder.create(true, true, monitor);
                                         }

                                         final ZipInputStream codenvyFolderZip = codenvy.project()
                                                                                        .exportResources(projectToExport, ".codenvy")
                                                                                        .execute();

                                         createOrUpdateResourcesFromZip(codenvyFolderZip, codenvyFolder, monitor);

                                         CodenvyMetaProject.create(oneProject,
                                                                   new CodenvyMetaProject(platformURL, username, oneProject.getName(),
                                                                                          workspaceReference.id()));
                                         RepositoryProvider.map(oneProject, CodenvyProvider.PROVIDER_ID);

                                         final IProjectDescription newProjectDescription = oneProject.getDescription();
                                         newProjectDescription.setNatureIds(ObjectArrays.concat(newProjectDescription.getNatureIds(),
                                                                                                CodenvyNature.NATURE_ID));
                                         oneProject.setDescription(newProjectDescription, monitor);

                                         // force Codenvy provider label decoration refresh
                                         workbench.getDisplay().syncExec(new Runnable() {
                                             @Override
                                             public void run() {
                                                 workbench.getDecoratorManager()
                                                          .update(CodenvyLightweightLabelDecorator.DECORATOR_ID);
                                             }
                                         });
                                     }

                                     monitor.worked(1);
                                 }

                             } catch (CoreException e) {
                                 throw new RuntimeException(e);
                             } finally {
                                 monitor.done();
                             }
                         }
                     });

        } catch (InvocationTargetException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public String getUrl() {
        return authenticationWizardPage.getURL();
    }

    @Override
    public String getUsername() {
        return authenticationWizardPage.getUsername();
    }

    @Override
    public String getPassword() {
        return authenticationWizardPage.getPassword();
    }

    @Override
    public boolean isStoreUserCredentials() {
        return authenticationWizardPage.isStoreUserCredentials();
    }
}
