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
import java.util.stream.Stream;

import com.b2international.index.query.Expressions;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeCollection;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobTracker;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobs;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

/**
 * @since 7.1
 */
public class SearchMergeRequest implements Request<RepositoryContext, MergeCollection> {

	private static final long serialVersionUID = 1L;
	
	private static final String SOURCE_FIELD = "source";
	private static final String TARGET_FIELD = "target";
	private static final String STATUS_FIELD = "status";
	
	private final String source;
	private final String target;
	private final String status;
	
	public SearchMergeRequest(final String source, final String target, final String status) {
		this.source = source; 
		this.target = target;
		this.status = status;
	}
	
	@Override
	public MergeCollection execute(RepositoryContext context) {
		final RemoteJobs jobs = context.service(RemoteJobTracker.class).search(Expressions.matchAll(), Integer.MAX_VALUE);
		final ObjectMapper mapper = context.service(ObjectMapper.class);
		final Stream<RemoteJobEntry> resultingJobs = jobs.stream().filter(RemoteJobEntry::isDone);
		
		if (!Strings.isNullOrEmpty(source)) {
			addSourceClause(mapper, resultingJobs);
		}
		
		if  (!Strings.isNullOrEmpty(target)) {
			addTargetClause(mapper, resultingJobs);
		}
		
		if (!Strings.isNullOrEmpty(status)) {
			addStatusClause(mapper, resultingJobs);
		}
		
		final List<Merge> matchingMerges = resultingJobs.map(job -> job.getResultAs(mapper, Merge.class)).collect(Collectors.toList());
		
		return new MergeCollection(matchingMerges);
	}
	
	private void addSourceClause(ObjectMapper mapper, final Stream<RemoteJobEntry> resultingJobs) {
		resultingJobs.filter(job -> {
			final Map<String, Object> jobParams = job.getParameters(mapper);
			final String source = (String) jobParams.get(SOURCE_FIELD);
			
			return this.source.equals(source);
		});
	}
	
	private void addTargetClause(ObjectMapper mapper, final Stream<RemoteJobEntry> resultingJobs) {
		resultingJobs.filter(job -> {
			final Map<String, Object> jobParams = job.getParameters(mapper);
			final String source = (String) jobParams.get(TARGET_FIELD);
			
			return this.target.equals(source);
		});
	}


	private void addStatusClause(ObjectMapper mapper, final Stream<RemoteJobEntry> resultingJobs) {
		resultingJobs.filter(job -> {
			final Map<String, Object> jobParams = job.getParameters(mapper);
			final String source = (String) jobParams.get(STATUS_FIELD);
			
			return this.status.equals(source);
		});
	}

}
