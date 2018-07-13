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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.CodeSystemApiProvider;
import com.b2international.snowowl.fhir.core.ICodeSystemApiProvider;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.codesystem.IConceptProperty;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedCodeSystemRequestProperties;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptGetRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.fhir.codesystems.CoreSnomedConceptProperties;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Provider for the SNOMED CT FHIR support
 * @since 6.4
 * @see ICodeSystemApiProvider
 * @see CodeSystemApiProvider
 */
public final class SnomedCodeSystemApiProvider extends CodeSystemApiProvider {

	private static final String URI_BASE = "http://snomed.info";
	
	private static final Uri FHIR_URI = new Uri(URI_BASE + "/sct");
	
	private static final Path SNOMED_INT_PATH = Paths.get(SnomedDatastoreActivator.REPOSITORY_UUID, SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
	
	private static final Set<String> SUPPORTED_URIS = ImmutableSet.of(
		SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME,
		SnomedTerminologyComponentConstants.SNOMED_INT_LINK,
		FHIR_URI.getUriValue()
	);
	
	public SnomedCodeSystemApiProvider() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID);
	}
	
	@Override
	protected Collection<IConceptProperty> getSupportedConceptProperties() {
		
		// what should be the locale here? Likely we need to add the config locale as well
		final List<ExtendedLocale> locales = newArrayList(ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference());
		locales.add(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US));
		
		final ImmutableList.Builder<IConceptProperty> properties = ImmutableList.builder();
		
		// add basic properties
		properties.add(CoreSnomedConceptProperties.INACTIVE); 
		properties.add(CoreSnomedConceptProperties.MODULE_ID); 
		properties.add(CoreSnomedConceptProperties.EFFECTIVE_TIME); 
		properties.add(CoreSnomedConceptProperties.SUFFICIENTLY_DEFINED); 
		properties.add(CommonConceptProperties.CHILD); 
		properties.add(CommonConceptProperties.PARENT); 
		
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
				return IConceptProperty.Dynamic.valueCode(URI_BASE + "/id", displayName, type.getId());
			})
			.forEach(properties::add);
		
		return properties.build();
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
			.setExpand(String.format("descriptions(expand(type(expand(pt())))),pt()%s%s", expandDescendants, expandAncestors))
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)));
		
		return req.build(repositoryId(), branchPath)
			.execute(getBus())
			.then(concept -> mapToLookupResult(concept, lookup))
			.getSync();
	}
	
	private LookupResult mapToLookupResult(SnomedConcept concept, LookupRequest lookupRequest) {
		
		final LookupResult.Builder resultBuilder = LookupResult.builder();
		
		setBaseProperties(lookupRequest, resultBuilder, SnomedTerminologyComponentConstants.SNOMED_NAME, lookupRequest.getVersion(), getPreferredTermOrId(concept));
		
		//add terms as designations
		if (lookupRequest.isPropertyRequested(SupportedCodeSystemRequestProperties.DESIGNATION)) {
				
			String languageCode = lookupRequest.getDisplayLanguage() != null ? lookupRequest.getDisplayLanguage() : "en-GB";
				for (SnomedDescription description : concept.getDescriptions()) {
						
					Coding coding = Coding.builder()
						.system(FHIR_URI.getUriValue())
						.code(description.getTypeId())
						.display(description.getType().getPt().getTerm())
						.build();
						
					resultBuilder.addDesignation(Designation.builder()
						.languageCode(languageCode)
						.use(coding)
						.value(description.getTerm())
						.build());
				}
		}
				
		// add basic SNOMED properties
		if (lookupRequest.isPropertyRequested(CoreSnomedConceptProperties.INACTIVE)) {
			resultBuilder.addProperty(CoreSnomedConceptProperties.INACTIVE.propertyOf(!concept.isActive()));
		}
		
		if (lookupRequest.isPropertyRequested(CoreSnomedConceptProperties.MODULE_ID)) {
			resultBuilder.addProperty(CoreSnomedConceptProperties.MODULE_ID.propertyOf(concept.getModuleId()));
		}
		
		if (lookupRequest.isPropertyRequested(CoreSnomedConceptProperties.SUFFICIENTLY_DEFINED)) {
			resultBuilder.addProperty(CoreSnomedConceptProperties.SUFFICIENTLY_DEFINED.propertyOf(concept.getDefinitionStatus() == DefinitionStatus.FULLY_DEFINED));
		}
		
		if (lookupRequest.isPropertyRequested(CoreSnomedConceptProperties.EFFECTIVE_TIME)) {
			resultBuilder.addProperty(CoreSnomedConceptProperties.EFFECTIVE_TIME.propertyOf(EffectiveTimes.format(concept.getEffectiveTime(), DateFormats.SHORT)));
		}
		
		//Optionally requested properties
		boolean requestedChild = lookupRequest.containsProperty(CommonConceptProperties.CHILD.getCodeValue());
		boolean requestedParent = lookupRequest.containsProperty(CommonConceptProperties.PARENT.getCodeValue());
		
		if (requestedChild && concept.getDescendants() != null) {
			for (SnomedConcept child : concept.getDescendants()) {
				resultBuilder.addProperty(CommonConceptProperties.CHILD.propertyOf(child.getId(), getPreferredTermOrId(child)));
			}
		}
		
		if (requestedParent && concept.getAncestors() != null) {
			for (SnomedConcept parent : concept.getAncestors()) {
				resultBuilder.addProperty(CommonConceptProperties.PARENT.propertyOf(parent.getId(), getPreferredTermOrId(parent)));
			}
		}
		
		//Relationship target properties
		Collection<String> properties = lookupRequest.getProperties();
		Set<String> relationshipTypeIds = properties.stream()
			.filter(p -> p.startsWith("http://snomed.info/id/"))
			.map(p -> p.substring(p.lastIndexOf('/') + 1, p.length()))
			.collect(Collectors.toSet());
		
		String branchPath = getBranchPath(lookupRequest.getVersion());
		
		SnomedRequests.prepareSearchRelationship()
			.all()
			.filterByActive(true)
			.filterByCharacteristicType(CharacteristicType.INFERRED_RELATIONSHIP.getConceptId())
			.filterBySource(concept.getId())
			.filterByType(relationshipTypeIds)
			.build(repositoryId(), branchPath)
			.execute(getBus())
			.then(rels -> {
				rels.forEach(r -> {
					Property property = Property.builder()
						.code(r.getTypeId())
						.valueCode(r.getDestinationId())
						.build();
					resultBuilder.addProperty(property);
				});
				return null;
			})
			.getSync();
		
		return resultBuilder.build();
	}
	
	@Override
	protected Set<String> fetchAncestors(String branchPath, String componentId) {
		return SnomedConcept.GET_ANCESTORS.apply(SnomedRequests.prepareGetConcept(componentId)
				.build(repositoryId(), branchPath)
				.execute(getBus())
				.getSync());
	}
	
	@Override
	protected int getCount() {
		return SnomedRequests.prepareSearchConcept().setLimit(0)
			.build(repositoryId(), getBranchPath(null))
			.execute(getBus()).getSync().getTotal();
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
	protected Uri getFhirUri(CodeSystemEntry codeSystemEntry, CodeSystemVersionEntry codeSystemVersion) {
		
		StringBuilder sb = new StringBuilder(FHIR_URI.getUriValue());
		
		if (codeSystemVersion != null) {
			//TODO: edition module should come here
			//sb.append("/");
			//sb.append(moduleId);
			sb.append("/version/");
			Date effectiveDate = new Date(codeSystemVersion.getEffectiveDate());
			sb.append(EffectiveTimes.format(effectiveDate, DateFormats.SHORT));
		} 
		
		return new Uri(sb.toString());
	}
	
	private String getPreferredTermOrId(SnomedConcept concept) {
		return concept.getPt() == null ? concept.getId() : concept.getPt().getTerm();
	}
	
}
