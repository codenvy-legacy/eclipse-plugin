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
package com.codenvy.eclipse.ui.preferences;

import static com.codenvy.eclipse.ui.preferences.CodenvyPreferencesInitializer.REMOTE_REPOSITORIES_LOCATION_KEY_NAME;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.codenvy.eclipse.core.utils.StringHelper;
import com.codenvy.eclipse.ui.CodenvyUIPlugin;

/**
 * Preferences for the Codenvy plugin. Use a {@link FieldEditorPreferencePage}, a {@link ListEditor} and an
 * {@linkplain org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer AbstractPreferenceInitializer} to get a as simple as
 * possible storage mechanism backed on a classic {@linkplain org.eclipse.jface.preference.PreferenceStore PreferenceStore}. Remote
 * repositories locations are stored as a single {@link String} joined, and then splitted.
 * 
 * @see CodenvyPreferencesInitializer
 * @author Stéphane Daviet
 */
public final class CodenvyPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    @Override
    public void init(IWorkbench workbench) {
        setTitle("Codenvy Preferences");
        setDescription("Manage here the URL of remote Codenvy repositories you want to keep in memory for type assist when you create a new project.\n"
                       + "Credentials for those repositories are managed through the Secure Storage preferences page.");
        setPreferenceStore(CodenvyUIPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        final Composite parent = getFieldEditorParent();
        final ListEditor locations = new ListEditor(REMOTE_REPOSITORIES_LOCATION_KEY_NAME, "Remote repositories locations:", parent) {
            @Override
            protected String getNewInputObject() {
                // Give the user an input dialog to enter its new location
                final InputDialog dialog = new InputDialog(getShell(), "Add a remote Codenvy repository location",
                                                           "Enter the URL of the repository", null, null);
                if (dialog.open() != Window.OK) {
                    return null;
                }

                final String newRepositoryLocation = dialog.getValue();
                if (StringHelper.isNullOrEmpty(newRepositoryLocation)) {
                    return null;
                }

                return newRepositoryLocation;
            }

            @Override
            protected String[] parseString(String stringList) {
                return CodenvyPreferencesInitializer.parseString(stringList);
            }

            @Override
            protected String createList(String[] items) {
                return CodenvyPreferencesInitializer.createList(items);
            }
        };

        locations.loadDefault();
        addField(locations);
    }
}
