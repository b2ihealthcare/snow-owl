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

import static com.b2international.snowowl.snomed.reasoner.index.ConcreteDomainChangeDocument.Expressions.classificationId;
import static com.b2international.snowowl.snomed.reasoner.index.ConcreteDomainChangeDocument.Expressions.referencedComponentId;

import java.util.Collection;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.snomed.reasoner.converter.ConcreteDomainChangeConverter;
import com.b2international.snowowl.snomed.reasoner.domain.ConcreteDomainChanges;
import com.b2international.snowowl.snomed.reasoner.index.ConcreteDomainChangeDocument;

/**
 * @since 7.0
 */
class ConcreteDomainChangeSearchRequest 
		extends SearchIndexResourceRequest<RepositoryContext, ConcreteDomainChanges, ConcreteDomainChangeDocument> {

	public enum OptionKey {
		CLASSIFICATION_ID, 
		REFERENCED_COMPONENT_ID
	}

	@Override
	protected Expression prepareQuery(final RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		addClassificationIdClause(queryBuilder);
		addReferencedComponentIdClause(queryBuilder);
		return queryBuilder.build();
	}

	private void addClassificationIdClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.CLASSIFICATION_ID)) {
			final Collection<String> classificationIds = getCollection(OptionKey.CLASSIFICATION_ID, String.class);
			builder.filter(classificationId(classificationIds));
		}
	}

	private void addReferencedComponentIdClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.REFERENCED_COMPONENT_ID)) {
			final String referencedComponentId = getString(OptionKey.REFERENCED_COMPONENT_ID);
			builder.filter(referencedComponentId(referencedComponentId));
		}
	}

	@Override
	protected Class<ConcreteDomainChangeDocument> getDocumentType() {
		return ConcreteDomainChangeDocument.class;
	}

	@Override
	protected ConcreteDomainChanges toCollectionResource(final RepositoryContext context, final Hits<ConcreteDomainChangeDocument> hits) {
		return new ConcreteDomainChangeConverter(context, expand(), locales()).convert(hits.getHits(), 
				hits.getScrollId(), 
				hits.getSearchAfter(), 
				hits.getLimit(), 
				hits.getTotal());
	}

	@Override
	protected ConcreteDomainChanges createEmptyResult(final int limit) {
		return new ConcreteDomainChanges(limit, 0);
	}
}
