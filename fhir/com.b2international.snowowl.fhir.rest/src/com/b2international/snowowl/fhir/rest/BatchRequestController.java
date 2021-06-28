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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.HttpVerb;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.model.*;
import com.b2international.snowowl.fhir.core.model.Bundle.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * REST endpoint for batch operations.
 * @since 8.0.0
 */
@RestController
@RequestMapping(value="/", produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class BatchRequestController {
	
	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;
	
	@Autowired
	private HttpServletRequest servletRequest;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ServletContext servletContext;
	
	@RequestMapping(value="/", method=RequestMethod.POST, consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public Promise<Bundle> getBatchResponse(@RequestBody final Bundle bundle, 
			@RequestHeader HttpHeaders headers,
			ServletResponse response) throws JsonProcessingException {
		
		
		System.out.println("Bundle: " + bundle);
		Collection<Entry> entries = bundle.getEntry();
		Bundle responseBundle = Bundle.builder()
				.language("en")
				.type(BundleType.BATCH_RESPONSE)
				.build();
		
		ObjectNode rootNode = (ObjectNode) objectMapper.valueToTree(responseBundle);
		
		ArrayNode arrayNode = rootNode.putArray("entry");
		
		for (Entry entry : entries) {
			if (entry instanceof RequestEntry) {
				RequestEntry requestEntry = (RequestEntry) entry;
				System.out.println("Request: " + requestEntry.getRequest().getUrl());
				processRequestEntry(arrayNode, requestEntry, headers);
				//reponseBundleBuilder.addEntry(responseEntry);
				
			} else {
				
				//Currently only operation request entries are supported
				OperationOutcomeEntry ooEntry = OperationOutcomeEntry.builder()
						.operationOutcome(OperationOutcome.builder()
								.addIssue(Issue.builder()
										.code(IssueType.INVALID)
										.addExpression("Request in batch mode not supported.")
										.addLocation(entry.getFullUrl().getUriValue())
										.build())
								.build())
						.build();
				//reponseBundleBuilder.addEntry(ooEntry);
			}
		}
		
		return Promise.immediate(objectMapper.treeToValue(rootNode, Bundle.class));
	}

	private void processRequestEntry(ArrayNode arrayNode, RequestEntry requestEntry, HttpHeaders headers) throws JsonProcessingException {
		
		BatchRequest request = requestEntry.getRequest();
		Uri url = request.getUrl();
		System.out.println("Request: " + url);
		
		//localHeaders.put(key, value)
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		
		RestTemplate restTemplate = getRestTemplate();
		
		//Fhir requestResource = requestEntry.getRequestResource();
		//String jsonString = new ObjectMapper().writeValueAsString(requestResource);
		//HttpEntity<String> httpEntity = new HttpEntity<String>(jsonString, headers);
		
		Code requestMethod = request.getMethod();
		if (requestMethod.equals(HttpVerb.POST.getCode())) {
		
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/snowowl/fhir/CodeSystem", HttpMethod.GET, httpEntity, String.class);
			
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
			
			//Bundle bundle = restTemplate.getForObject("http://localhost:8080/snowowl/fhir/CodeSystem", Bundle.class);
			//Entry entry = bundle.getEntry().iterator().next();
			
		}
		
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
	
	private HttpHeaders getHttpHeaders() {
		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		
		HttpHeaders headers = new HttpHeaders();
		//UserDetails principal = (UserDetails) auth.getPrincipal();
		headers.setBasicAuth("x", "x");
		
		MediaType mediaType = MediaType.parseMediaType("application/fhir+json;charset=utf-8");
		headers.setContentType(mediaType);
		
		return headers;
	}

}
