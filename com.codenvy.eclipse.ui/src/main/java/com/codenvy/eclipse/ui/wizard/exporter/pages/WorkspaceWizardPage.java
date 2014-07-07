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
package com.codenvy.eclipse.ui.wizard.exporter.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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

/**
 * @author Stéphane Daviet
 */
public class WorkspaceWizardPage extends WizardPage implements IPageChangedListener {
    private TableViewer workspaceTableViewer;

    public WorkspaceWizardPage() {
        super(WorkspaceWizardPage.class.getSimpleName());

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

        workspaceTableViewer = new TableViewer(wizardContainer, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        workspaceTableViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return element instanceof WorkspaceRef ? ((WorkspaceRef)element).name : super.getText(element);
            }
        });
        workspaceTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        workspaceTableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        workspaceTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setPageComplete(!workspaceTableViewer.getSelection().isEmpty());
            }
        });

        setControl(wizardContainer);
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        if (isCurrentPage()) {
            workspaceTableViewer.setInput(null);
            loadWorkspaces();
            setPageComplete(!workspaceTableViewer.getSelection().isEmpty());
        }
    }

    public WorkspaceRef getSelectedWorkspace() {
        return (WorkspaceRef)((IStructuredSelection)workspaceTableViewer.getSelection()).getFirstElement();
    }

    /**
     * Method used to load the workspaces asynchronously when the wizard page is displayed.
     */
    private void loadWorkspaces() {
        try {

            getContainer().run(true, false, new LoadWorkspacesJob(getWizard()) {
                @Override
                public void postLoadCallback(List<WorkspaceRef> workspaceRefs) {
                    workspaceTableViewer.setInput(workspaceRefs.toArray());
                    if (!workspaceRefs.isEmpty()) {
                        workspaceTableViewer.setSelection(new StructuredSelection(workspaceRefs.get(0)));
                    }
                    workspaceTableViewer.refresh();
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
