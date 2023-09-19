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
import com.b2international.snowowl.fhir.core.model.r5.*;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateResult;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemSearchRequestBuilder;
import com.google.common.collect.ImmutableList;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.ResourceVersionConflictException;

@Component
public class CodeSystemResourceProvider extends AbstractResourceProvider<CodeSystem> {

	public CodeSystemResourceProvider() {
		super(CodeSystem.class);
	}
	
	@Create
	public MethodOutcome create(
		@ResourceParam CodeSystem codeSystem, 
		RequestDetails requestDetails
	) {
		// Ignore the input identifier on purpose and assign one locally
		final String generatedId = IDs.base62UUID();
		codeSystem.setId(generatedId);
		
		createOrUpdate(codeSystem, requestDetails);
		return new MethodOutcome(codeSystem.getIdElement());
	}

	@Update
	public MethodOutcome update(
		@IdParam IdType id,
		@ResourceParam CodeSystem codeSystem, 
		RequestDetails requestDetails
	) {
		final String idInResource = codeSystem.getId();
		final String idInParameter = id.getValue();
		
		if (idInResource == null) {
			throw new BadRequestException("Code system resource did not contain an ID element.");
		}
		
		if (!Objects.equals(idInResource, idInParameter)) {
			throw new BadRequestException("Code system resource ID '" + idInResource + "' disagrees with '" + idInParameter + "' provided in the request URL.");
		}
		
		final boolean created = FhirResourceUpdateResult.Action.CREATED.equals(createOrUpdate(codeSystem, requestDetails).getAction());
		return new MethodOutcome(codeSystem.getIdElement()).setCreated(created);
	}
	
