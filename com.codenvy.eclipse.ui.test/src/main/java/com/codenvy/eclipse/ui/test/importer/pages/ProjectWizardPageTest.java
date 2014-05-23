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
package com.codenvy.eclipse.ui.test.importer.pages;

import static com.codenvy.eclipse.ui.test.mocks.ProjectServiceMock.MOCK_PROJECT_DESCRIPTION;
import static com.codenvy.eclipse.ui.test.mocks.ProjectServiceMock.MOCK_PROJECT_NAME;
import static com.codenvy.eclipse.ui.test.mocks.ProjectServiceMock.MOCK_PROJECT_TYPE_NAME;
import static com.codenvy.eclipse.ui.test.mocks.WorkspaceServiceMock.MOCK_WORKSPACE_NAME;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.codenvy.eclipse.ui.test.SWTBotBaseTest;

/**
 * Project page tests.
 * 
 * @author Kevin Pollet
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ProjectWizardPageTest extends SWTBotBaseTest {
    @Test
    public void testThatAllWorkspacesAreAvailable() {
        final SWTBotShell shell = openProjectWizardPage();
        final SWTBotCombo workspaceComboBox = shell.bot().comboBox(0);

        Assert.assertEquals(MOCK_WORKSPACE_NAME, workspaceComboBox.items()[0]);
        Assert.assertTrue(workspaceComboBox.itemCount() == 4);

        shell.close();
    }

    @Test
    public void testThatWorkspaceSelectionReloadProjectTable() {
        final SWTBotShell shell = openProjectWizardPage();
        final SWTBotTable projectTable = shell.bot().table(0);
        final SWTBotCombo workspaceComboBox = shell.bot().comboBox(0);

        Assert.assertTrue(projectTable.rowCount() == 4);

        workspaceComboBox.setSelection(1);

        Assert.assertTrue(projectTable.rowCount() == 0);

        shell.close();
    }

    @Test
    public void testThatProjectTableContainsAllWorkspaceProjects() {
        final SWTBotShell shell = openProjectWizardPage();
        final SWTBotTable projectTable = shell.bot().table(0);

        Assert.assertTrue(projectTable.rowCount() == 4);
        Assert.assertEquals(MOCK_PROJECT_NAME, projectTable.cell(0, 0));
        Assert.assertEquals(MOCK_PROJECT_TYPE_NAME, projectTable.cell(0, 1));
        Assert.assertEquals(MOCK_PROJECT_DESCRIPTION, projectTable.cell(0, 2));

        shell.close();
    }

    @Test
    public void testThatOneProjectMustBeSelectedToFinish() {
        final SWTBotShell shell = openProjectWizardPage();
        final SWTBotButton finishButton = shell.bot().button("Finish");
        final SWTBotTable projectTable = shell.bot().table(0);

        Assert.assertFalse(finishButton.isEnabled());

        projectTable.getTableItem(0)
                    .check();

        Assert.assertTrue(finishButton.isEnabled());

        shell.close();
    }

    @Test
    public void testThatAllProjectsAreSelectedWithSelectAll() {
        final SWTBotShell shell = openProjectWizardPage();
        final SWTBotTable projectTable = shell.bot().table(0);

        shell.bot()
             .button("Select All")
             .click();

        for (int i = 0; i < projectTable.rowCount(); i++) {
            final SWTBotTableItem oneProjectRow = projectTable.getTableItem(i);
            Assert.assertTrue(oneProjectRow.isChecked());
        }

        shell.close();
    }

    @Test
    public void testThatAllProjectsAreDeselectedWithDeselectAll() {
        final SWTBotShell shell = openProjectWizardPage();
        final SWTBotTable projectTable = shell.bot().table(0);

        for (int i = 0; i < projectTable.rowCount(); i++) {
            projectTable.getTableItem(i).check();
        }

        shell.bot()
             .button("Deselect All")
             .click();

        for (int i = 0; i < projectTable.rowCount(); i++) {
            final SWTBotTableItem oneProjectRow = projectTable.getTableItem(i);
            Assert.assertFalse(oneProjectRow.isChecked());
        }

        shell.close();
    }

    @Test
    public void testThatAddToWorkingSetCheckboxIsUncheckedByDefault() {
        final SWTBotShell shell = openProjectWizardPage();
        final SWTBotCheckBox addToWorkingSetCheckBox = shell.bot()
                                                            .checkBox(0);

        Assert.assertFalse(addToWorkingSetCheckBox.isChecked());

        shell.close();
    }

    @Test
    public void testThatWorkingSetComboAndButtonAreDisabledByDefault() {
        final SWTBotShell shell = openProjectWizardPage();
        final SWTBotCombo workingSetComboBox = shell.bot().comboBox(1);
        final SWTBotButton workingSetSelectButton = shell.bot().button("Select...");

        Assert.assertFalse(workingSetComboBox.isEnabled());
        Assert.assertFalse(workingSetSelectButton.isEnabled());

        shell.close();
    }

    @Test
    public void testThatCheckingAddToWorkingSetSelectButtonIsEnabled() {
        final SWTBotShell shell = openProjectWizardPage();
        final SWTBotButton workingSetSelectButton = shell.bot().button("Select...");
        final SWTBotCheckBox addToWorkingSetCheckBox = shell.bot().checkBox(0);

        addToWorkingSetCheckBox.click();

        Assert.assertTrue(workingSetSelectButton.isEnabled());

        shell.close();
    }

    @Test
    public void testThatAlreadyImportedProjectIsGrayedInProjectTable() throws CoreException {
        importProject(MOCK_WORKSPACE_NAME, MOCK_PROJECT_NAME);

        final SWTBotShell shell = openProjectWizardPage();
        final SWTBotTable projectTable = shell.bot().table(0);

        for (int i = 0; i < projectTable.rowCount(); i++) {
            final SWTBotTableItem oneProjectRow = projectTable.getTableItem(i);
            Assert.assertTrue(oneProjectRow.isGrayed() == MOCK_PROJECT_NAME.equals(projectTable.cell(i, 0)));
        }

        deleteProject(MOCK_PROJECT_NAME);

        shell.close();
    }
}
