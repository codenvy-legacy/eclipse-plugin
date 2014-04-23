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

import static com.codenvy.eclipse.ui.utils.ImageConstants.WIZARD_LOGO_KEY;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.eclipse.core.runtime.IProgressMonitor.UNKNOWN;

import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.RestServiceFactory;
import com.codenvy.eclipse.core.UserService;
import com.codenvy.eclipse.core.WorkspaceService;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.User;
import com.codenvy.eclipse.core.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.ui.Activator;
import com.google.common.base.Optional;

/**
 * Workspace wizard page. In this wizard page the user selects the workspace where the Codenvy projects have to be pulled.
 * 
 * @author Kevin Pollet
 */
public class WorkspaceWizardPage extends WizardPage implements IPageChangingListener, IPageChangedListener {
    private CheckboxTableViewer          workspaceTableViewer;
    private final ImportWizardSharedData importWizardSharedData;

    /**
     * Constructs an instance of {@link WorkspaceWizardPage}.
     * 
     * @param importWizardSharedData data shared between wizard pages.
     * @throws NullPointerException if importWizardSharedData is {@code null}.
     */
    public WorkspaceWizardPage(ImportWizardSharedData importWizardSharedData) {
        super(WorkspaceWizardPage.class.getSimpleName());

        checkNotNull(importWizardSharedData);

        this.importWizardSharedData = importWizardSharedData;

        setTitle("Codenvy Workspaces");
        setDescription("Select Codenvy workspace to pull projects from");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(WIZARD_LOGO_KEY));
        setPageComplete(false);
    }

    @Override
    public void createControl(Composite parent) {
        final Composite wizardContainer = new Composite(parent, SWT.NONE);
        wizardContainer.setLayout(new GridLayout());
        wizardContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // construct the workspace tree
        final Label workspaceTableLabel = new Label(wizardContainer, SWT.NONE);
        workspaceTableLabel.setText("Workspace(s) linked with your Codenvy account :");

        workspaceTableViewer =
                               CheckboxTableViewer.newCheckList(wizardContainer, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL
                                                                                 | SWT.V_SCROLL);
        workspaceTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        workspaceTableViewer.getTable().setHeaderVisible(true);
        workspaceTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        workspaceTableViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                workspaceTableViewer.setCheckedElements(new Object[0]);
                workspaceTableViewer.setChecked(event.getElement(), event.getChecked());

                setPageComplete(event.getChecked());
            }
        });

        final TableViewerColumn workspaceNameColumn = new TableViewerColumn(workspaceTableViewer, SWT.NONE);
        workspaceNameColumn.getColumn().setWidth(200);
        workspaceNameColumn.getColumn().setText("Name");
        workspaceNameColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return element instanceof WorkspaceRef ? ((WorkspaceRef)element).name : super.getText(element);
            }
        });

        setControl(wizardContainer);
    }

    /**
     * Method used to load the user workspaces asynchronously when the wizard page is displayed.
     */
    private void onEnterPage() {
        workspaceTableViewer.setInput(null);

        try {

            getContainer().run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Fetch user workspaces from Codenvy", UNKNOWN);

                    final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
                    final ServiceReference<RestServiceFactory> restServiceFactoryRef =
                                                                                       context.getServiceReference(RestServiceFactory.class);

                    if (restServiceFactoryRef != null) {
                        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);

                        if (restServiceFactory != null) {
                            try {

                                final String url = importWizardSharedData.getUrl().get();
                                final CodenvyToken token = importWizardSharedData.getCodenvyToken().get();
                                final UserService userService = restServiceFactory.newRestServiceWithAuth(UserService.class, url, token);
                                final WorkspaceService workspaceService =
                                                                          restServiceFactory.newRestServiceWithAuth(WorkspaceService.class,
                                                                                                                    url, token);

                                final User currentUser = userService.getCurrentUser();
                                final List<WorkspaceRef> workspaces = workspaceService.findWorkspacesByAccount(currentUser.id);

                                Display.getDefault().syncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        workspaceTableViewer.setInput(workspaces);

                                        final Optional<WorkspaceRef> checkedWorkspaceRef = importWizardSharedData.getWorkspaceRef();
                                        if (checkedWorkspaceRef.isPresent()) {
                                            workspaceTableViewer.setChecked(checkedWorkspaceRef.get(), true);
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
        if (isCurrentPage()) {
            final Object[] checkedElements = workspaceTableViewer.getCheckedElements();
            if (checkedElements.length > 0) {
                importWizardSharedData.setWorkspaceRef(Optional.fromNullable((WorkspaceRef)checkedElements[0]));
            }
        }
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        if (isCurrentPage()) {
            setPageComplete(importWizardSharedData.getWorkspaceRef().isPresent());
            onEnterPage();
        }
    }
}
