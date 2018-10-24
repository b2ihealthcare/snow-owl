/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.service;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.Match;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest.Builder;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.provider.IConceptMapApiProvider;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameters;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * A concept map defines a mapping from a set of concepts defined in a code system to one or more concepts defined in other code systems. 
 * Mappings are one way - from the source to the destination.
 *  
 * @see <a href="https://www.hl7.org/fhir/conceptmap.html">ConceptMap</a>
 * @see <a href="https://www.hl7.org/fhir/conceptmap-operations.html">ConceptMap</a>
 * @since 7.0
 */
@Api(value = "ConceptMap", description="FHIR ConceptMap Resource", tags = { "ConceptMap" })
@RestController //no need for method level @ResponseBody annotations
@RequestMapping(value="/ConceptMap", produces = { BaseFhirResourceRestService.APPLICATION_FHIR_JSON })
public class FhirConceptMapRestService extends BaseFhirResourceRestService<ConceptMap> {
	
	/**
	 * ConceptMaps
	 * @param request parameters
	 * @return bundle of concept maps
	 */
	@ApiOperation(
		value="Retrieve all concept maps",
		notes="Returns a collection of the supported concept maps.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public Bundle getConceptMaps(@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Multimap<String, String> multiMap = HashMultimap.create();
		parameters.keySet().forEach(k -> multiMap.putAll(k, parameters.get(k)));
		SearchRequestParameters requestParameters = new SearchRequestParameters(multiMap); 
		
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
		ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(FhirConceptMapRestService.class);
		String uri = linkBuilder.toUri().toString();
		
		Bundle.Builder builder = Bundle.builder(UUID.randomUUID().toString())
			.type(BundleType.SEARCHSET)
			.addLink(uri);
		
		int total = 0;
		for (IConceptMapApiProvider fhirProvider : IConceptMapApiProvider.Registry.getProviders()) {
			Collection<ConceptMap> conceptMaps = fhirProvider.getConceptMaps();
			for (ConceptMap conceptMap : conceptMaps) {
				applyResponseContentFilter(conceptMap, requestParameters);
				
				String resourceUrl = String.format("%s/%s", uri, conceptMap.getId().getIdValue());
				
				Entry entry = new Entry(new Uri(resourceUrl), conceptMap);
				builder.addEntry(entry);
				total++;
			}
		}
		return builder.total(total).build();
	}
	
	/**
	 * HTTP Get for retrieving a concept map by its concept map id
	 * @param conceptMapId
	 * @param request parameters
	 * @return @link {@link ConceptMap}
	 */
	@ApiOperation(
		response=ConceptMap.class,
		value="Retrieve the concept map by id",
		notes="Retrieves the concept map specified by its logical id.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Concept map not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{conceptMapId:**}", method=RequestMethod.GET)
	public MappingJacksonValue getConceptMap(@PathVariable("conceptMapId") String conceptMapId, 
			@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Multimap<String, String> multiMap = HashMultimap.create();
		parameters.keySet().forEach(k -> multiMap.putAll(k, parameters.get(k)));
		SearchRequestParameters requestParameters = new SearchRequestParameters(multiMap);
		
		LogicalId logicalId = LogicalId.fromIdString(conceptMapId);
		ConceptMap conceptMap = IConceptMapApiProvider.Registry
			.getConceptMapProvider(logicalId) 
			.getConceptMap(logicalId);

		return applyResponseContentFilter(conceptMap, requestParameters);
	}
	
	/**
	 * HTTP Get request to translate a code that belongs to a {@link ConceptMap} specified by its ID.
	 * @param conceptMapId
	 * @return translation of the code
	 */
	@ApiOperation(
		response=TranslateResult.class,
		value="Translate a code based on a specific Concept Map",
		notes="Translate a code from one value set to another, based on the existing value set and specific concept map.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Concept map not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{conceptMapId:**}/$translate", method=RequestMethod.GET)
	public Parameters.Fhir translate(
		@ApiParam(value="The id of the Concept Map to base the translation on") @PathVariable("conceptMapId") String conceptMapId,
		@ApiParam(value="The code to translate") @RequestParam(value="code") final String code,
		@ApiParam(value="The code system's uri") @RequestParam(value="system") final String system,
		@ApiParam(value="The code system's version") @RequestParam(value="version") final String version,
		@ApiParam(value="The source value set") @RequestParam(value="source") final Optional<String> source,
		@ApiParam(value="Value set in which a translation is sought") @RequestParam(value="target") final Optional<String> target,
		@ApiParam(value="Target code system") @RequestParam(value="targetsystem") final Optional<String> targetSystem,
		@ApiParam(value="If true, the mapping is reversed") @RequestParam(value="reverse") final Optional<Boolean> isReverse) {
		
		//validation is triggered by builder.build()
		Builder builder = TranslateRequest.builder()
			.code(code)
			.system(system)
			.version(version);
			
		if(source.isPresent()) {
			builder.source(source.get());
		}
		
		if(target.isPresent()) {
			builder.target(target.get());
		}
		
		if(targetSystem.isPresent()) {
			builder.targetSystem(targetSystem.get());
		}
		
		if(isReverse.isPresent()) {
			builder.isReverse(isReverse.get());
		}
		
		return toResponse(doTranslate(conceptMapId, builder.build()));
	}
	
