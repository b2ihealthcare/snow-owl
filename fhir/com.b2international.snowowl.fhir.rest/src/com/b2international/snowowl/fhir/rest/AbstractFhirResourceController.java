/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;

import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.FhirResource;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.FhirFilterParameter;
import com.b2international.snowowl.fhir.core.search.FhirParameter.PrefixedValue;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;
import com.b2international.snowowl.fhir.core.search.FhirUriFilterParameterDefinition.FhirFilterParameterKey;
import com.b2international.snowowl.fhir.core.search.FhirUriFilterParameterDefinition.SummaryParameterValue;
import com.b2international.snowowl.fhir.core.search.FhirUriParameterManager;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.*;

/**
 * @since 6.4
 */
public abstract class AbstractFhirResourceController<R extends FhirResource> extends AbstractFhirController {

	//TODO: should this be grabbed from the server preferences or from the request?
	public static final String NHS_REALM_LANGUAGE_REFSET_ID = "999000671000001103";
	public static final ExtendedLocale NHS_REALM_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_LANGUAGE_REFSET_ID);

	public static final String NHS_REALM_CLINICAL_LANGUAGE_REFSET_ID = "999001261000000100";
	public static final ExtendedLocale NHS_REALM_CLINICAL_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_CLINICAL_LANGUAGE_REFSET_ID);
	
	public static final String NHS_REALM_PHARMACY_LANGUAGE_REFSET_ID = "999000691000001104";
	public static final ExtendedLocale NHS_REALM_PHARMACY_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_PHARMACY_LANGUAGE_REFSET_ID);
	public static final ExtendedLocale INT_LOCALE = ExtendedLocale.valueOf("en-us");
	
	protected List<ExtendedLocale> locales = ImmutableList.of(INT_LOCALE, NHS_REALM_LOCALE, NHS_REALM_CLINICAL_LOCALE, NHS_REALM_PHARMACY_LOCALE);
	
	//cache supported parameters
	private FhirUriParameterManager parameterManager = FhirUriParameterManager.createFor(getModelClass());
	
	protected abstract Class<R> getModelClass();
	
	protected Pair<Set<FhirFilterParameter>, Set<FhirSearchParameter>> processParameters(MultiValueMap<String, String> parameters) {
		
		//Convert it to Guava's multimap
		Multimap<String, String> multiMap = HashMultimap.create();
		parameters.keySet().forEach(k -> multiMap.putAll(k, parameters.get(k)));
		
		Pair<Set<FhirFilterParameter>, Set<FhirSearchParameter>> fhirParameters = parameterManager.processParameters(multiMap);
		return fhirParameters;
	}
	
	protected Optional<PrefixedValue> getParameterSingleValue(Set<FhirSearchParameter> requestParameters, String parameterName) {
		
		Optional<FhirSearchParameter> parameterOptional = requestParameters.stream().filter(p -> parameterName.equals(p.getName())).findAny();
		
		if (parameterOptional.isPresent()) {
			Collection<PrefixedValue> parameterValues = parameterOptional.get().getValues();
			if (parameterValues.size() != 1) {
				throw new IllegalArgumentException("There should only be a single value for '" + parameterName + "'");
			} else {
				return Optional.of(parameterValues.iterator().next());
			}
		} else {
			return Optional.empty();
		}
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
	
	protected MappingJacksonValue applyResponseContentFilter(FhirResource filteredFhirResource, Set<FhirFilterParameter> filterParameters) {

		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);

		Optional<FhirFilterParameter> summaryFilterParameterOptional = filterParameters.stream()
			.filter(p -> FhirFilterParameterKey._summary.name().equals(p.getName()))
			.findAny();
		
		Optional<FhirFilterParameter> elementsFilterParameterOptional = filterParameters.stream()
				.filter(p -> FhirFilterParameterKey._elements.name().equals(p.getName()))
				.findAny();
		
		if (summaryFilterParameterOptional.isPresent()) {
			FhirFilterParameter summaryParameter = summaryFilterParameterOptional.get();
			String paramValue = summaryParameter.getValues().iterator().next().getValue();
			filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(SummaryParameterValue.fromRequestParameter(paramValue)));
			filteredFhirResource.setSubsetted();
		} else if (elementsFilterParameterOptional.isPresent()) {
			FhirFilterParameter elementsParameter = elementsFilterParameterOptional.get();
			Collection<String> values = elementsParameter.getValues().stream().map(PrefixedValue::getValue).collect(Collectors.toSet());
			filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(getRequestedFields(values)));
			filteredFhirResource.setSubsetted();
		}
		
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredFhirResource);
		mappingJacksonValue.setFilters(filterProvider);
		mapper.setFilterProvider(filterProvider);
		
		return mappingJacksonValue;
	}
	
	//TODO: replace it with a query
	//protected int applySearchParameters(Bundle.Builder builder, String uri, Collection<R> fhirResources, SearchRequestParameters parameters) {
	protected int applyFilterParameters(Bundle.Builder builder, String uri, Collection<R> fhirResources, Set<FhirFilterParameter> filterParameters) {
		
		Collection<FhirResource> filteredResources = Sets.newHashSet(fhirResources);
		int total = 0;
		
		for (FhirResource fhirResource : filteredResources) {
			applyResponseContentFilter(fhirResource, filterParameters);
			String resourceUrl = String.join("/", uri, fhirResource.getId().getIdValue());
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
	
}
