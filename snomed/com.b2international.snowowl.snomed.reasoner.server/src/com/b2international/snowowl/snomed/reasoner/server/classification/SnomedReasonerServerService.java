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
package com.b2international.snowowl.snomed.reasoner.server.classification;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.UUID.randomUUID;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.status.SerializableStatus;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.remotejobs.IRemoteJobManager;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils;
import com.b2international.snowowl.datastore.server.remotejobs.BranchExclusiveRule;
import com.b2international.snowowl.datastore.server.remotejobs.RemoteJobResultRegistry;
import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcThreadLocal;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractResponse.Type;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationRequest;
import com.b2international.snowowl.snomed.reasoner.classification.EquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.GetEquivalentConceptsResponse;
import com.b2international.snowowl.snomed.reasoner.classification.GetResultResponse;
import com.b2international.snowowl.snomed.reasoner.classification.GetResultResponseChanges;
import com.b2international.snowowl.snomed.reasoner.classification.PersistChangesResponse;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerServiceUtil;
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
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

/**
 * Manages reasoners that operate on the OWL representation of a SNOMED&nbsp;CT repository branch path. 
 *
 */
public class SnomedReasonerServerService extends CollectingService<Reasoner, ClassificationRequest> implements SnomedReasonerService, IDisposableService {

