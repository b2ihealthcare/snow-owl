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

import static com.google.common.collect.Sets.newHashSet;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemSearchRequestBuilder;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.version.VersionSearchRequestBuilder;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.core.version.Versions;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.codesystems.*;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.codesystem.*;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem.Builder;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.FhirParameter.PrefixedValue;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;
import com.b2international.snowowl.fhir.core.search.FhirUriSearchParameterDefinition;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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

	/**
	 * Returns the designated FHIR Uri for the given code system
	 * @param codeSystem
	 * @param codeSystemVersion 
	 * @return
	 */
//	protected abstract Uri getFhirUri(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem, Version codeSystemVersion);
	
	/**
	 * Creates a FHIR {@link CodeSystem} from a {@link com.b2international.snowowl.core.codesystem.CodeSystem}
	 * @param codeSystem
	 * @param codeSystemVersion
	 * @return FHIR Code system
	 */
//	protected Builder createCodeSystemBuilder(final com.b2international.snowowl.core.codesystem.CodeSystem codeSystem, final Version codeSystemVersion) {
//		
//		Identifier identifier = Identifier.builder()
//			.use(IdentifierUse.OFFICIAL)
//			.system(codeSystem.getUrl())
//			.value(codeSystem.getOid())
//			.build();
//		
//		String id = codeSystemVersion.getVersionResourceURI().toString();
//		
//		final Builder builder = CodeSystem.builder(id)
//			.identifier(identifier)
//			.language(codeSystem.getLanguage())
//			.title(codeSystem.getTitle())
//			.name(codeSystem.getTitle())
//			.url(getFhirUri(codeSystem, codeSystemVersion))
//			.status(PublicationStatus.getByCodeValue(codeSystem.getStatus()))
//			.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
//			.narrative(NarrativeStatus.ADDITIONAL, codeSystem.getDescription())
//			.description(codeSystem.getDescription())
//			.content(getCodeSystemContentMode())
//			.publisher(codeSystem.getOwner())
//			.purpose(codeSystem.getPurpose())
//			.copyright(codeSystem.getCopyright())
//			.count(getCount(codeSystemVersion));
//		// TODO add usage as UsageContext
//		
//		if (codeSystemVersion !=null) {
//			builder.version(codeSystemVersion.getVersion());
//			
//			 TODO support lastUpdated time on resources
//			Meta meta = Meta.builder()
//				.lastUpdated(Instant.builder().instant(codeSystemVersion.getLastModificationDate()).build())
//				.build();
//			builder.meta(meta);
//		}
//
//		add filters here
//		Collection<Filter> supportedFilters = getSupportedFilters();
//		for (Filter filter : supportedFilters) {
//			builder.addFilter(filter);
//		}
//		
//		Collection<Concept> concepts = getConcepts(codeSystem);
//		for (Concept concept: concepts) {
//			builder.addConcept(concept);
//		}
//		
//		// include supported concept properties
//		getSupportedProperties().stream()
//			.filter(p -> !(SupportedCodeSystemRequestProperties.class.isInstance(p)))
//			.map(SupportedConceptProperty::builder)
//			.map(SupportedConceptProperty.Builder::build)
//			.forEach(builder::addProperty);
//		
//		return builder;
//	}
//	
//	protected Collection<Concept> getConcepts(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
//		return Collections.emptySet();
//	}

//	protected abstract int getCount(Version codeSystemVersion);

//	protected ResourceURI resolveResourceUri(final String system, final String version) {
//		throw new NotImplementedException("TODO Resolve URI from system (%s) and version ('') parameters", system, version);
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
	 * @param request - the lookup request
	 */
//	protected void validateRequestedProperties(LookupRequest request) {
//		final Collection<String> properties = request.getPropertyCodes();
//		
//		final Set<String> supportedCodes = getSupportedProperties().stream().map(p -> {
//			if (p instanceof IConceptProperty.Dynamic) {
//				return p.getUri().getUriValue();
//			} else {
//				return p.getCodeValue();
//			}
//		})
//		.collect(Collectors.toSet());
//		
//		if (!supportedCodes.containsAll(properties)) {
//			if (properties.size() == 1) {
//				throw new BadRequestException(String.format("Unrecognized property %s. Supported properties are: %s.", Arrays.toString(properties.toArray()), Arrays.toString(supportedCodes.toArray())), "LookupRequest.property");
//			} else {
//				throw new BadRequestException(String.format("Unrecognized properties %s. Supported properties are: %s.", Arrays.toString(properties.toArray()), Arrays.toString(supportedCodes.toArray())), "LookupRequest.property");
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
