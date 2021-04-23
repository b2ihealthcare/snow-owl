/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemVersions;
import com.b2international.snowowl.core.codesystem.CodeSystems;
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
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * FHIR provider base class.
 * 
 * @since 7.0
 */
public abstract class CodeSystemApiProvider extends FhirApiProvider implements ICodeSystemApiProvider {
	
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
		
		String branchPath = codeSystemURI.getPath();
		
		if (branchPath.equals(IBranchPath.MAIN_BRANCH)) {
			throw FhirException.createFhirError(String.format("No code system version found for code system %s", codeSystemURI.getUri()), OperationOutcomeCode.MSG_PARAM_INVALID, "CodeSystem");
		} else {
			Optional<CodeSystemVersionEntry> csve = CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByBranchPath(branchPath)
				.build(repositoryId)
				.execute(getBus())
				.getSync()
				.first();
			
			//could not find code system + version
			if (!csve.isPresent()) {
				throw FhirException.createFhirError(String.format("No code system version found for code system %s", codeSystemURI.getUri()), OperationOutcomeCode.MSG_PARAM_INVALID, "CodeSystem");
			}
			
			CodeSystemVersionEntry codeSystemVersionEntry = csve.get();
			
			com.b2international.snowowl.core.codesystem.CodeSystem codeSystem = CodeSystemRequests
					.prepareGetCodeSystem(codeSystemVersionEntry.getCodeSystemShortName())
					.build(repositoryId)
					.execute(getBus())
					.getSync();

			return createCodeSystemBuilder(codeSystem, codeSystemVersionEntry).build();
		}
	}
	
	@Override
	public final CodeSystem getCodeSystem(String codeSystemUri) {
		if (!isSupported(codeSystemUri)) {
			throw new BadRequestException(String.format("Code system with URI %s is not supported by this provider %s.", codeSystemUri, this.getClass().getSimpleName()));
		}
		return getCodeSystems()
				.stream()
				.filter(cs -> cs.getUrl().getUriValue().equals(codeSystemUri))
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Could not find any code systems for %s.", codeSystemUri));
	}
	
	@Override
	public Collection<CodeSystem> getCodeSystems() {
		
		//Create a code system for every extension and every version
		CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.build(repositoryId)
			.execute(getBus())
			.getSync();
		
		//fetch all the versions
		CodeSystemVersions codeSystemVersions = CodeSystemRequests.prepareSearchCodeSystemVersion()
			.all()
			.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
			.build(repositoryId)
			.execute(getBus())
			.getSync();
		
		List<CodeSystem> fhirCodeSystemList = Lists.newArrayList();
		
		codeSystems.forEach(cse -> { 
			
			List<CodeSystem> fhirCodeSystems = codeSystemVersions.stream()
				.filter(csv -> csv.getCodeSystemShortName().equals(cse.getShortName()))
				.map(csve -> createCodeSystemBuilder(cse, csve))
				.map(Builder::build)
				.collect(Collectors.toList());
			
			fhirCodeSystemList.addAll(fhirCodeSystems);
			
		});
		return fhirCodeSystemList;
	}
	
	/**
	 * Returns the designated FHIR Uri for the given code system
	 * @param codeSystem
	 * @param codeSystemVersion 
	 * @return
	 */
	protected abstract Uri getFhirUri(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem, CodeSystemVersionEntry codeSystemVersion);
	
	/**
	 * Creates a FHIR {@link CodeSystem} from a {@link com.b2international.snowowl.core.codesystem.CodeSystem}
	 * @param codeSystem
	 * @param codeSystemVersion
	 * @return FHIR Code system
	 */
	protected final Builder createCodeSystemBuilder(final com.b2international.snowowl.core.codesystem.CodeSystem codeSystem, 
			final CodeSystemVersionEntry codeSystemVersion) {
		
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
			builder.version(codeSystemVersion.getVersionId());
			
			Meta meta = Meta.builder()
				.lastUpdated(Instant.builder().instant(codeSystemVersion.getLatestUpdateDate()).build())
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
	
	/*
	 * Returns the logical ID of the code system resource
	 */
	private String getId(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem, CodeSystemVersionEntry codeSystemVersion) {
		//in theory there should always be at least one version present
		if (codeSystemVersion != null) {
			return codeSystemVersion.getRepositoryUuid() + ":" + codeSystemVersion.getPath();
		} else {
			return codeSystem.getRepositoryId() + ":" + codeSystem.getBranchPath();
		}
	}

	protected Collection<Concept> getConcepts(com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
		return Collections.emptySet();
	}

	protected abstract int getCount(CodeSystemVersionEntry codeSystemVersion);

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
			Optional<CodeSystemVersionEntry> optionalVersion = CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByCodeSystemShortName(getCodeSystemShortName())
				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build(getRepositoryId())
				.execute(getBus())
				.getSync()
				.first();
				
			if (optionalVersion.isPresent()) {
				return optionalVersion.get().getVersionId();
			} else {
				//never been versioned
				return null;
			}
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
}
