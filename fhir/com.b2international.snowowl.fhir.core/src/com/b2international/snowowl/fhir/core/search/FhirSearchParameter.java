package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.search.SupportedParameter.FhirRequestParameterType;

public class FhirSearchParameter extends FhirParameter {
	
	private String modifier;
	
	//public FhirSearchParameter(String name, FhirRequestParameterType type, Collection<String> values) {
	//	this(name, null, type, values);
	//}

	//public FhirSearchParameter(SupportedSearchParameter param, final String modifier, FhirRequestParameterType type, Collection<String> values) {
	//	super(supportedSearchParameter, values);
	//	this.modifier = modifier;
	//}
	
	//public FhirSearchParameter(String name, String type, Collection<String> values) {
	//	this(name, null, type, values);
	//}

	//public FhirSearchParameter(String name, String modifier, String type, Collection<String> values) {
	//	super(name, type, values);
	//	this.modifier = modifier;
	//}
	
	public FhirSearchParameter(SupportedSearchParameter supportedSearchParameter, String modifier, Collection<String> values) {
		super(supportedSearchParameter, values);
	}

	public String getModifier() {
		return modifier;
	}

	@Override
	public void validate() {
		// TODO Auto-generated method stub
		
	}

	
	
}
