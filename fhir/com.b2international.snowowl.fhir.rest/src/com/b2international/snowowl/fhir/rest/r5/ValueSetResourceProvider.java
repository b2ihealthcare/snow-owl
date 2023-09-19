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
import com.b2international.snowowl.fhir.core.model.r5.FhirResourceConverter;
import com.b2international.snowowl.fhir.core.model.r5.FhirResourceInputConverter;
import com.b2international.snowowl.fhir.core.model.r5.FhirValidateCodeConverter;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateResult;
import com.b2international.snowowl.fhir.core.request.valueset.FhirValueSetSearchRequestBuilder;
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
public class ValueSetResourceProvider extends AbstractResourceProvider<ValueSet> {

	public ValueSetResourceProvider() {
		super(ValueSet.class);
	}
	
	@Create
	public MethodOutcome create(
		@ResourceParam ValueSet valueSet, 
		RequestDetails requestDetails
	) {
		// Ignore the input identifier on purpose and assign one locally
		final String generatedId = IDs.base62UUID();
		valueSet.setId(generatedId);
		
		createOrUpdate(valueSet, requestDetails);
		return new MethodOutcome(valueSet.getIdElement());
	}

	@Update
	public MethodOutcome update(
		@IdParam IdType id,
		@ResourceParam ValueSet valueSet, 
		RequestDetails requestDetails
	) {
		final String idInResource = valueSet.getId();
		final String idInParameter = id.getValue();
		
		if (idInResource == null) {
			throw new BadRequestException("Code system resource did not contain an ID element.");
		}
		
		if (!Objects.equals(idInResource, idInParameter)) {
			throw new BadRequestException("Code system resource ID '" + idInResource + "' disagrees with '" + idInParameter + "' provided in the request URL.");
		}
		
		final boolean created = FhirResourceUpdateResult.Action.CREATED.equals(createOrUpdate(valueSet, requestDetails).getAction());
		return new MethodOutcome(valueSet.getIdElement()).setCreated(created);
	}
	
