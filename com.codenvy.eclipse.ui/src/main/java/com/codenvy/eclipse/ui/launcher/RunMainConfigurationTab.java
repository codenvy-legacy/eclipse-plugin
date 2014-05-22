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
package com.codenvy.eclipse.ui.launcher;


import static org.eclipse.ui.model.WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.codenvy.eclipse.core.launcher.LaunchConstants;

/**
 * The codenvy launch configuration main tab.
 * 
 * @author Kevin Pollet
 */
public class RunMainConfigurationTab extends AbstractLaunchConfigurationTab {
    private static final String TAB_NAME = "Main";

    private Text                projectLocation;

    @Override
    public void createControl(Composite parent) {
        final Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());

        final Group projectGroup = new Group(mainComposite, SWT.NONE);
        projectGroup.setText("Project:");
        projectGroup.setLayout(new GridLayout(2, false));
        projectGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        projectLocation = new Text(projectGroup, SWT.BORDER);
        projectLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button browse = createPushButton(projectGroup, "Browse...", null);
        browse.setFocus();
        browse.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final ElementListSelectionDialog selectionDialog = new ElementListSelectionDialog(getShell(), getDecoratingWorkbenchLabelProvider());
                selectionDialog.setTitle("Project selection");
                selectionDialog.setMessage("Select a project to constrain your search.");
                selectionDialog.setMultipleSelection(false);
                selectionDialog.setElements(ResourcesPlugin.getWorkspace().getRoot().getProjects());
                selectionDialog.open();

                final Object[] resource = selectionDialog.getResult();
                if (resource != null && resource.length > 0) {
                    final IProject project = (IProject)resource[0];
                    projectLocation.setText(project.getName());
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
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        // no default configuration
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {

            projectLocation.setText(configuration.getAttribute(LaunchConstants.CODENVY_PROJECT_NAME_ATTRIBUTE_NAME, ""));

        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(LaunchConstants.CODENVY_PROJECT_NAME_ATTRIBUTE_NAME, projectLocation.getText());
    }

    @Override
    public String getName() {
        return TAB_NAME;
    }
}
