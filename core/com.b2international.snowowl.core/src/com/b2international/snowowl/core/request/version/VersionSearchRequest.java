/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.version;

import java.util.Collections;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.core.version.Versions;

/**
 * @since 4.7
 */
public final class VersionSearchRequest extends SearchIndexResourceRequest<RepositoryContext, Versions, VersionDocument> {

	private static final long serialVersionUID = 3L;

	/**
	 * @since 6.15
	 */
	public static enum OptionKey {
		/**
		 * Filter versions by their tag.
		 */
		VERSION,

		/**
		 * Filter versions by associated resource.
		 */
		RESOURCE,

		/**
		 * Filter versions by effective date starting with this value, inclusive.
		 */
		EFFECTIVE_TIME_START,

		/**
		 * Filter versions by effective date ending with this value, inclusive.
		 */
		EFFECTIVE_TIME_END,

		/**
		 * Filter versions by associated resource's type.
		 */
		RESOURCE_TYPE,

		/**
		 * Filter matches by corresponding resource branch path (formerly parent branch path).
		 */
		RESOURCE_BRANCHPATH,

		/**
		 * Filter by the author's username who have created the version.
		 */
		AUTHOR,
		
		/**
		 * "Greater than equal to filter
		 */
		CREATED_AT_FROM,
		
		/**
		 * "Less than equal to filter
		 */
		CREATED_AT_TO,
		
		/**
		 * The tooling identifier of the associated resource.
		 */
		TOOLING_ID,
	}

	VersionSearchRequest() {}

	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder query = Expressions.bool();

		addIdFilter(query, VersionDocument.Expressions::ids);
		addFilter(query, OptionKey.RESOURCE_TYPE, String.class, VersionDocument.Expressions::resourceTypes);
		// TODO add a security filter to return commits from resources that can be accessed by the current user
		addFilter(query, OptionKey.RESOURCE, String.class, resources -> Expressions.bool()
				.should(VersionDocument.Expressions.resources(resources))
				.should(VersionDocument.Expressions.resourceIds(resources))
				.build());
		addFilter(query, OptionKey.VERSION, String.class, VersionDocument.Expressions::versions);
		addFilter(query, OptionKey.RESOURCE_BRANCHPATH, String.class, VersionDocument.Expressions::resourceBranchPaths);
		addFilter(query, OptionKey.AUTHOR, String.class, VersionDocument.Expressions::authors);

		if (containsKey(OptionKey.CREATED_AT_FROM) || containsKey(OptionKey.CREATED_AT_TO)) {
			final Long createdAtFrom = containsKey(OptionKey.CREATED_AT_FROM) ? get(OptionKey.CREATED_AT_FROM, Long.class) : 0L;
			final Long createdAtTo = containsKey(OptionKey.CREATED_AT_TO) ? get(OptionKey.CREATED_AT_TO, Long.class) : Long.MAX_VALUE;
			query.filter(VersionDocument.Expressions.createdAt(createdAtFrom, createdAtTo));
		}

		if (containsKey(OptionKey.EFFECTIVE_TIME_START) || containsKey(OptionKey.EFFECTIVE_TIME_END)) {
			final long from = containsKey(OptionKey.EFFECTIVE_TIME_START) ? get(OptionKey.EFFECTIVE_TIME_START, Long.class) : 0;
			final long to = containsKey(OptionKey.EFFECTIVE_TIME_END) ? get(OptionKey.EFFECTIVE_TIME_END, Long.class) : Long.MAX_VALUE;
			query.filter(VersionDocument.Expressions.effectiveTime(from, to));
		}
		
		addFilter(query, OptionKey.TOOLING_ID, String.class, VersionDocument.Expressions::toolingIds);

		return query.build();
	}

	@Override
	protected Class<VersionDocument> getDocumentType() {
		return VersionDocument.class;
	}

	@Override
	protected Versions toCollectionResource(RepositoryContext context, Hits<VersionDocument> hits) {
		if (limit() < 1 || hits.getTotal() < 1) {
			return new Versions(Collections.emptyList(), null, limit(), hits.getTotal());
		} else {
			return new VersionConverter(context, expand(), locales())
					.convert(hits.getHits(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
		}
	}

	@Override
	protected Versions createEmptyResult(int limit) {
		return new Versions(Collections.emptyList(), null, limit, 0);
	}

}
