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
package com.b2international.snowowl.fhir.rest.r5;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.hl7.fhir.r5.model.*;
import org.springframework.stereotype.Component;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;
import com.b2international.snowowl.fhir.core.model.r5.FhirResourceConverter;
import com.b2international.snowowl.fhir.core.model.r5.FhirResourceInputConverter;
import com.b2international.snowowl.fhir.core.model.r5.FhirTranslateConverter;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateResult;
import com.b2international.snowowl.fhir.core.request.conceptmap.FhirConceptMapSearchRequestBuilder;
import com.google.common.collect.ImmutableList;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringOrListParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.UriOrListParam;

@Component
public class ConceptMapResourceProvider extends AbstractResourceProvider<ConceptMap> {

	public ConceptMapResourceProvider() {
		super(ConceptMap.class);
	}
	
	@Create
	public MethodOutcome create(
		@ResourceParam ConceptMap conceptMap, 
		RequestDetails requestDetails
	) {
		// Ignore the input identifier on purpose and assign one locally
		final String generatedId = IDs.base62UUID();
		conceptMap.setId(generatedId);
		
		createOrUpdate(conceptMap, requestDetails);
		return new MethodOutcome(conceptMap.getIdElement());
	}

	@Update
	public MethodOutcome update(
		@IdParam IdType id,
		@ResourceParam ConceptMap conceptMap, 
		RequestDetails requestDetails
	) {
		final String idInResource = conceptMap.getId();
		final String idInParameter = id.getValue();
		
		if (idInResource == null) {
			throw new BadRequestException("Code system resource did not contain an ID element.");
		}
		
		if (!Objects.equals(idInResource, idInParameter)) {
			throw new BadRequestException("Code system resource ID '" + idInResource + "' disagrees with '" + idInParameter + "' provided in the request URL.");
		}
		
		final boolean created = FhirResourceUpdateResult.Action.CREATED.equals(createOrUpdate(conceptMap, requestDetails).getAction());
		return new MethodOutcome(conceptMap.getIdElement()).setCreated(created);
	}
	
	private FhirResourceUpdateResult createOrUpdate(ConceptMap conceptMap, RequestDetails requestDetails) {
		String author = requestDetails.getHeader(AbstractRestService.X_AUTHOR);
		String owner = requestDetails.getHeader(HEADER_X_OWNER);
		String ownerProfileName = requestDetails.getHeader(HEADER_X_OWNER_PROFILE_NAME);
		String bundleId = requestDetails.getHeader(HEADER_X_BUNDLE_ID);
		
		String effectiveDateHeader = requestDetails.getHeader(HEADER_X_EFFECTIVE_DATE);
		LocalDate defaultEffectiveDate = !StringUtils.isEmpty(effectiveDateHeader)
			? LocalDate.parse(effectiveDateHeader)
			: null;
		
		final var coreConceptMap = FhirResourceInputConverter.toConceptMap(conceptMap);
		
		return FhirRequests.conceptMaps()
			.prepareUpdate()
			.setFhirConceptMap(coreConceptMap)
			.setAuthor(author)
			.setOwner(owner)
			.setOwnerProfileName(ownerProfileName)
			.setBundleId(bundleId)
			.setDefaultEffectiveDate(defaultEffectiveDate)
			.buildAsync()
			.execute(bus.get())
			.getSync();
	}
	
	@Read
	public ConceptMap getById(
		@IdParam IdType id, 
		SummaryEnum _summary, 
		@Elements Set<String> _elements, 
		RequestDetails requestDetails
	) {
		final String locales = requestDetails.getHeader(HEADER_ACCEPT_LANGUAGE);
		
		try {
			
			final String idOrUrl = id.getValue();
			final var conceptMap = FhirRequests.conceptMaps().prepareGet(idOrUrl)
				.setSummary(_summary != null ? _summary.getCode() : null)
				.setElements(_elements != null ? ImmutableList.copyOf(_elements) : null)
				.setLocales(locales)
				.buildAsync()
				.execute(bus.get())
				.getSync();
			
			return FhirResourceConverter.toFhirConceptMap(conceptMap);
			
		} catch (NotFoundException e) {
			return null;
		}
	}
	