	/**
	 * HTTP POST request to translate a code that belongs to a {@link ConceptMap} specified by its ID.
	 * @param conceptMapId
	 * @return translation of the code
	 */
	@ApiOperation(
		response=TranslateResult.class,
		value="Translate a code based on a specific Concept Map",
		notes="Translate a code from one value set to another, based on the existing value set and specific concept map.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Not found", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{conceptMapId:**}/$translate", method=RequestMethod.POST, consumes = BaseFhirResourceRestService.APPLICATION_FHIR_JSON)
	public Parameters.Fhir translate(
		@ApiParam(value="The id of the conceptMap to base the translation on") @PathVariable("conceptMapId") String conceptMapId,
		@ApiParam(name = "body", value = "The translate request parameters")
		@RequestBody Parameters.Fhir in) {
		
		//validation is triggered by builder.build()
		final TranslateRequest request = toRequest(in, TranslateRequest.class);
		TranslateResult result = doTranslate(conceptMapId, request);
		return toResponse(result);
	}

	/**
	 * HTTP Get request to translate a code that could belongs to any {@link ConceptMap} in the system.
	 * @return translation of the code
	 */
	@ApiOperation(
		response=TranslateResult.class,
		value="Translate a code",
		notes="Translate a code from one value set to another, based on the existing value set and concept maps resources, and/or other additional knowledge available to the server.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Concept map not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$translate", method=RequestMethod.GET)
	public Parameters.Fhir translate(
		@ApiParam(value="The code to translate") @RequestParam(value="code") final String code,
		@ApiParam(value="The code system's uri") @RequestParam(value="system") final String system,
		@ApiParam(value="The code system's version") @RequestParam(value="version") final String version,
		@ApiParam(value="The source value set") @RequestParam(value="source") final Optional<String> source,
		@ApiParam(value="Value set in which a translation is sought") @RequestParam(value="target") final Optional<String> target,
		@ApiParam(value="Target code system") @RequestParam(value="targetsystem") final Optional<String> targetSystem,
		@ApiParam(value="If true, the mapping is reversed") @RequestParam(value="reverse") final Optional<Boolean> isReverse) {
		
		//validation is triggered by builder.build()
		Builder builder = TranslateRequest.builder()
				.code(code)
				.system(system)
				.version(version);
				
			if(source.isPresent()) {
				builder.source(source.get());
			}
			
			if(target.isPresent()) {
				builder.target(target.get());
			}
			
			if(targetSystem.isPresent()) {
				builder.targetSystem(targetSystem.get());
			}
			
			if(isReverse.isPresent()) {
				builder.isReverse(isReverse.get());
			}
		
		return toResponse(doTranslate(builder.build()));
	}
	
	/**
	 * HTTP POST request to translate a code that belongs to any {@link ConceptMap} in the system.
	 * @param {@link TranslateRequest}
	 * @return translation of the code
	 */
	@ApiOperation(
		response=TranslateResult.class,
		value="Translate a code",
		notes="Translate a code from one value set to another, based on the existing value set and concept map resources.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Not found", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$translate", method=RequestMethod.POST, consumes = BaseFhirResourceRestService.APPLICATION_FHIR_JSON)
	public Parameters.Fhir translate(
			@ApiParam(name = "body", value = "The translate request parameters")
			@RequestBody Parameters.Fhir in) {
		
		//validation is triggered by builder.build()
		final TranslateRequest request = toRequest(in, TranslateRequest.class);
		TranslateResult result = doTranslate(request);
		return toResponse(result);
	}
	
	/*
	 * Specific Concept Map based translation
	 */
	private TranslateResult doTranslate(String conceptMapId, TranslateRequest translateRequest) {
		
		LogicalId logicalId = LogicalId.fromIdString(conceptMapId);
		IConceptMapApiProvider valueSetProvider = IConceptMapApiProvider.Registry.getConceptMapProvider(logicalId);
		TranslateResult translateResult = valueSetProvider.translate(logicalId, translateRequest);
		return translateResult;
	}
	
	/*
	 * Translation from ANY Concept Maps
	 */
	private TranslateResult doTranslate(TranslateRequest translateRequest) {
		
		TranslateResult.Builder resultBuilder = TranslateResult.builder();
		
		int totalMatch = 0;
		
		Collection<IConceptMapApiProvider> providers = IConceptMapApiProvider.Registry.getProviders();
		
		for (IConceptMapApiProvider provider : providers) {
			Collection<Match> matches = provider.translate(translateRequest);
			totalMatch = totalMatch + matches.size();
			resultBuilder.addMatches(matches);
		}
		
		return resultBuilder
			.message(totalMatch + " match(es).")
			.build();
	}
	
}
