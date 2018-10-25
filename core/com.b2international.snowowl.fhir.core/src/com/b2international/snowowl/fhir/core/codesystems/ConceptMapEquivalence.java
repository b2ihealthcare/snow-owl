package com.b2international.snowowl.fhir.core.codesystems;

import com.b2international.snowowl.fhir.core.ResourceNarrative;

/**
 * FHIR Concept Map Equivalence code system
 * @since 7.1
 */
@ResourceNarrative("The degree of equivalence between codes.")
public enum ConceptMapEquivalence implements FhirCodeSystem {
	
	//The concepts are related to each other, and have at least some overlap in meaning, but the exact relationship is not known
	RELATEDTO("Related To"),
	
	//The definitions of the concepts mean the same thing (including when structural implications of meaning are considered) 
	//(i.e. extensionally identical).
	EQUIVALENT("Equivalent"),
	
	//Child of EQUIVALENT, The definitions of the concepts are exactly the same (i.e. only grammatical differences) and structural 
	//implications of meaning are identical or irrelevant (i.e. intentionally identical).
	EQUAL("Equal"),
	
	//The target mapping is wider in meaning than the source concept.
	WIDER("Wider"),
	
	//The target mapping subsumes the meaning of the source concept (e.g. the source is-a target).
	SUBSUMES("Subsumes"),
	
	//The target mapping is narrower in meaning than the source concept. The sense in which the mapping is narrower SHALL be described in the comments in this case, 
	//and applications should be careful when attempting to use these mappings operationally.
	NARROWER("Narrower"),
	
	//The target mapping specializes the meaning of the source concept (e.g. the target is-a source).
	SPECIALIZES("Specializes"),
	
	//The target mapping overlaps with the source concept, but both source and target cover additional meaning, or the definitions are imprecise and it is 
	//uncertain whether they have the same boundaries to their meaning. The sense in which the mapping is narrower SHALL be described in the comments in this case, and applications should be careful when attempting to use these mappings operationally.
	INEXACT("Inexact"),
	
	//There is no match for this concept in the destination concept system.
	UNMATCHED("Unmatched"),
	
	//This is an explicit assertion that there is no mapping between the source and target concept.
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
