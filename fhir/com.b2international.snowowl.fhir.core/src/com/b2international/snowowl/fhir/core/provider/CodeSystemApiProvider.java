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
import com.b2international.snowowl.fhir.core.model.codesystem.*;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.codesystem.IConceptProperty;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedCodeSystemRequestProperties;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.codesystem.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.FhirParameter.PrefixedValue;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;
import com.b2international.snowowl.fhir.core.search.FhirUriSearchParameterDefinition;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * FHIR provider base class.
 * 
 * @since 7.0
 */
public abstract class CodeSystemApiProvider extends FhirApiProvider implements ICodeSystemApiProvider {
	
	protected static final String CODE_SYSTEM_LOCATION_MARKER = "CodeSystem";

	private Collection<IConceptProperty> supportedProperties;

	public CodeSystemApiProvider(IEventBus bus, List<ExtendedLocale> locales) {
		super(bus, locales);
	}
	
	/**
	 * Subclasses may override this method to provide additional properties supported by this FHIR provider.
	 * @return
	 */
	protected Collection<IConceptProperty> getSupportedConceptProperties() {
		return Collections.emptySet();
	}

	@Override
	public CodeSystem getCodeSystem(ResourceURI codeSystemURI) {

		final com.b2international.snowowl.core.codesystem.CodeSystem codeSystem;
		try {
			codeSystem = CodeSystemRequests.prepareGetCodeSystem(codeSystemURI.getResourceId())
					.buildAsync()
					.execute(getBus())
					.getSync(1, TimeUnit.MINUTES);
		} catch (NotFoundException e) {
			throw FhirException.createFhirError(String.format("No code system version found for code system %s", codeSystemURI.getUri()), OperationOutcomeCode.MSG_PARAM_INVALID, CODE_SYSTEM_LOCATION_MARKER);
		}
		
		Versions codeSystemVersions = ResourceRequests.prepareSearchVersion()
			.one()
			.filterById(codeSystemURI.toString())
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		
		Optional<Version> codeSystemVersionsOptional = codeSystemVersions.first();
		Version codeSystemVersionEntry = codeSystemVersionsOptional.orElse(getFakeVersion(codeSystem));
		return createCodeSystemBuilder(codeSystem, codeSystemVersionEntry).build();
	}
	
	@Override
	public Collection<CodeSystem> getCodeSystems(final Set<FhirSearchParameter> searchParameters) {
		
		//Pre-fetch code systems based on ES filters available
		Map<Version, com.b2international.snowowl.core.codesystem.CodeSystem> versionMap = fetchCodeSystems(searchParameters);
		
		//Perform the search from here
		Set<Version> filteredVersions = Sets.newHashSet(versionMap.keySet());
		
		getSearchParam(searchParameters, "_id").ifPresent(idFilter -> {
			idFilter.getValues().stream()
				.map(PrefixedValue::getValue)
				.map(v -> new ResourceURI(v)) // TODO make sure parsing errors do NOT result in HTTP 500 errors during search
				.flatMap(codeSystemURI -> versionMap.keySet().stream().filter(csv -> csv.getVersionResourceURI().equals(codeSystemURI)))
				.forEach(filteredVersions::add);
		});
		
		getSearchParam(searchParameters, "_name").ifPresent(nameFilter -> {
			nameFilter.getValues().stream()
				.map(PrefixedValue::getValue)
				.flatMap(name -> versionMap.keySet().stream().filter(csv -> csv.getResourceId().equals(name)))
				.forEach(filteredVersions::add);
		});
		
		Optional<FhirSearchParameter> lastUpdatedOptional = getSearchParam(searchParameters, "_lastUpdated"); //date type
		if (lastUpdatedOptional.isPresent()) {

			PrefixedValue lastUpdatedPrefixedValue = lastUpdatedOptional.get().getValues().iterator().next();
			String lastUpdatedDateString = lastUpdatedPrefixedValue.getValue();
			
			//validate the date value
			long localLong = Long.MAX_VALUE;
			try {
				LocalDate localDate = LocalDate.parse(lastUpdatedDateString);
				localLong = localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(); //?
			} catch (DateTimeParseException dtpe) {
				throw FhirException.createFhirError(String.format("Invalid _lastUpdate search parameter value '%s'.", lastUpdatedDateString), OperationOutcomeCode.MSG_PARAM_INVALID, CODE_SYSTEM_LOCATION_MARKER);
			}

			//TODO: filterByCreated at searches for importDates as opposed to lastModificationDates
			if (lastUpdatedPrefixedValue.getPrefix() == null || 
					FhirUriSearchParameterDefinition.SearchRequestParameterValuePrefix.eq == lastUpdatedPrefixedValue.getPrefix()) {
					
				//versionSearchRequestBuilder.filterByCreatedAt(localLong);
			} else {
				switch (lastUpdatedPrefixedValue.getPrefix()) {
				case eb:
					//versionSearchRequestBuilder.filterByCreatedAt(0, localLong);
					break;
				case sa:
					//versionSearchRequestBuilder.filterByCreatedAt(localLong, Long.MAX_VALUE);
					break;
				default:
					throw FhirException.createFhirError(String.format("Unsupported _lastUpdate search parameter modifier '%s' for value '%s'.", lastUpdatedPrefixedValue.getPrefix().name(), lastUpdatedDateString), OperationOutcomeCode.MSG_PARAM_INVALID, CODE_SYSTEM_LOCATION_MARKER);
				}
			}
		}
		
		//build the FHIR code systems
		return filteredVersions.stream()
				.map(csve -> createCodeSystemBuilder(versionMap.get(csve), csve))
				.map(Builder::build)
				.collect(Collectors.toList());
	}

