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
package com.b2international.snowowl.datastore.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.commons.ChangeKind;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IHistoryInfo;
import com.b2international.snowowl.core.api.IHistoryInfoDetails;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("DetachedHistoryInfo")
public final class DetachedHistoryInfo implements IHistoryInfo, Serializable {
	
	private static final long serialVersionUID = 3769287564795577607L;

	@XStreamAlias("timeStamp")
	private long timeStamp;
	
	@XStreamAlias("version")
	private IVersion<CDOID> version;
	
	@XStreamAlias("author")
	private String author;
	
	@XStreamAlias("comments")
	private String comments;
	
	@XStreamAlias("incomplete")
	private boolean incomplete;
	
	@XStreamImplicit(itemFieldName="historyDetails")
	private final List<IHistoryInfoDetails> details = new ArrayList<IHistoryInfoDetails>();
	
	public DetachedHistoryInfo(final CDOBranch branch, final CDOID cdoId) {
		
		CDOView auditView = null;
		
		try {
			
			final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
			final ICDOConnection connection = connectionManager.get(cdoId);
			final CDONet4jSession session = connection.getSession();
			final CDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
			
			CDOCommitInfo commitInfo = commitInfoManager.getCommitInfo(session.getLastUpdateTime());
			CDOCommitInfo lastDetachedCommitInfo = null;
			auditView = session.openView(branch, commitInfo.getTimeStamp(), false);
			
			// Find the cdo view where the concept still exists
			while (commitInfo.getTimeStamp() != commitInfo.getPreviousTimeStamp() && null == CDOUtils.getObjectIfExists(auditView, cdoId)) {
				lastDetachedCommitInfo = commitInfo;
				final long previousTimeStamp = lastDetachedCommitInfo.getPreviousTimeStamp();
				commitInfo = commitInfoManager.getCommitInfo(previousTimeStamp);
				auditView.setTimeStamp(previousTimeStamp);
			}
			
			// If we didn't go back to the repository creation commit, there will be some useful info on lastDetachedCommitInfo
			if (commitInfo.getTimeStamp() != session.getRepositoryInfo().getCreationTime()) {
				this.comments = lastDetachedCommitInfo.getComment();
				this.author = lastDetachedCommitInfo.getUserID();
				this.timeStamp = lastDetachedCommitInfo.getTimeStamp();
			} else {
				initializeUnknownAttributes();
			}
			
			this.version = new Version(-1);
			if (version instanceof Version) {
				((Version) this.version).addAffectedObjectId(cdoId, commitInfo.getTimeStamp());
			}
			this.details.add(new HistoryInfoDetails("Deleted component", "Deleted from database.", ChangeKind.DELETED));
			
		} finally {
			LifecycleUtil.deactivate(auditView);
		}
	}

	@Override
	public IVersion<?> getVersion() {
		return version;
	}
	
	@Override
	public long getTimeStamp() {
		return timeStamp;
	}
	
	@Override
	public String getAuthor() {
		return author;
	}
	
	@Override
	public String getComments() {
		return comments;
	}

	@Override
	public List<IHistoryInfoDetails> getDetails() {
		return Collections.unmodifiableList(details);
	}
	
	@Override
	public boolean isIncomplete() {
		return incomplete;
	}
	
	private void initializeUnknownAttributes() {
		this.comments = "Unknown";
		this.author = "Unknown";
		this.timeStamp =System.currentTimeMillis();
	}
}