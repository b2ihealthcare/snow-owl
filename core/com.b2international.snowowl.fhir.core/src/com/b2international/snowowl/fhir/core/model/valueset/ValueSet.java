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
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.TerminologyResource;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.usagecontext.UsageContext;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion;
import com.b2international.snowowl.fhir.core.search.Summary;
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
	
	@Summary
	@JsonProperty
	private final Boolean immutable;
	
	@Summary
	@JsonProperty
	private final Boolean extensible;
	
	//at least one compose or expansion should exist
	@Valid
	@JsonProperty("compose")
	private final Collection<Compose> composeParts;
	
	@Valid
	@JsonProperty
	private final Expansion expansion;
	
	@SuppressWarnings("rawtypes")
	public ValueSet(Id id, final Meta meta, final Uri impliciteRules, Code language, Narrative text,
			
			final Uri url, final Identifier identifier, final String version, final String name, final String title, Code status, final Date date, String publisher, 
			final ContactDetail contact, String description, final Collection<UsageContext> usageContexts,
			final CodeableConcept jurisdiction, final Boolean immutable, final String purpose, final String copyright,
			final Boolean extensible, final Collection<Compose> composeParts, final Expansion expansion) {
		
		super(id, meta, impliciteRules, language, text, url, identifier, version, name, title, status, date, publisher, contact,
				description, usageContexts, jurisdiction, purpose, copyright);
		
		this.immutable = immutable;
		this.extensible = extensible;
		this.composeParts = composeParts;
		this.expansion = expansion;
	}
	
	/**
	 * To create a builder without resource ID (dynamically created or to be persisted)
	 * @return
	 */
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(String valueSetId) {
		return new Builder(valueSetId);
	}

	public static class Builder extends TerminologyResource.Builder<Builder, ValueSet> {

		private Boolean immutable;
		private Boolean extensible;
		private Collection<Compose> composeParts = Lists.newArrayList();
		private Expansion expansion;
		
		public Builder() {
		}
		
		public Builder(String valueSetId) {
			super(valueSetId);
		}
		
		public Builder immutable(Boolean immutable) {
			this.immutable = immutable;
			return getSelf();
		}
		
		public Builder extensible(Boolean extensible) {
			this.extensible = extensible;
			return getSelf();
		}
		
		public Builder addCompose(final Compose compose) {
			this.composeParts.add(compose);
			return getSelf();
		}
		
		public Builder expansion(Expansion expansion) {
			this.expansion = expansion;
			return getSelf();
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		@Override
		protected ValueSet doBuild() {
			
			//cross field validation
			//if (composeParts.isEmpty() && expansion == null) {
			//	throw new FhirException("No 'compose' or 'expansion' fields are defined for the value set.", "ValueSet");
			//}
			
			return new ValueSet(id, meta, implicitRules, language, text, url, identifier, version, name, 
					title, status, date, publisher, contact, description, usageContexts, jurisdiction, immutable, 
					purpose, copyright, extensible, composeParts, expansion);
		}
	}
		
}