	private FhirResourceUpdateResult createOrUpdate(ValueSet valueSet, RequestDetails requestDetails) {
		String author = requestDetails.getHeader(AbstractRestService.X_AUTHOR);
		String owner = requestDetails.getHeader(HEADER_X_OWNER);
		String ownerProfileName = requestDetails.getHeader(HEADER_X_OWNER_PROFILE_NAME);
		String bundleId = requestDetails.getHeader(HEADER_X_BUNDLE_ID);
		
		String effectiveDateHeader = requestDetails.getHeader(HEADER_X_EFFECTIVE_DATE);
		LocalDate defaultEffectiveDate = !StringUtils.isEmpty(effectiveDateHeader)
			? LocalDate.parse(effectiveDateHeader)
			: null;
		
		final var coreValueSet = FhirResourceInputConverter.toValueSet(valueSet);
		
		return FhirRequests.valueSets()
			.prepareUpdate()
			.setFhirValueSet(coreValueSet)
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
	public ValueSet getById(
		@IdParam IdType id, 
		SummaryEnum _summary, 
		@Elements Set<String> _elements, 
		RequestDetails requestDetails
	) {
		final String locales = requestDetails.getHeader(HEADER_ACCEPT_LANGUAGE);
		
		try {
			
			final String idOrUrl = id.getValue();
			final var valueSet = FhirRequests.valueSets().prepareGet(idOrUrl)
				.setSummary(_summary != null ? _summary.getCode() : null)
				.setElements(_elements != null ? ImmutableList.copyOf(_elements) : null)
				.setLocales(locales)
				.buildAsync()
				.execute(bus.get())
				.getSync();
			
			return FhirResourceConverter.toFhirValueSet(valueSet);
			
		} catch (NotFoundException e) {
			return null;
		}
	}
	
	@Search
	public IBundleProvider search(
		@OptionalParam(name = ValueSet.SP_RES_ID) StringOrListParam _id,
		@OptionalParam(name = ValueSet.SP_NAME) StringOrListParam name,
		@OptionalParam(name = ValueSet.SP_TITLE) StringParam title,
		@OptionalParam(name = Constants.PARAM_LASTUPDATED) DateParam _lastUpdated,
		@OptionalParam(name = ValueSet.SP_URL) UriOrListParam url,
		@OptionalParam(name = ValueSet.SP_VERSION) StringOrListParam version,
//		@OptionalParam(name = ValueSet.SP_STATUS) StringParam status,
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
		
		final FhirValueSetSearchRequestBuilder requestBuilder = FhirRequests.valueSets().prepareSearch()
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
//			FhirRequests.valueSets().prepareDelete(id.getValue())
//				.force(force)
//				.build(author, String.format("Deleting value set %s", id))
//				.execute(bus.get())
//				.getSync();
//			
//		} catch (NotFoundException e) {
//			throw new ResourceNotFoundException(id);
//		} catch (Exception e) {
//			throw new ResourceVersionConflictException(e.getMessage());
//		}
//	}
	
	@Operation(name = "$expand", idempotent = true)
	public ValueSet expandType(
		@OperationParam(name = "url") UriType url,
//		@OperationParam(name = "valueSet") ValueSet valueSet,
		@OperationParam(name = "valueSetVersion") String valueSetVersion,
//		@OperationParam(name = "context") UriType context,
//		@OperationParam(name = "contextDirection") CodeType contextDirection,
		@OperationParam(name = "filter") String filter,
//		@OperationParam(name = "date") DateTimeType date,
//		@OperationParam(name = "offset") IntegerType offset,
		@OperationParam(name = "count") IntegerType count,
		@OperationParam(name = "includeDesignations") BooleanType includeDesignations,
//		@OperationParam(name = "designation") TokenOrListParam designation,
//		@OperationParam(name = "includeDefinition") BooleanType includeDefinition,
		@OperationParam(name = "activeOnly") BooleanType activeOnly,
//		@OperationParam(name = "useSupplement") List<CanonicalType> useSupplement,
//		@OperationParam(name = "excludeNested") BooleanType excludeNested,
//		@OperationParam(name = "excludeNotForUI") BooleanType excludeNotForUI,
//		@OperationParam(name = "excludePostCoordinated") BooleanType excludePostCoordinated,
		@OperationParam(name = "displayLanguage") CodeType displayLanguage,
//		@OperationParam(name = "property") List<TokenOrListParam> property,
//		@OperationParam(name = "exclude-system") List<CanonicalType> excludeSystem,
//		@OperationParam(name = "system-version") List<CanonicalType> systemVersion,
//		@OperationParam(name = "check-system-version") List<CanonicalType> checkSystemVersion,
//		@OperationParam(name = "force-system-version") List<CanonicalType> forceSystemVersion,
		// Additional non-standard parameters
		@OperationParam(name = "after") String after,
		@OperationParam(name = "withHistorySupplements") BooleanType withHistorySupplements
	) {
		final var expandRequestBuilder = com.b2international.snowowl.fhir.core.model.valueset.ExpandValueSetRequest.builder();

		if (url != null) {
			expandRequestBuilder.url(url.getValue());
		}
		
		expandRequestBuilder.valueSetVersion(valueSetVersion);
		expandRequestBuilder.filter(filter);
		expandRequestBuilder.after(after);
		
		if (activeOnly != null) {
			expandRequestBuilder.activeOnly(activeOnly.getValue());
		}
		
		if (count != null) {
			expandRequestBuilder.count(count.getValue());
		}

		if (displayLanguage != null) {
			expandRequestBuilder.displayLanguage(asCode(displayLanguage));
		}
		
		if (includeDesignations != null) {
			expandRequestBuilder.includeDesignations(includeDesignations.getValue());
		}
		
		if (withHistorySupplements != null) {
			expandRequestBuilder.withHistorySupplements(withHistorySupplements.getValue());
		}
		
		// expandRequestBuilder.checkSystemVersion(...)
		// expandRequestBuilder.context(...)
		// expandRequestBuilder.contextDirection(...)
		// expandRequestBuilder.date(...)
		// expandRequestBuilder.designation(...)
		// expandRequestBuilder.excludeNested(...)
		// expandRequestBuilder.excludeNotForUI(...)
		// expandRequestBuilder.excludePostCoordinated(...)
		// expandRequestBuilder.excludeSystem(...)
		// expandRequestBuilder.forceSystemVersion(...)
		// expandRequestBuilder.includeDefinition(...)
		// expandRequestBuilder.offset(...)
		// expandRequestBuilder.systemVersion(...)
		// expandRequestBuilder.valueSet(...)
		// expandRequestBuilder.valueSetVersion(...)
		
		final var expandedValueSet = FhirRequests.valueSets().prepareExpand()
			.setRequest(expandRequestBuilder.build())
			.buildAsync()
			.execute(bus.get())
			.getSync();
		
		return FhirResourceConverter.toFhirValueSet(expandedValueSet);
	}

	@Operation(name = "$expand", idempotent = true)
	public ValueSet expandInstance(
		@IdParam IdType id,
//		@OperationParam(name = "context") UriType context,
//		@OperationParam(name = "contextDirection") CodeType contextDirection,
		@OperationParam(name = "filter") String filter,
//		@OperationParam(name = "date") DateTimeType date,
//		@OperationParam(name = "offset") IntegerType offset,
		@OperationParam(name = "count") IntegerType count,
		@OperationParam(name = "includeDesignations") BooleanType includeDesignations,
//		@OperationParam(name = "designation") TokenOrListParam designation,
//		@OperationParam(name = "includeDefinition") BooleanType includeDefinition,
		@OperationParam(name = "activeOnly") BooleanType activeOnly,
//		@OperationParam(name = "useSupplement") List<CanonicalType> useSupplement,
//		@OperationParam(name = "excludeNested") BooleanType excludeNested,
//		@OperationParam(name = "excludeNotForUI") BooleanType excludeNotForUI,
//		@OperationParam(name = "excludePostCoordinated") BooleanType excludePostCoordinated,
		@OperationParam(name = "displayLanguage") CodeType displayLanguage,
//		@OperationParam(name = "property") List<TokenOrListParam> property,
//		@OperationParam(name = "exclude-system") List<CanonicalType> excludeSystem,
//		@OperationParam(name = "system-version") List<CanonicalType> systemVersion,
//		@OperationParam(name = "check-system-version") List<CanonicalType> checkSystemVersion,
//		@OperationParam(name = "force-system-version") List<CanonicalType> forceSystemVersion,
		// Additional non-standard parameters
		@OperationParam(name = "after") String after,
		@OperationParam(name = "withHistorySupplements") BooleanType withHistorySupplements
	) {
		return expandType(
			new UriType(id.withResourceType(null).getValue()), 
			null, 
			filter, 
			count, 
			includeDesignations, 
			activeOnly, 
			displayLanguage, 
			after, 
			withHistorySupplements); 
	}
	
	@Operation(name = "$validate-code", idempotent = true)
	public Parameters validateCodeType(
		@OperationParam(name = "url") UriType url,
//		@OperationParam(name = "context") UriType context,
//		@OperationParam(name = "valueSet") ValueSet valueSet,
		@OperationParam(name = "valueSetVersion") String valueSetVersion,
		@OperationParam(name = "code") CodeType code,
		@OperationParam(name = "system") UriType system,
		@OperationParam(name = "systemVersion") String systemVersion
//		@OperationParam(name = "display") String display,
//		@OperationParam(name = "coding") Coding coding,
//		@OperationParam(name = "codeableConcept") CodeableConcept codeableConcept,
//		@OperationParam(name = "date") DateTimeType date,
//		@OperationParam(name = "abstract") BooleanType isAbstract,
//		@OperationParam(name = "displayLanguage") CodeType displayLanguage
//		@OperationParam(name = "useSupplement") List<CanonicalType> useSupplement
	) {
		final var validateCodeRequestBuilder = com.b2international.snowowl.fhir.core.model.valueset.ValidateCodeRequest.builder();
		
		if (url != null) {
			validateCodeRequestBuilder.url(url.getValue());
		}
		
		validateCodeRequestBuilder.valueSetVersion(valueSetVersion);
		
		if (code != null) {
			validateCodeRequestBuilder.code(code.getCode());
		}
		
		if (system != null) {
			validateCodeRequestBuilder.system(system.getValue());
		}
		
		validateCodeRequestBuilder.systemVersion(systemVersion);
		
		// validateCodeRequestBuilder.codeableConcept(...)
		// validateCodeRequestBuilder.coding(...)
		// validateCodeRequestBuilder.context(...)
		// validateCodeRequestBuilder.date(...)
		// validateCodeRequestBuilder....(...)
		// validateCodeRequestBuilder....Language(...)
		// validateCodeRequestBuilder.isAbstract(...)
		// validateCodeRequestBuilder.valueSet(...)
		
		final var validateCodeResult = FhirRequests.valueSets().prepareValidateCode()
			.setRequest(validateCodeRequestBuilder.build())
			.buildAsync()
			.execute(bus.get())
			.getSync();
		
		return FhirValidateCodeConverter.toParameters(validateCodeResult);
	}
	
	@Operation(name = "$validate-code", idempotent = true)
	public Parameters validateCodeInstance(
		@IdParam IdType id,
//		@OperationParam(name = "context") UriType context,
		@OperationParam(name = "code") CodeType code,
		@OperationParam(name = "system") UriType system,
		@OperationParam(name = "systemVersion") String systemVersion
//		@OperationParam(name = "display") String display,
//		@OperationParam(name = "coding") Coding coding,
//		@OperationParam(name = "codeableConcept") CodeableConcept codeableConcept,
//		@OperationParam(name = "date") DateTimeType date,
//		@OperationParam(name = "abstract") BooleanType isAbstract,
//		@OperationParam(name = "displayLanguage") CodeType displayLanguage
//		@OperationParam(name = "useSupplement") List<CanonicalType> useSupplement
	) {
		return validateCodeType(
			new UriType(id.withResourceType(null).getValue()), 
			null, 
			code, 
			system, 
			systemVersion);
	}
}
