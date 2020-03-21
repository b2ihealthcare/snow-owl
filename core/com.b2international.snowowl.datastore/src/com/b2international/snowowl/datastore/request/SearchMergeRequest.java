/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.branch.BranchMergeRequest;
import com.b2international.snowowl.core.branch.BranchRebaseRequest;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.Merge.Builder;
import com.b2international.snowowl.core.merge.Merge.Status;
import com.b2international.snowowl.core.merge.Merges;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobTracker;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobs;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

/**
 * @since 7.1
 */
public final class SearchMergeRequest extends SearchResourceRequest<RepositoryContext, Merges> implements RepositoryAccessControl {

	private static final String SOURCE_FIELD = "source";
	private static final String TARGET_FIELD = "target";
	private static final String STATUS_FIELD = "status";

	public enum OptionKey {
		
		SOURCE,
		
		TARGET,
		
		STATUS
		
	}

	@Override
	protected Merges createEmptyResult(int limit) {
		return new Merges(limit, 0);
	}
	
	@Override
	public Merges doExecute(RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		// add mergerequest type filter
		queryBuilder.filter(RemoteJobEntry.Expressions.matchRequestType(ImmutableSet.of(BranchRebaseRequest.class.getSimpleName(), BranchMergeRequest.class.getSimpleName())));
		
		if (containsKey(OptionKey.SOURCE)) {
			queryBuilder.filter(RemoteJobEntry.Expressions.matchParameter(SOURCE_FIELD, getCollection(OptionKey.SOURCE, String.class)));
		}
		
		if  (containsKey(OptionKey.TARGET)) {
			queryBuilder.filter(RemoteJobEntry.Expressions.matchParameter(TARGET_FIELD, getCollection(OptionKey.TARGET, String.class)));
		}
		
		if (containsKey(OptionKey.STATUS)) {
			queryBuilder.filter(RemoteJobEntry.Expressions.matchParameter(STATUS_FIELD, getCollection(OptionKey.STATUS, String.class)));
		}
		
		final RemoteJobs jobs = context.service(RemoteJobTracker.class).search(queryBuilder.build(), Integer.MAX_VALUE);
		final ObjectMapper mapper = context.service(ObjectMapper.class);
		final List<Merge> items = jobs.stream().map(job -> createMergefromJobEntry(job, mapper)).collect(Collectors.toList());
		return new Merges(items, jobs.getSearchAfter(), jobs.getLimit(), jobs.getTotal());
	}
	
	@Override
	public String getOperation() {
		return Permission.BROWSE;
	}

	public static Merge createMergefromJobEntry(RemoteJobEntry job, ObjectMapper mapper) {
		// the final state will be available as job result value
		if (job.isSuccessful()) {
			return job.getResultAs(mapper, Merge.class);
		}
		
		// any other state should be computed from the ongoing remote job
		final Map<String, Object> params = job.getParameters(mapper);
		final String source = (String) params.get(SOURCE_FIELD);
		final String target = (String) params.get(TARGET_FIELD);
		final Builder merge = Merge.builder()
				.id(job.getId())
				.source(source)
				.target(target)
				.startDate(job.getStartDate())
				.scheduledDate(job.getScheduleDate())
				.endDate(job.getFinishDate());
		
		switch (job.getState()) {
		case FINISHED:
			return merge.status(Status.COMPLETED).build();
		case CANCEL_REQUESTED:
			return merge.status(Status.CANCEL_REQUESTED).build();
		case CANCELED:
			return merge.status(Status.FAILED).build();
		case FAILED:
			return merge
					.status(Status.FAILED)
					.apiError(job.getResultAs(mapper, ApiError.class))
					.build();
		case RUNNING:
			return merge.status(Status.IN_PROGRESS).build();
		case SCHEDULED:
			return merge.status(Status.SCHEDULED).build();
		default:
			throw new NotImplementedException("Not implemented case for " + job.getState());
		}
	}

}
