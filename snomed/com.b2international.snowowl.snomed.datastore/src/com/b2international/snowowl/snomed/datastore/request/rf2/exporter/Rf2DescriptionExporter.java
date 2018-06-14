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
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.3
 */
public final class Rf2DescriptionExporter extends Rf2CoreComponentExporter<SnomedDescriptionSearchRequestBuilder, SnomedDescriptions, SnomedDescription> {

	private final String languageCode;
	private final Collection<String> descriptionTypes;

	public Rf2DescriptionExporter(final Rf2ReleaseType releaseType, 
			final String countryNamespaceElement,
			final String namespaceFilter, 
			final String transientEffectiveTime, 
			final String archiveEffectiveTime, 
			final boolean includePreReleaseContent, 
			final Collection<String> modules,
			final Collection<String> descriptionTypes,
			final String languageCode) {

		super(releaseType, 
				countryNamespaceElement, 
				namespaceFilter, 
				transientEffectiveTime, 
				archiveEffectiveTime, 
				includePreReleaseContent, 
				modules);
		this.descriptionTypes = descriptionTypes;
		this.languageCode = languageCode;
	}

	@Override
	protected String getCoreComponentType() {
		return descriptionTypes.contains(Concepts.TEXT_DEFINITION) // FIXME ugly as fuck
				? "TextDefinition"
				: "Description";
	}
	
	@Override
	protected String getLanguageElement() {
		return "-" + languageCode;
	}

	@Override
	protected String[] getHeader() {
		return SnomedRf2Headers.DESCRIPTION_HEADER;
	}

	@Override
	protected SnomedDescriptionSearchRequestBuilder createComponentSearchRequestBuilder() {
		return SnomedRequests
				.prepareSearchDescription()
				.filterByLanguageCodes(ImmutableList.of(languageCode))
				.filterByType(descriptionTypes)
				.sortBy(SortField.ascending(SnomedConceptDocument.Fields.ID));
	}

	@Override
	protected Stream<List<String>> getMappedStream(final SnomedDescriptions results, 
			final RepositoryContext context, 
			final String branch) {
		
		return results.stream()
				.map(description -> ImmutableList.of(description.getId(),	// id
						getEffectiveTime(description),						// effectiveTime 
						getActive(description),								// active
						description.getModuleId(),							// moduleId
						description.getConceptId(),							// conceptId
						description.getLanguageCode(),						// languageCode
						description.getTypeId(),							// typeId
						description.getTerm(),								// term
						description.getCaseSignificance().getConceptId()));	// caseSignificanceId
	}
}
