/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.test.commons;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IStatus;
import org.junit.rules.ExternalResource;

import com.b2international.snowowl.snomed.reasoner.preferences.IReasonerPreferencesService;

/**
 * @since 2.2
 */
public class ReasonerConfig extends ExternalResource {

	public static final String MORE_A = "org.semanticweb.more.MORe.reasoner.factory";
	public static final String FACTPP = "uk.ac.manchester.cs.owl.factplusplus.factplusplus-factory";
	
	private String previousReasoner;
	private String selectedReasonerId;
	
	private ReasonerConfig(String selectedReasonerId) {
		this.selectedReasonerId = checkNotNull(selectedReasonerId, "selectedReasonerId");
	}
	
	@Override
	protected void before() throws Throwable {
		final IReasonerPreferencesService preferences = getReasonerPreferences();
		previousReasoner = preferences.getSelectedReasonerId();
		final IStatus result = preferences.setSelectedReasoner(selectedReasonerId);
		if (!result.isOK()) {
			throw new RuntimeException("Failed to set reasoner to " + selectedReasonerId);
		}
	}

	@Override
	protected void after() {
		getReasonerPreferences().setSelectedReasoner(previousReasoner);
	}
	
	private IReasonerPreferencesService getReasonerPreferences() {
		return Services.service(IReasonerPreferencesService.class);
	}
	
	public static ReasonerConfig moreA() {
		return select(MORE_A);
	}
	
	public static ReasonerConfig factpp() {
		return select(FACTPP);
	}
	
	public static ReasonerConfig select(String selectedReasonerId) {
		return new ReasonerConfig(selectedReasonerId);
	} 
	
}
