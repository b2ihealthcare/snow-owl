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
package com.b2international.snowowl.snomed.fhir;

import static com.google.common.collect.Lists.newArrayList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.FhirProvider;
import com.b2international.snowowl.fhir.core.IFhirProvider;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.ConceptProperties;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptGetRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.fhir.codesystems.CoreSnomedConceptProperties;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Provider for the SNOMED CT FHIR support
 * @since 6.4
 * @see IFhirProvider
 * @see FhirProvider
 */
public final class SnomedFhirProvider extends FhirProvider {

	private static final String URI_BASE = "http://snomed.info";
	private static final Uri FHIR_URI = new Uri(URI_BASE + "/sct");
	private static final Path SNOMED_INT_PATH = Paths.get(SnomedDatastoreActivator.REPOSITORY_UUID, SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
	private static final Set<String> SUPPORTED_URIS = ImmutableSet.of(
		SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME,
		SnomedTerminologyComponentConstants.SNOMED_INT_LINK,
		FHIR_URI.getUriValue()
	);
	
	private final Collection<ConceptProperties> supportedProperties;
	
	public SnomedFhirProvider() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID);

		// what should be the locale here? Likely we need to add the config locale as well
		final List<ExtendedLocale> locales = newArrayList(ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference());
		locales.add(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US));
 
		final ImmutableList.Builder<ConceptProperties> supportedProperties = ImmutableList.builder();
		
		// add basic properties
		supportedProperties.add(
			CoreSnomedConceptProperties.INACTIVE, 
			CoreSnomedConceptProperties.MODULE_ID, 
			CoreSnomedConceptProperties.SUFFICIENTLY_DEFINED, 
			CommonConceptProperties.CHILD, 
			CommonConceptProperties.PARENT
		);
		
		// fetch available relationship types and register them as supported concept property
		SnomedRequests.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.filterByAncestor(Concepts.CONCEPT_MODEL_ATTRIBUTE)
			.setExpand("pt()")
			.setLocales(locales)
			.build(repositoryId(), Branch.MAIN_PATH)
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync()
			.stream()
			.map(type -> {
				final String displayName = type.getPt() == null ? "N/A" : type.getPt().getTerm();
				return ConceptProperties.Dynamic.valueCode(URI_BASE + "/id", displayName, type.getId());
			})
			.forEach(supportedProperties::add);
		
		this.supportedProperties = supportedProperties.build();
	}
	
	@Override
	public boolean isSupported(Path path) {
		return SNOMED_INT_PATH.equals(path);
	}

	@Override
	public LookupResult lookup(LookupRequest lookup) {
		String version = lookup.getVersion();
		String branchPath = getBranchPath(version);
		
		validateRequestedProperties(lookup);
		
		boolean requestedChild = lookup.containsProperty(CommonConceptProperties.CHILD.getCodeValue());
		boolean requestedParent = lookup.containsProperty(CommonConceptProperties.PARENT.getCodeValue());
		
		String expandDescendants = requestedChild ? ",descendants(direct:true,expand(pt()))" : "";
		String expandAncestors = requestedParent ? ",ancestors(direct:true,expand(pt()))" : "";
		String displayLanguage = lookup.getDisplayLanguage() != null ? lookup.getDisplayLanguage() : "en-GB";
		
		SnomedConceptGetRequestBuilder req = SnomedRequests.prepareGetConcept(lookup.getCode())
				.setExpand(String.format("descriptions(),pt()%s%s", expandDescendants, expandAncestors))
				.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)));
		
		return req.build(repositoryId(), branchPath)
			.execute(getBus())
			.then(concept -> mapToLookupResult(concept, lookup))
			.getSync();
		
	}
	
	/* Value set related methods */
	@Override
	public Collection<ValueSet> getValueSets() {
		
		//TODO: what to do with the language? Where do i get the locale from the request? 
		String displayLanguage = "en-us";
		
		return SnomedRequests.prepareSearchRefSet()
			.all()
			.filterByType(SnomedRefSetType.SIMPLE)
			.build(repositoryId(), IBranchPath.MAIN_BRANCH)
			.execute(getBus())
			.then(refsets -> {
				return refsets.stream()
					.map(r -> createValueSetBuilder(r, displayLanguage))
					.map(ValueSet.Builder::build)
					.collect(Collectors.toList());
			})
			.getSync();
	}
	
	@Override
	public ValueSet getValueSet(Path valueSetPath) {
		
		//TODO: what to do with the language? Where do i get the locale from the request? 
		String displayLanguage = "en-us";
		
		String referenceSetId = valueSetPath.getFileName().toString();
		
		return SnomedRequests.prepareSearchRefSet()
				.filterById(referenceSetId)
				.filterByType(SnomedRefSetType.SIMPLE)
				.build(repositoryId(), IBranchPath.MAIN_BRANCH)
				.execute(getBus())
				.then(refsets -> {
					return refsets.stream()
						.map(r -> createValueSetBuilder(r, displayLanguage))
						.map(ValueSet.Builder::build)
						.collect(Collectors.toList());
				})
				.getSync()
				.stream()
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Active value set", valueSetPath.toString()));
		
	}
	
	private ValueSet.Builder createValueSetBuilder(final SnomedReferenceSet referenceSet, final String displayLanguage) {
		
		String referenceSetId = referenceSet.getId();
		Identifier identifier = new Identifier(IdentifierUse.OFFICIAL, null, getFhirUri(), referenceSetId);
		String id = repositoryId() + "/" + referenceSetId;
		
		SnomedConcept refsetConcept = SnomedRequests.prepareGetConcept(referenceSetId)
			.setExpand("pt()")
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
			.build(repositoryId(), IBranchPath.MAIN_BRANCH)
			.execute(getBus())
			.getSync();
			
		
		return ValueSet.builder(id)
			.identifier(identifier)
			.language(displayLanguage)
			.url(getFhirUri())
			.status(referenceSet.isActive() ? PublicationStatus.ACTIVE : PublicationStatus.RETIRED)
			.title(refsetConcept.getPt().getTerm());
	}

	private LookupResult mapToLookupResult(SnomedConcept concept, LookupRequest lookup) {
		boolean requestedChild = lookup.containsProperty(CommonConceptProperties.CHILD.getCodeValue());
		boolean requestedParent = lookup.containsProperty(CommonConceptProperties.PARENT.getCodeValue());
		
		final LookupResult.Builder result = LookupResult.builder()
				.name(SnomedTerminologyComponentConstants.SNOMED_NAME)
				.version(lookup.getVersion())
				.display(getPreferredTermOrId(concept));

		// add basic properties
		result.addProperty(CoreSnomedConceptProperties.INACTIVE.propertyOf(!concept.isActive(), null));
		result.addProperty(CoreSnomedConceptProperties.MODULE_ID.propertyOf(concept.getModuleId(), null));
		result.addProperty(CoreSnomedConceptProperties.SUFFICIENTLY_DEFINED.propertyOf(concept.getDefinitionStatus() == DefinitionStatus.FULLY_DEFINED, null));
		
		// add remaining terms as designations
		for (SnomedDescription description : concept.getDescriptions()) {
			final String preferredTermId = concept.getPt() == null ? "" : concept.getPt().getId();
			if (!description.getId().equals(preferredTermId)) {
				result.addDesignation(Designation.builder()
					.value(description.getTerm())
					.build());
			}
		}
		
		if (requestedChild && concept.getDescendants() != null) {
			for (SnomedConcept child : concept.getDescendants()) {
				result.addProperty(CommonConceptProperties.CHILD.propertyOf(child.getId(), getPreferredTermOrId(child)));
			}
		}
		
		if (requestedParent && concept.getAncestors() != null) {
			for (SnomedConcept parent : concept.getAncestors()) {
				result.addProperty(CommonConceptProperties.PARENT.propertyOf(parent.getId(), getPreferredTermOrId(parent)));
			}
		}
		
		return result.build();
	}

	private String getPreferredTermOrId(SnomedConcept concept) {
		return concept.getPt() == null ? concept.getId() : concept.getPt().getTerm();
	}

	@Override
	protected Collection<ConceptProperties> getSupportedConceptProperties() {
		return supportedProperties;
	}
	
	@Override
	protected Collection<Filter> getSupportedFilters() {
		return ImmutableList.of(Filter.IS_A_FILTER, Filter.EXPRESSION_FILTER, Filter.EXPRESSIONS_FILTER);
	}
	
	@Override
	public Collection<String> getSupportedURIs() {
		return SUPPORTED_URIS;
	}

	@Override
	protected Uri getFhirUri() {
		return FHIR_URI;
	}
	
}
