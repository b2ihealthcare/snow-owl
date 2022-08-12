/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.commit;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.regexp;
import static com.b2international.index.revision.Commit.Expressions.*;
import static com.b2international.index.revision.Commit.Fields.BRANCH;
import static com.b2international.index.revision.RevisionBranch.DEFAULT_MAXIMUM_BRANCH_NAME_LENGTH;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.MatchAll;
import com.b2international.index.revision.Commit;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.authorization.AuthorizationService;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @since 5.2
 */
final class CommitInfoSearchRequest extends SearchIndexResourceRequest<RepositoryContext, CommitInfos, Commit> {

	private static final long serialVersionUID = 1L;
	
	enum OptionKey {
		
		BRANCH,
		BRANCH_PREFIX,
		AUTHOR,
		COMMENT,
		TIME_STAMP_FROM,
		TIME_STAMP_TO,
		TIME_STAMP,
		AFFECTED_COMPONENT_ID
		
	}
	
	CommitInfoSearchRequest() {}

	@Override
	protected Class<Commit> getDocumentType() {
		return Commit.class;
	}
	
	@Override
	protected void prepareQuery(RepositoryContext context, ExpressionBuilder queryBuilder) {
		super.prepareQuery(context, queryBuilder);
		addIdFilter(queryBuilder, Commit.Expressions::ids);
		addSecurityFilter(context, queryBuilder);
		addBranchClause(queryBuilder, context);
		addBranchPrefixClause(queryBuilder);
		addUserIdClause(queryBuilder);
		addCommentClause(queryBuilder);
		addTimeStampClause(queryBuilder);
		addTimeStampRangeClause(queryBuilder);
		addAffectedComponentClause(queryBuilder);
	}
	
	@Override
	protected boolean trackScores() {
		return containsKey(OptionKey.COMMENT);
	}

	@Override
	protected CommitInfos toCollectionResource(RepositoryContext context, Hits<Commit> hits) {
		if (limit() < 1 || hits.getTotal() < 1) {
			return new CommitInfos(limit(), hits.getTotal());
		} else {
			return new CommitInfoConverter(context, expand(), locales(), options()).convert(hits.getHits(), hits.getSearchAfter(), limit(), hits.getTotal());
		}
	}
	
	@Override
	protected CommitInfos createEmptyResult(int limit) {
		return new CommitInfos(limit, 0);
	}
	
