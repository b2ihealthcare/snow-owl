/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Provider;

import bak.pcj.set.LongSet;

/**
 * Reference set hierarchy browser service for the SNOMED&nbsp;CT ontology.
 * @see AbstractClientRefSetBrowser
 */
@Client
public class SnomedClientRefSetBrowser extends AbstractClientRefSetBrowser<SnomedRefSetIndexEntry, SnomedConceptIndexEntry, String> {

	private final Provider<SnomedClientTerminologyBrowser> browser;
	private final IEventBus bus;
	private final Provider<LanguageSetting> languageSetting;

	/**
	 * Creates a new service instance based on the wrapped server side reference set browser service.
	 * @param wrappedBrowser the wrapped server side service.
	 */
	public SnomedClientRefSetBrowser(final SnomedRefSetBrowser wrapperService, final Provider<SnomedClientTerminologyBrowser> browser, final IEventBus bus, final Provider<LanguageSetting> languageSetting) {
		super(wrapperService);
		this.browser = browser;
		this.bus = bus;
		this.languageSetting = languageSetting;
	}
	
	protected final List<ExtendedLocale> getLocales() {
		return languageSetting.get().getLanguagePreference();
	}
	
	@Override
	protected EPackage getEPackage() {
		return SnomedRefSetPackage.eINSTANCE;
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getRootConcepts() {
		final Iterable<String> refSetTypeConceptIds = FluentIterable.from(SnomedRefSetUtil.getTypesForUI()).transform(new Function<SnomedRefSetType, String>() {
			@Override
			public String apply(SnomedRefSetType input) {
				return SnomedRefSetUtil.getConceptId(input);
			}
		});
		return browser.get().getComponents(refSetTypeConceptIds);
	}
	
	@Override
	public SnomedRefSetIndexEntry getRefSet(String refSetId) {
		try {
			final SnomedReferenceSet refset = SnomedRequests
					.prepareGetReferenceSet()
					.setComponentId(refSetId)
					.setLocales(getLocales())
					.build(getBranchPath().getPath())
					.executeSync(bus);
			
			final SnomedConceptIndexEntry concept = browser.get().getConcept(refSetId);
			return SnomedRefSetIndexEntry.builder(refset).label(concept.getLabel()).build();
		} catch (NotFoundException e) {
			return null;
		}
	}
	
	@Override
	public Iterable<SnomedRefSetIndexEntry> getRefsSets() {
		final List<SnomedReferenceSet> refSets = SnomedRequests.prepareSearchRefSet()
				.all()
				.setLocales(getLocales())
				.build(getBranchPath().getPath())
				.executeSync(bus)
				.getItems();

		final Set<String> matchingRefSetIds = FluentIterable.from(refSets).transform(IComponent.ID_FUNCTION).toSet();
		final Iterable<SnomedConceptIndexEntry> identifierConcepts = getComponents(matchingRefSetIds);
		final ImmutableMap<String, SnomedConceptIndexEntry> identifierConceptMap = FluentIterable.from(identifierConcepts)
				.uniqueIndex(new Function<SnomedConceptIndexEntry, String>() {
					@Override
					public String apply(SnomedConceptIndexEntry input) {
						return input.getId();
					}
				});

		return FluentIterable.from(refSets).transform(new Function<SnomedReferenceSet, SnomedRefSetIndexEntry>() {
			@Override
			public SnomedRefSetIndexEntry apply(SnomedReferenceSet input) {
				final String label = identifierConceptMap.containsKey(input.getId()) ? identifierConceptMap.get(input.getId()).getLabel()
						: input.getId();
				return SnomedRefSetIndexEntry.builder(input).label(label).build();
			}
		}).toList();
	}

	@Override
	public Iterable<SnomedConceptIndexEntry> getComponents(final Iterable<String> ids) {
		final List<ISnomedConcept> concepts = SnomedRequests.prepareSearchConcept()
				.all()
				.setComponentIds(Lists.newArrayList(ids))
				.setLocales(getLocales())
				.setExpand("pt()")
				.build(getBranchPath().getPath())
				.executeSync(bus)
				.getItems();

		return SnomedConceptIndexEntry.fromConcepts(concepts);
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getMemberConcepts(final String refsetId) {
		
		SnomedReferenceSet referenceSet = SnomedRequests.prepareGetReferenceSet()
			.setComponentId(refsetId)
			.build(getBranchPath().getPath())
			.executeSync(bus);
		
		//only concept referenced components are supported
		if (!referenceSet.getReferencedComponentType().equals(SnomedTerminologyComponentConstants.CONCEPT)) {
			return Sets.newHashSet();
		}
		
		//fetch the members
		SnomedReferenceSetMembers members = SnomedRequests.prepareSearchMember()
			.all()
			.filterByRefSet(refsetId)
			.setExpand("referencedComponent()")
			.build(getBranchPath().getPath())
			.executeSync(bus);
			
		Set<ISnomedConcept> referencedConcepts = FluentIterable.from(members).transform(new Function<SnomedReferenceSetMember, ISnomedConcept>() {

			@Override
			public ISnomedConcept apply(SnomedReferenceSetMember member) {
				return (ISnomedConcept) member.getReferencedComponent();
			}
		}).toSet();
		return SnomedConceptIndexEntry.fromConcepts(referencedConcepts);
	}
	
	/**
	 * Returns with the reference set identifier concept IDs of all {@link SnomedRefSetType#SIMPLE simple} and 
	 * {@link SnomedRefSetType#ATTRIBUTE_VALUE attribute value} reference sets where the SNOMED&nbsp;CT concept 
	 * given by its unique ID is referenced by an active reference set member.
	 * <p>The following method will return with an empty collection if:
	 * <ul>
	 * <li>the concept ID is not a valid SNOMED&nbsp;CT concept ID,</li>
	 * <li>or the concept is referenced with a retired reference set member,</li>
	 * <li>or the referenced SNOMED&nbsp;CT concept is retired,</li>
	 * <li>or the container reference set is neither {@link SnomedRefSetType#SIMPLE simple} nor 
	 * {@link SnomedRefSetType#ATTRIBUTE_VALUE attribute value} reference set type.</li>
	 * </ul>
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @return a collection of container reference set IDs.
	 */
	public Collection<String> getContainerRefSetIds(final String conceptId) {
		
		//if not a concept ID, we do not invoke server side service.
		if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER != 
				SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(conceptId)) {
			
			return Collections.emptySet();
			
		}
		
		return getWrapperService().getContainerRefSetIds(getBranchPath(), conceptId);
	}
	
	
	/**
	 * Returns with the reference set identifier concept IDs of all {@link SnomedRefSetType#SIMPLE_MAP simple map} 
	 * reference sets where the SNOMED&nbsp;CT concept  given by its unique ID is referenced by an active reference set member.
	 * <p>The following method will return with an empty collection if:
	 * <ul>
	 * <li>the concept ID is not a valid SNOMED&nbsp;CT concept ID,</li>
	 * <li>or the concept is referenced with a retired reference set member,</li>
	 * <li>or the referenced SNOMED&nbsp;CT concept is retired,</li>
	 * <li>or the container reference set is *NOT* {@link SnomedRefSetType#SIMPLE_MAP simple map} reference set type.</li>
	 * </ul>
	 * @return a collection of container simple map reference set IDs.
	 */
	public Collection<String> getContainerMappingRefSetIds(final String conceptId) {
		
		//if not a concept ID, we do not invoke server side service.
		if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER != 
				SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(conceptId)) {
			
			return Collections.emptySet();
			
		}
				
		return getWrapperService().getContainerMappingRefSetIds(getBranchPath(), conceptId);
		
	}
	
