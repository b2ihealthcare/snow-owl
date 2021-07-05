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
package com.b2international.snowowl.fhir.rest;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.annotations.*;

/**
 * REST end-point for batch operations.
 * 
 * @since 8.0.0
 */
@Api(value = "Bundle", description="Bundle Resource and batch operations", tags = { "Bundle" })
@RestController
@RequestMapping(value="/", produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class BatchRequestController extends AbstractFhirResourceController<Bundle> {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@ApiOperation(
			value="Perform batch operations",
			notes="Executes the FHIR requests included in the bundle provided.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad Request", response = OperationOutcome.class),
	})
	@RequestMapping(value="/", method=RequestMethod.POST, consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public Promise<Bundle> getBatchResponse(
			@ApiParam(name = "bundle", value = "The bundle including the list of requests to perform")
			@RequestBody final Bundle bundle, 
			HttpServletRequest request) throws JsonProcessingException {
		
		Collection<Entry> entries = bundle.getEntry();
		
		Bundle responseBundle = Bundle.builder()
				.language("en")
				.type(BundleType.BATCH_RESPONSE)
				.build();
		
		ObjectNode rootNode = (ObjectNode) objectMapper.valueToTree(responseBundle);
		
		ArrayNode arrayNode = rootNode.putArray("entry");
		
		for (Entry entry : entries) {
			BatchRequestProcessor requestProcessor = BatchRequestProcessor.getInstance(entry, objectMapper, this);
			requestProcessor.process(arrayNode, request);
		}
		
		Bundle treeToValue = objectMapper.treeToValue(rootNode, Bundle.class);
		return Promise.immediate(treeToValue);
	}
	
	/**
	 * Bundles
	 * @return bundle of bundles
	 */
	@ApiOperation(
			value="Retrieve all bundles in a bundle",
			notes="Returns a collection of the bundles included in a bundle.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad Request", response = OperationOutcome.class),
	})
	@GetMapping("/bundle")
	public Promise<Bundle> getBundles(final FhirBundleSearchParameters params) {
		
		return FhirRequests.bundles().prepareSearch()
				.filterByIds(asList(params.get_id()))
				
				//TODO: additional supported filters come here
				.buildAsync()
				.execute(getBus());
		
	}
	
	/**
	 * HTTP Get for retrieving a bundle by its id
	 * @param id
	 * @return
	 */
	@ApiOperation(
			response=Bundle.class,
			value="Retrieve a bundle by id",
			notes="Retrieves a bundle specified by its logical id.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Bundle not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/bundle/{id:**}", method=RequestMethod.GET)
	public Promise<Bundle> getBundle(
			@ApiParam(value = "The identifier of the bundle resource")
			@PathVariable(value = "id") 
			final String id,
			
			final FhirResourceSelectors selectors) {
		
		return FhirRequests.bundles().prepareGet(id)
			.buildAsync()
			.execute(getBus());
	}

	@Override
	protected Class<Bundle> getModelClass() {
		return Bundle.class;
	}
	
}
