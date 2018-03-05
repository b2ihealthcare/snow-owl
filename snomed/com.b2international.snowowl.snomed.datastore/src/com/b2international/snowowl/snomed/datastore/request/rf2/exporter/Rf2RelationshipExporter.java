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
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.3
 */
public final class Rf2RelationshipExporter extends Rf2CoreComponentExporter<SnomedRelationshipSearchRequestBuilder, SnomedRelationships, SnomedRelationship> {

	private final String characteristicTypeExpression;

	public Rf2RelationshipExporter(final Rf2ReleaseType releaseType, 
			final String countryNamespaceElement,
			final String namespaceFilter, 
			final String transientEffectiveTime, 
			final String archiveEffectiveTime, 
			final boolean includePreReleaseContent, 
			final Collection<String> modules,
			final String characteristicTypeExpression) {

		super(releaseType, 
				countryNamespaceElement, 
				namespaceFilter, 
				transientEffectiveTime, 
				archiveEffectiveTime, 
				includePreReleaseContent, 
				modules);

		this.characteristicTypeExpression = characteristicTypeExpression;
	}

	@Override
	protected String getCoreComponentType() {
		return Concepts.STATED_RELATIONSHIP.equals(characteristicTypeExpression) 
				? "StatedRelationship"
				: "Relationship";
	}

	@Override
	protected String[] getHeader() {
		return SnomedRf2Headers.RELATIONSHIP_HEADER;
	}

	@Override
	protected SnomedRelationshipSearchRequestBuilder createComponentSearchRequestBuilder() {
		return SnomedRequests
				.prepareSearchRelationship()
				.filterByCharacteristicType(characteristicTypeExpression)
				.sortBy(SortField.ascending(SnomedConceptDocument.Fields.ID));
	}

	@Override
	protected Stream<List<String>> getMappedStream(final SnomedRelationships results, 
			final RepositoryContext context, 
			final String branch) {
		
		return results.stream()
				.map(relationship -> ImmutableList.of(relationship.getId(),	// id
						getEffectiveTime(relationship),							// effectiveTime 
						getActive(relationship),								// active
						relationship.getModuleId(),								// moduleId
						relationship.getSourceId(),								// sourceId
						relationship.getDestinationId(),						// destinationId
						relationship.getGroup().toString(),						// group
						relationship.getTypeId(),								// typeId
						relationship.getCharacteristicType().getConceptId(),	// characteristicTypeId
						relationship.getModifier().getConceptId()));				// modifierId
	}
}
