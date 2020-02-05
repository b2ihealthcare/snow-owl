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
package com.b2international.snowowl.fhir.core.model.dt;

import java.util.Collection;

import javax.validation.constraints.Min;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR ContactPoint datatype
 * 
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#contactpoint">FHIR:Data Types:ContactPoint</a>
 * @since 6.6
 */
public class ContactPoint extends Element {
	
	@Summary
	@JsonProperty
	private Code system;
	
	@Summary
	@JsonProperty
	private String value;
	
	@Summary
	@JsonProperty
	private Code use;
	
	@Summary
	@Min(value = 1, message = "Rank must be larger than 0")
	@JsonProperty
	private Integer rank;
	
	@Summary
	@JsonProperty
	private Period period;
	
	public Code getSystem() {
		return system;
	}

	public String getValue() {
		return value;
	}

	public Code getUse() {
		return use;
	}

	public Integer getRank() {
		return rank;
	}

	public Period getPeriod() {
		return period;
	}
	
	/**
	 * @param id
	 * @param extensions
	 */
	ContactPoint(final String id, final Collection<Extension> extensions, 
			final Code system, final String value, final Code use, final Integer rank, final Period period) {
		super(id, extensions);
		this.system = system;
		this.value = value;
		this.use = use;
		this.rank = rank;
		this.period = period;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, ContactPoint> {
		
		private Code system;
		private String value;
		private Code use;
		private Integer rank;
		private Period period;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder system(String system) {
			this.system = new Code(system);
			return getSelf();
		}
		
		public Builder system(Code systemCode) {
			this.system = systemCode;
			return getSelf();
		}
		
		public Builder value(String value) {
			this.value = value;
			return getSelf();
		}
		
		public Builder use(String use) {
			this.use = new Code(use);
			return getSelf();
		}
		
		public Builder use(Code useCode) {
			this.use = useCode;
			return getSelf();
		}
		
		public Builder rank(Integer rank) {
			this.rank = rank;
			return getSelf();
		}
		
		public Builder period(Period period) {
			this.period = period;
			return getSelf();
		}
		
		@Override
		protected ContactPoint doBuild() {
			return new ContactPoint(id, extensions, system, value, use, rank, period);
		}
	}

}
