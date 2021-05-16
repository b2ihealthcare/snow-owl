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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemSearchRequestBuilder;
import com.b2international.snowowl.core.codesystem.CodeSystemVersion;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemVersions;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.codesystem.version.CodeSystemVersionSearchRequestBuilder;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.Meta;
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
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
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

	private final String repositoryId;
	
	private Collection<IConceptProperty> supportedProperties;

	public CodeSystemApiProvider(IEventBus bus, List<ExtendedLocale> locales, String repositoryId) {
		super(bus, locales);
		this.repositoryId = repositoryId;
	}
	
	@Override
	protected String getRepositoryId() {
		return repositoryId;
	}
	
	/**
	 * Subclasses may override this method to provide additional properties supported by this FHIR provider.
	 * @return
	 */
	protected Collection<IConceptProperty> getSupportedConceptProperties() {
		return Collections.emptySet();
	}

	@Override
	public boolean isSupported(String uri) {
		if (Strings.isNullOrEmpty(uri)) return false;
		return getSupportedURIs().stream()
			.filter(uri::equalsIgnoreCase)
			.findAny()
			.isPresent();
	}
	
	@Override
	public CodeSystem getCodeSystem(CodeSystemURI codeSystemURI) {
		
		CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
			.one()
			.filterById(codeSystemURI.getCodeSystem())
			.build(repositoryId)
			.execute(getBus())
			.getSync(1000, TimeUnit.MILLISECONDS);
		
		Optional<com.b2international.snowowl.core.codesystem.CodeSystem> optionalCodeSystem = codeSystems.first();
		if (optionalCodeSystem.isEmpty()) {
			throw FhirException.createFhirError(String.format("No code system version found for code system %s", codeSystemURI.getUri()), OperationOutcomeCode.MSG_PARAM_INVALID, CODE_SYSTEM_LOCATION_MARKER);
		}
		
		com.b2international.snowowl.core.codesystem.CodeSystem codeSystem = optionalCodeSystem.get();
		
		CodeSystemVersions codeSystemVersions = CodeSystemRequests.prepareSearchCodeSystemVersion()
			.one()
			.filterByCodeSystemShortName(codeSystem.getShortName())
			.filterByVersionId(codeSystemURI.getPath())
			.build(repositoryId)
			.execute(getBus())
			.getSync(1000, TimeUnit.MILLISECONDS);
		
		Optional<CodeSystemVersion> codeSystemVersionsOptional = codeSystemVersions.first();
		CodeSystemVersion codeSystemVersionEntry = codeSystemVersionsOptional.orElse(getFakeVersion(codeSystem));
		return createCodeSystemBuilder(codeSystem, codeSystemVersionEntry).build();
	}
	
	@Override
	public Collection<CodeSystem> getCodeSystems(final Set<FhirSearchParameter> searchParameters) {
		
		//Pre-fetch code systems based on ES filters available
		Map<CodeSystemVersion, com.b2international.snowowl.core.codesystem.CodeSystem> versionMap = fetchCodeSystems(searchParameters);
		
		//Perform the search from here
		Optional<FhirSearchParameter> idParamOptional = getSearchParam(searchParameters, "_id");
		
		Set<CodeSystemVersion> filteredVersions = Sets.newHashSet(versionMap.keySet());
		
		if (idParamOptional.isPresent()) {
			
			Set<CodeSystemURI> codeSystemUris = idParamOptional.get().getValues().stream()
					.map(PrefixedValue::getValue)
					.map(v -> new CodeSystemURI(v))
					.collect(Collectors.toSet());
			
			for (CodeSystemURI codeSystemURI : codeSystemUris) {
				filteredVersions = versionMap.keySet().stream()
						.filter(csv -> csv.getUri().equals(codeSystemURI))
						.collect(Collectors.toSet());
				
			}
		}
		
		Optional<FhirSearchParameter> nameOptional = getSearchParam(searchParameters, "_name"); 
		if (nameOptional.isPresent()) {
			Collection<String> names = nameOptional.get().getValues().stream()
					.map(PrefixedValue::getValue)
					.collect(Collectors.toSet());
			
			for (String name : names) {
				filteredVersions = versionMap.keySet().stream()
						.filter(csv -> csv.getCodeSystem().equals(name))
						.collect(Collectors.toSet());
				
			}
		}
		
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

	

	/**
	 * Returns the designated FHIR Uri for the given code system
	 * @param codeSystem
	 * @param codeSystemVersion 
	 * @return
	 */
	protected abstract Uri getFhirUri(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem, CodeSystemVersion codeSystemVersion);
	
	/**
	 * Creates a FHIR {@link CodeSystem} from a {@link com.b2international.snowowl.core.codesystem.CodeSystem}
	 * @param codeSystem
	 * @param codeSystemVersion
	 * @return FHIR Code system
	 */
	protected Builder createCodeSystemBuilder(final com.b2international.snowowl.core.codesystem.CodeSystem codeSystem, 
			final CodeSystemVersion codeSystemVersion) {
		
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(codeSystem.getOrganizationLink())
			.value(codeSystem.getOid())
			.build();
		
		String id = getId(codeSystem, codeSystemVersion);
		
		final Builder builder = CodeSystem.builder(id)
			.identifier(identifier)
			.language(getLanguage(codeSystem))
			.name(codeSystem.getShortName())
			.narrative(NarrativeStatus.ADDITIONAL, "<div>"+ codeSystem.getCitation() + "</div>")
			.publisher(codeSystem.getOrganizationLink())
			.status(PublicationStatus.ACTIVE)
			.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
			.title(codeSystem.getName())
			.description(codeSystem.getCitation())
			.url(getFhirUri(codeSystem, codeSystemVersion))
			.content(getCodeSystemContentMode())
			.count(getCount(codeSystemVersion));
		
		if (codeSystemVersion !=null) {
			builder.version(codeSystemVersion.getVersion());
			
			Meta meta = Meta.builder()
				.lastUpdated(Instant.builder().instant(codeSystemVersion.getLastModificationDate()).build())
				.build();
			
			builder.meta(meta);
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
	
	/**
	 * Returns the logical ID of the code system resource
	 * @param codeSystem
	 * @param codeSystemVersion
	 * @return
	 */
	protected String getId(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem, CodeSystemVersion codeSystemVersion) {
		return codeSystemVersion.getUri().toString(); //already fixed on a different branch
	}

	protected Collection<Concept> getConcepts(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
		return Collections.emptySet();
	}

	protected abstract int getCount(CodeSystemVersion codeSystemVersion);

	@Override
	public SubsumptionResult subsumes(SubsumptionRequest subsumptionRequest) {
		
		final String version = getVersion(subsumptionRequest);
		final String branchPath = getBranchPath(version);
		
		String codeA = null;
		String codeB = null;
		if (subsumptionRequest.getCodeA() != null && subsumptionRequest.getCodeB() != null) {
			codeA = subsumptionRequest.getCodeA();
			codeB = subsumptionRequest.getCodeB();
		} else {
			codeA = subsumptionRequest.getCodingA().getCodeValue();
			codeB = subsumptionRequest.getCodingB().getCodeValue();
		}
		
		final Set<String> ancestorsA = fetchAncestors(branchPath, codeA);
		final Set<String> ancestorsB = fetchAncestors(branchPath, codeB);
		
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
	
	/**
	 * Returns the version information from the request
	 * @param subsumptionRequest 
	 * @return version string
	 */
	protected String getVersion(SubsumptionRequest subsumptionRequest) {
		String version = subsumptionRequest.getVersion();

		//get the latest version
		if (version == null) {
			return CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByCodeSystemShortName(getCodeSystemShortName())
				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build(getRepositoryId())
				.execute(getBus())
				.getSync()
				.first()
				.map(CodeSystemVersion::getVersion)
				//never been versioned
				.orElse(null);
		}
		
		return version;
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
	 * @param branchPath
	 * @param componentId
	 * @return
	 */
	protected abstract Set<String> fetchAncestors(String branchPath, String componentId);

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

	protected String getLanguage(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
		return getLanguageCode(codeSystem.getPrimaryLanguage());
	}
	
	private Map<CodeSystemVersion, com.b2international.snowowl.core.codesystem.CodeSystem> fetchCodeSystems(final Set<FhirSearchParameter> searchParameters) {
		
		CodeSystemSearchRequestBuilder codeSystemRequestBuilder = CodeSystemRequests.prepareSearchCodeSystem()
				.all();
			
		CodeSystemVersionSearchRequestBuilder codeSystemVersionRequestBuilder = CodeSystemRequests.prepareSearchCodeSystemVersion()
				.all();
		
		//ID and name searches can be pre-filtered by ES using our requests
		handleIdParameter(codeSystemRequestBuilder, codeSystemVersionRequestBuilder, searchParameters);
		handleNameParameter(codeSystemRequestBuilder, codeSystemVersionRequestBuilder, searchParameters);
		
		CodeSystems codeSystems = codeSystemRequestBuilder
				.build(repositoryId)
				.execute(getBus())
				.getSync();
		 
		CodeSystemVersions codeSystemVersions = codeSystemVersionRequestBuilder
				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build(repositoryId)
				.execute(getBus())
				.getSync();

		Map<CodeSystemVersion, com.b2international.snowowl.core.codesystem.CodeSystem> versionMap = Maps.newLinkedHashMap();
		
		for (CodeSystemVersion codeSystemVersion : codeSystemVersions) {
			
			codeSystems.stream()
					.filter(cs -> cs.getCodeSystemURI().getCodeSystem().equals(codeSystemVersion.getCodeSystem()))
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
			CodeSystemVersionSearchRequestBuilder codeSystemVersionRequestBuilder,
			Set<FhirSearchParameter> searchParameters) {
		
		Optional<FhirSearchParameter> idParamOptional = getSearchParam(searchParameters, "_id");
		
		if (idParamOptional.isPresent()) {
			
			Set<String> codeSystemIds = idParamOptional.get().getValues().stream()
					.map(PrefixedValue::getValue)
					.map(v -> new CodeSystemURI(v).getCodeSystem())
					.collect(Collectors.toSet());
			
			codeSystemRequestBuilder.filterByIds(codeSystemIds);
			codeSystemVersionRequestBuilder.filterByCodeSystemShortNames(codeSystemIds);
			
		}
	}
	
	private void handleNameParameter(CodeSystemSearchRequestBuilder codeSystemRequestBuilder,
			CodeSystemVersionSearchRequestBuilder codeSystemVersionRequestBuilder,
			Set<FhirSearchParameter> searchParameters) {
		
		Optional<FhirSearchParameter> nameParamOptional = getSearchParam(searchParameters, "_name");
		
		if (nameParamOptional.isPresent()) {
			
			Collection<String> names = nameParamOptional.get().getValues().stream()
					.map(PrefixedValue::getValue)
					.collect(Collectors.toSet());
			
			codeSystemRequestBuilder.filterByNameExact(names);
			codeSystemVersionRequestBuilder.filterByCodeSystemShortNames(names);
			
		}
	}
	
	private CodeSystemVersion getFakeVersion(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
		CodeSystemVersion codeSystemVersion = new CodeSystemVersion();
		codeSystemVersion.setDescription("Latest (HEAD) revision, prone to change.");
		codeSystemVersion.setPath(codeSystem.getCodeSystemURI().getPath());
		codeSystemVersion.setRepositoryId(repositoryId);
		codeSystemVersion.setVersion(CodeSystemURI.HEAD);
		codeSystemVersion.setUri(codeSystem.getCodeSystemURI());
		
		//This should be the last revision's date, dummy for now
		codeSystemVersion.setLastModificationDate(new Date());
		return codeSystemVersion;
	}
	
}
