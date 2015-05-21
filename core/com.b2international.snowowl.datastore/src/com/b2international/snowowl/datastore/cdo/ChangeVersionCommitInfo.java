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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

import com.b2international.commons.StringUtils;
import com.b2international.commons.Triple;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.google.common.base.Preconditions;

/**
 * Customized {@link CDOCommitInfo commit info} representing a version change event.
 *
 */
public class ChangeVersionCommitInfo extends EmptyCDOCommitInfo {

	public static ChangeVersionCommitInfo create(final String repositoryUuid, final String targetVersion) {
		final Triple<CDOBranch, String, String> triple = init(repositoryUuid, targetVersion);
		final IBranchPath branchPath = BranchPathUtils.createPath(triple.getA());
		final CDOCommitInfo commitInfo = CDOCommitInfoUtils.getLatestCommitInfo(BranchPointUtils.create(repositoryUuid, branchPath));
		return new ChangeVersionCommitInfo(triple.getA(), triple.getB(), triple.getC(), commitInfo.getTimeStamp(), commitInfo.getPreviousTimeStamp());
	}
	
	private ChangeVersionCommitInfo(final CDOBranch branch, final String userId, final String comment, long timestamp, long previousTimestamp) {
		super(branch, userId, comment, timestamp, previousTimestamp);
	}
	
	private static final Triple<CDOBranch, String, String> init(final String repositoryUuid, final String targetVersion) {
		
		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
		Preconditions.checkNotNull(targetVersion, "Target version argument cannot be null.");
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final ICDOConnection connection = connectionManager.getByUuid(repositoryUuid);
		
		Preconditions.checkNotNull(connection, "Connection does not exist with repository UUID: " +  repositoryUuid);
		final IBranchPath branchPath = IBranchPath.MAIN_BRANCH.equals(targetVersion) 
				? BranchPathUtils.createMainPath() 
				: BranchPathUtils.createPath(BranchPathUtils.createMainPath(), targetVersion);
		
		final CDOBranch cdoBranch = connection.getBranch(branchPath);
		Preconditions.checkNotNull(cdoBranch, "Target version does not exist: " + targetVersion);
		
		final String toolingName = CodeSystemUtils.getSnowOwlToolingName(repositoryUuid);
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Switching to version '");
		sb.append((IBranchPath.MAIN_BRANCH.equals(targetVersion) ? "HEAD" : targetVersion));
		sb.append("' in '");
		sb.append(StringUtils.isEmpty(toolingName) ? connection.getRepositoryName() : toolingName);
		sb.append("'.");
		final String comment = sb.toString();
		final String userId = connectionManager.getUserId();
		
		return Triple.of(cdoBranch, userId, comment);
		
	}

}