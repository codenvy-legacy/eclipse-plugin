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

import static org.eclipse.core.resources.ResourcesPlugin.PI_RESOURCES;
import static org.eclipse.core.resources.ResourcesPlugin.PREF_AUTO_BUILDING;
import static org.eclipse.core.resources.ResourcesPlugin.PREF_AUTO_REFRESH;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;

import com.codenvy.eclipse.core.CodenvyPlugin;

/**
 * SWTBot base test class.
 * 
 * @author Kevin Pollet
 */
public class SWTBotBaseTest {
    public static final SWTWorkbenchBot bot = new SWTWorkbenchBot();

    public SWTBotBaseTest() {
        final IEclipsePreferences resourcesPreferences = InstanceScope.INSTANCE.getNode(PI_RESOURCES);
        resourcesPreferences.putBoolean(PREF_AUTO_BUILDING, false);
        resourcesPreferences.putBoolean(PREF_AUTO_REFRESH, false);
    }

    @Before
    public void baseBeforeTest() {
        UIThreadRunnable.syncExec(new VoidResult() {
            @Override
            public void run() {
                final Shell eclipseShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                eclipseShell.forceFocus();
            }
        });

        try {

            final SWTBotView welcomeView = bot.viewByTitle("Welcome");
            welcomeView.close();

        } catch (WidgetNotFoundException e) {
            // ignore the exception
        }
    }

    @After
    public void baseAfterTest() {
        closeAllShells();
        deleteAllProjects();
    }

    public void closeAllShells() {
        for (SWTBotShell shell : bot.shells()) {
            if (shell.isOpen() && !isEclipseShell(shell)) {
                shell.close();
            }
        }
    }

    public void deleteAllProjects() {
        final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (IProject oneProject : projects) {
            deleteProject(oneProject.getName());
        }
    }

    public void deleteProject(String projectName) {
        final SWTBotView projectExplorerView = bot.viewByTitle("Project Explorer");
        projectExplorerView.show();
        projectExplorerView.bot().tree().select(projectName);

        bot.menu("Edit").menu("Delete").click();

        // the project deletion confirmation dialog
        final SWTBotShell shell = bot.shell("Delete Resources").activate();

        bot.checkBox("Delete project contents on disk (cannot be undone)").select();
        bot.button("OK").click();

        bot.waitUntil(shellCloses(shell));
    }

    public SWTBotShell openCodenvyImportWizard() {
        bot.menu("File").menu("Import...").click();
        bot.waitUntil(shellIsActive("Import"));

        bot.tree().expandNode("Codenvy").select("Existing Codenvy Projects");
        bot.button("Next >").click();
        bot.waitUntil(shellIsActive("Import Codenvy Projects"));

        return bot.shell("Import Codenvy Projects").activate();
    }

    public SWTBotShell openCodenvyExportWizard() {
        bot.menu("File").menu("Export...").click();
        bot.waitUntil(shellIsActive("Export"));

        bot.tree().expandNode("Codenvy").select("Codenvy project");
        bot.button("Next >").click();
        bot.waitUntil(shellIsActive("Export Projects to Codenvy"));

        return bot.shell("Export Projects to Codenvy").activate();
    }

    public SWTBotShell openImportProjectWizardPage() {
        final SWTBotShell shell = openCodenvyImportWizard();

        bot.comboBox(0).setText("http://localhost:8080");
        bot.comboBox(1).typeText("johndoe");
        bot.text(0).typeText("secret");
        bot.button("Next >").click();

        bot.waitUntil(new ComboHasOptions(bot.comboBox(0)));
        bot.waitUntil(new TableHasRows(bot.table(0)));

        return shell;
    }

    public void importCodenvyProject(String workspaceName, String projectName) {
        final SWTBotShell shell = openImportProjectWizardPage();

        final SWTBotCombo workspaceCombo = bot.comboBox(0);
        workspaceCombo.setSelection(workspaceName);

        final SWTBotTable projectTable = bot.table(0);
        bot.waitUntil(new TableHasRows(projectTable));

        for (int i = 0; i < projectTable.rowCount(); i++) {
            if (projectTable.cell(i, 0).equals(projectName)) {
                projectTable.getTableItem(i).check();
            }
        }

        bot.table(0).getTableItem(projectName).check();
        bot.button("Finish").click();

        bot.waitUntil(shellCloses(shell));
        try {

            Job.getJobManager().join(CodenvyPlugin.FAMILY_CODENVY, null);

        } catch (OperationCanceledException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isEclipseShell(final SWTBotShell shell) {
        return UIThreadRunnable.syncExec(new BoolResult() {
            public Boolean run() {
                return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                 .getShell() == shell.widget;
            }
        });
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
