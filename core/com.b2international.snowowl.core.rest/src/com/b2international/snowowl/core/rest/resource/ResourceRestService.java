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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.b2international.snowowl.core.rest.RestApiError;
import com.google.common.base.Strings;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 8.0
 */
@Api(value = "Resources", description = "Resources", tags = { "resources" })
@RestController
@RequestMapping("/resources")
public class ResourceRestService extends AbstractRestService {
	
	public ResourceRestService() {
		super(Commit.Fields.ALL);
	}

	@ApiOperation(value = "Retrive Resources", notes = "Returns a collection resource containing all/filtered registered Resources."
			+ "<p>Results are by default sorted by ID.")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = Resources.class),
			@ApiResponse(code = 400, message = "Bad Request", response = RestApiError.class) })
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

	@ApiOperation(value = "Retrieve resource by it's unique identifier", notes = "Returns generic information about a single resource associated to the given unique identifier.")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "Not Found", response = RestApiError.class) })
	@GetMapping(value = "/{resourceId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Resource> get(@ApiParam(value = "The resource identifier") @PathVariable(value = "resourceId") final String resourceId) {
		return ResourceRequests
				.prepareGet(resourceId)
				.buildAsync()
				.execute(getBus());
	}

	@ApiOperation(value = "Retrive all resource commits", notes = "Returns a collection, that contains all/filtered resource commits")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = CommitInfos.class),
			@ApiResponse(code = 400, message = "Bad Request", response = RestApiError.class) })
	@GetMapping(value = "/commits", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CommitInfos> searchCommits(@ApiParam(value = "The author of the commit to match")
	@RequestParam(value="author", required=false)
	final String author,
	
	@ApiParam(value = "The identifier(s) to match")
	@RequestParam(value="id", required=false)
	final Set<String> id,
	
	@ApiParam(value = "Affected component identifier to match")
	@RequestParam(value="affectedComponentId", required=false)
	final String affectedComponentId,
	
	@ApiParam(value = "Commit comment term to match")
	@RequestParam(value="comment", required=false)
	final String comment,
	
	@ApiParam(value = "One or more branch paths to match")
	@RequestParam(value="branch", required=false)
	final List<String> branch,
	
	@ApiParam(value = "Commit timestamp to match")
	@RequestParam(value="timestamp", required=false)
	final Long timestamp,
	
	@ApiParam(value = "Minimum commit timestamp to search matches from")
	@RequestParam(value="timestampFrom", required=false)
	final Long timestampFrom,
	
	@ApiParam(value = "Maximum commit timestamp to search matches to")
	@RequestParam(value="timestampTo", required=false)
	final Long timestampTo,
	
	@ApiParam(value = "Expansion parameters")
	@RequestParam(value="expand", required=false)
	final String expand,
	
	@ApiParam(value = "The search key to use for retrieving the next page of results")
	@RequestParam(value="searchAfter", required=false)
	final String searchAfter,
	
	@ApiParam(value = "Sort keys")
	@RequestParam(value="sort", required=false)
	final List<String> sort,
	
	@ApiParam(value = "The maximum number of items to return", defaultValue = "50")
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
