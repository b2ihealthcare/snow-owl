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

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.CompareUtils;
import com.b2international.index.revision.Commit;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.commit.CommitInfoRestSearch;
import com.b2international.snowowl.core.rest.domain.ResourceSelectors;

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
	public Promise<Resources> searchByGet(@ParameterObject final ResourceRestSearch params) {
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
				.setFields(params.getField())
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
			@ParameterObject
			final CommitInfoRestSearch params) {
		final List<String> fields;
		if (!CompareUtils.isEmpty(params.getField())) {
			fields = params.getField();
		} else if (CompareUtils.isEmpty(params.getExpand())) {
			fields = CommitInfo.Fields.DEAFULT_FIELD_SELECTION;
		} else {
			fields = null;
		}
		
		Request<RepositoryContext, CommitInfos> req = RepositoryRequests
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
	
	@Operation(
			summary = "Retrieve a commit",
			description = "Returns a single commit entry from resource commits"
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@GetMapping(value = "/commits/{commitId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CommitInfo> get(
			@Parameter(description = "Commit ID to match")
			@PathVariable(value="commitId")
			final String commitId, 

			@ParameterObject
			final ResourceSelectors selectors) {

		Request<RepositoryContext, CommitInfo> req = RepositoryRequests.commitInfos()
				.prepareGetCommitInfo(commitId)
				.setExpand(selectors.getExpand())
				.setFields(selectors.getField())
				.build();

		return new ResourceRepositoryRequestBuilder<CommitInfo>() {

			@Override
			public Request<RepositoryContext, CommitInfo> build() {
				return req;
			}

		}
		.buildAsync()
		.execute(getBus());
	}
}
