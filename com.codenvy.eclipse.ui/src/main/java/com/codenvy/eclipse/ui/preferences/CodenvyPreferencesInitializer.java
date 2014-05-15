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

import java.lang.reflect.Array;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

import com.codenvy.eclipse.ui.CodenvyUIPlugin;
import com.google.common.base.Joiner;

/**
 * This initializer setup all the default Codenvy preferences if needed.
 * 
 * @author Stéphane Daviet
 */
public class CodenvyPreferencesInitializer extends AbstractPreferenceInitializer {
    /**
     * Separator used when storing a list as a concatenated {@link String}.
     */
    private static final String LOCATION_SEPARATOR                    = ";";

    /**
     * Key where the locations of remote repositories are stored in the {@link PreferenceStore} of the plugin.
     */
    public final static String  REMOTE_REPOSITORIES_LOCATION_KEY_NAME = "remoteRepositoriesLocation";

    /**
     * Default location.
     */
    public final static String  DEFAULT_LOCATION                      = "https://codenvy.com";

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore codenvyUIPreferenceStore = CodenvyUIPlugin.getDefault()
                                                                   .getPreferenceStore();
        codenvyUIPreferenceStore
                                .setDefault(REMOTE_REPOSITORIES_LOCATION_KEY_NAME, DEFAULT_LOCATION);
    }

    /**
     * Util method to get an {@link Array} of {@link String} from a value. Needed for values where a list is stored as a joined
     * {@link String}.
     * 
     * @param stringList the concatenated {@link String} representing a list of values.
     * @return the associated {@link Array} of values.
     */
    public static String[] parseString(String stringList) {
        return stringList.split(LOCATION_SEPARATOR);
    }

    /**
     * Util method to get a {@link String} from a list of values. Needed for values where a list is stored as a joined {@link String}.
     * 
     * @param items the list of values.
     * @return the concatenated {@link String} representing a list of values
     */
    public static String createList(String[] items) {
        return Joiner.on(LOCATION_SEPARATOR).join(items);
    }
}
