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
import static com.google.common.collect.Maps.newHashMap;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.SnomedNamespaceAndModuleAssigner;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractResponse.Type;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationSettings;
import com.b2international.snowowl.snomed.reasoner.classification.EquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.GetEquivalentConceptsResponse;
import com.b2international.snowowl.snomed.reasoner.classification.GetResultResponse;
import com.b2international.snowowl.snomed.reasoner.classification.GetResultResponseChanges;
import com.b2international.snowowl.snomed.reasoner.classification.PersistChangesResponse;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;
import com.b2international.snowowl.snomed.reasoner.classification.UnsatisfiableSet;
import com.b2international.snowowl.snomed.reasoner.classification.entry.AbstractChangeEntry.Nature;
import com.b2international.snowowl.snomed.reasoner.classification.entry.ChangeConcept;
import com.b2international.snowowl.snomed.reasoner.classification.entry.ConceptConcreteDomainChangeEntry;
import com.b2international.snowowl.snomed.reasoner.classification.entry.ConcreteDomainElement;
import com.b2international.snowowl.snomed.reasoner.classification.entry.IConcreteDomainChangeEntry;
import com.b2international.snowowl.snomed.reasoner.classification.entry.RelationshipChangeEntry;
import com.b2international.snowowl.snomed.reasoner.classification.entry.RelationshipConcreteDomainChangeEntry;
import com.b2international.snowowl.snomed.reasoner.model.LongConcepts;
import com.b2international.snowowl.snomed.reasoner.preferences.IReasonerPreferencesService;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.reasoner.server.normalform.ConceptConcreteDomainNormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.server.normalform.RelationshipNormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.server.request.SnomedReasonerRequests;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;

/**
 * Manages reasoners that operate on the OWL representation of a SNOMED&nbsp;CT repository branch path. 
 */
public class SnomedReasonerServerService extends CollectingService<Reasoner, ClassificationSettings> implements SnomedReasonerService, IDisposableService {

	private static final String NAMESPACE_ASSIGNER_EXTENSION = "com.b2international.snowowl.snomed.reasoner.server.namespaceAssigner";

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
	private final Cache<String, InitialReasonerTaxonomyBuilder> taxonomyBuilderRegistry;
	private final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner;
	private final boolean concreteDomainSupportEnabled;
	
	public SnomedReasonerServerService(int maximumReasonerCount, int maximumTaxonomiesToKeep) {
		super(maximumReasonerCount);

		this.taxonomyResultRegistry = CacheBuilder.newBuilder().maximumSize(maximumTaxonomiesToKeep).build();
		this.taxonomyBuilderRegistry = CacheBuilder.newBuilder().maximumSize(maximumTaxonomiesToKeep).build();
		this.namespaceAndModuleAssigner = checkNotNull(getNamespaceModuleAssigner(), "Could not find a namespace and module allocator in the extension registry");
		this.concreteDomainSupportEnabled = ApplicationContext.getInstance().getServiceChecked(SnomedCoreConfiguration.class).isConcreteDomainSupported();
		
		LOGGER.info("Initialized SNOMED CT reasoner server with maximum of {} reasoner(s) instances and {} result(s) to keep.", maximumReasonerCount, maximumTaxonomiesToKeep);
		LOGGER.info("Reasoner service will use the {} class for relationship/concrete domain namespace and module assignement.", namespaceAndModuleAssigner.getClass().getSimpleName());
	}

	public void registerListeners() {
		getApplicationContext().addServiceListener(ICDOConnectionManager.class, invalidationListenerRegistrator);
		getApplicationContext().addServiceListener(IReasonerPreferencesService.class, preferencesListenerRegistrator);
	}

	@Override
	protected void onDispose() {
		taxonomyResultRegistry.invalidateAll();
		taxonomyResultRegistry.cleanUp();
		taxonomyBuilderRegistry.invalidateAll();
		taxonomyBuilderRegistry.cleanUp();
		getApplicationContext().removeServiceListener(IReasonerPreferencesService.class, preferencesListenerRegistrator);
		getApplicationContext().removeServiceListener(ICDOConnectionManager.class, invalidationListenerRegistrator);
		super.onDispose();
	}

	private static ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
	
	private static IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

