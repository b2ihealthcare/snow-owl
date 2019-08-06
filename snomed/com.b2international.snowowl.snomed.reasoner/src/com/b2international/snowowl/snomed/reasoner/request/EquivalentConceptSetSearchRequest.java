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
package com.b2international.snowowl.snomed.reasoner.request;

import static com.b2international.snowowl.snomed.reasoner.index.EquivalentConceptSetDocument.Expressions.classificationId;
import static com.b2international.snowowl.snomed.reasoner.index.EquivalentConceptSetDocument.Expressions.conceptIds;

import java.util.Collection;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.snomed.reasoner.converter.EquivalentConceptSetConverter;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSets;
import com.b2international.snowowl.snomed.reasoner.index.EquivalentConceptSetDocument;

/**
 * @since 7.0
 */
class EquivalentConceptSetSearchRequest 
		extends SearchIndexResourceRequest<RepositoryContext, EquivalentConceptSets, EquivalentConceptSetDocument> {

	public enum OptionKey {
		CLASSIFICATION_ID, 
		CONCEPT_ID
	}

	@Override
	protected Expression prepareQuery(final RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		addClassificationIdClause(queryBuilder);
		addConceptIdClause(queryBuilder);
		return queryBuilder.build();
	}

	private void addClassificationIdClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.CLASSIFICATION_ID)) {
			final Collection<String> classificationIds = getCollection(OptionKey.CLASSIFICATION_ID, String.class);
			builder.filter(classificationId(classificationIds));
		}
	}

	private void addConceptIdClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.CONCEPT_ID)) {
			final Collection<String> conceptIds = getCollection(OptionKey.CONCEPT_ID, String.class);
			builder.filter(conceptIds(conceptIds));
		}
	}

	@Override
	protected Class<EquivalentConceptSetDocument> getDocumentType() {
		return EquivalentConceptSetDocument.class;
	}

	@Override
	protected EquivalentConceptSets toCollectionResource(final RepositoryContext context, final Hits<EquivalentConceptSetDocument> hits) {
		return new EquivalentConceptSetConverter(context, expand(), locales()).convert(hits.getHits(), 
				hits.getScrollId(), 
				hits.getSearchAfter(), 
				hits.getLimit(), 
				hits.getTotal());
	}

	@Override
	protected EquivalentConceptSets createEmptyResult(final int limit) {
		return new EquivalentConceptSets(limit, 0);
	}
}
