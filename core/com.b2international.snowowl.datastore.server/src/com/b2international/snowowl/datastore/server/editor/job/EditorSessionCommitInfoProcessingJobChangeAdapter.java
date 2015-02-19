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
package com.b2international.snowowl.datastore.server.editor.job;

import java.util.UUID;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.editor.job.EditorSessionCommitInfoProcessingJob;
import com.b2international.snowowl.datastore.editor.notification.MergeConflictNotificationMessage;
import com.b2international.snowowl.datastore.editor.notification.MergeWithRefreshNotificationMessage;
import com.b2international.snowowl.datastore.server.editor.session.EditorSession;

/**
 * @since 2.9
 */
public class EditorSessionCommitInfoProcessingJobChangeAdapter extends JobChangeAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(EditorSessionCommitInfoProcessingJobChangeAdapter.class);

	private final EditorSession<?> session;
	private final CDOEditingContext editingContext;
	
	public EditorSessionCommitInfoProcessingJobChangeAdapter(
			final EditorSession<?> session,
			final CDOEditingContext editingContext) {
		
		this.session = session;
		this.editingContext = editingContext;
	}

	@Override
	public void done(final IJobChangeEvent event) {
		try {
			processEvent(event);
		} finally {
			editingContext.close();
		}

	}

	private void processEvent(final IJobChangeEvent event) {
		if (event.getResult().isOK()) {
			if (event.getJob() instanceof EditorSessionCommitInfoProcessingJob) {
				final EditorSessionCommitInfoProcessingJob job = (EditorSessionCommitInfoProcessingJob) event.getJob();
				final UUID uuid = session.getUuid();
				
				// at each commit info processing inform the session about deleted objects
				session.getDeletedObjectStorageKeys().addAll(job.getDeletedObjectStorageKeys());
				
				switch (job.getAction()) {
				case MERGE_NO_REFRESH:
					session.loadComponents(editingContext);
					try {
						session.applySessionOperations();
					} catch (final SnowowlServiceException e) {
						session.pushNotification(uuid, new MergeConflictNotificationMessage(job.getAction(), "Merge conflicts."));
					}
					break;
				case MERGE_WITH_REFRESH:
					session.loadComponents(editingContext);
					try {
						session.applySessionOperations();
						session.pushNotification(uuid, new MergeWithRefreshNotificationMessage(job.getAction(), "Changes merged successfully."));
					} catch (final SnowowlServiceException e) {
						session.pushNotification(uuid, new MergeConflictNotificationMessage(job.getAction(), "Merge conflicts."));
					}
					break;
				case NONE:
					LOG.info("Store update doesn't affect session " + uuid);
					break;
				case CONFLICT:
					LOG.error("Conflict when merging.");
					session.pushNotification(uuid, new MergeConflictNotificationMessage(job.getAction(), "Merge conflicts."));
					break;
				default:
					throw new IllegalArgumentException("Unknown commit info action: " + job.getAction());
				}
			}
		}
	}

}