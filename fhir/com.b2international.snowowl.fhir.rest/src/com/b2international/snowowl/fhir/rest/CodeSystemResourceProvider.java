/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hl7.fhir.r5.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemSearchRequestBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.inject.Provider;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Component
public class CodeSystemResourceProvider implements IResourceProvider {

	private static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
	
	@Autowired
	private Provider<IEventBus> bus;
	
	@Override
	public Class<CodeSystem> getResourceType() {
		return CodeSystem.class;
	}

	@Read
	public CodeSystem read(
		@IdParam IdType id, 
		SummaryEnum _summary, 
		@Elements Set<String> _elements, 
		RequestDetails requestDetails
	) {
		final String locales = requestDetails.getHeader(HEADER_ACCEPT_LANGUAGE);
		
		try {
			
			return FhirRequests.codeSystems().prepareGet(id.getIdPart())
				.setSummary(_summary)
				.setElements(_elements != null ? ImmutableList.copyOf(_elements) : null)
				.setLocales(locales)
				.buildAsync()
				.execute(bus.get())
				.getSync();
			
		} catch (NotFoundException e) {
			return null;
		}
	}
	
	@Search
	public IBundleProvider search(
		@OptionalParam(name = CodeSystem.SP_RES_ID) StringOrListParam _id,
		@OptionalParam(name = CodeSystem.SP_NAME) StringOrListParam name,
		@OptionalParam(name = CodeSystem.SP_TITLE) StringParam title,
		@OptionalParam(name = Constants.PARAM_LASTUPDATED) DateParam _lastUpdated,
//		@OptionalParam(name = CodeSystem.SP_CONTENT_MODE) TokenParam _content,
		@OptionalParam(name = CodeSystem.SP_URL) UriOrListParam url,
		@OptionalParam(name = CodeSystem.SP_SYSTEM) UriOrListParam system,
		@OptionalParam(name = CodeSystem.SP_VERSION) StringOrListParam version,
		@OptionalParam(name = CodeSystem.SP_STATUS) StringParam status,
		@Count Integer _count,
		@Sort SortSpec _sort,
		SummaryEnum _summary, 
		@Elements Set<String> _elements,
		RequestDetails requestDetails
	) {
		final String locales = requestDetails.getHeader(HEADER_ACCEPT_LANGUAGE);
		
		if (_count == null) {
			_count = 10;
		}

		final String[] pageIdParams = requestDetails.getParameters().get(Constants.PARAM_PAGEID);
		final String _pageId;
		if (!CompareUtils.isEmpty(pageIdParams)) {
			_pageId = pageIdParams[0];
		} else {
			_pageId = null;
		}

		final String[] sortByFields;
		if (_sort != null) {
			sortByFields = Stream.iterate(_sort, s -> s.getChain() != null, s -> s.getChain())
				.map(spec -> getSortField(spec))
				.toArray(String[]::new);
		} else {
			sortByFields = null;
		}
		
		final FhirCodeSystemSearchRequestBuilder requestBuilder = FhirRequests.codeSystems().prepareSearch()
			.filterByIds(asSet(_id))
			.filterByNames(asSet(name))
			.filterByTitle(asNonNullValue(title))
	//		.filterByContent(_content())
			.filterByLastUpdated(asStringValue(_lastUpdated))
			// values defined in both url and system match the same field, compute intersection to simulate ES behavior here
			.filterByUrls(intersectionOf(url, system))
			.filterByVersions(asSet(version))
			.filterByStatus(asNonNullValue(status))
			// XXX: _summary=count may override the default _count=10 value, so order of method calls is important here
			.setLimit(_count)
			.setSummary(_summary)
			.addElements(_elements)
			.sortByFields(sortByFields)
			.setLocales(locales);
		
		return new SearchAfterBundleProvider(requestBuilder).fetchPage(_pageId, bus.get());
	}

	@Operation(name = "$lookup", idempotent = true)
	public Parameters lookupType(
		@OperationParam(name = "code") CodeType code,
		@OperationParam(name = "system") UriType system,
		@OperationParam(name = "version") String version,
		@OperationParam(name = "coding") Coding coding,
		@OperationParam(name = "date") DateTimeType date,
		@OperationParam(name = "displayLanguage") CodeType displayLanguage,
		@OperationParam(name = "property") List<TokenOrListParam> property
//		@OperationParam(name = "useSupplement") UriOrListParam useSupplement
	) {
	
		final LookupRequest lookupRequest = new LookupRequest();
		lookupRequest.setCode(code);
		lookupRequest.setSystem(system);
		lookupRequest.setVersion(version);
		lookupRequest.setCoding(coding);
		lookupRequest.setDate(date);
		lookupRequest.setDisplayLanguage(displayLanguage);
		lookupRequest.setPropertyCodes(getPropertyCodes(property));
		
		final LookupResult lookupResult = FhirRequests.codeSystems().prepareLookup()
			.setRequest(lookupRequest)
			.buildAsync()
			.execute(bus.get())
			.getSync();
		
		return lookupResult.toParameters();
	}

	@Operation(name = "$lookup", idempotent = true)
	public Parameters lookupInstance(
		@IdParam IdType id,
		@OperationParam(name = "code") CodeType code,
		@OperationParam(name = "coding") Coding coding,
		@OperationParam(name = "date") DateTimeType date,
		@OperationParam(name = "displayLanguage") CodeType displayLanguage,
		@OperationParam(name = "property") List<TokenOrListParam> property
//		@OperationParam(name = "useSupplement") UriOrListParam useSupplement
	) {
		return lookupType(code, new UriType(id.withResourceType(null).getValue()), null, coding, date, displayLanguage, property);
	}

	private static Set<String> getPropertyCodes(final List<TokenOrListParam> list) {
		if (list == null) {
			return null;
		} else {
			return list.stream()
				.flatMap(tolp -> tolp.getValuesAsQueryTokens().stream())
				.map(TokenParam::getValue)
				.collect(Collectors.toSet());
		}
	}

	private static String asStringValue(DateParam dateParam) {
		return (dateParam != null && !dateParam.isEmpty()) ? dateParam.getValueAsString() : null;
	}

	private static String asNonNullValue(StringParam stringParam) {
		return (stringParam != null && !stringParam.isEmpty()) ? stringParam.getValueNotNull() : null; 
	}

	private static String getSortField(SortSpec spec) {
		return SortOrderEnum.DESC.equals(spec.getOrder()) 
			? "-" + spec.getParamName() 
			: spec.getParamName();
	}

	private static Set<String> asSet(StringOrListParam stringOrList) {
		if (stringOrList == null) {
			return null;
		}
		
		return stringOrList.getValuesAsQueryTokens()
			.stream()
			.map(sp -> sp.getValueNotNull())
			.collect(Collectors.toSet());
	}
	
	private static Set<String> asSet(UriOrListParam uriOrList) {
		if (uriOrList == null) {
			return null;
		}

		return uriOrList.getValuesAsQueryTokens()
			.stream()
			.map(sp -> sp.getValueNotNull())
			.collect(Collectors.toSet());
	}
	
	private static Set<String> intersectionOf(UriOrListParam uriOrList1, UriOrListParam uriOrList2) {
		final Set<String> set1 = asSet(uriOrList1);
		final Set<String> set2 = asSet(uriOrList2);
		
		if (set2 == null) {
			return set1;
		} else if (set1 == null) {
			return set2;
		} else {
			return Sets.intersection(set1, set2);
		}
	}
}
