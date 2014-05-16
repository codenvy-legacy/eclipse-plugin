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

import static com.google.common.collect.ObjectArrays.concat;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.ui.progress.IProgressConstants2;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.ProjectService;
import com.codenvy.eclipse.core.RestServiceFactory;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.utils.EclipseProjectHelper;
import com.codenvy.eclipse.ui.wizard.importer.pages.AuthenticationWizardPage;
import com.codenvy.eclipse.ui.wizard.importer.pages.ImportWizardSharedData;
import com.codenvy.eclipse.ui.wizard.importer.pages.ProjectWizardPage;

/**
 * Wizard used to import Codenvy projects from the given Codenvy platform.
 * 
 * @author Kevin Pollet
 */
public class ImportProjectFromCodenvyWizard extends Wizard implements IImportWizard, INewWizard {
    private final AuthenticationWizardPage authenticationWizardPage;
    private final ProjectWizardPage        projectWizardPage;
    private final ImportWizardSharedData   importWizardSharedData;

    /**
     * Default constructor.
     */
    public ImportProjectFromCodenvyWizard() {
        this.importWizardSharedData = new ImportWizardSharedData();
        this.authenticationWizardPage = new AuthenticationWizardPage(importWizardSharedData);
        this.projectWizardPage = new ProjectWizardPage(importWizardSharedData);

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
        final Job importProjectsJob = new Job("Import projects from Codenvy") {

            @Override
            public IStatus run(IProgressMonitor monitor) {
                final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
                final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);

                if (restServiceFactoryRef != null) {
                    final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);

                    if (restServiceFactory != null) {

                        try {

                            final List<CodenvyProject> projectsToImport = importWizardSharedData.getProjects();
                            monitor.beginTask("Importing projects", projectsToImport.size());

                            final String url = importWizardSharedData.getUrl().get();
                            final CodenvyToken token = importWizardSharedData.getCodenvyToken().get();
                            final IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
                            final List<IProject> importedProjects = new ArrayList<>();
                            final ProjectService projectService = restServiceFactory.newRestServiceWithAuth(ProjectService.class, url, token);

                            for (CodenvyProject oneProject : projectsToImport) {
                                final ZipInputStream zipInputStream = projectService.exportResources(oneProject, null);
                                final IProject newProject = EclipseProjectHelper.createIProjectFromZipStream(zipInputStream, new CodenvyMetaProject(url, oneProject.name, oneProject.workspaceId, token.value), monitor);
                                importedProjects.add(newProject);

                                monitor.worked(1);
                            }

                            if (importWizardSharedData.getWorkingSet().isPresent()) {
                                final IWorkingSet workingSet = importWizardSharedData.getWorkingSet().get();
                                final boolean workingSetExists = workingSetManager.getWorkingSet(workingSet.getName()) != null;
                                final IAdaptable[] workingSetElements = concat( workingSet.getElements(), workingSet.adaptElements(importedProjects.toArray(new IProject[importedProjects.size()])), IAdaptable.class);

                                workingSet.setElements(workingSetElements);
                                if (!workingSetExists) {
                                    workingSetManager.addWorkingSet(workingSet);
                                }
                            }

                        } finally {
                            context.ungetService(restServiceFactoryRef);
                            monitor.done();
                        }
                    }
                }

                return Status.OK_STATUS;
            }
        };

        importProjectsJob.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, true);
        importProjectsJob.setUser(true);
        importProjectsJob.schedule();

        return true;
    }

    public ProjectWizardPage getProjectWizardPage() {
        return projectWizardPage;
    }
}
