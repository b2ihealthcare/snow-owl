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
import static java.util.UUID.randomUUID;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.TopDocs;
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

import com.b2international.commons.ClassUtils;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.commons.platform.Extensions;
import com.b2international.commons.status.SerializableStatus;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.remotejobs.IRemoteJobManager;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.datastore.server.remotejobs.RemoteJobResultRegistry;
import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcThreadLocal;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.request.DescriptionRequestHelper;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
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
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndMolduleAssigner;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.reasoner.server.normalform.ConceptConcreteDomainNormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.server.normalform.RelationshipNormalFormGenerator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import bak.pcj.LongIterator;
import bak.pcj.set.LongSet;

/**
 * Manages reasoners that operate on the OWL representation of a SNOMED&nbsp;CT repository branch path. 
 *
 */
public class SnomedReasonerServerService extends CollectingService<Reasoner, ClassificationRequest> implements SnomedReasonerService, IDisposableService {

	private static final Ordering<SnomedConceptIndexEntry> STORAGE_KEY_ORDERING = Ordering.from(StorageKeyComparator.INSTANCE).nullsLast();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedReasonerServerService.class);
	
	private static final Set<String> FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().iconId().build();

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
	
	private NamespaceAndMolduleAssigner namespaceAndModuleAssigner;
	
	public SnomedReasonerServerService(final int maximumReasonerCount, final int maximumReasonerResultsToKeep) {
		super(maximumReasonerCount);
		this.taxonomyResultRegistry = new RemoteJobResultRegistry<ReasonerTaxonomy>(maximumReasonerResultsToKeep);
		
		namespaceAndModuleAssigner = Extensions.getFirstPriorityExtension("com.b2international.snowowl.snomed.reasoner.server.namespaceAssigner", NamespaceAndMolduleAssigner.class);
		if (namespaceAndModuleAssigner == null) {
			throw new NullPointerException("Could not find a namespace and module allocator in the extension registry");
		}
		
		LOGGER.info("Initialized SNOMED CT reasoner server with maximum of {} reasoner(s) instances and {} result(s) to keep.", maximumReasonerCount, maximumReasonerResultsToKeep);
		LOGGER.info("Reasoner service will use the {} class for relationship/concrete domain namespace and module assignment.", namespaceAndModuleAssigner.getClass().getSimpleName());
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

	private static SnomedTerminologyBrowser getTerminologyBrowser() {
		return getServiceForClass(SnomedTerminologyBrowser.class);
	}
	
	private static IndexServerService<?> getIndexServerService() {
		return ClassUtils.checkAndCast(getServiceForClass(SnomedIndexService.class), IndexServerService.class);
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
		remoteJob.setRule(new ReasonerRemoteJobKey(SnomedDatastoreActivator.REPOSITORY_UUID, snomedBranchPath));
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

	private GetResultResponseChanges doGetResult(final UUID classificationId, final ReasonerTaxonomy taxonomy) {
		
		final IBranchPath branchPath = taxonomy.getBranchPath();
		final InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder = new InitialReasonerTaxonomyBuilder(branchPath, InitialReasonerTaxonomyBuilder.Type.REASONER);
		final SnomedTerminologyBrowser terminologyBrowser = getTerminologyBrowser();
		
		final ImmutableList.Builder<RelationshipChangeEntry> relationshipBuilder = ImmutableList.builder();
		final ImmutableList.Builder<IConcreteDomainChangeEntry> concreteDomainBuilder = ImmutableList.builder();
	
		new RelationshipNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder).collectNormalFormChanges(null, new OntologyChangeProcessor<StatementFragment>(namespaceAndModuleAssigner) {
			@Override 
			protected void handleAddedSubject(final String conceptId, final StatementFragment addedSubject) {
				registerEntry(Long.valueOf(conceptId), addedSubject, Nature.INFERRED);
			}
			
			@Override 
			protected void handleRemovedSubject(final String conceptId, final StatementFragment removedSubject) {
				registerEntry(Long.valueOf(conceptId), removedSubject, Nature.REDUNDANT);
			}
	
			private void registerEntry(final long conceptId, final StatementFragment subject, final Nature changeNature) {
				
				final ChangeConcept sourceComponent = createChangeConcept(branchPath, terminologyBrowser, conceptId);
				final ChangeConcept typeComponent = createChangeConcept(branchPath, terminologyBrowser, subject.getTypeId());
				final ChangeConcept destinationComponent = createChangeConcept(branchPath, terminologyBrowser, subject.getDestinationId());
				
				final long modifierId = subject.isUniversal() 
						? LongConcepts.UNIVERSAL_RESTRICTION_MODIFIER_ID
						: LongConcepts.EXISTENTIAL_RESTRICTION_MODIFIER_ID;
				
				final ChangeConcept modifierComponent = createChangeConcept(branchPath, terminologyBrowser, modifierId);
				
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
					
					final ConcreteDomainElement concreteDomainElement = createConcreteDomainElement(
							branchPath, 
							terminologyBrowser, 
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
		
		new ConceptConcreteDomainNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder).collectNormalFormChanges(null, new OntologyChangeProcessor<ConcreteDomainFragment>(namespaceAndModuleAssigner) {
			@Override 
			protected void handleAddedSubject(final String conceptId, final ConcreteDomainFragment addedSubject) {
				registerEntry(Long.valueOf(conceptId), addedSubject, Nature.INFERRED);
			}
			
			@Override 
			protected void handleRemovedSubject(final String conceptId, final ConcreteDomainFragment removedSubject) {
				registerEntry(Long.valueOf(conceptId), removedSubject, Nature.REDUNDANT);
			}
	
			private void registerEntry(final long conceptId, final ConcreteDomainFragment subject, final Nature changeNature) {
				final ConcreteDomainElement concreteDomainElement = createConcreteDomainElement(
						branchPath, 
						terminologyBrowser, 
						subject);
				
				final ChangeConcept sourceComponent = createChangeConcept(
						branchPath, 
						terminologyBrowser, 
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

	private ChangeConcept createChangeConcept(final IBranchPath branchPath, final SnomedTerminologyBrowser terminologyBrowser, final long id) {
		
		final IndexServerService<?> indexService = getIndexServerService();
		
		final TopDocs topDocs = indexService.search(branchPath, SnomedMappings.newQuery().concept().id(id).matchAll(), 1);
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return new ChangeConcept(id, Long.parseLong(Concepts.ROOT_CONCEPT));
		} else {
			final Document doc = indexService.document(branchPath, topDocs.scoreDocs[0].doc, FIELDS_TO_LOAD);
			return new ChangeConcept(id, SnomedMappings.iconId().getValue(doc));
		}
	}

	private ConcreteDomainElement createConcreteDomainElement(final IBranchPath branchPath, final SnomedTerminologyBrowser terminologyBrowser, final ConcreteDomainFragment fragment) {
		
		final ChangeConcept unitConcept = (ConcreteDomainFragment.UNSET_UOM_ID == fragment.getUomId()) 
				? null
				: createChangeConcept(branchPath, terminologyBrowser, fragment.getUomId());
		
		final ConcreteDomainElement concreteDomainElement = new ConcreteDomainElement(
				fragment.getLabel().utf8ToString(), 
				fragment.getValue().utf8ToString(), 
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
		final SnomedTerminologyBrowser terminologyBrowser = getTerminologyBrowser();
		final List<AbstractEquivalenceSet> results = newArrayList();
		
		final LongSet unsatisfiableConceptIds = taxonomy.getUnsatisfiableConceptIds();
		if (!unsatisfiableConceptIds.isEmpty()) {
			final List<SnomedConceptIndexEntry> unsatisfiableEntries = convertIdsToIndexEntries(branchPath, terminologyBrowser, unsatisfiableConceptIds);
			results.add(new UnsatisfiableSet(unsatisfiableEntries));
		}
		
		final List<LongSet> equivalentConceptSets = taxonomy.getEquivalentConceptIds();
		for (final LongSet equivalentConceptSet : equivalentConceptSets) {
			final List<SnomedConceptIndexEntry> equivalentEntries = convertIdsToIndexEntries(branchPath, terminologyBrowser, equivalentConceptSet);
			final SnomedConceptIndexEntry suggestedConcept = equivalentEntries.remove(0);
			results.add(new EquivalenceSet(suggestedConcept, equivalentEntries));
		}
		
		return results;
	}

	private List<SnomedConceptIndexEntry> convertIdsToIndexEntries(final IBranchPath branchPath, final SnomedTerminologyBrowser terminologyBrowser, final LongSet conceptIds) {
		final Set<String> conceptIdFilter = LongSets.toStringSet(conceptIds);
		final DescriptionRequestHelper helper = new DescriptionRequestHelper() {
			@Override
			protected SnomedDescriptions execute(SnomedDescriptionSearchRequestBuilder req) {
				return req.build(branchPath.getPath()).executeSync(ApplicationContext.getInstance().getService(IEventBus.class));
			}
		};
		final Map<String, ISnomedDescription> preferredTerms = helper.getPreferredTerms(conceptIdFilter, ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference());
		
		final List<SnomedConceptIndexEntry> convertedSet = newArrayList();
		for (final LongIterator itr = conceptIds.iterator(); itr.hasNext(); /* empty */) {
			final String conceptId = String.valueOf(itr.next());
			SnomedConceptIndexEntry conceptIndexEntry = terminologyBrowser.getConcept(branchPath, conceptId);
			if (null == conceptIndexEntry) {
				conceptIndexEntry = SnomedConceptIndexEntry.builder()
						.id(conceptId)
						.label(conceptId + " (unresolved)")
						.iconId(Concepts.ROOT_CONCEPT) 
						.moduleId(Concepts.MODULE_ROOT)
						.storageKey(Long.MAX_VALUE) // XXX: set Long.MAX_VALUE storage key to never suggest it as a replacement	
						.effectiveTimeLong(EffectiveTimes.UNSET_EFFECTIVE_TIME)
						.build();
			} else {
				final ISnomedDescription description = preferredTerms.get(conceptId);
				final String label = description == null ? conceptId : description.getTerm();
				conceptIndexEntry = SnomedConceptIndexEntry.builder(conceptIndexEntry).label(label).build();
			}
			
			convertedSet.add(conceptIndexEntry);
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
		
		final Job remoteJob = new PersistChangesRemoteJob(sb.toString(), taxonomy, branchPath, userId, namespaceAndModuleAssigner);
		remoteJob.setRule(new ReasonerRemoteJobKey(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath));
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