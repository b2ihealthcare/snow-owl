package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.search.FhirRequestParameterDefinition.FhirRequestParameterType;

public class FhirParameter {
	
	private FhirRequestParameterDefinition parameterDefinition;
	
	private Collection<String> values;
	
	public FhirParameter(String name, String type, Collection<String> values) {
		parameterDefinition = new FhirRequestParameterDefinition(name, type);
		this.values = values;
	}

	public FhirParameter(final String name, final FhirRequestParameterType type, Collection<String> values) {
		parameterDefinition = new FhirRequestParameterDefinition(name, type);
		this.values = values;
	}
	
	public String getName() {
		return parameterDefinition.getName();
	}
	
	public FhirRequestParameterType getType() {
		return parameterDefinition.getType();
	}
	
	public Collection<String> getValues() {
		return values;
	}

}