	@Override
	public ValidateCodeResult validateCode(final String systemUri, final ValidateCodeRequest validationRequest) {
		throw new UnsupportedOperationException();
//		//try to convert the system URi to internal code systemUri
//		ResourceURI codeSystemUri = getCodeSystemUri(systemUri, validationRequest.getVersion());
//		return validateCode(codeSystemUri, validationRequest);
	}
	
	@Override
	public ValidateCodeResult validateCode(final ResourceURI codeSystemUri, final ValidateCodeRequest validationRequest) {
		
		Set<Coding> codings = collectCodingsToValidate(validationRequest);
		
		Map<String, Coding> codingMap = codings.stream().collect(Collectors.toMap(c -> c.getCodeValue(), c -> c));
		
		Concepts concepts = CodeSystemRequests.prepareSearchConcepts()
			.all()
			.filterByIds(codingMap.keySet())
			.build(codeSystemUri)
			.execute(getBus())
			.getSync(1000, TimeUnit.MILLISECONDS);
		
		if (concepts.isEmpty()) {
			return ValidateCodeResult.builder().result(false)
					.message(String.format("Could not find code(s) '%s'", Arrays.toString(codingMap.keySet().toArray())))
					.build();
		} else {
			for (com.b2international.snowowl.core.domain.Concept concept : concepts) {
				Coding coding = codingMap.get(concept.getId());
				if (coding.getDisplay() != null) {
					//any mismatch is false (?)
					if (!coding.getDisplay().equals(concept.getTerm())) {
						return ValidateCodeResult.builder()
								.result(false)
								.display(concept.getTerm())
								.message(String.format("Incorrect display '%s' for code '%s'", coding.getDisplay(), coding.getCodeValue()))
								.build();
					}
				}
			}
			return ValidateCodeResult.builder().result(true).build();
		}
	}

	/**
	 * Returns the designated FHIR Uri for the given code system
	 * @param codeSystem
	 * @param codeSystemVersion 
	 * @return
	 */
	protected abstract Uri getFhirUri(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem, Version codeSystemVersion);
	
	protected Set<Coding> collectCodingsToValidate(ValidateCodeRequest validationRequest) {
		
		Set<Coding> codings = Sets.newHashSet();
				
		if (validationRequest.getCode() != null) {
			
			Coding coding = Coding.builder()
					.code(validationRequest.getCode())
					.display(validationRequest.getDisplay())
					.build();
			
			codings.add(coding);
		}
				
		if (validationRequest.getCoding() != null) {
			codings.add(validationRequest.getCoding());
		}
			
		CodeableConcept codeableConcept = validationRequest.getCodeableConcept();
		if (codeableConcept != null) {
			if (codeableConcept.getCodings() != null) { 
				codeableConcept.getCodings().forEach(c -> codings.add(c));
			}
		}
		return codings;
	}
	
