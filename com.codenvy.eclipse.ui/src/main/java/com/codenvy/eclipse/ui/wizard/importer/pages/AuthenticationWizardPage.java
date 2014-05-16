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
package com.codenvy.eclipse.ui.wizard.importer.pages;

import static com.codenvy.eclipse.ui.Images.WIZARD_LOGO;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;

import javax.ws.rs.ProcessingException;

import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.exceptions.AuthenticationException;
import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.services.AuthenticationService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.SecureStorageService;
import com.codenvy.eclipse.core.utils.StringHelper;
import com.codenvy.eclipse.ui.CodenvyUIPlugin;
import com.codenvy.eclipse.ui.wizard.importer.ImportProjectFromCodenvyWizard;
import com.google.common.base.Optional;

/**
 * Authentication wizard page. In this wizard page the user authenticates with the Codenvy platform by it's URL, Username and Password.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class AuthenticationWizardPage extends WizardPage implements IPageChangingListener {
    private static final String          CODENVY_URL = "https://codenvy.com";

    private Combo                        urls;
    private Combo                        usernames;
    private Text                         password;
    private Button                       storeUserCredentials;
    private final ImportWizardSharedData importWizardSharedData;

    /**
     * Constructs an instance of {@link AuthenticationWizardPage}.
     * 
     * @param importWizardSharedData data shared between wizard pages.
     * @throws NullPointerException if importWizardSharedData is {@code null}.
     */
    public AuthenticationWizardPage(ImportWizardSharedData importWizardSharedData) {
        super(AuthenticationWizardPage.class.getSimpleName());

        checkNotNull(importWizardSharedData);

        this.importWizardSharedData = importWizardSharedData;

        setTitle("Codenvy Authentication");
        setDescription("Authenticate with your Codenvy account");
        setImageDescriptor(CodenvyUIPlugin.getDefault().getImageRegistry().getDescriptor(WIZARD_LOGO));
        setPageComplete(false);
    }

    @Override
    public void createControl(Composite parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        final Composite wizardContainer = new Composite(parent, SWT.NONE);
        wizardContainer.setLayout(gridLayout);

        final Label hostLabel = new Label(wizardContainer, SWT.NONE);
        hostLabel.setText("URL:");

        urls = new Combo(wizardContainer, SWT.DROP_DOWN | SWT.BORDER | SWT.FOCUSED);
        urls.add(CODENVY_URL);
        final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
        final ServiceReference<SecureStorageService> codenvySecureStorageServiceRef =
                                                                                             context.getServiceReference(SecureStorageService.class);
        if (codenvySecureStorageServiceRef != null) {
            final SecureStorageService codenvySecureStorageService =
                                                                            context.getService(codenvySecureStorageServiceRef);
            for (final String url : codenvySecureStorageService.getURLs()) {
                urls.add(url);
            }
        }
        urls.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label usernameLabel = new Label(wizardContainer, SWT.NONE);
        usernameLabel.setText("Username:");

        usernames = new Combo(wizardContainer, SWT.DROP_DOWN | SWT.BORDER);
        usernames.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label passwordLabel = new Label(wizardContainer, SWT.NONE);
        passwordLabel.setText("Password:");

        password = new Text(wizardContainer, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        storeUserCredentials = new Button(wizardContainer, SWT.CHECK | SWT.BORDER);
        storeUserCredentials.setText("Store these user credentials in Eclipse secure storage.");
        storeUserCredentials.setSelection(true);
        storeUserCredentials.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        final PageCompleteListener pageCompleteListener = new PageCompleteListener();
        urls.addKeyListener(pageCompleteListener);
        urls.addSelectionListener(pageCompleteListener);
        usernames.addKeyListener(pageCompleteListener);
        usernames.addSelectionListener(pageCompleteListener);
        password.addKeyListener(pageCompleteListener);
        storeUserCredentials.addKeyListener(pageCompleteListener);

        final AutofillFieldsListener autofillFieldsListener = new AutofillFieldsListener();
        urls.addKeyListener(autofillFieldsListener);
        urls.addSelectionListener(autofillFieldsListener);
        usernames.addKeyListener(autofillFieldsListener);
        usernames.addSelectionListener(autofillFieldsListener);

        setControl(wizardContainer);
    }

    @Override
    public void handlePageChanging(PageChangingEvent event) {
        final ImportProjectFromCodenvyWizard wizard = (ImportProjectFromCodenvyWizard)getWizard();
        final IWizardPage targetPage = (IWizardPage)event.getTargetPage();

        if (isCurrentPage() && wizard.getProjectWizardPage().getName().equals(targetPage.getName())) {
            final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
            final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);

            if (restServiceFactoryRef != null) {
                // TODO Stéphane Daviet - 2014/05/12: Throw an exception either.
                final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);

                if (restServiceFactory != null) {

                    try {

                        final AuthenticationService authenticationService =
                                                                            restServiceFactory.newRestService(AuthenticationService.class,
                                                                                                              urls.getText());
                        final CodenvyToken token = authenticationService.login(usernames.getText(), password.getText());

                        importWizardSharedData.setCodenvyToken(Optional.fromNullable(token));
                        importWizardSharedData.setUrl(Optional.fromNullable(urls.getText()));
                        importWizardSharedData.setProjects(new ArrayList<CodenvyProject>());

                        if (storeUserCredentials.getSelection()) {
                            final ServiceReference<SecureStorageService> codenvySecureStorageServiceRef =
                                                                                                                 context.getServiceReference(SecureStorageService.class);
                            if (codenvySecureStorageServiceRef != null) {
                                final SecureStorageService codenvySecureStorageService =
                                                                                                context.getService(codenvySecureStorageServiceRef);
                                codenvySecureStorageService.storeCredentials(urls.getText(), new CodenvyCredentials(usernames.getText(),
                                                                                                                    password.getText()),
                                                                             token);
                            }
                        }

                        setErrorMessage(null);

                    } catch (final AuthenticationException e) {
                        event.doit = false;
                        setErrorMessage("Authentication failed: wrong Username and/or Password.");

                    } catch (final ProcessingException e) {
                        event.doit = false;
                        setErrorMessage("Authentication failed: wrong URL.");

                    } finally {
                        context.ungetService(restServiceFactoryRef);
                    }
                }
            }
        }
    }

    private boolean isBlankFields() {
        final boolean isUrlsBlank = StringHelper.isNullOrEmpty(urls.getText());
        final boolean isUsernameBlank = StringHelper.isNullOrEmpty(usernames.getText());
        final boolean isPasswordBlank = StringHelper.isNullOrEmpty(password.getText());

        return isUrlsBlank || isUsernameBlank || isPasswordBlank;
    }

    private SecureStorageService getCodenvySecureStorageService() {
        final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
        final ServiceReference<SecureStorageService> codenvySecureStorageServiceRef =
                                                                                             context.getServiceReference(SecureStorageService.class);
        if (codenvySecureStorageServiceRef == null) {
            return null;
        }
        return context.getService(codenvySecureStorageServiceRef);
    }

    private class PageCompleteListener implements KeyListener, SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            setPageComplete(!isBlankFields());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            setPageComplete(!isBlankFields());
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }
    }

    private class AutofillFieldsListener implements KeyListener, SelectionListener, FocusListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            autoFill();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            autoFill();
        }

        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            autoFill();
        }

        private void autoFill() {
            autoFillUsernames();
            autoFillPassword();

            setPageComplete(!isBlankFields());
        }

        private void autoFillUsernames() {
            if (!StringHelper.isNullOrEmpty(urls.getText())) {
                String currentUsername = usernames.getText();

                for (int i = usernames.getItemCount() - 1; i >= 0; i--) {
                    usernames.remove(i);
                }

                for (final String username : getCodenvySecureStorageService().getUsernamesForURL(urls.getText())) {
                    if (currentUsername.equals(username)) {
                        continue;
                    }
                    usernames.add(username);
                }
            }
        }

        private void autoFillPassword() {
            if (!StringHelper.isNullOrEmpty(usernames.getText())) {
                final String storedPassword = getCodenvySecureStorageService().getPassword(urls.getText(), usernames.getText());
                if (storedPassword != null && !storedPassword.isEmpty()) {
                    password.setText(storedPassword);
                }
            }
        }
    }
}
