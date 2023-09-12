/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemSearchRequestBuilder.*;

import java.util.List;
import java.util.Set;

import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r5.model.Enumerations.CodeSystemContentMode;
import org.hl7.fhir.r5.terminologies.CodeSystemUtilities;

import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.fhir.core.model.ResourceConstants;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequest;
import com.google.common.collect.ImmutableSet;

/**
 * @since 8.0
 */
final class FhirCodeSystemSearchRequest extends FhirResourceSearchRequest<CodeSystem> {

	private static final long serialVersionUID = 1L;

	private static final Set<String> CODE_SYSTEM_FHIR_ELEMENTS = ImmutableSet.of(
		CODE_SYSTEM_COUNT,
		CODE_SYSTEM_CONTENT,
		CODE_SYSTEM_CONCEPT,
		CODE_SYSTEM_FILTER,
		CODE_SYSTEM_PROPERTY,
		CODE_SYSTEM_IDENTIFIER
	);

	@Override
	protected String getResourceType() {
		return com.b2international.snowowl.core.codesystem.CodeSystem.RESOURCE_TYPE;
	}

	@Override
	protected Set<String> getExternalFhirElements() {
		return CODE_SYSTEM_FHIR_ELEMENTS;
	}

	@Override
	protected CodeSystem createEmptyResource() {
		return new CodeSystem();
	}

	@Override
	protected void expandResourceSpecificFields(final RepositoryContext context, final CodeSystem codeSystem, final ResourceFragment fragment) {
		includeIfFieldSelected(CODE_SYSTEM_COPYRIGHT, fragment::getCopyright, codeSystem::setCopyright);
		includeIfFieldSelected(CODE_SYSTEM_IDENTIFIER, fragment::getOid, oid -> { if (oid != null) { CodeSystemUtilities.setOID(codeSystem, oid); } });

		final ResourceURI resourceURI = fragment.getResourceURI();
		codeSystem.setUserData(ResourceConstants.RESOURCE_URI, resourceURI);
		
		final FhirCodeSystemResourceConverter converter = context.service(RepositoryManager.class)
			.get(fragment.getToolingId())
			.optionalService(FhirCodeSystemResourceConverter.class)
			.orElse(FhirCodeSystemResourceConverter.DEFAULT);

		if (fields().isEmpty() || fields().contains(CODE_SYSTEM_CONCEPT)) {
			// XXX: When "concept" is requested to be included, we also need a total concept count to set "content" properly
			final List<ConceptDefinitionComponent> concepts = converter.expandConcepts(context, resourceURI, locales());
			final int count = converter.count(context, resourceURI);

			codeSystem.setConcept(concepts);
			codeSystem.setCount(count);

			if (concepts.size() == 0) {
				codeSystem.setContent(CodeSystemContentMode.NOTPRESENT);	
			} else if (concepts.size() == count) {
				codeSystem.setContent(CodeSystemContentMode.COMPLETE);
			} else {
				/*
				 * If the total concept count differs from the returned list's size, content is
				 * to be considered partial. We have two values to represent this scenario,
				 * "example" and "fragment", but the latter implies a curated subset, whereas
				 * the former is intended for subsets without a specific intent.
				 */				
				codeSystem.setContent(CodeSystemContentMode.EXAMPLE);
			}

		} else {
			includeIfFieldSelected(CODE_SYSTEM_COUNT, () -> converter.count(context, resourceURI), codeSystem::setCount);
			codeSystem.setContent(CodeSystemContentMode.NOTPRESENT);	
		}

		// XXX: Previously some commonly accepted properties were filtered out
		includeIfFieldSelected(CODE_SYSTEM_PROPERTY, () -> converter.expandProperties(context, resourceURI, locales()), codeSystem::setProperty);
		includeIfFieldSelected(CODE_SYSTEM_FILTER, () -> converter.expandFilters(context, resourceURI, locales()), codeSystem::setFilter);
	}
}
