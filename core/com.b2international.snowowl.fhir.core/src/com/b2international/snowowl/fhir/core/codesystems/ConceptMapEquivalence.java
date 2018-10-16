package com.b2international.snowowl.fhir.core.codesystems;

import com.b2international.snowowl.fhir.core.ResourceNarrative;

/**
 * FHIR Concept Map Equivalence code system
 * @since 7.1
 */
@ResourceNarrative("The degree of equivalence between codes.")
public enum ConceptMapEquivalence implements FhirCodeSystem {
	
	RELATEDTO("Related To"),
	EQUIVALENT("Equivalent"),
	EQUAL("Equal"),
	WIDER("Wider"),
	SUBSUMES("Subsumes"),
	NARROWER("Narrower"),
	SPECIALIZES("Specializes"),
	INEXACT("Inexact"),
	UNMATCHED("Unmatched"),
	DISJOINT("Disjoint");

	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/concept-map-equivalence"; //$NON-NLS-N$

	private String displayName;
	
	private ConceptMapEquivalence(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}

	@Override
	public String getCodeValue() {
		return name().toLowerCase();
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

}
