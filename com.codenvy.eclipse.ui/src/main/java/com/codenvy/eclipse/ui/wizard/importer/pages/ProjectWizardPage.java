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
package com.codenvy.eclipse.ui.wizard.importer.pages;

import static org.eclipse.core.runtime.IProgressMonitor.UNKNOWN;
import static org.eclipse.jface.viewers.CheckboxTableViewer.newCheckList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.WorkingSetGroup;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.WorkingSetDescriptor;

import com.codenvy.client.Codenvy;
import com.codenvy.client.auth.Credentials;
import com.codenvy.client.model.Project;
import com.codenvy.client.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.ui.CodenvyUIPlugin;
import com.codenvy.eclipse.ui.Images;
import com.codenvy.eclipse.ui.wizard.common.CredentialsProviderWizard;
import com.codenvy.eclipse.ui.wizard.common.jobs.LoadWorkspacesJob;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * Project wizard page. In this wizard page the user selects the projects to import in Eclipse.
 *
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
@SuppressWarnings("restriction")
public final class ProjectWizardPage extends WizardPage implements IPageChangedListener {
    private ComboViewer         workspaceComboViewer;
    private CheckboxTableViewer projectTableViewer;
    private WorkingSetGroup     workingSetGroup;

    /**
     * Constructs an instance of {@link ProjectWizardPage}.
     */
    public ProjectWizardPage() {
        super(ProjectWizardPage.class.getSimpleName());

        setTitle("Codenvy Projects");
        setDescription("Select Codenvy projects to import");
        setImageDescriptor(CodenvyUIPlugin.getDefault().getImageRegistry().getDescriptor(Images.WIZARD_LOGO));
        setPageComplete(false);
    }

