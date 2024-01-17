/*
 * Copyright 2020-2023 B2i Healthcare, https://b2ihealthcare.com
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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyIntMap;
import com.b2international.collections.longs.LongSortedSet;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.DescendantsExpander;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Stopwatch;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;

/**
 * @since 7.7
 */
public final class SnomedDescendantsExpander extends DescendantsExpander<SnomedConcept> {

	private final boolean stated;
	private final int batchLimit;

	public SnomedDescendantsExpander(BranchContext context, Options expand, List<ExtendedLocale> locales, String descendantExpandKey) {
		super(context, expand, locales, descendantExpandKey);
		this.stated = SnomedConcept.Expand.STATED_DESCENDANTS.equals(descendantExpandKey);
		this.batchLimit = context.service(RepositoryConfiguration.class)
			.getIndexConfiguration()
			.getResultWindow();
	}
	
	@Override
	protected void expand(List<SnomedConcept> results, final Set<String> conceptIds, Options descendantOptions, boolean direct) {
		// Nothing to do if the ancestor ID set is empty
		if (conceptIds.isEmpty()) {
			return;
		}
		
		Stopwatch w = null;
		final Logger log = context().log();
		if (log.isDebugEnabled()) {
			w = Stopwatch.createStarted();
		}
		
		final int descendantLimit = getLimit(descendantOptions);
		final boolean totalOnly = (descendantLimit == 0);
		
		if (totalOnly) {
			expandTotalOnly(results, conceptIds, direct);
		} else {
			final Options descendantExpandOptions = descendantOptions.get("expand", Options.class);
			expandConcepts(results, conceptIds, direct, descendantLimit, descendantExpandOptions);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Spent {}ms expanding {}{}{}descendants for {} concept(s)", 
				w.elapsed(TimeUnit.MILLISECONDS),
				totalOnly ? "total number of " : "", 
				stated ? "stated " : "inferred ",
				direct ? "direct " : "direct and indirect ",		
				conceptIds.size());
		}
	}

	private void expandTotalOnly(final List<SnomedConcept> results, final Set<String> conceptIds, final boolean direct) {
		final List<String> fieldsToLoad;
		
		if (stated) {
			fieldsToLoad = ImmutableList.of(
				SnomedConceptDocument.Fields.ID,
				SnomedConceptDocument.Fields.STATED_PARENTS,
				SnomedConceptDocument.Fields.STATED_ANCESTORS
			);
		} else {
			fieldsToLoad = ImmutableList.of(
				SnomedConceptDocument.Fields.ID,
				SnomedConceptDocument.Fields.PARENTS,
				SnomedConceptDocument.Fields.ANCESTORS
			);
		}		

		final boolean singleConcept = (conceptIds.size() == 1);
		
		final int limit;
		if (singleConcept) {
			// The "total" count we get from the first query result is the number of descendants on this concept
			limit = 0;
		} else {
			// We need to load all concept documents and distribute the "total" count amongst all ancestors
			limit = batchLimit;
		}
		
		final RevisionSearcher searcher = context().service(RevisionSearcher.class);
		final Query<SnomedConceptDocument> query = Query.select(SnomedConceptDocument.class)
			.fields(fieldsToLoad)
			.where(Expressions.bool()
				.filter(active())
				.filter(createDescendantFilter(conceptIds, direct))
				.build())
			.limit(limit)
			.build();

		
		if (singleConcept) {

			// The "total" number returned from Hits is equal to the number of descendants on the single concept
			try {
				
				final Hits<SnomedConceptDocument> descendantHits = searcher.search(query);
				final SnomedConcepts singleConceptDescendants = new SnomedConcepts(0, descendantHits.getTotal());
				
				for (final SnomedConcept ancestor : results) {
					setDescendants(ancestor, singleConceptDescendants);
				}
				
				return;
				
			} catch (IOException e) {
				throw SnowowlRuntimeException.wrap(e);
			}
		
		}
		
		// The Multiset keeps track of the number of descendants seen so far (by parent/ancestor concept ID)
		LongKeyIntMap descendantCounts = PrimitiveMaps.newLongKeyIntOpenHashMapWithExpectedSize(conceptIds.size());
		
		final Stream<Hits<SnomedConceptDocument>> stream = searcher.stream(query);
		final Iterator<Hits<SnomedConceptDocument>> itr = stream.iterator();
		
		while (itr.hasNext()) {
			final Hits<SnomedConceptDocument> batch = itr.next();
			
			if (batch.getTotal() < 1) {
				// No descendants found whatsoever
				final SnomedConcepts emptyDescendants = new SnomedConcepts(0, 0);
				for (SnomedConcept ancestor : results) {
					setDescendants(ancestor, emptyDescendants);
				}
				
				return;
			}

			for (final SnomedConceptDocument descendant : batch) {
				// Add +1 to the descendant counters for all parent/ancestors that we have seen in this batch
				registerRelevantAncestors(descendantCounts, descendant, direct);
			}
		}
		
		// Populate total descendant counts based on the information collected
		for (final SnomedConcept ancestor : results) {
			final int total = descendantCounts.get(Long.parseLong(ancestor.getId()));
			final SnomedConcepts descendants = new SnomedConcepts(0, total);
			setDescendants(ancestor, descendants);
		}
	}

	private Expression createDescendantFilter(final Set<String> conceptIds, boolean direct) {
		final ExpressionBuilder descendantFilter = Expressions.bool();
		
		// Add direct parents filter
		if (stated) {
			descendantFilter.should(statedParents(conceptIds));
		} else {
			descendantFilter.should(parents(conceptIds));
		}
		
		if (direct) {
			return descendantFilter.build();
		}
	
		// Add indirect ancestors filter as well if not in direct mode
		if (stated) {
			descendantFilter.should(statedAncestors(conceptIds));
		} else {
			descendantFilter.should(ancestors(conceptIds));				
		}
		
		return descendantFilter.build();
	}

	private void registerRelevantAncestors(
		final LongKeyIntMap descendantCounts, 
		final SnomedConceptDocument descendant, 
		final boolean direct
	) {
		// Always collect direct parent IDs
		if (stated) {
			registerRelevantAncestors(descendantCounts, descendant.getStatedParents());
		} else {
			registerRelevantAncestors(descendantCounts, descendant.getParents());
		}
	
		if (direct) {
			return;
		}
		
		// Collect indirect ancestor IDs as well if not in direct mode
		if (stated) {
			registerRelevantAncestors(descendantCounts, descendant.getStatedAncestors());
		} else {
			registerRelevantAncestors(descendantCounts, descendant.getAncestors());
		}
	}

	private void registerRelevantAncestors(
		final LongKeyIntMap descendantCounts, 
		final LongSortedSet ancestorIdsOfDescendant
	) {
		for (var itr = ancestorIdsOfDescendant.iterator(); itr.hasNext(); /* empty */) {
			final long id = itr.next();
			descendantCounts.put(id, descendantCounts.get(id) + 1);
		}
	}

	private void expandConcepts(
		final List<SnomedConcept> results, 
		final Set<String> conceptIds, 
		final boolean direct, 
		final int limit, 
		final Options descendantExpandOptions
	) {
		final SnomedConceptSearchRequestBuilder descendantSearchRequestBuilder = SnomedRequests.prepareSearchConcept();
		
		/*
		 * Contrary to the low-level queries in "createDescendantFilter" below, we only
		 * need a single filter for the search request ("ancestors" cover both direct
		 * and indirect parentage).
		 */
		if (direct) {
			if (stated) {
				descendantSearchRequestBuilder.filterByStatedParents(conceptIds);
			} else {
				descendantSearchRequestBuilder.filterByParents(conceptIds);
			}
		} else {
			if (stated) {
				descendantSearchRequestBuilder.filterByStatedAncestors(conceptIds);
			} else {
				descendantSearchRequestBuilder.filterByAncestors(conceptIds);
			}
		}
		
		final ListMultimap<String, SnomedConcept> descendantsByAncestorId = ArrayListMultimap.create();
		
		descendantSearchRequestBuilder.setLimit(batchLimit)
			.setLocales(locales())
			.setExpand(descendantExpandOptions)
			.sortBy(SnomedConceptDocument.Fields.ID)
			.stream(context())
			.flatMap(SnomedConcepts::stream)
			.forEachOrdered(descendant -> registerRelevantAncestors(descendantsByAncestorId, descendant, conceptIds, direct));

		for (SnomedConcept ancestor : results) {
			final List<SnomedConcept> descendants = descendantsByAncestorId.get(ancestor.getId());
			final int total = descendants.size();
			
			/*
			 * We can only enforce the requested limit at this point, as a per-ancestor
			 * total can not be collected from the search request alone, it can only be
			 * computed after we get to know which concept belongs to which ancestor.
			 */
			final List<SnomedConcept> limitedDescendants = ImmutableList.copyOf(descendants.subList(0, Ints.min(limit, descendants.size())));
			final SnomedConcepts descendantConcepts = new SnomedConcepts(limitedDescendants, null, limit, total);
			setDescendants(ancestor, descendantConcepts);
		}
	}

	private void registerRelevantAncestors(
		final Multimap<String, SnomedConcept> descendantsByAncestorId, 
		final SnomedConcept descendant, 
		final Set<String> conceptIds, 
		final boolean direct
	) {
		final Set<String> parentIds;
		
		// Always collect direct parent IDs
		if (stated) {
			parentIds = ImmutableSet.copyOf(descendant.getStatedParentIdsAsString());
		} else {
			parentIds = ImmutableSet.copyOf(descendant.getParentIdsAsString());
		}
		
		for (final String relevantParentId : Sets.intersection(parentIds, conceptIds)) {
			descendantsByAncestorId.put(relevantParentId, descendant);
		}

		if (direct) {
			return;
		}
		
		final Set<String> ancestorIds;
		
		// Collect indirect ancestor IDs as well if not in direct mode
		if (stated) {
			ancestorIds = ImmutableSet.copyOf(descendant.getStatedAncestorIdsAsString());
		} else {
			ancestorIds = ImmutableSet.copyOf(descendant.getAncestorIdsAsString());
		}

		for (final String relevantAncestorId : Sets.intersection(ancestorIds, conceptIds)) {
			descendantsByAncestorId.put(relevantAncestorId, descendant);
		}
	}

	private void setDescendants(final SnomedConcept ancestor, final SnomedConcepts descendants) {
		if (stated) {
			ancestor.setStatedDescendants(descendants);
		} else {
			ancestor.setDescendants(descendants);
		}
	}
}
