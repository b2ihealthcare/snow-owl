package com.b2international.snowowl.fhir.rest;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.BatchRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.RequestEntry;
import com.b2international.snowowl.fhir.core.model.dt.Uri;

@RestController
@RequestMapping(value="/", produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class BatchRequestController {
	
	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;
	
	@RequestMapping(value="/", method=RequestMethod.POST, consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public Promise<Bundle> getBatchResponse(@RequestBody final Bundle bundle) {
		
		System.out.println("Bundle: " + bundle);
		Collection<Entry> entries = bundle.getEntry();
		
		for (Entry entry : entries) {
			if (entry instanceof RequestEntry) {
				RequestEntry requestEntry = (RequestEntry) entry;
				System.out.println("Request: " + requestEntry.getRequest().getUrl());
				Entry responseEntry = processRequestEntry(requestEntry);
			}
		}
		
		return Promise.immediate(Bundle.builder()
				.type(BundleType.BATCH_RESPONSE)
				.build());
	}

	private Entry processRequestEntry(RequestEntry requestEntry) {
		
		BatchRequest request = requestEntry.getRequest();
		Uri url = request.getUrl();
		System.out.println("Request: " + url);
		
		parseUrl(url);
		
		
		// TODO Auto-generated method stub
		return null;
	}

	private void parseUrl(Uri url) {
		
		String uriValue = url.getUriValue();
		boolean isGet = uriValue.contains("?");
		
		String[] split = uriValue.split("/");
		if (split.length < 2) {
			throw new BadRequestException("Invalid request '" + url.getUriValue() + "'.");
		}
		
		String resource = split[0];
		
		if (resource.equals("CodeSystem")) {
			System.out.println("Code system call");
			RestTemplate restTemplate = new RestTemplate();
			
			
			
			//WebClient.create("http://localhost:8080/snowowl");
			Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
			Set<RequestMappingInfo> keySet = handlerMethods.keySet();
			for (RequestMappingInfo requestMappingInfo : keySet) {
				System.out.println(requestMappingInfo);
				HandlerMethod handlerMethod = handlerMethods.get(requestMappingInfo);
				Method method = handlerMethod.getMethod();
				System.out.println(method);
			}
			try {
				RequestMatchResult match = requestMappingHandlerMapping.match(null, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
		
		// TODO Auto-generated method stub
		
		
	}

}
