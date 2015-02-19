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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;

import com.b2international.snowowl.core.ApplicationContext;
import com.google.common.base.Preconditions;

/**
 * Empty {@link CDOCommitInfo commit info} with a specified {@link CDOCommitInfo#getBranch() branch},
 * {@link CDOCommitInfo#getUserID() user ID} and a {@link CDOCommitInfo#getComment() comment}.
 */
public class EmptyCDOCommitInfo implements CDOCommitInfo {

	private final CDOBranch branch;
	private final String userId;
	private final String comment;
	private final long timestamp;
	private final long previousTimestamp;

	public EmptyCDOCommitInfo(final CDOBranch branch, final String userId, final String comment, final long timestamp, final long previousTimestamp) {
		this.branch = Preconditions.checkNotNull(branch, "Branch argument cannot be null.");
		this.userId = Preconditions.checkNotNull(userId, "User ID argument cannot be null.");
		this.comment = Preconditions.checkNotNull(comment, "Commit comment argument cannot be null.");
		this.timestamp = timestamp;
		this.previousTimestamp = previousTimestamp;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.branch.CDOBranchPoint#getBranch()
	 */
	@Override
	public CDOBranch getBranch() {
		return branch;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.branch.CDOBranchPoint#getTimeStamp()
	 */
	@Override
	public long getTimeStamp() {
		return timestamp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOCommitData#getNewPackageUnits()
	 */
	@Override
	public List<CDOPackageUnit> getNewPackageUnits() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#copy()
	 */
	@Override
	public CDOChangeSetData copy() {
		return EmptyCDOChangeSetData.INSTANCE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#merge(org.eclipse.emf.cdo.common.commit.CDOChangeSetData)
	 */
	@Override
	public void merge(CDOChangeSetData changeSetData) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#getNewObjects()
	 */
	@Override
	public List<CDOIDAndVersion> getNewObjects() {
		return EmptyCDOChangeSetData.INSTANCE.getNewObjects();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#getChangedObjects()
	 */
	@Override
	public List<CDORevisionKey> getChangedObjects() {
		return EmptyCDOChangeSetData.INSTANCE.getChangedObjects();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#getDetachedObjects()
	 */
	@Override
	public List<CDOIDAndVersion> getDetachedObjects() {
		return EmptyCDOChangeSetData.INSTANCE.getDetachedObjects();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#getChangeKinds()
	 */
	@Override
	public Map<CDOID, CDOChangeKind> getChangeKinds() {
		return EmptyCDOChangeSetData.INSTANCE.getChangeKinds();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeKindProvider#getChangeKind(org.eclipse.emf.cdo.common.id.CDOID)
	 */
	@Override
	public CDOChangeKind getChangeKind(CDOID id) {
		return EmptyCDOChangeSetData.INSTANCE.getChangeKind(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOCommitInfo#getCommitInfoManager()
	 */
	@Override
	public CDOCommitInfoManager getCommitInfoManager() {
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final ICDOConnection connection = connectionManager.get(branch);
		final CDONet4jSession session = connection.getSession();
		final CDOCommitInfoManager $ = session.getCommitInfoManager();
		return $;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOCommitInfo#getPreviousTimeStamp()
	 */
	@Override
	public long getPreviousTimeStamp() {
		return previousTimestamp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOCommitInfo#getUserID()
	 */
	@Override
	public String getUserID() {
		return userId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOCommitInfo#getComment()
	 */
	@Override
	public String getComment() {
		return comment;
	}


}