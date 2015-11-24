/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.server.request;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.queries.ChainedFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.QueryBuilder;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.lucene.BookendTokenFilter;
import com.b2international.snowowl.datastore.index.lucene.ComponentTermAnalyzer;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.datastore.server.converter.SnomedConverters;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

import bak.pcj.LongCollection;
import bak.pcj.adapter.LongCollectionToCollectionAdapter;

/**
 * @since 4.5
 */
final class SnomedDescriptionSearchRequest extends SnomedSearchRequest<SnomedDescriptions> {

	enum OptionKey {
		TERM,
		CONCEPT_ESCG,
		CONCEPT_ID,
		TYPE,
		ACCEPTABILITY,
		LANGUAGE;
	}
	
	SnomedDescriptionSearchRequest() {}

	@Override
	protected SnomedDescriptions doExecute(BranchContext context) throws IOException {
		final IndexSearcher searcher = context.service(IndexSearcher.class);
		
		if (containsKey(OptionKey.ACCEPTABILITY) || !languageRefSetIds().isEmpty()) {
			
			if (containsKey(OptionKey.ACCEPTABILITY) && languageRefSetIds().isEmpty()) {
				throw new BadRequestException("A list of language reference set identifiers must be specified if acceptability is set.");
			}
			
			final ImmutableMultimap.Builder<Long, ISnomedDescription> buckets = ImmutableMultimap.builder();
			
			int position = 0;
			int total = 0;
			
			for (final Long languageRefSetId : languageRefSetIds()) {
				// Do a hitcount-only run for this language reference set first
				SnomedDescriptions subResults = search(context, searcher, languageRefSetId, 0, 0);
				int subTotal = subResults.getTotal();

				// Run actual search only if it is within the required range
				if (position + subTotal > offset() && position < offset() + limit()) {
					// Relative offset may not become negative
					int subOffset = Math.max(0, offset() - position);
					// Relative limit may not go over subTotal, or the number of remaining items to collect
					int subLimit = Math.min(subTotal - subOffset, offset() + limit() - position);
					
					subResults = search(context, searcher, languageRefSetId, subOffset, subLimit);
					buckets.putAll(languageRefSetId, subResults.getItems());
				}
				
				total += subTotal;
				position += subTotal;
			}
			
			List<ISnomedDescription> concatenatedList = buckets.build().values().asList();
			return new SnomedDescriptions(concatenatedList, offset(), limit(), total);
			
		} else {
			return search(context, searcher, -1L, offset(), limit());
		}
	}

	private SnomedDescriptions search(BranchContext context, final IndexSearcher searcher, Long languageRefSetId, int offset, int limit) throws IOException {
		final SnomedQueryBuilder queryBuilder = SnomedMappings.newQuery().description();
		
		if (containsKey(SnomedSearchRequest.OptionKey.ACTIVE)) {
			queryBuilder.active(getBoolean(SnomedSearchRequest.OptionKey.ACTIVE));
		}
		
		if (containsKey(SnomedSearchRequest.OptionKey.MODULE)) {
			queryBuilder.module(getString(SnomedSearchRequest.OptionKey.MODULE));
		}
		
		final Sort sort;
		
		if (containsKey(OptionKey.TERM)) {
			sort = Sort.RELEVANCE;
			final String searchTerm = getString(OptionKey.TERM);
			final QueryBuilder termQueryBuilder = new QueryBuilder(new ComponentTermAnalyzer(true, true));
			final DisjunctionMaxQuery termDisjunctionQuery = new DisjunctionMaxQuery(0.0f);
			
			// "absolutely exact match"
			termDisjunctionQuery.add(termQueryBuilder.createPhraseQuery(SnomedMappings.descriptionTerm().fieldName(), searchTerm));
			
			// "matchAllTokenizedTerms"
			termDisjunctionQuery.add(termQueryBuilder.createBooleanQuery(SnomedMappings.descriptionTerm().fieldName(), searchTerm, Occur.MUST));
			
			// "matchAllTokenizedTermPrefixSequences"
			final List<SpanQuery> clauses = newArrayList();
			clauses.add(new SpanTermQuery(SnomedMappings.descriptionTerm().toTerm(Character.toString(BookendTokenFilter.LEADING_MARKER))));
			
			final List<String> prefixes = IndexUtils.split(new ComponentTermAnalyzer(false, false), searchTerm);
			for (String prefix : prefixes) {
				clauses.add(new SpanMultiTermQueryWrapper<>(new PrefixQuery(SnomedMappings.descriptionTerm().toTerm(prefix))));
			}
			
			final SpanFirstQuery matchAllTokenizedPrefixQuery = new SpanFirstQuery(new SpanNearQuery(Iterables.toArray(clauses, SpanQuery.class), 0, true), prefixes.size() + 1);
			termDisjunctionQuery.add(matchAllTokenizedPrefixQuery);
			
			queryBuilder.and(termDisjunctionQuery);
		} else {
			sort = Sort.INDEXORDER;
		}

		final Query query;
		final List<Filter> filters = newArrayList();
		final List<Integer> ops = newArrayList();
		
		// Add (presumably) most selective filters first
		addComponentIdFilter(filters, ops);
		addConceptIdsFilter(filters, ops);
		addLanguageFilter(filters, ops);
		addEscgFilter(context, filters, ops, OptionKey.CONCEPT_ESCG, SnomedMappings.descriptionConcept());
		addEscgFilter(context, filters, ops, OptionKey.TYPE, SnomedMappings.descriptionType());
		addLocaleFilter(context, filters, ops, languageRefSetId); 
		
		if (!filters.isEmpty()) {
			final ChainedFilter filter = new ChainedFilter(Iterables.toArray(filters, Filter.class), Ints.toArray(ops));
			query = new FilteredQuery(queryBuilder.matchAll(), filter);
		} else {
			query = queryBuilder.matchAll();
		}
		
		if (limit == 0) {
			final TotalHitCountCollector totalCollector = new TotalHitCountCollector();
			searcher.search(new ConstantScoreQuery(query), totalCollector); 
			return new SnomedDescriptions(offset, limit, totalCollector.getTotalHits());
		}
		
		// TODO: track score only if it should be expanded
		final TopDocs topDocs = searcher.search(query, null, offset + limit, sort, true, false);
		if (IndexUtils.isEmpty(topDocs)) {
			return new SnomedDescriptions(offset, limit, topDocs.totalHits);
		}
		
		final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		final ImmutableList.Builder<SnomedDescriptionIndexEntry> descriptionBuilder = ImmutableList.builder();
		
		for (int i = offset; i < scoreDocs.length && i < offset + limit; i++) {
			Document doc = searcher.doc(scoreDocs[i].doc); // TODO: should expand & filter drive fieldsToLoad? Pass custom fieldValueLoader?
			SnomedDescriptionIndexEntry indexEntry = SnomedDescriptionIndexEntry.builder(doc).score(scoreDocs[i].score).build();
			descriptionBuilder.add(indexEntry);
		}

		return SnomedConverters.newDescriptionConverter(context, expand(), locales()).convert(descriptionBuilder.build(), offset, limit, topDocs.totalHits);
	}

