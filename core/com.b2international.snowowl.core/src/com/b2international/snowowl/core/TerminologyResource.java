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
package com.b2international.snowowl.core;

import java.util.List;
import java.util.Map;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchInfo;
import com.b2international.snowowl.core.codesystem.UpgradeInfo;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.b2international.snowowl.core.version.Versions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @since 8.0
 */
public abstract class TerminologyResource extends Resource {

	private static final long serialVersionUID = 1L;

	/**
	 * @since 8.0
	 */
	public static final class Expand {
		public static final String AVAILABLE_UPGRADES = "availableUpgrades";
		public static final String EXTENSION_OF_BRANCH_INFO = "extensionOfBranchInfo";
		public static final String UPGRADE_INFO = "upgradeInfo";
		public static final String VERSIONS = "versions";
	}
	
	// standard oid
	private String oid;
	
	// the current working branch
	private String branchPath;
	
	// identifies the tooling behind this resource, never null, should be picked from the available toolings/schemas
	private String toolingId;

	// used, when this resource is an upgrade of another TerminologyResource to a newer extensionOf resource (aka dependency)
	private ResourceURI extensionOf;

	// used, when this resource is an extension of another TerminologyResource
	private ResourceURI upgradeOf;
	
	// formerly additionalProperties in 7.x
	private Map<String, Object> settings;
	
	// expandable
	private BranchInfo extensionOfBranchInfo;
	
	private UpgradeInfo upgradeInfo;
	
	private List<ResourceURI> availableUpgrades;
	
	private Versions versions;

	/**
	 * @return the assigned object identifier (OID) of this code system, eg. "{@code 3.4.5.6.10000}" (can be {@code null})
	 */
	public String getOid() {
		return oid;
	}
	
	/**
	 * @return the working branch path for the resource, eg. "{@code MAIN/2018-07-31/SNOMEDCT-EXT}"
	 */
	public String getBranchPath() {
		return branchPath;
	}
	
	public String getToolingId() {
		return toolingId;
	}
	
	public ResourceURI getExtensionOf() {
		return extensionOf;
	}

	public BranchInfo getExtensionOfBranchInfo() {
		return extensionOfBranchInfo;
	}
	
	public ResourceURI getUpgradeOf() {
		return upgradeOf;
	}
	
	public UpgradeInfo getUpgradeInfo() {
		return upgradeInfo;
	}
	
	/**
	 * A configuration map storing additional key-value pairs specific to this terminology resource (can be {@code null}). Interpretation of values is
	 * implementation-dependent.
	 * 
	 * @return
	 */
	public Map<String, Object> getSettings() {
		return settings;
	}
	
	/**
	 * @return a list of {@link ResourceURI}s pointing to resource versions that have been created after the current {{@link #getExtensionOf()} version
	 *         on the parent resource (can be {@code null} if not requested as part of an expand() option)
	 */
	public List<ResourceURI> getAvailableUpgrades() {
		return availableUpgrades;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public void setToolingId(String toolingId) {
		this.toolingId = toolingId;
	}
	
	public void setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
	}
	
	public void setExtensionOfBranchInfo(BranchInfo extensionOfBranchInfo) {
		this.extensionOfBranchInfo = extensionOfBranchInfo;
	}
	
	public void setUpgradeOf(ResourceURI upgradeOf) {
		this.upgradeOf = upgradeOf;
	}
	
	public void setUpgradeInfo(UpgradeInfo upgradeInfo) {
		this.upgradeInfo = upgradeInfo;
	}
	
	public void setSettings(Map<String, Object> settings) {
		this.settings = settings;
	}
	
	public void setAvailableUpgrades(List<ResourceURI> availableUpgrades) {
		this.availableUpgrades = availableUpgrades;
	}
	
	public Versions getVersions() {
		return versions;
	}
	
	public void setVersions(Versions versions) {
		this.versions = versions;
	}
	
	/**
	 * @return a new branch path that originates from this resource's branch path
	 */
	@JsonIgnore
	public String getRelativeBranchPath(String relativeTo) {
		return String.join(Branch.SEPARATOR, branchPath, relativeTo);
	}
	
	/**
	 * @return the {@link ResourceURI} of this resource uri at the given branch, if the branch is empty it returns the HEAD of this {@link TerminologyResource}.
	 */
	@JsonIgnore
	public ResourceURI getResourceURI(String branch) {
		Preconditions.checkNotNull(branch, "Branch argument should not be null");
		if (!Strings.isNullOrEmpty(branch)) {
			Preconditions.checkArgument(branch.startsWith(branchPath), "Branch argument '%s' should start with Code System working branch '%s'.", branch, branchPath);
			final String relativePath = branch.replaceFirst(branchPath, "").replaceFirst("/", "");
			return relativePath.isEmpty() ? getResourceURI() : getResourceURI().withPath(relativePath);
		} else {
			return getResourceURI();
		}
	}
	
	@Override
	public Builder toDocumentBuilder() {
		return ResourceDocument.builder()
				.resourceType(getResourceType())
				.id(getId())
				.url(getUrl())
				.title(getTitle())
				.language(getLanguage())
				.description(getDescription())
				.status(getStatus())
				.copyright(getCopyright())
				.owner(getOwner())
				.contact(getContact())
				.usage(getUsage())
				.purpose(getPurpose())
				.bundleId(getBundleId())
				.branchPath(getBranchPath())
				.extensionOf(getExtensionOf())
				.oid(getOid())
				.settings(getSettings())
				.toolingId(getToolingId())
				.upgradeOf(getUpgradeOf());
	}
	
}
