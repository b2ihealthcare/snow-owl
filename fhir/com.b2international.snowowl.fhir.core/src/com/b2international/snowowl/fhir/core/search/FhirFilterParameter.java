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

/**
 * Class to represent a FHIR URI filter parameter.
 * 
 * @since 7.14
 *
 */
public class FhirFilterParameter extends FhirParameter {

	public FhirFilterParameter(FhirUriFilterParameterDefinition supportedFilterParameter, Collection<String> values) {
		super(supportedFilterParameter, values);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends FhirParameter.Builder<Builder, FhirFilterParameter> {
	
		private FhirUriFilterParameterDefinition filterParameterDefinition;
		
		public Builder parameterDefinition(final FhirUriFilterParameterDefinition parameterDefinition) {
			this.filterParameterDefinition = parameterDefinition;
			return getSelf();
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		@Override
		protected FhirFilterParameter doBuild() {
			filterParameterDefinition.validateValues(values);
			return new FhirFilterParameter(filterParameterDefinition, values);
		}
	}

}
