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
package com.b2international.snowowl.fhir.core.provider;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;

/**
 * FHIR provider base class.
 * 
 * @since 7.0
 */
public abstract class CodeSystemApiProvider extends FhirApiProvider {
	
	protected static final String CODE_SYSTEM_LOCATION_MARKER = "CodeSystem";

	public CodeSystemApiProvider(IEventBus bus, List<ExtendedLocale> locales) {
		super(bus, locales);
	}
	
//	public Collection<CodeSystem> getCodeSystems(final Set<FhirSearchParameter> searchParameters) {
//		
//		//Pre-fetch code systems based on ES filters available
//		Map<Version, com.b2international.snowowl.core.codesystem.CodeSystem> versionMap = fetchCodeSystems(searchParameters);
//		
//		//Perform the search from here
//		Set<Version> filteredVersions = Sets.newHashSet(versionMap.keySet());
//		
//		Optional<FhirSearchParameter> lastUpdatedOptional = getSearchParam(searchParameters, "_lastUpdated"); //date type
//		if (lastUpdatedOptional.isPresent()) {
//
//			PrefixedValue lastUpdatedPrefixedValue = lastUpdatedOptional.get().getValues().iterator().next();
//			String lastUpdatedDateString = lastUpdatedPrefixedValue.getValue();
//			
//			//validate the date value
//			long localLong = Long.MAX_VALUE;
//			try {
//				LocalDate localDate = LocalDate.parse(lastUpdatedDateString);
//				localLong = localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(); //?
//			} catch (DateTimeParseException dtpe) {
//				throw FhirException.createFhirError(String.format("Invalid _lastUpdate search parameter value '%s'.", lastUpdatedDateString), OperationOutcomeCode.MSG_PARAM_INVALID, CODE_SYSTEM_LOCATION_MARKER);
//			}
//
//			//TODO: filterByCreated at searches for importDates as opposed to lastModificationDates
//			if (lastUpdatedPrefixedValue.getPrefix() == null || 
//					FhirUriSearchParameterDefinition.SearchRequestParameterValuePrefix.eq == lastUpdatedPrefixedValue.getPrefix()) {
//					
//				//versionSearchRequestBuilder.filterByCreatedAt(localLong);
//			} else {
//				switch (lastUpdatedPrefixedValue.getPrefix()) {
//				case eb:
//					//versionSearchRequestBuilder.filterByCreatedAt(0, localLong);
//					break;
//				case sa:
//					//versionSearchRequestBuilder.filterByCreatedAt(localLong, Long.MAX_VALUE);
//					break;
//				default:
//					throw FhirException.createFhirError(String.format("Unsupported _lastUpdate search parameter modifier '%s' for value '%s'.", lastUpdatedPrefixedValue.getPrefix().name(), lastUpdatedDateString), OperationOutcomeCode.MSG_PARAM_INVALID, CODE_SYSTEM_LOCATION_MARKER);
//				}
//			}
//		}
//		
//		//build the FHIR code systems
//		return filteredVersions.stream()
//				.map(csve -> createCodeSystemBuilder(versionMap.get(csve), csve))
//				.map(Builder::build)
//				.collect(Collectors.toList());
//	}

//	/**
//	 * Builds a lookup result property for the given @see {@link IConceptProperty} based on the supplier's value
//	 * @param supplier
//	 * @param lookupRequest
//	 * @param resultBuilder
//	 * @param conceptProperty
//	 */
//	protected void addProperty(Supplier<?> supplier, LookupRequest lookupRequest, LookupResult.Builder resultBuilder, IConceptProperty conceptProperty) {
//		
//		if (lookupRequest.containsProperty(conceptProperty.getCode())) {
//			if (supplier.get() != null) {
//				resultBuilder.addProperty(conceptProperty.propertyOf(supplier));
//			}
//		}
//	}

	/**
	 * Set the base properties if requested
	 * @param lookupRequest
	 * @param resultBuilder 
	 * @param name
	 * @param version
	 * @param displayString
	 */
	protected void setBaseProperties(LookupRequest lookupRequest, LookupResult.Builder resultBuilder, String name, String version, String displayString) {
		
		/*
		 * Name is mandatory, why is it allowed to be listed as a requested property in the spec?? - bbanfai
		 */
		resultBuilder.name(name);
				
		if (lookupRequest.isVersionPropertyRequested()) {
			resultBuilder.version(version);
		}
			
		/*
		 * Display is mandatory, why is it allowed to be listed as a requested property in the spec?? - bbanfai
		 */
		resultBuilder.display(displayString);
	}
	
}
