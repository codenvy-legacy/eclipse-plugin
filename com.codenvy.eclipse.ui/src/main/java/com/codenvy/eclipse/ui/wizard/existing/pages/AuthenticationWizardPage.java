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
package com.codenvy.eclipse.ui.wizard.existing.pages;

import static com.codenvy.eclipse.ui.utils.ImageConstants.WIZARD_LOGO_KEY;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;

import javax.ws.rs.ProcessingException;

import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.AuthenticationService;
import com.codenvy.eclipse.core.RestServiceFactory;
import com.codenvy.eclipse.core.exception.AuthenticationException;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.Project;
import com.codenvy.eclipse.core.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.ui.Activator;
import com.codenvy.eclipse.ui.wizard.existing.ImportProjectFromCodenvyWizard;
import com.google.common.base.Optional;

/**
 * Authentication wizard page. In this wizard page the user authenticates with the Codenvy platform by it's URL, Username and Password.
 * 
 * @author Kevin Pollet
 */
public class AuthenticationWizardPage extends WizardPage implements IPageChangingListener {
    private Text                         url;
    private Text                         username;
    private Text                         password;
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
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(WIZARD_LOGO_KEY));
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

        url = new Text(wizardContainer, SWT.SINGLE | SWT.BORDER | SWT.FOCUSED);
        url.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label usernameLabel = new Label(wizardContainer, SWT.NONE);
        usernameLabel.setText("Username:");

        username = new Text(wizardContainer, SWT.SINGLE | SWT.BORDER);
        username.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label passwordLabel = new Label(wizardContainer, SWT.NONE);
        passwordLabel.setText("Password:");

        password = new Text(wizardContainer, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final KeyListener isPageCompleteKeyListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                final boolean isHostBlank = isNullOrEmptyString(url.getText());
                final boolean isUsernameBlank = isNullOrEmptyString(username.getText());
                final boolean isPasswordBlank = isNullOrEmptyString(password.getText());

                setPageComplete(!isHostBlank && !isUsernameBlank && !isPasswordBlank);
            }
        };

        url.addKeyListener(isPageCompleteKeyListener);
        username.addKeyListener(isPageCompleteKeyListener);
        password.addKeyListener(isPageCompleteKeyListener);

        setControl(wizardContainer);
    }

    @Override
    public void handlePageChanging(PageChangingEvent event) {
        final ImportProjectFromCodenvyWizard wizard = (ImportProjectFromCodenvyWizard)getWizard();
        final IWizardPage targetPage = (IWizardPage)event.getTargetPage();

        if (isCurrentPage() && wizard.getWorkspaceWizardPage().getName().equals(targetPage.getName())) {
            final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
            final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);

            if (restServiceFactoryRef != null) {
                final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);

                if (restServiceFactory != null) {

                    try {

                        final AuthenticationService authenticationService =
                                                                            restServiceFactory.newRestService(AuthenticationService.class,
                                                                                                              url.getText());
                        final CodenvyToken token = authenticationService.login(username.getText(), password.getText());

                        importWizardSharedData.setCodenvyToken(Optional.fromNullable(token));
                        importWizardSharedData.setUrl(Optional.fromNullable(url.getText()));
                        importWizardSharedData.setWorkspaceRef(Optional.<WorkspaceRef> absent());
                        importWizardSharedData.setProjects(new ArrayList<Project>());

                        setErrorMessage(null);

                    } catch (AuthenticationException e) {
                        event.doit = false;
                        setErrorMessage("Authentication failed: wrong Username and/or Password");

                    } catch (ProcessingException e) {
                        event.doit = false;
                        setErrorMessage("Authentication failed: wrong URL");

                    } finally {
                        context.ungetService(restServiceFactoryRef);
                    }
                }
            }
        }
    }

    /**
     * Tests that the given string is {@code null} or empty. A string containing only whitespace is assumed to be empty.
     * 
     * @param string the sting to test.
     * @return {@code true} if the given string is {@code null} or empty, {@code false} otherwise.
     */
    private boolean isNullOrEmptyString(String string) {
        return string == null || string.trim().isEmpty();
    }
}
