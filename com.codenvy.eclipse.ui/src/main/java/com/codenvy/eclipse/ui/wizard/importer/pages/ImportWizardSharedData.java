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

import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.Project;
import com.codenvy.eclipse.core.model.Workspace.WorkspaceRef;
import com.google.common.base.Optional;

/**
 * Class used to share data between project pages.
 * 
 * @author Kevin Pollet
 */
public final class ImportWizardSharedData {
    private Optional<String>       url;
    private Optional<CodenvyToken> codenvyToken;
    private Optional<WorkspaceRef> workspaceRef;
    private List<Project>          projects;

    /**
     * Default constructor.
     */
    public ImportWizardSharedData() {
        this.url = Optional.absent();
        this.codenvyToken = Optional.absent();
        this.workspaceRef = Optional.absent();
        this.projects = new ArrayList<>();
    }

    /**
     * Returns the Codenvy platform url entered in step 1 of the wizard.
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
     * Returns the Codenvy authentication token negotiated in step 1 of the wizard.
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
     * Returns the Codenvy workspace reference checked by the user in step 2.
     * 
     * @return the checked Codenvy workspace reference never {@code null}.
     */
    public Optional<WorkspaceRef> getWorkspaceRef() {
        return workspaceRef;
    }

    /**
     * Defines the Codenvy workspace reference checked by the user.
     * 
     * @param workspaceRef the Codenvy workspace reference.
     * @throws NullPointerException if workspaceRef parameter is {@code null}.
     */
    public void setWorkspaceRef(Optional<WorkspaceRef> workspaceRef) {
        checkNotNull(workspaceRef);

        this.workspaceRef = workspaceRef;
    }

    /**
     * Returns the Codenvy projects checked by the user in step 3.
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
}
