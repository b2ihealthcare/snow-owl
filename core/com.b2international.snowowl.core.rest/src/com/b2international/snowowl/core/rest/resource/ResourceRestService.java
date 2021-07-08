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
package com.b2international.snowowl.core.rest.resource;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.*;

import com.b2international.index.revision.Commit;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.google.common.base.Strings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.0
 */
@Tag(description = "Resources", name = "resources")
@RestController
@RequestMapping("/resources")
public class ResourceRestService extends AbstractRestService {
	
	public ResourceRestService() {
		super(Commit.Fields.ALL);
	}

	@Operation(
		summary = "Retrive Resources", 
		description = "Returns a collection resource containing all/filtered registered Resources."
			+ "<p>Results are by default sorted by ID.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Bad Request") })
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Resources> searchByGet(final ResourceRestSearch params) {
		return ResourceRequests
				.prepareSearch()
				.filterByIds(params.getId())
				.filterByResourceType(params.getResourceType())
				.filterByTitleExact(params.getTitleExact())
				.filterByTitle(params.getTitle())
				.filterByToolingIds(params.getToolingId())
				.filterByBundleIds(params.getBundleId())
				.filterByStatus(params.getStatus())
				.setLimit(params.getLimit())
				.setExpand(params.getExpand())
				.setSearchAfter(params.getSearchAfter())
				.sortBy(extractSortFields(params.getSort()))
				.buildAsync()
				.execute(getBus());
	}

	@Operation(
		summary = "Retrieve resource by it's unique identifier", 
		description = "Returns generic information about a single resource associated to the given unique identifier."
	)
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"), 
		@ApiResponse(responseCode = "404", description = "Not Found") 
	})
	@GetMapping(value = "/{resourceId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Resource> get(
			@Parameter(description = "The resource identifier") 
			@PathVariable(value = "resourceId") 
			final String resourceId) {
		return ResourceRequests
				.prepareGet(resourceId)
				.buildAsync()
				.execute(getBus());
	}

	@Operation(
		summary = "Retrive all resource commits", 
		description = "Returns a collection, that contains all/filtered resource commits")
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@GetMapping(value = "/commits", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CommitInfos> searchCommits(
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
		 Request<RepositoryContext, CommitInfos> req = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByIds(id)
				.filterByAuthor(author)
				.filterByAffectedComponent(affectedComponentId)
				.filterByComment(comment)
				.filterByBranches(branch)
				.filterByTimestamp(timestamp)
				.filterByTimestamp(timestampFrom, timestampTo)
				.setFields(Strings.isNullOrEmpty(expand) ? List.of(Commit.Fields.ID, Commit.Fields.AUTHOR, Commit.Fields.BRANCH, Commit.Fields.COMMENT, Commit.Fields.TIMESTAMP, Commit.Fields.GROUP_ID) : null)
				.setExpand(expand)
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.sortBy(extractSortFields(sort))
				.build();
		 return new ResourceRepositoryRequestBuilder<CommitInfos>() {

			@Override
			public Request<RepositoryContext, CommitInfos> build() {
				return req;
			}
			
		 }
		 .buildAsync()
		 .execute(getBus());
	}
}
