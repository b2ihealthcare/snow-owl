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
package com.b2international.snowowl.snomed.api.impl;

import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.SnowOwl;
import com.b2international.snowowl.core.events.Notifications;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.index.SingleDirectoryIndexManager;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobNotification;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.ISnomedClassificationService;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.domain.classification.ClassificationStatus;
import com.b2international.snowowl.snomed.api.domain.classification.IClassificationRun;
import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConcept;
import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConceptSet;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChange;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChangeList;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationshipTarget;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationshipType;
import com.b2international.snowowl.snomed.api.impl.domain.classification.ClassificationRun;
import com.b2international.snowowl.snomed.api.impl.domain.classification.EquivalentConcept;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractResponse.Type;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationSettings;
import com.b2international.snowowl.snomed.reasoner.classification.GetResultResponse;
import com.b2international.snowowl.snomed.reasoner.classification.PersistChangesResponse;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Closeables;

import io.reactivex.disposables.Disposable;

/**
 */
public class SnomedClassificationServiceImpl implements ISnomedClassificationService {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedClassificationServiceImpl.class);
	
	private ClassificationRunIndex indexService;
	private ExecutorService executorService;
	private Disposable remoteJobSubscription;

	@Resource
	private SnomedBrowserService browserService;
	
	@Resource
	private IEventBus bus;
	
	@Resource
	private int maxReasonerRuns;

	private ObjectMapper mapper;

	@PostConstruct
	protected void init() {
		LOG.info("Initializing classification service; keeping indexed data for {} recent run(s).", maxReasonerRuns); 
		this.mapper = new ObjectMapper();
		final File dir = new File(new File(ApplicationContext.getServiceForClass(Environment.class).getDataDirectory(), "indexes"), "classification_runs");
		indexService = new ClassificationRunIndex(dir, mapper);
		ApplicationContext.getInstance().getServiceChecked(SingleDirectoryIndexManager.class).registerIndex(indexService);

		try {
			indexService.trimIndex(maxReasonerRuns);
			indexService.invalidateClassificationRuns();
		} catch (final IOException e) {
			LOG.error("Failed to run housekeeping tasks for the classification index.", e);
		}

		// TODO: common ExecutorService for asynchronous work?
		executorService = Executors.newCachedThreadPool();
		remoteJobSubscription = getNotifications()
				.ofType(RemoteJobNotification.class)
				.subscribe(this::onRemoteJobNotification);
	}
	
	private void onRemoteJobNotification(RemoteJobNotification notification) {
		if (!RemoteJobNotification.isChanged(notification)) {
			return;
		}
		
		JobRequests.prepareSearch()
		.all()
		.filterByIds(notification.getJobIds())
		.buildAsync()
		.execute(getEventBus())
		.then(remoteJobs -> {
			for (RemoteJobEntry remoteJob : remoteJobs) {
				onRemoteJobChanged(remoteJob);
			}
			return remoteJobs;
		});
	}

	private void onRemoteJobChanged(RemoteJobEntry remoteJob) {
		String type = (String) remoteJob.getParameters(mapper).get("type");
		
		switch (type) {
		case "ClassifyRequest":
			onClassifyJobChanged(remoteJob);
			break;
		case "PersistChangesRequest":
			onPersistJobChanged(remoteJob);
			break;
		default:
			break;
		}
	}
	
	private void onClassifyJobChanged(RemoteJobEntry remoteJob) {
		try {
			
			switch (remoteJob.getState()) {
			case CANCELED:
				indexService.updateClassificationRunStatus(remoteJob.getId(), ClassificationStatus.CANCELED);
				break;
			case FAILED:
				indexService.updateClassificationRunStatus(remoteJob.getId(), ClassificationStatus.FAILED);
				break;
			case FINISHED: 
				onClassifyJobFinished(remoteJob);
				break;
			case RUNNING:
				indexService.updateClassificationRunStatus(remoteJob.getId(), ClassificationStatus.RUNNING);
				break;
			case SCHEDULED:
				indexService.updateClassificationRunStatus(remoteJob.getId(), ClassificationStatus.SCHEDULED);
				break;
			case CANCEL_REQUESTED:
				// Nothing to do for this state change
				break;
			default:
				throw new IllegalStateException(MessageFormat.format("Unexpected remote job state ''{0}''.", remoteJob.getState()));
			}
			
		} catch (final IOException e) {
			LOG.error("Caught IOException while updating classification status.", e);
		}
	}

	private void onClassifyJobFinished(RemoteJobEntry remoteJob) {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					
					final GetResultResponse result = getReasonerService().getResult(remoteJob.getId());
					final Type responseType = result.getType();
	
					switch (responseType) {
						case NOT_AVAILABLE: 
							indexService.updateClassificationRunStatus(remoteJob.getId(), ClassificationStatus.FAILED);
							break;
						case SUCCESS:
							indexService.updateClassificationRunStatus(remoteJob.getId(), ClassificationStatus.COMPLETED, result.getChanges());
							break;
						default:
							throw new IllegalStateException(MessageFormat.format("Unexpected response type ''{0}''.", responseType));
					}
	
				} catch (final IOException e) {
					LOG.error("Caught IOException while registering classification data.", e);
				}
			}
		});
	}

	private void onPersistJobChanged(RemoteJobEntry remoteJob) {
		try {

			String classificationJobId = (String) remoteJob.getParameters(mapper).get("classificationId");
			
			switch (remoteJob.getState()) {
			case CANCELED: //$FALL-THROUGH$
			case FAILED:
				indexService.updateClassificationRunStatus(classificationJobId, ClassificationStatus.SAVE_FAILED);
				break;
			case FINISHED: 
				indexService.updateClassificationRunStatus(classificationJobId, ClassificationStatus.SAVED);
				break;
			case RUNNING: //$FALL-THROUGH$
			case SCHEDULED: //$FALL-THROUGH$
			case CANCEL_REQUESTED:
				// Nothing to do for these state changes
				break;
			default:
				throw new IllegalStateException(MessageFormat.format("Unexpected remote job state ''{0}''.", remoteJob.getState()));
			}

		} catch (final IOException e) {
			LOG.error("Caught IOException while updating classification status after save.", e);
		}
	}

	@PreDestroy
	protected void shutdown() {
		if (null != remoteJobSubscription) {
			remoteJobSubscription.dispose();
			remoteJobSubscription = null;
		}

		if (null != executorService) {
			executorService.shutdown();
			executorService = null;
		}
		
		if (null != indexService) {
			ApplicationContext.getInstance().getServiceChecked(SingleDirectoryIndexManager.class).unregisterIndex(indexService);
			try {
				Closeables.close(indexService, true);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			indexService = null;
		}
		
		LOG.info("Classification service shut down.");
	}

	private static SnomedReasonerService getReasonerService() {
		return ApplicationContext.getServiceForClass(SnomedReasonerService.class);
	}

	private static IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
	
	private static Notifications getNotifications() {
		return ApplicationContext.getServiceForClass(Notifications.class);
	}

	@Override
	public List<IClassificationRun> getAllClassificationRuns(final String branchPath, final String userId) {
		try {
			return indexService.getAllClassificationRuns(branchPath, userId);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IClassificationRun beginClassification(final String branchPath, final String reasonerId, final String userId) {

		final ClassificationSettings settings = new ClassificationSettings(userId, BranchPathUtils.createPath(branchPath))
				.withParentContextDescription(DatastoreLockContextDescriptions.ROOT)
				.withReasonerId(reasonerId);

		final ClassificationRun classificationRun = new ClassificationRun();
		classificationRun.setId(settings.getClassificationId());
		classificationRun.setReasonerId(reasonerId);
		classificationRun.setCreationDate(new Date());
		classificationRun.setUserId(userId);
		classificationRun.setStatus(ClassificationStatus.SCHEDULED);
		
		try {
			indexService.upsertClassificationRun(branchPath, classificationRun);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		
		getReasonerService().beginClassification(settings);
		return classificationRun;
	}

	@Override
	public IClassificationRun getClassificationRun(final String branchPath, final String classificationId, final String userId) {
		try {
			return indexService.getClassificationRun(branchPath, classificationId, userId);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<IEquivalentConceptSet> getEquivalentConceptSets(final String branchPath, final String classificationId, final List<ExtendedLocale> locales, final String userId) {
		// Check if it exists
		getClassificationRun(branchPath, classificationId, userId);
		try {
			final List<IEquivalentConceptSet> conceptSets = indexService.getEquivalentConceptSets(branchPath, classificationId, userId);
			final Set<String> conceptIds = newHashSet();
			
			for (final IEquivalentConceptSet conceptSet : conceptSets) {
				for (final IEquivalentConcept equivalentConcept : conceptSet.getEquivalentConcepts()) {
					conceptIds.add(equivalentConcept.getId());
				}
			}

			final Map<String, SnomedDescription> fsnMap = new DescriptionService(bus, branchPath).getFullySpecifiedNames(conceptIds, locales);
			for (final IEquivalentConceptSet conceptSet : conceptSets) {
				for (final IEquivalentConcept equivalentConcept : conceptSet.getEquivalentConcepts()) {
					final String equivalentConceptId = equivalentConcept.getId();
					final SnomedDescription fsn = fsnMap.get(equivalentConceptId);
					if (fsn != null) {
						((EquivalentConcept) equivalentConcept).setLabel(fsn.getTerm());
					} else {
						((EquivalentConcept) equivalentConcept).setLabel(equivalentConceptId);
					}
				}
			}
			
			return conceptSets;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IRelationshipChangeList getRelationshipChanges(final String branchPath, final String classificationId, final String userId, final int offset, final int limit) {
		return getRelationshipChanges(branchPath, classificationId, null, userId, offset, limit);
	}

	private IRelationshipChangeList getRelationshipChanges(String branchPath, String classificationId, String conceptId, String userId, int offset, int limit) {
		// Check if it exists
		getClassificationRun(branchPath, classificationId, userId);

		try {
			return indexService.getRelationshipChanges(branchPath, classificationId, conceptId, userId, offset, limit);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ISnomedBrowserConcept getConceptPreview(String branchPath, String classificationId, String conceptId, List<ExtendedLocale> locales, String userId) {
		final SnomedBrowserConcept conceptDetails = (SnomedBrowserConcept) browserService.getConceptDetails(branchPath, conceptId, locales);

		// Replace ImmutableCollection of relationships
		final List<ISnomedBrowserRelationship> relationships = new ArrayList<ISnomedBrowserRelationship>(conceptDetails.getRelationships());
		conceptDetails.setRelationships(relationships);

		final IRelationshipChangeList relationshipChanges = getRelationshipChanges(branchPath, classificationId, conceptId, userId, 0, 10000);
		for (IRelationshipChange relationshipChange : relationshipChanges.getItems()) {
			switch (relationshipChange.getChangeNature()) {
				case REDUNDANT:
					relationships.remove(findRelationship(relationships, relationshipChange));
					break;
				case INFERRED:
					final SnomedBrowserRelationship inferred = new SnomedBrowserRelationship();
					inferred.setType(new SnomedBrowserRelationshipType(relationshipChange.getTypeId()));
					inferred.setSourceId(relationshipChange.getSourceId());

					final SnomedConcept targetConcept = SnomedRequests.prepareGetConcept(relationshipChange.getDestinationId())
							.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
							.execute(bus)
							.getSync();
					final SnomedBrowserRelationshipTarget relationshipTarget = browserService.getSnomedBrowserRelationshipTarget(targetConcept, branchPath, locales);
					inferred.setTarget(relationshipTarget);

					inferred.setGroupId(relationshipChange.getGroup());
					inferred.setModifier(relationshipChange.getModifier());
					inferred.setActive(true);
					inferred.setCharacteristicType(CharacteristicType.INFERRED_RELATIONSHIP);

					relationships.add(inferred);
					break;
			}
		}
		return conceptDetails;
	}

	private ISnomedBrowserRelationship findRelationship(List<ISnomedBrowserRelationship> relationships, IRelationshipChange relationshipChange) {
		for (ISnomedBrowserRelationship relationship : relationships) {
			if (relationship.isActive()
					&& relationship.getSourceId().equals(relationshipChange.getSourceId())
					&& relationship.getType().getConceptId().equals(relationshipChange.getTypeId())
					&& relationship.getTarget().getConceptId().equals(relationshipChange.getDestinationId())
					&& relationship.getGroupId() == relationshipChange.getGroup()
					&& relationship.getCharacteristicType().equals(CharacteristicType.INFERRED_RELATIONSHIP)
					&& relationship.getModifier().equals(relationshipChange.getModifier())) {					
				return relationship;
			}
		}
		return null;
	}

	@Override
	public void persistChanges(final String branchPath, final String classificationId, final String userId) {
		// Check if it exists
		IClassificationRun classificationRun = getClassificationRun(branchPath, classificationId, userId);

		if (!ClassificationStatus.COMPLETED.equals(classificationRun.getStatus())) {
			return;
		}

		final PersistChangesResponse persistChanges = getReasonerService().persistChanges(classificationId, userId);
		final ClassificationStatus saveStatus;

		switch (persistChanges.getType()) {
			case NOT_AVAILABLE:
				saveStatus = ClassificationStatus.STALE;
				break;
			case SUCCESS:
				saveStatus = ClassificationStatus.SAVING_IN_PROGRESS;
				break;
			default:
				throw new IllegalStateException(MessageFormat.format("Unhandled persist change response type ''{0}''.", persistChanges.getType()));
		}
		
		try {
			indexService.updateClassificationRunStatus(classificationId, saveStatus);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeClassificationRun(final String branchPath, final String classificationId, final String userId) {
		
		JobRequests.prepareDelete(classificationId)
				.buildAsync()
				.execute(getEventBus())
				.then(ignored -> {
					try {
						indexService.deleteClassificationData(classificationId);
					} catch (IOException e) {
						LOG.error("Caught IOException while deleting classification data for ID {}.", classificationId, e);
					}
					return ignored;
				})
				.getSync();
		
	}

}
