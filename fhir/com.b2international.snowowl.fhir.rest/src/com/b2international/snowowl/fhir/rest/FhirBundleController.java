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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST end-point for batch operations.
 * 
 * @since 8.0.0
 */
@Tag(name = "Bundle", description="Bundle Resource and batch operations")
@RestController
@RequestMapping(value="/", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
public class FhirBundleController extends AbstractFhirController {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Operation(
		summary = "Perform batch operations",
	    description = "Executes the FHIR requests included in the bundle provided.")
	@ApiResponses({
		@ApiResponse(responseCode = "200" , description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
	})
	@RequestMapping(value="/", method=RequestMethod.POST, consumes = AbstractFhirController.APPLICATION_FHIR_JSON)
	public Promise<Bundle> getBatchResponse(
			@Parameter(name = "bundle", description = "The bundle including the list of requests to perform")
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
			FhirBatchRequestProcessor requestProcessor = FhirBatchRequestProcessor.getInstance(entry, objectMapper, this);
			requestProcessor.process(arrayNode, request);
		}
		
		Bundle treeToValue = objectMapper.treeToValue(rootNode, Bundle.class);
		return Promise.immediate(treeToValue);
	}
	
	/**
	 * @param params
	 * @return bundle of bundles
	 */
	@Operation(
		summary="Retrieve all bundles in a bundle",
		description="Returns a collection of the bundles included in a bundle.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
	})
	@GetMapping("/Bundle")
	public Promise<Bundle> getBundles(@ParameterObject final FhirBundleSearchParameters params) {
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
	@Operation(
		summary="Retrieve a bundle by id",
		description="Retrieves a bundle specified by its logical id.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Bundle not found")
	})
	@RequestMapping(value="/Bundle/{id:**}", method=RequestMethod.GET)
	public Promise<Bundle> getBundle(
			@Parameter(name = "id", description = "The identifier of the bundle resource")
			@PathVariable(value = "id") 
			final String id,
			
			final FhirResourceSelectors selectors) {
		
		return FhirRequests.bundles().prepareGet(id)
			.buildAsync()
			.execute(getBus());
	}

}
