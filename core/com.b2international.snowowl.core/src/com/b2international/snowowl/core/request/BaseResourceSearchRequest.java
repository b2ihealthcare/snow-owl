/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 8.0
 */
public abstract class BaseResourceSearchRequest<R> extends SearchIndexResourceRequest<RepositoryContext, R, ResourceDocument> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @since 8.0
	 */
	public enum OptionKey {
		
		/**
		 * Filter matches by their stored URL value.
		 */
		URL,
		
		/**
		 * Search resources by title 
		 */
		TITLE,
		
		/** 
		 * "Smart" search by title (taking prefixes, stemming, etc. into account)
		 */
		TITLE_EXACT,
		
		/**
		 * Filter matches by their bundle ID.
		 */
		BUNDLE_ID, 

		/**
		 * Filter matches by their bundle ancestor ID.
		 */
		BUNDLE_ANCESTOR_ID, 
		
		/**
		 * HL7 registry OID
		 */
		OID,

		/**
		 * Search resources by status
		 */
		STATUS,
	}
	
	@Override
	protected final Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addSecurityFilter(context, queryBuilder);
		
		addFilter(queryBuilder, OptionKey.BUNDLE_ID, String.class, ResourceDocument.Expressions::bundleIds);
		if (containsKey(OptionKey.BUNDLE_ANCESTOR_ID)) {
			final Collection<String> ancestorIds = getCollection(OptionKey.BUNDLE_ANCESTOR_ID, String.class);
			queryBuilder.filter(Expressions.builder()
				.should(ResourceDocument.Expressions.bundleIds(ancestorIds))
				.should(ResourceDocument.Expressions.bundleAncestorIds(ancestorIds))
				.build());
		}

		addFilter(queryBuilder, OptionKey.OID, String.class, ResourceDocument.Expressions::oids);
		addFilter(queryBuilder, OptionKey.STATUS, String.class, ResourceDocument.Expressions::statuses);
		addIdFilter(queryBuilder, ResourceDocument.Expressions::ids);
		addTitleFilter(queryBuilder);
		addTitleExactFilter(queryBuilder);
		addUrlFilter(queryBuilder);
		
		prepareAdditionalFilters(context, queryBuilder);
		
		return queryBuilder.build();
	}
	
	/**
	 * Configures security filters to allow access to certain resources only. This method is no-op if the given {@link ServiceProvider context}'s {@link User} is an administrator or has read access to everything. 
	 * 
	 * @param context - the context where user information will be extracted
	 * @param queryBuilder - the query builder to append the clauses to
	 */
	protected final void addSecurityFilter(ServiceProvider context, ExpressionBuilder queryBuilder) {
		final User user = context.service(User.class);
		if (user.isAdministrator() || user.hasPermission(Permission.requireAll(Permission.OPERATION_BROWSE, Permission.ALL))) {
			return;
		}
		
		// extract read permissions
		final List<Permission> readPermissions = user.getPermissions().stream()
				.filter(p -> Permission.ALL.equals(p.getOperation()) || Permission.OPERATION_BROWSE.equals(p.getOperation()))
				.collect(Collectors.toList());
		
		final Set<String> exactResourceIds = readPermissions.stream()
				.flatMap(p -> p.getResources().stream())
				.filter(resource -> !resource.endsWith("*"))
				.collect(Collectors.toSet());
		final Set<String> resourceIdPrefixes = readPermissions.stream()
				.flatMap(p -> p.getResources().stream())
				.filter(resource -> resource.endsWith("*"))
				.map(resource -> resource.substring(0, resource.length() - 1))
				.collect(Collectors.toSet());
		
		if (!exactResourceIds.isEmpty() || !resourceIdPrefixes.isEmpty()) {
			context.log().info("Restricting user '{}' to resources exact: '{}', prefix: '{}'.", user.getUsername(), ImmutableSortedSet.copyOf(exactResourceIds), ImmutableSortedSet.copyOf(resourceIdPrefixes));
			ExpressionBuilder bool = Expressions.builder();
			// the permissions give access to either
			if (!exactResourceIds.isEmpty()) {
				// explicit IDs
				bool.should(ResourceDocument.Expressions.ids(exactResourceIds));
				// or the permitted resources are bundles which give access to all resources within it (recursively)
				bool.should(ResourceDocument.Expressions.bundleIds(exactResourceIds));
				bool.should(ResourceDocument.Expressions.bundleAncestorIds(exactResourceIds));
			}
			
			if (!resourceIdPrefixes.isEmpty()) {
				// partial IDs, prefixes
				bool.should(ResourceDocument.Expressions.idPrefixes(resourceIdPrefixes));
			}
			
			queryBuilder.filter(bool.build());
		}
	}

	/**
	 * Subclasses may override this method to provide additional filter clauses to the supplied bool {@link ExpressionBuilder}. This method does nothing by default.
	 * 
	 * @param context
	 * @param queryBuilder
	 */
	protected void prepareAdditionalFilters(RepositoryContext context, ExpressionBuilder queryBuilder) {
	}

	protected final void addUrlFilter(ExpressionBuilder queryBuilder) {
		addFilter(queryBuilder, OptionKey.URL, String.class, ResourceDocument.Expressions::urls);
	}
	
	protected final ExpressionBuilder addTitleFilter(ExpressionBuilder queryBuilder) {
		if (!containsKey(OptionKey.TITLE)) {
			return queryBuilder;
		}
		
		final TermFilter termFilter = get(OptionKey.TITLE, TermFilter.class);
		
		final ExpressionBuilder expressionBuilder = Expressions.builder();
		
		if (termFilter.isFuzzy()) {
			expressionBuilder.should(ResourceDocument.Expressions.titleFuzzy(termFilter.getTerm()));
		} else if (termFilter.isExact()) {
			expressionBuilder.should(ResourceDocument.Expressions.matchTitleExact(termFilter.getTerm(), termFilter.isCaseSensitive()));
		} else if (termFilter.isParsed()) {
			expressionBuilder.should(ResourceDocument.Expressions.parsedTitle(termFilter.getTerm()));
		} else if (termFilter.isAnyMatch()) {
			expressionBuilder.should(ResourceDocument.Expressions.minShouldMatchTermDisjunctionQuery(termFilter));
		} else {
			expressionBuilder.should(ResourceDocument.Expressions.defaultTitleDisjunctionQuery(termFilter));
		}
		
		expressionBuilder.should(Expressions.boost(ResourceDocument.Expressions.id(termFilter.getTerm()), 1000.0f));
		return queryBuilder.must(expressionBuilder.build());
	}
	
	protected final void addTitleExactFilter(ExpressionBuilder queryBuilder) {
		addFilter(queryBuilder, OptionKey.TITLE_EXACT, String.class, ResourceDocument.Expressions::titles);
	}

	@Override
	protected final Class<ResourceDocument> getSelect() {
		return ResourceDocument.class;
	}
}