	private void addSecurityFilter(RepositoryContext context, final ExpressionBuilder builder) {
		
		final User user = context.service(User.class);
		if (user.isAdministrator() || user.hasPermission(Permission.requireAll(Permission.OPERATION_BROWSE, Permission.ALL))) {
			return;
		}
		
		// no need to perform security filtering when we are asking the metadata repository
		// TODO store the resourceUri/Id on the Commit objects to allow filtering by external authorization systems 
		if (context.info().id().equals("resources")) {
			return;
		}
		
		final Set<String> accessibleResources = context.optionalService(AuthorizationService.class)
				.orElse(AuthorizationService.DEFAULT)
				.getAccessibleResources(context, user);
		
		final Set<String> exactResourceIds = Sets.newHashSet();
		final Set<String> wildResourceIds = Sets.newHashSet();
		final Multimap<String, String> branchesByResourceId = HashMultimap.create();
		
		accessibleResources.stream()
				.forEach(resource -> {
					if (resource.endsWith("*")) {
						if (resource.endsWith("/*")) {
							// wild only match
							wildResourceIds.add(resource.replace("/*", ""));
						} else {
							// append to both exact and wild match
							String resourceToAdd = resource.replace("*", "");
							wildResourceIds.add(resourceToAdd);
							exactResourceIds.add(resourceToAdd);
						}
					} else if (resource.contains(Branch.SEPARATOR)) {
						branchesByResourceId.put(resource.substring(0, resource.indexOf(Branch.SEPARATOR)), resource.substring(resource.indexOf(Branch.SEPARATOR) + 1, resource.length()));
					} else {
						exactResourceIds.add(resource);
					}
				});
		
		Set<String> resourceIdsToSearchFor = ImmutableSet.<String>builder()
				.addAll(exactResourceIds)
				.addAll(wildResourceIds)
				.addAll(branchesByResourceId.keySet())
				.build();
		
		ExpressionBuilder branchFilter = Expressions.bool();
		ResourceRequests.prepareSearch()
			.filterByIds(resourceIdsToSearchFor)
			.setLimit(resourceIdsToSearchFor.size())
			.setFields(ResourceDocument.Fields.ID, 
					ResourceDocument.Fields.BRANCH_PATH,
					ResourceDocument.Fields.RESOURCE_TYPE)
			.buildAsync()
			.getRequest()
			.execute(context)
			.stream()
			.filter(TerminologyResource.class::isInstance)
			.map(TerminologyResource.class::cast)
			.forEach(res -> {
				// if present as prefix query, append the branch regex filter
				if (wildResourceIds.contains(res.getId())) {
					final String branchPattern = String.format("%s/[a-zA-Z0-9.~_\\-]{1,%d}", res.getBranchPath(), DEFAULT_MAXIMUM_BRANCH_NAME_LENGTH);
					branchFilter.should(regexp(BRANCH, branchPattern));
				}
				// if present as exact query, append exact match
				if (exactResourceIds.contains(res.getId())) {
					branchFilter.should(exactMatch(BRANCH, res.getBranchPath()));
				}
				
				// if present as exact branch match, then append a terms filter
				final Collection<String> exactBranchesToMatch = branchesByResourceId.removeAll(res.getId());
				if (!exactBranchesToMatch.isEmpty()) {
					branchFilter.should(Commit.Expressions.branches(exactBranchesToMatch.stream().map(res::getRelativeBranchPath).collect(Collectors.toSet())));
				}
			});
		
		// if the built expression does not have any clauses
		Expression authorizationClause = branchFilter.build();
		if (authorizationClause instanceof MatchAll) {
			// then match no documents instead
			throw new NoResultException();
		}
		
		builder.filter(authorizationClause);
	}
	
	private void addBranchClause(final ExpressionBuilder builder, RepositoryContext context) {
		if (containsKey(OptionKey.BRANCH)) {
			final Collection<String> branchPaths = getCollection(OptionKey.BRANCH, String.class);
			builder.filter(branches(branchPaths));
		}
	}
	
	private void addUserIdClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.AUTHOR)) {
			final String userId = getString(OptionKey.AUTHOR);
			builder.filter(author(userId));
		}
	}

	private void addCommentClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.COMMENT)) {
			final String comment = getString(OptionKey.COMMENT);
			builder.must(Expressions.dismaxWithScoreCategories(
				exactComment(comment),
				allCommentPrefixesPresent(comment)
			));
		}
	}

	private void addTimeStampClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.TIME_STAMP)) {
			final Iterable<Long> timestamps = getCollection(OptionKey.TIME_STAMP, Long.class);
			builder.filter(timestamps(timestamps));
		}
	}

	private void addTimeStampRangeClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.TIME_STAMP_FROM) || containsKey(OptionKey.TIME_STAMP_TO)) {
			final Long timestampFrom = containsKey(OptionKey.TIME_STAMP_FROM) ? get(OptionKey.TIME_STAMP_FROM, Long.class) : 0L;
			final Long timestampTo = containsKey(OptionKey.TIME_STAMP_TO) ? get(OptionKey.TIME_STAMP_TO, Long.class) : Long.MAX_VALUE;
			builder.filter(timestampRange(timestampFrom, timestampTo));
		}
	}
	
	private void addAffectedComponentClause(final ExpressionBuilder builder) {
		if (containsKey(OptionKey.AFFECTED_COMPONENT_ID)) {
			final String affectedComponentId = getString(OptionKey.AFFECTED_COMPONENT_ID);
			builder.filter(affectedObject(affectedComponentId));
		}
	}
	
	private void addBranchPrefixClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.BRANCH_PREFIX)) {
			final String branchPrefix = getString(OptionKey.BRANCH_PREFIX);
			queryBuilder.filter(branchPrefix(branchPrefix));
		}
	}

}
