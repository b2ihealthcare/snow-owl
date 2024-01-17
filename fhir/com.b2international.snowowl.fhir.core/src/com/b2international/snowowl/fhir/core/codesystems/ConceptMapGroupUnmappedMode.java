package com.b2international.snowowl.fhir.core.codesystems;

import java.util.Locale;

import com.b2international.snowowl.fhir.core.ResourceNarrative;

/**
 * FHIR Concept Map Group Unmapped Mode
 * @since 7.1
 */
@ResourceNarrative("Defines which action to take if there is no match in the group.")
public enum ConceptMapGroupUnmappedMode implements FhirCodeSystem {
	
	PROVIDED("Provided Code"),
	FIXED("Fixed Code"),
	OTHER_MAP("Other Map");

	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/conceptmap-unmapped-mode"; //$NON-NLS-N$

	private String displayName;
	
	private ConceptMapGroupUnmappedMode(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}

	@Override
	public String getCodeValue() {
		return name()
			.toLowerCase(Locale.ENGLISH)
			.replace('_', '-');
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public static ConceptMapGroupUnmappedMode forValue(String value) {
		if (value == null) {
			return null;
		}
		
		return ConceptMapGroupUnmappedMode.valueOf(value
			.toUpperCase(Locale.ENGLISH)
			.replace('-', '_'));
    }
}
