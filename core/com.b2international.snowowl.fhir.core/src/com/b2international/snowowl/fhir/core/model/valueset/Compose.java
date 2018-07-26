/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.valueset;

import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

/**
 * FHIR Value set compose backbone element
 * 
 * @since 6.4
 */
public class Compose {
	
	@JsonProperty
	private final Date lockedDate;
	
	@JsonProperty("inactive")
	private final Boolean isInactive;
	
	@Valid
	@NotEmpty
	@JsonProperty("include")
	private final Collection<Include> includes;
	
	@Valid
	@JsonProperty("exclude")
	private final Collection<Include> excludes;
	
	Compose(Date lockedDate, Boolean isInactive, Collection<Include> includes, Collection<Include> excludes) {
		this.lockedDate = lockedDate;
		this.isInactive = isInactive;
		this.includes = includes;
		this.excludes = excludes;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ValidatingBuilder<Compose> {
		
		private Date lockedDate;
		
		private Boolean isInactive;
		
		private Collection<Include> includes = Sets.newHashSet();

		private Collection<Include> excludes = Sets.newHashSet();
		
		public Builder lockedDate(final Date lockedDate) {
			this.lockedDate = lockedDate;
			return this;
		}

		public Builder inactive(final boolean isInactive) {
			this.isInactive = isInactive;
			return this;
		}
		
		public Builder addInclude(final Include include) {
			this.includes.add(include);
			return this;
		}
		
		public Builder addExclude(final Include exclude) {
			this.excludes.add(exclude);
			return this;
		}
		
		@Override
		protected Compose doBuild() {
			return new Compose(lockedDate, isInactive, includes, excludes);
		}
	}

}
