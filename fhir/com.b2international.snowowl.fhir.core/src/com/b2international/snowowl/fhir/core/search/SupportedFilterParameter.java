/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.search;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SupportedFilterParameter extends FhirRequestParameterDefinition {
	
	public enum SummaryParameterValue {
		
		TRUE, //	Return only those elements marked as "summary" in the base definition of the resource(s) (see ElementDefinition.isSummary)
		TEXT, //	Return only the "text" element, the 'id' element, the 'meta' element, and only top-level mandatory elements
		DATA	, //Remove the text element
		COUNT, //Search only: just return a count of the matching resources, without returning the actual matches
		FALSE; //Return all parts of the resource(s)
		
		public static SummaryParameterValue fromRequestParameter(String requestParam) {
			return valueOf(requestParam.toUpperCase());
		}
	}
	
	/**
	 * Available parameter keys in FHIR
	 */
	public enum FhirFilterParameterKey  {
		
		_sort(FhirRequestParameterType.STRING),
		_count(FhirRequestParameterType.NUMBER),
		_include(FhirRequestParameterType.STRING),
		_revinclude(FhirRequestParameterType.STRING),
		_summary(FhirRequestParameterType.STRING),
		_elements(FhirRequestParameterType.STRING), 
		_contained(FhirRequestParameterType.STRING),
		_containedType(FhirRequestParameterType.STRING);
		
		private FhirRequestParameterType parameterType;
		
		FhirFilterParameterKey(FhirRequestParameterType parameterType) {
			this.parameterType = parameterType;
		}
		
		public FhirRequestParameterType getParameterType() {
			return parameterType;
		}
		
		public static boolean hasParameter(String parameterName) {
			return Arrays.stream(values()).anyMatch(k -> k.name().equals(parameterName));
		}
		
		public static FhirFilterParameterKey fromRequestParameter(String requestParam) {
			return valueOf(requestParam.toLowerCase());
		}
		
		
		
		public static Set<String> getParameterNames() {
			return Arrays.stream(values()).map(k -> k.name()).collect(Collectors.toSet());
		}
	}
	
	
	public SupportedFilterParameter(final String name, final String type) {
		super(name, type);
	}
	
	public SupportedFilterParameter(String requestParameterKey) {
		super(requestParameterKey, FhirFilterParameterKey.valueOf(requestParameterKey).getParameterType().name());
	}

	private FhirFilterParameterKey requestParameterKey;


	public FhirFilterParameterKey getKey() {
		return requestParameterKey;
	}
	
}
