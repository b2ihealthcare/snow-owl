/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.events.Notifications;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobNotification;
import com.b2international.snowowl.datastore.server.snomed.index.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
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
import com.b2international.snowowl.snomed.reasoner.classification.entry.ChangeEntry.Nature;
import com.b2international.snowowl.snomed.reasoner.classification.entry.ConcreteDomainChangeEntry;
import com.b2international.snowowl.snomed.reasoner.classification.entry.RelationshipChangeEntry;
import com.b2international.snowowl.snomed.reasoner.preferences.IReasonerPreferencesService;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.reasoner.server.normalform.NormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.server.request.SnomedReasonerRequests;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;

import io.reactivex.disposables.Disposable;

/**
 * Manages reasoners that operate on the OWL representation of a SNOMED&nbsp;CT repository branch path. 
 */
public class SnomedReasonerServerService extends CollectingService<Reasoner, ClassificationSettings> implements SnomedReasonerService, IDisposableService {

	private static final class ConcreteDomainChangeCollector extends OntologyChangeProcessor<ConcreteDomainFragment> {
		private final ImmutableList.Builder<ConcreteDomainChangeEntry> concreteDomainBuilder;

		private ConcreteDomainChangeCollector(final ImmutableList.Builder<ConcreteDomainChangeEntry> concreteDomainBuilder) {
			this.concreteDomainBuilder = concreteDomainBuilder;
		}

		@Override 
		protected void handleAddedSubject(final String conceptId, final ConcreteDomainFragment addedSubject) {
			registerEntry(conceptId, addedSubject, Nature.INFERRED);
		}

		@Override 
		protected void handleRemovedSubject(final String conceptId, final ConcreteDomainFragment removedSubject) {
			registerEntry(conceptId, removedSubject, Nature.REDUNDANT);
		}

		private void registerEntry(final String conceptId, final ConcreteDomainFragment subject, final Nature changeNature) {
			final ConcreteDomainChangeEntry changeEntry = new ConcreteDomainChangeEntry(subject.getId(),
					changeNature, 
					conceptId, 
					Long.toString(subject.getTypeId()), 
					subject.getGroup(), 
					subject.getDataType(), 
					subject.getSerializedValue());

			concreteDomainBuilder.add(changeEntry);
		}
	}

	private static final class RelationshipChangeCollector extends OntologyChangeProcessor<StatementFragment> {
		private final ImmutableList.Builder<RelationshipChangeEntry> relationshipBuilder;

		private RelationshipChangeCollector(final ImmutableList.Builder<RelationshipChangeEntry> relationshipBuilder) {
			this.relationshipBuilder = relationshipBuilder;
		}

		@Override 
		protected void handleAddedSubject(final String conceptId, final StatementFragment addedSubject) {
			registerEntry(conceptId, addedSubject, Nature.INFERRED);
		}

		@Override 
		protected void handleRemovedSubject(final String conceptId, final StatementFragment removedSubject) {
			registerEntry(conceptId, removedSubject, Nature.REDUNDANT);
		}

