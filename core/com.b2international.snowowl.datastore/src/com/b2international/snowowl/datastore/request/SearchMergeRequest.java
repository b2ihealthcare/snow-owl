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

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.nestedMatch;

import java.util.stream.Collectors;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
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
		final ExpressionBuilder expressionBuilder = Expressions.builder();
		if (!Strings.isNullOrEmpty(source)) {
			expressionBuilder.filter(nestedMatch(RemoteJobEntry.Fields.PARAMETERS, exactMatch(SOURCE_FIELD, source)));
		}
		
		if  (!Strings.isNullOrEmpty(target)) {
			expressionBuilder.filter(nestedMatch(RemoteJobEntry.Fields.PARAMETERS, exactMatch(TARGET_FIELD, target)));
		}
		
		if (!Strings.isNullOrEmpty(status)) {
			expressionBuilder.filter(nestedMatch(RemoteJobEntry.Fields.PARAMETERS, exactMatch(STATUS_FIELD, status)));
		}
		
		final RemoteJobs jobs = context.service(RemoteJobTracker.class).search(expressionBuilder.build(), Integer.MAX_VALUE);
		final ObjectMapper mapper = context.service(ObjectMapper.class);
		return new MergeCollection(jobs.stream().filter(RemoteJobEntry::isDone).map(job -> job.getResultAs(mapper, Merge.class)).collect(Collectors.toList()));
	}
	

}
