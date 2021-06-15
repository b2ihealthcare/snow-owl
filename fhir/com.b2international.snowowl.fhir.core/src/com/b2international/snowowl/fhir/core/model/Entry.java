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

import java.io.Serializable;
import java.util.Collection;

import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Entry BackBone element in the Bundle domain object
 * 
 * @since 6.3
 */
public final class Entry implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Collection<String> links;
	
	private Uri fullUrl;
	
	private FhirResource resource;
	
	public Entry(final Uri fullUrl, final FhirResource resource) {
		this.fullUrl = fullUrl;
		this.resource = resource;
	}

	@JsonProperty("link")
	public Collection<String> getLinks() {
		return links;
	}
	
	public Uri getFullUrl() {
		return fullUrl;
	}
	
	public FhirResource getResource() {
		return resource;
	}

}
