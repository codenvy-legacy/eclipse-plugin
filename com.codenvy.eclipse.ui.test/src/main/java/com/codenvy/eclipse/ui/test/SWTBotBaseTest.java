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
package com.codenvy.eclipse.ui.test;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.junit.BeforeClass;

/**
 * SWTBot base test class.
 * 
 * @author Kevin Pollet
 */
public class SWTBotBaseTest {
    public static final String          RESOURCE_PERSPECTIVE_ID = "org.eclipse.ui.resourcePerspective";
    public static final SWTWorkbenchBot bot                  = new SWTWorkbenchBot();

    @BeforeClass
    public static void beforeClass() {
        // turn off automatic building by default
        final SWTBotShell shell = openEclipsePreferences();
        shell.bot()
             .tree()
             .expandNode("General")
             .select("Workspace");

        final SWTBotCheckBox buildAuto = bot.checkBox("Build automatically");
        if (buildAuto.isChecked()) {
            buildAuto.click();
        }

        shell.bot()
             .button("Apply")
             .click();

        shell.bot()
             .button("OK")
             .click();
    }

    public SWTBotShell openAuthenticationWizardPage() {
        bot.menu("File")
              .menu("Import...")
              .click();

        final SWTBotShell shell = bot.shell("Import");

        shell.bot()
             .tree()
             .expandNode("Codenvy")
             .select("Existing Codenvy Projects");

        shell.bot()
             .button("Next >")
             .click();

        return shell;
    }

    public void openNavigatorView() {
        bot.menu("Window")
              .menu("Show View")
              .menu("Other...")
              .click();

        bot.waitUntilWidgetAppears(shellIsActive("Show View"));

        final SWTBotShell shell = bot.shell("Show View");

        shell.bot()
             .tree()
             .expandNode("General")
             .getNode("Navigator")
             .select();

        shell.bot()
             .button("OK")
             .click();
    }

    public SWTBotShell openProjectWizardPage() {
        bot.menu("File")
              .menu("Import...")
              .click();

        final SWTBotShell shell = bot.shell("Import");

        shell.bot()
             .tree()
             .expandNode("Codenvy")
             .select("Existing Codenvy Projects");

        shell.bot()
             .button("Next >")
             .click();

        shell.bot()
             .comboBox(0)
             .setText("http://localhost:8080");

        shell.bot()
             .comboBox(1)
             .typeText("johndoe");

        shell.bot()
             .text(0)
             .typeText("secret");

        shell.bot()
             .button("Next >")
             .click();

        shell.bot()
             .waitUntil(new ComboHasOptions(bot.comboBox(0)));

        shell.bot()
             .waitUntil(new TableHasRows(bot.table(0)));

        return shell;
    }

    public void importProject(String workspaceName, String projectName) {
        openProjectWizardPage();

        final SWTBotCombo workspaceCombo = bot.comboBox(0);
        workspaceCombo.setSelection(workspaceName);

        final SWTBotTable projectTable = bot.table(0);
        bot.waitUntil(new TableHasRows(projectTable));

        for (int i = 0; i < projectTable.rowCount(); i++) {
            if (projectTable.cell(i, 0).equals(projectName)) {
                projectTable.getTableItem(i)
                            .check();
            }
        }

        bot.table(0)
              .getTableItem(projectName)
              .check();

        bot.button("Finish")
              .click();
    }

    public void deleteProject(String projectName) throws CoreException {
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.exists()) {
            project.delete(true, new NullProgressMonitor());
        }
    }

    private static SWTBotShell openEclipsePreferences() {
        final IWorkbench workbench = PlatformUI.getWorkbench();

        bot.perspectiveById(RESOURCE_PERSPECTIVE_ID)
              .activate();

        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
                final IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
                ActionFactory.PREFERENCES.create(workbenchWindow).run();
            }
        });

        return bot.shell("Preferences");
    }

    class TableHasRows extends DefaultCondition {
        private final SWTBotTable table;

        public TableHasRows(SWTBotTable table) {
            this.table = table;
        }

        @Override
        public boolean test() throws Exception {
            return table.rowCount() > 0;
        }

        @Override
        public String getFailureMessage() {
            return "Timeout waiting for table data to be loaded";
        }
    }

    class ComboHasOptions extends DefaultCondition {
        private final SWTBotCombo combo;

        public ComboHasOptions(SWTBotCombo combo) {
            this.combo = combo;
        }

        @Override
        public boolean test() throws Exception {
            return combo.itemCount() > 0;
        }

        @Override
        public String getFailureMessage() {
            return "Timeout waiting for combobox data to be loaded";
        }
    }
}
