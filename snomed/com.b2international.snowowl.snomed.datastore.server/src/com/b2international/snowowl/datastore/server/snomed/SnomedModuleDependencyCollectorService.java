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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.b2international.snowowl.datastore.server.snomed.ModuleCollectorConfigurationThreadLocal.getConfiguration;
import static com.b2international.snowowl.datastore.server.snomed.ModuleCollectorConfigurationThreadLocal.reset;
import static com.b2international.snowowl.datastore.server.snomed.ModuleCollectorConfigurationThreadLocal.setConfiguration;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Stopwatch.createStarted;
import static com.google.common.collect.HashMultimap.create;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Long.parseLong;
import static java.util.UUID.randomUUID;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.view.CDOView;
import org.slf4j.Logger;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

/**
 * Server side stateless singleton service for resolving SNOMED&nbsp;CT module dependencies.
 * This service is responsible for creating new module dependency reference set members as the
 * part of the versioning process.
 *
 */
public enum SnomedModuleDependencyCollectorService {

	/**Singleton service.*/
	INSTANCE;
	
	private static final Logger LOGGER = getLogger(SnomedModuleDependencyCollectorService.class);
	
	private static final long PRIMITIVE = parseLong(Concepts.PRIMITIVE);
	private static final long FULLY_DEFINED = parseLong(Concepts.FULLY_DEFINED);
	private static final long MODULE_ROOT = parseLong(Concepts.MODULE_ROOT);
	
	public Collection<SnomedModuleDependencyRefSetMember> collectModuleMembers(Map<String, SnomedComponent> unpublishedComponentsById) {
		return Collections.emptySet();
	}
	
	/**
	 * Returns with a collection of {@link SnomedModuleDependencyRefSetMember module dependency members}
	 * representing the current module dependency state of the SNOMED&nbsp;CT ontology. The list might contain
	 * new reference set members.
	 * @param view the CDO view. The view is not closed by this method. It's the callers responsibility to clean up the CDO view.
	 * @param unpublishedStorageKeys a collection of unpublished component storage keys.
	 * @return a collection of module dependency members.
	 * @throws IOException 
	 */
	public Collection<SnomedModuleDependencyRefSetMember> collectModuleMembers(final RevisionSearcher searcher, final CDOView view, final LongCollection unpublishedStorageKeys) throws IOException {
	
		final Stopwatch stopwatch = createStarted();
		final Collection<SnomedModuleDependencyRefSetMember> members = newHashSet();
		
		try {
			
			LOGGER.info("Initializing resources to resolve module dependencies... [1 of 6]");
			setConfiguration(initConfiguration(check(view), checkNotNull(unpublishedStorageKeys, "unpublishedStorageKeys")));

			LOGGER.info("Processing unversioned concept... [2 of 6]");
			tryCreateMembersForConceptChanges(searcher);

			LOGGER.info("Processing unversioned descriptions... [3 of 6]");
			tryCreateMembersForDescriptionChanges(searcher);
			
			LOGGER.info("Processing unversioned relationships... [4 of 6]");
			tryCreateMembersForRelationshipChanges(searcher);
			
			LOGGER.info("Processing unversioned concrete domains... [5 of 6]");
			tryCreateMembersForDataTypeChanges(searcher);
			
			LOGGER.info("Processing unversioned module concepts... [6 of 6]");
			tryCreateMembersForNewModules(searcher);
			
			members.addAll(getMembers());
			
		} finally {
			reset();
		}
		
		LOGGER.info("SNOMED CT module dependencies have been successfully resolved. Found " + members.size() + " modules. [" + stopwatch + "]");
		return members;
		
	}

