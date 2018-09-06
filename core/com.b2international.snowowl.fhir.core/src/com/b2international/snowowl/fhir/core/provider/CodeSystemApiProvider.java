/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.fhir.core.LogicalId;
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
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedCodeSystemRequestProperties;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.b2international.snowowl.fhir.core.model.subsumption.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.subsumption.SubsumptionResult;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * FHIR provider base class.
 * 
 * @since 6.4
 */
public abstract class CodeSystemApiProvider extends FhirApiProvider implements ICodeSystemApiProvider {
	
	private final String repositoryId;
	
	private final Collection<IConceptProperty> supportedProperties = Sets.newHashSet();

	public CodeSystemApiProvider(String repositoryId) {
		this.repositoryId = repositoryId;
		supportedProperties.addAll(Sets.newHashSet(SupportedCodeSystemRequestProperties.values()));
		supportedProperties.addAll(getSupportedConceptProperties());
	}
	
	@Override
	protected String getRepositoryId() {
		return repositoryId;
	}
	
	/**
	 * Subclasses may override this method to provide additional properties supported by this FHIR provider.
	 * @param supportedProperties
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
	public CodeSystem getCodeSystem(LogicalId codeSystemLogicalId) {
		
		String branchPath = codeSystemLogicalId.getBranchPath();
		
		if (branchPath.equals(IBranchPath.MAIN_BRANCH)) {
			throw FhirException.createFhirError(String.format("No code system version found for code system %s", codeSystemLogicalId), OperationOutcomeCode.MSG_PARAM_INVALID, "CodeSystem");
		} else {
			Optional<CodeSystemVersionEntry> csve = CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByBranchPath(branchPath)
				.build(repositoryId)
				.execute(getBus())
				.getSync()
				.first();
			
			CodeSystemVersionEntry codeSystemVersionEntry = csve.get();

			CodeSystemEntry codeSystemEntry = CodeSystemRequests.prepareGetCodeSystem(codeSystemVersionEntry.getCodeSystemShortName())
					.build(repositoryId)
					.execute(getBus())
					.getSync();

			return createCodeSystemBuilder(codeSystemEntry, codeSystemVersionEntry).build();
		}
	}
	
	@Override
	public final CodeSystem getCodeSystem(String codeSystemUri) {
		if (!isSupported(codeSystemUri)) {
			throw new BadRequestException(String.format("Code system with URI %s is not supported by this provider %s.", codeSystemUri, this.getClass().getSimpleName()));
		}
		return getCodeSystems()
				.stream()
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
			.sortBy(SearchResourceRequest.SortField.descending(Revision.STORAGE_KEY))
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
	 * @param codeSystemEntry 
	 * @param codeSystemVersion 
	 * @return
	 */
	protected abstract Uri getFhirUri(CodeSystemEntry codeSystemEntry, CodeSystemVersionEntry codeSystemVersion);
	
	/**
	 * Creates a FHIR {@link CodeSystem} from a {@link CodeSystemEntry}
	 * @param codeSystemEntry
	 * @return FHIR Code system
	 */
	protected final Builder createCodeSystemBuilder(final CodeSystemEntry codeSystemEntry, final CodeSystemVersionEntry codeSystemVersion) {
		
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(codeSystemEntry.getOrgLink())
			.value(codeSystemEntry.getOid())
			.build();
		
		String id = getId(codeSystemEntry, codeSystemVersion);
		
		final Builder builder = CodeSystem.builder(id)
			.identifier(identifier)
			.language(getLanguage(codeSystemEntry))
			.name(codeSystemEntry.getShortName())
			.narrative(NarrativeStatus.ADDITIONAL, "<div>"+ codeSystemEntry.getCitation() + "</div>")
			.publisher(codeSystemEntry.getOrgLink())
			.status(PublicationStatus.ACTIVE)
			.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
			.title(codeSystemEntry.getName())
			.description(codeSystemEntry.getCitation())
			.url(getFhirUri(codeSystemEntry, codeSystemVersion))
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
		
		Collection<Concept> concepts = getConcepts(codeSystemEntry);
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
	private String getId(CodeSystemEntry codeSystemEntry, CodeSystemVersionEntry codeSystemVersion) {
		//in theory there should always be at least one version present
		if (codeSystemVersion != null) {
			return codeSystemVersion.getRepositoryUuid() + ":" + codeSystemVersion.getPath();
		} else {
			return codeSystemEntry.getRepositoryUuid() + ":" + codeSystemEntry.getBranchPath();
		}
	}

	protected Collection<Concept> getConcepts(CodeSystemEntry codeSystemEntry) {
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
		return subsumptionRequest.getVersion();
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
	 * @param properties
	 */
	protected void validateRequestedProperties(LookupRequest request) {
		final Collection<String> properties = request.getProperties();
		
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
	 * @param snomedName
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

	protected String getLanguage(CodeSystemEntry codeSystemEntry) {
		return getLanguageCode(codeSystemEntry.getLanguage());
	}
	
}
