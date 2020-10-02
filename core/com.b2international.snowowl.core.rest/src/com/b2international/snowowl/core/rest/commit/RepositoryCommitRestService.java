/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.b2international.index.revision.Commit;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;

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
		@ApiResponse(responseCode = "200", description="OK")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CommitInfos> search(
			@Parameter(description = "The author of the commit to match")
			@RequestParam(value="author", required=false)
			final String author,
			
			@Parameter(description = "The identifier(s) to match")
			@RequestParam(value="id", required=false)
			final Set<String> id,
			
			@Parameter(description = "Affected component identifier to match")
			@RequestParam(value="affectedComponentId", required=false)
			final String affectedComponentId,
			
			@Parameter(description = "Commit comment term to match")
			@RequestParam(value="comment", required=false)
			final String comment,
			
			@Parameter(description = "One or more branch paths to match")
			@RequestParam(value="branch", required=false)
			final List<String> branch,
			
			@Parameter(description = "Commit timestamp to match")
			@RequestParam(value="timestamp", required=false)
			final Long timestamp,
			
			@Parameter(description = "Minimum commit timestamp to search matches from")
			@RequestParam(value="timestampFrom", required=false)
			final Long timestampFrom,
			
			@Parameter(description = "Maximum commit timestamp to search matches to")
			@RequestParam(value="timestampTo", required=false)
			final Long timestampTo,
			
			@Parameter(description = "Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,
			
			@Parameter(description = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@Parameter(description = "Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sort,
			
			@Parameter(description = "The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {
		return RepositoryRequests
					.commitInfos()
					.prepareSearchCommitInfo()
					.filterByIds(id)
					.filterByAuthor(author)
					.filterByAffectedComponent(affectedComponentId)
					.filterByComment(comment)
					.filterByBranches(branch)
					.filterByTimestamp(timestamp)
					.filterByTimestamp(timestampFrom, timestampTo)
					.setExpand(expand)
					.setSearchAfter(searchAfter)
					.setLimit(limit)
					.sortBy(extractSortFields(sort))
					.build(repositoryId)
					.execute(getBus());
	}
	
	@Operation(
		summary = "Retrieve a commit",
		description = "Returns a single commit entry from SNOMED CT commits"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK")
	})
	@GetMapping(value = "/{commitId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CommitInfo> get(
			@Parameter(description = "Commit ID to match")
			@PathVariable(value="commitId")
			final String commitId, 
			
			@Parameter(description = "Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand) {
		return RepositoryRequests
					.commitInfos()
					.prepareGetCommitInfo(commitId)
					.setExpand(expand)
					.build(repositoryId)
					.execute(getBus());
	}
}
