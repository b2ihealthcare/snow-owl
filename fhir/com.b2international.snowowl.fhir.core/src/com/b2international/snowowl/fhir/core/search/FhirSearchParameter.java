package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;

public class FhirSearchParameter extends FhirRequestParameterDefinition {

	private Collection<String> values;

	public FhirSearchParameter(String name, String type) {
		super(name, type);
	}

	public FhirSearchParameter(String name, FhirRequestParameterType type,
			Collection<String> values) {
		super(name, type);
		this.values = values;
	}

}
