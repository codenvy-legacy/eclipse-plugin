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

import static com.codenvy.eclipse.ui.Images.WIZARD_LOGO;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.eclipse.jface.viewers.CheckboxTableViewer.newCheckList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.codenvy.eclipse.ui.CodenvyUIPlugin;

/**
 * @author Stéphane Daviet
 */
public class ProjectWizardPage extends WizardPage {
    private CheckboxTableViewer projectsTableViewer;
    private Set<IProject>       selectedProjects;

    public ProjectWizardPage(Set<IProject> selectedProjects) {
        super(ProjectWizardPage.class.getSimpleName());

        this.selectedProjects = checkNotNull(selectedProjects);

        setTitle("Select workspaces project");
        setDescription("Select local projects that you want to push to a remote Codenvy repository");
        setImageDescriptor(CodenvyUIPlugin.getDefault().getImageRegistry().getDescriptor(WIZARD_LOGO));
        setPageComplete(selectedProjects.size() > 0);
    }

    @Override
    public void createControl(Composite parent) {
        final Composite wizardContainer = new Composite(parent, SWT.NONE);
        wizardContainer.setLayout(new GridLayout(2, false));

        final Label projectsTableLabel = new Label(wizardContainer, SWT.NONE);
        projectsTableLabel.setText("Projects:");
        projectsTableLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        projectsTableViewer = newCheckList(wizardContainer, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        projectsTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        final TableViewerColumn projectNameColumn = new TableViewerColumn(projectsTableViewer, SWT.NONE);
        projectNameColumn.getColumn().setWidth(450);
        projectNameColumn.getColumn().setText("Name");
        projectNameColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return element instanceof IProject ? ((IProject)element).getName() : super.getText(element);
            }
        });

        projectsTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        projectsTableViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getChecked()) {
                    selectedProjects.add((IProject)event.getElement());
                } else {
                    selectedProjects.remove(event.getElement());
                }
                setPageComplete(selectedProjects.size() > 0);
            }
        });

        projectsTableViewer.setInput(ResourcesPlugin.getWorkspace().getRoot().getProjects());
        projectsTableViewer.setCheckedElements(selectedProjects.toArray());

        final Composite projectTableButtonsContainer = new Composite(wizardContainer, SWT.NONE);
        projectTableButtonsContainer.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, true, true));
        projectTableButtonsContainer.setLayout(new GridLayout());

        final Button selectAll = new Button(projectTableButtonsContainer, SWT.NONE);
        selectAll.setText("Select All");
        selectAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        selectAll.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent e) {
                projectsTableViewer.setAllChecked(true);
                selectedProjects.addAll((Collection< ? extends IProject>)Arrays.asList(projectsTableViewer.getCheckedElements()));
                setPageComplete(true);
            }
        });

        final Button deselectAll = new Button(projectTableButtonsContainer, SWT.NONE);
        deselectAll.setText("Deselect All");
        deselectAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        deselectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                projectsTableViewer.setAllChecked(false);
                selectedProjects.clear();
                setPageComplete(false);
            }
        });

        setControl(wizardContainer);
    }

    /**
     * Returns the selected projects.
     * 
     * @return the selected projects {@link Set}.
     */
    public Set<IProject> getSelectedProjects() {
        return selectedProjects;
    }
}
