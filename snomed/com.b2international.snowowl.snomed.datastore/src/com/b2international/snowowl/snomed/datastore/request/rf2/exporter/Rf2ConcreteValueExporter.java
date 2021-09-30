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
package com.b2international.snowowl.snomed.datastore.request.rf2.exporter;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @since 7.17
 */
public final class Rf2ConcreteValueExporter 
	extends Rf2CoreComponentExporter<SnomedRelationshipSearchRequestBuilder, SnomedRelationships, SnomedRelationship> {

	private static final Set<RelationshipValueType> VALUE_TYPES = ImmutableSet.copyOf(RelationshipValueType.values());

	public Rf2ConcreteValueExporter(final Rf2ReleaseType releaseType, 
		final String countryNamespaceElement,
		final String namespaceFilter, 
		final String transientEffectiveTime, 
		final String archiveEffectiveTime, 
		final Collection<String> modules) {

		super(releaseType, 
			countryNamespaceElement, 
			namespaceFilter, 
			transientEffectiveTime, 
			archiveEffectiveTime, 
			modules);
	}

	@Override
	protected String getCoreComponentType() {
		return "RelationshipConcreteValues";
	}

	@Override
	protected String[] getHeader() {
		return SnomedRf2Headers.CONCRETE_VALUE_HEADER;
	}

	@Override
	protected SnomedRelationshipSearchRequestBuilder createComponentSearchRequestBuilder() {
		return SnomedRequests
			.prepareSearchRelationship()
			.filterByValueTypes(VALUE_TYPES)
			.sortBy(Sort.fieldAsc(SnomedRelationshipIndexEntry.Fields.ID));
	}

	@Override
	protected Stream<List<String>> getMappedStream(final SnomedRelationships results, 
			final RepositoryContext context, 
			final String branch) {

		return results.stream()
            .map(result -> ImmutableList.of(
                result.getId(),                   // id
                getEffectiveTime(result),         // effectiveTime 
                getActive(result),                // active
                result.getModuleId(),             // moduleId
                result.getSourceId(),             // sourceId
                result.getValue(),                // value
                result.getRelationshipGroup().toString(),     // relationshipGroup
                result.getTypeId(),               // typeId
                result.getCharacteristicTypeId(), // characteristicTypeId
                result.getModifierId()));         // modifierId
	}
}
