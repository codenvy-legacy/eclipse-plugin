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
package com.codenvy.eclipse.ui.wizards.existing.pages;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.eclipse.core.runtime.IProgressMonitor.UNKNOWN;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.service.api.ProjectService;
import com.codenvy.eclipse.core.service.api.RestServiceFactory;
import com.codenvy.eclipse.core.service.api.WorkspaceService;
import com.codenvy.eclipse.core.service.api.model.CodenvyToken;
import com.codenvy.eclipse.core.service.api.model.Project;
import com.codenvy.eclipse.core.service.api.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.ui.Activator;
import com.codenvy.eclipse.ui.utils.ImageConstants;

/**
 * Project wizard page. In this wizard page the user selects the projects to import in Eclipse.
 * 
 * @author Kevin Pollet
 */
public class ProjectWizardPage extends WizardPage implements IPageChangingListener, IPageChangedListener {
    private CheckboxTableViewer          projectTableViewer;
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
        wizardContainer.setLayout(new GridLayout());
        wizardContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Label projectTableLabel = new Label(wizardContainer, SWT.NONE);
        final String selectedWorkspaceName = importWizardSharedData.getWorkspace().get().workspaceRef.name;

        projectTableLabel.setText("Project(s) in workspace '" + selectedWorkspaceName + "'");

        projectTableViewer =
                             CheckboxTableViewer.newCheckList(wizardContainer, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL
                                                                               | SWT.V_SCROLL);

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
                ProjectWizardPage.this.setPageComplete(projectTableViewer.getCheckedElements().length > 0);
            }
        });

        final Table workspaceTable = projectTableViewer.getTable();
        workspaceTable.getHorizontalBar().setEnabled(true);
        workspaceTable.setHeaderVisible(true);
        workspaceTable.setLinesVisible(true);
        workspaceTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        setControl(wizardContainer);
    }

    /**
     * Method used to load the workspace projects asynchronously when the wizard page is displayed.
     */
    private void onEnterPage() {
        projectTableViewer.setInput(null);

        try {

            getContainer().run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Fetch workspace projects from Codenvy", UNKNOWN);

                    final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
                    final ServiceReference<RestServiceFactory> restServiceFactoryRef =
                                                                                       context.getServiceReference(RestServiceFactory.class);

                    if (restServiceFactoryRef != null) {
                        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);

                        if (restServiceFactory != null) {

                            try {

                                final String url = importWizardSharedData.getUrl().get();
                                final CodenvyToken token = importWizardSharedData.getCodenvyToken().get();
                                final String selectedWorkspaceName = importWizardSharedData.getWorkspace().get().workspaceRef.name;
                                final WorkspaceService workspaceService =
                                                                          restServiceFactory.newRestServiceWithAuth(WorkspaceService.class,
                                                                                                                    url, token);
                                final ProjectService projectService =
                                                                      restServiceFactory.newRestServiceWithAuth(ProjectService.class, url,
                                                                                                                token);
                                final WorkspaceRef workspace = workspaceService.getWorkspaceByName(selectedWorkspaceName);
                                final List<Project> projects = projectService.getWorkspaceProjects(workspace.id);

                                Display.getDefault().syncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        projectTableViewer.setInput(projects);

                                        // restore previous state if needed
                                        final List<Project> checkedProjects = importWizardSharedData.getProjects();
                                        if (!checkedProjects.isEmpty()) {
                                            projectTableViewer.setCheckedElements(checkedProjects.toArray());
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

    @Override
    public void handlePageChanging(PageChangingEvent event) {
        final IWizardPage currentPage = (IWizardPage)event.getCurrentPage();

        if (getName().equals(currentPage.getName())) {
            final List<Project> checkedProjects = new ArrayList<>();
            for (Object oneProject : projectTableViewer.getCheckedElements()) {
                checkedProjects.add((Project)oneProject);
            }

            importWizardSharedData.setProjects(checkedProjects);
        }
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        final IWizardPage currentPage = (IWizardPage)event.getSelectedPage();

        if (getName().equals(currentPage.getName())) {
            onEnterPage();
        }
    }
}
