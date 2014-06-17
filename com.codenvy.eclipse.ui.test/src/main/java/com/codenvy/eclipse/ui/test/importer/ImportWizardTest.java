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
package com.codenvy.eclipse.ui.test.importer;

import static com.codenvy.eclipse.client.MockConstants.MOCK_PROJECT_NAME;
import static com.codenvy.eclipse.client.MockConstants.MOCK_WORKSPACE_NAME;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.codenvy.eclipse.ui.test.SWTBotBaseTest;

/**
 * Import wizard tests.
 * 
 * @author Kevin Pollet
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ImportWizardTest extends SWTBotBaseTest {
    @Test
    public void testThatWizardIsInNewProjectDialog() {
        bot.menu("File")
              .menu("New")
              .menu("Project...")
              .click();

        final SWTBotShell shell = bot.shell("New Project");
        shell.activate();

        bot.tree()
              .expandNode("Codenvy")
              .select("Projects from Codenvy");

        shell.close();
    }

    @Test
    public void testThatWizardIsInOtherProjectDialog() {
        bot.menu("File")
              .menu("New")
              .menu("Other...")
              .click();

        final SWTBotShell shell = bot.shell("New");
        shell.activate();

        bot.tree()
              .expandNode("Codenvy")
              .select("Projects from Codenvy");

        shell.close();
    }

    @Test
    public void testThatWizardIsInImportDialog() {
        bot.menu("File")
              .menu("Import...")
              .click();

        final SWTBotShell shell = bot.shell("Import");
        shell.activate();

        bot.tree()
              .expandNode("Codenvy")
              .select("Existing Codenvy Projects");

        shell.close();
    }

    @Test
    public void testThatImportedProjectIsAvailableInProjectExplorerView() throws CoreException {
        importProject(MOCK_WORKSPACE_NAME, MOCK_PROJECT_NAME);
        openNavigatorView();

        final SWTBotView navigatorView = bot.viewByTitle("Navigator");
        navigatorView.setFocus();

        final SWTBotTree tree = navigatorView.bot().tree();
        final SWTBotTreeItem treeNode = tree.getTreeItem(MOCK_PROJECT_NAME);

        Assert.assertTrue(treeNode.getText().equals(MOCK_PROJECT_NAME));
    }
}
