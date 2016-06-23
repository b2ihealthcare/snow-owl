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

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.acceptableIn;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.concepts;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.languageCodes;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.preferredIn;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.types;

import java.io.IOException;
import java.util.List;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.IllegalQueryParameterException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.escg.ConceptIdQueryEvaluator2;
import com.b2international.snowowl.snomed.datastore.escg.EscgRewriter;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.dsl.query.RValue;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.google.common.base.Function;
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
			
			final ImmutableMultimap.Builder<String, ISnomedDescription> buckets = ImmutableMultimap.builder();
			
			int position = 0;
			int total = 0;
			
			for (final String languageRefSetId : languageRefSetIds()) {
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
			return search(context, searcher, "-1", offset(), limit());
		}
	}

	private SnomedDescriptions search(BranchContext context, final RevisionSearcher searcher, String languageRefSetId, int offset, int limit) throws IOException {
		
		final ExpressionBuilder queryBuilder = Expressions.builder();
		// Add (presumably) most selective filters first
		addActiveClause(queryBuilder);
		addEffectiveTimeClause(queryBuilder);
		addModuleClause(queryBuilder);
		addConceptIdsFilter(queryBuilder);
		addComponentIdFilter(queryBuilder);
		addLocaleFilter(context, queryBuilder, languageRefSetId);
		addLanguageFilter(queryBuilder);
		addEscgFilter(context, queryBuilder, OptionKey.CONCEPT_ESCG, new Function<LongSet, Expression>() {
			@Override
			public Expression apply(LongSet input) {
				return concepts(LongSets.toStringSet(input));
			}
		});
		addEscgFilter(context, queryBuilder, OptionKey.TYPE, new Function<LongSet, Expression>() {
			@Override
			public Expression apply(LongSet input) {
				return types(LongSets.toStringSet(input));
			}
		});
		
		if (containsKey(OptionKey.TERM)) {
//			if (!containsKey(OptionKey.USE_FUZZY)) {
//				addDescriptionTermQuery(queryBuilder);
//			} else {
//				addFuzzyQuery(queryBuilder);
//			}
//			
//			sort = Sort.RELEVANCE;
		}

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

	private void addConceptIdsFilter(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.CONCEPT_ID)) {
			queryBuilder.must(concepts(getCollection(OptionKey.CONCEPT_ID, String.class)));
		}
	}

	private void addEscgFilter(BranchContext context, final ExpressionBuilder queryBuilder, OptionKey key, Function<LongSet, Expression> expressionProvider) {
		if (containsKey(key)) {
			try {
				final String escg = getString(key);
				final RValue expression = context.service(EscgRewriter.class).parseRewrite(escg);
				final LongSet conceptIds = new ConceptIdQueryEvaluator2(context.service(RevisionSearcher.class)).evaluate(expression);
				final Expression conceptFilter = expressionProvider.apply(conceptIds);
				queryBuilder.must(conceptFilter);
			} catch (SyntaxErrorException e) {
				throw new IllegalQueryParameterException(e.getMessage());
			}
		}
	}
	
	private void addLanguageFilter(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.LANGUAGE)) {
			queryBuilder.must(languageCodes(getCollection(OptionKey.LANGUAGE, String.class)));
		}
	}

	private void addLocaleFilter(BranchContext context, ExpressionBuilder queryBuilder, String positiveRefSetId) {
		for (String languageRefSetId : languageRefSetIds()) {
			final Expression acceptabilityFilter; 
			if (containsKey(OptionKey.ACCEPTABILITY)) {
				acceptabilityFilter = Acceptability.PREFERRED.equals(get(OptionKey.ACCEPTABILITY, Acceptability.class)) ?
						preferredIn(languageRefSetId) :
							SnomedDescriptionIndexEntry.Expressions.acceptableIn(languageRefSetId);
			} else {
				acceptabilityFilter = Expressions.builder()
						.should(preferredIn(languageRefSetId))
						.should(acceptableIn(languageRefSetId))
						.build();
			}

			if (languageRefSetId.equals(positiveRefSetId)) {
				queryBuilder.must(acceptabilityFilter);
				break;
			} else {
				queryBuilder.mustNot(acceptabilityFilter);
			}
		}
	}

	@Override
	protected Class<SnomedDescriptions> getReturnType() {
		return SnomedDescriptions.class;
	}
	
}
