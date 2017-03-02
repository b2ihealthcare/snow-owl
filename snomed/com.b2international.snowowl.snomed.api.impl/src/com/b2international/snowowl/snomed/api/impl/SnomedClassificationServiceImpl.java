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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.status.SerializableStatus;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.remotejobs.AbstractRemoteJobEvent;
import com.b2international.snowowl.datastore.remotejobs.IRemoteJobManager;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobChangedEvent;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEventBusHandler;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEventSwitch;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobState;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils;
import com.b2international.snowowl.datastore.server.domain.StorageRef;
import com.b2international.snowowl.datastore.server.index.SingleDirectoryIndexManager;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
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
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationRequest;
import com.b2international.snowowl.snomed.reasoner.classification.GetResultResponse;
import com.b2international.snowowl.snomed.reasoner.classification.PersistChangesResponse;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerServiceUtil;
import com.google.common.io.Closeables;

/**
 */
public class SnomedClassificationServiceImpl implements ISnomedClassificationService {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedClassificationServiceImpl.class);
	
	private final class PersistenceCompletionHandler implements IHandler<IMessage> {

		private final UUID uuid;

		private PersistenceCompletionHandler(final UUID uuid) {
			this.uuid = uuid;
		}

		@Override
		public void handle(final IMessage message) {
			try {

				final SerializableStatus result = message.body(SerializableStatus.class);
				if (result.isOK()) {
					indexService.updateClassificationRunStatus(uuid, ClassificationStatus.SAVED);
				} else {
					indexService.updateClassificationRunStatus(uuid, ClassificationStatus.SAVE_FAILED);
				}

			} catch (final IOException e) {
				LOG.error("Caught IOException while updating classification status after save.", e);
			} finally {
				getEventBus().unregisterHandler(SnomedReasonerServiceUtil.getChangesPersistedAddress(uuid), this);
			}
		}
	}

	private final class RemoteJobChangeHandler implements IHandler<IMessage> {
		@Override
		public void handle(final IMessage message) {
			new RemoteJobEventSwitch() {

				@Override
				protected void caseChanged(final RemoteJobChangedEvent event) {

					try {

						if (RemoteJobEntry.PROP_STATE.equals(event.getPropertyName())) {
							final RemoteJobState newState = (RemoteJobState) event.getNewValue();
							final UUID id = event.getId();

							switch (newState) {
								case CANCEL_REQUESTED:
									// Nothing to do
									break;
								case FAILED:
									indexService.updateClassificationRunStatus(id, ClassificationStatus.FAILED);
									break;
								case FINISHED: 
									// Handled in RemoteJobCompletionHandler
									break;
								case RUNNING:
									indexService.updateClassificationRunStatus(id, ClassificationStatus.RUNNING);
									break;
								case SCHEDULED:
									// Nothing to do
									break;
								default:
									throw new IllegalStateException(MessageFormat.format("Unexpected remote job state ''{0}''.", newState));
							}
						}

					} catch (final IOException e) {
						LOG.error("Caught IOException while updating classification status.", e);
					}
				}

			}.doSwitch(message.body(AbstractRemoteJobEvent.class));
		}
	}

	private final class RemoteJobCompletionHandler extends RemoteJobEventBusHandler {

		public RemoteJobCompletionHandler(final UUID remoteJobId) {
			super(remoteJobId);
		}

		@Override
		protected void handleResult(final UUID remoteJobId, final boolean cancelRequested) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						
						if (cancelRequested) {
							indexService.updateClassificationRunStatus(remoteJobId, ClassificationStatus.CANCELED);
							return;
						} 

						final GetResultResponse result = getReasonerService().getResult(remoteJobId);
						final Type responseType = result.getType();

						switch (responseType) {
							case NOT_AVAILABLE: 
								indexService.updateClassificationRunStatus(remoteJobId, ClassificationStatus.FAILED);
								break;
							case STALE: 
								indexService.updateClassificationRunStatus(remoteJobId, ClassificationStatus.STALE);
								break;
							case SUCCESS:
								indexService.updateClassificationRunStatus(remoteJobId, ClassificationStatus.COMPLETED, result.getChanges());
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
	}

	private ClassificationRunIndex indexService;
	private RemoteJobChangeHandler changeHandler;
	private ExecutorService executorService;

	@Resource
	private SnomedBrowserService browserService;
	
	@Resource
	private IEventBus bus;
	
	@Resource
	private int maxReasonerRuns;

	@PostConstruct
	protected void init() {
		LOG.info("Initializing classification service; keeping indexed data for {} recent run(s).", maxReasonerRuns); 
		
		final File dir = new File(new File(SnowOwlApplication.INSTANCE.getEnviroment().getDataDirectory(), "indexes"), "classification_runs");
		indexService = new ClassificationRunIndex(dir);
		ApplicationContext.getInstance().getServiceChecked(SingleDirectoryIndexManager.class).registerIndex(indexService);

		try {
			indexService.trimIndex(maxReasonerRuns);
			indexService.invalidateClassificationRuns();
		} catch (final IOException e) {
			LOG.error("Failed to run housekeeping tasks for the classification index.", e);
		}

		// TODO: common ExecutorService for asynchronous work?
		executorService = Executors.newCachedThreadPool(); 
		changeHandler = new RemoteJobChangeHandler();
		getEventBus().registerHandler(IRemoteJobManager.ADDRESS_REMOTE_JOB_CHANGED, changeHandler);
	}

	@PreDestroy
	protected void shutdown() {
		getEventBus().unregisterHandler(IRemoteJobManager.ADDRESS_REMOTE_JOB_CHANGED, changeHandler);
		changeHandler = null;

		if (null != executorService) {
			executorService.shutdown();
			executorService = null;
		}
		
		if (null != indexService) {
			ApplicationContext.getInstance().getServiceChecked(SingleDirectoryIndexManager.class).unregisterIndex(indexService);
			Closeables.closeQuietly(indexService);
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

	private static IRemoteJobManager getRemoteJobManager() {
		return ApplicationContext.getServiceForClass(IRemoteJobManager.class);
	}

	@Override
	public List<IClassificationRun> getAllClassificationRuns(final String branchPath, final String userId) {

		final StorageRef storageRef = createStorageRef(branchPath);

		try {
			return indexService.getAllClassificationRuns(storageRef, userId);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IClassificationRun beginClassification(final String branchPath, final String reasonerId, final String userId) {

		final StorageRef storageRef = createStorageRef(branchPath);
		final IBranchPath oldBranchPath = storageRef.getBranch().branchPath();

		final ClassificationRequest classificationRequest = new ClassificationRequest(userId, oldBranchPath)
		.withParentContextDescription(DatastoreLockContextDescriptions.ROOT)
		.withReasonerId(reasonerId);

		final UUID remoteJobId = classificationRequest.getClassificationId();
		getEventBus().registerHandler(RemoteJobUtils.getJobSpecificAddress(IRemoteJobManager.ADDRESS_REMOTE_JOB_COMPLETED, remoteJobId), new RemoteJobCompletionHandler(remoteJobId));

		final ClassificationRun classificationRun = new ClassificationRun();
		classificationRun.setId(remoteJobId.toString());
		classificationRun.setReasonerId(reasonerId);
		classificationRun.setCreationDate(new Date());
		classificationRun.setUserId(userId);
		classificationRun.setStatus(ClassificationStatus.SCHEDULED);

		try {
			indexService.upsertClassificationRun(oldBranchPath, classificationRun);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		getReasonerService().beginClassification(classificationRequest);
		return classificationRun;
	}

	@Override
	public IClassificationRun getClassificationRun(final String branchPath, final String classificationId, final String userId) {

		final StorageRef storageRef = createStorageRef(branchPath);

		try {
			return indexService.getClassificationRun(storageRef, classificationId, userId);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<IEquivalentConceptSet> getEquivalentConceptSets(final String branchPath, final String classificationId, final List<ExtendedLocale> locales, final String userId) {
		// Check if it exists
		getClassificationRun(branchPath, classificationId, userId);
		final StorageRef storageRef = createStorageRef(branchPath);

		try {
			final List<IEquivalentConceptSet> conceptSets = indexService.getEquivalentConceptSets(storageRef, classificationId, userId);
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

		final StorageRef storageRef = createStorageRef(branchPath);

		try {
			return indexService.getRelationshipChanges(storageRef, classificationId, conceptId, userId, offset, limit);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ISnomedBrowserConcept getConceptPreview(String branchPath, String classificationId, String conceptId, List<ExtendedLocale> locales, String userId) {
		final SnomedBrowserConcept conceptDetails = (SnomedBrowserConcept) browserService.getConceptDetails(SnomedServiceHelper.createComponentRef(branchPath, conceptId), locales);

		// Replace ImmutableCollection of relationships
		final List<ISnomedBrowserRelationship> relationships = new ArrayList<ISnomedBrowserRelationship>(conceptDetails.getRelationships());
		conceptDetails.setRelationships(relationships);

		final IRelationshipChangeList relationshipChanges = getRelationshipChanges(branchPath, classificationId, conceptId, userId, 0, 10000);
		for (IRelationshipChange relationshipChange : relationshipChanges.getChanges()) {
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

		final UUID uuid = UUID.fromString(classificationId);
		final String address = SnomedReasonerServiceUtil.getChangesPersistedAddress(uuid);
		final PersistenceCompletionHandler handler = new PersistenceCompletionHandler(uuid);
		getEventBus().registerHandler(address, handler);

		final PersistChangesResponse persistChanges = getReasonerService().persistChanges(uuid, userId);
		if (!Type.SUCCESS.equals(persistChanges.getType())) {
			// We will never get a reply, unregister immediately
			getEventBus().unregisterHandler(address, handler);
		} 
		
		final ClassificationStatus saveStatus;
		switch (persistChanges.getType()) {
			case NOT_AVAILABLE:
			case STALE:
				saveStatus = ClassificationStatus.STALE;
				break;
			case SUCCESS:
				saveStatus = ClassificationStatus.SAVING_IN_PROGRESS;
				break;
			default:
				throw new IllegalStateException(MessageFormat.format("Unhandled persist change response type ''{0}''.", persistChanges.getType()));
		}
		
		try {
			indexService.updateClassificationRunStatus(uuid, saveStatus);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeClassificationRun(final String branchPath, final String classificationId, final String userId) {
		// Check if it exists
		getClassificationRun(branchPath, classificationId, userId);
		getRemoteJobManager().cancelRemoteJob(UUID.fromString(classificationId));
		
		try {
			indexService.deleteClassificationData(classificationId);
		} catch (final IOException e) {
			LOG.error("Caught IOException while deleting classification data for ID {}.", classificationId, e);
		}					
	}

	private StorageRef createStorageRef(final String branchPath) {
		final StorageRef storageRef = new StorageRef(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath);
		storageRef.checkStorageExists();
		return storageRef;
	}
}
