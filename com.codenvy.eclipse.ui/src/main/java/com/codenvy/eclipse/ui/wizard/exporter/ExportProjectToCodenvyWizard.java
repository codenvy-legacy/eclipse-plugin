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

import static com.codenvy.eclipse.core.utils.EclipseProjectHelper.exportIProjectToZipStream;
import static com.google.common.base.Predicates.notNull;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.codenvy.client.Codenvy;
import com.codenvy.client.CodenvyAPI;
import com.codenvy.client.model.Project;
import com.codenvy.client.model.WorkspaceRef;
import com.codenvy.eclipse.core.CodenvyNature;
import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.team.CodenvyProvider;
import com.codenvy.eclipse.core.utils.EclipseProjectHelper;
import com.codenvy.eclipse.ui.wizard.common.CredentialsProviderWizard;
import com.codenvy.eclipse.ui.wizard.common.pages.AuthenticationWizardPage;
import com.codenvy.eclipse.ui.wizard.exporter.pages.ProjectWizardPage;
import com.codenvy.eclipse.ui.wizard.exporter.pages.WorkspaceWizardPage;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
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
    public boolean canFinish() {
        final IWizardPage currentWizardPage = getContainer().getCurrentPage();

        return currentWizardPage != null
               && currentWizardPage.getName().equals(workspaceWizardPage.getName())
               && currentWizardPage.isPageComplete();
    }

    @Override
    public boolean performFinish() {
        final String platformURL = getUrl();
        final String username = getUsername();
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final Set<IProject> projects = exportToCodenvyProjectsSelectionPage.getSelectedProjects();
        final WorkspaceRef workspaceRef = workspaceWizardPage.getSelectedWorkspace();

        try {
            workbench.getProgressService()
                     .run(true, true, new IRunnableWithProgress() {
                         @Override
                         public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                             monitor.beginTask("Exporting project", 1);

                             final Codenvy codenvy = CodenvyPlugin.getDefault()
                                                                  .getCodenvyBuilder(platformURL, username)
                                                                  .build();

                             final List<? extends Project> remoteWorkspaceProjects = codenvy.project()
                                                                                  .getWorkspaceProjects(workspaceRef.id())
                                                                                  .execute();

                             for (final IProject project : projects) {
                                 try {
                                     Project remoteProject =
                                                             Iterables.tryFind(remoteWorkspaceProjects, new Predicate<Project>() {
                                                                 @Override
                                                                 public boolean apply(Project input) {
                                                                     return input.name().equals(project.getName());
                                                                 }
                                                             }).orNull();

                                     if (remoteProject == null) {
                                         String codenvyProjectType = null;
                                         for (String natureId : project.getDescription().getNatureIds()) {
                                             codenvyProjectType = CodenvyNature.NATURE_MAPPINGS.inverse().get(project.getNature(natureId));
                                         }
                                         remoteProject =
                                                         CodenvyAPI.getClient().newProjectBuilder().withProjectTypeId(codenvyProjectType != null
                                                             ? codenvyProjectType : "unknown")
                                                                              .withName(project.getName())
                                                                              .withWorkspaceId(workspaceRef.id())
                                                                              .withWorkspaceName(workspaceRef.name())
                                                                              .build();

                                         codenvy.project()
                                                .create(remoteProject)
                                                .execute();
                                     }

                                     final InputStream archiveInputStream = exportIProjectToZipStream(project, monitor);
                                     codenvy.project()
                                            .importArchive(workspaceRef.id(), remoteProject, archiveInputStream)
                                            .execute();

                                     IFolder codenvyFolder = project.getFolder(new Path(".codenvy"));
                                     if (!codenvyFolder.exists()) {
                                         codenvyFolder.create(true, true, monitor);
                                     }
                                     EclipseProjectHelper.createOrUpdateResourcesFromZip(codenvy.project()
                                                                                                .exportResources(remoteProject,
                                                                                                                 ".codenvy")
                                                                                                .execute(),
                                                                                         codenvyFolder,
                                                                                         monitor);

                                     CodenvyMetaProject.create(project, new CodenvyMetaProject(platformURL, username, project.getName(),
                                                                                               workspaceRef.id()));
                                     RepositoryProvider.map(project, CodenvyProvider.PROVIDER_ID);

                                     final IProjectDescription newProjectDescription = project.getDescription();
                                     newProjectDescription.setNatureIds(ObjectArrays.concat(newProjectDescription.getNatureIds(),
                                                                                            CodenvyNature.NATURE_ID));
                                     project.setDescription(newProjectDescription, new NullProgressMonitor());
                                 } catch (CoreException e) {
                                     throw new RuntimeException(e);
                                 }

                                 monitor.worked(1);
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
