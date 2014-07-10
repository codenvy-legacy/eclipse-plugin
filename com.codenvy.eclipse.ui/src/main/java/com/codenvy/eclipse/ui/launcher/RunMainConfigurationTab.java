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
package com.codenvy.eclipse.ui.launcher;


import static com.codenvy.eclipse.core.launcher.LaunchConstants.CODENVY_PROJECT_NAME_ATTRIBUTE_NAME;
import static com.codenvy.eclipse.core.utils.StringHelper.isEmpty;
import static com.codenvy.eclipse.ui.Images.RUN_MAIN_TAB_ICON;
import static org.eclipse.ui.model.WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.codenvy.eclipse.core.team.CodenvyProvider;
import com.codenvy.eclipse.ui.CodenvyUIPlugin;

/**
 * The Codenvy launch configuration main tab.
 * 
 * @author Kevin Pollet
 */
public final class RunMainConfigurationTab extends AbstractLaunchConfigurationTab {
    private static final String TAB_NAME = "Main";

    private Text                projectName;
    private IWorkspaceRoot      workspaceRoot;

    public RunMainConfigurationTab() {
        this.workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    }

    @Override
    public void createControl(Composite parent) {
        final Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());

        final Group projectGroup = new Group(mainComposite, SWT.NONE);
        projectGroup.setText("Project:");
        projectGroup.setLayout(new GridLayout(2, false));
        projectGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        projectName = new Text(projectGroup, SWT.BORDER);
        projectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        projectName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                updateLaunchConfigurationDialog();
            }
        });

        final Button browse = createPushButton(projectGroup, "Browse...", null);
        browse.setFocus();
        browse.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final ElementListSelectionDialog selectionDialog =
                                                                   new ElementListSelectionDialog(getShell(),
                                                                                                  getDecoratingWorkbenchLabelProvider());
                selectionDialog.setTitle("Project selection");
                selectionDialog.setMessage("Select a project to constrain your search.");
                selectionDialog.setMultipleSelection(false);
                selectionDialog.setElements(ResourcesPlugin.getWorkspace().getRoot().getProjects());
                selectionDialog.open();

                final Object[] resource = selectionDialog.getResult();
                if (resource != null && resource.length > 0) {
                    final IProject project = (IProject)resource[0];
                    projectName.setText(project.getName());
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        setControl(mainComposite);
    }

    @Override
    public boolean isValid(ILaunchConfiguration configuration) {
        final String projectName;
        try {

            projectName = configuration.getAttribute(CODENVY_PROJECT_NAME_ATTRIBUTE_NAME, "");

        } catch (CoreException e) {
            throw new RuntimeException(e);
        }

        if (!isEmpty(projectName)) {

            final IProject project = workspaceRoot.getProject(projectName);
            final boolean isCodenvyProject =
                                             project.exists()
                                                 && RepositoryProvider.getProvider(project, CodenvyProvider.PROVIDER_ID) != null;
            setErrorMessage(isCodenvyProject ? null : "Project specified is not a valid Codenvy project");

            return isCodenvyProject;
        }

        setErrorMessage("Project not specified");
        return false;
    }

    @Override
    public boolean canSave() {
        final String projectName = this.projectName.getText();
        if (!isEmpty(projectName)) {
            final IProject project = workspaceRoot.getProject(projectName);
            return project.exists() && RepositoryProvider.getProvider(project, CodenvyProvider.PROVIDER_ID) != null;
        }
        return false;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        // no default configuration
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {

            projectName.setText(configuration.getAttribute(CODENVY_PROJECT_NAME_ATTRIBUTE_NAME, ""));

        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(CODENVY_PROJECT_NAME_ATTRIBUTE_NAME, projectName.getText());
    }

    @Override
    public String getName() {
        return TAB_NAME;
    }

    @Override
    public Image getImage() {
        final ImageRegistry imageRegistry = CodenvyUIPlugin.getDefault().getImageRegistry();
        return imageRegistry.get(RUN_MAIN_TAB_ICON);
    }
}
