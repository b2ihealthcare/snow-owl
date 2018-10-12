/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.fhir.core.model.dt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@SuppressWarnings("rawtypes")
	Instant(String id, Collection<Extension> extensions, java.time.Instant instant) {
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
