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
package com.codenvy.eclipse.ui.wizard.importer;

import static com.codenvy.eclipse.core.utils.EclipseProjectHelper.createIProjectFromZipStream;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

import com.codenvy.eclipse.core.client.Codenvy;
import com.codenvy.eclipse.core.client.model.Project;
import com.codenvy.eclipse.core.client.store.secure.SecureStorageDataStoreFactory;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.ui.wizard.importer.pages.AuthenticationWizardPage;
import com.codenvy.eclipse.ui.wizard.importer.pages.ProjectWizardPage;

/**
 * Wizard used to import Codenvy projects from the given Codenvy platform.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class ImportProjectFromCodenvyWizard extends Wizard implements IImportWizard, INewWizard {
    private final AuthenticationWizardPage authenticationWizardPage;
    private final ProjectWizardPage        projectWizardPage;

    /**
     * Default constructor.
     */
    public ImportProjectFromCodenvyWizard() {
        this.authenticationWizardPage = new AuthenticationWizardPage();
        this.projectWizardPage = new ProjectWizardPage();

        setNeedsProgressMonitor(true);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    @Override
    public void addPages() {
        addPage(authenticationWizardPage);
        addPage(projectWizardPage);
    }

    @Override
    public void createPageControls(Composite pageContainer) {
        authenticationWizardPage.createControl(pageContainer);
        // workspace and project pages are created lazily
    }

    @Override
    public void setContainer(IWizardContainer wizardContainer) {
        super.setContainer(wizardContainer);

        if (wizardContainer != null) {
            final WizardDialog wizardDialog = (WizardDialog)wizardContainer;
            wizardDialog.addPageChangingListener(authenticationWizardPage);
            wizardDialog.addPageChangedListener(projectWizardPage);
        }
    }

    @Override
    public boolean canFinish() {
        final IWizardPage currentWizardPage = getContainer().getCurrentPage();

        return currentWizardPage != null
               && currentWizardPage.getName().equals(projectWizardPage.getName())
               && currentWizardPage.isPageComplete();
    }

    @Override
    public boolean performFinish() {
        final String platformURL = authenticationWizardPage.getURL();
        final String username = authenticationWizardPage.getUsername();
        final List<IWorkingSet> workingSets = projectWizardPage.getWorkingSets();
        final List<Project> projects = projectWizardPage.getProjects();
        final IWorkbench workbench = PlatformUI.getWorkbench();

        try {

            workbench.getProgressService()
                     .run(true, true, new IRunnableWithProgress() {
                         @Override
                         public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                             monitor.beginTask("Importing projects", projects.size());

                             final Codenvy codenvy =
                                                     new Codenvy.Builder(platformURL, username, SecureStorageDataStoreFactory.INSTANCE).build();

                             final List<IProject> importedProjects = new ArrayList<>();
                             for (Project oneProject : projects) {
                                 final ZipInputStream zipInputStream = codenvy.project()
                                                                              .exportResources(oneProject, null)
                                                                              .execute();

                                 final IProject newProject = createIProjectFromZipStream(zipInputStream,
                                                                                         new CodenvyMetaProject(
                                                                                                                platformURL,
                                                                                                                username,
                                                                                                                oneProject.name,
                                                                                                                oneProject.workspaceId),
                                                                                         monitor);
                                 importedProjects.add(newProject);
                                 monitor.worked(1);
                             }

                             final IWorkingSetManager workingSetManager = workbench.getWorkingSetManager();
                             for (IAdaptable importedProject : importedProjects) {
                                 workingSetManager.addToWorkingSets(importedProject,
                                                                    workingSets.toArray(new IWorkingSet[workingSets.size()]));
                             }
                         }
                     });

        } catch (InvocationTargetException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public AuthenticationWizardPage getAuthenticationWizardPage() {
        return authenticationWizardPage;
    }

    public ProjectWizardPage getProjectWizardPage() {
        return projectWizardPage;
    }
}