	private FhirResourceUpdateResult createOrUpdate(CodeSystem codeSystem, RequestDetails requestDetails) {
		String author = requestDetails.getHeader(AbstractRestService.X_AUTHOR);
		String owner = requestDetails.getHeader(HEADER_X_OWNER);
		String ownerProfileName = requestDetails.getHeader(HEADER_X_OWNER_PROFILE_NAME);
		String bundleId = requestDetails.getHeader(HEADER_X_BUNDLE_ID);
		
		String effectiveDateHeader = requestDetails.getHeader(HEADER_X_EFFECTIVE_DATE);
		LocalDate defaultEffectiveDate = !StringUtils.isEmpty(effectiveDateHeader)
			? LocalDate.parse(effectiveDateHeader)
			: null;
		
		final var coreCodeSystem = FhirResourceInputConverter.toCodeSystem(codeSystem);
		
		return FhirRequests.codeSystems()
			.prepareUpdate()
			.setFhirCodeSystem(coreCodeSystem)
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
	public CodeSystem getById(
		@IdParam IdType id, 
		SummaryEnum _summary, 
		@Elements Set<String> _elements, 
		RequestDetails requestDetails
	) {
		final String locales = requestDetails.getHeader(HEADER_ACCEPT_LANGUAGE);
		
		try {
			
			final String idOrUrl = id.getValue();
			final var codeSystem = FhirRequests.codeSystems().prepareGet(idOrUrl)
				.setSummary(_summary != null ? _summary.getCode() : null)
				.setElements(_elements != null ? ImmutableList.copyOf(_elements) : null)
				.setLocales(locales)
				.buildAsync()
				.execute(bus.get())
				.getSync();
			
			return FhirResourceConverter.toFhirCodeSystem(codeSystem);
			
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
//		@OptionalParam(name = CodeSystem.SP_STATUS) StringParam status,
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
		
		final FhirCodeSystemSearchRequestBuilder requestBuilder = FhirRequests.codeSystems()
			.prepareSearch()
			.filterByIds(asSet(_id))
			.filterByNames(asSet(name))
			.filterByTitle(asString(title))
//			.filterByContent(_content())
			.filterByLastUpdated(asString(_lastUpdated))
			// values defined in both url and system match the same field, compute intersection to simulate ES behavior here
			.filterByUrls(intersectionOf(url, system))
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

	@Delete
	public void delete(
		@IdParam IdType id,
		RequestDetails requestDetails
	) {
		final String author = requestDetails.getHeader(AbstractRestService.X_AUTHOR);
		final boolean force = isForceDelete(requestDetails);
		
		try {
			
			FhirRequests.codeSystems().prepareDelete(id.getValue())
				.force(force)
				.build(author, String.format("Deleting code system %s", id))
				.execute(bus.get())
				.getSync();
			
		} catch (NotFoundException e) {
			throw new ResourceNotFoundException(id);
		} catch (Exception e) {
			throw new ResourceVersionConflictException(e.getMessage());
		}
	}
	
	@Operation(name = "$lookup", idempotent = true)
	public Parameters lookupType(
		@OperationParam(name = "code") CodeType code,
		@OperationParam(name = "system") UriType system,
		@OperationParam(name = "version") String version,
		@OperationParam(name = "coding") Coding coding,
		@OperationParam(name = "date") DateTimeType date,
		@OperationParam(name = "displayLanguage") CodeType displayLanguage,
		@OperationParam(name = "property") TokenOrListParam property
//		@OperationParam(name = "useSupplement") UriOrListParam useSupplement
	) {
		final var lookupRequestBuilder = com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest.builder();
		
		if (code != null) {
			lookupRequestBuilder.code(code.getCode());
		}
		
		if (system != null) {
			lookupRequestBuilder.system(system.getValue());
		}
		
		lookupRequestBuilder.version(version);
		
		if (coding != null) {
			lookupRequestBuilder.coding(asCoding(coding));
		}
		
		if (date != null) {
			lookupRequestBuilder.date(date.getValueAsString());
		}

		if (displayLanguage != null) {
			lookupRequestBuilder.displayLanguage(displayLanguage.getCode());
		}

		if (property != null && property.size() > 0) {
			lookupRequestBuilder.properties(getUniqueOrListTokens(property));
		}
		
		final var lookupResult = FhirRequests.codeSystems().prepareLookup()
			.setRequest(lookupRequestBuilder.build())
			.buildAsync()
			.execute(bus.get())
			.getSync();
		
		return FhirLookupConverter.toParameters(lookupResult);
	}

	@Operation(name = "$lookup", idempotent = true)
	public Parameters lookupInstance(
		@IdParam IdType id,
		@OperationParam(name = "code") CodeType code,
		@OperationParam(name = "coding") Coding coding,
		@OperationParam(name = "date") DateTimeType date,
		@OperationParam(name = "displayLanguage") CodeType displayLanguage,
		@OperationParam(name = "property") TokenOrListParam property
//		@OperationParam(name = "useSupplement") UriOrListParam useSupplement
	) {
		return lookupType(
			code, 
			new UriType(id.withResourceType(null).getValue()), 
			null, 
			coding, 
			date, 
			displayLanguage, 
			property);
	}
	
	@Operation(name = "$subsumes", idempotent = true)
	public Parameters subsumesType(
		@OperationParam(name = "codeA") CodeType codeA,
		@OperationParam(name = "codeB") CodeType codeB,
		@OperationParam(name = "system") UriType system,
		@OperationParam(name = "version") String version,
		@OperationParam(name = "codingA") Coding codingA,
		@OperationParam(name = "codingB") Coding codingB
	) {
		final var subsumptionRequestBuilder = com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionRequest.builder();
		
		if (codeA != null) {
			subsumptionRequestBuilder.codeA(codeA.getCode());
		}
		
		if (codeB != null) {
			subsumptionRequestBuilder.codeB(codeB.getCode());
		}
		
		if (system != null) {
			subsumptionRequestBuilder.system(system.getValue());
		}
		
		subsumptionRequestBuilder.version(version);
		
		if (codingA != null) {
			subsumptionRequestBuilder.codingA(asCoding(codingA));
		}
		
		if (codingB != null) {
			subsumptionRequestBuilder.codingB(asCoding(codingB));
		}
		
		final var subsumptionResult = FhirRequests.codeSystems().prepareSubsumes()
			.setRequest(subsumptionRequestBuilder.build())
			.buildAsync()
			.execute(bus.get())
			.getSync();
		
		return FhirSubsumptionConverter.toParameters(subsumptionResult);
	}

	@Operation(name = "subsumes", idempotent = true)
	public Parameters subsumesInstance(
		@IdParam IdType id,
		@OperationParam(name = "codeA") CodeType codeA,
		@OperationParam(name = "codeB") CodeType codeB,
		@OperationParam(name = "codingA") Coding codingA,
		@OperationParam(name = "codingB") Coding codingB
	) {
		return subsumesType(
			codeA, 
			codeB, 
			new UriType(id.withResourceType(null).getValue()), 
			null, 
			codingA, 
			codingB);
	}	
	
	@Operation(name = "$validate-code", idempotent = true)
	public Parameters validateCodeType(
		@OperationParam(name = "url") UriType url,
//		@OperationParam(name = "codeSystem") CodeSystem codeSystem,
		@OperationParam(name = "code") CodeType code,
		@OperationParam(name = "version") String version,
		@OperationParam(name = "display") String display,
		@OperationParam(name = "coding") Coding coding,
		@OperationParam(name = "codeableConcept") CodeableConcept codeableConcept,
		@OperationParam(name = "date") DateTimeType date,
		@OperationParam(name = "abstract") BooleanType isAbstract
//		@OperationParam(name = "displayLanguage") CodeType displayLanguage
	) {
		final var validateCodeRequestBuilder = com.b2international.snowowl.fhir.core.model.codesystem.ValidateCodeRequest.builder();

		if (url != null) {
			validateCodeRequestBuilder.url(url.getValue());
		}
		
		if (code != null) {
			validateCodeRequestBuilder.code(code.getCode());
		}
		
		if (coding != null) {
			validateCodeRequestBuilder.coding(asCoding(coding));
		}
		
		if (codeableConcept != null) {
			validateCodeRequestBuilder.codeableConcept(asCodeableConcept(codeableConcept));
		}
		
		validateCodeRequestBuilder.version(version);
		validateCodeRequestBuilder.display(display);
		
		if (isAbstract != null) {
			validateCodeRequestBuilder.isAbstract(isAbstract.getValue());
		}
		
		if (date != null) {
			validateCodeRequestBuilder.date(date.getValueAsString());
		}
		
		final var validateCodeResult = FhirRequests.codeSystems().prepareValidateCode()
			.setRequest(validateCodeRequestBuilder.build())
			.buildAsync()
			.execute(bus.get())
			.getSync();
		
		return FhirValidateCodeConverter.toParameters(validateCodeResult);
	}
	
	@Operation(name = "$validate-code", idempotent = true)
	public Parameters validateCodeInstance(
		@IdParam IdType id,
		@OperationParam(name = "code") CodeType code,
		@OperationParam(name = "display") String display,
		@OperationParam(name = "coding") Coding coding,
		@OperationParam(name = "codeableConcept") CodeableConcept codeableConcept,
		@OperationParam(name = "date") DateTimeType date,
		@OperationParam(name = "abstract") BooleanType isAbstract
//		@OperationParam(name = "displayLanguage") CodeType displayLanguage
	) {
		return validateCodeType(
			new UriType(id.withResourceType(null).getValue()), 
			code, 
			null, 
			display, 
			coding, 
			codeableConcept, 
			date, 
			isAbstract);
	}
}
