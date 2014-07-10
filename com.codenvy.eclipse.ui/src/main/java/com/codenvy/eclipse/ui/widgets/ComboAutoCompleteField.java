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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.widgets.Combo;

/**
 * Inspired by {@link org.eclipse.equinox.internal.p2.ui.dialogs.ComboAutoCompleteField} with a simpler pattern matcher. Simple "widget"
 * that enhances a {@link Combo} with a {@link ContentProposalAdapter}.
 * 
 * @author Stéphane Daviet
 */
public final class ComboAutoCompleteField {
    private final ContentProposalAdapter adapter;
    private final Combo                  combo;
    private String[]                     proposalStrings;

    /**
     * Constructs an instance of {@link ComboAutoCompleteField}
     * 
     * @param combo the {@link Combo} to auto-complete.
     * @throws NullPointerException if combo parameter is {@code null}
     */
    public ComboAutoCompleteField(Combo combo) {
        this.combo = checkNotNull(combo);
        this.adapter = new ContentProposalAdapter(combo, new ComboContentAdapter(), getProposalProvider(), null, null);
        this.adapter.setPropagateKeys(true);
        this.adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
        this.proposalStrings = null;
    }

    /**
     * Get a way to set your own completion proposals, for instance after getting a {@linkplain org.eclipse.core.runtime.jobs.Job Job}
     * processing result.
     * 
     * @param proposals the proposals to add instead those of the {@link Combo}.
     */
    public void setProposalStrings(String[] proposals) {
        proposalStrings = proposals;
    }

    private String[] getStringItems() {
        if (proposalStrings == null)
            return combo.getItems();
        return proposalStrings;
    }

    private IContentProposalProvider getProposalProvider() {
        return new IContentProposalProvider() {
            @Override
            public IContentProposal[] getProposals(String contents, int position) {
                String[] items = getStringItems();
                if (contents.length() == 0 || items.length == 0)
                    return new IContentProposal[0];
                Pattern matcher = Pattern.compile(Pattern.quote(contents) + ".*");
                ArrayList<String> matches = new ArrayList<String>();
                for (int i = 0; i < items.length; i++) {
                    if (matcher.matcher(items[i]).find()) {
                        matches.add(items[i]);
                    }
                }

                // We don't want to autoactivate if the only proposal exactly matches
                // what is in the combo. This prevents the popup from
                // opening when the user is merely scrolling through the combo values or
                // has accepted a combo value.
                if (matches.size() == 1 && matches.get(0).equals(combo.getText()))
                    return new IContentProposal[0];

                if (matches.isEmpty())
                    return new IContentProposal[0];

                // Make the proposals
                IContentProposal[] proposals = new IContentProposal[matches.size()];
                for (int i = 0; i < matches.size(); i++) {
                    final String proposal = matches.get(i);
                    proposals[i] = new IContentProposal() {

                        @Override
                        public String getContent() {
                            return proposal;
                        }

                        @Override
                        public int getCursorPosition() {
                            return proposal.length();
                        }

                        @Override
                        public String getDescription() {
                            return null;
                        }

                        @Override
                        public String getLabel() {
                            return null;
                        }
                    };
                }
                return proposals;
            }
        };
    }
}
