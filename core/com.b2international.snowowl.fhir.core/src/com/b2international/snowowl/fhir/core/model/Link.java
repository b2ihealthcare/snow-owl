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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Link BackBone element in the Bundle domain object
 * @since 6.3
 */
public class Link {
	
	@NotEmpty
	@JsonProperty
	private String relation = "self";
	
	@Valid
	@NotNull
	@JsonProperty
	private Uri url;
	
	public Link(String relation, Uri url) {
		this.relation = relation;
		this.url = url;
	}

	public Link(Uri url) {
		this.url = url;
	}

	public Link(String urlString) {
		this.url = new Uri(urlString);
	}

}
