package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.search.FhirRequestParameterDefinition.FhirRequestParameterType;

public class FhirFilterParameter extends FhirParameter {

	public FhirFilterParameter(String name, FhirRequestParameterType type, Collection<String> values) {
		super(name, type, values);
	}

	public FhirFilterParameter(String name, String type, Collection<String> values) {
		super(name, type, values);
	}

}
