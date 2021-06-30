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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.HttpVerb;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.model.*;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * REST end-point for batch operations.
 * @since 8.0.0
 */
@RestController
@RequestMapping(value="/", produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class BatchRequestController {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@RequestMapping(value="/", method=RequestMethod.POST, consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public Promise<Bundle> getBatchResponse(@RequestBody final Bundle bundle, HttpServletRequest request) throws JsonProcessingException {
		
		Collection<Entry> entries = bundle.getEntry();
		Bundle responseBundle = Bundle.builder()
				.language("en")
				.type(BundleType.BATCH_RESPONSE)
				.build();
		
		ObjectNode rootNode = (ObjectNode) objectMapper.valueToTree(responseBundle);
		
		ArrayNode arrayNode = rootNode.putArray("entry");
		
		for (Entry entry : entries) {
			if (entry instanceof ParametersRequestEntry) {
				ParametersRequestEntry requestEntry = (ParametersRequestEntry) entry;
				System.out.println("Request: " + requestEntry.getRequest().getUrl());
				
				try {
					processRequestEntry(arrayNode, requestEntry, request);
				} catch (HttpClientErrorException hcee) {
					processClientErrorException(arrayNode, hcee);
				} catch (HttpServerErrorException hsee) {
					processServerErrorException(arrayNode, hsee);
				} catch (Exception e) {
					processGenericError(arrayNode, e);
					System.out.println("Exception: " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				processNotSupportedError(arrayNode);
			}
		}
		
		return Promise.immediate(objectMapper.treeToValue(rootNode, Bundle.class));
	}

	private void processClientErrorException(ArrayNode arrayNode, HttpClientErrorException hcee) throws JsonMappingException, JsonProcessingException {
		
		//HttpClientErrorException returns an OperationOutcome in the response body
		ObjectNode resourceNode = (ObjectNode) objectMapper.readTree(hcee.getResponseBodyAsString());
		addToArrayNode(arrayNode, resourceNode, hcee);
	}

	private void processServerErrorException(ArrayNode arrayNode, HttpServerErrorException hsee) throws JsonMappingException, JsonProcessingException {
		
		//TODO: dig out the message from the error
		OperationOutcomeEntry ooEntry = OperationOutcomeEntry.builder()
				.operationOutcome(OperationOutcome.builder()
						.addIssue(Issue.builder()
								.code(IssueType.INVALID)
								.addExpression("Request in batch mode not supported.")
								//.addLocation(entry.getFullUrl().getUriValue())
								.build())
						.build())
				.build();
		
		ObjectNode resourceNode = (ObjectNode) objectMapper.valueToTree(ooEntry);
		addToArrayNode(arrayNode, resourceNode, hsee);
	}

	private void processGenericError(ArrayNode arrayNode, Exception e) {
		// TODO Auto-generated method stub
		
	}
	
	private void processNotSupportedError(ArrayNode arrayNode) {
		//Currently only operation request entries are supported
//		OperationOutcomeEntry ooEntry = OperationOutcomeEntry.builder()
//				.operationOutcome(OperationOutcome.builder()
//						.addIssue(Issue.builder()
//								.code(IssueType.INVALID)
//								.addExpression("Request in batch mode not supported.")
//								.addLocation(entry.getFullUrl().getUriValue())
//								.build())
//						.build())
//				.build();
		
		//bundle.getEntry().add(ooEntry);
		
	}
	
	private void addToArrayNode(ArrayNode arrayNode, ObjectNode resourceNode, HttpStatusCodeException statusCodeException) {
		
		ObjectNode resourceRoot = objectMapper.createObjectNode().putPOJO("resource", resourceNode);
		arrayNode.add(resourceRoot);
		
		BatchResponse batchResponse = new BatchResponse(String.valueOf(statusCodeException.getStatusCode().value()));
		JsonNode responseNode = objectMapper.valueToTree(batchResponse);
		ObjectNode responseRoot = objectMapper.createObjectNode().putPOJO("response", responseNode);
		arrayNode.add(responseRoot);
	}

	private void processRequestEntry(ArrayNode arrayNode, ParametersRequestEntry requestEntry, HttpServletRequest request) throws JsonProcessingException {
		
	    BatchRequest batchRequest = requestEntry.getRequest();
		
		HttpHeaders headers = getHeaders(request);
		
		RestTemplate restTemplate = getRestTemplate();
		
		StringBuilder uriBuilder = new StringBuilder(request.getScheme())
				.append("://")
				.append(request.getServerName())
				.append(":")
				.append(request.getLocalPort())
				.append(request.getRequestURI())
				.append(batchRequest.getUrl().getUriValue());
		
		Code requestMethod = batchRequest.getMethod();
		
		if (requestMethod.equals(HttpVerb.GET.getCode())) {
		
			HttpEntity<String> httpEntity = new HttpEntity<>(headers);
			System.out.println("URI: " + uriBuilder.toString());
			ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, httpEntity, String.class);
			
			String json = response.getBody();
			System.out.println("Body: " + json);
			
			ObjectNode resourceNode = (ObjectNode) objectMapper.readTree(json);
			ObjectNode resourceRoot = objectMapper.createObjectNode().putPOJO("resource", resourceNode);
			arrayNode.add(resourceRoot);
			
			BatchResponse batchResponse = BatchResponse.createOkResponse();
			JsonNode responseNode = objectMapper.valueToTree(batchResponse);
			ObjectNode responseRoot = objectMapper.createObjectNode().putPOJO("response", responseNode);
			arrayNode.add(responseRoot);
			
		} else if (requestMethod.equals(HttpVerb.POST.getCode())){
			
			System.out.println("URI: " + uriBuilder.toString());
			
			//TODO: change the RequestEntry to accommodate resources for non-operation bulk support
			HttpEntity httpEntity = new HttpEntity(requestEntry.getRequestResource(), headers);
			ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toString(), HttpMethod.POST, httpEntity, String.class);
			
			String json = response.getBody();
			System.out.println("Body: " + json);
		}
		
	}

	private HttpHeaders getHeaders(HttpServletRequest request) {
		
		HttpHeaders headers = new HttpHeaders();
		
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			
			String headerName = (String) headerNames.nextElement();
			
			Enumeration<String> headerValues = request.getHeaders(headerName);
			while (headerValues.hasMoreElements()) {
				String headerValue = (String) headerValues.nextElement();
				headers.add(headerName, headerValue);
				
			}
		};
		
		return headers;
	}

	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter() {

			public boolean canRead(java.lang.Class<?> clazz, org.springframework.http.MediaType mediaType) {
				return true;
			}

			public boolean canRead(java.lang.reflect.Type type, java.lang.Class<?> contextClass,
					org.springframework.http.MediaType mediaType) {
				return true;
			}

			protected boolean canRead(org.springframework.http.MediaType mediaType) {
				return true;
			}
		};

		jsonMessageConverter.setObjectMapper(objectMapper);
		messageConverters.add(jsonMessageConverter);

		//restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}
	
}
