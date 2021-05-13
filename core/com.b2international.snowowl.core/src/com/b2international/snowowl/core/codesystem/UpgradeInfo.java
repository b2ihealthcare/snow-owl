/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import java.io.Serializable;
import java.util.List;

import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.snowowl.core.branch.BranchInfo;
import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
 * @since 7.17.0
 */
public final class UpgradeInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	BranchInfo mainBranchInfo;
	List<BranchInfo> versionBranchInfos;
	List<CodeSystemURI> blockedURIs;
	
	public UpgradeInfo(BranchInfo mainBranchInfo, List<BranchInfo> versionBranchInfos, List<CodeSystemURI> blockedURIs) {
		this.mainBranchInfo = mainBranchInfo;
		this.versionBranchInfos = versionBranchInfos;
		this.blockedURIs = blockedURIs;
	}
	
	public BranchInfo getMainBranchInfo() {
		return mainBranchInfo;
	}
	
	public List<BranchInfo> getVersionBranchInfos() {
		return versionBranchInfos;
	}
	
	public List<CodeSystemURI> getBlockedURIs() {
		return blockedURIs;
	}
	
	public boolean isBlocked() {
		return mainBranchInfo.getState()  == BranchState.DIVERGED || mainBranchInfo.getState()  == BranchState.BEHIND;
	}
	
}
