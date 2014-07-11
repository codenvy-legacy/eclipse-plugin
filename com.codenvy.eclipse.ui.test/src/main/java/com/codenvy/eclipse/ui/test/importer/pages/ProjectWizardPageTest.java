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
package com.codenvy.eclipse.ui.test.importer.pages;

import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_PROJECT_DESCRIPTION;
import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_PROJECT_NAME;
import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_PROJECT_TYPE_NAME;
import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_WORKSPACE_NAME;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
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
        openImportProjectWizardPage();

        final SWTBotCombo workspaceComboBox = bot.comboBox(0);

        Assert.assertEquals(MOCK_WORKSPACE_NAME, workspaceComboBox.items()[0]);
        Assert.assertTrue(workspaceComboBox.itemCount() == 4);
    }

    @Test
    public void testThatWorkspaceSelectionReloadProjectTable() {
        openImportProjectWizardPage();

        final SWTBotTable projectTable = bot.table(0);
        final SWTBotCombo workspaceComboBox = bot.comboBox(0);

        Assert.assertTrue(projectTable.rowCount() == 4);

        workspaceComboBox.setSelection(1);

        Assert.assertTrue(projectTable.rowCount() == 0);
    }

    @Test
    public void testThatProjectTableContainsAllWorkspaceProjects() {
        openImportProjectWizardPage();

        final SWTBotTable projectTable = bot.table(0);

        Assert.assertTrue(projectTable.rowCount() == 4);
        Assert.assertEquals(MOCK_PROJECT_NAME, projectTable.cell(0, 0));
        Assert.assertEquals(MOCK_PROJECT_TYPE_NAME, projectTable.cell(0, 1));
        Assert.assertEquals(MOCK_PROJECT_DESCRIPTION, projectTable.cell(0, 2));
    }

    @Test
    public void testThatOneProjectMustBeSelectedToFinish() {
        openImportProjectWizardPage();

        final SWTBotTable projectTable = bot.table(0);
        final SWTBotButton finishButton = bot.button("Finish");

        Assert.assertFalse(finishButton.isEnabled());

        projectTable.getTableItem(0).check();

        Assert.assertTrue(finishButton.isEnabled());
    }

    @Test
    public void testThatAllProjectsAreSelectedWithSelectAll() {
        openImportProjectWizardPage();

        final SWTBotTable projectTable = bot.table(0);

        bot.button("Select All").click();

        for (int i = 0; i < projectTable.rowCount(); i++) {
            final SWTBotTableItem oneProjectRow = projectTable.getTableItem(i);
            Assert.assertTrue(oneProjectRow.isChecked());
        }
    }

    @Test
    public void testThatAllProjectsAreDeselectedWithDeselectAll() {
        openImportProjectWizardPage();

        final SWTBotTable projectTable = bot.table(0);

        for (int i = 0; i < projectTable.rowCount(); i++) {
            projectTable.getTableItem(i).check();
        }

        bot.button("Deselect All").click();

        for (int i = 0; i < projectTable.rowCount(); i++) {
            final SWTBotTableItem oneProjectRow = projectTable.getTableItem(i);
            Assert.assertFalse(oneProjectRow.isChecked());
        }
    }

    @Test
    public void testThatAddToWorkingSetCheckboxIsUncheckedByDefault() {
        openImportProjectWizardPage();

        final SWTBotCheckBox addToWorkingSetCheckBox = bot.checkBox(0);

        Assert.assertFalse(addToWorkingSetCheckBox.isChecked());
    }

    @Test
    public void testThatWorkingSetComboAndButtonAreDisabledByDefault() {
        openImportProjectWizardPage();

        final SWTBotCombo workingSetComboBox = bot.comboBox(1);
        final SWTBotButton workingSetSelectButton = bot.button("Select...");

        Assert.assertFalse(workingSetComboBox.isEnabled());
        Assert.assertFalse(workingSetSelectButton.isEnabled());
    }

    @Test
    public void testThatCheckingAddToWorkingSetSelectButtonIsEnabled() {
        openImportProjectWizardPage();

        final SWTBotButton workingSetSelectButton = bot.button("Select...");
        final SWTBotCheckBox addToWorkingSetCheckBox = bot.checkBox(0);

        addToWorkingSetCheckBox.click();

        Assert.assertTrue(workingSetSelectButton.isEnabled());
    }

    @Test
    public void testThatAlreadyImportedProjectIsGrayedInProjectTable() throws CoreException {
        importCodenvyProject(MOCK_WORKSPACE_NAME, MOCK_PROJECT_NAME);
        openImportProjectWizardPage();

        final SWTBotTable projectTable = bot.table(0);

        for (int i = 0; i < projectTable.rowCount(); i++) {
            final SWTBotTableItem oneProjectRow = projectTable.getTableItem(i);
            Assert.assertTrue(oneProjectRow.isGrayed() == MOCK_PROJECT_NAME.equals(projectTable.cell(i, 0)));
        }
    }
}
