/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.List;

import org.hl7.fhir.r5.model.ConceptMap;

import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.fhir.core.R5ObjectFields;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequest;

/**
 * @since 8.0
 */
final class FhirConceptMapSearchRequest extends FhirResourceSearchRequest<ConceptMap> {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getResourceType() {
		return "conceptmaps";
	}
	
	@Override
	protected ConceptMap createResource() {
		return new ConceptMap();
	}
	
	@Override
	protected void configureFieldsToLoad(List<String> fields) {
		fields.remove(R5ObjectFields.ConceptMap.GROUP);
	}
	
	@Override
	protected void expandResourceSpecificFields(RepositoryContext context, ConceptMap entry, ResourceFragment resource) {
		includeIfFieldSelected(R5ObjectFields.CodeSystem.COPYRIGHT, resource::getCopyright, entry::setCopyright);

		FhirConceptMapResourceConverter converter = context.service(RepositoryManager.class)
				.get(resource.getToolingId())
				.optionalService(FhirConceptMapResourceConverter.class)
				.orElse(FhirConceptMapResourceConverter.DEFAULT);
		
		includeIfFieldSelected(R5ObjectFields.ConceptMap.GROUP, () -> converter.expandMembers(context, resource.getResourceURI()), entry::setGroup);
	}
	
}
