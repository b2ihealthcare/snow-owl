/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.index.revision.Commit.Expressions.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.ResourceSearchRequestBuilder;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

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
	protected Expression prepareQuery(RepositoryContext context) {
		ExpressionBuilder queryBuilder = Expressions.builder();
		addIdFilter(queryBuilder, Commit.Expressions::ids);
		addSecurityFilter(queryBuilder, context);
		addBranchPrefixClause(queryBuilder);
		addUserIdClause(queryBuilder);
		addCommentClause(queryBuilder);
		addTimeStampClause(queryBuilder);
		addTimeStampRangeClause(queryBuilder);
		addAffectedComponentClause(queryBuilder);
		return queryBuilder.build();
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
	
	private void addSecurityFilter(final ExpressionBuilder builder, RepositoryContext context) {
		
		final User user = context.service(User.class);
		if (user.isAdministrator() || user.hasPermission(Permission.requireAll(Permission.OPERATION_BROWSE, Permission.ALL))) {
			if (containsKey(OptionKey.BRANCH)) {
				addBranchClause(builder, context, null, null, true);		
			}
			return;
		}
		
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
		
		if (containsKey(OptionKey.BRANCH)) {
			addBranchClause(builder, context, exactResourceIds, resourceIdPrefixes, false);
			return;
		}
		
		ExpressionBuilder branchFilter = Expressions.builder();
		
		SetView<String> resourceIds = Sets.union(exactResourceIds, resourceIdPrefixes);
		ResourceRequests.prepareSearch()
			.filterByIds(resourceIds)
			.setLimit(resourceIds.size())
			.setFields(ResourceDocument.Fields.ID, 
					ResourceDocument.Fields.BRANCH_PATH,
					ResourceDocument.Fields.RESOURCE_TYPE)
			.buildAsync()
			.getRequest()
			.execute(context)
			.stream()
			.filter(TerminologyResource.class::isInstance)
			.map(TerminologyResource.class::cast)
			.forEach(r -> {				
				if (resourceIdPrefixes.contains(r.getId())) {
					final String branchPattern = String.format("%s/[^[%s]{1,%d}(_[0-9]{1,19})?$]+", r.getBranchPath(), 
							RevisionBranch.DEFAULT_ALLOWED_BRANCH_NAME_CHARACTER_SET, RevisionBranch.DEFAULT_MAXIMUM_BRANCH_NAME_LENGTH);
					branchFilter.should(branches(branchPattern));
				}
				branchFilter.should(branches(r.getBranchPath()));
			});
		
		builder.filter(branchFilter.build());
	}
	
	private void addBranchClause(final ExpressionBuilder builder, RepositoryContext context, 
			Set<String> exactResourceIds, Set<String> resourceIdPrefixes, boolean isAdmin) {		
		final Collection<String> branchPaths = getCollection(OptionKey.BRANCH, String.class);
				
		ExpressionBuilder branchFilter = Expressions.builder();
		ResourceSearchRequestBuilder resourceRequest = ResourceRequests.prepareSearch();
		
		if (isAdmin) {
			resourceRequest.setLimit(Integer.MAX_VALUE);			
		} else {
			SetView<String> resourceIds = Sets.union(exactResourceIds, resourceIdPrefixes);
			resourceRequest.filterByIds(resourceIds).setLimit(resourceIds.size());
		}
		
		resourceRequest
			.setFields(ResourceDocument.Fields.ID, 
					ResourceDocument.Fields.BRANCH_PATH,
					ResourceDocument.Fields.RESOURCE_TYPE)
			.buildAsync()
			.getRequest()
			.execute(context)
			.stream()
			.filter(TerminologyResource.class::isInstance)
			.map(TerminologyResource.class::cast)
			.forEach(r -> {
				if (branchPaths.contains(r.getBranchPath())) {
					branchFilter.should(branches(r.getBranchPath()));
				}
				
				for (String branchPath : branchPaths) {
					if (branchPath.startsWith(r.getBranchPath())) {
						int resourceBranchDepth = r.getBranchPath().split("/").length;
						int filterBranchDepth = branchPath.split("/").length;
						
						if (filterBranchDepth - 1 <= resourceBranchDepth) {
							branchFilter.should(branches(branchPath));
						}
					}
				}
					
			});
		
		builder.filter(branchFilter.build());
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
