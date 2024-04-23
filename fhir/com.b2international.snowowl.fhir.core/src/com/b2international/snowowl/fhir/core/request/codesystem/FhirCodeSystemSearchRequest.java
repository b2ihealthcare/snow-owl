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
package com.b2international.snowowl.fhir.core.request.codesystem;

import java.util.List;
import java.util.Set;

import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.Enumerations.CodeSystemContentMode;
import org.hl7.fhir.r5.model.Identifier;
import org.hl7.fhir.r5.model.Identifier.IdentifierUse;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.fhir.core.R5ObjectFields;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequest;

/**
 * @since 8.0
 */
final class FhirCodeSystemSearchRequest extends FhirResourceSearchRequest<CodeSystem> {

	private static final long serialVersionUID = 1L;
	private static final Set<String> EXTERNAL_FHIR_CODESYSTEM_FIELDS = Set.of(
		R5ObjectFields.CodeSystem.COUNT,
		R5ObjectFields.CodeSystem.CONTENT,
		R5ObjectFields.CodeSystem.CONCEPT,
		R5ObjectFields.CodeSystem.FILTER,
		R5ObjectFields.CodeSystem.PROPERTY,
		R5ObjectFields.CodeSystem.IDENTIFIER
	);
	
	@Override
	protected String getResourceType() {
		return com.b2international.snowowl.core.codesystem.CodeSystem.RESOURCE_TYPE;
	}
	
	@Override
	protected Set<String> getExternalFhirResourceFields() {
		return EXTERNAL_FHIR_CODESYSTEM_FIELDS;
	}

	@Override
	protected CodeSystem createResource() {
		return new CodeSystem();
	}
	
	@Override
	protected void expandResourceSpecificFields(RepositoryContext context, CodeSystem entry, ResourceFragment resource) {
		includeIfFieldSelected(R5ObjectFields.CodeSystem.COPYRIGHT, resource::getCopyright, entry::setCopyright);
		includeIfFieldSelected(R5ObjectFields.CodeSystem.IDENTIFIER, () -> {
			if (!CompareUtils.isEmpty(resource.getOid())) {
				return new Identifier()
						.setUse(IdentifierUse.OFFICIAL)
						.setSystem(resource.getUrl())
						.setValue(resource.getOid());
			} else {
				return null;
			}
		}, entry::addIdentifier);
		
		FhirCodeSystemResourceConverter converter = context.service(RepositoryManager.class)
				.get(resource.getToolingId())
				.optionalService(FhirCodeSystemResourceConverter.class)
				.orElse(FhirCodeSystemResourceConverter.DEFAULT);
		
		final ResourceURI resourceURI = resource.getResourceURI();
		
		if (fields().isEmpty() || fields().contains(R5ObjectFields.CodeSystem.CONCEPT)) {
			// XXX: When "concept" is requested to be included, we also need a total concept count to set "content" properly
			final List<CodeSystem.ConceptDefinitionComponent> concepts = converter.expandConcepts(context, resourceURI, locales());
			final int count = converter.count(context, resourceURI);

			if (concepts.size() == 0) {
				entry.setContent(CodeSystemContentMode.NOTPRESENT);
			} else if (concepts.size() == count) {
				entry.setContent(CodeSystemContentMode.COMPLETE);
			} else {
				/*
				 * If the total concept count differs from the returned list's size, content is
				 * to be considered partial. We have two values to represent this scenario,
				 * "example" and "fragment", but the latter implies a curated subset, whereas
				 * the former is intended for subsets without a specific intent.
				 */				
				entry.setContent(CodeSystemContentMode.EXAMPLE);
			}
			
			entry.setConcept(concepts);
			entry.setCount(count);
		} else {
			entry.setContent(CodeSystemContentMode.NOTPRESENT);
			includeIfFieldSelected(R5ObjectFields.CodeSystem.COUNT, () -> converter.count(context, resourceURI), entry::setCount);
		}
		
		includeIfFieldSelected(R5ObjectFields.CodeSystem.FILTER, () -> converter.expandFilters(context, resourceURI, locales()), entry::setFilter);
		includeIfFieldSelected(R5ObjectFields.CodeSystem.PROPERTY, () -> converter.expandProperties(context, resourceURI, locales()), properties -> {
			properties.stream()
				.filter(p -> !(SupportedCodeSystemRequestProperties.class.isInstance(p)))
				.map(prop -> SupportedConceptProperty.builder(prop).build())
				.forEach(entry::addProperty);
		});
	}
}