    @Override
    public void createControl(Composite parent) {
        final Composite wizardContainer = new Composite(parent, SWT.NONE);
        wizardContainer.setLayout(new GridLayout(2, false));
        wizardContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite workspaceSelectionContainer = new Composite(wizardContainer, SWT.NONE);
        workspaceSelectionContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        workspaceSelectionContainer.setLayout(new GridLayout(2, false));

        final Label workspaceLabel = new Label(workspaceSelectionContainer, SWT.NONE);
        workspaceLabel.setText("Workspace:");

        workspaceComboViewer = new ComboViewer(workspaceSelectionContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
        workspaceComboViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        workspaceComboViewer.setContentProvider(new ArrayContentProvider());
        workspaceComboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return element instanceof WorkspaceRef ? ((WorkspaceRef)element).name : super.getText(element);
            }
        });
        workspaceComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                final IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                if (!selection.isEmpty()) {
                    loadWorkspaceProjects((WorkspaceRef)selection.getFirstElement());
                }
            }
        });

        final Label projectTableLabel = new Label(wizardContainer, SWT.NONE);
        projectTableLabel.setText("Projects:");
        projectTableLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        projectTableViewer = newCheckList(wizardContainer, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

        final TableViewerColumn projectNameColumn = new TableViewerColumn(projectTableViewer, SWT.NONE);
        projectNameColumn.getColumn().setWidth(150);
        projectNameColumn.getColumn().setText("Name");
        projectNameColumn.setLabelProvider(new ColumnLabelProviderWithGreyElement() {
            @Override
            public String getText(Object element) {
                return element instanceof Project ? ((Project)element).name : super.getText(element);
            }
        });

        final TableViewerColumn projectTypeNameColumn = new TableViewerColumn(projectTableViewer, SWT.NONE);
        projectTypeNameColumn.getColumn().setWidth(150);
        projectTypeNameColumn.getColumn().setText("Type");
        projectTypeNameColumn.setLabelProvider(new ColumnLabelProviderWithGreyElement() {
            @Override
            public String getText(Object element) {
                return element instanceof Project ? ((Project)element).projectTypeName : super.getText(element);
            }
        });

        final TableViewerColumn projectDescriptionColumn = new TableViewerColumn(projectTableViewer, SWT.NONE);
        projectDescriptionColumn.getColumn().setWidth(150);
        projectDescriptionColumn.getColumn().setText("Description");
        projectDescriptionColumn.setLabelProvider(new ColumnLabelProviderWithGreyElement() {
            @Override
            public String getText(Object element) {
                return element instanceof Project ? ((Project)element).description : super.getText(element);
            }
        });
        projectTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        projectTableViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                setCheckedProjects();
                setPageComplete(projectTableViewer.getCheckedElements().length > 0);
            }
        });

        final Table projectTable = projectTableViewer.getTable();
        projectTable.getHorizontalBar().setEnabled(true);
        projectTable.setHeaderVisible(true);
        projectTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite projectTableButtonsContainer = new Composite(wizardContainer, SWT.NONE);
        projectTableButtonsContainer.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
        projectTableButtonsContainer.setLayout(new GridLayout());

        final Button selectAll = new Button(projectTableButtonsContainer, SWT.NONE);
        selectAll.setText("Select All");
        selectAll.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        selectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                projectTableViewer.setAllChecked(true);

                setCheckedProjects();
                setPageComplete(projectTableViewer.getCheckedElements().length > 0);
            }
        });

        final Button deselectAll = new Button(projectTableButtonsContainer, SWT.NONE);
        deselectAll.setText("Deselect All");
        deselectAll.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        deselectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                projectTableViewer.setAllChecked(false);

                setCheckedProjects();
                setPageComplete(projectTableViewer.getCheckedElements().length > 0);
            }
        });

        // TODO remove when https://bugs.eclipse.org/bugs/show_bug.cgi?id=245106 is fixed
        final WorkingSetDescriptor[] descriptors = WorkbenchPlugin.getDefault().getWorkingSetRegistry().getWorkingSetDescriptors();
        final List<String> workingSetTypes = FluentIterable.from(Arrays.asList(descriptors))
                                                           .transform(new Function<WorkingSetDescriptor, String>() {
                                                               @Override
                                                               public String apply(WorkingSetDescriptor descriptor) {
                                                                   return descriptor.getId();
                                                               }
                                                           }).toList();

        workingSetGroup = new WorkingSetGroup(wizardContainer, null, workingSetTypes.toArray(new String[workingSetTypes.size()]));

        setControl(wizardContainer);
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        if (isCurrentPage()) {
            projectTableViewer.setInput(null);
            workspaceComboViewer.setInput(null);
            loadWorkspaces();
            setPageComplete(!getProjects().isEmpty());
        }
    }

    /**
     * Method used to load the workspaces asynchronously when the wizard page is displayed.
     */
    private void loadWorkspaces() {
        try {

            getContainer().run(true, false, new LoadWorkspacesJob(getWizard()) {
                @Override
                public void postLoadCallback(List<WorkspaceRef> workspaceRefs) {
                    workspaceComboViewer.setInput(workspaceRefs.toArray());
                    if (!workspaceRefs.isEmpty()) {
                        workspaceComboViewer.setSelection(new StructuredSelection(workspaceRefs.get(0)));
                    }
                    workspaceComboViewer.refresh();
                }
            });

        } catch (InvocationTargetException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method used to load the workspace projects asynchronously when the workspace is selected.
     */
    private void loadWorkspaceProjects(final WorkspaceRef workspaceRef) {
        try {

            projectTableViewer.setInput(null);

            getContainer().run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    final CredentialsProviderWizard wizard = getWizard();

                    try {

                        monitor.beginTask("Fetch workspace projects from Codenvy", UNKNOWN);

                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                final String platformURL = wizard.getUrl();
                                final String username = wizard.getUsername();
                                final String password = wizard.getPassword();
                                final boolean isStoreUserCredentials = wizard.isStoreUserCredentials();
                                final Credentials credentials = new Credentials.Builder().withUsername(username)
                                                                                         .withPassword(password)
                                                                                         .storeOnlyToken(!isStoreUserCredentials)
                                                                                         .build();


                                final Codenvy codenvy = CodenvyPlugin.getDefault()
                                                                     .getCodenvyBuilder(platformURL, username)
                                                                     .withCredentials(credentials)
                                                                     .build();

                                final List<Project> projects = codenvy.project()
                                                                      .getWorkspaceProjects(workspaceRef.id)
                                                                      .execute();

                                projectTableViewer.setInput(projects);

                                final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
                                for (Project oneProject : projects) {
                                    final IProject workspaceProject = workspaceRoot.getProject(oneProject.name);
                                    projectTableViewer.setGrayed(oneProject, workspaceProject.exists());
                                }

                                projectTableViewer.refresh();
                            }
                        });

                    } finally {
                        monitor.done();
                    }
                }
            });

        } catch (InvocationTargetException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Defines checked projects in UI and shared data.
     *
     * @see ImportWizardSharedData
     */
    private void setCheckedProjects() {
        for (Object oneCheckedElement : projectTableViewer.getCheckedElements()) {
            final boolean isGrayed = projectTableViewer.getGrayed(oneCheckedElement);
            if (isGrayed) {
                projectTableViewer.setChecked(oneCheckedElement, false);
            }
        }
    }

    /**
     * Returns the selected working sets.
     *
     * @return the selected working sets
     */
    public List<IWorkingSet> getWorkingSets() {
        final List<IWorkingSet> selectedWorkingSets = ImmutableList.copyOf(workingSetGroup.getSelectedWorkingSets());
        return selectedWorkingSets;
    }

    /**
     * Returns the selected Codenvy projects.
     *
     * @return the selected Codenvy projects never {@code null}.
     */
    public List<Project> getProjects() {
        final List<Project> selectedProjects = new ArrayList<>();
        for (Object oneProject : projectTableViewer.getCheckedElements()) {
            selectedProjects.add((Project)oneProject);
        }
        return selectedProjects;
    }

    /**
     * Custom label provider displaying grayed elements in grey.
     *
     * @author Kevin Pollet
     */
    private class ColumnLabelProviderWithGreyElement extends ColumnLabelProvider {
        @Override
        public Color getForeground(Object element) {
            final boolean isGrayed = projectTableViewer.getGrayed(element);
            return isGrayed ? Display.getCurrent().getSystemColor(SWT.COLOR_GRAY) : super.getForeground(element);
        }
    }

    @Override
    public CredentialsProviderWizard getWizard() {
        return (CredentialsProviderWizard)super.getWizard();
    }
}
