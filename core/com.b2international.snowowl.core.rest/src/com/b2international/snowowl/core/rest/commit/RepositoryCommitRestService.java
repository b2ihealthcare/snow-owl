/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.commit;

import java.util.List;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.b2international.commons.CompareUtils;
import com.b2international.index.revision.Commit;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.domain.ResourceSelectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 7.0
 */
@Tag(description = "Commits", name = "commits")
@RequestMapping(value="/commits")
public abstract class RepositoryCommitRestService extends AbstractRestService {

	private final String repositoryId;

	public RepositoryCommitRestService(String repositoryId) {
		super(Commit.Fields.ALL);
		this.repositoryId = repositoryId;
	}
	
	@Operation(
		summary = "Retrieve commit entries",
		description = "Returns all SNOMED CT commits"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CommitInfos> search(@ParameterObject CommitInfoRestSearch params) {
		
		final List<String> fields;
		if (!CompareUtils.isEmpty(params.getField())) {
			fields = params.getField();
		} else if (CompareUtils.isEmpty(params.getExpand())) {
			fields = CommitInfo.Fields.DEAFULT_FIELD_SELECTION;
		} else {
			fields = null;
		}
		
		return RepositoryRequests
					.commitInfos()
					.prepareSearchCommitInfo()
					.filterByIds(params.getId())
					.filterByAuthor(params.getAuthor())
					.filterByAffectedComponent(params.getAffectedComponentId())
					.filterByComment(params.getComment())
					.filterByBranches(params.getBranch())
					.filterByTimestamp(params.getTimestamp())
					.filterByTimestamp(params.getTimestampFrom(), params.getTimestampTo())
					.setFields(fields)
					.setExpand(params.getExpand())
					.setSearchAfter(params.getSearchAfter())
					.setLimit(params.getLimit())
					.sortBy(extractSortFields(params.getSort()))
					.build(repositoryId)
					.execute(getBus());
	}
	
	@Operation(
		summary = "Retrieve a commit",
		description = "Returns a single commit entry from SNOMED CT commits"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@GetMapping(value = "/{commitId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CommitInfo> get(
			@Parameter(description = "Commit ID to match")
			@PathVariable(value="commitId")
			final String commitId, 
			
			@ParameterObject
			final ResourceSelectors selectors) {
		return RepositoryRequests.commitInfos()
					.prepareGetCommitInfo(commitId)
					.setExpand(selectors.getExpand())
					.setFields(selectors.getField())
					.build(repositoryId)
					.execute(getBus());
	}
}
