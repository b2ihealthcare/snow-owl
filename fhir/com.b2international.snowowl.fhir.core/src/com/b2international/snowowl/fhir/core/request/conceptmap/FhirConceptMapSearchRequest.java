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
package com.b2international.snowowl.fhir.core.request.conceptmap;

import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap.Builder;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequest;

/**
 * @since 8.0
 */
final class FhirConceptMapSearchRequest extends FhirResourceSearchRequest<ConceptMap.Builder, ConceptMap> {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getResourceType() {
		return "conceptmaps";
	}
	
	@Override
	protected Builder createResourceBuilder() {
		return ConceptMap.builder();
	}
	
	@Override
	protected void expandResourceSpecificFields(RepositoryContext context, Builder entry, ResourceFragment resource) {
		FhirConceptMapResourceConverter converter = context.service(RepositoryManager.class)
				.get(resource.getToolingId())
				.optionalService(FhirConceptMapResourceConverter.class)
				.orElse(FhirConceptMapResourceConverter.DEFAULT);
		
		includeIfFieldSelected(ConceptMap.Fields.GROUP, () -> converter.expandMembers(context, resource.getResourceURI()), entry::groups);
	}
	
}
