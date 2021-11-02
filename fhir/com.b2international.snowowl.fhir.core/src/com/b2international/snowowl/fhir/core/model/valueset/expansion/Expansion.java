/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Lists;

/**
 * A value set can also be "expanded", where the value set is turned into a simple collection of enumerated codes. 
 * This element holds the expansion if it has been performed.
 * 
 * @since 6.6
 */
@SuppressWarnings("rawtypes")
@JsonDeserialize(builder = Expansion.Builder.class)
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
	private final List<Parameter> parameters;
	
	@Valid
	@JsonProperty
	private final List<Contains> contains;
	
	@JsonProperty
	private final String after;
	
	Expansion(Uri identifier, Date timestamp, Integer total, Integer offset, List<Parameter> parameters, List<Contains> contains, String after) {
		this.identifier = identifier;
		this.timestamp = timestamp;
		this.total = total;
		this.offset = offset;
		this.parameters = parameters;
		this.contains = contains;
		this.after = after;
	}
	
	public Uri getIdentifier() {
		return identifier;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public Integer getTotal() {
		return total;
	}
	
	public Integer getOffset() {
		return offset;
	}
	
	public Collection<Parameter> getParameters() {
		return parameters;
	}
	
	public Collection<Contains> getContains() {
		return contains;
	}
	
	public String getAfter() {
		return after;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Expansion> {
		
		private Uri identifier;
		private Date timestamp;
		private Integer total; 
		private Integer offset; 
		private List<Parameter> parameters;
		private List<Contains> contains;
		private String after;
		
		public Builder identifier(final String identifier) {
			this.identifier = new Uri(identifier);
			return this;
		}
		
		public Builder timestamp(Date timestamp) {
			this.timestamp = timestamp;
			return this;
		}
		
		public Builder timestamp(String dateString) {
			this.timestamp = FhirDates.parseDate(dateString);
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
		
		@JsonProperty("parameter")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder parameters(Collection<Parameter> parameters) {
			this.parameters = List.copyOf(parameters);
			return this;
		}
		
		public Builder addParameter(Parameter<?> parameter) {
			if (parameters == null) {
				parameters = Lists.newArrayList();
			}
			parameters.add(parameter);
			return this;
		}
		
		@JsonProperty("contains")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder contains(Collection<Contains> contains) {
			this.contains = List.copyOf(contains);
			return this;
		}
		
		public Builder addContains(Contains content) {
			if (contains == null) {
				contains = Lists.newArrayList();
			}
			contains.add(content);
			return this;
		}
		
		public Builder after(String after) {
			this.after = after;
			return this;
		}
		
		@Override
		protected Expansion doBuild() {
			return new Expansion(identifier, timestamp, total, offset, parameters, contains, after);
		}
	}

}
