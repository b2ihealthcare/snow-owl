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

import java.util.Collection;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.search.FhirUriParameterDefinition.SearchRequestParameterModifier;

/**
 * FHIR URI request parameter for searching.
 * @since 7.14
 */
public class FhirSearchParameter extends FhirParameter {
	
	private SearchRequestParameterModifier modifier;
	
	FhirSearchParameter(FhirUriSearchParameterDefinition searchParameterDefinition, SearchRequestParameterModifier modifier, Collection<String> values) {
		super(searchParameterDefinition, values);
		this.modifier = modifier;
	}

	public SearchRequestParameterModifier getModifier() {
		return modifier;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends FhirParameter.Builder<Builder, FhirSearchParameter> {
		
		private FhirUriSearchParameterDefinition fhirUriSearchParameterDefinition;
		private SearchRequestParameterModifier modifier;

		public Builder parameterDefinition(FhirUriSearchParameterDefinition parameterDefinition) {
			this.fhirUriSearchParameterDefinition = parameterDefinition;
			return getSelf();
		}
		
		public Builder modifier(final SearchRequestParameterModifier modifier) {
			this.modifier = modifier;
			return getSelf();
		}

		public Builder modifier(final String modifier) {
			if (!StringUtils.isEmpty(modifier)) {
				this.modifier = SearchRequestParameterModifier.fromRequestParameter(modifier);
			}
			return getSelf();
		}
		
		@Override
		protected Builder getSelf() {
			return this;
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
		@Override
		protected FhirSearchParameter doBuild() {
			
			//additional validation comes here
			fhirUriSearchParameterDefinition.isValidModifier(modifier);
			return new FhirSearchParameter(fhirUriSearchParameterDefinition, modifier, values);
			
		}
	
	}
	
}
