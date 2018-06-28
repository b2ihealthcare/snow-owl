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
package com.b2international.snowowl.fhir.core.model.valueset;

import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;

import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.TerminologyResource;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModel;

/**
 * A value set contains a set of codes from those defined by one or more code systems to specify which codes can be used in a particular context.
 * 
 * Value sets aspects:
 * <ul>
 * <li>.compose: A definition of which codes are intended to be in the value set ("intension")
 * <li>.expansion: The list of codes that are actually in the value set under a given set of conditions ("extension")
 * </ul>
 * 
 * @see <a href="https://www.hl7.org/fhir/valueset.html">FHIR:ValueSet</a>
 * @since 6.3
 */
@ApiModel("ValueSet")
public class ValueSet extends TerminologyResource {
	
	//FHIR header "resourceType" : "ValueSet",
	@JsonProperty
	private final String resourceType = "ValueSet";
	
	@Valid
	@JsonProperty("compose")
	private final Collection<Compose> composeParts;
	
	public ValueSet(Id id, Code language, Narrative text, Uri url, Identifier identifier, String version, String name, 
			String title, Code status, final Date date, final ContactDetail contact, String publisher, String description, Collection<Compose> composeParts) {
		
		super(id, language, text, url, identifier, version, name, title, status, date, contact, publisher, description);
		this.composeParts = composeParts;
	}
	
	public static Builder builder(String valueSetId) {
		return new Builder(valueSetId);
	}

	public static class Builder extends TerminologyResource.Builder<Builder, ValueSet> {

		private Collection<Compose> composeParts = Lists.newArrayList();
		
		public Builder(String valueSetId) {
			super(valueSetId);
		}
		
		public Builder addCompose(final Compose compose) {
			this.composeParts.add(compose);
			return getSelf();
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		@Override
		protected ValueSet doBuild() {
			return new ValueSet(id, language, text, url, identifier, version, name, 
					title, status, date, contact, publisher, description, composeParts);
		}
	}
		
}
