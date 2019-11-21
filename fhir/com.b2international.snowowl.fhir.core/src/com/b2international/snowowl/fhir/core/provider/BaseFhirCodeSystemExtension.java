/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
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

/**
 * FHIR provider base class.
 * 
 * @since 7.0
 */
public abstract class BaseFhirCodeSystemExtension implements FhirCodeSystemExtension {
	
	private Collection<IConceptProperty> supportedProperties;

	/**
	 * Subclasses may override this method to provide additional properties supported by this FHIR provider.
	 * @return
	 */
	protected Collection<IConceptProperty> getSupportedConceptProperties() {
		return Collections.emptySet();
	}

	@Override
	public CodeSystem createFhirCodeSystem(com.b2international.snowowl.datastore.CodeSystem codeSystem, CodeSystemVersionEntry version) {
		return createCodeSystemBuilder(codeSystem, version)
				.build();
	}
	
	/**
	 * Creates a FHIR {@link CodeSystem} from a {@link com.b2international.snowowl.datastore.CodeSystem} and optional {@link CodeSystemVersionEntry}.
	 * @param codeSystem
	 * @param version
	 * @return FHIR Code system
	 */
	protected final Builder createCodeSystemBuilder(final com.b2international.snowowl.datastore.CodeSystem codeSystem, final CodeSystemVersionEntry version) {
		
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(codeSystem.getOrgLink())
			.value(codeSystem.getOid())
			.build();
		
		String id = getId(codeSystem, version);
		
		final Builder builder = CodeSystem.builder(id)
			.toolingId(getToolingId())
			.identifier(identifier)
			.language(FhirCodeSystemExtension.getLanguageCode(codeSystem.getLanguage()))
			.name(codeSystem.getShortName())
			.narrative(NarrativeStatus.ADDITIONAL, "<div>"+ codeSystem.getCitation() + "</div>")
			.publisher(codeSystem.getOrgLink())
			.status(PublicationStatus.ACTIVE)
			.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
			.title(codeSystem.getName())
			.description(codeSystem.getCitation())
			.url((String) null)
//			getFhirUri(codeSystemEntry, codeSystemVersion)
			.content(getCodeSystemContentMode())
			.count(getCount(version));
		
		if (version !=null) {
			builder.version(version.getVersionId());
			
			Meta meta = Meta.builder()
				.lastUpdated(Instant.builder().instant(version.getLatestUpdateDate()).build())
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
	private String getId(com.b2international.snowowl.datastore.CodeSystem codeSystemEntry, CodeSystemVersionEntry codeSystemVersion) {
		//in theory there should always be at least one version present
		if (codeSystemVersion != null) {
			return codeSystemVersion.getRepositoryUuid() + ":" + codeSystemVersion.getPath();
		} else {
			return codeSystemEntry.getRepositoryId() + ":" + codeSystemEntry.getBranchPath();
		}
	}

	protected Collection<Concept> getConcepts(com.b2international.snowowl.datastore.CodeSystem codeSystemEntry) {
		return Collections.emptySet();
	}

	protected abstract int getCount(CodeSystemVersionEntry codeSystemVersion);

	@Override
	public SubsumptionResult subsumes(BranchContext context, SubsumptionRequest subsumptionRequest) {
//		final String version = getVersion(subsumptionRequest);
//		final String branchPath = getBranchPath(version);
		
		String codeA = null;
		String codeB = null;
		if (subsumptionRequest.getCodeA() != null && subsumptionRequest.getCodeB() != null) {
			codeA = subsumptionRequest.getCodeA();
			codeB = subsumptionRequest.getCodeB();
		} else {
			codeA = subsumptionRequest.getCodingA().getCodeValue();
			codeB = subsumptionRequest.getCodingB().getCodeValue();
		}
		
		final Set<String> ancestorsA = fetchAncestors(context, codeA);
		final Set<String> ancestorsB = fetchAncestors(context, codeB);
		
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
	
//	/**
//	 * Returns the version information from the request
//	 * @param subsumptionRequest 
//	 * @return version string
//	 */
//	protected String getVersion(SubsumptionRequest subsumptionRequest) {
//		
//		String version = subsumptionRequest.getVersion();
//
//		//get the latest version
//		if (version == null) {
//			Optional<CodeSystemVersionEntry> optionalVersion = CodeSystemRequests.prepareSearchCodeSystemVersion()
//				.one()
//				.filterByCodeSystemShortName(getCodeSystemShortName())
//				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
//				.build(getRepositoryId())
//				.execute(getBus())
//				.getSync()
//				.first();
//				
//			if (optionalVersion.isPresent()) {
//				return optionalVersion.get().getVersionId();
//			} else {
//				//never been versioned
//				return null;
//			}
//		}
//		
//		return version;
//	}
	
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
	 * @param context
	 * @param componentId
	 * @return
	 */
	protected abstract Set<String> fetchAncestors(BranchContext context, String componentId);

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

}