	private ModuleCollectorConfiguration initConfiguration(final CDOView view, final LongCollection unpublishedStorageKeys) {
	
		final IBranchPath branchPath = createPath(view);
		final ModuleCollectorConfiguration configuration = new ModuleCollectorConfiguration();
		
		configuration.setView(view);
		configuration.setBranchPath(branchPath);
		configuration.setConceptModuleMapping(getConceptModuleMapping(branchPath));
		configuration.setUnpublishedStorageKeys(unpublishedStorageKeys);
		configuration.setExistingModules(getExistingModules(branchPath));
		configuration.setModuleMapping(getModuleMapping(configuration.getExistingModules()));
		final SnomedRegularRefSet moduleRefSet = getModuleRefSet(view);
		configuration.setModuleDependencyRefSet(moduleRefSet);
		configuration.getMembers().addAll(getModuleMembers(moduleRefSet));
		
		return configuration;
	}

	private void tryCreateMembersForConceptChanges(RevisionSearcher searcher) throws IOException {
		for (final SnomedConceptDocument concept : searcher.get(SnomedConceptDocument.class, LongSets.toSet(getUnpublishedStorageKeys()))) {
			final long conceptModuleId = Long.parseLong(concept.getModuleId());
			tryCreateMember(conceptModuleId, getConceptModuleMapping().get(PRIMITIVE));
			tryCreateMember(conceptModuleId, getConceptModuleMapping().get(FULLY_DEFINED));
		}
		
	}

