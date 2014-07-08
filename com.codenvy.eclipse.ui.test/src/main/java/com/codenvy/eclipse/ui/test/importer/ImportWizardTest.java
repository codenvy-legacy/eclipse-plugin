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
package com.codenvy.eclipse.ui.test.importer;

import static com.codenvy.client.MockConstants.MOCK_PROJECT_NAME;
import static com.codenvy.client.MockConstants.MOCK_WORKSPACE_NAME;

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
    public void testThatImportedProjectIsAvailableInPackageExplorerView() throws CoreException {
        importCodenvyProject(MOCK_WORKSPACE_NAME, MOCK_PROJECT_NAME);

        final SWTBotView packageExplorerView = bot.viewByTitle("Package Explorer");
        packageExplorerView.show();
        packageExplorerView.bot().tree().getTreeItem(MOCK_PROJECT_NAME);
    }
}
