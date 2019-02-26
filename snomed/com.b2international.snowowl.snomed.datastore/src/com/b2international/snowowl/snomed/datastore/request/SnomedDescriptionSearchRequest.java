/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.datastore.index.RevisionDocument.Expressions.id;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.acceptableIn;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.fuzzy;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.languageCodes;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.matchTermOriginal;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.parsedTerm;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.preferredIn;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Expressions.termDisjunctionQuery;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;

/**
 * @since 4.5
 */
final class SnomedDescriptionSearchRequest extends SnomedComponentSearchRequest<SnomedDescriptions, SnomedDescriptionIndexEntry> {

	enum OptionKey {
		EXACT_TERM,
		TERM,
		CONCEPT,
		TYPE,
		LANGUAGE,
		USE_FUZZY,
		PARSED_TERM, 
		CASE_SIGNIFICANCE, 
		TERM_REGEX, 
		SEMANTIC_TAG,
		SEMANTIC_TAG_REGEX,
		LANGUAGE_REFSET,
		ACCEPTABLE_IN,
		PREFERRED_IN;
	}
	
	SnomedDescriptionSearchRequest() {}

	@Override
	protected Class<SnomedDescriptionIndexEntry> getDocumentType() {
		return SnomedDescriptionIndexEntry.class;
	}
	
	@Override
	protected Expression prepareQuery(BranchContext context) {
		if (containsKey(OptionKey.TERM) && getString(OptionKey.TERM).length() < 2) {
			throw new BadRequestException("Description term must be at least 2 characters long.");
		}

		final ExpressionBuilder queryBuilder = Expressions.builder();
		// Add (presumably) most selective filters first
		addActiveClause(queryBuilder);
		addReleasedClause(queryBuilder);
		addLanguageFilter(queryBuilder);
		addNamespaceFilter(queryBuilder);
		addActiveMemberOfClause(context, queryBuilder);
		addMemberOfClause(context, queryBuilder);
		
		addEclFilter(context, queryBuilder, OptionKey.LANGUAGE_REFSET, ids -> {
			return Expressions.builder()
					.should(preferredIn(ids))
					.should(acceptableIn(ids))
					.build();
		});
		addEclFilter(context, queryBuilder, OptionKey.ACCEPTABLE_IN, ids -> acceptableIn(ids));
		addEclFilter(context, queryBuilder, OptionKey.PREFERRED_IN, ids -> preferredIn(ids));
		
		addEffectiveTimeClause(queryBuilder);
		addIdFilter(queryBuilder, RevisionDocument.Expressions::ids);
		addEclFilter(context, queryBuilder, SnomedSearchRequest.OptionKey.MODULE, SnomedDocument.Expressions::modules);
		addEclFilter(context, queryBuilder, OptionKey.CONCEPT, SnomedDescriptionIndexEntry.Expressions::concepts);
		addEclFilter(context, queryBuilder, OptionKey.TYPE, SnomedDescriptionIndexEntry.Expressions::types);
		addEclFilter(context, queryBuilder, OptionKey.CASE_SIGNIFICANCE, SnomedDescriptionIndexEntry.Expressions::caseSignificances);
		
		if (containsKey(OptionKey.SEMANTIC_TAG)) {
			queryBuilder.filter(SnomedDescriptionIndexEntry.Expressions.semanticTags(getCollection(OptionKey.SEMANTIC_TAG, String.class)));
		}
		
		if (containsKey(OptionKey.SEMANTIC_TAG_REGEX)) {
			queryBuilder.filter(SnomedDescriptionIndexEntry.Expressions.semanticTagRegex(getString(OptionKey.SEMANTIC_TAG_REGEX)));
		}
			
		if (containsKey(OptionKey.TERM_REGEX)) {
			final String regex = getString(OptionKey.TERM_REGEX);
			queryBuilder.filter(SnomedDescriptionIndexEntry.Expressions.matchTermRegex(regex));
		}
		
		if (containsKey(OptionKey.TERM)) {
			final String searchTerm = getString(OptionKey.TERM);
			if (!containsKey(OptionKey.USE_FUZZY)) {
				queryBuilder.must(toDescriptionTermQuery(searchTerm));
			} else {
				queryBuilder.must(fuzzy(searchTerm));
			}
		}
		
		if (containsKey(OptionKey.EXACT_TERM)) {
			queryBuilder.must(matchTermOriginal(getString(OptionKey.EXACT_TERM)));
		}
		
		return queryBuilder.build();
	}

	@Override
	protected boolean trackScores() {
		return containsKey(OptionKey.TERM);
	}

	@Override
	protected SnomedDescriptions toCollectionResource(BranchContext context, Hits<SnomedDescriptionIndexEntry> hits) {
		if (limit() < 1 || hits.getTotal() < 1) {
			return new SnomedDescriptions(limit(), hits.getTotal());
		} else {
			return SnomedConverters.newDescriptionConverter(context, expand(), locales()).convert(hits.getHits(), hits.getScrollId(), hits.getSearchAfter(), limit(), hits.getTotal());
		}
	}
	
	@Override
	protected SnomedDescriptions createEmptyResult(int limit) {
		return new SnomedDescriptions(limit, 0);
	}
	
	private Expression toDescriptionTermQuery(final String searchTerm) {
		final ExpressionBuilder qb = Expressions.builder();
		
		if (containsKey(OptionKey.PARSED_TERM)) {
			qb.should(parsedTerm(searchTerm));
		} else {
			qb.should(termDisjunctionQuery(searchTerm));
		}
		
		if (isComponentId(searchTerm, ComponentCategory.DESCRIPTION)) {
			qb.should(Expressions.boost(id(searchTerm), 1000f));
		}
		
		return qb.build();
	}
	
	private boolean isComponentId(String value, ComponentCategory type) {
		try {
			return SnomedIdentifiers.getComponentCategory(value) == type;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private void addLanguageFilter(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.LANGUAGE)) {
			queryBuilder.filter(languageCodes(getCollection(OptionKey.LANGUAGE, String.class)));
		}
	}

}
