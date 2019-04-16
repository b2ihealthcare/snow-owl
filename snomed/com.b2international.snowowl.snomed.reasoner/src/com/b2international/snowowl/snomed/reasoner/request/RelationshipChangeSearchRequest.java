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

import static com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument.Expressions.classificationId;
import static com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument.Expressions.destinationId;
import static com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument.Expressions.sourceId;

import java.util.Collection;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.snomed.reasoner.converter.RelationshipChangeConverter;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChanges;
import com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument;

/**
 * @since 7.0
 */
class RelationshipChangeSearchRequest 
		extends SearchIndexResourceRequest<RepositoryContext, RelationshipChanges, RelationshipChangeDocument> {

	public enum OptionKey {
		CLASSIFICATION_ID, 
		SOURCE_ID, 
		DESTINATION_ID
	}

	@Override
	protected Expression prepareQuery(final RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		addClassificationIdClause(queryBuilder);
		addSourceIdClause(queryBuilder);
		addDestinationIdClause(queryBuilder);
		return queryBuilder.build();
	}

	private void addClassificationIdClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.CLASSIFICATION_ID)) {
			final Collection<String> classificationIds = getCollection(OptionKey.CLASSIFICATION_ID, String.class);
			builder.filter(classificationId(classificationIds));
		}
	}

	private void addSourceIdClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.SOURCE_ID)) {
			final Collection<String> sourceIds = getCollection(OptionKey.SOURCE_ID, String.class);
			builder.filter(sourceId(sourceIds));
		}
	}
	
	private void addDestinationIdClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.DESTINATION_ID)) {
			final Collection<String> destinationIds = getCollection(OptionKey.DESTINATION_ID, String.class);
			builder.filter(destinationId(destinationIds));
		}
	}

	@Override
	protected Class<RelationshipChangeDocument> getDocumentType() {
		return RelationshipChangeDocument.class;
	}

	@Override
	protected RelationshipChanges toCollectionResource(final RepositoryContext context, final Hits<RelationshipChangeDocument> hits) {
		return new RelationshipChangeConverter(context, expand(), locales()).convert(hits.getHits(), 
				hits.getScrollId(), 
				hits.getSearchAfter(), 
				hits.getLimit(), 
				hits.getTotal());
	}

	@Override
	protected RelationshipChanges createEmptyResult(final int limit) {
		return new RelationshipChanges(limit, 0);
	}
}
