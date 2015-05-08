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
package com.b2international.snowowl.datastore.server.cdo;

import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.server.index.IndexServerServiceManager;

/**
 * Creates task branches and freezes state in the parent branch index, so a differencing task index can be based on it.
 */
public class PrepareBranchAction extends AbstractCDOBranchAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrepareBranchAction.class);

	public PrepareBranchAction(final IBranchPathMap branchPathMap, final String userId) {
		super(branchPathMap, userId, DatastoreLockContextDescriptions.PREPARE);
	}

	@Override
	protected void apply(final String repositoryId, final IBranchPath taskBranchPath) {

		final ICDOConnection connection = getConnectionManager().getByUuid(repositoryId);
		final IIndexUpdater<IIndexEntry> indexService = IndexServerServiceManager.INSTANCE.getByUuid(repositoryId);

		if (taskBranchPath == null) {
			return;
		}

		if (BranchPathUtils.isMain(taskBranchPath)) {
			return;
		}

		final IBranchPath parentBranchPath = taskBranchPath.getParent();
		final CDOBranch parentBranch = connection.getBranch(parentBranchPath);

		if (parentBranch == null) {
			throw new IllegalStateException(MessageFormat.format("Parent branch ''{0}'' not found on connection ''{1}''.", parentBranchPath.getPath(), connection.getUuid()));
		}

		CDOBranch taskBranch = connection.getBranch(taskBranchPath);

		final String message = MessageFormat.format("Changing to {0} in ''{1}''...", taskBranchPath.getPath(), connection.getRepositoryName());
		LOGGER.info(message);
		LogUtils.logUserEvent(LOGGER, getUserId(), parentBranchPath, message);

		if (taskBranch == null) {
			taskBranch = parentBranch.createBranch(taskBranchPath.lastSegment());
			indexService.snapshotFor(taskBranchPath, false, false);
		}

		indexService.prepare(taskBranchPath);
	}
}
