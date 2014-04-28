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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.asList;
import static org.eclipse.core.runtime.IProgressMonitor.UNKNOWN;
import static org.eclipse.jface.viewers.CheckboxTableViewer.newCheckList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.ProjectService;
import com.codenvy.eclipse.core.RestServiceFactory;
import com.codenvy.eclipse.core.UserService;
import com.codenvy.eclipse.core.WorkspaceService;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.Project;
import com.codenvy.eclipse.core.model.User;
import com.codenvy.eclipse.core.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.ui.Activator;
import com.codenvy.eclipse.ui.utils.ImageConstants;

/**
 * Project wizard page. In this wizard page the user selects the projects to import in Eclipse.
 * 
 * @author Kevin Pollet
 */
public class ProjectWizardPage extends WizardPage implements IPageChangingListener, IPageChangedListener {
    private ComboViewer                  workspaceComboViewer;
    private CheckboxTableViewer          projectTableViewer;
    private ComboViewer                  workingSetComboViewer;
    private final ImportWizardSharedData importWizardSharedData;

    /**
     * Constructs an instance of {@link ProjectWizardPage}.
     * 
     * @param importWizardSharedData data shared between wizard pages.
     * @throws NullPointerException if importWizardSharedData is {@code null}.
     */
    public ProjectWizardPage(ImportWizardSharedData importWizardSharedData) {
        super(ProjectWizardPage.class.getSimpleName());

        checkNotNull(importWizardSharedData);

        this.importWizardSharedData = importWizardSharedData;

        setTitle("Codenvy Projects");
        setDescription("Select Codenvy projects to import");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageConstants.WIZARD_LOGO_KEY));
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
        projectNameColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return element instanceof Project ? ((Project)element).name : super.getText(element);
            }
        });

        final TableViewerColumn projectTypeNameColumn = new TableViewerColumn(projectTableViewer, SWT.NONE);
        projectTypeNameColumn.getColumn().setWidth(150);
        projectTypeNameColumn.getColumn().setText("Type");
        projectTypeNameColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return element instanceof Project ? ((Project)element).projectTypeName : super.getText(element);
            }
        });

        final TableViewerColumn projectDescriptionColumn = new TableViewerColumn(projectTableViewer, SWT.NONE);
        projectDescriptionColumn.getColumn().setWidth(150);
        projectDescriptionColumn.getColumn().setText("Description");
        projectDescriptionColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return element instanceof Project ? ((Project)element).description : super.getText(element);
            }
        });

        projectTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        projectTableViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
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
                setPageComplete(true);
            }
        });

        final Button deselectAll = new Button(projectTableButtonsContainer, SWT.NONE);
        deselectAll.setText("Deselect All");
        deselectAll.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        deselectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                projectTableViewer.setAllChecked(false);
                setPageComplete(false);
            }
        });

        final Button addToWorkingSet = new Button(wizardContainer, SWT.CHECK);
        addToWorkingSet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        addToWorkingSet.setText("Add project(s) to working set");
        addToWorkingSet.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final Combo workingSetCombo = workingSetComboViewer.getCombo();
                if (addToWorkingSet.getSelection()) {
                    workingSetCombo.setEnabled(true);
                    workingSetCombo.setFocus();
                } else {
                    workingSetCombo.setEnabled(false);
                }
            }
        });

        workingSetComboViewer = new ComboViewer(wizardContainer, SWT.NONE);
        workingSetComboViewer.getCombo().setEnabled(false);
        workingSetComboViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        workingSetComboViewer.setContentProvider(new ArrayContentProvider());
        workingSetComboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof IWorkingSet) {
                    return ((IWorkingSet)element).getLabel();
                }
                return super.getText(element);
            }
        });

        setControl(wizardContainer);
    }

    @Override
    public void handlePageChanging(PageChangingEvent event) {
        if (isCurrentPage()) {
            final List<Project> checkedProjects = new ArrayList<>();
            for (Object oneProject : projectTableViewer.getCheckedElements()) {
                checkedProjects.add((Project)oneProject);
            }

            importWizardSharedData.setProjects(checkedProjects);
        }
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        if (isCurrentPage()) {
            projectTableViewer.setInput(null);
            workingSetComboViewer.setInput(null);
            workspaceComboViewer.setInput(null);
            setPageComplete(!importWizardSharedData.getProjects().isEmpty());
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
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Fetch workspaces from Codenvy", UNKNOWN);

                    final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
                    final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);

                    if (restServiceFactoryRef != null) {
                        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);

                        if (restServiceFactory != null) {

                            try {

                                final String url = importWizardSharedData.getUrl().get();
                                final CodenvyToken token = importWizardSharedData.getCodenvyToken().get();
                                final UserService userService = restServiceFactory.newRestServiceWithAuth(UserService.class, url, token);
                                final WorkspaceService workspaceService = restServiceFactory.newRestServiceWithAuth(WorkspaceService.class, url, token);
                                final User currentUser = userService.getCurrentUser();
                                final List<WorkspaceRef> workspaces = workspaceService.findWorkspacesByAccount(currentUser.id);

                                Display.getDefault().syncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        workspaceComboViewer.setInput(workspaces.toArray());
                                        if (!workspaces.isEmpty()) {
                                            workspaceComboViewer.setSelection(new StructuredSelection(workspaces.get(0)));
                                        }
                                    }
                                });

                            } finally {
                                context.ungetService(restServiceFactoryRef);
                            }
                        }
                    }

                    monitor.done();
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

            getContainer().run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Fetch workspace projects from Codenvy", UNKNOWN);

                    final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
                    final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);

                    if (restServiceFactoryRef != null) {
                        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);

                        if (restServiceFactory != null) {

                            try {

                                final String url = importWizardSharedData.getUrl().get();
                                final CodenvyToken token = importWizardSharedData.getCodenvyToken().get();
                                final ProjectService projectService = restServiceFactory.newRestServiceWithAuth(ProjectService.class, url, token);
                                final List<Project> projects = projectService.getWorkspaceProjects(workspaceRef.id);

                                Display.getDefault().syncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        projectTableViewer.setInput(projects);

                                        final IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
                                        final IWorkingSet codenvyWorkspaceWorkingSet = workingSetManager.createWorkingSet("codenvy-ws-" + workspaceRef.name, new IAdaptable[0]);
                                        final List<IWorkingSet> workingSets = asList(codenvyWorkspaceWorkingSet, workingSetManager.getWorkingSets());

                                        workingSetComboViewer.setInput(workingSets.toArray());
                                        workingSetComboViewer.setSelection(new StructuredSelection(codenvyWorkspaceWorkingSet));
                                    }
                                });

                            } finally {
                                context.ungetService(restServiceFactoryRef);
                            }
                        }
                    }

                    monitor.done();
                }
            });

        } catch (InvocationTargetException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
