/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.resource;

import static com.b2international.snowowl.core.internal.ResourceDocument.Expressions.bundleAncestorIds;
import static com.b2international.snowowl.core.internal.ResourceDocument.Expressions.bundleIds;
import static com.b2international.snowowl.core.internal.ResourceDocument.Expressions.hidden;

import java.util.*;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ForbiddenException;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Builder;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.authorization.AuthorizationService;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.search.TermFilter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

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
		
		/**
		 * Search resources by owner
		 */
		OWNER,
		
		/**
		 * Search resources by the presence of a property or by a specific property-value pair in settings
		 */
		SETTINGS
	}
	
	@Override
	protected final Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.bool();
		// always apply the non-hidden resource filter, as hidden resources are meant to be internal documents managed by other services
		queryBuilder.filter(hidden(false));
		
		addSecurityFilter(context, queryBuilder);
		
		addFilter(queryBuilder, OptionKey.BUNDLE_ID, String.class, ResourceDocument.Expressions::bundleIds);
		if (containsKey(OptionKey.BUNDLE_ANCESTOR_ID)) {
			final Collection<String> ancestorIds = getCollection(OptionKey.BUNDLE_ANCESTOR_ID, String.class);
			queryBuilder.filter(Expressions.bool()
				.should(bundleIds(ancestorIds))
				.should(bundleAncestorIds(ancestorIds))
				.build());
		}

		if (containsKey(OptionKey.SETTINGS)) {
			final Collection<String> properties = getCollection(OptionKey.SETTINGS, String.class);
			properties.forEach( property -> {
				if (property.contains(Resource.SETTINGS_DELIMITER)) {
					final String propertyName = property.split(Resource.SETTINGS_DELIMITER)[0];
					final String propertyValue = property.substring(propertyName.length() + 1, property.length());
					if (Strings.isNullOrEmpty(propertyValue)) {
						throw new BadRequestException("Settings argument %s is not allowed. Expected format is propertyName" + Resource.SETTINGS_DELIMITER + "propertyValue.", property);
					}
					//Check if property has specified value
					if (propertyValue.endsWith("*")) {
						queryBuilder.filter(Expressions.prefixMatch(String.format("settings.%s", propertyName), propertyValue.substring(0, (propertyValue.length() - 1))));						
					} else {
						queryBuilder.filter(Expressions.exactMatch(String.format("settings.%s", propertyName), propertyValue));															
					}
				} else {
					//Check if property exists
					queryBuilder.filter(Expressions.exists(String.format("settings.%s", property)));
				}
			});
		}
		
		addFilter(queryBuilder, OptionKey.OID, String.class, ResourceDocument.Expressions::oids);
		addFilterWithNegations(queryBuilder, OptionKey.OWNER, ResourceDocument.Expressions::owners);
		addFilterWithNegations(queryBuilder, OptionKey.STATUS, ResourceDocument.Expressions::statuses);
		addIdFilter(queryBuilder, ResourceDocument.Expressions::ids);
		addTitleFilter(queryBuilder);
		addFilter(queryBuilder, OptionKey.TITLE_EXACT, String.class, ResourceDocument.Expressions::titles);
		addUrlFilter(queryBuilder);
		
		prepareAdditionalFilters(context, queryBuilder);
		
		return queryBuilder.build();
	}
	
	@Override
	protected boolean trackScores() {
		return containsKey(OptionKey.TITLE);
	}
	
	/**
	 * Configures security filters to allow access to certain resources only. This method is no-op if the given {@link ServiceProvider context}'s {@link User} is an administrator or has read access to everything. 
	 * 
	 * @param context - the context where user information will be extracted
	 * @param queryBuilder - the query builder to append the clauses to
	 */
	protected final void addSecurityFilter(RepositoryContext context, ExpressionBuilder queryBuilder) {
		final User user = context.service(User.class);
		if (user.isAdministrator() || user.hasPermission(Permission.requireAll(Permission.OPERATION_BROWSE, Permission.ALL))) {
			return;
		}
		
		final AuthorizationService authz = context.optionalService(AuthorizationService.class).orElse(AuthorizationService.DEFAULT);
		
		// special check for a single resource content access request to not lookup all the visible resources for the user but to check only the one needed for faster execution
		if (!CompareUtils.isEmpty(componentIds()) && componentIds().size() == 1) {
			try {
				authz.checkPermission(context, user, List.of(Permission.requireAll(Permission.OPERATION_BROWSE, Iterables.getOnlyElement(componentIds()))));
				return;
			} catch (ForbiddenException e) {
				// if this fails let the system carry on with the default execution path and check all visible resources, including bundles
				// TODO remove when we have proper bundle management in the authorization subsystem
			}
		}
		
		final Set<String> accessibleResources = authz.getAccessibleResources(context, user);
		final SortedSet<String> exactResourceIds = accessibleResources.stream()
				.filter(resource -> !resource.endsWith("*"))
				.collect(ImmutableSortedSet.toImmutableSortedSet(String::compareTo));
		
		final SortedSet<String> resourceIdPrefixes = accessibleResources.stream()
				.filter(resource -> resource.endsWith("*"))
				.map(resource -> resource.substring(0, resource.length() - 1))
				.collect(ImmutableSortedSet.toImmutableSortedSet(String::compareTo));
		
		if (!exactResourceIds.isEmpty() || !resourceIdPrefixes.isEmpty()) {
			context.log().trace("Restricting user '{}' to resources exact: '{}', prefix: '{}'.", user.getUserId(), exactResourceIds, resourceIdPrefixes);
			ExpressionBuilder bool = Expressions.bool();
			// the permissions give access to either
			if (!exactResourceIds.isEmpty()) {
				// explicit IDs
				bool.should(ResourceDocument.Expressions.ids(exactResourceIds));
				if (authz.isDefault()) {
					// or the permitted resources are bundles which give access to all resources within it (recursively) (perform only in default mode, let external authorization systems handle this)
					bool.should(ResourceDocument.Expressions.bundleIds(exactResourceIds));
					bool.should(ResourceDocument.Expressions.bundleAncestorIds(exactResourceIds));
				}
				// allow backward compatibility with older authorization systems where repositoryId/toolingIds are being used in permissions
				// XXX this needs to be removed in Snow Owl 9, once we completely eliminate reflective access and toolingId/branch support from the Java API
				bool.should(ResourceDocument.Expressions.toolingIds(exactResourceIds));
			}
			
			if (!resourceIdPrefixes.isEmpty()) {
				// partial IDs, prefixes
				bool.should(ResourceDocument.Expressions.idPrefixes(resourceIdPrefixes));
				if (authz.isDefault()) {
					// or the permitted resources are bundle ID prefixes which give access to all resources within it (recursively) (perform only in default mode, let external authorization systems handle this)
					bool.should(ResourceDocument.Expressions.bundleIdPrefixes(resourceIdPrefixes));
					bool.should(ResourceDocument.Expressions.bundleAncestorIdPrefixes(resourceIdPrefixes));
				}
			}
			
			queryBuilder.filter(bool.build());
		} else {
			throw new NoResultException();
		}
	}

	/**
	 * Subclasses may override this method to provide additional filter clauses to the supplied bool {@link ExpressionBuilder}. This method does nothing by default.
	 * 
	 * @param context
	 * @param queryBuilder
	 */
	@OverridingMethodsMustInvokeSuper
	protected void prepareAdditionalFilters(RepositoryContext context, ExpressionBuilder queryBuilder) {
	}

	protected final void addUrlFilter(ExpressionBuilder queryBuilder) {
		addFilter(queryBuilder, OptionKey.URL, String.class, ResourceDocument.Expressions::urls);
	}
	
	protected final void addTitleFilter(ExpressionBuilder queryBuilder) {
		if (!containsKey(OptionKey.TITLE)) {
			return;
		}
		
		final TermFilter termFilter = get(OptionKey.TITLE, TermFilter.class);
		
		final Set<String> terms = termFilter.getTerms();
		
		// in case of searching for a single term in the term filter add a boosted query in case that term matches an ID
		final Expression titleQuery = termFilter.toExpression(ResourceDocument.Fields.TITLE);
		if (terms.size() == 1) {
			final ExpressionBuilder expressionBuilder = Expressions.bool();
			expressionBuilder.should(titleQuery);
			final String termFilterValue = Iterables.getOnlyElement(terms);
			expressionBuilder.should(Expressions.boost(ResourceDocument.Expressions.ids(List.of(termFilterValue, termFilterValue.toUpperCase())), 100.0f));
			queryBuilder.must(expressionBuilder.build());
		} else {
			queryBuilder.must(titleQuery);
		}
	}
	
	@Override
	protected final Class<ResourceDocument> getSelect() {
		return ResourceDocument.class;
	}
	
	@Override
	protected void toQuerySortBy(RepositoryContext context, Builder sortBuilder, Sort sort) {
		if (sort instanceof SortField) {
			SortField sortField = (SortField) sort;
			if (Resource.SNOMED_FIRST.equals(sortField.getField())) {
				sortBuilder.sortByScript("snomedFirst", Map.of(), sort.isAscending() ? Order.ASC : Order.DESC);
				return;
			}
		}
		super.toQuerySortBy(context, sortBuilder, sort);
	}
	
	@Override
	protected SortBy getDefaultSortBy() {
		return SortBy.builder().sortByScript("snomedFirst", Map.of(), Order.ASC).build();
	}
	
}
