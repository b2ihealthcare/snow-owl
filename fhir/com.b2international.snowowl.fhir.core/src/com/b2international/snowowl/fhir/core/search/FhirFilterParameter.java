package com.b2international.snowowl.fhir.core.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;

public class FhirFilterParameter extends FhirParameter {

//	public FhirFilterParameter(String name, FhirRequestParameterType type, Collection<String> values) {
//		this(name, type.name(), values);
//	}
//
//	public FhirFilterParameter(String name, String type, Collection<String> values) {
//		super(new SupportedFilterParameter(name, type), values);
//	}
	
	public FhirFilterParameter(FhirUriFilterParameterDefinition supportedFilterParameter, Collection<String> values) {
		super(supportedFilterParameter, values);
	}

	@Override
	public void validate() {
		
		//No restriction in the definition
		Set<String> supportedValues = parameterDefinition.getSupportedValues();
		if (supportedValues.isEmpty()) return;
		
		Set<String> uppercaseValues = values.stream().map(String::toUpperCase).collect(Collectors.toSet());
		if (supportedValues.containsAll(uppercaseValues)) return;
			
		throw FhirException.createFhirError(String.format("Filter parameter value %s is not supported. Supported parameter values are %s.", Arrays.toString(values.toArray()), Arrays.toString(supportedValues.toArray())), OperationOutcomeCode.MSG_PARAM_UNKNOWN, "SEARCH_REQUEST_PARAMETER_MARKER");
		
	}

}
