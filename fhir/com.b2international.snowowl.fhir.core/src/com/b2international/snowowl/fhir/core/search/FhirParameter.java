package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.search.SupportedParameter.FhirRequestParameterType;

public abstract class FhirParameter {
	
	protected SupportedParameter parameterDefinition;
	
	protected Collection<String> values;
	
	public FhirParameter(final SupportedParameter parameterDefinition, Collection<String> values) {
		this.parameterDefinition = parameterDefinition;
		this.values = values;
	}

//	public FhirParameter(final String name, final FhirRequestParameterType type, Collection<String> values) {
//		parameterDefinition = new FhirRequestParameterDefinition(name, type);
//		this.values = values;
//	}
	
	public String getName() {
		return parameterDefinition.getName();
	}
	
	public FhirRequestParameterType getType() {
		return parameterDefinition.getType();
	}
	
	public Collection<String> getValues() {
		return values;
	}
	
	public SupportedParameter getParameterDefinition() {
		return parameterDefinition;
	}

	public abstract void validate();

}
