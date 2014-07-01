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
package com.codenvy.eclipse.ui.wizard.exporter.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.codenvy.eclipse.client.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.ui.CodenvyUIPlugin;
import com.codenvy.eclipse.ui.Images;
import com.codenvy.eclipse.ui.wizard.common.CredentialsProviderWizard;
import com.codenvy.eclipse.ui.wizard.common.jobs.LoadWorkspacesJob;
import com.codenvy.eclipse.ui.wizard.importer.pages.ProjectWizardPage;

/**
 * @author Stéphane Daviet
 */
public class WorkspaceWizardPage extends WizardPage implements IPageChangedListener {
    private ListViewer workspaceListViewer;

    public WorkspaceWizardPage() {
        super(ProjectWizardPage.class.getSimpleName());

        setTitle("Codenvy Workspaces");
        setDescription("Select Codenvy workspace where project should be imported.");
        setImageDescriptor(CodenvyUIPlugin.getDefault().getImageRegistry().getDescriptor(Images.WIZARD_LOGO));
        setPageComplete(false);
    }

    @Override
    public void createControl(Composite parent) {
        final Composite wizardContainer = new Composite(parent, SWT.NONE);
        wizardContainer.setLayout(new GridLayout(2, false));
        wizardContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Label workspaceTableLabel = new Label(wizardContainer, SWT.NONE);
        workspaceTableLabel.setText("Remote Codenvy Workspaces:");
        workspaceTableLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        workspaceListViewer = new ListViewer(wizardContainer, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        workspaceListViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return element instanceof WorkspaceRef ? ((WorkspaceRef)element).name : super.getText(element);
            }
        });
        workspaceListViewer.setContentProvider(ArrayContentProvider.getInstance());
        workspaceListViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        workspaceListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setPageComplete(!workspaceListViewer.getSelection().isEmpty());
            }
        });

        setControl(wizardContainer);
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        if (isCurrentPage()) {
            workspaceListViewer.setInput(null);
            loadWorkspaces();
            setPageComplete(!workspaceListViewer.getSelection().isEmpty());
        }
    }

    public WorkspaceRef getSelectedWorkspace() {
        return (WorkspaceRef)((IStructuredSelection)workspaceListViewer.getSelection()).getFirstElement();
    }

    /**
     * Method used to load the workspaces asynchronously when the wizard page is displayed.
     */
    private void loadWorkspaces() {
        try {

            getContainer().run(true, false, new LoadWorkspacesJob(getWizard()) {
                @Override
                public void postLoadCallback(List<WorkspaceRef> workspaceRefs) {
                    workspaceListViewer.setInput(workspaceRefs.toArray());
                    if (!workspaceRefs.isEmpty()) {
                        workspaceListViewer.setSelection(new StructuredSelection(workspaceRefs.get(0)));
                    }
                    workspaceListViewer.refresh();
                }
            });

        } catch (InvocationTargetException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CredentialsProviderWizard getWizard() {
        return (CredentialsProviderWizard)super.getWizard();
    }
}