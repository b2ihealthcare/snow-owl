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
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.BranchInfo;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.17.0
 */
public final class UpgradeInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	BranchInfo upgradeOfBranchInfo;
	List<BranchInfo> versionBranchInfos;
	List<ResourceURI> availableVersions;
	
	@JsonCreator
	public UpgradeInfo(
			@JsonProperty("upgradeOfBranchInfo") BranchInfo upgradeOfBranchInfo, 
			@JsonProperty("versionBranchInfos") List<BranchInfo> versionBranchInfos, 
			@JsonProperty("availableVersions") List<ResourceURI> availableVersions) {
		this.upgradeOfBranchInfo = upgradeOfBranchInfo;
		this.versionBranchInfos = versionBranchInfos;
		this.availableVersions = availableVersions;
	}
	
	// returns the BranchInfo of the upgradeOf Code System's HEAD
	public BranchInfo getUpgradeOfBranchInfo() {
		return upgradeOfBranchInfo;
	}
	
	public List<BranchInfo> getVersionBranchInfos() {
		return versionBranchInfos;
	}
	
	public List<ResourceURI> getAvailableVersions() {
		return availableVersions;
	}
	
	@JsonIgnore
	public boolean isBlocked() {
		return upgradeOfBranchInfo.getState()  == BranchState.DIVERGED || upgradeOfBranchInfo.getState()  == BranchState.BEHIND;
	}
	
}
