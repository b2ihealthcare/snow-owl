/*******************************************************************************
 * Copyright (c) 2020 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.core;

import java.util.function.Function;

import com.b2international.snowowl.snomed.core.domain.SnomedConcept;

/**
 * @since 7.10.0
 */
public enum SnomedDisplayTermType {
	
	FSN(
		"Fully specified name", 
		SnomedConcept.Expand.FULLY_SPECIFIED_NAME + "()",
		concept -> concept.getFsn() != null ? concept.getFsn().getTerm() : concept.getId()
	),
	
	PT(
		"Preferred term", 
		SnomedConcept.Expand.PREFERRED_TERM + "()",
		concept -> concept.getPt() != null ? concept.getPt().getTerm() : concept.getId()
	),
	
	ID_ONLY(
		"ID", 
		"",
		SnomedConcept::getId
	);
	
	private final String label;
	private final String expand;
	private final Function<SnomedConcept, String> getLabel;
	
	private SnomedDisplayTermType(final String label, final String expand, final Function<SnomedConcept, String> getLabel) {
		this.label = label;
		this.expand = expand;
		this.getLabel = getLabel;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getExpand() {
		return expand;
	}
	
	public String getLabel(SnomedConcept concept) {
		return getLabel.apply(concept);
	}
	
	@Override
	public String toString() {
		return label;
	}

	public static SnomedDisplayTermType getEnum(final String value) {
		try {
			return SnomedDisplayTermType.valueOf(value);
		} catch(Exception e) {
			return ID_ONLY;
		}
	}
	
}
