package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.search.FhirRequestParameterDefinition.FhirRequestParameterType;

public class FhirSearchParameter extends FhirParameter {
	
	public FhirSearchParameter(String name, FhirRequestParameterType type, Collection<String> values) {
		super(name, type, values);
	}

	public FhirSearchParameter(String name, String type, Collection<String> values) {
		super(name, type, values);
	}

	
	
}
