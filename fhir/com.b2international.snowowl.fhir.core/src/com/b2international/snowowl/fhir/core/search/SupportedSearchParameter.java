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

import com.google.common.collect.Sets;

public class SupportedSearchParameter extends SupportedParameter {
	
	public enum FhirCommonSearchKey {
		
		_id(FhirRequestParameterType.TOKEN), //Resource, type token
		_lastUpdated(FhirRequestParameterType.DATE), //Resource
		_tag(FhirRequestParameterType.TOKEN), //Resource
		_profile(FhirRequestParameterType.URI), //Resource
		_security(FhirRequestParameterType.TOKEN), //Resource
		_text(FhirRequestParameterType.STRING), //DomainResource
		_content(FhirRequestParameterType.STRING), //Resource
		
		//Custom types, not fields on the resource
		_list(FhirRequestParameterType.STRING),
		_has(FhirRequestParameterType.STRING),
		_type(FhirRequestParameterType.STRING),
		_query(FhirRequestParameterType.STRING);
		
		private FhirRequestParameterType parameterType;
		
		FhirCommonSearchKey(FhirRequestParameterType parameterType) {
			this.parameterType = parameterType;
		}

		public FhirRequestParameterType getParameterType() {
			return parameterType;
		}
		
		public static FhirCommonSearchKey fromRequestParameter(String requestParam) {
			return valueOf(requestParam.toLowerCase());
		}
		
		public static Set<String> getParameterNames() {
			return Arrays.stream(values()).map(k -> k.name()).collect(Collectors.toSet());
		}
		
		public static boolean hasParameter(String parameterName) {
			return Arrays.stream(values()).anyMatch(k -> k.name().equals(parameterName));
		}
	}
	
	private Set<String> supportedModifiers = Sets.newHashSet();
	
	public SupportedSearchParameter(final String requestParameterKey, final String type, final String[] supportedModifiers) {
		super(requestParameterKey, type);
		this.supportedModifiers = Sets.newHashSet(supportedModifiers);
	}

	public boolean hasSupportedModifier(String parameterModifier) {
		return supportedModifiers.contains(parameterModifier);
	}
	
	public Set<String> getSupportedModifiers() {
		return supportedModifiers;
	}

}
