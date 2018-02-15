/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.b2international.snowowl.datastore.server.snomed.ModuleCollectorConfigurationThreadLocal.getConfiguration;
import static com.b2international.snowowl.datastore.server.snomed.ModuleCollectorConfigurationThreadLocal.reset;
import static com.b2international.snowowl.datastore.server.snomed.ModuleCollectorConfigurationThreadLocal.setConfiguration;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Stopwatch.createStarted;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static java.lang.Long.parseLong;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;
import org.slf4j.Logger;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

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
	
	/**
	 * Returns with a collection of {@link SnomedModuleDependencyRefSetMember module dependency members}
	 * representing the current module dependency state of the SNOMED&nbsp;CT ontology. The list might contain
	 * new reference set members.
	 * @param view the CDO view. The view is not closed by this method. It's the callers responsibility to clean up the CDO view.
	 * @param unpublishedStorageKeys a collection of unpublished component storage keys.
	 * @return a collection of module dependency members.
	 * @throws IOException 
	 */
	public Collection<Pair<String, String>> collectModuleMembers(final RevisionSearcher searcher, final CDOEditingContext context, final LongCollection unpublishedStorageKeys) throws IOException {
	
		final Stopwatch stopwatch = createStarted();
		final Collection<Pair<String, String>> members = Sets.newHashSet();
		
		try {
			
			LOGGER.info("Initializing resources to resolve module dependencies... [1 of 6]");
			setConfiguration(initConfiguration(check(context.getTransaction()), checkNotNull(unpublishedStorageKeys, "unpublishedStorageKeys")));

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
			
			members.addAll(getConfiguration().getMembers());
			
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
		
		return configuration;
	}

	private void tryCreateMembersForConceptChanges(RevisionSearcher searcher) throws IOException {
		for (final SnomedConceptDocument concept : searcher.get(SnomedConceptDocument.class, LongSets.toSet(getUnpublishedStorageKeys()))) {
			final long conceptModuleId = Long.parseLong(concept.getModuleId());
			tryAddModulePair(conceptModuleId, getConceptModuleMapping().get(PRIMITIVE));
			tryAddModulePair(conceptModuleId, getConceptModuleMapping().get(FULLY_DEFINED));
		}
		
	}

	private void tryCreateMembersForDescriptionChanges(RevisionSearcher searcher) throws IOException {
		final Iterable<SnomedDescriptionIndexEntry> unpublisedDescriptions = searcher.get(SnomedDescriptionIndexEntry.class, LongSets.toSet(getUnpublishedStorageKeys()));
		
		for (SnomedDescriptionIndexEntry description : unpublisedDescriptions) {
			final long moduleId = Long.parseLong(description.getModuleId());
			final long conceptId = Long.parseLong(description.getConceptId());
			final long typeId = Long.parseLong(description.getTypeId());
			final long caseSignificanceId = Long.parseLong(description.getCaseSignificanceId());
			tryAddModulePair(moduleId, getConceptModuleMapping().get(conceptId));
			tryAddModulePair(moduleId, getConceptModuleMapping().get(typeId));
			tryAddModulePair(moduleId, getConceptModuleMapping().get(caseSignificanceId));
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
			tryAddModulePair(moduleId, getConceptModuleMapping().get(characteristicTypeId));
			tryAddModulePair(moduleId, getConceptModuleMapping().get(typeId));
			tryAddModulePair(moduleId, getConceptModuleMapping().get(sourceId));
			tryAddModulePair(moduleId, getConceptModuleMapping().get(destinationId));
			tryAddModulePair(moduleId, getConceptModuleMapping().get(modifierId));
		}
	}

	private void tryCreateMembersForDataTypeChanges(RevisionSearcher searcher) throws IOException {
		final Iterable<SnomedRefSetMemberIndexEntry> members = searcher.get(SnomedRefSetMemberIndexEntry.class, LongSets.toSet(getUnpublishedStorageKeys()));
		for (SnomedRefSetMemberIndexEntry member : members) {
			// member should depend on the reference set
			final long memberModuleId = Long.parseLong(member.getModuleId());
			final long refSetId = Long.parseLong(member.getReferenceSetId());
			tryAddModulePair(memberModuleId, getConceptModuleMapping().get(refSetId));
			
			// handle all possible RF2 member fields which refers to a component
			tryAdddModulePairIfNotEmpty(memberModuleId, member.getAcceptabilityId());
			tryAdddModulePairIfNotEmpty(memberModuleId, member.getCharacteristicTypeId());
			tryAdddModulePairIfNotEmpty(memberModuleId, member.getCorrelationId());
			tryAdddModulePairIfNotEmpty(memberModuleId, member.getDescriptionFormat());
			tryAdddModulePairIfNotEmpty(memberModuleId, member.getOperatorId());
			tryAdddModulePairIfNotEmpty(memberModuleId, member.getValueId());
			tryAdddModulePairIfNotEmpty(memberModuleId, member.getUnitId());
			tryAdddModulePairIfNotEmpty(memberModuleId, member.getTargetComponent());
			tryAdddModulePairIfNotEmpty(memberModuleId, member.getMapCategoryId());
		}
	}

	private void tryAdddModulePairIfNotEmpty(long sourceModule, String dependsOnConceptId) {
		if (SnomedIdentifiers.isConceptIdentifier(dependsOnConceptId)) {
			tryAddModulePair(sourceModule, getConceptModuleMapping().get(Long.parseLong(dependsOnConceptId)));
		}
	}

	private void tryCreateMembersForNewModules(final RevisionSearcher searcher) throws IOException {
		final Set<String> allModuleIds = getAllModuleConceptIds(); 
		for (final SnomedRelationshipIndexEntry isARelationship : getInboundIsARelationships(searcher, allModuleIds)) {
			if (isComponentAffected(isARelationship.getStorageKey())) {
				
				final Concept concept = new SnomedConceptLookupService().getComponent(isARelationship.getSourceId(), getView());
				final Concept module = concept.getModule();
				
				tryAddModulePair(module.getId(), isARelationship.getModuleId());
				
				for (final Description description : concept.getDescriptions()) {
					tryAddModulePair(module, description.getModule());
					tryAddModulePair(module, description.getType().getModule());
					tryAddModulePair(module, description.getCaseSignificance().getModule());
				}
				
				for (final Relationship relationship : concept.getOutboundRelationships()) {
					tryAddModulePair(module, relationship.getModule());
					tryAddModulePair(module, relationship.getCharacteristicType().getModule());
					tryAddModulePair(module, relationship.getDestination().getModule());
					tryAddModulePair(module, relationship.getModifier().getModule());
					tryAddModulePair(module, relationship.getType().getModule());
				}
				
			}
		}
	}

	private void tryAddModulePair(final long sourceModuleId, final long targetModuleId) {
		
		if (sourceModuleId != targetModuleId ) {
			Pair<String, String> pair = Tuples.pair(Long.toString(sourceModuleId), Long.toString(targetModuleId));
			getConfiguration().getMembers().add(pair);
		}
		
	}
	
	private void tryAddModulePair(final Concept sourceConcept, final Concept targetConcept) {
		tryAddModulePair(parseLong(sourceConcept.getId()), parseLong(targetConcept.getId()));
	}
	
	private void tryAddModulePair(final String sourceModuleId, final String targetModuleId) {
		tryAddModulePair(parseLong(sourceModuleId), parseLong(targetModuleId));
	}

	private Iterable<SnomedRelationshipIndexEntry> getInboundIsARelationships(final RevisionSearcher searcher, final Set<String> destinationIds) throws IOException {
		final Query<SnomedRelationshipIndexEntry> query = Query.select(SnomedRelationshipIndexEntry.class)
				.where(Expressions.builder()
						.filter(SnomedRelationshipIndexEntry.Expressions.destinationIds(destinationIds))
						.filter(SnomedRelationshipIndexEntry.Expressions.typeId(Concepts.IS_A))
						.build())
				.limit(Integer.MAX_VALUE)
				.build();
		return searcher.search(query);
	}

	private boolean isComponentAffected(final long storageKey) {
		return getUnpublishedStorageKeys().contains(storageKey);
	}
	
	private Set<String> getAllModuleConceptIds() {
		return SnomedRequests.prepareSearchConcept()
				.all()
				.filterByActive(true)
				.filterByAncestor(Concepts.MODULE_ROOT)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranchPath().getPath())
				.execute(getBus())
				.then(new Function<SnomedConcepts, Set<String>>() {
					@Override
					public Set<String> apply(SnomedConcepts input) {
						final Set<String> moduleIds = newHashSetWithExpectedSize(input.getTotal() + 1);
						FluentIterable.from(input).transform(IComponent.ID_FUNCTION).copyInto(moduleIds);
						moduleIds.add(Concepts.MODULE_ROOT);
						return moduleIds;
					}
				})
				.getSync();
	}
	
	private LongKeyLongMap getConceptModuleMapping(final IBranchPath branchPath) {
		return SnomedRequests.prepareSearchConcept()
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.then(new Function<SnomedConcepts, LongKeyLongMap>() {
					@Override
					public LongKeyLongMap apply(SnomedConcepts input) {
						final LongKeyLongMap result = PrimitiveMaps.newLongKeyLongOpenHashMapWithExpectedSize(input.getTotal());
						for (SnomedConcept concept : input) {
							result.put(Long.parseLong(concept.getId()), Long.parseLong(concept.getModuleId()));
						}
						return result;
					}
				})
				.getSync();
	}

	private IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
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

}