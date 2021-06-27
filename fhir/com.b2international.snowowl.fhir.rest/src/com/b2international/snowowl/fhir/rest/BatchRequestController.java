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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.HttpVerb;
import com.b2international.snowowl.fhir.core.model.BatchRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.RequestEntry;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private ObjectMapper objectMapper;
	
	@RequestMapping(value="/", method=RequestMethod.POST, consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public Promise<Bundle> getBatchResponse(@RequestBody final Bundle bundle, @RequestHeader HttpHeaders headers) throws JsonProcessingException {
		
		System.out.println("Bundle: " + bundle);
		Collection<Entry> entries = bundle.getEntry();
		
		for (Entry entry : entries) {
			if (entry instanceof RequestEntry) {
				RequestEntry requestEntry = (RequestEntry) entry;
				System.out.println("Request: " + requestEntry.getRequest().getUrl());
				Entry responseEntry = processRequestEntry(requestEntry, headers);
			}
		}
		
		return Promise.immediate(Bundle.builder()
				.type(BundleType.BATCH_RESPONSE)
				.build());
	}

	private Entry processRequestEntry(RequestEntry requestEntry, HttpHeaders headers2) throws JsonProcessingException {
		
		BatchRequest request = requestEntry.getRequest();
		Uri url = request.getUrl();
		System.out.println("Request: " + url);
		
		HttpHeaders headers = getHttpHeaders();
		HttpEntity<Bundle> httpEntity = new HttpEntity<>(headers2);
		
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

		restTemplate.setMessageConverters(messageConverters);
		
		ResponseEntity<Bundle> response = restTemplate.exchange("http://localhost:8080/snowowl/fhir/CodeSystem", HttpMethod.GET, httpEntity, Bundle.class);
		
		System.out.println("STATUS: " + response.getStatusCodeValue());
		System.out.println("Response id: " + response.getBody().getId());
		
		//Fhir requestResource = requestEntry.getRequestResource();
		//String jsonString = new ObjectMapper().writeValueAsString(requestResource);
		//HttpEntity<String> httpEntity = new HttpEntity<String>(jsonString, headers);
		
		String uriValue = request.getUrl().getUriValue();
		Code requestMethod = request.getMethod();
		if (requestMethod.equals(HttpVerb.GET.getCode())) {
		
			//RestTemplate restTemplate = new RestTemplate();
			Entry entry = restTemplate.getForObject(uriValue, Entry.class);
			
			return entry;
		} else {
			//RestTemplate restTemplate = new RestTemplate();
			//Entry entry = restTemplate.postForObject("http://localhost:8080/snowowl/fhir/" + uriValue, httpEntity , Entry.class);
			Bundle bundle = restTemplate.getForObject("http://localhost:8080/snowowl/fhir/CodeSystem", Bundle.class);
			Entry entry = bundle.getEntry().iterator().next();
			
			return entry;
		}
		
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

	private Entry handleRequest(final BatchRequest request) {
		
		
		
		return null;
		
//		String[] split = uriValue.split("/");
//		if (split.length < 2) {
//			throw new BadRequestException("Invalid request '" + uriValue + "'.");
//		}
//		
//		String resource = split[0];
//		
//		if (resource.equals("CodeSystem")) {
//			System.out.println("Code system call");
//			
//			
//			
//			//WebClient.create("http://localhost:8080/snowowl");
//			Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
//			Set<RequestMappingInfo> keySet = handlerMethods.keySet();
//			for (RequestMappingInfo requestMappingInfo : keySet) {
//				System.out.println(requestMappingInfo);
//				HandlerMethod handlerMethod = handlerMethods.get(requestMappingInfo);
//				Method method = handlerMethod.getMethod();
//				System.out.println(method);
//			}
//			try {
//				RequestMatchResult match = requestMappingHandlerMapping.match(null, null);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		
//		
//		
//		
//		// TODO Auto-generated method stub
		
		
	}

}