	/**
	 * Returns {@code true} only and if only a SNOMED CT reference set, identified by it unique reference set identifier concept ID,
	 * has at least one active reference set member referencing to a SNOMED&nbsp;CT concept given by its unique concept ID. Otherwise,
	 * returns with {@code false}.
	 * @param identifierConceptId the reference set identifier concept ID.
	 * @param conceptId the unique concept ID. 
	 * @return {@code true} if the reference set has at least one active member referencing to the specified concept, otherwise returns with {@code false}.
	 * @deprecated Use {@link SnomedRequests#prepareSearchMember()}.
	 */
	public boolean isActiveMemberOf(final long identifierConceptId, final long conceptId) {
		return getWrapperService().isActiveMemberOf(getBranchPath(), identifierConceptId, conceptId);
	}
	
	/**
	 * Returns with the unique storage keys (CDO ID) of the published SNOMED&nbsp;CT module dependency reference set members specified
	 * with their module or referenced component ID.
	 * 
	 * @param branchPath 
	 * 		the branch path.
	 * @param id 
	 * 		the module or referenced component ID of the module dependency member. 
	 * @return 
	 * 		a collection of storage keys where the module dependency refset member is published and the module or referenced component
	 * 		ID equals to the ID in the parameter. 
	 */
	public LongSet getPublishedModuleDependencyMembers(final String id) {
		return getWrapperService().getPublishedModuleDependencyMembers(getBranchPath(), id);
	}
	
