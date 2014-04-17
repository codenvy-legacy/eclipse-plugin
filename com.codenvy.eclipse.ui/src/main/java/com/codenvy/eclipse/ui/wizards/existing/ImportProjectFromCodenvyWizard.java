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
package com.codenvy.eclipse.ui.wizards.existing;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.codenvy.eclipse.ui.wizards.existing.pages.AuthenticationWizardPage;
import com.codenvy.eclipse.ui.wizards.existing.pages.ImportWizardSharedData;
import com.codenvy.eclipse.ui.wizards.existing.pages.ProjectWizardPage;
import com.codenvy.eclipse.ui.wizards.existing.pages.WorkspaceWizardPage;

/**
 * Wizard used to import Codenvy projects from the given Codenvy platform.
 * 
 * @author Kevin Pollet
 */
public class ImportProjectFromCodenvyWizard extends Wizard implements IImportWizard, INewWizard {
    private final AuthenticationWizardPage authenticationWizardPage;
    private final WorkspaceWizardPage      workspaceWizardPage;
    private final ProjectWizardPage        projectWizardPage;
    private final ImportWizardSharedData   importWizardSharedData;

    /**
     * Default constructor.
     */
    public ImportProjectFromCodenvyWizard() {
        this.importWizardSharedData = new ImportWizardSharedData();
        this.authenticationWizardPage = new AuthenticationWizardPage(importWizardSharedData);
        this.workspaceWizardPage = new WorkspaceWizardPage(importWizardSharedData);
        this.projectWizardPage = new ProjectWizardPage(importWizardSharedData);
        
        setNeedsProgressMonitor(true);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    @Override
    public void addPages() {
        addPage(authenticationWizardPage);
        addPage(workspaceWizardPage);
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
            wizardDialog.addPageChangingListener(workspaceWizardPage);
            wizardDialog.addPageChangedListener(workspaceWizardPage);
            wizardDialog.addPageChangingListener(projectWizardPage);
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
        // TODO do import stuff here!!!!
        return true;
    }

    public WorkspaceWizardPage getWorkspaceWizardPage() {
        return workspaceWizardPage;
    }
}
