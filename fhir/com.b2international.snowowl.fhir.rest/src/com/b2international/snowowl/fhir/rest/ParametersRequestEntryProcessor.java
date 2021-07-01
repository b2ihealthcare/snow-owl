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

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.b2international.snowowl.fhir.core.codesystems.HttpVerb;
import com.b2international.snowowl.fhir.core.model.BatchRequest;
import com.b2international.snowowl.fhir.core.model.ParametersRequestEntry;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * {@link BatchRequestProcessor} to process parameters-based POST requests in a batch
 * @ssince 8.0.0
 */
public class ParametersRequestEntryProcessor extends BatchRequestProcessor {

	private ParametersRequestEntry requestEntry;

	public ParametersRequestEntryProcessor(ObjectMapper objectMapper, ParametersRequestEntry requestEntry, BatchRequestController batchRequestController) {
		super(objectMapper, batchRequestController);
		this.requestEntry = requestEntry;
	}

	@Override
	public void doProcess(ArrayNode arrayNode, HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
		
		BatchRequest batchRequest = requestEntry.getRequest();
		Code requestMethod = batchRequest.getMethod();
		
		if (!requestMethod.equals(HttpVerb.POST.getCode())) {
			createInvalidMethodResponse(arrayNode);
			return;
		}
		
		HttpHeaders headers = getHeaders(request);
		
		RestTemplate restTemplate = getRestTemplate();
		
		StringBuilder uriBuilder = new StringBuilder(request.getScheme())
				.append("://")
				.append(request.getServerName())
				.append(":")
				.append(request.getLocalPort())
				.append(request.getRequestURI())
				.append(batchRequest.getUrl().getUriValue());
		
		System.out.println("URI: " + uriBuilder.toString());
			
		//TODO: change the RequestEntry to accommodate resources for non-operation bulk support
		HttpEntity<?> httpEntity = new HttpEntity<>(requestEntry.getRequestResource(), headers);
		ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toString(), HttpMethod.POST, httpEntity, String.class);
		
		String json = response.getBody();
		System.out.println("Body: " + json);
		
		ObjectNode resourceNode = (ObjectNode) objectMapper.readTree(json);
		
		addResponse(arrayNode, resourceNode, String.valueOf(response.getStatusCode().value()));
	}

}
