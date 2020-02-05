/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests;

import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;

import io.restassured.path.json.JsonPath;

/**
 * Custom matcher to check primitive type FHIR parameters e.g.:
 *  "name" : "name",
 *  "valueString" : "test"
 * @author 7.1
 */
public class FhirParameterMatcher extends TypeSafeMatcher<JsonPath>{

	private String parameterName;
	private FhirDataType fhirDataType;
	private Object expectedValue;
	
	private boolean isParameterFound;
	
	private Object foundValue;

	public FhirParameterMatcher(String parameterName, FhirDataType fhirDataType, Object value) {
		this.parameterName = parameterName;
		this.fhirDataType = fhirDataType;
		this.expectedValue = value;
	}

	@Override
	public void describeTo(Description description) {
		
		if (!isParameterFound) {
			description.appendText("Parameter with name ")
			.appendValue(parameterName)
			.appendText(" is not found.");
		} else {
			description
				.appendText("Found ")
				.appendValue(foundValue)
				.appendText(" instead of the expected:  ")
				.appendValue(expectedValue)
				.appendText(" for parameter name ")
				.appendValue(parameterName)
				.appendText(" and type ")
				.appendValue(fhirDataType);
		}
	}

	@Override
	protected boolean matchesSafely(JsonPath jsonPath) {
		
		Map<String, Object> map = jsonPath.getMap("parameter.find {it.name =='" + parameterName + "'}");
		if (map == null || map.isEmpty()) {
			isParameterFound = false;
			return false;
		}
		
		isParameterFound = true;
		
		if (!map.containsKey(fhirDataType.getSerializedName())) {
			return false;
		}
		
		foundValue = map.get(fhirDataType.getSerializedName());
		return foundValue.equals(expectedValue);
	}

	/**
	 * Returns a matcher to check a FHIR parameter for the supplied FHIR Data type and corresponding value
	 * @param parameter name
	 * @param FHIR data type
	 * @param data type value
	 * @return matcher
	 */
	public static FhirParameterMatcher hasParameter(String parameterName, FhirDataType fhirDataType, Object value) {
		return new FhirParameterMatcher(parameterName, fhirDataType, value);
	}

}
