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
        bot.menu("File").menu("New").menu("Project...").click();

        bot.shell("New Project").activate();
        bot.tree().expandNode("Codenvy").select("Projects from Codenvy");
    }

    @Test
    public void testThatWizardIsInOtherProjectDialog() {
        bot.menu("File").menu("New").menu("Other...").click();

        bot.shell("New").activate();
        bot.tree().expandNode("Codenvy").select("Projects from Codenvy");
    }

    @Test
    public void testThatWizardIsInImportDialog() {
        bot.menu("File").menu("Import...").click();

        bot.shell("Import").activate();
        bot.tree().expandNode("Codenvy").select("Existing Codenvy Projects");
    }

    @Test
    public void testThatImportedProjectIsAvailableInProjectExplorerView() throws CoreException {
        importCodenvyProject(MOCK_WORKSPACE_NAME, MOCK_PROJECT_NAME);

        final SWTBotView projectExplorerView = bot.viewByTitle("Project Explorer");
        projectExplorerView.show();
        projectExplorerView.bot().tree().getTreeItem(MOCK_PROJECT_NAME);
    }
}
