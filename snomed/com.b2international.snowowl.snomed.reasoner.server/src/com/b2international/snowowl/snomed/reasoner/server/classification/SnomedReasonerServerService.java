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
package com.b2international.snowowl.snomed.reasoner.server.classification;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.classification.*;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractResponse.Type;
import com.b2international.snowowl.snomed.reasoner.classification.entry.*;
import com.b2international.snowowl.snomed.reasoner.classification.entry.AbstractChangeEntry.Nature;
import com.b2international.snowowl.snomed.reasoner.model.LongConcepts;
import com.b2international.snowowl.snomed.reasoner.preferences.IReasonerPreferencesService;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.reasoner.server.normalform.ConceptConcreteDomainNormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.server.normalform.RelationshipNormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.server.request.SnomedReasonerRequests;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

/**
 * Manages reasoners that operate on the OWL representation of a SNOMED&nbsp;CT repository branch path. 
 */
public class SnomedReasonerServerService extends CollectingService<Reasoner, ClassificationSettings> implements SnomedReasonerService, IDisposableService {

	private static final Ordering<SnomedConcept> STORAGE_KEY_ORDERING = Ordering.from(new Comparator<SnomedConcept>() {
		@Override
		public int compare(SnomedConcept o1, SnomedConcept o2) {
			return Longs.compare(o1.getStorageKey(), o2.getStorageKey());
		}
	}).nullsLast();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedReasonerServerService.class);
	
	private final IListener invalidationListener = new IListener() {
		@Override 
		public void notifyEvent(IEvent event) {
			if (!(event instanceof CDOSessionInvalidationEvent)) {
				return;
			}
			
			CDOSessionInvalidationEvent invalidationEvent = (CDOSessionInvalidationEvent) event;
			CDOBranch cdoBranch = invalidationEvent.getBranch();
			IBranchPath branchPath = BranchPathUtils.createPath(cdoBranch);
			setStale(branchPath);
		}
	};
	
	private final IServiceChangeListener<ICDOConnectionManager> invalidationListenerRegistrator = new IServiceChangeListener<ICDOConnectionManager>() {
		@Override 
		public void serviceChanged(ICDOConnectionManager oldService, ICDOConnectionManager newService) {
			if (hasSnomedSession(oldService))  {
				getSnomedSession(oldService).removeListener(invalidationListener);
			}
			
			if (hasSnomedSession(newService))  {
				getSnomedSession(newService).addListener(invalidationListener);
			}
		}

		private boolean hasSnomedSession(ICDOConnectionManager connectionManager) {
			return null != connectionManager && null != getSnomedConnection(connectionManager) && null != getSnomedSession(connectionManager);
		}

		private CDONet4jSession getSnomedSession(ICDOConnectionManager connectionManager) {
			return getSnomedConnection(connectionManager).getSession();
		}

		private ICDOConnection getSnomedConnection(ICDOConnectionManager connectionManager) {
			return connectionManager.get(SnomedPackage.eINSTANCE);
		}
	};
	
	private final IListener preferencesListener = new IListener() {
		@Override 
		public void notifyEvent(IEvent event) {
			evictAll();
		}
	};
	
	private final IServiceChangeListener<IReasonerPreferencesService> preferencesListenerRegistrator = new IServiceChangeListener<IReasonerPreferencesService>() {
		@Override 
		public void serviceChanged(IReasonerPreferencesService oldService, IReasonerPreferencesService newService) {
			if (null != oldService)  {
				oldService.removeListener(preferencesListener);
			}
			
			if (null != newService)  {
				newService.addListener(preferencesListener);
			}
		}
	};
	
	private final Cache<String, ReasonerTaxonomy> taxonomyResultRegistry;
	
	public SnomedReasonerServerService(int maximumReasonerCount, int maximumTaxonomiesToKeep) {
		super(maximumReasonerCount);
		this.taxonomyResultRegistry = CacheBuilder.newBuilder().maximumSize(maximumTaxonomiesToKeep).build();
		LOGGER.info("Initialized SNOMED CT reasoner server with maximum of {} reasoner(s) instances and {} saveable taxonomies to keep.", maximumReasonerCount, maximumTaxonomiesToKeep);
	}

	public void registerListeners() {
		getApplicationContext().addServiceListener(ICDOConnectionManager.class, invalidationListenerRegistrator);
		getApplicationContext().addServiceListener(IReasonerPreferencesService.class, preferencesListenerRegistrator);
	}

	@Override
	protected void onDispose() {
		taxonomyResultRegistry.invalidateAll();
		taxonomyResultRegistry.cleanUp();
		getApplicationContext().removeServiceListener(IReasonerPreferencesService.class, preferencesListenerRegistrator);
		getApplicationContext().removeServiceListener(ICDOConnectionManager.class, invalidationListenerRegistrator);
		super.onDispose();
	}

	private static ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
	
	private static RevisionIndex getIndex() {
		return ApplicationContext.getServiceForClass(RepositoryManager.class)
				.get(SnomedDatastoreActivator.REPOSITORY_UUID)
				.service(RevisionIndex.class);
	}
	
	private static IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

	private void setStale(IBranchPath branchPath) {
		for (ReasonerTaxonomy taxonomy : taxonomyResultRegistry.asMap().values()) {
			if (branchPath.equals(taxonomy.getBranchPath())) {
				taxonomy.setStale();
			}
		}
		
		CollectingServiceReference<Reasoner> sharedReference = getSharedServiceReferenceIfExists(branchPath);
		if (null != sharedReference) {
			sharedReference.getService().setStale();
		}
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	protected Reasoner createService(IBranchPath branchPath, boolean shared, ClassificationSettings request) throws Exception {
		LOGGER.info(MessageFormat.format("Creating reasoner for branch path ''{0}''.", branchPath));
		return new Reasoner(request.getReasonerId(), branchPath, shared);
	}

	@Override
	protected void retireService(Reasoner reasoner) throws InterruptedException {
		LOGGER.info(MessageFormat.format("Retiring reasoner for branch path ''{0}''.", reasoner.getBranchPath()));
		reasoner.dispose();
	}
	
	@Override
	protected boolean matchesParams(Reasoner service, ClassificationSettings serviceParams) {
		return service.getReasonerId().equals(serviceParams.getReasonerId());
	}
	
	public void registerResult(String remoteJobId, ReasonerTaxonomy reasonerTaxonomy) {
		taxonomyResultRegistry.put(remoteJobId, reasonerTaxonomy);
	}

	@Override
	public void beginClassification(ClassificationSettings settings) {
		checkNotNull(settings, "Classification settings may not be null.");

		if (null == settings.getReasonerId()) {
			settings.withReasonerId(ApplicationContext.getServiceForClass(IReasonerPreferencesService.class).getSelectedReasonerId());
		}

		SnomedReasonerRequests.prepareClassify()
				.setSettings(settings)
				.buildAsync()
				.execute(getEventBus())
				.getSync();
	}

	@Override 
	public GetResultResponse getResult(String classificationId) {
		
		ReasonerTaxonomy taxonomy = taxonomyResultRegistry.getIfPresent(classificationId);
		
		if (null == taxonomy) {
			return new GetResultResponse(GetResultResponse.Type.NOT_AVAILABLE);
		}
		
		Type responseType = taxonomy.isStale() ? Type.STALE : Type.SUCCESS;
		return new GetResultResponse(responseType, doGetResult(classificationId, taxonomy));  
	}

	private GetResultResponseChanges doGetResult(String classificationId, ReasonerTaxonomy taxonomy) {
		
		IBranchPath branchPath = taxonomy.getBranchPath();
		InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder = getIndex().read(branchPath.getPath(), new RevisionIndexRead<InitialReasonerTaxonomyBuilder>() {
			@Override
			public InitialReasonerTaxonomyBuilder execute(RevisionSearcher searcher) throws IOException {
				return new InitialReasonerTaxonomyBuilder(searcher, InitialReasonerTaxonomyBuilder.Type.REASONER);
			}
		});
		
		ImmutableList.Builder<RelationshipChangeEntry> relationshipBuilder = ImmutableList.builder();
		ImmutableList.Builder<IConcreteDomainChangeEntry> concreteDomainBuilder = ImmutableList.builder();
	
		Map<Long, ChangeConcept> changeConceptCache = newHashMap();
		new RelationshipNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder).collectNormalFormChanges(null, new OntologyChangeProcessor<StatementFragment>() {
			@Override 
			protected void handleAddedSubject(long conceptId, StatementFragment addedSubject) {
				registerEntry(conceptId, addedSubject, Nature.INFERRED);
			}
			
			@Override 
			protected void handleRemovedSubject(long conceptId, StatementFragment removedSubject) {
				registerEntry(conceptId, removedSubject, Nature.REDUNDANT);
			}
	
			private void registerEntry(long conceptId, StatementFragment subject, Nature changeNature) {
				
				ChangeConcept sourceComponent = getOrCreateChangeConcept(changeConceptCache, branchPath, conceptId);
				ChangeConcept typeComponent = getOrCreateChangeConcept(changeConceptCache, branchPath, subject.getTypeId());
				ChangeConcept destinationComponent = getOrCreateChangeConcept(changeConceptCache, branchPath, subject.getDestinationId());
				
				long modifierId = subject.isUniversal() 
						? LongConcepts.UNIVERSAL_RESTRICTION_MODIFIER_ID
						: LongConcepts.EXISTENTIAL_RESTRICTION_MODIFIER_ID;
				
				ChangeConcept modifierComponent = getOrCreateChangeConcept(changeConceptCache, branchPath, modifierId);
				
				RelationshipChangeEntry entry = new RelationshipChangeEntry(
						changeNature, 
						sourceComponent, 
						typeComponent, 
						destinationComponent, 
						subject.getGroup(), 
						subject.getUnionGroup(), 
						modifierComponent, 
						subject.isDestinationNegated());
				
				relationshipBuilder.add(entry);
				
				// look up all CDEs from the original relationship and add them as inferred
				Collection<ConcreteDomainFragment> relationshipConcreteDomainElements = reasonerTaxonomyBuilder.getStatementConcreteDomainFragments(subject.getStatementId());
				
				for (ConcreteDomainFragment concreteDomainElementIndexEntry : relationshipConcreteDomainElements) {
					
					ConcreteDomainElement concreteDomainElement = createConcreteDomainElement(changeConceptCache,
							branchPath, 
							concreteDomainElementIndexEntry);
					
					RelationshipConcreteDomainChangeEntry relationshipConcreteDomainElementEntry = 
							new RelationshipConcreteDomainChangeEntry(
									changeNature, 
									sourceComponent, 
									typeComponent, 
									destinationComponent, 
									concreteDomainElement);
					
					concreteDomainBuilder.add(relationshipConcreteDomainElementEntry);
				}
			}
		});
		
		new ConceptConcreteDomainNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder).collectNormalFormChanges(null, new OntologyChangeProcessor<ConcreteDomainFragment>() {
			@Override 
			protected void handleAddedSubject(long conceptId, ConcreteDomainFragment addedSubject) {
				registerEntry(conceptId, addedSubject, Nature.INFERRED);
			}
			
			@Override 
			protected void handleRemovedSubject(long conceptId, ConcreteDomainFragment removedSubject) {
				registerEntry(conceptId, removedSubject, Nature.REDUNDANT);
			}
	
			private void registerEntry(long conceptId, ConcreteDomainFragment subject, Nature changeNature) {
				ConcreteDomainElement concreteDomainElement = createConcreteDomainElement(changeConceptCache,
						branchPath, 
						subject);
				
				ChangeConcept sourceComponent = getOrCreateChangeConcept(changeConceptCache,
						branchPath, 
						conceptId);
				
				ConceptConcreteDomainChangeEntry responseEntry = new ConceptConcreteDomainChangeEntry(
						changeNature, 
						sourceComponent, 
						concreteDomainElement);
				
				concreteDomainBuilder.add(responseEntry);
			}
		});
		
		List<AbstractEquivalenceSet> equivalentConcepts = doGetEquivalentConcepts(taxonomy);
		GetResultResponseChanges convertedChanges = new GetResultResponseChanges(taxonomy.getElapsedTimeMillis(), 
				equivalentConcepts,
				relationshipBuilder.build(), 
				concreteDomainBuilder.build());
		
		return convertedChanges;
	}

	private ChangeConcept getOrCreateChangeConcept(Map<Long, ChangeConcept> changeConceptCache, IBranchPath branchPath, long id) {
		if (changeConceptCache.containsKey(id)) {
			return changeConceptCache.get(id); 
		} else {
			ChangeConcept concept = SnomedRequests.prepareGetConcept(Long.toString(id))
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.then(new Function<SnomedConcept, ChangeConcept>() {
						@Override
						public ChangeConcept apply(SnomedConcept input) {
							return new ChangeConcept(id, Long.parseLong(input.getIconId()));
						}
					})
					.fail(new Function<Throwable, ChangeConcept>() {
						@Override
						public ChangeConcept apply(Throwable input) {
							return new ChangeConcept(id, Long.parseLong(Concepts.ROOT_CONCEPT));
						}
					})
					.getSync();
			changeConceptCache.put(id, concept);
			return concept;
		}
	}

	private ConcreteDomainElement createConcreteDomainElement(Map<Long, ChangeConcept> changeConceptCache, IBranchPath branchPath, ConcreteDomainFragment fragment) {
		
		ChangeConcept unitConcept = (ConcreteDomainFragment.UNSET_UOM_ID == fragment.getUomId()) 
				? null
				: getOrCreateChangeConcept(changeConceptCache, branchPath, fragment.getUomId());
		
		ConcreteDomainElement concreteDomainElement = new ConcreteDomainElement(
				fragment.getLabel(), 
				fragment.getValue(), 
				unitConcept);
		
		return concreteDomainElement;
	}

	@Override 
	public GetEquivalentConceptsResponse getEquivalentConcepts(String classificationId) {
		
		ReasonerTaxonomy taxonomy = taxonomyResultRegistry.getIfPresent(classificationId);
		
		if (null == taxonomy) {
			return new GetEquivalentConceptsResponse(GetResultResponse.Type.NOT_AVAILABLE);
		}
		
		Type responseType = taxonomy.isStale() ? Type.STALE : Type.SUCCESS;
		return new GetEquivalentConceptsResponse(responseType, doGetEquivalentConcepts(taxonomy));  
	}

	private List<AbstractEquivalenceSet> doGetEquivalentConcepts(ReasonerTaxonomy taxonomy) {
		IBranchPath branchPath = taxonomy.getBranchPath();
		List<AbstractEquivalenceSet> results = newArrayList();
		
		LongSet unsatisfiableConceptIds = taxonomy.getUnsatisfiableConceptIds();
		if (!unsatisfiableConceptIds.isEmpty()) {
			List<SnomedConcept> unsatisfiableEntries = convertIdsToIndexEntries(branchPath, unsatisfiableConceptIds);
			results.add(new UnsatisfiableSet(unsatisfiableEntries));
		}
		
		List<LongSet> equivalentConceptSets = taxonomy.getEquivalentConceptIds();
		for (LongSet equivalentConceptSet : equivalentConceptSets) {
			List<SnomedConcept> equivalentEntries = convertIdsToIndexEntries(branchPath, equivalentConceptSet);
			SnomedConcept suggestedConcept = equivalentEntries.remove(0);
			results.add(new EquivalenceSet(suggestedConcept, equivalentEntries));
		}
		
		return results;
	}

	private List<SnomedConcept> convertIdsToIndexEntries(IBranchPath branchPath, LongSet conceptIds) { 
		Set<String> conceptIdFilter = LongSets.toStringSet(conceptIds);
		SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
				.filterByIds(conceptIdFilter)
				.setLimit(conceptIds.size())
				.setExpand("pt()")
				.setLocales(ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getInstance().getService(IEventBus.class))
				.getSync();
		Map<String, SnomedConcept> existingConcepts = FluentIterable.from(concepts).uniqueIndex(IComponent.ID_FUNCTION);
		List<SnomedConcept> convertedSet = newArrayListWithExpectedSize(conceptIdFilter.size());
		for (String conceptId : conceptIdFilter) {
			SnomedConcept concept = existingConcepts.get(conceptId);
			if (null == concept) {
				SnomedConcept fakeConcept = new SnomedConcept();
				fakeConcept.setId(conceptId);
				fakeConcept.setIconId(Concepts.ROOT_CONCEPT);
				fakeConcept.setModuleId(Concepts.MODULE_ROOT);
				SnomedDescription pt = new SnomedDescription();
				pt.setTerm(conceptId + " (unresolved)");
				fakeConcept.setPt(pt);
				convertedSet.add(fakeConcept);
			} else {
				convertedSet.add(concept);
			}
		}
		
		Collections.sort(convertedSet, STORAGE_KEY_ORDERING);
		return convertedSet;
	}

	@Override 
	public PersistChangesResponse persistChanges(String classificationId, String userId) {
		ReasonerTaxonomy taxonomy = taxonomyResultRegistry.getIfPresent(classificationId);
		
		if (null == taxonomy) {
			return new PersistChangesResponse(Type.NOT_AVAILABLE);
		}
		
		if (taxonomy.isStale()) {
			return new PersistChangesResponse(Type.STALE);
		}
		
		try {
			return doPersistChanges(classificationId, taxonomy, userId);
		} catch (OperationLockException | InterruptedException e) {
			LOGGER.error("Cannot persist classification changes.", e);
			return new PersistChangesResponse(Type.NOT_AVAILABLE);
		}
	}

	private PersistChangesResponse doPersistChanges(String classificationId, ReasonerTaxonomy taxonomy, String userId) throws OperationLockException, InterruptedException {
		
		String persistJobId = SnomedReasonerRequests.preparePersistChanges()
				.setClassificationId(classificationId)
				.setTaxonomy(taxonomy)
				.setUserId(userId)
				.buildAsync()
				.execute(getEventBus())
				.getSync();

		return new PersistChangesResponse(Type.SUCCESS, persistJobId);
	}

	@Override
	public boolean canStartImmediately() {
		return super.hasAvailableServiceReferences();
	}
	
	@Override
	public void removeResult(String classificationId) {
		taxonomyResultRegistry.asMap().remove(classificationId);
	}
	
}
