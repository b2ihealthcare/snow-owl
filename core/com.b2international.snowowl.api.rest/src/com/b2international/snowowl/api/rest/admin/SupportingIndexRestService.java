/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.admin;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.b2international.snowowl.api.admin.ISupportingIndexService;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Spring controller for exposing {@link ISupportingIndexService} functionality.
 * 
 */
@Controller
@RequestMapping(value={"/supportingIndexes"}, produces={ MediaType.TEXT_PLAIN_VALUE })
@Api("Administration")
@ApiIgnore
public class SupportingIndexRestService extends AbstractAdminRestService {

	@Autowired
	protected ISupportingIndexService delegate;

	@RequestMapping(method=RequestMethod.GET)
	@ApiOperation(
			value="Retrieve all supporting index identifiers",
			notes="Retrieves a list of identifiers for indexes which are storing supplementary information "
					+ "(eg. task state, bookmarks, etc.)")
	public @ResponseBody String getSupportingIndexIds() {
		final List<String> indexIds = delegate.getSupportingIndexIds();
		return joinStrings(indexIds);
	}

	@RequestMapping(value="{indexId:**}/snapshots", method=RequestMethod.GET)
	@ApiOperation(
			value="Retrieve all snapshot identifiers for an index",
			notes="Retrieves a list of snapshot identifiers for the specified supporting index.")
	@ApiResponses({
		@ApiResponse(code=404, message="Supporting index not found")
	})
	public @ResponseBody String getSupportingIndexSnapshotIds(
			@PathVariable(value="indexId") 
			@ApiParam(value="the identifier of the supporting index service")
			final String indexId) {

		final List<String> snapshotIds = delegate.getSupportingIndexSnapshotIds(indexId);
		return joinStrings(snapshotIds);
	}

	@RequestMapping(value="{indexId:**}/snapshots", method=RequestMethod.POST)
	@ApiOperation(
			value="Create snapshot for an index",
			notes="Retrieves a list of snapshot identifiers for the specified supporting index.",
			response=String.class)
	@ApiResponses({
		@ApiResponse(code=404, message="Supporting index or snapshot not found"),
		@ApiResponse(code=500, message="Error while creating snapshot")
	})
	public ResponseEntity<String> createSupportingIndexSnapshot(
			@PathVariable(value="indexId") 
			@ApiParam(value="the identifier of the supporting index service")
			final String indexId) {

		final String snapshotId = delegate.createSupportingIndexSnapshot(indexId);
		final HttpHeaders headers = new HttpHeaders();
		headers.setLocation(linkTo(SupportingIndexRestService.class).slash(indexId).slash("snapshots").slash(snapshotId).toUri());
		return new ResponseEntity<String>(snapshotId, headers, HttpStatus.CREATED);
	}

	@RequestMapping(value="{indexId:**}/snapshots/{snapshotId}", method=RequestMethod.GET)
	@ApiOperation(
			value="List contents of an index snapshot",
			notes="Retrieves a list of snapshot identifiers for the specified supporting index.")
	@ApiResponses({
		@ApiResponse(code=404, message="Supporting index or snapshot not found"),
		@ApiResponse(code=500, message="Error while retrieving list of files")
	})
	public @ResponseBody String getSupportingIndexFiles(
			@PathVariable(value="indexId") final String indexId, 
			@PathVariable(value="snapshotId") final String snapshotId) {

		final List<String> indexFiles = delegate.getSupportingIndexFiles(indexId, snapshotId);
		return joinStrings(indexFiles);
	}

	@RequestMapping(value="{indexId:**}/snapshots/{snapshotId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(
			value="Release resources associated with an index snapshot",
			notes="Releases an existing, consistent snapshot of the specified supporting index.")
	@ApiResponses({
		@ApiResponse(code=204, message="Snapshot successfully released"),
		@ApiResponse(code=404, message="Supporting index or snapshot not found"),
		@ApiResponse(code=500, message="Error while releasing snapshot")
	})
	public void releaseSupportingIndexSnapshot(
			@PathVariable(value="indexId") final String indexId, 
			@PathVariable(value="snapshotId") final String snapshotId) {

		delegate.releaseSupportingIndexSnapshot(indexId, snapshotId);
	}
}