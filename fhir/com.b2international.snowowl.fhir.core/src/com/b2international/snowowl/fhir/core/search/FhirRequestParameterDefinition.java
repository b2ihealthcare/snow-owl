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
import java.util.HashSet;

import com.google.common.collect.Sets;

public class FhirRequestParameterDefinition {
	
	public enum SearchRequestParameterModifier {
		missing,
		exact,
		contains,
		text,
		in,
		below,
		above,
		not_in,
		type;
		
		public String getParameterString() {
			return name().replaceAll("_", "-");
		}
		
		public static SearchRequestParameterModifier fromRequestParameter(String parameterModifier) {
			return valueOf(parameterModifier.replaceAll("-", "_").toLowerCase());
		}
		
		public static boolean hasValue(String parameterModifier) {
			return Arrays.stream(values())
					.anyMatch(v -> parameterModifier.replaceAll("-", "_").toLowerCase().equals(v.name()));
		}
	}
	
	
	/**
	 * Request parameter types
	 * 	<li>number (missing)
	 * 	<li>date (missing)
	 * 	<li>string (missing, exact, contains)
	 * 	<li>token (missing, text, in, below, above, not-in)
	 * 	<li>reference (missing, type)
	 * 	<li>composite (missing)
	 * 	<li>quantity (missing)
	 * 	<li>uri (missing, below, above) 
	 */
	public enum FhirRequestParameterType {
		
		NUMBER(Sets.newHashSet(SearchRequestParameterModifier.missing)),
		DATE(Sets.newHashSet(SearchRequestParameterModifier.missing)),
		DATETIME(Sets.newHashSet(SearchRequestParameterModifier.missing)),
		STRING(Sets.newHashSet(SearchRequestParameterModifier.missing, 
				SearchRequestParameterModifier.exact, 
				SearchRequestParameterModifier.contains)),
		TOKEN(Sets.newHashSet(SearchRequestParameterModifier.missing,
				SearchRequestParameterModifier.text,
				SearchRequestParameterModifier.in,
				SearchRequestParameterModifier.below,
				SearchRequestParameterModifier.above,
				SearchRequestParameterModifier.not_in)),
		REFERENCE(Sets.newHashSet(SearchRequestParameterModifier.missing,
				SearchRequestParameterModifier.type)),
		COMPOSITE(Sets.newHashSet(SearchRequestParameterModifier.missing)),
		QUANTITY(Sets.newHashSet(SearchRequestParameterModifier.missing)),
		URI(Sets.newHashSet(SearchRequestParameterModifier.missing,
				SearchRequestParameterModifier.below,
				SearchRequestParameterModifier.above));
		
		private HashSet<SearchRequestParameterModifier> supportedModifiers;

		FhirRequestParameterType(HashSet<SearchRequestParameterModifier> supportedModifiers) {
			this.supportedModifiers = supportedModifiers;
		}
		
		public HashSet<SearchRequestParameterModifier> getSupportedModifiers() {
			return supportedModifiers;
		}

		public static FhirRequestParameterType fromRequestParameter(String requestParam) {
			return valueOf(requestParam.toUpperCase());
		}
	}
	
	protected String name;
	
	protected FhirRequestParameterType type;
	
	public FhirRequestParameterDefinition(final String name, final String type) {
		this.name = name;
		this.type = FhirRequestParameterType.fromRequestParameter(type);
	}

	public FhirRequestParameterDefinition(final String name, final FhirRequestParameterType type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public FhirRequestParameterType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return name + ":" + type.name();
	}

}