	/**
	 * Creates a FHIR {@link CodeSystem} from a {@link com.b2international.snowowl.core.codesystem.CodeSystem}
	 * @param codeSystem
	 * @param codeSystemVersion
	 * @return FHIR Code system
	 */
	protected Builder createCodeSystemBuilder(final com.b2international.snowowl.core.codesystem.CodeSystem codeSystem, final Version codeSystemVersion) {
		
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(codeSystem.getUrl())
			.value(codeSystem.getOid())
			.build();
		
		String id = codeSystemVersion.getVersionResourceURI().toString();
		
		final Builder builder = CodeSystem.builder(id)
			.identifier(identifier)
			.language(codeSystem.getLanguage())
			.title(codeSystem.getTitle())
			.name(codeSystem.getTitle())
			.url(getFhirUri(codeSystem, codeSystemVersion))
			.status(PublicationStatus.getByCodeValue(codeSystem.getStatus()))
			.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
			.narrative(NarrativeStatus.ADDITIONAL, codeSystem.getDescription())
			.description(codeSystem.getDescription())
			.content(getCodeSystemContentMode())
			.publisher(codeSystem.getOwner())
			.purpose(codeSystem.getPurpose())
			.copyright(codeSystem.getCopyright())
			.count(getCount(codeSystemVersion));
		// TODO add usage as UsageContext
		
		if (codeSystemVersion !=null) {
			builder.version(codeSystemVersion.getVersion());
			
			// TODO support lastUpdated time on resources
//			Meta meta = Meta.builder()
//				.lastUpdated(Instant.builder().instant(codeSystemVersion.getLastModificationDate()).build())
//				.build();
//			builder.meta(meta);
		}

		//add filters here
		Collection<Filter> supportedFilters = getSupportedFilters();
		for (Filter filter : supportedFilters) {
			builder.addFilter(filter);
		}
		
		Collection<Concept> concepts = getConcepts(codeSystem);
		for (Concept concept: concepts) {
			builder.addConcept(concept);
		}
		
		// include supported concept properties
		getSupportedProperties().stream()
			.filter(p -> !(SupportedCodeSystemRequestProperties.class.isInstance(p)))
			.map(SupportedConceptProperty::builder)
			.map(SupportedConceptProperty.Builder::build)
			.forEach(builder::addProperty);
		
		return builder;
	}
	
	protected Collection<Concept> getConcepts(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
		return Collections.emptySet();
	}

	protected abstract int getCount(Version codeSystemVersion);

	@Override
	public SubsumptionResult subsumes(SubsumptionRequest subsumptionRequest) {
		
		final ResourceURI codeSystemUri = resolveResourceUri(subsumptionRequest.getSystem(), subsumptionRequest.getVersion());
		
		String codeA = null;
		String codeB = null;
		if (subsumptionRequest.getCodeA() != null && subsumptionRequest.getCodeB() != null) {
			codeA = subsumptionRequest.getCodeA();
			codeB = subsumptionRequest.getCodeB();
		} else {
			codeA = subsumptionRequest.getCodingA().getCodeValue();
			codeB = subsumptionRequest.getCodingB().getCodeValue();
		}
		
		final Set<String> ancestorsA = fetchAncestors(codeSystemUri, codeA);
		final Set<String> ancestorsB = fetchAncestors(codeSystemUri, codeB);
		
		if (codeA.equals(codeB)) {
			return SubsumptionResult.equivalent();
		} else if (ancestorsA.contains(codeB)) {
			return SubsumptionResult.subsumedBy();
		} else if (ancestorsB.contains(codeA)) {
			return SubsumptionResult.subsumes();
		} else {
			return SubsumptionResult.notSubsumed();
		}
	}
	
	protected ResourceURI resolveResourceUri(final String system, final String version) {
		throw new NotImplementedException("TODO Resolve URI from system (%s) and version ('') parameters", system, version);
	}

	/**
	 * Builds a lookup result property for the given @see {@link IConceptProperty} based on the supplier's value
	 * @param supplier
	 * @param lookupRequest
	 * @param resultBuilder
	 * @param conceptProperty
	 */
	protected void addProperty(Supplier<?> supplier, LookupRequest lookupRequest, LookupResult.Builder resultBuilder, IConceptProperty conceptProperty) {
		
		if (lookupRequest.containsProperty(conceptProperty.getCode())) {
			if (supplier.get() != null) {
				resultBuilder.addProperty(conceptProperty.propertyOf(supplier));
			}
		}
	}

	/**
	 * Returns all ancestors up to the terminology's root component (in terms of Snow Owl, this means {@link IComponent#ROOT_ID}).
	 * 
	 * @param codeSystemUri
	 * @param componentId
	 * @return set of parent IDs
	 */
	protected abstract Set<String> fetchAncestors(final ResourceURI codeSystemUri, String componentId);

	/**
	 * Returns the supported properties
	 * @return the supported properties
	 */
	protected Collection<IConceptProperty> getSupportedProperties() {
		if (supportedProperties == null) {
			supportedProperties = newHashSet();
			Stream.of(SupportedCodeSystemRequestProperties.values()).forEach(supportedProperties::add);
			supportedProperties.addAll(getSupportedConceptProperties());
		}
		return supportedProperties;
	}
	
	/**
	 * Subclasses may override this method to provide filters supported by this FHIR provider/code system.
	 * @return the supported filters
	 */
	protected Collection<Filter> getSupportedFilters() {
		return Collections.emptySet();
	}
	
