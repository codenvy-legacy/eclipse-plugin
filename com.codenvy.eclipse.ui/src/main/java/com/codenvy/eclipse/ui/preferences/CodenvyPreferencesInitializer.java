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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.codenvy.eclipse.ui.CodenvyUIPlugin;
import com.google.common.base.Joiner;

/**
 * This initializer setup all the default Codenvy preferences if needed.
 * 
 * @author Stéphane Daviet
 */
public final class CodenvyPreferencesInitializer extends AbstractPreferenceInitializer {
    /**
     * Separator used when storing a list as a concatenated {@link String}.
     */
    private static final String LOCATION_SEPARATOR                    = ";";

    /**
     * Key where the locations of remote repositories are stored in the {@linkplain org.eclipse.jface.preference.PreferenceStore
     * PreferenceStore} of the plugin.
     */
    public final static String  REMOTE_REPOSITORIES_LOCATION_KEY_NAME = "remoteRepositoriesLocation";

    /**
     * Default location.
     */
    public final static String  DEFAULT_LOCATION                      = "https://codenvy.com";

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore codenvyUIPreferenceStore = CodenvyUIPlugin.getDefault().getPreferenceStore();
        codenvyUIPreferenceStore.setDefault(REMOTE_REPOSITORIES_LOCATION_KEY_NAME, DEFAULT_LOCATION);
    }

    /**
     * Util method to get an {@linkplain java.lang.reflect.Array Array} of {@link String} from a value. Needed for values where a list is
     * stored as a joined {@link String}.
     * 
     * @param stringList the concatenated {@link String} representing a list of values.
     * @return the associated {@linkplain java.lang.reflect.Array Array} of values.
     * @throws NullPointerException if stringList parameter is {@code null}.
     */
    public static String[] parseString(String stringList) {
        return checkNotNull(stringList).split(LOCATION_SEPARATOR);
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
