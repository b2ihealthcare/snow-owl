/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * FHIR Instant datatype
 * 
 * An instant in time - known at least to the second and always includes a time zone. 
 * Note: This is intended for precisely observed times (typically system logs etc.), 
 * and not human-reported times - for them, use date and dateTime. 
 * Instant is a more constrained dateTime
 *
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#instant">FHIR:Data Types:Instant</a>
 * @since 6.6
 */
public class Instant extends Element {
	
	@NotNull
	private java.time.Instant instant;
	
	@JsonCreator
	Instant(String instant) {
		this(null, null, FhirDates.parseDate(instant).toInstant());
	}
	
	@SuppressWarnings("rawtypes")
	Instant(String id, List<Extension> extensions, java.time.Instant instant) {
		super(id, extensions);
		this.instant = instant;
	}
	
	@JsonValue
	public String getInstant() {
		return instant.toString();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, Instant> {
		
		private java.time.Instant instant;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder instant(java.time.Instant instant) {
			this.instant = instant;
			return getSelf();
		}
		
		public Builder instant(long ms) {
			this.instant = new Date(ms).toInstant();
			return getSelf();
		}
		
		public Builder instant(Date date) {
			this.instant = date.toInstant();
			return getSelf();
		}
		
		@JsonCreator
		public Builder instant(String instant) {
			this.instant = java.time.Instant.parse(instant);
			return getSelf();
		}
		
		public Builder instant(LocalDateTime localDateTime, String zoneId) {
			this.instant = ZonedDateTime.of(localDateTime, ZoneId.of(zoneId)).toInstant();
			return getSelf();
		}
		
		@Override
		protected Instant doBuild() {
			return new Instant(id, extensions, instant);
		}
	}

}