	private static SnomedNamespaceAndModuleAssigner getNamespaceModuleAssigner() {
		return Extensions.getFirstPriorityExtension(NAMESPACE_ASSIGNER_EXTENSION, SnomedNamespaceAndModuleAssigner.class);
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
	
	public void registerTaxonomyBuilder(String classificationId, InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder) {
		taxonomyBuilderRegistry.put(classificationId, checkNotNull(reasonerTaxonomyBuilder));
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
		InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder = taxonomyBuilderRegistry.getIfPresent(classificationId);
		
		ImmutableList.Builder<RelationshipChangeEntry> relationshipBuilder = ImmutableList.builder();
		ImmutableList.Builder<IConcreteDomainChangeEntry> concreteDomainBuilder = ImmutableList.builder();
		Map<Long, ChangeConcept> changeConceptCache = newHashMap();
				
		new RelationshipNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder)
			.collectNormalFormChanges(null, new OntologyChangeProcessor<StatementFragment>() {
			@Override 
			protected void handleAddedSubject(final String conceptId, final StatementFragment addedSubject) {
				registerEntry(Long.valueOf(conceptId), addedSubject, Nature.INFERRED);
			}
			
			@Override 
			protected void handleRemovedSubject(final String conceptId, final StatementFragment removedSubject) {
				registerEntry(Long.valueOf(conceptId), removedSubject, Nature.REDUNDANT);
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
				if (concreteDomainSupportEnabled) {
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
			}
		});
		
		if (concreteDomainSupportEnabled) {
			new ConceptConcreteDomainNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder)
			.collectNormalFormChanges(null, new OntologyChangeProcessor<ConcreteDomainFragment>() {
				@Override 
				protected void handleAddedSubject(final String conceptId, final ConcreteDomainFragment addedSubject) {
					registerEntry(Long.valueOf(conceptId), addedSubject, Nature.INFERRED);
				}
				
				@Override 
				protected void handleRemovedSubject(final String conceptId, final ConcreteDomainFragment removedSubject) {
					registerEntry(Long.valueOf(conceptId), removedSubject, Nature.REDUNDANT);
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
		}
		
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
		List<AbstractEquivalenceSet> results = newArrayList();
		
		LongSet unsatisfiableConceptIds = taxonomy.getUnsatisfiableConceptIds();
		if (!unsatisfiableConceptIds.isEmpty()) {
			results.add(new UnsatisfiableSet(LongSets.toStringList(unsatisfiableConceptIds)));
		}
		
		List<LongSet> equivalentConceptSets = taxonomy.getEquivalentConceptIds();
		for (LongSet equivalentConceptSet : equivalentConceptSets) {
			List<String> equivalentIds = LongSets.toStringList(equivalentConceptSet);
			String suggestedConcept = equivalentIds.remove(0);
			results.add(new EquivalenceSet(suggestedConcept, equivalentIds));
		}
		
		return results;
	}

	@Override 
	public PersistChangesResponse persistChanges(String classificationId, String userId) {
		ReasonerTaxonomy taxonomy = taxonomyResultRegistry.getIfPresent(classificationId);
		InitialReasonerTaxonomyBuilder taxonomyBuilder = taxonomyBuilderRegistry.getIfPresent(classificationId);
		
		if (null == taxonomy) {
			return new PersistChangesResponse(Type.NOT_AVAILABLE);
		}
		
		if (taxonomy.isStale()) {
			return new PersistChangesResponse(Type.STALE);
		}
		
		try {
			return doPersistChanges(classificationId, taxonomy, taxonomyBuilder, userId);
		} catch (OperationLockException | InterruptedException e) {
			LOGGER.error("Cannot persist classification changes.", e);
			return new PersistChangesResponse(Type.NOT_AVAILABLE);
		}
	}

	private PersistChangesResponse doPersistChanges(String classificationId, ReasonerTaxonomy taxonomy, InitialReasonerTaxonomyBuilder taxonomyBuilder, String userId) throws OperationLockException, InterruptedException {
		
		String persistJobId = SnomedReasonerRequests.preparePersistChanges()
				.setClassificationId(classificationId)
				.setTaxonomy(taxonomy)
				.setTaxonomyBuilder(taxonomyBuilder)
				.setUserId(userId)
				.setNamespaceAndModuleAssigner(namespaceAndModuleAssigner)
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
		taxonomyBuilderRegistry.asMap().remove(classificationId);
	}
	
}