	private void tryCreateMembersForDescriptionChanges(RevisionSearcher searcher) throws IOException {
		final Iterable<SnomedDescriptionIndexEntry> unpublisedDescriptions = searcher.get(SnomedDescriptionIndexEntry.class, LongSets.toSet(getUnpublishedStorageKeys()));
		
		for (SnomedDescriptionIndexEntry description : unpublisedDescriptions) {
			final long moduleId = Long.parseLong(description.getModuleId());
			final long conceptId = Long.parseLong(description.getConceptId());
			final long typeId = Long.parseLong(description.getTypeId());
			final long caseSignificanceId = Long.parseLong(description.getCaseSignificanceId());
			tryCreateMember(moduleId, getConceptModuleMapping().get(conceptId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(typeId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(caseSignificanceId));
		}
	}

	private void tryCreateMembersForRelationshipChanges(RevisionSearcher searcher) throws IOException {
		final Iterable<SnomedRelationshipIndexEntry> unpublishedRelationships = searcher.get(SnomedRelationshipIndexEntry.class, LongSets.toSet(getUnpublishedStorageKeys()));
		for (SnomedRelationshipIndexEntry relationship : unpublishedRelationships) {
			final long moduleId = Long.parseLong(relationship.getModuleId());
			final long sourceId = Long.parseLong(relationship.getSourceId());
			final long typeId = Long.parseLong(relationship.getTypeId());
			final long destinationId = Long.parseLong(relationship.getDestinationId());
			final long modifierId = Long.parseLong(relationship.getModifierId());
			final long characteristicTypeId = Long.parseLong(relationship.getCharacteristicTypeId());
			tryCreateMember(moduleId, getConceptModuleMapping().get(characteristicTypeId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(typeId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(sourceId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(destinationId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(modifierId));
		}
	}

	private void tryCreateMembersForDataTypeChanges(RevisionSearcher searcher) throws IOException {
		final Iterable<SnomedRefSetMemberIndexEntry> members = searcher.get(SnomedRefSetMemberIndexEntry.class, LongSets.toSet(getUnpublishedStorageKeys()));
		for (SnomedRefSetMemberIndexEntry member : members) {
			// member should depend on the reference set
			final long memberModuleId = Long.parseLong(member.getModuleId());
			final long refSetId = Long.parseLong(member.getReferenceSetId());
			tryCreateMember(memberModuleId, getConceptModuleMapping().get(refSetId));
			
			// handle all possible RF2 member fields which refers to a component
			tryCreateMemberIfNotEmpty(memberModuleId, member.getAcceptabilityId());
			tryCreateMemberIfNotEmpty(memberModuleId, member.getCharacteristicTypeId());
			tryCreateMemberIfNotEmpty(memberModuleId, member.getCorrelationId());
			tryCreateMemberIfNotEmpty(memberModuleId, member.getDescriptionFormat());
			tryCreateMemberIfNotEmpty(memberModuleId, member.getOperatorId());
			tryCreateMemberIfNotEmpty(memberModuleId, member.getValueId());
			tryCreateMemberIfNotEmpty(memberModuleId, member.getUnitId());
			tryCreateMemberIfNotEmpty(memberModuleId, member.getTargetComponent());
			tryCreateMemberIfNotEmpty(memberModuleId, member.getMapCategoryId());
		}
	}

	private void tryCreateMemberIfNotEmpty(long sourceModule, String dependsOnConceptId) {
		if (!Strings.isNullOrEmpty(dependsOnConceptId)) {
			tryCreateMember(sourceModule, getConceptModuleMapping().get(Long.parseLong(dependsOnConceptId)));
		}
	}

	private void tryCreateMembersForNewModules(final RevisionSearcher searcher) throws IOException {
		final Set<String> allModuleIds = LongSets.toStringSet(getAllModuleConceptIds()); 
		for (final SnomedRelationshipIndexEntry isARelationship : getInboundIsARelationships(searcher, allModuleIds)) {
			if (isComponentAffected(isARelationship.getStorageKey())) {
				
				final Concept concept = new SnomedConceptLookupService().getComponent(isARelationship.getSourceId(), getView());
				final Concept module = concept.getModule();
				
				tryCreateMember(module.getId(), isARelationship.getModuleId());
				
				for (final Description description : concept.getDescriptions()) {
					tryCreateMember(module, description.getModule());
					tryCreateMember(module, description.getType().getModule());
					tryCreateMember(module, description.getCaseSignificance().getModule());
				}
				
				for (final Relationship relationship : concept.getOutboundRelationships()) {
					tryCreateMember(module, relationship.getModule());
					tryCreateMember(module, relationship.getCharacteristicType().getModule());
					tryCreateMember(module, relationship.getDestination().getModule());
					tryCreateMember(module, relationship.getModifier().getModule());
					tryCreateMember(module, relationship.getType().getModule());
				}
				
			}
		}
	}

	private void tryCreateMember(final long sourceModuleId, final long targetModuleId) {
		
		if (sourceModuleId != targetModuleId && !isKnownModuleDependency(sourceModuleId, targetModuleId)) {
			
			final String targetId = Long.toString(targetModuleId);
			final SnomedModuleDependencyRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedModuleDependencyRefSetMember();
			member.setUuid(randomUUID().toString());
			member.setActive(true);
			member.setReleased(false);
			member.setModuleId(Long.toString(sourceModuleId));
			member.setReferencedComponentId(targetId);
			member.setRefSet(getModuleDependencyRefSet());
			member.setEffectiveTime(getExistingModuleEffectiveTime(targetId));
			
			getMembers().add(member);
			getModuleMapping().put(sourceModuleId, targetModuleId);
			
		}
		
	}
	
	private void tryCreateMember(final Concept sourceConcept, final Concept targetConcept) {
		tryCreateMember(parseLong(sourceConcept.getId()), parseLong(targetConcept.getId()));
	}
	
	private void tryCreateMember(final String sourceModuleId, final String targetModuleId) {
		tryCreateMember(parseLong(sourceModuleId), parseLong(targetModuleId));
	}

	private Iterable<SnomedRelationshipIndexEntry> getInboundIsARelationships(final RevisionSearcher searcher, final Set<String> destinationIds) throws IOException {
		final Query<SnomedRelationshipIndexEntry> query = Query.builder(SnomedRelationshipIndexEntry.class)
				.selectAll()
				.where(Expressions.builder()
						.must(SnomedRelationshipIndexEntry.Expressions.destinationIds(destinationIds))
						.must(SnomedRelationshipIndexEntry.Expressions.typeId(Concepts.IS_A))
						.build())
				.limit(Integer.MAX_VALUE)
				.build();
		return searcher.search(query);
	}

	private boolean isComponentAffected(final long storageKey) {
		return getUnpublishedStorageKeys().contains(storageKey);
	}
	
	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return getServiceForClass(SnomedTerminologyBrowser.class);
	}

	private LongSet getAllModuleConceptIds() {
		final LongSet moduleConceptIds = getTerminologyBrowser().getAllSubTypeIds(getBranchPath(), MODULE_ROOT);
		moduleConceptIds.add(MODULE_ROOT);
		return moduleConceptIds;
	}
	
	private boolean isKnownModuleDependency(final long sourceModuleId, final long targetModuleId) {
		return getModuleMapping().get(sourceModuleId).contains(targetModuleId);
	}

	private Date getExistingModuleEffectiveTime(final String expectedModuleId) {
		for (final SnomedModuleDependencyRefSetMemberFragment module : getExistingModules()) {
			final Date sourceEffectiveTime = module.getSourceEffectiveTime();
			final String moduleId = module.getModuleId();
			if (null != sourceEffectiveTime && moduleId.equals(expectedModuleId)) {
				return sourceEffectiveTime;
			}
			final Date targetEffectiveTime = module.getTargetEffectiveTime();
			if (null != targetEffectiveTime && moduleId.equals(expectedModuleId)) {
				return targetEffectiveTime;
			}
		}
		return null;
	}

	private HashSet<SnomedModuleDependencyRefSetMember> getModuleMembers(final SnomedRegularRefSet moduleRefSet) {
		return newHashSet(filter(moduleRefSet.getMembers(), SnomedModuleDependencyRefSetMember.class));
	}

	private SnomedRegularRefSet getModuleRefSet(final CDOView view) {
		return checkNotNull((SnomedRegularRefSet) new SnomedRefSetLookupService().getComponent(REFSET_MODULE_DEPENDENCY_TYPE, view), "Missing module reference set %s", REFSET_MODULE_DEPENDENCY_TYPE);
	}
	
	private Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules(final IBranchPath branchPath) {
		return getComponentService().getExistingModules(branchPath);
	}
	
	private Multimap<Long, Long> getModuleMapping(final Collection<SnomedModuleDependencyRefSetMemberFragment> existingModules) {
		final Multimap<Long, Long> moduleMapping = create();
		for (final SnomedModuleDependencyRefSetMemberFragment module : existingModules) {
			moduleMapping.put(parseLong(module.getModuleId()), parseLong(module.getReferencedComponentId()));
		}
		return moduleMapping;
	}

	private LongKeyLongMap getConceptModuleMapping(final IBranchPath branchPath) {
		return SnomedRequests.prepareSearchConcept()
				.all()
				.build(branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedConcepts, LongKeyLongMap>() {
					@Override
					public LongKeyLongMap apply(SnomedConcepts input) {
						final LongKeyLongMap result = PrimitiveMaps.newLongKeyLongOpenHashMapWithExpectedSize(input.getTotal());
						for (ISnomedConcept concept : input) {
							result.put(Long.parseLong(concept.getId()), Long.parseLong(concept.getModuleId()));
						}
						return result;
					}
				})
				.getSync();
	}

	private ISnomedComponentService getComponentService() {
		return getServiceForClass(ISnomedComponentService.class);
	}
	
	private LongCollection getUnpublishedStorageKeys() {
		return getConfiguration().getUnpublishedStorageKeys();
	}

	private CDOView getView() {
		return getConfiguration().getView();
	}
	
	private IBranchPath getBranchPath() {
		return getConfiguration().getBranchPath();
	}

	private LongKeyLongMap getConceptModuleMapping() {
		return getConfiguration().getConceptModuleMapping();
	}

	private Collection<SnomedModuleDependencyRefSetMember> getMembers() {
		return getConfiguration().getMembers();
	}
	
	private Multimap<Long, Long> getModuleMapping() {
		return getConfiguration().getModuleMapping();
	}
	
	private SnomedRefSet getModuleDependencyRefSet() {
		return getConfiguration().getModuleDependencyRefSet();
	}
	
	private Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules() {
		return getConfiguration().getExistingModules();
	}
	
}