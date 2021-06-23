/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * FHIR Entry BackBone element in the Bundle domain object
 * 
 * @since 8.0.0
 */
public class ResourceEntry extends Entry {
	
	private FhirResource resource;
	
	protected ResourceEntry(final Collection<String> links, final Uri fullUrl, final FhirResource resource) {
		super(links, fullUrl);
		this.resource = resource;
	}

	public FhirResource getResource() {
		return resource;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Entry.Builder<Builder, ResourceEntry> {
		
		private FhirResource fhirResource;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder resource(FhirResource fhirResource) {
			this.fhirResource = fhirResource;
			return getSelf();
		}
		
		@Override
		protected ResourceEntry doBuild() {
			return new ResourceEntry(links, fullUrl, fhirResource);
		}
		
	}

}
