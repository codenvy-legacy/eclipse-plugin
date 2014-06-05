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

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withMnemonic;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;

/**
 * SWTBot base test class.
 * 
 * @author Kevin Pollet
 */
public class SWTBotBaseTest {
    public static final SWTWorkbenchBot bot = new SWTWorkbenchBot();

    public SWTBotBaseTest() {
        final IEclipsePreferences resourcesPreferences = InstanceScope.INSTANCE.getNode(ResourcesPlugin.PI_RESOURCES);
        resourcesPreferences.putBoolean(ResourcesPlugin.PREF_AUTO_BUILDING, false);
        resourcesPreferences.putBoolean(ResourcesPlugin.PREF_AUTO_REFRESH, false);
    }

    @SuppressWarnings("unchecked")
    @After
    public void clearWorkspace() throws Exception {
        openNavigatorView();

        final SWTBotView navigatorView = bot.viewByTitle("Navigator");
        navigatorView.setFocus();

        final SWTBotTree tree = navigatorView.bot().tree();

        ArrayList<String> projectsToDelete = new ArrayList<>();
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            if ("RemoteSystemsTempFiles".equals(project.getName())) {
                continue;
            }
            projectsToDelete.add(project.getName());
        }
        if (!projectsToDelete.isEmpty()) {
            tree.select(projectsToDelete.toArray(new String[projectsToDelete.size()]));

            bot.menu("Edit").menu("Delete").click();

            // the project deletion confirmation dialog
            SWTBotShell shell = bot.shell("Delete Resources");
            shell.activate();
            bot.checkBox("Delete project contents on disk (cannot be undone)").select();
            bot.button("OK").click();
            try {
                bot.waitUntilWidgetAppears(Conditions.waitForWidget(allOf(widgetOfType(Button.class), withMnemonic("Continue"))));
                bot.button("Continue").click();
            } catch (WidgetNotFoundException e) {
                // Nothing to do, no confirmation page, just go on.
            }
            bot.waitUntil(Conditions.shellCloses(shell));
        }
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
