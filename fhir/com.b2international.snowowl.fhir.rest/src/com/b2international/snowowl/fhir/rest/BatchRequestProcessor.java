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
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Class to process a bulk request
 * @since 8.0.0
 */
public abstract class BatchRequestProcessor {
	
	protected ObjectMapper objectMapper;
	private BatchRequestController batchRequestController;

	public BatchRequestProcessor(ObjectMapper objectMapper, BatchRequestController batchRequestController) {
		this.objectMapper = objectMapper;
		this.batchRequestController = batchRequestController;
	}

	public static BatchRequestProcessor getInstance(Entry entry, ObjectMapper objectMapper, BatchRequestController batchRequestController) {
		
		if (entry instanceof RequestEntry) {
			RequestEntry requestEntry = (RequestEntry) entry;
			return new RequestEntryProcessor(objectMapper, requestEntry, batchRequestController);
		} else if (entry instanceof ResourceRequestEntry) {
			ResourceRequestEntry requestEntry = (ResourceRequestEntry) entry;
			return new ResourceRequestEntryProcessor(objectMapper, requestEntry, batchRequestController);
		} else if (entry instanceof ParametersRequestEntry) {
			ParametersRequestEntry requestEntry = (ParametersRequestEntry) entry;
			return new ParametersRequestEntryProcessor(objectMapper, requestEntry, batchRequestController);
		} else {
			return new UnknowRequestEntryProcessor(objectMapper, entry, batchRequestController);
		}
		
	}

	public void process(ArrayNode arrayNode, HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
		
		try {
			doProcess(arrayNode, request);
		} catch (HttpClientErrorException hcee) {
			processClientErrorException(arrayNode, hcee);
			System.out.println("ArrayNode: " + arrayNode);
		} catch (HttpServerErrorException hsee) {
			processHttpException(arrayNode, hsee);
		} catch (JsonProcessingException e) {
			System.out.println("JSON Processing!");
			//e.printStackTrace();
		} catch (Exception e) {
			processGenericError(arrayNode, e);
			System.out.println("Exception: " + e.getMessage());
			//e.printStackTrace();
		}
	}
	
	/**
	 * Process the entry and insert the result to the {@link ArrayNode} of the response
	 * @param arrayNode
	 * @param request
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws Exception 
	 */
	public abstract void doProcess(ArrayNode arrayNode, HttpServletRequest request) throws Exception;
	
	protected void createInvalidMethodResponse(ArrayNode arrayNode) {
		System.out.println("Invalid method!");
		// TODO Auto-generated method stub
		
	}
	
	protected HttpHeaders getHeaders(HttpServletRequest request) {
		
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
	
	protected RestTemplate getRestTemplate() {
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
	
	private void processClientErrorException(ArrayNode arrayNode, HttpClientErrorException hcee) throws JsonMappingException, JsonProcessingException {
		
		//HttpClientErrorException can return an OperationOutcome in the response body
		ObjectNode resourceNode = (ObjectNode) objectMapper.readTree(hcee.getResponseBodyAsString());
		
		TreeNode resourceTypeNode = resourceNode.path("resource").path("resourceType");
		if (resourceTypeNode instanceof TextNode) {
			TextNode textNode = (TextNode) resourceTypeNode;
			if (textNode.textValue().equals("OperationOutcome")) {
				addResponse(arrayNode, resourceNode, String.valueOf(hcee.getStatusCode().value()));
				return;
				//return objectMapper.treeToValue(objectMapper, OperationOutcomeEntry.class);
			}
		}
		processHttpException(arrayNode, hcee);
		
	}

	private void processHttpException(ArrayNode arrayNode, HttpStatusCodeException hsee) throws JsonMappingException, JsonProcessingException {
		
		OperationOutcome operationOutcome = batchRequestController.handle(hsee);
		
		OperationOutcomeEntry ooEntry = OperationOutcomeEntry.builder()
				.operationOutcome(operationOutcome)
				.build();
		
		ObjectNode resourceNode = (ObjectNode) objectMapper.valueToTree(ooEntry);
		BatchResponse batchResponse = new BatchResponse(String.valueOf(hsee.getStatusCode().value()));
		JsonNode responseNode = objectMapper.valueToTree(batchResponse);
		resourceNode.putPOJO("response", responseNode);
		arrayNode.add(resourceNode);
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
	
	protected void addResponse(ArrayNode arrayNode, ObjectNode resourceNode, String statusCode) {
		
		ObjectNode resourceRoot = objectMapper.createObjectNode().putPOJO("resource", resourceNode);
		
		BatchResponse batchResponse = new BatchResponse(statusCode);
		JsonNode responseNode = objectMapper.valueToTree(batchResponse);
		resourceRoot.putPOJO("response", responseNode);
		arrayNode.add(resourceRoot);
	}
	
}
