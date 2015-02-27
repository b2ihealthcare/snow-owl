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
package com.b2international.snowowl.snomed.api.impl;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.b2international.commons.status.SerializableStatus;
import com.b2international.snowowl.api.impl.domain.StorageRef;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.remotejobs.AbstractRemoteJobEvent;
import com.b2international.snowowl.datastore.remotejobs.IRemoteJobManager;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobChangedEvent;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEventBusHandler;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEventSwitch;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobRemovedEvent;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobState;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.snomed.api.ISnomedClassificationService;
import com.b2international.snowowl.snomed.api.domain.classification.ClassificationStatus;
import com.b2international.snowowl.snomed.api.domain.classification.IClassificationRun;
import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConceptSet;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChangeList;
import com.b2international.snowowl.snomed.api.impl.domain.classification.ClassificationRun;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractResponse.Type;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationRequest;
import com.b2international.snowowl.snomed.reasoner.classification.GetResultResponse;
import com.b2international.snowowl.snomed.reasoner.classification.PersistChangesResponse;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerServiceUtil;

/**
 */
public class SnomedClassificationServiceImpl implements ISnomedClassificationService {

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
				throw new RuntimeException(e);
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
						e.printStackTrace();
					}
				}

				@Override
				protected void caseRemoved(final RemoteJobRemovedEvent event) {

					try {
						indexService.deleteClassificationData(event.getId());
					} catch (final IOException e) {
						e.printStackTrace();
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

			try {

				if (!cancelRequested) {

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
							indexService.updateClassificationRunStatus(remoteJobId, ClassificationStatus.COMPLETED);
							indexService.indexChanges(result.getChanges());
							break;
						default:
							throw new IllegalStateException(MessageFormat.format("Unexpected response type ''{0}''.", responseType));
					}

				} else {
					indexService.updateClassificationRunStatus(remoteJobId, ClassificationStatus.CANCELED);
				}

			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ClassificationIndexServerService indexService;
	private RemoteJobChangeHandler changeHandler;

	@PostConstruct
	protected void init() {
		indexService = new ClassificationIndexServerService(new File("classification_runs"));

		changeHandler = new RemoteJobChangeHandler();
		getEventBus().registerHandler(IRemoteJobManager.ADDRESS_REMOTE_JOB_CHANGED, changeHandler);
	}

	@PreDestroy
	protected void shutdown() {
		getEventBus().unregisterHandler(IRemoteJobManager.ADDRESS_REMOTE_JOB_CHANGED, changeHandler);
		changeHandler = null;

		if (null != indexService) {
			indexService.dispose();
			indexService = null;
		}
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
	public List<IClassificationRun> getAllClassificationRuns(final String version, final String taskId, final String userId) {

		final StorageRef storageRef = createStorageRef(version, taskId);

		try {
			return indexService.getAllClassificationRuns(storageRef, userId);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IClassificationRun beginClassification(final String version, final String taskId, final String reasonerId, final String userId) {

		final StorageRef storageRef = createStorageRef(version, taskId);

		final ClassificationRequest classificationRequest = new ClassificationRequest(userId, storageRef.getBranchPath())
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
			indexService.insertOrUpdateClassificationRun(version, storageRef.getBranchPath(), classificationRun);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		getReasonerService().beginClassification(classificationRequest);
		return classificationRun;
	}

	@Override
	public IClassificationRun getClassificationRun(final String version, final String taskId, final String classificationId, final String userId) {

		final StorageRef storageRef = createStorageRef(version, taskId);

		try {
			return indexService.getClassificationRun(storageRef, classificationId, userId);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<IEquivalentConceptSet> getEquivalentConceptSets(final String version, final String taskId, final String classificationId, final String userId) {
		// Check if it exists
		getClassificationRun(version, taskId, classificationId, userId);

		final StorageRef storageRef = createStorageRef(version, taskId);

		try {
			return indexService.getEquivalentConceptSets(storageRef, classificationId, userId);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IRelationshipChangeList getRelationshipChanges(final String version, final String taskId, final String classificationId, final String userId, final int offset, final int limit) {
		// Check if it exists
		getClassificationRun(version, taskId, classificationId, userId);

		final StorageRef storageRef = createStorageRef(version, taskId);

		try {
			return indexService.getRelationshipChanges(storageRef, classificationId, userId, offset, limit);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void persistChanges(final String version, final String taskId, final String classificationId, final String userId) {
		// Check if it exists
		IClassificationRun classificationRun = getClassificationRun(version, taskId, classificationId, userId);

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
		} else {
			// Set a flag to indicate that saving is under way
			try {
				indexService.updateClassificationRunStatus(uuid, ClassificationStatus.SAVING_IN_PROGRESS);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void removeClassificationRun(final String version, final String taskId, final String classificationId, final String userId) {
		// Check if it exists
		getClassificationRun(version, taskId, classificationId, userId);
		getRemoteJobManager().cancelRemoteJob(UUID.fromString(classificationId));
	}

	private StorageRef createStorageRef(final String version, final String taskId) {
		final StorageRef storageRef = new StorageRef();
		storageRef.setShortName("SNOMEDCT");
		storageRef.setVersion(version);
		storageRef.setTaskId(taskId);
		storageRef.checkStorageExists();
		return storageRef;
	}
}