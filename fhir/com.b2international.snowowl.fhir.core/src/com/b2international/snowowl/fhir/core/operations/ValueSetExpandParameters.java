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

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r5.model.*;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;

/**
 * This class represents a FHIR ValueSet$expand operation request in the R5 version.
 * 
 * @see <a href="https://www.hl7.org/fhir/valueset-operations.html#expand">FHIR:ValueSet:Operations:expand</a>
 * 
 * @since 9.2
 */
public final class ValueSetExpandParameters extends BaseParameters {

	public ValueSetExpandParameters() {
		super(new Parameters());
	}
	
	public ValueSetExpandParameters(Parameters parameters) {
		super(parameters);
	}
	
	public UriType getUrl() {
		return getParameterValue("url", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public ValueSet getValueSet() {
		return getParameterValue("valueSet", value -> (ValueSet) value.getResource());
	}
	
	public StringType getValueSetVersion() {
		return getParameterValue("valueSetVersion", Parameters.ParametersParameterComponent::getValueStringType);
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
	
	public List<StringType> getDesignation() {
		return getParameters("designation").stream().map(Parameters.ParametersParameterComponent::getValueStringType).toList();
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

	public ValueSetExpandParameters setUrl(String url) {
		return setUrl(new UriType(url));
	}
	
	public ValueSetExpandParameters setUrl(UriType url) {
		getParameters().addParameter("url", url);
		return this;
	}
	
	public ValueSetExpandParameters setValueSet(ValueSet valueSet) {
		getParameters().addParameter(new Parameters.ParametersParameterComponent("valueSet").setResource(valueSet));
		return this;
	}
	
	public ValueSetExpandParameters setValueSetVersion(String valueSetVersion) {
		return setValueSetVersion(new StringType(valueSetVersion));
	}
	
	public ValueSetExpandParameters setValueSetVersion(StringType valueSetVersion) {
		getParameters().addParameter("valueSetVersion", valueSetVersion);
		return this;
	}
	
	public ValueSetExpandParameters setContext(String context) {
		return setContext(new UriType(context));
	}

	public ValueSetExpandParameters setContext(UriType context) {
		getParameters().addParameter("context", context);
		return this;
	}
	
	public ValueSetExpandParameters setContextDirection(String contextDirection) {
		return setContextDirection(new CodeType(contextDirection));
	}
	
	public ValueSetExpandParameters setContextDirection(CodeType contextDirection) {
		getParameters().addParameter("contextDirection", contextDirection);
		return this;
	}
	
	public ValueSetExpandParameters setFilter(String filter) {
		return setFilter(new StringType(filter));
	}
	
	public ValueSetExpandParameters setFilter(StringType filter) {
		getParameters().addParameter("filter", filter);
		return this;
	}
	
	public ValueSetExpandParameters setDate(Date date) {
		return setDate(new DateType(date));
	}
	
	public ValueSetExpandParameters setDate(DateType date) {
		getParameters().addParameter("date", date);
		return this;
	}
	
	public ValueSetExpandParameters setOffset(Integer offset) {
		return setOffset(new IntegerType(offset));
	}
	
	public ValueSetExpandParameters setOffset(IntegerType offset) {
		getParameters().addParameter("offset", offset);
		return this;
	}
	
	public ValueSetExpandParameters setCount(Integer count) {
		return setCount(new IntegerType(count));
	}
	
	public ValueSetExpandParameters setCount(IntegerType count) {
		getParameters().addParameter("count", count);
		return this;
	}
	
	public ValueSetExpandParameters setIncludeDesignations(Boolean includeDesignations) {
		return setIncludeDesignations(new BooleanType(includeDesignations));
	}
	
	public ValueSetExpandParameters setIncludeDesignations(BooleanType includeDesignations) {
		getParameters().addParameter("includeDesignations", includeDesignations);
		return this;
	}
	
	public ValueSetExpandParameters setDesignation(List<?> designation) {
		if (designation != null && !designation.isEmpty()) {
			designation.stream().map(d -> {
				if (d instanceof StringType) {
					return (StringType) d;
				} else if (d instanceof String) {
					return new StringType((String) d);
				} else {
					throw new FhirException("", null);
				}
			}).forEach(designationValue -> getParameters().addParameter("designation", designationValue));
		}
		return this;
	}
	
	public ValueSetExpandParameters setIncludeDefinition(Boolean includeDefinition) {
		return setIncludeDefinition(new BooleanType(includeDefinition));
	}
	
	public ValueSetExpandParameters setIncludeDefinition(BooleanType includeDefinition) {
		getParameters().addParameter("includeDefinition", includeDefinition);
		return this;
	}
	
	public ValueSetExpandParameters setActiveOnly(Boolean activeOnly) {
		return setActiveOnly(new BooleanType(activeOnly));
	}
	
	public ValueSetExpandParameters setActiveOnly(BooleanType activeOnly) {
		getParameters().addParameter("activeOnly", activeOnly);
		return this;
	}
	
	public ValueSetExpandParameters setExcludeNested(Boolean excludeNested) {
		return setExcludeNested(new BooleanType(excludeNested));
	}
	
	public ValueSetExpandParameters setExcludeNested(BooleanType excludeNested) {
		getParameters().addParameter("excludeNested", excludeNested);
		return this;
	}
	
	public ValueSetExpandParameters setExcludeNestedForUI(Boolean excludeNestedForUI) {
		return setExcludeNestedForUI(new BooleanType(excludeNestedForUI));
	}
	
	public ValueSetExpandParameters setExcludeNestedForUI(BooleanType excludeNestedForUI) {
		getParameters().addParameter("excludeNestedForUI", excludeNestedForUI);
		return this;
	}
	
	public ValueSetExpandParameters setExcludeNestedPostCoordinated(Boolean excludeNestedPostCoordinated) {
		return setExcludeNestedPostCoordinated(new BooleanType(excludeNestedPostCoordinated));
	}
	
	public ValueSetExpandParameters setExcludeNestedPostCoordinated(BooleanType excludeNestedPostCoordinated) {
		getParameters().addParameter("excludeNestedPostCoordinated", excludeNestedPostCoordinated);
		return this;
	}
	
	public ValueSetExpandParameters setDisplayLanguage(String displayLanguage) {
		return setDisplayLanguage(new CodeType(displayLanguage));
	}
	
	public ValueSetExpandParameters setDisplayLanguage(CodeType displayLanguage) {
		getParameters().addParameter("displayLanguage", displayLanguage);
		return this;
	}
	
	public ValueSetExpandParameters setExcludeSystem(String excludeSystem) {
		return setExcludeSystem(new UriType(excludeSystem));
	}
	
	public ValueSetExpandParameters setExcludeSystem(UriType excludeSystem) {
		getParameters().addParameter("excludeSystem", excludeSystem);
		return this;
	}
	
	public ValueSetExpandParameters setSystemVersion(String systemVersion) {
		return setSystemVersion(new UriType(systemVersion));
	}
	
	public ValueSetExpandParameters setSystemVersion(UriType systemVersion) {
		getParameters().addParameter("systemVersion", systemVersion);
		return this;
	}
	
	public ValueSetExpandParameters setCheckSystemVersion(String checkSystemVersion) {
		return setCheckSystemVersion(new UriType(checkSystemVersion));
	}
	
	public ValueSetExpandParameters setCheckSystemVersion(UriType checkSystemVersion) {
		getParameters().addParameter("checkSystemVersion", checkSystemVersion);
		return this;
	}
	
	public ValueSetExpandParameters setForceSystemVersion(String forceSystemVersion) {
		return setForceSystemVersion(new UriType(forceSystemVersion));
	}
	
	public ValueSetExpandParameters setForceSystemVersion(UriType forceSystemVersion) {
		getParameters().addParameter("forceSystemVersion", forceSystemVersion);
		return this;
	}
	
	// XXX Snowy specific parameters
	
	public StringType getAfter() {
		return getParameterValue("after", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public BooleanType getWithHistorySupplements() {
		return getParameterValue("withHistorySupplements", Parameters.ParametersParameterComponent::getValueBooleanType);
	}
	
	public ValueSetExpandParameters setAfter(String after) {
		getParameters().addParameter("after", after);
		return this;
	}
	
	public ValueSetExpandParameters setWithHistorySupplements(Boolean withHistorySupplements) {
		getParameters().addParameter("withHistorySupplements", withHistorySupplements);
		return this;
	}
 
}