	private void addComponentIdFilter(final List<Filter> filters, final List<Integer> ops) {
		if (!componentIds().isEmpty()) {
			filters.add(createComponentIdFilter());
			ops.add(ChainedFilter.AND);
		}
	}

	private void addConceptIdsFilter(List<Filter> filters, List<Integer> ops) {
		if (containsKey(OptionKey.CONCEPT_ID)) {
			filters.add(SnomedMappings.descriptionConcept().createTermsFilter(getCollection(OptionKey.CONCEPT_ID, Long.class)));
			ops.add(ChainedFilter.AND);
		}
	}

	private void addEscgFilter(BranchContext context, final List<Filter> filters, final List<Integer> ops, OptionKey key, IndexField<Long> field) {
		if (containsKey(key)) {
			IBranchPath branchPath = context.branch().branchPath();
			LongCollection conceptIds = context.service(IEscgQueryEvaluatorService.class).evaluateConceptIds(branchPath, getString(key));
			Filter conceptFilter = field.createTermsFilter(new LongCollectionToCollectionAdapter(conceptIds));
			filters.add(conceptFilter);
			ops.add(ChainedFilter.AND);
		}
	}
	
	private void addLanguageFilter(List<Filter> filters, List<Integer> ops) {
		if (containsKey(OptionKey.LANGUAGE)) {
			filters.add(SnomedMappings.descriptionLanguageCode().createTermsFilter(getCollection(OptionKey.LANGUAGE, String.class)));
			ops.add(ChainedFilter.AND);
		}
	}

	private void addLocaleFilter(BranchContext context, List<Filter> filters, List<Integer> ops, Long positiveRefSetId) {
		for (Long languageRefSetId : languageRefSetIds()) {

			if (containsKey(OptionKey.ACCEPTABILITY)) {
				final Filter acceptabilityFilter = Acceptability.PREFERRED.equals(get(OptionKey.ACCEPTABILITY, Acceptability.class)) ?
						SnomedMappings.descriptionPreferredReferenceSetId().toTermFilter(languageRefSetId) :
						SnomedMappings.descriptionAcceptableReferenceSetId().toTermFilter(languageRefSetId);
						
				filters.add(acceptabilityFilter);
			} else {
				final BooleanFilter anyAcceptabilityFilter = new BooleanFilter();
				
				anyAcceptabilityFilter.add(SnomedMappings.descriptionPreferredReferenceSetId().toTermFilter(languageRefSetId), Occur.SHOULD);
				anyAcceptabilityFilter.add(SnomedMappings.descriptionAcceptableReferenceSetId().toTermFilter(languageRefSetId), Occur.SHOULD);
				filters.add(anyAcceptabilityFilter);
			}
			
			if (languageRefSetId.equals(positiveRefSetId)) {
				ops.add(ChainedFilter.AND);
				break;
			} else {
				ops.add(ChainedFilter.ANDNOT);
			}
		}
	}

	@Override
	protected Class<SnomedDescriptions> getReturnType() {
		return SnomedDescriptions.class;
	}
}
