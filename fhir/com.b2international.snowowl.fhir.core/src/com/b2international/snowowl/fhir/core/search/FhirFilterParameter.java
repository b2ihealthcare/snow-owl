package com.b2international.snowowl.fhir.core.search;

public class FhirFilterParameter extends FhirRequestParameterDefinition {

	public FhirFilterParameter(String name, String type) {
		super(name, type);
	}

	public FhirFilterParameter(final String name, final FhirRequestParameterType type) {
		super(name, type);
	}

}