	private static final Ordering<ISnomedConcept> STORAGE_KEY_ORDERING = Ordering.from(new Comparator<ISnomedConcept>() {
		@Override
		public int compare(ISnomedConcept o1, ISnomedConcept o2) {
			return Longs.compare(o1.getStorageKey(), o2.getStorageKey());
		}
	}).nullsLast();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedReasonerServerService.class);
	
	private final IListener invalidationListener = new IListener() {
		@Override 
		public void notifyEvent(final IEvent event) {
			if (!(event instanceof CDOSessionInvalidationEvent)) {
				return;
			}
			
			final CDOSessionInvalidationEvent invalidationEvent = (CDOSessionInvalidationEvent) event;
			final CDOBranch cdoBranch = invalidationEvent.getBranch();
			final IBranchPath branchPath = BranchPathUtils.createPath(cdoBranch);
			setStale(branchPath);
		}
	};
	
	private final IServiceChangeListener<ICDOConnectionManager> invalidationListenerRegistrator = new IServiceChangeListener<ICDOConnectionManager>() {
		@Override 
		public void serviceChanged(final ICDOConnectionManager oldService, final ICDOConnectionManager newService) {
			if (hasSnomedSession(oldService))  {
				getSnomedSession(oldService).removeListener(invalidationListener);
			}
			
			if (hasSnomedSession(newService))  {
				getSnomedSession(newService).addListener(invalidationListener);
			}
		}

		private boolean hasSnomedSession(final ICDOConnectionManager connectionManager) {
			return null != connectionManager && null != getSnomedConnection(connectionManager) && null != getSnomedSession(connectionManager);
		}

		private CDONet4jSession getSnomedSession(final ICDOConnectionManager connectionManager) {
			return getSnomedConnection(connectionManager).getSession();
		}

		private ICDOConnection getSnomedConnection(final ICDOConnectionManager connectionManager) {
			return connectionManager.get(SnomedPackage.eINSTANCE);
		}
	};
	
	private final IListener preferencesListener = new IListener() {
		@Override 
		public void notifyEvent(final IEvent event) {
			evictAll();
		}
	};
	
	private final IServiceChangeListener<IReasonerPreferencesService> preferencesListenerRegistrator = new IServiceChangeListener<IReasonerPreferencesService>() {
		@Override 
		public void serviceChanged(final IReasonerPreferencesService oldService, final IReasonerPreferencesService newService) {
			if (null != oldService)  {
				oldService.removeListener(preferencesListener);
			}
			
			if (null != newService)  {
				newService.addListener(preferencesListener);
			}
		}
	};
	
	private final RemoteJobResultRegistry<ReasonerTaxonomy> taxonomyResultRegistry;
	
	public SnomedReasonerServerService(final int maximumReasonerCount, final int maximumTaxonomiesToKeep) {
		super(maximumReasonerCount);
		this.taxonomyResultRegistry = new RemoteJobResultRegistry<ReasonerTaxonomy>(maximumTaxonomiesToKeep);
		LOGGER.info("Initialized SNOMED CT reasoner server with maximum of {} reasoner(s) instances and {} saveable taxonomies to keep.", maximumReasonerCount, maximumTaxonomiesToKeep);
	}

	public void registerListeners() {
		getApplicationContext().addServiceListener(ICDOConnectionManager.class, invalidationListenerRegistrator);
		getApplicationContext().addServiceListener(IReasonerPreferencesService.class, preferencesListenerRegistrator);
		taxonomyResultRegistry.registerListeners();
	}

	@Override
	protected void onDispose() {
		taxonomyResultRegistry.dispose();
		getApplicationContext().removeServiceListener(IReasonerPreferencesService.class, preferencesListenerRegistrator);
		getApplicationContext().removeServiceListener(ICDOConnectionManager.class, invalidationListenerRegistrator);
		super.onDispose();
	}

	private static ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
	
	private static IEventBus getEventBus() {
		return getServiceForClass(IEventBus.class);
	}

	private void setStale(final IBranchPath branchPath) {
		for (final ReasonerTaxonomy taxonomy : taxonomyResultRegistry.getAllResults()) {
			if (branchPath.equals(taxonomy.getBranchPath())) {
				taxonomy.setStale();
			}
		}
		
		final CollectingServiceReference<Reasoner> sharedReference = getSharedServiceReferenceIfExists(branchPath);
		if (null != sharedReference) {
			sharedReference.getService().setStale();
		}
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	protected Reasoner createService(final IBranchPath branchPath, final boolean shared, final ClassificationRequest request) throws Exception {
		LOGGER.info(MessageFormat.format("Creating reasoner for branch path ''{0}''.", branchPath));
		return new Reasoner(request.getReasonerId(), branchPath, shared);
	}

	@Override
	protected void retireService(final Reasoner reasoner) throws InterruptedException {
		LOGGER.info(MessageFormat.format("Retiring reasoner for branch path ''{0}''.", reasoner.getBranchPath()));
		reasoner.dispose();
	}
	
	@Override
	protected boolean matchesParams(final Reasoner service, ClassificationRequest serviceParams) {
		return service.getReasonerId().equals(serviceParams.getReasonerId());
	}
	
	public void registerResult(final UUID remoteJobId, final ReasonerTaxonomy reasonerTaxonomy) {
		taxonomyResultRegistry.put(remoteJobId, reasonerTaxonomy);
	}

	@Override
	public void beginClassification(final ClassificationRequest classificationRequest) {
		checkNotNull(classificationRequest, "Classification request parameter object may not be null.");
		
		if (null == classificationRequest.getReasonerId()) {
			classificationRequest.withReasonerId(ApplicationContext.getServiceForClass(IReasonerPreferencesService.class).getSelectedReasonerId());
		}
		
		final RpcSession session = RpcThreadLocal.getSessionUnchecked();
		final String userId = (null != session) ? (String) session.get(IApplicationSessionManager.KEY_USER_ID) : SpecialUserStore.SYSTEM_USER_NAME;

		final IBranchPath snomedBranchPath = classificationRequest.getSnomedBranchPath();
		final UUID classificationId = classificationRequest.getClassificationId();
		
		final ReasonerRemoteJob remoteJob = new ReasonerRemoteJob(this, classificationRequest);
		remoteJob.setRule(new BranchExclusiveRule(SnomedDatastoreActivator.REPOSITORY_UUID, snomedBranchPath));
		RemoteJobUtils.configureProperties(remoteJob, userId, SnomedReasonerService.USER_COMMAND_ID, classificationId);
		remoteJob.schedule();
	}

	@Override 
	public GetResultResponse getResult(final UUID classificationId) {
		
		final ReasonerTaxonomy taxonomy = taxonomyResultRegistry.get(classificationId);
		
		if (null == taxonomy) {
			return new GetResultResponse(GetResultResponse.Type.NOT_AVAILABLE);
		}
		
		final Type responseType = taxonomy.isStale() ? Type.STALE : Type.SUCCESS;
		return new GetResultResponse(responseType, doGetResult(classificationId, taxonomy));  
	}

	private RevisionIndex getIndex() {
		return ApplicationContext.getInstance().getService(RepositoryManager.class).get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
	}
	
	private GetResultResponseChanges doGetResult(final UUID classificationId, final ReasonerTaxonomy taxonomy) {
		
		final IBranchPath branchPath = taxonomy.getBranchPath();
		final InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder = getIndex().read(branchPath.getPath(), new RevisionIndexRead<InitialReasonerTaxonomyBuilder>() {
			@Override
			public InitialReasonerTaxonomyBuilder execute(RevisionSearcher searcher) throws IOException {
				return new InitialReasonerTaxonomyBuilder(searcher, InitialReasonerTaxonomyBuilder.Type.REASONER);
			}
		});
		
		final ImmutableList.Builder<RelationshipChangeEntry> relationshipBuilder = ImmutableList.builder();
		final ImmutableList.Builder<IConcreteDomainChangeEntry> concreteDomainBuilder = ImmutableList.builder();
	
		final Map<Long, ChangeConcept> changeConceptCache = newHashMap();
		new RelationshipNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder).collectNormalFormChanges(null, new OntologyChangeProcessor<StatementFragment>() {
			@Override 
			protected void handleAddedSubject(final long conceptId, final StatementFragment addedSubject) {
				registerEntry(conceptId, addedSubject, Nature.INFERRED);
			}
			
			@Override 
			protected void handleRemovedSubject(final long conceptId, final StatementFragment removedSubject) {
				registerEntry(conceptId, removedSubject, Nature.REDUNDANT);
			}
	
			private void registerEntry(final long conceptId, final StatementFragment subject, final Nature changeNature) {
				
				final ChangeConcept sourceComponent = getOrCreateChangeConcept(changeConceptCache, branchPath, conceptId);
				final ChangeConcept typeComponent = getOrCreateChangeConcept(changeConceptCache, branchPath, subject.getTypeId());
				final ChangeConcept destinationComponent = getOrCreateChangeConcept(changeConceptCache, branchPath, subject.getDestinationId());
				
				final long modifierId = subject.isUniversal() 
						? LongConcepts.UNIVERSAL_RESTRICTION_MODIFIER_ID
						: LongConcepts.EXISTENTIAL_RESTRICTION_MODIFIER_ID;
				
				final ChangeConcept modifierComponent = getOrCreateChangeConcept(changeConceptCache, branchPath, modifierId);
				
				final RelationshipChangeEntry entry = new RelationshipChangeEntry(
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
				final Collection<ConcreteDomainFragment> relationshipConcreteDomainElements = reasonerTaxonomyBuilder.getStatementConcreteDomainFragments(subject.getStatementId());
				
				for (final ConcreteDomainFragment concreteDomainElementIndexEntry : relationshipConcreteDomainElements) {
					
					final ConcreteDomainElement concreteDomainElement = createConcreteDomainElement(changeConceptCache,
							branchPath, 
							concreteDomainElementIndexEntry);
					
					final RelationshipConcreteDomainChangeEntry relationshipConcreteDomainElementEntry = 
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
			protected void handleAddedSubject(final long conceptId, final ConcreteDomainFragment addedSubject) {
				registerEntry(conceptId, addedSubject, Nature.INFERRED);
			}
			
			@Override 
			protected void handleRemovedSubject(final long conceptId, final ConcreteDomainFragment removedSubject) {
				registerEntry(conceptId, removedSubject, Nature.REDUNDANT);
			}
	
			private void registerEntry(final long conceptId, final ConcreteDomainFragment subject, final Nature changeNature) {
				final ConcreteDomainElement concreteDomainElement = createConcreteDomainElement(changeConceptCache,
						branchPath, 
						subject);
				
				final ChangeConcept sourceComponent = getOrCreateChangeConcept(changeConceptCache,
						branchPath, 
						conceptId);
				
				final ConceptConcreteDomainChangeEntry responseEntry = new ConceptConcreteDomainChangeEntry(
						changeNature, 
						sourceComponent, 
						concreteDomainElement);
				
				concreteDomainBuilder.add(responseEntry);
			}
		});
		
		final List<AbstractEquivalenceSet> equivalentConcepts = doGetEquivalentConcepts(taxonomy);
		final GetResultResponseChanges convertedChanges = new GetResultResponseChanges(classificationId, 
				taxonomy.getElapsedTimeMillis(),
				equivalentConcepts, 
				relationshipBuilder.build(), 
				concreteDomainBuilder.build());
		
		return convertedChanges;
	}

	private ChangeConcept getOrCreateChangeConcept(final Map<Long, ChangeConcept> changeConceptCache, final IBranchPath branchPath, final long id) {
		if (changeConceptCache.containsKey(id)) {
			return changeConceptCache.get(id); 
		} else {
			final ChangeConcept concept = SnomedRequests.prepareGetConcept()
					.setComponentId(Long.toString(id))
					.build(branchPath.getPath())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.then(new Function<ISnomedConcept, ChangeConcept>() {
						@Override
						public ChangeConcept apply(ISnomedConcept input) {
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

	private ConcreteDomainElement createConcreteDomainElement(final Map<Long, ChangeConcept> changeConceptCache, final IBranchPath branchPath, final ConcreteDomainFragment fragment) {
		
		final ChangeConcept unitConcept = (ConcreteDomainFragment.UNSET_UOM_ID == fragment.getUomId()) 
				? null
				: getOrCreateChangeConcept(changeConceptCache, branchPath, fragment.getUomId());
		
		final ConcreteDomainElement concreteDomainElement = new ConcreteDomainElement(
				fragment.getLabel(), 
				fragment.getValue(), 
				unitConcept);
		
		return concreteDomainElement;
	}

	@Override 
	public GetEquivalentConceptsResponse getEquivalentConcepts(final UUID classificationId) {
		
		final ReasonerTaxonomy taxonomy = taxonomyResultRegistry.get(classificationId);
		
		if (null == taxonomy) {
			return new GetEquivalentConceptsResponse(GetResultResponse.Type.NOT_AVAILABLE);
		}
		
		final Type responseType = taxonomy.isStale() ? Type.STALE : Type.SUCCESS;
		return new GetEquivalentConceptsResponse(responseType, doGetEquivalentConcepts(taxonomy));  
	}

	private List<AbstractEquivalenceSet> doGetEquivalentConcepts(final ReasonerTaxonomy taxonomy) {
		final IBranchPath branchPath = taxonomy.getBranchPath();
		final List<AbstractEquivalenceSet> results = newArrayList();
		
		final LongSet unsatisfiableConceptIds = taxonomy.getUnsatisfiableConceptIds();
		if (!unsatisfiableConceptIds.isEmpty()) {
			final List<ISnomedConcept> unsatisfiableEntries = convertIdsToIndexEntries(branchPath, unsatisfiableConceptIds);
			results.add(new UnsatisfiableSet(unsatisfiableEntries));
		}
		
		final List<LongSet> equivalentConceptSets = taxonomy.getEquivalentConceptIds();
		for (final LongSet equivalentConceptSet : equivalentConceptSets) {
			final List<ISnomedConcept> equivalentEntries = convertIdsToIndexEntries(branchPath, equivalentConceptSet);
			final ISnomedConcept suggestedConcept = equivalentEntries.remove(0);
			results.add(new EquivalenceSet(suggestedConcept, equivalentEntries));
		}
		
		return results;
	}

	private List<ISnomedConcept> convertIdsToIndexEntries(final IBranchPath branchPath, final LongSet conceptIds) { 
		final Set<String> conceptIdFilter = LongSets.toStringSet(conceptIds);
		final SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
				.setLimit(conceptIds.size())
				.setComponentIds(conceptIdFilter)
				.setExpand("pt()")
				.setLocales(ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference())
				.build(branchPath.getPath())
				.execute(ApplicationContext.getInstance().getService(IEventBus.class))
				.getSync();
		final Map<String, ISnomedConcept> existingConcepts = FluentIterable.from(concepts).uniqueIndex(IComponent.ID_FUNCTION);
		final List<ISnomedConcept> convertedSet = newArrayListWithExpectedSize(conceptIdFilter.size());
		for (final String conceptId : conceptIdFilter) {
			final ISnomedConcept concept = existingConcepts.get(conceptId);
			if (null == concept) {
				final SnomedConcept fakeConcept = new SnomedConcept();
				fakeConcept.setId(conceptId);
				fakeConcept.setIconId(Concepts.ROOT_CONCEPT);
				fakeConcept.setModuleId(Concepts.MODULE_ROOT);
				final SnomedDescription pt = new SnomedDescription();
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
	public PersistChangesResponse persistChanges(final UUID classificationId, final String userId) {
		final ReasonerTaxonomy taxonomy = taxonomyResultRegistry.get(classificationId);
		
		if (null == taxonomy) {
			return new PersistChangesResponse(Type.NOT_AVAILABLE);
		}
		
		if (taxonomy.isStale()) {
			return new PersistChangesResponse(Type.STALE);
		}
		
		try {
			return doPersistChanges(classificationId, taxonomy, userId);
		} catch (final OperationLockException | InterruptedException e) {
			LOGGER.error("Cannot persist classification changes.", e);
			return new PersistChangesResponse(Type.NOT_AVAILABLE);
		}
	}

	private PersistChangesResponse doPersistChanges(final UUID classificationId, final ReasonerTaxonomy taxonomy, final String userId) throws OperationLockException, InterruptedException {
		
		final IBranchPath branchPath = taxonomy.getBranchPath();
		final UUID persistenceId = randomUUID();

		final StringBuilder sb = new StringBuilder();
		sb.append("Persisting ontology changes on ");
		sb.append(branchPath.getPath());
		
		final Job remoteJob = new PersistChangesRemoteJob(sb.toString(), taxonomy, branchPath, userId);
		remoteJob.setRule(new BranchExclusiveRule(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath));
		RemoteJobUtils.configureProperties(remoteJob, userId, null, persistenceId);
		
		remoteJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				if (event.getResult().isOK()) {
					getServiceForClass(IRemoteJobManager.class).cancelRemoteJob(persistenceId);
				}
				
				getEventBus().send(SnomedReasonerServiceUtil.getChangesPersistedAddress(classificationId), new SerializableStatus(event.getResult()));
			}
		});
		
		remoteJob.schedule();
		return new PersistChangesResponse(Type.SUCCESS);
	}

	@Override
	public boolean canStartImmediately() {
		return super.hasAvailableServiceReferences();
	}
}