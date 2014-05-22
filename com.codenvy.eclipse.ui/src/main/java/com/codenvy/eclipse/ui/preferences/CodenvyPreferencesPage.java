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
package com.codenvy.eclipse.ui.preferences;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.codenvy.eclipse.ui.CodenvyUIPlugin;

/**
 * Preferences for the Codenvy plugin. Use a {@link FieldEditorPreferencePage}, a {@link ListEditor} and an
 * {@link AbstractPreferenceInitializer} to get a as simple as possible storage mechanism backed on a classic {@link PreferenceStore}.
 * Remote repositories locations are stored as a single {@link String} joined, and then splitted.
 * 
 * @see CodenvyPreferencesInitializer
 * @author Stéphane Daviet
 */
public class CodenvyPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    @Override
    public void init(IWorkbench workbench) {
        setTitle("Codenvy Preferences");
        setDescription("Manage here the URL of remote Codenvy repositories you want to keep in memory for type assist when you create a new project.\n"
                       + "Credentials for those repositories are managed through the Secure Storage preferences page.");
        setPreferenceStore(CodenvyUIPlugin.getDefault()
                                          .getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        ListEditor locations =
                               new ListEditor(CodenvyPreferencesInitializer.REMOTE_REPOSITORIES_LOCATION_KEY_NAME,
                                              "Remote repositories locations:", parent) {
                                   @Override
                                   protected String getNewInputObject() {
                                       // Give the user an input dialog to enter its new location
                                       InputDialog dialog =
                                                            new InputDialog(getShell(), "Add a remote Codenvy repository location",
                                                                            "Enter the URL of the repository", null, null) {
                                                                @Override
                                                                protected Control createDialogArea(Composite parent) {
                                                                    Control control = super.createDialogArea(parent);
                                                                    return control;
                                                                }
                                                            };

                                       dialog.open();
                                       if (dialog.getReturnCode() != Window.OK) {
                                           return null;
                                       }
                                       String newRepositoryLocation = dialog.getValue();
                                       if ("".equals(newRepositoryLocation)) {
                                           return null; //$NON-NLS-1$
                                       }

                                       return newRepositoryLocation;
                                   }

                                   @Override
                                   protected String[] parseString(String stringList) {
                                       // Delegate to util method
                                       return CodenvyPreferencesInitializer.parseString(stringList);
                                   }

                                   @Override
                                   protected String createList(String[] items) {
                                       // Delegate to util method
                                       return CodenvyPreferencesInitializer.createList(items);
                                   }
                               };
        locations.loadDefault();
        addField(locations);
    }
}
