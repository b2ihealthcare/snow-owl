package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;

public class FhirFilterParameter extends FhirRequestParameterDefinition {

	private Collection<String> values;

	public FhirFilterParameter(String name, String type) {
		super(name, type);
	}

	public FhirFilterParameter(final String name, final FhirRequestParameterType type, Collection<String> values) {
		super(name, type);
		this.values = values;
	}

}
