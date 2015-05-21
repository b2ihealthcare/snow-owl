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
package com.b2international.snowowl.datastore.server.index.diff;

import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.isMain;
import static com.b2international.snowowl.datastore.server.index.IndexBranchService.getIndexCommit;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import org.apache.lucene.index.IndexCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.index.diff.IndexDifferService;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;
import com.b2international.snowowl.datastore.server.index.IndexBranchService;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.datastore.server.index.IndexServerServiceManager;
import com.b2international.snowowl.index.diff.IndexDiff;
import com.b2international.snowowl.index.diff.IndexDiffer;
import com.b2international.snowowl.index.diff.IndexDifferFactory;

/**
 * Index differ service implementation.
 *
 */
public class IndexDifferServiceImpl implements IndexDifferService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexDifferServiceImpl.class);
	
	@Override
	public IndexDiff calculateDiff(final VersionCompareConfiguration configuration) {
		return calculateDiff(getIndexService(checkNotNull(configuration, "configuration").getRepositoryUuid()), configuration);
	}

	private IndexDiff calculateDiff(final IndexServerService<?> indexService, final VersionCompareConfiguration configuration) {
		
		checkNotNull(indexService, "indexService");
		checkNotNull(configuration, "configuration");
		final IBranchPath sourceBranchPath = configuration.getSourcePath();
		checkArgument(!isMain(sourceBranchPath), "Source path argument cannot reference onto the MAIN branch.");

		final IndexBranchService targetBranchService = indexService.getBranchService(configuration.getTargetPath());
		
		log("Calculating index diff between '" + sourceBranchPath + "' and '" + configuration.getTargetPath() + "' for " + configuration.getToolingName() + "...");
		
		@Nullable final IndexCommit ancestorCommit;
		final IndexCommit sourceIndexCommit;
		
		if (configuration.isThreeWay()) {
			ancestorCommit = getIndexCommit(indexService.getBranchService(sourceBranchPath).getDirectory(), sourceBranchPath);
			sourceIndexCommit = indexService.getBranchService(createPath(sourceBranchPath)).getHeadIndexCommit();
		} else {
			final IndexBranchService sourceBranchService = indexService.getBranchService(sourceBranchPath);
			ancestorCommit = null;
			sourceIndexCommit = getIndexCommit(sourceBranchService.getDirectory(), sourceBranchPath);
		}
		
		final IndexCommit targetIndexCommit = targetBranchService.getHeadIndexCommit(); 
		
		final IndexDiffer differ = IndexDifferFactory.INSTANCE.createDiffer();
		final IndexDiff diff;
		
		if (configuration.isThreeWay()) {
			diff = differ.calculateDiff(ancestorCommit, sourceIndexCommit, targetIndexCommit);
		} else {
			diff = differ.calculateDiff(sourceIndexCommit, targetIndexCommit);
		}
		
		log(getDiffStatisticMessage(diff));
		
		return diff;
	}

	private String getDiffStatisticMessage(final IndexDiff diff) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Index diff calculation was successful. New: ");
		sb.append(diff.getNewIds().size());
		sb.append(" changed: ");
		sb.append(diff.getChangedIds().size());
		sb.append(" detached: ");
		sb.append(diff.getDetachedIds().size());
		sb.append(".");
		return sb.toString();
	}

	private IndexServerService<?> getIndexService(final String repositoryUuid) {
		return (IndexServerService<?>) IndexServerServiceManager.INSTANCE.getByUuid(checkNotNull(repositoryUuid, "repositoryUuid"));
	}

	private void log(final String message) {
		LOGGER.info(message);
	}
	
}