	/**
	 * @param request - the lookup request
	 */
	protected void validateRequestedProperties(LookupRequest request) {
		final Collection<String> properties = request.getPropertyCodes();
		
		final Set<String> supportedCodes = getSupportedProperties().stream().map(p -> {
			if (p instanceof IConceptProperty.Dynamic) {
				return p.getUri().getUriValue();
			} else {
				return p.getCodeValue();
			}
		})
		.collect(Collectors.toSet());
		
		if (!supportedCodes.containsAll(properties)) {
			if (properties.size() == 1) {
				throw new BadRequestException(String.format("Unrecognized property %s. Supported properties are: %s.", Arrays.toString(properties.toArray()), Arrays.toString(supportedCodes.toArray())), "LookupRequest.property");
			} else {
				throw new BadRequestException(String.format("Unrecognized properties %s. Supported properties are: %s.", Arrays.toString(properties.toArray()), Arrays.toString(supportedCodes.toArray())), "LookupRequest.property");
			}
		}
	}
	
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
	
	protected CodeSystemContentMode getCodeSystemContentMode() {
		return CodeSystemContentMode.NOT_PRESENT;
	}

	private Map<Version, com.b2international.snowowl.core.codesystem.CodeSystem> fetchCodeSystems(final Set<FhirSearchParameter> searchParameters) {
		
		CodeSystemSearchRequestBuilder codeSystemRequestBuilder = CodeSystemRequests.prepareSearchCodeSystem()
				.all();
			
		VersionSearchRequestBuilder codeSystemVersionRequestBuilder = ResourceRequests.prepareSearchVersion()
				.all();
		
		//ID and name searches can be pre-filtered by ES using our requests
		handleIdParameter(codeSystemRequestBuilder, codeSystemVersionRequestBuilder, searchParameters);
		handleNameParameter(codeSystemRequestBuilder, codeSystemVersionRequestBuilder, searchParameters);
		
		CodeSystems codeSystems = codeSystemRequestBuilder
				.buildAsync()
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		 
		Versions codeSystemVersions = codeSystemVersionRequestBuilder
				.sortBy(SearchResourceRequest.SortField.descending(VersionDocument.Fields.EFFECTIVE_TIME))
				.buildAsync()
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);

		Map<Version, com.b2international.snowowl.core.codesystem.CodeSystem> versionMap = Maps.newLinkedHashMap();
		
		for (Version codeSystemVersion : codeSystemVersions) {
			codeSystems.stream()
					.filter(cs -> cs.getResourceURI().equals(codeSystemVersion.getResource()))
					.findAny()
					.ifPresent(cs -> versionMap.put(codeSystemVersion, cs));
		}
		
		//add a fake 'HEAD' version to each code system
		for (com.b2international.snowowl.core.codesystem.CodeSystem codeSystem : codeSystems) {
			 versionMap.put(getFakeVersion(codeSystem), codeSystem);
		}
		return versionMap;
	}

	private void handleIdParameter(CodeSystemSearchRequestBuilder codeSystemRequestBuilder,
			VersionSearchRequestBuilder codeSystemVersionRequestBuilder,
			Set<FhirSearchParameter> searchParameters) {
		
		Optional<FhirSearchParameter> idParamOptional = getSearchParam(searchParameters, "_id");
		
		if (idParamOptional.isPresent()) {
			
			Set<String> codeSystemIds = idParamOptional.get().getValues().stream()
					.map(PrefixedValue::getValue)
					.map(v -> new ResourceURI(v).withoutPath().toString()) // TODO parsing exception should NOT result in HTTP 500 error
					.collect(Collectors.toSet());
			
			codeSystemRequestBuilder.filterByIds(codeSystemIds);
			codeSystemVersionRequestBuilder.filterByResources(codeSystemIds);
			
		}
	}
	
	private void handleNameParameter(CodeSystemSearchRequestBuilder codeSystemRequestBuilder,
			VersionSearchRequestBuilder codeSystemVersionRequestBuilder,
			Set<FhirSearchParameter> searchParameters) {
		
		Optional<FhirSearchParameter> nameParamOptional = getSearchParam(searchParameters, "_name");
		
		if (nameParamOptional.isPresent()) {
			
			Collection<String> names = nameParamOptional.get().getValues().stream()
					.map(PrefixedValue::getValue)
					.collect(Collectors.toSet());
			
			codeSystemRequestBuilder.filterByTitleExact(names);
			// TODO do we need this???
//			codeSystemVersionRequestBuilder.filterByCodeSystemShortNames(names);
			
		}
	}
	
	private Version getFakeVersion(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
		Version codeSystemVersion = new Version();
		codeSystemVersion.setDescription("Latest (HEAD) revision, prone to change.");
		codeSystemVersion.setBranchPath(codeSystem.getBranchPath());
		codeSystemVersion.setVersion(ResourceURI.HEAD);
		codeSystemVersion.setResource(codeSystem.getResourceURI());
		// TODO if needed we should be able to fetch the associated branch's last commit timestamp, the current head timestamp and use that for last change date
		return codeSystemVersion;
	}
	
}
