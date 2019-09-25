/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.FhirResource;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SummaryParameterValue;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameters;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 6.4
 */
public abstract class BaseFhirResourceRestService<R extends FhirResource> extends AbstractRestService {

	//TODO: should this be grabbed from the server preferences or from the request?
	public static final String NHS_REALM_LANGUAGE_REFSET_ID = "999000671000001103";
	public static final ExtendedLocale NHS_REALM_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_LANGUAGE_REFSET_ID);

	public static final String NHS_REALM_CLINICAL_LANGUAGE_REFSET_ID = "999001261000000100";
	public static final ExtendedLocale NHS_REALM_CLINICAL_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_CLINICAL_LANGUAGE_REFSET_ID);
	
	public static final String NHS_REALM_PHARMACY_LANGUAGE_REFSET_ID = "999000691000001104";
	public static final ExtendedLocale NHS_REALM_PHARMACY_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_PHARMACY_LANGUAGE_REFSET_ID);
	public static final ExtendedLocale INT_LOCALE = ExtendedLocale.valueOf("en-us");
	
	protected List<ExtendedLocale> locales = ImmutableList.of(INT_LOCALE, NHS_REALM_LOCALE, NHS_REALM_CLINICAL_LOCALE, NHS_REALM_PHARMACY_LOCALE);
	
	public static final String APPLICATION_FHIR_JSON = "application/fhir+json;charset=utf-8";
	
	@Autowired
	protected ObjectMapper mapper;
	
	protected final <T> T toRequest(Parameters.Fhir in, Class<T> request) {
		return mapper.convertValue(in.toJson(), request);
	}
	
	protected final Parameters.Fhir toResponse(Object response) {
		return new Parameters.Fhir(Parameters.from(response));
	}
	
	/**
	 * Applies an empty filterprovider. It is required by Spring even if we don't want to filter the response.
	 */
	protected void applyEmptyContentFilter(FhirResource fhirResource) {
		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(fhirResource);
		mappingJacksonValue.setFilters(filterProvider);
		mapper.setFilterProvider(filterProvider);
	}
	
	protected MappingJacksonValue applyResponseContentFilter(FhirResource filteredFhirResource, SearchRequestParameters parameters) {

		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
		SummaryParameterValue summaryParameter = parameters.getSummary();
		Collection<String> elementsParameters = parameters.getElements();
		
		if (summaryParameter !=null) {
			filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(summaryParameter));
			filteredFhirResource.setSubsetted();
		} else if (elementsParameters != null) {
			filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(getRequestedFields(elementsParameters)));
			filteredFhirResource.setSubsetted();
		}
		
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredFhirResource);
		mappingJacksonValue.setFilters(filterProvider);
		mapper.setFilterProvider(filterProvider);
		
		return mappingJacksonValue;
	}
	
	protected int applySearchParameters(Bundle.Builder builder, String uri, Collection<R> fhirResources, SearchRequestParameters parameters) {
		
		Collection<FhirResource> filteredResources = Sets.newHashSet(fhirResources);
		int total = 0;
		
		for (FhirResource fhirResource : filteredResources) {
			applyResponseContentFilter(fhirResource, parameters);
			String resourceUrl = String.format("%s/%s", uri, fhirResource.getId().getIdValue());
			Entry entry = new Entry(new Uri(resourceUrl), fhirResource);
			builder.addEntry(entry);
			total++;
		}
		return total;
	}
	
	protected List<String> getRequestedFields(Collection<String> elements) {
		
		List<String> requestedParameters = Lists.newArrayList();
		for (String element : elements) {
			element = element.replaceAll(" ", "");
			if (element.contains(",")) {
				String requestedFields[] = element.split(",");
				requestedParameters.addAll(Lists.newArrayList(requestedFields));
			} else {
				if (!StringUtils.isEmpty(element)) {
					requestedParameters.add(element);
				}
			}
		}
		return requestedParameters;
	}
	
	// custom FHIR exception handling common to all FHIR resources, endpoints
	private static final Logger LOG = LoggerFactory.getLogger(BaseFhirResourceRestService.class);
	private static final String GENERIC_USER_MESSAGE = "Something went wrong during the processing of your request.";
	
	/**
	 * Generic <b>Internal Server Error</b> exception handler, serving as a fallback for RESTful client calls.
	 * 
	 * @param ex
	 * @return {@link OperationOutcome} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody OperationOutcome handle(final Exception ex) {
		LOG.error("Exception during processing of a request", ex);
		
		FhirException fhirException = FhirException.createFhirError(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage(), OperationOutcomeCode.MSG_BAD_SYNTAX);
		return fhirException.toOperationOutcome();
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody OperationOutcome handle(final BadRequestException ex) {
		return ex.toOperationOutcome();
	}

	
	/**
	 * Exception handler converting any {@link JsonMappingException} to an <em>HTTP 400</em>.
	 * 
	 * @param ex
	 * @return {@link OperationOutcome} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody OperationOutcome handle(HttpMessageNotReadableException ex) {
		LOG.error("Exception during processing of a JSON document", ex);
		
		FhirException fhirException = FhirException.createFhirError(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage(), OperationOutcomeCode.MSG_CANT_PARSE_CONTENT);
		return fhirException.toOperationOutcome();
	}

	/**
	 * <b>Not Found</b> exception handler. All {@link NotFoundException not found exception}s are mapped to {@link HttpStatus#NOT_FOUND
	 * <em>404 Not Found</em>} in case of the absence of an instance resource.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody OperationOutcome handle(final NotFoundException ex) {
		FhirException fhirException = FhirException.createFhirError(ex.getMessage(), OperationOutcomeCode.MSG_NO_EXIST, ex.getKey());
		return fhirException.toOperationOutcome();
	}

	/**
	 * Exception handler to return <b>Not Implemented</b> when an {@link UnsupportedOperationException} is thrown from the underlying system.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	public @ResponseBody OperationOutcome handle(NotImplementedException ex) {
		FhirException fhirException = FhirException.createFhirError(ex.getMessage(), OperationOutcomeCode.MSG_UNKNOWN_OPERATION);
		return fhirException.toOperationOutcome();
	}

	/**
	 * Exception handler to return <b>Bad Request</b> when an {@link BadRequestException} is thrown from the underlying system.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public @ResponseBody OperationOutcome handle(final ConflictException ex) {
		FhirException fhirException = FhirException.createFhirError(ex.getMessage(), OperationOutcomeCode.MSG_LOCAL_FAIL);
		return fhirException.toOperationOutcome();
	}
	
}
