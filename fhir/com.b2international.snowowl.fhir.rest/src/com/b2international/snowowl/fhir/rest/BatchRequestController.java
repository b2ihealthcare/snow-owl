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
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * REST end-point for batch operations.
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

	@Override
	protected Class<Bundle> getModelClass() {
		return Bundle.class;
	}
	
}
