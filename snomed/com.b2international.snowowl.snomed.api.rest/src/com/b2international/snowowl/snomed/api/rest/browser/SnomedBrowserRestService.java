/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.b2international.snowowl.snomed.api.rest.browser;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.impl.domain.StorageRef;
import com.b2international.snowowl.snomed.api.browser.ISnomedBrowserService;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserChildConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConstant;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescriptionResult;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedRestService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("IHTSDO SNOMED CT Browser")
@Controller
@RequestMapping(
		value="/{version}", 
		produces={ SnomedBrowserRestService.IHTSDO_V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class SnomedBrowserRestService extends AbstractSnomedRestService {

	/**
	 * The currently supported versioned media type of the IHTSDO SNOMED CT Browser RESTful API.
	 */
	public static final String IHTSDO_V1_MEDIA_TYPE = "application/vnd.org.ihtsdo.browser-v1+json";

	@Autowired
	protected ISnomedBrowserService delegate;

	@ApiOperation(
			value="Retrieve single concept properties",
			notes="Retrieves a single concept and related information on a version.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(value="/concepts/{conceptId}", method=RequestMethod.GET)
	public @ResponseBody ISnomedBrowserConcept getSingleConcept(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {

		return getSingleConceptOnTask(version, null, conceptId, languageSetting, request);
	}

	@ApiOperation(
			value="Retrieve single concept properties on task",
			notes="Retrieves a single concept and related information on a task branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Code system version, task or concept not found")
	})
	@RequestMapping(value="/tasks/{taskId}/concepts/{conceptId}", method=RequestMethod.GET)
	public @ResponseBody ISnomedBrowserConcept getSingleConceptOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {

		final IComponentRef conceptRef = createComponentRef(version, taskId, conceptId);
		return delegate.getConceptDetails(conceptRef, Collections.list(request.getLocales()));
	}

	@ApiOperation(
			value = "Retrieve children of a concept",
			notes = "Returns a list of child concepts of the specified concept on a version.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(
			value="/concepts/{conceptId}/children",
			method = RequestMethod.GET)
	public @ResponseBody List<ISnomedBrowserChildConcept> getChildren(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {

		return getChildrenOnTask(version, null, conceptId, languageSetting, request);
	}

	@ApiOperation(
			value = "Retrieve descendants of a concept",
			notes = "Returns the list of child concepts of the specified concept on a task branch.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version, task or concept not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/concepts/{conceptId}/children",
			method = RequestMethod.GET)
	public @ResponseBody List<ISnomedBrowserChildConcept> getChildrenOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {

		final IComponentRef ref = createComponentRef(version, taskId, conceptId);
		return delegate.getConceptChildren(ref, Collections.list(request.getLocales()));
	}

	@ApiOperation(
			value = "Retrieve descriptions matching a query",
			notes = "Returns a list of descriptions which have a term matching the specified query string on a version.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(
			value="/descriptions",
			method = RequestMethod.GET)
	public @ResponseBody List<ISnomedBrowserDescriptionResult> searchDescriptions(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The query string")
			@RequestParam(value="query")
			final String query,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {

		return searchDescriptionsOnTask(version, null, query, offset, limit, languageSetting, request);
	}

	@ApiOperation(
			value = "Retrieve descendants of a concept",
			notes = "Returns the list of child concepts of the specified concept on a task branch.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version, task or concept not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/descriptions",
			method = RequestMethod.GET)
	public @ResponseBody List<ISnomedBrowserDescriptionResult> searchDescriptionsOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,

			@ApiParam(value="The query string")
			@RequestParam(value="query")
			final String query,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,
			
			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {

		final StorageRef ref = new StorageRef();
		ref.setShortName("SNOMEDCT");
		ref.setVersion(version);
		ref.setTaskId(taskId);

		return delegate.getDescriptions(ref, query, Collections.list(request.getLocales()), offset, limit);
	}

	@ApiOperation(
			value="Retrieve constants and properties",
			notes="Retrieves referenced constants and related concept properties from a version branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class)
	})
	@RequestMapping(value="/constants", method=RequestMethod.GET)
	public @ResponseBody Map<String, ISnomedBrowserConstant> getConstants(

			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {

		final StorageRef ref = new StorageRef();
		ref.setShortName("SNOMEDCT");
		ref.setVersion("MAIN");

		return getConstantsOnTask(version, null, languageSetting, request);
	}

	@ApiOperation(
			value="Retrieve constants and properties on task",
			notes="Retrieves referenced constants and related concept properties from a task branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class)
	})
	@RequestMapping(value="/tasks/{taskId}/constants", method=RequestMethod.GET)
	public @ResponseBody Map<String, ISnomedBrowserConstant> getConstantsOnTask(

			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {

		final StorageRef ref = new StorageRef();
		ref.setShortName("SNOMEDCT");
		ref.setVersion(version);
		ref.setTaskId(taskId);

		return delegate.getConstants(ref, Collections.list(request.getLocales()));
	}
}
