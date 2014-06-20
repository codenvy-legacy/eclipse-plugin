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
