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
package com.codenvy.eclipse.ui.test.exporter;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.codenvy.eclipse.ui.test.SWTBotBaseTest;

/**
 * Export wizard tests.
 *
 * @author Kevin Pollet
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ExportWizardTest extends SWTBotBaseTest {
    @Test
    public void testThatExportProjectToCodenvyWizardIsInExportProjectDialog() {
        bot.menu("File").menu("Export...").click();

        bot.shell("Export").activate();
        bot.tree().expandNode("Codenvy").select("Project to Codenvy");
    }
}
