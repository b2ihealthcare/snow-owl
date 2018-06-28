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
package com.b2international.snowowl.fhir.core.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR terminology resource with common properties.
 * 
 * @since 6.3
 */
public abstract class TerminologyResource extends DomainResource {
	
	//same as logical id
	@Summary
	@JsonProperty
	private Uri url; //ORG_LINK or hardcoded provider value
	
	@Summary
	@JsonProperty
	private Identifier identifier; //OID
	
	@Summary
	@JsonProperty
	private String version; //not necessarily available - and what to do when we have more than 1??
	
	@Mandatory
	@JsonProperty
	private String name;
	
	@Summary
	@JsonProperty
	private String title;
	
	@Mandatory
	@Valid
	@NotNull
	@JsonProperty
	private Code status;
	
	//Revision date
	@Summary
	@JsonProperty
	private Date date;
	
	@Summary
	@JsonProperty
	private String publisher;
	
	@Summary
	@JsonProperty
	private ContactDetail contact;

	@JsonProperty
	private String description;

	/**
	 * @param id
	 * @param language
	 * @param text
	 */
	public TerminologyResource(Id id, Code language, Narrative text, Uri url, Identifier identifier, String version, 
			String name, String title, Code status, final Date date, String publisher, String description) {
		super(id, language, text);
		this.url = url;
		this.identifier = identifier;
		this.version = version;
		this.name = name;
		this.title = title;
		this.status = status;
		this.date = date;
		this.publisher = publisher;
		this.description = description;
	}
	
	public Uri getUrl() {
		return url;
	}
	
	public static abstract class Builder<B extends Builder<B, T>, T extends FhirResource> extends DomainResource.Builder<B, T> {

		protected Uri url; //ORG_LINK or hardcoded provider value
		
		protected Identifier identifier; //OID
		
		protected String version; //not necessarily available - and what to do when we have more than 1??
		
		protected String name;
		
		protected String title;

		protected Code status;
		
		protected Date date;
		
		protected String publisher;
		
		protected String description;
		
		/**
		 * Use this constructor when a new resource is sent to the server to be created.
		 */
		public Builder() {
		}
		
		public Builder(String resourceId) {
			super(resourceId);
		}
		
		public B url(final Uri url) {
			this.url = url;
			return getSelf();
		}
		
		public B identifier(final Identifier identifer) {
			this.identifier = identifer;
			return getSelf();
		}

		public B version(final String version) {
			this.version = version;
			return getSelf();
		}

		public B name(final String name) {
			this.name = name;
			return getSelf();
		}
		
		public B description(final String description) {
			this.description = description;
			return getSelf();
		}
		
		public B title(final String title) {
			this.title = title;
			return getSelf();
		}

		public B status(PublicationStatus status) {
			this.status = status.getCode();
			return getSelf();
		}
		
		public B date(Date date) {
			this.date = date;
			return getSelf();
		}
		
		public B date(String dateString) {
			DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
			try {
				this.date = df.parse(dateString);
			} catch (ParseException e) {
				throw new IllegalArgumentException(dateString + " cannot be parsed, use the format " + FhirConstants.DATE_TIME_FORMAT, e);
			}
			return getSelf();
		}
		
		public B publisher(String publisher) {
			this.publisher = publisher;
			return getSelf();
		}
	}

}
