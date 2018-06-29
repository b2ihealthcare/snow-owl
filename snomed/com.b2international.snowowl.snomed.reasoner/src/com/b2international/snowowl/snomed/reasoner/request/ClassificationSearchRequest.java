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

import static com.b2international.snowowl.snomed.reasoner.index.ClassificationTaskDocument.Expressions.branches;
import static com.b2international.snowowl.snomed.reasoner.index.ClassificationTaskDocument.Expressions.created;
import static com.b2international.snowowl.snomed.reasoner.index.ClassificationTaskDocument.Expressions.statuses;
import static com.b2international.snowowl.snomed.reasoner.index.ClassificationTaskDocument.Expressions.userId;

import java.util.Collection;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.snomed.reasoner.converter.ClassificationTaskConverter;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTasks;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationTaskDocument;

/**
 * @since 7.0
 */
final class ClassificationSearchRequest 
		extends SearchIndexResourceRequest<RepositoryContext, ClassificationTasks, ClassificationTaskDocument> {

	public enum OptionKey {
		BRANCH, 
		USER_ID,
		STATUS,
		CREATED_AFTER,
		CREATED_BEFORE  
	}

	@Override
	protected Expression prepareQuery(final RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		addIdFilter(queryBuilder, ClassificationTaskDocument.Expressions::ids);
		addBranchClause(queryBuilder);
		addUserIdClause(queryBuilder);
		addStatusClause(queryBuilder);
		addCreatedClause(queryBuilder);
		return queryBuilder.build();
	}

	private void addBranchClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.BRANCH)) {
			final Collection<String> branches = getCollection(OptionKey.BRANCH, String.class);
			builder.filter(branches(branches));
		}
	}

	private void addUserIdClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.USER_ID)) {
			final String userId = getString(OptionKey.USER_ID);
			builder.filter(userId(userId));
		}
	}

	private void addStatusClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.STATUS)) {
			final Collection<ClassificationStatus> statuses = getCollection(OptionKey.STATUS, ClassificationStatus.class);
			builder.filter(statuses(statuses));
		}
	}

	private void addCreatedClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.CREATED_AFTER) || containsKey(OptionKey.CREATED_BEFORE)) {
			final Long afterInclusive = containsKey(OptionKey.CREATED_AFTER) ? get(OptionKey.CREATED_AFTER, Long.class) : 0L;
			final Long beforeExclusive = containsKey(OptionKey.CREATED_BEFORE) ? get(OptionKey.CREATED_BEFORE, Long.class) : Long.MAX_VALUE;
			builder.filter(created(afterInclusive, beforeExclusive));
		}
	}

	@Override
	protected Class<ClassificationTaskDocument> getDocumentType() {
		return ClassificationTaskDocument.class;
	}

	@Override
	protected ClassificationTasks toCollectionResource(final RepositoryContext context, final Hits<ClassificationTaskDocument> hits) {
		return new ClassificationTaskConverter(context, expand(), locales()).convert(hits.getHits(), 
				hits.getScrollId(), 
				hits.getSearchAfter(), 
				hits.getLimit(), 
				hits.getTotal());
	}

	@Override
	protected ClassificationTasks createEmptyResult(final int limit) {
		return new ClassificationTasks(limit, 0);
	}
}