	@Search
	public IBundleProvider search(
		@OptionalParam(name = ConceptMap.SP_RES_ID) StringOrListParam _id,
		@OptionalParam(name = ConceptMap.SP_NAME) StringOrListParam name,
		@OptionalParam(name = ConceptMap.SP_TITLE) StringParam title,
		@OptionalParam(name = Constants.PARAM_LASTUPDATED) DateParam _lastUpdated,
		@OptionalParam(name = ConceptMap.SP_URL) UriOrListParam url,
		@OptionalParam(name = ConceptMap.SP_VERSION) StringOrListParam version,
//		@OptionalParam(name = ConceptMap.SP_STATUS) StringParam status,
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
		
		final FhirConceptMapSearchRequestBuilder requestBuilder = FhirRequests.conceptMaps().prepareSearch()
			.filterByIds(asSet(_id))
			.filterByNames(asSet(name))
			.filterByTitle(asString(title))
			.filterByLastUpdated(asString(_lastUpdated))
			.filterByUrls(asSet(url))
			.filterByVersions(asSet(version))
//			.filterByStatus(asNonNullValue(status))
			// XXX: _summary=count may override the default _count=10 value, so order of method calls is important here
			.setLimit(_count)
			.setSummary(_summary != null ? _summary.getCode() : null)
			.setElements(_elements)
			.sortByFields(sortByFields)
			.setLocales(locales);
		
		return new SearchAfterBundleProvider(requestBuilder).fetchPage(_pageId, bus.get());
	}

//	@Delete
//	public void delete(
//		@IdParam IdType id,
//		RequestDetails requestDetails
//	) {
//		String author = requestDetails.getHeader(AbstractRestService.X_AUTHOR);
//		Boolean force = isForceDelete(requestDetails);
//		
//		try {
//			
//			FhirRequests.conceptMaps().prepareDelete(id.getValue())
//				.force(force)
//				.build(author, String.format("Deleting concept map %s", id))
//				.execute(bus.get())
//				.getSync();
//			
//		} catch (NotFoundException e) {
//			throw new ResourceNotFoundException(id);
//		} catch (Exception e) {
//			throw new ResourceVersionConflictException(e.getMessage());
//		}
//	}
	
	@Operation(name = "$translate", idempotent = true)
	public Parameters translateType(
		@OperationParam(name = "url") UriType url,
//		@OperationParam(name = "conceptMap") ConceptMap conceptMap,		
		@OperationParam(name = "conceptMapVersion") String conceptMapVersion,
		
		@OperationParam(name = "system") UriType system,
		@OperationParam(name = "version") String version,
		
		@OperationParam(name = "sourceCode") CodeType sourceCode,
//		@OperationParam(name = "sourceScope") UriType sourceScope,
//		@OperationParam(name = "sourceCoding") Coding sourceCoding,
//		@OperationParam(name = "sourceCodeableConcept") CodeableConcept sourceCodeableConcept,
		
		@OperationParam(name = "targetSystem") UriType targetSystem,
		
		@OperationParam(name = "targetCode") CodeType targetCode
//		@OperationParam(name = "targetScope") UriType targetScope,
//		@OperationParam(name = "targetCoding") Coding targetCoding,
//		@OperationParam(name = "targetCodeableConcept") CodeableConcept targetCodeableConcept
		
		// TODO: dependency input parameters?
	) {
		final var translateRequestBuilder = com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest.builder();
		
		if (url != null) {
			translateRequestBuilder.url(url.getValue());
		}
		
		translateRequestBuilder.conceptMapVersion(conceptMapVersion);
		
		if (sourceCode != null) {
			translateRequestBuilder.code(sourceCode.getCode());
		}
		
		if (system != null) {
			translateRequestBuilder.system(system.getValue());
		}
		
		translateRequestBuilder.version(version);
		
		if (targetCode != null) {
			translateRequestBuilder.target(targetCode.getValue());
		}
		
		if (targetSystem != null) {
			translateRequestBuilder.targetSystem(targetSystem.getValue());
		}
		
		// translateRequestBuilder.codeableConcept(...)
		// translateRequestBuilder.coding(...)
		// translateRequestBuilder.conceptMap(...)
		// translateRequestBuilder.dependencies(...)
		// translateRequestBuilder.isReverse(...) -- reverse mapping is not supported in R5
		// translateRequestBuilder.source(...) -- source value sets are not supported in R5
		
		final TranslateResult translateResult = FhirRequests.conceptMaps().prepareTranslate()
			.setRequest(translateRequestBuilder.build())
			.buildAsync()
			.execute(bus.get())
			.getSync();
		
		return FhirTranslateConverter.toParameters(translateResult);
	}

	@Operation(name = "$translate", idempotent = true)
	public Parameters translateInstance(
		@IdParam IdType id,
		
		@OperationParam(name = "system") UriType system,
		@OperationParam(name = "version") String version,
		
		@OperationParam(name = "sourceCode") CodeType sourceCode,
//		@OperationParam(name = "sourceScope") UriType sourceScope,
//		@OperationParam(name = "sourceCoding") Coding sourceCoding,
//		@OperationParam(name = "sourceCodeableConcept") CodeableConcept sourceCodeableConcept,
		
		@OperationParam(name = "targetSystem") UriType targetSystem,
		
		@OperationParam(name = "targetCode") CodeType targetCode
//		@OperationParam(name = "targetScope") UriType targetScope,
//		@OperationParam(name = "targetCoding") Coding targetCoding,
//		@OperationParam(name = "targetCodeableConcept") CodeableConcept targetCodeableConcept
	) {
		return translateType(
			new UriType(id.withResourceType(null).getValue()), 
			null, 
			system,
			version,
			sourceCode,
			targetSystem,
			targetCode);
	}
}
