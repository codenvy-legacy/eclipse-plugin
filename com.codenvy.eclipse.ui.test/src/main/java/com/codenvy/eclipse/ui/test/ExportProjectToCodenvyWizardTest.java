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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The export project wizard test.
 * 
 * @author Kevin Pollet
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ExportProjectToCodenvyWizardTest {
    @Test
    public void testThatExportProjectToCodenvyWizardIsInExportProjectDialog() {
        final SWTWorkbenchBot bot = new SWTWorkbenchBot();
        bot.menu("File").menu("Export...").click();

        final SWTBotShell shell = bot.shell("Export");
        shell.activate();

        bot.tree().expandNode("Codenvy").select("Codenvy project");
        shell.close();
    }
}
