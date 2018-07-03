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
package com.b2international.snowowl.fhir.core;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.ConceptProperties;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.subsumption.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.subsumption.SubsumptionResult;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Strings;

/**
 * FHIR provider base class.
 * 
 * @since 6.4
 */
public abstract class CodeSystemApiProvider extends FhirApiProvider implements ICodeSystemApiProvider {
	
	private final String repositoryId;

	public CodeSystemApiProvider(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	protected final String repositoryId() {
		return repositoryId;
	}
	
	@Override
	public final boolean isSupported(String uri) {
		if (Strings.isNullOrEmpty(uri)) return false;
		return getSupportedURIs().stream()
			.filter(uri::equalsIgnoreCase)
			.findAny()
			.isPresent();
	}
	
	@Override
	public CodeSystem getCodeSystem(Path codeSystemPath) {
		String repositoryId = codeSystemPath.getParent().toString();
		String shortName = codeSystemPath.getFileName().toString();
		CodeSystemEntry codeSystemEntry = CodeSystemRequests.prepareGetCodeSystem(shortName).build(repositoryId).execute(getBus()).getSync();
		return createCodeSystemBuilder(codeSystemEntry).build();
	}
	
	@Override
	public final CodeSystem getCodeSystem(String codeSystemUri) {
		if (!isSupported(codeSystemUri)) {
			throw new BadRequestException("Code system with URI %s is not supported by this provider %s.", codeSystemUri, this.getClass().getSimpleName());
		}
		return getCodeSystems()
				.stream()
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Could not find any code systems for %s.", codeSystemUri));
	}
	
	@Override
	public Collection<CodeSystem> getCodeSystems() {
		return CodeSystemRequests.prepareSearchCodeSystem()
				.all()
				.build(repositoryId)
				.execute(getBus())
				.then(codeSystems -> {
					return codeSystems.stream()
						.map(this::createCodeSystemBuilder)
						.map(Builder::build)
						.collect(Collectors.toList());
				})
				.getSync();
	}
	
	/**
	 * Returns the designated FHIR Uri for the given code system
	 * @return
	 */
	protected abstract Uri getFhirUri();
	
	/**
	 * Creates a FHIR {@link CodeSystem} from a {@link CodeSystemEntry}
	 * @param codeSystemEntry
	 * @return FHIR Code system
	 */
	protected final Builder createCodeSystemBuilder(final CodeSystemEntry codeSystemEntry) {
		
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(codeSystemEntry.getOrgLink())
			.value(codeSystemEntry.getOid())
			.build();
		
		String id = codeSystemEntry.getRepositoryUuid() + "/" + codeSystemEntry.getShortName();
		
		final Builder builder = CodeSystem.builder(id)
			.identifier(identifier)
			.language(getLanguageCode(codeSystemEntry.getLanguage()))
			.name(codeSystemEntry.getShortName())
			.narrative(NarrativeStatus.ADDITIONAL, codeSystemEntry.getCitation())
			.publisher(codeSystemEntry.getOrgLink())
			.status(PublicationStatus.ACTIVE)
			.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
			.title(codeSystemEntry.getName())
			.description(codeSystemEntry.getCitation())
			.url(getFhirUri())
			.content(CodeSystemContentMode.NOT_PRESENT)
			.count(getCount());
		
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
		getSupportedConceptProperties().stream()
			.map(SupportedConceptProperty::builder)
			.map(SupportedConceptProperty.Builder::build)
			.forEach(builder::addProperty);
		
		return builder;
	}
	
	protected Collection<Concept> getConcepts(CodeSystemEntry codeSystemEntry) {
		return Collections.emptySet();
	}

	protected abstract int getCount();

	@Override
	public SubsumptionResult subsumes(SubsumptionRequest subsumptionRequest) {
		final String version = subsumptionRequest.getVersion();
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
	 * Returns all ancestors up to the terminology's root component (in terms of Snow Owl, this means {@link IComponent#ROOT_ID}).
	 * 
	 * @param branchPath
	 * @param componentId
	 * @return
	 */
	protected abstract Set<String> fetchAncestors(String branchPath, String componentId);

	/**
	 * Subclasses may override this method to provide additional properties supported by this FHIR provider.
	 * @return the supported properties
	 */
	protected Collection<ConceptProperties> getSupportedConceptProperties() {
		return Collections.emptySet();
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
		final Set<String> supportedCodes = getSupportedConceptProperties().stream().map(ConceptProperties::getCodeValue).collect(Collectors.toSet());
		if (!supportedCodes.containsAll(properties)) {
			throw new BadRequestException("Unrecognized properties '%s.'", Arrays.toString(properties.toArray()));
		}
	}
	
	
}