		private void registerEntry(final String conceptId, final StatementFragment subject, final Nature changeNature) {
			
			final String modifierId = subject.isUniversal() 
					? Concepts.UNIVERSAL_RESTRICTION_MODIFIER
					: Concepts.EXISTENTIAL_RESTRICTION_MODIFIER;
			
			final RelationshipChangeEntry entry = new RelationshipChangeEntry(
					Long.toString(subject.getStatementId()),
					changeNature, 
					conceptId, 
					Long.toString(subject.getTypeId()), 
					subject.getGroup(), 
					Long.toString(subject.getDestinationId()), 
					subject.getUnionGroup(), 
					modifierId, 
					subject.isDestinationNegated());
			
			relationshipBuilder.add(entry);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger("reasoner");
	
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
	private final Cache<String, ReasonerTaxonomyBuilder> taxonomyBuilderRegistry;
	private final Disposable remoteJobSubscription;
	
	public SnomedReasonerServerService(int maximumReasonerCount, int maximumTaxonomiesToKeep) {
		super(maximumReasonerCount);

		this.taxonomyResultRegistry = CacheBuilder.newBuilder().maximumSize(maximumTaxonomiesToKeep).build();
		this.taxonomyBuilderRegistry = CacheBuilder.newBuilder().maximumSize(maximumTaxonomiesToKeep).build();
		
		LOGGER.info("Initialized SNOMED CT reasoner server with maximum of {} reasoner(s) instances and {} result(s) to keep.", maximumReasonerCount, maximumTaxonomiesToKeep);
		
		remoteJobSubscription = ApplicationContext.getServiceForClass(Notifications.class)
				.ofType(RemoteJobNotification.class)
				.subscribe(this::onRemoteJobNotification);
	}
	
	private void onRemoteJobNotification(RemoteJobNotification notification) {
		if (RemoteJobNotification.isRemoved(notification)) {
			notification.getJobIds().forEach(this::removeResult);
		}
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
		remoteJobSubscription.dispose();
		super.onDispose();
	}

	private static ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
	
	private static IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

	private void setStale(IBranchPath branchPath) {
		Set<String> classificationToRemove = taxonomyResultRegistry.asMap().entrySet()
			.stream()
			.filter(entry -> branchPath.equals(entry.getValue().getBranchPath()))
			.map(entry -> entry.getKey())
			.collect(Collectors.toSet());
		classificationToRemove.forEach(this::removeResult);
		
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
	protected void retireService(Reasoner reasoner) {
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
	
	public void registerTaxonomyBuilder(String classificationId, ReasonerTaxonomyBuilder reasonerTaxonomyBuilder) {
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
			return new GetResultResponse(null);
		} else {
			return new GetResultResponse(doGetResult(classificationId, taxonomy));  
		}
	}

	private GetResultResponseChanges doGetResult(final String classificationId, final ReasonerTaxonomy taxonomy) {
		final ReasonerTaxonomyBuilder reasonerTaxonomyBuilder = taxonomyBuilderRegistry.getIfPresent(classificationId);
		
		final ImmutableList.Builder<RelationshipChangeEntry> relationshipBuilder = ImmutableList.builder();
		final ImmutableList.Builder<ConcreteDomainChangeEntry> concreteDomainBuilder = ImmutableList.builder();
				
		new NormalFormGenerator(taxonomy, reasonerTaxonomyBuilder).computeChanges(null, 
				new RelationshipChangeCollector(relationshipBuilder), 
				new ConcreteDomainChangeCollector(concreteDomainBuilder));
		
		final List<AbstractEquivalenceSet> equivalentConcepts = doGetEquivalentConcepts(taxonomy);
		final GetResultResponseChanges convertedChanges = new GetResultResponseChanges(taxonomy.getElapsedTimeMillis(), 
				equivalentConcepts,
				relationshipBuilder.build(), 
				concreteDomainBuilder.build());
		
		return convertedChanges;
	}

	@Override 
	public GetEquivalentConceptsResponse getEquivalentConcepts(String classificationId) {
		
		ReasonerTaxonomy taxonomy = taxonomyResultRegistry.getIfPresent(classificationId);
		
		if (null == taxonomy) {
			return new GetEquivalentConceptsResponse(null);
		} else {
			return new GetEquivalentConceptsResponse(doGetEquivalentConcepts(taxonomy));  
		}
		
	}

	private List<AbstractEquivalenceSet> doGetEquivalentConcepts(ReasonerTaxonomy taxonomy) {
		List<AbstractEquivalenceSet> results = newArrayList();
		
		LongSet unsatisfiableConceptIds = taxonomy.getUnsatisfiableConceptIds();
		if (!unsatisfiableConceptIds.isEmpty()) {
			results.add(new UnsatisfiableSet(LongSets.toStringList(unsatisfiableConceptIds)));
		}
		
		List<LongCollection> equivalentConceptSets = taxonomy.getEquivalentConceptIds();
		for (LongCollection equivalentConceptSet : equivalentConceptSets) {
			List<String> equivalentIds = LongSets.toStringList(equivalentConceptSet);
			String suggestedConcept = equivalentIds.remove(0);
			results.add(new EquivalenceSet(suggestedConcept, equivalentIds));
		}
		
		return results;
	}

	@Override 
	public PersistChangesResponse persistChanges(String classificationId, String userId) {
		ReasonerTaxonomy taxonomy = taxonomyResultRegistry.getIfPresent(classificationId);
		ReasonerTaxonomyBuilder taxonomyBuilder = taxonomyBuilderRegistry.getIfPresent(classificationId);
		
		if (null == taxonomy) {
			return new PersistChangesResponse(Type.NOT_AVAILABLE);
		}
		
		final PersistChangesResponse response;
		
		try {
			response = doPersistChanges(classificationId, taxonomy, taxonomyBuilder, userId);
		} catch (OperationLockException | InterruptedException e) {
			LOGGER.error("Cannot persist classification changes.", e);
			return new PersistChangesResponse(Type.NOT_AVAILABLE);
		}
		
		// Remove results only if saving was successful
		removeResult(classificationId);
		return response;
	}

	private PersistChangesResponse doPersistChanges(String classificationId, ReasonerTaxonomy taxonomy, ReasonerTaxonomyBuilder taxonomyBuilder, String userId) 
			throws OperationLockException, InterruptedException {
		
		String persistJobId = SnomedReasonerRequests.preparePersistChanges()
				.setClassificationId(classificationId)
				.setTaxonomy(taxonomy)
				.setTaxonomyBuilder(taxonomyBuilder)
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
		taxonomyBuilderRegistry.asMap().remove(classificationId);
	}
	
}
