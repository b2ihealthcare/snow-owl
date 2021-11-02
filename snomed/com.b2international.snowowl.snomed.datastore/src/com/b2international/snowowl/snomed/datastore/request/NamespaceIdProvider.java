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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * @since 8.0
 */
public interface NamespaceIdProvider {

	Pattern NAMESPACE_PATTERN = Pattern.compile(SnomedTerminologyComponentConstants.DEFAULT_NAMESPACE_PATTERN);
	
	NamespaceIdProvider DEFAULT = new NamespaceIdProvider() {};

	default Map<String, String> extractNamespaceIds(BranchContext context, final Collection<String> namespaceConceptIds, boolean ignoreInvalidValues) {
		final Set<String> mutableNamespaceConceptIds = Sets.newHashSet(namespaceConceptIds);
		final ImmutableMap.Builder<String, String> namespacesByNamespaceConceptId = ImmutableMap.builderWithExpectedSize(mutableNamespaceConceptIds.size());  
				
		// Keep only valid SCTIDs passed in to the filter
		if (ignoreInvalidValues) {
			mutableNamespaceConceptIds.removeIf(id -> !SnomedIdentifiers.isValid(id));
		} else {
			final Set<String> invalidNamespaceConceptIds = mutableNamespaceConceptIds.stream().filter(id -> !SnomedIdentifiers.isValid(id)).collect(ImmutableSortedSet.toImmutableSortedSet(Comparator.naturalOrder()));
			if (!invalidNamespaceConceptIds.isEmpty()) {
				throw new BadRequestException("The following namespaceConceptId values are invalid SNOMED CT Concept identifiers, %s", invalidNamespaceConceptIds.toString());
			}
		}
		
		/* 
		 * The International core namespace concept will not have an FSN matching the pattern,
		 * so remove it from the set, and convert it to the empty namespace directly. 
		 */
		if (mutableNamespaceConceptIds.remove(Concepts.CORE_NAMESPACE)) {
			namespacesByNamespaceConceptId.put(Concepts.CORE_NAMESPACE, "");
		}
		
		// Find the FSN of namespace SCTIDs
		SnomedRequests.prepareSearchDescription()
			.filterByActive(true)
			.filterByType(Concepts.FULLY_SPECIFIED_NAME)
			.filterByConcepts(mutableNamespaceConceptIds)
			.setFields(SnomedDescriptionIndexEntry.Fields.ID, SnomedDescriptionIndexEntry.Fields.CONCEPT_ID, SnomedDescriptionIndexEntry.Fields.TERM)
			.setLimit(1000)
			.stream(context)
			.flatMap(SnomedDescriptions::stream)
			.forEach(fsn -> {
				// Extract namespace from description terms
				final Matcher matcher = NAMESPACE_PATTERN.matcher(fsn.getTerm());
				if (matcher.matches()) {
					namespacesByNamespaceConceptId.put(fsn.getConceptId(), matcher.group(1));
				}
			});
		
		return namespacesByNamespaceConceptId.build();
	}
	
}
