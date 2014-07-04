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

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.codenvy.eclipse.ui.test.SWTBotBaseTest;

/**
 * Authentication page tests.
 * 
 * @author Kevin Pollet
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class AuthenticationWizardPageTest extends SWTBotBaseTest {
    @Before
    public void beforeTest() {
        openCodenvyImportWizard();
    }

    @Test
    public void testThatAllFieldsMustBeFilledToAuthenticate() {
        final SWTBotButton nextButton = bot.button("Next >");

        bot.comboBox(0).setText("http://localhost:8080");

        Assert.assertFalse(nextButton.isEnabled());

        bot.comboBox(1).typeText("johndoe");

        Assert.assertFalse(nextButton.isEnabled());

        bot.text(0).typeText("secret");

        Assert.assertTrue(nextButton.isEnabled());
    }

    @Test
    public void testThatStoreCredentialsIsCheckedByDefault() {
        final SWTBotCheckBox storeCrendentialsCheckBox = bot.checkBox(0);

        Assert.assertTrue(storeCrendentialsCheckBox.isChecked());
    }
}
