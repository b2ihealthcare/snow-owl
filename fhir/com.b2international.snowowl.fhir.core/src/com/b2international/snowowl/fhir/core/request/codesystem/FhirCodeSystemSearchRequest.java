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
package com.b2international.snowowl.fhir.core.request.codesystem;

import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedCodeSystemRequestProperties;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequest;

/**
 * @since 8.0
 */
final class FhirCodeSystemSearchRequest extends FhirResourceSearchRequest<CodeSystem.Builder, CodeSystem> {

	private static final long serialVersionUID = 1L;
	private static final Set<String> EXTERNAL_FHIR_CODESYSTEM_FIELDS = Set.of(
		CodeSystem.Fields.COUNT,
		CodeSystem.Fields.CONTENT,
		CodeSystem.Fields.CONCEPT,
		CodeSystem.Fields.FILTER,
		CodeSystem.Fields.PROPERTY,
		CodeSystem.Fields.IDENTIFIER
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
	protected CodeSystem.Builder createResourceBuilder() {
		return CodeSystem.builder();
	}
	
	@Override
	protected void expandResourceSpecificFields(RepositoryContext context, CodeSystem.Builder entry, ResourceFragment resource) {
		// treat all CodeSystems complete by default, later we might add this field to the document, if needed
		entry.content(CodeSystemContentMode.COMPLETE);
		
		includeIfFieldSelected(CodeSystem.Fields.IDENTIFIER, () -> {
			if (!CompareUtils.isEmpty(resource.getOid())) {
				return Identifier.builder()
						.use(IdentifierUse.OFFICIAL)
						.system(resource.getUrl())
						.value(resource.getOid())
						.build();
			} else {
				return null;
			}
		}, entry::addIdentifier);
		
		FhirCodeSystemResourceConverter converter = context.service(RepositoryManager.class)
				.get(resource.getToolingId())
				.optionalService(FhirCodeSystemResourceConverter.class)
				.orElse(FhirCodeSystemResourceConverter.DEFAULT);
		
		includeIfFieldSelected(CodeSystem.Fields.COUNT, () -> converter.count(context, resource.getResourceURI()), entry::count);
		includeIfFieldSelected(CodeSystem.Fields.CONCEPT, () -> converter.expandConcepts(context, resource.getResourceURI(), locales()), entry::concepts);
		includeIfFieldSelected(CodeSystem.Fields.FILTER, () -> converter.expandFilters(context, resource.getResourceURI(), locales()), entry::filters);
		includeIfFieldSelected(CodeSystem.Fields.PROPERTY, () -> converter.expandProperties(context, resource.getResourceURI(), locales()), properties -> {
			properties.stream()
				.filter(p -> !(SupportedCodeSystemRequestProperties.class.isInstance(p)))
				.map(prop -> SupportedConceptProperty.builder(prop).build())
				.forEach(entry::addProperty);
		});
	}
	
}
