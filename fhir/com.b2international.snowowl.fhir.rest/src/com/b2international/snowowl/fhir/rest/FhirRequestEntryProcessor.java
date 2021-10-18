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
import com.b2international.snowowl.fhir.core.model.RequestEntry;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * {@link FhirBatchRequestProcessor} to process GET requests in a batch
 * @since 8.0.0
 */
public class FhirRequestEntryProcessor extends FhirBatchRequestProcessor {

	private RequestEntry requestEntry;

	public FhirRequestEntryProcessor(ObjectMapper objectMapper, RequestEntry requestEntry, FhirBatchRequestController batchRequestController) {
		super(objectMapper, batchRequestController);
		this.requestEntry = requestEntry;
	}

	@Override
	public void doProcess(ArrayNode arrayNode, HttpServletRequest request) throws Exception {
		 

		BatchRequest batchRequest = requestEntry.getRequest();
		Code requestMethod = batchRequest.getMethod();
		
		if (!requestMethod.equals(HttpVerb.GET.getCode())) {
			createInvalidMethodResponse(arrayNode, requestMethod);
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
			
			
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, httpEntity, String.class);
		
		String json = response.getBody();
		
		ObjectNode resourceNode = (ObjectNode) objectMapper.readTree(json);
		
		addResponse(arrayNode, resourceNode, String.valueOf(response.getStatusCode().value()));
	}

}
