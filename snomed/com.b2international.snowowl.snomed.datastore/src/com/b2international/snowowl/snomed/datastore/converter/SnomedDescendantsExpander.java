/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.converter;

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedAncestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedParents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.active;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.collect.LongSets;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.DescendantsExpander;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Functions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * @since 7.7
 */
public final class SnomedDescendantsExpander extends DescendantsExpander<SnomedConcept> {

	private final boolean stated;

	public SnomedDescendantsExpander(BranchContext context, Options expand, List<ExtendedLocale> locales, String descendantExpandKey) {
		super(context, expand, locales, descendantExpandKey);
		this.stated = SnomedConcept.Expand.STATED_DESCENDANTS.equals(descendantExpandKey);
	}
	
	@Override
	protected void expand(List<SnomedConcept> results, final Set<String> conceptIds, Options descendantExpandOptions, boolean direct) {
		try {
			final int limit = getLimit(descendantExpandOptions);
			
			final ExpressionBuilder expression = Expressions.builder();
			expression.filter(active());
			final ExpressionBuilder descendantFilter = Expressions.builder();
			if (stated) {
				descendantFilter.should(statedParents(conceptIds));
				if (!direct) {
					descendantFilter.should(statedAncestors(conceptIds));
				}
			} else {
				descendantFilter.should(parents(conceptIds));
				if (!direct) {
					descendantFilter.should(ancestors(conceptIds));
				}
			}
			expression.filter(descendantFilter.build());
			
			final Query<SnomedConceptDocument> query = Query.select(SnomedConceptDocument.class)
					.where(expression.build())
					.limit((conceptIds.size() == 1 && limit == 0) ? limit : Integer.MAX_VALUE)
					.build();
			
			final RevisionSearcher searcher = context().service(RevisionSearcher.class);
			final Hits<SnomedConceptDocument> hits = searcher.search(query);
			
			if (hits.getTotal() < 1) {
				final SnomedConcepts descendants = new SnomedConcepts(0, 0);
				for (SnomedConcept concept : results) {
					if (stated) {
						concept.setStatedDescendants(descendants);
					} else {
						concept.setDescendants(descendants);
					}
				}
				return;
			}
			
			// in case of only one match and limit zero, use shortcut instead of loading all IDs and components
			// XXX won't work if number of results is greater than one, either use custom ConceptSearch or figure out how to expand descendants effectively
			if (conceptIds.size() == 1 && limit == 0) {
				for (SnomedConcept concept : results) {
					final SnomedConcepts descendants = new SnomedConcepts(0, hits.getTotal());
					if (stated) {
						concept.setStatedDescendants(descendants);
					} else {
						concept.setDescendants(descendants);
					}
				}
				return;
			}
			
			final Multimap<String, String> descendantsByAncestor = TreeMultimap.create();
			for (SnomedConceptDocument hit : hits) {
				final Set<String> parentsAndAncestors = newHashSet();
				if (stated) {
					parentsAndAncestors.addAll(LongSets.toStringSet(hit.getStatedParents()));
					if (!direct) {
						parentsAndAncestors.addAll(LongSets.toStringSet(hit.getStatedAncestors()));
					}
				} else {
					parentsAndAncestors.addAll(LongSets.toStringSet(hit.getParents()));
					if (!direct) {
						parentsAndAncestors.addAll(LongSets.toStringSet(hit.getAncestors()));
					}
				}
				
				parentsAndAncestors.retainAll(conceptIds);
				for (String ancestor : parentsAndAncestors) {
					descendantsByAncestor.put(ancestor, hit.getId());
				}
			}
			
			final Collection<String> componentIds = newHashSet(descendantsByAncestor.values());
			
			if (limit > 0 && !componentIds.isEmpty()) {
				// query descendants again
				final SnomedConcepts descendants = SnomedRequests.prepareSearchConcept()
						.all()
						.filterByIds(componentIds)
						.setLocales(locales())
						.setExpand(descendantExpandOptions.get("expand", Options.class))
						.build()
						.execute(context());
				
				final Map<String, SnomedConcept> descendantsById = newHashMap();
				descendantsById.putAll(Maps.uniqueIndex(descendants, SnomedConcept::getId));
				for (SnomedConcept concept : results) {
					final Collection<String> descendantIds = descendantsByAncestor.get(concept.getId());
					final List<SnomedConcept> currentDescendants = FluentIterable.from(descendantIds).limit(limit).transform(Functions.forMap(descendantsById)).toList();
					final SnomedConcepts descendantConcepts = new SnomedConcepts(currentDescendants, null, limit, descendantIds.size());
					if (stated) {
						concept.setStatedDescendants(descendantConcepts);
					} else {
						concept.setDescendants(descendantConcepts);
					}
				}
			} else {
				for (SnomedConcept concept : results) {
					final Collection<String> descendantIds = descendantsByAncestor.get(concept.getId());
					final SnomedConcepts descendants = new SnomedConcepts(limit, descendantIds.size());
					if (stated) {
						concept.setStatedDescendants(descendants);
					} else {
						concept.setDescendants(descendants);
					}
				}
			}
			
		} catch (IOException e) {
			throw SnowowlRuntimeException.wrap(e);
		}
	}
	
}
