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
package com.codenvy.eclipse.ui.widgets;

import static com.codenvy.eclipse.core.utils.StringHelper.isNullOrEmpty;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog used by the user to provide it's credentials.
 * 
 * @author Kevin Pollet
 */
public class CredentialsDialog extends TitleAreaDialog {
    private Text    passwordText;
    private Button  storeUserCredentialsButton;
    private String  username;
    private String  password;
    private boolean storeUserCredentials;

    /**
     * Constructs an instance of {@link CredentialsDialog}.
     * 
     * @param username the username.
     * @param parentShell the parent {@link Shell}.
     */
    public CredentialsDialog(String username, Shell parentShell) {
        super(parentShell);

        this.username = username;
    }

    @Override
    public void create() {
        super.create();

        setTitle("Authentication");
        setMessage("Authenticate with the Codenvy platform");

        getButton(OK_ID).setEnabled(false);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite contents = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        contents.setLayout(new GridLayout(2, false));

        final Label usernameLabel = new Label(contents, SWT.NONE);
        usernameLabel.setText("Username:");

        final Text usernameText = new Text(contents, SWT.BORDER | SWT.READ_ONLY);
        usernameText.setText(username);
        usernameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label passwordLabel = new Label(contents, SWT.NONE);
        passwordLabel.setText("Password:");

        passwordText = new Text(contents, SWT.BORDER | SWT.PASSWORD);
        passwordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        passwordText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                getButton(OK_ID).setEnabled(!isNullOrEmpty(passwordText.getText()));
            }
        });

        storeUserCredentialsButton = new Button(contents, SWT.CHECK);
        storeUserCredentialsButton.setText("Store these user credentials in Eclipse secure storage.");
        storeUserCredentialsButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        return contents;
    }

    @Override
    protected void okPressed() {
        this.password = passwordText.getText();
        this.storeUserCredentials = storeUserCredentialsButton.getSelection();
        super.okPressed();
    }

    /**
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return
     */
    public boolean isStoreUserCredentials() {
        return storeUserCredentials;
    }

    /**
     * @return
     */
    public String getUsername() {
        return username;
    }
}
