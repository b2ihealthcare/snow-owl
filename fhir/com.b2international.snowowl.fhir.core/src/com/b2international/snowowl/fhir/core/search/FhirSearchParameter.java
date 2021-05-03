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
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.search.FhirUriParameterDefinition.FhirRequestParameterType;
import com.b2international.snowowl.fhir.core.search.FhirUriParameterDefinition.SearchRequestParameterModifier;
import com.b2international.snowowl.fhir.core.search.FhirUriSearchParameterDefinition.FhirCommonSearchKey;
import com.b2international.snowowl.fhir.core.search.FhirUriSearchParameterDefinition.SearchRequestParameterValuePrefix;
import com.google.common.collect.Sets;

/**
 * FHIR URI request parameter for searching.
 * @since 7.14
 */
public class FhirSearchParameter extends FhirParameter {
	
	private SearchRequestParameterModifier modifier;
	
	public FhirSearchParameter(FhirUriSearchParameterDefinition searchParameterDefinition, String modifier, Collection<String> values) {
		super(searchParameterDefinition, values);
		if (!StringUtils.isEmpty(modifier)) {
			this.modifier = SearchRequestParameterModifier.fromRequestParameter(modifier);
		}
		validate();
	}
	
	public FhirSearchParameter(FhirUriSearchParameterDefinition searchParameterDefinition, SearchRequestParameterModifier modifier, Collection<String> values) {
		super(searchParameterDefinition, values);
		this.modifier = modifier;
	}

	public SearchRequestParameterModifier getModifier() {
		return modifier;
	}
	
	@Override
	public void validate() {
		
		parameterDefinition.isValidModifier(modifier);
		
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ValidatingBuilder<FhirSearchParameter> {
		
		private String name;
		private FhirRequestParameterType type;
		private SearchRequestParameterModifier modifier;
		private SearchRequestParameterValuePrefix prefix;
		private Collection<String> values = Sets.newHashSet();

		public Builder name(final FhirCommonSearchKey name) {
			this.name = name.name();
			return this;
		}
		
		public Builder name(final String name) {
			this.name = name;
			return this;
		}

		public Builder type(final FhirRequestParameterType type) {
			this.type = type;
			return this;
		}
		
		public Builder type(final String typeName) {
			this.type = FhirRequestParameterType.valueOf(typeName.toUpperCase());
			return this;
		}
		
		public Builder modifier(final SearchRequestParameterModifier modifier) {
			this.modifier = modifier;
			return this;
		}

		public Builder modifier(final String modifier) {
			this.modifier = SearchRequestParameterModifier.fromRequestParameter(modifier);
			return this;
		}
		
		public Builder prefix(final SearchRequestParameterValuePrefix prefix) {
			this.prefix = prefix;
			return this;
		}
		
		public Builder prefix(final String prefixString) {
			this.prefix = SearchRequestParameterValuePrefix.valueOf(prefixString);
			return this;
		}
		
		public Builder values(final Collection<String> values) {
			this.values = values;
			return this;
		}
		
		public Builder value(final String value) {
			this.values = Sets.newHashSet(value);
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
			//do additional validation here
			if (modifier !=null) {
				if (type == FhirRequestParameterType.NUMBER) {
					if (modifier != SearchRequestParameterModifier.missing) {
						throw FhirException.createFhirError(String.format("Invalid modifier [%s] for number type parameter [%s].", modifier, name), OperationOutcomeCode.MSG_PARAM_INVALID);
					}
					
				} else if (type == FhirRequestParameterType.DATE || type == FhirRequestParameterType.DATETIME) {
					if (modifier != SearchRequestParameterModifier.missing) {
						throw FhirException.createFhirError(String.format("Invalid modifier [%s] for date/datetime type parameter [%s].", modifier, name), OperationOutcomeCode.MSG_PARAM_INVALID);
					}
				} else if (type == FhirRequestParameterType.STRING) {
					if (modifier != SearchRequestParameterModifier.missing && modifier != SearchRequestParameterModifier.exact && modifier != SearchRequestParameterModifier.contains) {
						throw FhirException.createFhirError(String.format("Invalid modifier [%s] for string type parameter [%s].", modifier, name), OperationOutcomeCode.MSG_PARAM_INVALID);
					}
				} else if (type == FhirRequestParameterType.TOKEN) {
					if (modifier != SearchRequestParameterModifier.missing 
							&& modifier != SearchRequestParameterModifier.text 
							&& modifier != SearchRequestParameterModifier.in 
							&& modifier != SearchRequestParameterModifier.below 
							&& modifier != SearchRequestParameterModifier.above 
							&& modifier != SearchRequestParameterModifier.not_in) {
						throw FhirException.createFhirError(String.format("Invalid modifier [%s] for token type parameter [%s].", modifier, name), OperationOutcomeCode.MSG_PARAM_INVALID);
					}
				} else if (type == FhirRequestParameterType.REFERENCE) {
					if (modifier != SearchRequestParameterModifier.missing 
							&& modifier != SearchRequestParameterModifier.type) {
						throw FhirException.createFhirError(String.format("Invalid modifier [%s] for reference type parameter [%s].", modifier, name), OperationOutcomeCode.MSG_PARAM_INVALID);
					}
				} else if (type == FhirRequestParameterType.COMPOSITE) {
					if (modifier != SearchRequestParameterModifier.missing) {
						throw FhirException.createFhirError(String.format("Invalid modifier [%s] for composite type parameter [%s].", modifier, name), OperationOutcomeCode.MSG_PARAM_INVALID);
					}
				} else if (type == FhirRequestParameterType.QUANTITY) {
					if (modifier != SearchRequestParameterModifier.missing) {
						throw FhirException.createFhirError(String.format("Invalid modifier [%s] for quantity type parameter [%s].", modifier, name), OperationOutcomeCode.MSG_PARAM_INVALID);
					}
				} else if (type == FhirRequestParameterType.URI) {
					if (modifier != SearchRequestParameterModifier.missing 
							&& modifier != SearchRequestParameterModifier.below 
							&& modifier != SearchRequestParameterModifier.above) {
						throw FhirException.createFhirError(String.format("Invalid modifier [%s] for URI type parameter [%s].", modifier, name), OperationOutcomeCode.MSG_PARAM_INVALID);
					}
				}
			}
			
			String[] supportedModifiers = null;
			FhirUriSearchParameterDefinition definition = new FhirUriSearchParameterDefinition(name, type, supportedModifiers);
			
			return new FhirSearchParameter(definition, modifier, values);
		}
	
	}
	
	
	
}
