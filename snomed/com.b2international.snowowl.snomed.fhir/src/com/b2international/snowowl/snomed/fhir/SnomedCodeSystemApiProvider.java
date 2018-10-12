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

import java.util.Collection;
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
import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.codesystem.Filters;
import com.b2international.snowowl.fhir.core.model.codesystem.IConceptProperty;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedCodeSystemRequestProperties;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.b2international.snowowl.fhir.core.model.subsumption.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.provider.CodeSystemApiProvider;
import com.b2international.snowowl.fhir.core.provider.ICodeSystemApiProvider;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptGetRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.fhir.SnomedUri.Builder;
import com.b2international.snowowl.snomed.fhir.codesystems.CoreSnomedConceptProperties;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Provider for the SNOMED CT FHIR support
 * @since 6.4
 * @see ICodeSystemApiProvider
 * @see CodeSystemApiProvider
 */
public final class SnomedCodeSystemApiProvider extends CodeSystemApiProvider {

	private static final String URI_BASE = "http://snomed.info/sct";
	
	private static final Set<String> SUPPORTED_URIS = ImmutableSet.of(
		SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME,
		SnomedTerminologyComponentConstants.SNOMED_INT_LINK,
		SnomedUri.SNOMED_BASE_URI_STRING
	);
	
	public SnomedCodeSystemApiProvider() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID);
	}
	
	@Override
	public LookupResult lookup(LookupRequest lookup) {
		
		SnomedUri snomedUri = SnomedUri.fromUriString(lookup.getSystem(), "CodeSystem$lookup.system");
		
		validateVersion(snomedUri, lookup.getVersion());
		
		CodeSystemVersionEntry codeSystemVersion = getCodeSystemVersion(snomedUri.getVersionTag());
		String branchPath = codeSystemVersion.getPath();
		String versionString = EffectiveTimes.format(codeSystemVersion.getEffectiveDate(), DateFormats.SHORT);
		
		validateRequestedProperties(lookup);
		
		boolean requestedChild = lookup.containsProperty(CommonConceptProperties.CHILD.getCodeValue());
		boolean requestedParent = lookup.containsProperty(CommonConceptProperties.PARENT.getCodeValue());
		
		String expandDescendants = requestedChild ? ",descendants(direct:true,expand(pt()))" : "";
		String expandAncestors = requestedParent ? ",ancestors(direct:true,expand(pt()))" : "";
		String displayLanguage = lookup.getDisplayLanguage() != null ? lookup.getDisplayLanguage() : "en-GB";
		
		SnomedConceptGetRequestBuilder req = SnomedRequests.prepareGetConcept(lookup.getCode())
			.setExpand(String.format("descriptions(expand(type(expand(pt())))),pt()%s%s", expandDescendants, expandAncestors))
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)));
		
		return req.build(getRepositoryId(), branchPath)
			.execute(getBus())
			.then(concept -> mapToLookupResult(concept, lookup, versionString))
			.getSync();
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
			.build(getRepositoryId(), Branch.MAIN_PATH)
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
	public boolean isSupported(LogicalId logicalId) {
		return logicalId.getRepositoryId().startsWith(SnomedDatastoreActivator.REPOSITORY_UUID);
	}
	
	@Override
	public final boolean isSupported(String uri) {
		if (Strings.isNullOrEmpty(uri)) return false;
		
		//supported URI perfect match
		boolean foundInList = getSupportedURIs().stream()
			.filter(uri::equalsIgnoreCase)
			.findAny()
			.isPresent();
		
		//extension and version is part of the URI
		boolean extensionUri = uri.startsWith(SnomedUri.SNOMED_BASE_URI_STRING);
		
		boolean logicalId = uri.startsWith(SnomedDatastoreActivator.REPOSITORY_UUID);
		
		return foundInList || extensionUri || logicalId;
	}
	
	@Override
	protected Set<String> fetchAncestors(String branchPath, String componentId) {
		return SnomedConcept.GET_ANCESTORS.apply(SnomedRequests.prepareGetConcept(componentId)
			.build(getRepositoryId(), branchPath)
			.execute(getBus())
			.getSync());
	}
	
	@Override
	protected int getCount(CodeSystemVersionEntry codeSystemVersion) {
		return SnomedRequests.prepareSearchConcept().setLimit(0)
			.build(getRepositoryId(), codeSystemVersion.getPath())
			.execute(getBus()).getSync().getTotal();
	}
	
	@Override
	protected Collection<Filter> getSupportedFilters() {
		return ImmutableList.of(
				Filters.EXPRESSION_FILTER, 
				Filters.EXPRESSIONS_FILTER,
				Filters.IS_A_FILTER, 
				Filters.REFSET_MEMBER_OF);
	}
	
	@Override
	public Collection<String> getSupportedURIs() {
		return SUPPORTED_URIS;
	}
	
	@Override
	protected String getCodeSystemShortName() {
		return SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
	}
	
	@Override
	protected Uri getFhirUri(CodeSystemEntry codeSystemEntry, CodeSystemVersionEntry codeSystemVersion) {
		
		//TODO: edition module should come here
		Builder builder = SnomedUri.builder();
		
		if (codeSystemVersion != null) {
			builder.version(codeSystemVersion.getEffectiveDate());
		} 
		return builder.build().toUri();
	}
	
	/**
	 * SNOMED versions are embedded in the system URI
	 * @param subsumptionRequest 
	 * @return version string
	 */
	protected String getVersion(SubsumptionRequest subsumptionRequest) {
		SnomedUri snomedUri = SnomedUri.fromUriString(subsumptionRequest.getSystem(), "CodeSystem$subsumes.system");
		validateVersion(snomedUri, subsumptionRequest.getVersion());
		return getCodeSystemVersion(snomedUri.getVersionTag()).getVersionId();
	}
	
	private LookupResult mapToLookupResult(SnomedConcept concept, LookupRequest lookupRequest, String version) {
		
		final LookupResult.Builder resultBuilder = LookupResult.builder();
		
		setBaseProperties(lookupRequest, resultBuilder, SnomedTerminologyComponentConstants.SNOMED_NAME, version, getPreferredTermOrId(concept));
		
		//add terms as designations
		if (lookupRequest.isPropertyRequested(SupportedCodeSystemRequestProperties.DESIGNATION)) {
				
			String languageCode = lookupRequest.getDisplayLanguage() != null ? lookupRequest.getDisplayLanguage() : "en-GB";
				for (SnomedDescription description : concept.getDescriptions()) {
						
					Coding coding = Coding.builder()
						.system(SnomedUri.SNOMED_BASE_URI_STRING)
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
			.build(getRepositoryId(), branchPath)
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
	
	/*
	 * If version tag is supplied, it should be the same as the one defined in the SNOMED CT URI
	 */
	private void validateVersion(SnomedUri snomedUri, String version) {
		
		if (version != null) {
			if (snomedUri.getVersionTag() == null) {
				throw new BadRequestException(String.format("Version is not specified in the URI [%s], while it is set in the request [%s]", snomedUri.toString(), version), "LookupRequest.version");
			} else if (!snomedUri.getVersionTag().equals(version)) {
				throw new BadRequestException(String.format("Version specified in the URI [%s] does not match the version set in the request [%s]", snomedUri.toString(), version), "LookupRequest.version");
			}
		}
	}

	private String getPreferredTermOrId(SnomedConcept concept) {
		return concept.getPt() == null ? concept.getId() : concept.getPt().getTerm();
	}
	
}
