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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IWorkingSet;

import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.Project;
import com.google.common.base.Optional;

/**
 * Class used to share data between project pages.
 * 
 * @author Kevin Pollet
 */
public final class ImportWizardSharedData {
    private Optional<String>       url;
    private Optional<CodenvyToken> codenvyToken;
    private List<Project>          projects;
    private Optional<IWorkingSet>  workingSet;

    /**
     * Default constructor.
     */
    public ImportWizardSharedData() {
        this.url = Optional.absent();
        this.codenvyToken = Optional.absent();
        this.projects = new ArrayList<>();
        this.workingSet = Optional.absent();
    }

    /**
     * Returns the Codenvy platform url entered.
     * 
     * @return the Codenvy platform url never {@code null}.
     */
    public Optional<String> getUrl() {
        return url;
    }

    /**
     * Defines the Codenvy platform url.
     * 
     * @param url the Codenvy platform url
     * @throws NullPointerException if url parameter is {@code null}
     */
    public void setUrl(Optional<String> url) {
        checkNotNull(codenvyToken);

        this.url = url;
    }

    /**
     * Returns the Codenvy authentication token negotiated.
     * 
     * @return the Codenvy authentication token never {@code null}.
     */
    public Optional<CodenvyToken> getCodenvyToken() {
        return codenvyToken;
    }

    /**
     * Defines the Codenvy authentication token.
     * 
     * @param codenvyToken the the Codenvy authentication token.
     * @throws NullPointerException if codenvyToken parameter is {@code null}.
     */
    public void setCodenvyToken(Optional<CodenvyToken> codenvyToken) {
        checkNotNull(codenvyToken);

        this.codenvyToken = codenvyToken;
    }

    /**
     * Returns the Codenvy projects checked by the user.
     * 
     * @return the checked Codenvy projects never {@code null}.
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * Defines the Codenvy projects checked by the user.
     * 
     * @param projects the Codenvy projects.
     * @throws NullPointerException if projects parameter is {@code null}.
     */
    public void setProjects(List<Project> projects) {
        checkNotNull(projects);

        this.projects = projects;
    }

    /**
     * Returns the working set selected by the user.
     * 
     * @return the selected working set never {@code null}.
     */
    public Optional<IWorkingSet> getWorkingSet() {
        return workingSet;
    }

    /**
     * Defines the working set selected by the user.
     * 
     * @param workingSet the selected working set.
     * @throws NullPointerException if workingSet parameter is {@code null}.
     */
    public void setWorkingSet(Optional<IWorkingSet> workingSet) {
        checkNotNull(workingSet);

        this.workingSet = workingSet;
    }
}
