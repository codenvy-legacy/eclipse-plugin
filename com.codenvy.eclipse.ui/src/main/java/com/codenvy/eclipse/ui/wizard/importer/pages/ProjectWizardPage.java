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
package com.codenvy.eclipse.ui.wizard.importer.pages;

import static org.eclipse.core.runtime.IProgressMonitor.UNKNOWN;
import static org.eclipse.jface.viewers.CheckboxTableViewer.newCheckList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

import com.codenvy.eclipse.core.CodenvyPlugin;
import com.codenvy.eclipse.core.client.Codenvy;
import com.codenvy.eclipse.core.client.model.Credentials;
import com.codenvy.eclipse.core.client.model.Project;
import com.codenvy.eclipse.core.client.model.Workspace;
import com.codenvy.eclipse.core.client.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.ui.CodenvyUIPlugin;
import com.codenvy.eclipse.ui.Images;
import com.codenvy.eclipse.ui.wizard.importer.ImportProjectFromCodenvyWizard;
import com.google.common.collect.ImmutableList;

/**
 * Project wizard page. In this wizard page the user selects the projects to import in Eclipse.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class ProjectWizardPage extends WizardPage implements IPageChangedListener {
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

        // TODO: replace hardcoded ids once bug 245106 is fixed
        String[] workingSetTypes = new String[]{"org.eclipse.ui.resourceWorkingSetPage", //$NON-NLS-1$
                "org.eclipse.jdt.ui.JavaWorkingSetPage" //$NON-NLS-1$
        };
        workingSetGroup = new WorkingSetGroup(wizardContainer, null, workingSetTypes);

        setControl(wizardContainer);
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        if (isCurrentPage()) {
            projectTableViewer.setInput(null);
            workspaceComboViewer.setInput(null);
            setPageComplete(!getProjects().isEmpty());
            loadWorkspaces();
        }
    }

    /**
     * Method used to load the workspaces asynchronously when the wizard page is displayed.
     */
    private void loadWorkspaces() {
        try {

            getContainer().run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    final ImportProjectFromCodenvyWizard wizard = (ImportProjectFromCodenvyWizard)getWizard();

                    try {
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                final String platformURL = wizard.getAuthenticationWizardPage().getURL();
                                final String username = wizard.getAuthenticationWizardPage().getUsername();
                                final String password = wizard.getAuthenticationWizardPage().getPassword();

                                final Codenvy codenvy = CodenvyPlugin.getDefault()
                                                                     .getCodenvyBuilder(platformURL, username)
                                                                     .withCredentials(new Credentials(username, password))
                                                                     .build();


                                final List<Workspace> workspaces = codenvy.workspace()
                                                                          .all()
                                                                          .execute();

                                monitor.beginTask("Fetch workspaces from Codenvy", workspaces.size());

                                final List<WorkspaceRef> workspaceRefs = new ArrayList<>();
                                for (Workspace workspace : workspaces) {
                                    workspaceRefs.add(codenvy.workspace().withName(workspace.workspaceRef.name).execute());
                                    monitor.worked(1);
                                }

                                workspaceComboViewer.setInput(workspaceRefs.toArray());
                                if (!workspaces.isEmpty()) {
                                    workspaceComboViewer.setSelection(new StructuredSelection(workspaceRefs.get(0)));
                                }
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
     * Method used to load the workspace projects asynchronously when the workspace is selected.
     */
    private void loadWorkspaceProjects(final WorkspaceRef workspaceRef) {
        try {

            projectTableViewer.setInput(null);

            getContainer().run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    final ImportProjectFromCodenvyWizard wizard = (ImportProjectFromCodenvyWizard)getWizard();

                    try {

                        monitor.beginTask("Fetch workspace projects from Codenvy", UNKNOWN);

                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                final String platformURL = wizard.getAuthenticationWizardPage().getURL();
                                final String username = wizard.getAuthenticationWizardPage().getUsername();
                                final String password = wizard.getAuthenticationWizardPage().getPassword();

                                final Codenvy codenvy = CodenvyPlugin.getDefault()
                                                                     .getCodenvyBuilder(platformURL, username)
                                                                     .withCredentials(new Credentials(username, password))
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
}
