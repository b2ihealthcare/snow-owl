/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request;

import static com.b2international.index.query.Expressions.bool;
import static com.b2international.index.query.Expressions.exists;
import static com.b2international.index.query.Expressions.match;

import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.SortBy.Builder;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.ResourceTypeConverter;
import com.b2international.snowowl.core.ResourceTypeConverter.Registry;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.resource.BaseResourceSearchRequest;
import com.b2international.snowowl.core.request.resource.ResourceConverter;

/**
 * @since 8.0
 */
public final class ResourceSearchRequest extends BaseResourceSearchRequest<Resources> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @since 8.0
	 */
	enum OptionKey {

		/**
		 * Filter matches by their resource type.
		 */
		RESOURCE_TYPE,

		/**
		 * Filter matches by their tooling id property.
		 */
		TOOLING_ID,

		/**
		 * Filter matches by their currently associated working branch (exact match).
		 */
		BRANCH, 
		
		/**
		 * Filter matches by their dependency array (supports partial and exact matches as well via special syntax).
		 */
		DEPENDENCY, 
		
		/**
		 * Filters terminology resources by whether they have an upgrade in-progress.
		 */
		IN_UPGRADE,

	}

	@Override
	protected void prepareAdditionalFilters(RepositoryContext context, ExpressionBuilder queryBuilder) {
		super.prepareAdditionalFilters(context, queryBuilder);
		addFilter(queryBuilder, OptionKey.RESOURCE_TYPE, String.class, ResourceDocument.Expressions::resourceTypes);
		addFilter(queryBuilder, OptionKey.TOOLING_ID, String.class, ResourceDocument.Expressions::toolingIds);
		addFilter(queryBuilder, OptionKey.BRANCH, String.class, ResourceDocument.Expressions::branchPaths);
		addDependencyFilter(context, queryBuilder);
		addInUpgradeFilter(context, queryBuilder);
	}

	private void addDependencyFilter(RepositoryContext context, ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.DEPENDENCY)) {
			String queryString = getString(OptionKey.DEPENDENCY);
			queryBuilder.filter(ResourceDocument.Expressions.dependency(queryString));
		}
	}
	
	private void addInUpgradeFilter(RepositoryContext context, ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.IN_UPGRADE)) {
			final String fieldName = "settings.inUpgrade";
			boolean inUpgrade = getBoolean(OptionKey.IN_UPGRADE);
			if (inUpgrade) {
				queryBuilder.filter(com.b2international.index.query.Expressions.match(fieldName, inUpgrade));				
			} else {
				queryBuilder.filter(bool()
					.should(match(fieldName, inUpgrade))
					.should(bool().mustNot(exists(fieldName)).build())
					.build()
				);
			}
		}
	}
	
	@Override
	protected Resources toCollectionResource(RepositoryContext context, Hits<ResourceDocument> hits) {
		return new ResourceConverter(context, expand(), locales()).convert(hits);
	}

	@Override
	protected Resources createEmptyResult(int limit) {
		return new Resources(limit, 0);
	}
	
	@Override
	protected void toQuerySortBy(RepositoryContext context, Builder sortBuilder, Sort sort) {
		if (sort instanceof SortField) {
			SortField sortField = (SortField) sort;
			if (ResourceDocument.Fields.TYPE_RANK.equals(sortField.getField())) {
				Registry registry = context.service(ResourceTypeConverter.Registry.class);
				Map<String, Integer> orderMap = registry.getResourceTypeConverters().values().stream().collect(Collectors.toMap(typeDef -> typeDef.getResourceType(), typeDef -> typeDef.getRank()));
				
				sortBuilder.sortByScriptNumeric(ResourceDocument.Fields.TYPE_RANK, Map.of("ranks", orderMap) , sort.isAscending() ? Order.ASC : Order.DESC);
				return;
			}
		}
		super.toQuerySortBy(context, sortBuilder, sort);
	}
	
}
