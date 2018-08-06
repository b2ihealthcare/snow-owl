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
package com.b2international.snowowl.fhir.core.model.valueset.expansion;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

/**
 * A value set can also be "expanded", where the value set is turned into a simple collection of enumerated codes. 
 * This element holds the expansion if it has been performed.
 * 
 * @since 6.6
 */
public class Expansion {
	
	@Valid
	@NotNull
	@JsonProperty
	private final Uri identifier;
	
	@NotNull
	@JsonProperty
	private final Date timestamp;
	
	//not primitive int to avoid serialization when the default value is 0
	@Min(value = 0, message = "Total must be equal to or larger than 0")
	@JsonProperty
	private final Integer total; 
	
	@Min(value = 0, message = "Offset must be equal to or larger than 0")
	@JsonProperty
	private final Integer offset; 
	
	@Valid
	@JsonProperty("parameter")
	private final Collection<Parameter<?>> parameters;
	
	@Valid
	@JsonProperty
	private final Collection<Contains> contains;
	
	Expansion(Uri identifier, Date timestamp, Integer total, Integer offset, Collection<Parameter<?>> parameters, Collection<Contains> contains) {
		this.identifier = identifier;
		this.timestamp = timestamp;
		this.total = total;
		this.offset = offset;
		this.parameters = parameters;
		this.contains = contains;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ValidatingBuilder<Expansion> {
		
		private Uri identifier;
		private Date timestamp;
		private Integer total; 
		private Integer offset; 
		private Collection<Parameter<?>> parameters = Sets.newHashSet();
		private Collection<Contains> contains = Sets.newHashSet();
		
		public Builder identifier(final String identifier) {
			this.identifier = new Uri(identifier);
			return this;
		}
		
		public Builder timestamp(Date timestamp) {
			this.timestamp = timestamp;
			return this;
		}
		
		public Builder timestamp(String dateString) {
			DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
			try {
				this.timestamp = df.parse(dateString);
			} catch (ParseException e) {
				throw FhirException.createFhirError(dateString + " cannot be parsed, use the format " + FhirConstants.DATE_TIME_FORMAT, OperationOutcomeCode.MSG_PARAM_INVALID);
			}
			return this;
		}

		public Builder total(Integer total) {
			this.total = total;
			return this;
		}
		
		public Builder offset(Integer offset) {
			this.offset = offset;
			return this;
		}
		
		public Builder addParameter(Parameter<?> parameter) {
			parameters.add(parameter);
			return this;
		}
		
		public Builder addContains(Contains content) {
			contains.add(content);
			return this;
		}
		
		@Override
		protected Expansion doBuild() {
			return new Expansion(identifier, timestamp, total, offset, parameters, contains);
		}
	}

}
