/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.fhir.core.operations;

import java.util.List;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r5.model.*;

/**
 * This class represents a FHIR ValueSet$expand operation request in the R5 version.
 * 
 * @see <a href="https://www.hl7.org/fhir/valueset-operations.html#expand">FHIR:ValueSet:Operations:expand</a>
 * 
 * @since 9.2
 */
public final class ValueSetExpandParameters extends BaseParameters {

	public ValueSetExpandParameters(Parameters parameters) {
		super(parameters);
	}
	
	public UriType getUrl() {
		return getParameterValue("url", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public ValueSet getValueSet() {
		throw new FHIRException("Inline input parameter 'valueSet' is not supported.");
	}
	
	public StringType getValueSetVersion() {
		return getParameterValue("url", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public UriType getContext() {
		return getParameterValue("context", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public CodeType getContextDirection() {
		return getParameterValue("contextDirection", Parameters.ParametersParameterComponent::getValueCodeType);
	}
	
	public StringType getFilter() {
		return getParameterValue("filter", Parameters.ParametersParameterComponent::getValueStringType);
	}

	public DateType getDate() {
		return getParameterValue("date", Parameters.ParametersParameterComponent::getValueDateType);
	}

	public IntegerType getOffset() {
		return getParameterValue("offset", Parameters.ParametersParameterComponent::getValueIntegerType);
	}

	public IntegerType getCount() {
		return getParameterValue("count", Parameters.ParametersParameterComponent::getValueIntegerType);
	}

	public BooleanType getIncludeDesignations() {
		return getParameterValue("includeDesignations", Parameters.ParametersParameterComponent::getValueBooleanType);
	}
	
	public List<StringType> getDesignations() {
		return getParameters("designations").stream().map(Parameters.ParametersParameterComponent::getValueStringType).toList();
	}

	public BooleanType getIncludeDefinition() {
		return getParameterValue("includeDefinition", Parameters.ParametersParameterComponent::getValueBooleanType);
	}

	public BooleanType getActiveOnly() {
		return getParameterValue("activeOnly", Parameters.ParametersParameterComponent::getValueBooleanType);
	}

	public BooleanType getExcludeNested() {
		return getParameterValue("excludeNested", Parameters.ParametersParameterComponent::getValueBooleanType);
	}

	public BooleanType getExcludeNotForUI() {
		return getParameterValue("excludeNotForUI", Parameters.ParametersParameterComponent::getValueBooleanType);
	}

	public BooleanType getExcludePostCoordinated() {
		return getParameterValue("excludePostCoordinated", Parameters.ParametersParameterComponent::getValueBooleanType);
	}

	public CodeType getDisplayLanguage() {
		return getParameterValue("displayLanguage", Parameters.ParametersParameterComponent::getValueCodeType);
	}
	
	public UriType getExcludeSystem() {
		return getParameterValue("excludeCodeSystem", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public UriType getSystemVersion() {
		return getParameterValue("systemVersion", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public UriType getCheckSystemVersion() {
		return getParameterValue("checkSystemVersion", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public UriType getForceSystemVersion() {
		return getParameterValue("forceSystemVersion", Parameters.ParametersParameterComponent::getValueUriType);
	}

	// extra Snowy specific parameters
	public StringType getAfter() {
		return getParameterValue("after", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public BooleanType getWithHistorySupplements() {
		return getParameterValue("withHistorySupplements", Parameters.ParametersParameterComponent::getValueBooleanType);
	}

}
