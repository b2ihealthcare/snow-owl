/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.exporter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Rf2MaintainerType;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.3
 */
public final class Rf2ConceptExporter extends Rf2CoreComponentExporter<SnomedConceptSearchRequestBuilder, SnomedConcepts, SnomedConcept> {

	public Rf2ConceptExporter(final Rf2ReleaseType releaseType, 
			final Rf2MaintainerType maintainerType, 
			final String nrcCountryCode,
			final String namespace, 
			final String latestEffectiveTime, 
			final boolean includePreReleaseContent, 
			final Collection<String> modules) {

		super(releaseType, 
				maintainerType, 
				nrcCountryCode, 
				namespace, 
				latestEffectiveTime, 
				includePreReleaseContent, 
				modules);
	}

	@Override
	protected String getCoreComponentType() {
		return "Concept";
	}

	@Override
	protected String[] getHeader() {
		return SnomedRf2Headers.CONCEPT_HEADER;
	}

	@Override
	protected SnomedConceptSearchRequestBuilder createComponentSearchRequestBuilder() {
		return SnomedRequests
				.prepareSearchConcept()
				.sortBy(SortField.ascending(SnomedConceptDocument.Fields.ID));
	}

	@Override
	protected Stream<List<String>> getMappedStream(final SnomedConcepts results, 
			final RepositoryContext context, 
			final String branch) {
		
		return results.stream()
				.map(concept -> ImmutableList.of(concept.getId(),		// id
						getEffectiveTime(concept),						// effectiveTime 
						getActive(concept),								// active
						concept.getModuleId(),							// moduleId
						concept.getDefinitionStatus().getConceptId()));	// definitionStatus
	}
}
