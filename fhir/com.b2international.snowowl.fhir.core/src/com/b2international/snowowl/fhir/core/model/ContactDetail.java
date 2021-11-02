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
package com.b2international.snowowl.fhir.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Contact detail object
 * 
 * @see <a href="https://www.hl7.org/fhir/metadatatypes.html#ContactDetail>FHIR:ContactDetail</a>
 * @since 6.6
 */
@JsonDeserialize(builder = ContactDetail.Builder.class)
public class ContactDetail extends Element {
	
	@Summary
	private String name;
	
	@Summary
	private Collection<ContactPoint> contactPoints;

	/**
	 * @param id
	 * @param extensions
	 */
	ContactDetail(final String id, final List<Extension<?>> extensions, final String name, final Collection<ContactPoint> contactPoints) {
		super(id, extensions);
		this.name = name;
		this.contactPoints = contactPoints;
	}


	public String getName() {
		return name;
	}

	@JsonProperty("telecom")
	public Collection<ContactPoint> getTelecoms() {
		return contactPoints;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Element.Builder<Builder, ContactDetail> {
		
		private String name;
		private Collection<ContactPoint> telecoms;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder name(String name) {
			this.name = name;
			return getSelf();
		}
		
		@JsonProperty("telecom")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder telecoms(List<ContactPoint> telecoms) {
			this.telecoms = telecoms;
			return getSelf();
		}
		
		public Builder addTelecom(ContactPoint telecom) {
			if (telecoms == null) {
				telecoms = new ArrayList<ContactPoint>();
			}
			telecoms.add(telecom);
			return getSelf();
		}
		
		@Override
		protected ContactDetail doBuild() {
			return new ContactDetail(id, extensions, name, telecoms);
		}
	}

}
