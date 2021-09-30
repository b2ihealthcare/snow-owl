/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.namespaces;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;

/**
 * @since 5.3
 */
public abstract class SnomedComponentSearchRequest<R, D extends SnomedComponentDocument> extends SnomedSearchRequest<R, D> {

	private static final Pattern NAMESPACE_PATTERN = Pattern.compile(SnomedTerminologyComponentConstants.DEFAULT_NAMESPACE_PATTERN);
	
	enum OptionKey {
		
		/**
		 * Filters component to be active members of the specified reference sets.
		 */
		ACTIVE_MEMBER_OF,
		
		/**
		 * Filters matches to be active/inactive members of the specified reference sets.
		 */
		MEMBER_OF,
		
		/**
		 * Namespace part of the component ID to match (?)
		 */
		NAMESPACE,
		
		/**
		 * Namespace part of the component ID to match, by namespace concept ID
		 */
		NAMESPACE_CONCEPT_ID,
	}
	
	protected final void addMemberOfClause(BranchContext context, ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.MEMBER_OF)) {
			final Collection<String> refSetFilters = getCollection(OptionKey.MEMBER_OF, String.class);
			final Collection<String> referringRefSetIds = evaluateEclFilter(context, refSetFilters);
			queryBuilder.filter(SnomedComponentDocument.Expressions.memberOf(referringRefSetIds));
		}
	}
	
	protected final void addActiveMemberOfClause(BranchContext context, ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.ACTIVE_MEMBER_OF)) {
			final Collection<String> refSetFilters = getCollection(OptionKey.ACTIVE_MEMBER_OF, String.class);
			final Collection<String> referringRefSetIds = evaluateEclFilter(context, refSetFilters);
			queryBuilder.filter(SnomedComponentDocument.Expressions.activeMemberOf(referringRefSetIds));
		}
	}
	
	protected final void addNamespaceFilter(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.NAMESPACE)) {
			queryBuilder.filter(namespaces(getCollection(OptionKey.NAMESPACE, String.class)));
		}
	}
		
	protected final void addNamespaceConceptIdFilter(BranchContext context, ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.NAMESPACE_CONCEPT_ID)) {
			final Set<String> namespaceConceptIds = newHashSet(getCollection(OptionKey.NAMESPACE_CONCEPT_ID, String.class));
			final Set<String> namespaces = newHashSet();
			
			// Keep only valid SCTIDs passed in to the filter
			namespaceConceptIds.removeIf(id -> !SnomedIdentifiers.isValid(id));
			
			/* 
			 * The International core namespace concept will not have an FSN matching the pattern,
			 * so remove it from the set, and convert it to the empty namespace directly. 
			 */
			if (namespaceConceptIds.remove(Concepts.CORE_NAMESPACE)) {
				namespaces.add("");
			}
			
			// Find the FSN of namespace SCTIDs
			final SnomedDescriptionSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchDescription()
				.filterByActive(true)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.filterByConcepts(namespaceConceptIds)
				.setLimit(1000);
			
			final SearchResourceRequestIterator<SnomedDescriptionSearchRequestBuilder, SnomedDescriptions> requestIterator = new SearchResourceRequestIterator<>(
				requestBuilder, 
				r -> r.build().execute(context));

			// Extract namespace from description terms
			requestIterator.forEachRemaining(batch -> {
				batch.forEach(fsn -> {
					final Matcher matcher = NAMESPACE_PATTERN.matcher(fsn.getTerm());
					if (matcher.matches()) {
						namespaces.add(matcher.group(1));
					}
				});
			});
			
			queryBuilder.filter(namespaces(namespaces));
		}
	}
}