	/**
	 * Returns with a collection of active mapping reference set members which are establish mapping between the given source and target and contained by the given reference set.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getMappings(final String mappingRefSetId, final String sourceId, final String targetId) {
		return getWrapperService().getMappings(getBranchPath(), mappingRefSetId, sourceId, targetId);
	}
	
	/**
	 * Returns with all reference sets.
	 * @return a collection of all existing reference sets.
	 * @deprecated Use {@link SnomedRequests#prepareSearchRefSet()}.
	 */
	public Collection<SnomedRefSetIndexEntry> getAllReferenceSets() {
		return getWrapperService().getAllReferenceSets(getBranchPath());
	}
	
	/**
	 * Returns with all active reference set members referring the given SNOMED&nbsp;CT concept.
	 * @param conceptId the unique concept ID.
	 * @return a collection of active members referring to the given concept.
	 * @deprecated Use {@link SnomedRequests#prepareSearchMember()}.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getActiveReferringMembers(final String conceptId) {
		return getWrapperService().getActiveReferringMembers(getBranchPath(), conceptId);
	}

	/**
	 * Returns with all (including the inactive ones) reference set members referring the given SNOMED&nbsp;CT concept.
	 * @param conceptId the unique concept ID.
	 * @return a collection of all members referring to the given concept.
	 * @deprecated Use {@link SnomedRequests#prepareSearchMember()}.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getReferringMembers(final String conceptId) {
		return getWrapperService().getReferringMembers(getBranchPath(), conceptId);
	}
	
	/**
	 * Returns with all (including the inactive ones) reference set members of a given reference set.
	 * @param referenceSetId the reference set identifier concept ID.
	 * @return a collection of all reference set members of a reference set.
	 * @deprecated Use {@link SnomedRequests#prepareSearchMember()}.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getMembers(final String referenceSetId) {
		return getWrapperService().getMembers(getBranchPath(), referenceSetId);
	}
	
	/**
	 * Returns with all active reference set members of a given reference set.
	 * @param referenceSetId the reference set identifier concept ID.
	 * @return a collection of active reference set members of a reference set.
	 * @deprecated Use {@link SnomedRequests#prepareSearchMember()}.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getActiveMembers(final String referenceSetId) {
		return getWrapperService().getActiveMembers(getBranchPath(), referenceSetId);
	}
	
	/**
	 * Returns with {@code true} if the given mapping reference set has at least one active member referencing from the given source to the given target.
	 * <br>Otherwise {@code false}.  
	 */
	public boolean hasMapping(final String mappingRefSetId, final String sourceId, final String targetId) {
		return getWrapperService().hasMapping(getBranchPath(), mappingRefSetId, sourceId, targetId);
	}
	
	/**
	 * Returns with all mapping type reference set where the map source is the given concept.
	 * @param conceptId the concept ID as the map source. (Referenced component.)
	 * @return a collection of active reference set member where the map source is the given concept. 
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getMappingMembers(final String conceptId) {
		return getWrapperService().getMappingMembers(getBranchPath(), conceptId);
	}
	
	/**
	 * Returns with all active mapping type reference set members from a reference set where the map target is the given component.
	 * @param mapTarget the map target component ID.
	 * @param mappingRefSetId the identifier concept ID of the mapping reference set.
	 * @return a collection of active reference set members from a particular reference set where the map target is the given component.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getMembersForMapTarget(final String mapTarget, final String mappingRefSetId) {
		return getWrapperService().getMembersForMapTarget(getBranchPath(), mapTarget, mappingRefSetId);
	}
	
	/**
	 * Returns with a collection of ESCG queries associated with the query type reference set given with 
	 * its reference set identifier concept ID argument.
	 * <p>This method returns with an empty collection, if the reference set does not exist with the given ID on the specified 
	 * branch, or the reference set is not a query type or the reference set does not have any members.
	 * @param branchPath the branch path.
	 * @param refSetId the query type reference set ID.
	 * @return a collection of queries associated with the query type reference set.
	 */
	public Collection<String> getAllQueries(final String refSetId) {
		return getWrapperService().getAllQueries(getBranchPath(), checkNotNull(refSetId, "refSetId"));
	}
	
	/**
	 * Returns with a map of active mappings from a give mapping type reference set. The keys are the map source component IDs
	 * and the values are a collection of map target component IDs.
	 * @param refSetId the mapping type reference set ID.
	 * @return a map of mapping between sources and targets. Sources are unique component IDs and the values are a collection 
	 * of map target component IDs.
	 */
	public Map<String, Collection<String>> getMapppings(final String refSetId) {
		return getWrapperService().getMapppings(getBranchPath(), refSetId);
	}
	
	@Override
	public Map<String, Boolean> exist(Collection<String> componentIds) {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.AbstractClientRefSetBrowser#getWrapperService()
	 */
	@Override
	public SnomedRefSetBrowser getWrapperService() {
		return (SnomedRefSetBrowser) super.getWrapperService();
	}
	
}