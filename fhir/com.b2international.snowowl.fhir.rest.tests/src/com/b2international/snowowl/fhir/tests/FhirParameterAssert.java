/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Objects;

import org.assertj.core.api.Condition;

import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;

import io.restassured.path.json.JsonPath;

/** 
 * @since 7.11
 */
public abstract class FhirParameterAssert {

	private FhirParameterAssert() {}
	
	public static Condition<JsonPath> fhirParameter(String parameterName, FhirDataType fhirDataType, Object expectedValue) {
		return new Condition<JsonPath>() {
			@Override
			public boolean matches(JsonPath jsonPath) {
				Map<String, Object> map = jsonPath.getMap("parameter.find {it.name =='" + parameterName + "'}");
				if (map == null || map.isEmpty()) {
					return false;
				}
				
				if (!map.containsKey(fhirDataType.getSerializedName())) {
					return false;
				}
				
				Object actualValue = map.get(fhirDataType.getSerializedName());
				return Objects.equals(expectedValue, actualValue);
			}
		};
	}
	
}
