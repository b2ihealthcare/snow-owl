/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.index.revision.Commit;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.util.DeferredResults;
import com.b2international.snowowl.datastore.request.RepositoryRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 7.0
 */
@Tag(name = "commits", description="Commits")
@RestController
@RequestMapping(value="/commits")
public class SnomedCommitInfoRestService extends AbstractSnomedRestService {

	public SnomedCommitInfoRestService() {
		super(Commit.Fields.ALL);
	}
	
	@Operation(
		summary = "Retrieve commit entries",
		description = "Returns all SNOMED CT commits"
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response=CollectionResource.class)
//	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public DeferredResult<CommitInfos> search(
			@Parameter(description="The author of the commit to match")
			@RequestParam(value="author", required=false)
			final String author,
			
			@Parameter(description="Affected component identifier to match")
			@RequestParam(value="affectedComponentId", required=false)
			final String affectedComponentId,
			
			@Parameter(description="Commit comment term to match")
			@RequestParam(value="comment", required=false)
			final String comment,
			
			@Parameter(description="One or more branch paths to match")
			@RequestParam(value="branch", required=false)
			final List<String> branch,
			
			@Parameter(description="Commit timestamp to match")
			@RequestParam(value="timestamp", required=false)
			final Long timestamp,
			
			@Parameter(description="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,
			
			@Parameter(description = "The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false)
			final String scrollKeepAlive,
			
			@Parameter(description = "A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false)
			final String scrollId,
			
			@Parameter(description = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@Parameter(description="Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sort,
			
			@Parameter(description="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {
		return DeferredResults.wrap(
				RepositoryRequests
					.commitInfos()
					.prepareSearchCommitInfo()
					.filterByAuthor(author)
					.filterByAffectedComponent(affectedComponentId)
					.filterByComment(comment)
					.filterByBranches(branch)
					.filterByTimestamp(timestamp)
					.setExpand(expand)
					.setScroll(scrollKeepAlive)
					.setScrollId(scrollId)
					.setSearchAfter(searchAfter)
					.setLimit(limit)
					.sortBy(extractSortFields(sort))
					.build(repositoryId)
					.execute(bus));
	}
	
	@Operation(
		summary = "Retrieve a commit",
		description = "Returns a single commit entry from SNOMED CT commits"
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response=CollectionResource.class)
//	})
	@GetMapping(value = "/{commitId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public DeferredResult<CommitInfo> get(
			@Parameter(description="Commit ID to match")
			@PathVariable(value="commitId")
			final String commitId, 
			
			@Parameter(description="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand) {
		return DeferredResults.wrap(
				RepositoryRequests
					.commitInfos()
					.prepareGetCommitInfo(commitId)
					.setExpand(expand)
					.build(repositoryId)
					.execute(bus));
	}
}
