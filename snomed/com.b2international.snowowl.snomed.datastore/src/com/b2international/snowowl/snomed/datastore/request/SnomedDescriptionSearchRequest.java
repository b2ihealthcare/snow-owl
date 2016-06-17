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
package com.b2international.snowowl.snomed.datastore.request;

import java.io.IOException;
import java.util.List;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.google.common.collect.ImmutableMultimap;

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
		LANGUAGE,
		USE_FUZZY,
		PARSED_TERM;
	}
	
	SnomedDescriptionSearchRequest() {}

	@Override
	protected SnomedDescriptions doExecute(BranchContext context) throws IOException {
		final RevisionSearcher searcher = context.service(RevisionSearcher.class);
		if (containsKey(OptionKey.TERM) && getString(OptionKey.TERM).length() < 2) {
			throw new BadRequestException("Description term must be at least 2 characters long.");
		}
		
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

	private SnomedDescriptions search(BranchContext context, final RevisionSearcher searcher, Long languageRefSetId, int offset, int limit) throws IOException {
		
		final ExpressionBuilder queryBuilder = Expressions.builder();
		addActiveClause(queryBuilder);
		addModuleClause(queryBuilder);
		
		if (containsKey(OptionKey.TERM)) {
//			if (!containsKey(OptionKey.USE_FUZZY)) {
//				addDescriptionTermQuery(queryBuilder);
//			} else {
//				addFuzzyQuery(queryBuilder);
//			}
//			
//			sort = Sort.RELEVANCE;
		}

//		final List<Filter> filters = newArrayList();
//		final List<Integer> ops = newArrayList();
		
		// Add (presumably) most selective filters first
//		addComponentIdFilter(filters, ops);
//		addConceptIdsFilter(filters, ops);
//		addLanguageFilter(filters, ops);
//		addEscgFilter(context, filters, ops, OptionKey.CONCEPT_ESCG, (LongIndexField) SnomedMappings.descriptionConcept());
//		addEscgFilter(context, filters, ops, OptionKey.TYPE, (LongIndexField) SnomedMappings.descriptionType());
//		addLocaleFilter(context, filters, ops, languageRefSetId); 
		
//		final Query query = createFilteredQuery(queryBuilder.matchAll(), filters, ops);
		
		// TODO: control score tracking
		final Hits<SnomedDescriptionIndexEntry> hits = searcher.search(Query.builder(SnomedDescriptionIndexEntry.class)
				.selectAll()
				.where(queryBuilder.build())
				.offset(offset)
				.limit(limit)
				.build());
		if (limit < 1 || hits.getTotal() < 1) {
			return new SnomedDescriptions(offset, limit, hits.getTotal());
		} else {
			return SnomedConverters.newDescriptionConverter(context, expand(), locales()).convert(hits.getHits(), offset, limit, hits.getTotal());
		}
	}
	
	private void addDescriptionTermQuery(final ExpressionBuilder queryBuilder) {
		final String searchTerm = getString(OptionKey.TERM);
		
		final ExpressionBuilder qb = Expressions.builder();
		qb.should(createTermDisjunctionQuery(searchTerm));
		
		if (containsKey(OptionKey.PARSED_TERM)) {
			qb.should(createParsedTermQuery(searchTerm));
		}
		
		if (isComponentId(searchTerm, ComponentCategory.DESCRIPTION)) {
			qb.should(Expressions.boost(SnomedDescriptionIndexEntry.Expressions.id(searchTerm), 1000f));
		}
		
		queryBuilder.must(qb.build());
	}
	
	private boolean isComponentId(String value, ComponentCategory type) {
		try {
			return SnomedIdentifiers.getComponentCategory(value) == type;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private Expression createTermDisjunctionQuery(final String searchTerm) {
		throw new UnsupportedOperationException("Support dismax queries");
//		final DisjunctionMaxQuery termDisjunctionQuery = new DisjunctionMaxQuery(0.0f);
//		
//		final ComponentTermAnalyzer nonBookendAnalyzer = new ComponentTermAnalyzer(false, false);
//		final ComponentTermAnalyzer bookendAnalyzer = new ComponentTermAnalyzer(true, true);
//		
//		final QueryBuilder bookendTermQueryBuilder = new QueryBuilder(bookendAnalyzer);
//		final QueryBuilder nonBookendTermQueryBuilder = new QueryBuilder(nonBookendAnalyzer);
//		termDisjunctionQuery.add(createExactMatchQuery(searchTerm, bookendTermQueryBuilder));
//		termDisjunctionQuery.add(createAllTermsPresentQuery(searchTerm, nonBookendTermQueryBuilder));
//		
//		final List<String> prefixes = IndexUtils.split(nonBookendAnalyzer, searchTerm);
//		termDisjunctionQuery.add(createAllTermPrefixesPresentQuery(prefixes));
//		
//		return termDisjunctionQuery;
	}

	private Expression createParsedTermQuery(final String searchTerm) {
		throw new UnsupportedOperationException("TODO support parsed term queries");
//		final ComponentTermAnalyzer analyzer = new ComponentTermAnalyzer(true, true);
//		final QueryParser parser = new QueryParser(Version.LUCENE_4_9, SnomedMappings.descriptionTerm().fieldName(), analyzer);
//		parser.setDefaultOperator(Operator.AND);
//		parser.setAllowLeadingWildcard(true);
//		
//		try {
//			return parser.parse(escape(searchTerm));
//		} catch (ParseException e) {
//			throw new IllegalQueryParameterException(e.getMessage());
//		}		
	}
	
	// copied from IndexQueryBuilder
	private String escape(final String searchTerm) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < searchTerm.length(); i++) {
			final char c = searchTerm.charAt(i);
			outerLoop: {
				switch (c) {
				case '\\':
					if (i != searchTerm.length() - 1) {
						final char charAt = searchTerm.charAt(i + 1);
						switch (charAt) {
						case '^':
						case '?':
						case '~':
						case '*':
							break outerLoop;
						}
					}
				case '+':
				case '-':
				case '!':
				case '(':
				case ')':
				case ':':
				case '[':
				case ']':
				case '\"':
				case '{':
				case '}':
				case '|':
				case '&':
				case '/':
					sb.append('\\');
					break;
				}
			}

			sb.append(c);

			switch (c) {
			case '~':
				sb.append(' ');
				break;
			}
		}

		return sb.toString();
	}

	private void addFuzzyQuery(final ExpressionBuilder queryBuilder) {
		throw new UnsupportedOperationException("TODO support fuzzy queries");
//		final Splitter tokenSplitter = Splitter.on(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER).omitEmptyStrings();
//		final ExpressionBuilder fuzzyQuery = Expressions.builder();
//		int tokenCount = 0;
//
//		final String term = getString(OptionKey.TERM).toLowerCase();
//		for (final String token : tokenSplitter.split(term)) {
//			fuzzyQuery.should(SnomedDescriptionIndexEntry.Expressions.fuzzyTerm(token));
//			++tokenCount;
//		}
//
//		final int minShouldMatch = Math.max(1, tokenCount - 2);
//		fuzzyQuery.setMinimumNumberShouldMatch(minShouldMatch);
//
//		queryBuilder.must(fuzzyQuery.build());
	}

//	private Expression createExactMatchQuery(final String searchTerm, final ExpressionBuilder termQueryBuilder) {
//		return termQueryBuilder.createPhraseQuery(SnomedMappings.descriptionTerm().fieldName(), searchTerm);
//	}

//	private Query createAllTermsPresentQuery(final String searchTerm, final ExpressionBuilder termQueryBuilder) {
//		return termQueryBuilder.createBooleanQuery(SnomedMappings.descriptionTerm().fieldName(), searchTerm, Occur.MUST);
//	}

	private Expression createAllTermPrefixesPresentQuery(List<String> prefixes) {
		final ExpressionBuilder query = Expressions.builder();

		for (String prefix : prefixes) {
			query.must(SnomedDescriptionIndexEntry.Expressions.termPrefix(prefix));
		}

		return query.build();
	}

//	private void addComponentIdFilter(final List<Filter> filters, final List<Integer> ops) {
//		if (!componentIds().isEmpty()) {
//			addFilterClause(filters, createComponentIdFilter());
//			ops.add(ChainedFilter.AND);
//		}
//	}

//	private void addConceptIdsFilter(List<Filter> filters, List<Integer> ops) {
//		if (containsKey(OptionKey.CONCEPT_ID)) {
//			addFilterClause(filters, SnomedMappings.descriptionConcept().createTermsFilter(getCollection(OptionKey.CONCEPT_ID, Long.class)));
//			ops.add(ChainedFilter.AND);
//		}
//	}

//	private void addEscgFilter(BranchContext context, final List<Filter> filters, final List<Integer> ops, OptionKey key, Function<LongSet, Expression> expressionProvider) {
//		if (containsKey(key)) {
//			try {
//				final String escg = getString(key);
//				final RValue expression = context.service(EscgRewriter.class).parseRewrite(escg);
//				final LongSet conceptIds = new ConceptIdQueryEvaluator2(context.service(RevisionSearcher.class)).evaluate(expression);
//				final Expression conceptFilter = expressionProvider.apply(conceptIds);
//				addFilterClause(filters, conceptFilter);
//				ops.add(ChainedFilter.AND);
//			} catch (SyntaxErrorException e) {
//				throw new IllegalQueryParameterException(e.getMessage());
//			}
//		}
//	}
	
//	private void addLanguageFilter(List<Filter> filters, List<Integer> ops) {
//		if (containsKey(OptionKey.LANGUAGE)) {
//			addFilterClause(filters, SnomedMappings.descriptionLanguageCode().createTermsFilter(getCollection(OptionKey.LANGUAGE, String.class)));
//			ops.add(ChainedFilter.AND);
//		}
//	}

//	private void addLocaleFilter(BranchContext context, List<Filter> filters, List<Integer> ops, Long positiveRefSetId) {
//		for (Long languageRefSetId : languageRefSetIds()) {
//			if (containsKey(OptionKey.ACCEPTABILITY)) {
//				final Filter filter = Acceptability.PREFERRED.equals(get(OptionKey.ACCEPTABILITY, Acceptability.class)) ?
//						SnomedMappings.descriptionPreferredReferenceSetId().toTermFilter(languageRefSetId) :
//						SnomedMappings.descriptionAcceptableReferenceSetId().toTermFilter(languageRefSetId);
//				
//				addFilterClause(filters, filter);
//			} else {
//				final BooleanFilter booleanFilter = new BooleanFilter();
//				addFilterClause(booleanFilter, SnomedMappings.descriptionPreferredReferenceSetId().toTermFilter(languageRefSetId), Occur.SHOULD);
//				addFilterClause(booleanFilter, SnomedMappings.descriptionAcceptableReferenceSetId().toTermFilter(languageRefSetId), Occur.SHOULD);					
//				
//				addFilterClause(filters, booleanFilter);
//			}
//
//			if (languageRefSetId.equals(positiveRefSetId)) {
//				ops.add(ChainedFilter.AND);
//				break;
//			} else {
//				ops.add(ChainedFilter.ANDNOT);
//			}
//		}
//	}

	@Override
	protected Class<SnomedDescriptions> getReturnType() {
		return SnomedDescriptions.class;
	}
	
}
