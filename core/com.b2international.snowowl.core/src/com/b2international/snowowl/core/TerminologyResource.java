/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchInfo;
import com.b2international.snowowl.core.codesystem.UpgradeInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
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
	 * @since 8.12
	 */
	public static class DependencyScope {
		
		/**
		 * Constant denoting a dependency as the base resource of this resource. Points to the resource (or versioned resource) that this resource is the extension of.
		 */
		public static final String EXTENSION_OF = "extensionOf";
		
		/**
		 * Constant denoting a dependency as the development version of this upgrade resource. Points to the resource that this resource is the upgrade of. 
		 */
		public static final String UPGRADE_OF = "upgradeOf";
	}
	
	/**
	 * @since 8.0
	 */
	public static abstract class Expand extends Resource.Expand {
		
		public static final String UPGRADE_INFO = "upgradeInfo";
		public static final String VERSIONS = "versions";
		public static final String COMMITS = "commits";
		
		/**
		 * Expand option to expand dependencies of a resource.
		 */
		public static final String DEPENDENCIES = "dependencies";
		
		/**
		 * Expand option to expand the current latest branch information based on the current {@link TerminologyResource#getBranchPath()} value.
		 */
		public static final String BRANCH = "branch";
		
		/**
		 * @deprecated - this expand option has been moved to the new {@link Dependency} model, will be removed from this model in 9.0
		 */
		public static final String AVAILABLE_UPGRADES = "availableUpgrades";

		/**
		 * @deprecated - this expand option has been moved to the new {@link Dependency} model and also to here as {@link #BRANCH}, will be removed from this model in 9.0
		 */
		public static final String EXTENSION_OF_BRANCH_INFO = "extensionOfBranchInfo";
		
//		public static final String DEPENDENCIES = 
		
	}
	
	// standard oid
	private String oid;
	
	// the current working branch
	private String branchPath;
	
	// identifies the tooling behind this resource, never null, should be picked from the available toolings/schemas
	private String toolingId;
	
	// identifies a set of resource dependencies this resource depends on 
	private List<Dependency> dependencies;

	// used, when this resource is an extension of another TerminologyResource
	@Deprecated
	private ResourceURI extensionOf;

	// used, when this resource is an upgrade of another TerminologyResource to a newer extensionOf resource (aka dependency)
	@Deprecated
	private ResourceURI upgradeOf;
	
	// expandable
	@Deprecated
	private BranchInfo extensionOfBranchInfo;
	
	@Deprecated
	private List<ResourceURI> availableUpgrades;
	
	private UpgradeInfo upgradeInfo;
	
	private Versions versions;
	private CommitInfos commits;

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
	
	/**
	 * @return the tooling/repository ID where this resource's content is being maintained
	 */
	public String getToolingId() {
		return toolingId;
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
	
	/**
	 * @return the dependencies of this terminology resource, if <code>null</code> or empty there are no dependencies from this resource to other resources
	 */
	public List<Dependency> getDependencies() {
		return dependencies;
	}
	
	/**
	 * @return the {@link ResourceURI} pointing to a resource this resource is an extension of 
	 * @deprecated - moved this information to {@link #getDependencies()}, this method will be removed in 9.0
	 */
	public ResourceURI getExtensionOf() {
		return extensionOf;
	}

	/**
	 * @return the latest {@link BranchInfo} state of the Resource denoted by the {@link #getExtensionOf()} property.
	 * @deprecated - moved this information to the {@link Dependency#getResource()} expansion, this method will be removed in 9.0 
	 */
	public BranchInfo getExtensionOfBranchInfo() {
		return extensionOfBranchInfo;
	}
	
	/**
	 * @return the {@link ResourceURI} pointing to a resource this resource is an upgrade of, this usually references the current non-upgrade point in time of the same resource
	 * @deprecated - moved this information to the {@link #getDependencies()}, this method will be removed in 9.0
	 */
	public ResourceURI getUpgradeOf() {
		return upgradeOf;
	}
	
	/**
	 * @return a list of {@link ResourceURI}s pointing to resource versions that have been created after the current {{@link #getExtensionOf()} version
	 *         on the parent resource (can be {@code null} if not requested as part of an expand() option)
	 * @deprecated - moved this information to each separate dependency instead of allowing expanding it here, see {@link Dependency#getUpgrades()}
	 */
	public List<ResourceURI> getAvailableUpgrades() {
		return availableUpgrades;
	}

	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	/**
	 * @param extensionOf
	 * @deprecated - moved this information to {@link #getDependencies()}, this method will be removed in 9.0
	 */
	public void setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
	}
	
	/**
	 * @param extensionOfBranchInfo
	 * @deprecated - moved this information to the {@link Dependency#getResource()} expansion, this method will be removed in 9.0 
	 */
	public void setExtensionOfBranchInfo(BranchInfo extensionOfBranchInfo) {
		this.extensionOfBranchInfo = extensionOfBranchInfo;
	}
	
	/**
	 * @param availableUpgrades
	 * @deprecated - moved this information to each separate dependency instead of allowing expanding it here, see {@link Dependency#setUpgrades(List)}
	 */
	public void setAvailableUpgrades(List<ResourceURI> availableUpgrades) {
		this.availableUpgrades = availableUpgrades;
	}
	
	/**
	 * @param upgradeOf
	 * @deprecated - moved this information to {@link #getDependencies()}, this method will be removed in 9.0
	 */
	public void setUpgradeOf(ResourceURI upgradeOf) {
		this.upgradeOf = upgradeOf;
	}
	
	/**
	 * @return an {@link UpgradeInfo} object representing the state of a currently ongoing upgrade. Can only be expanded on an upgrade version of a {@link TerminologyResource}.
	 */
	public UpgradeInfo getUpgradeInfo() {
		return upgradeInfo;
	}
	
	public void setUpgradeInfo(UpgradeInfo upgradeInfo) {
		this.upgradeInfo = upgradeInfo;
	}
	
	public Versions getVersions() {
		return versions;
	}
	
	public void setVersions(Versions versions) {
		this.versions = versions;
	}
	
	public CommitInfos getCommits() {
		return commits;
	}
	
	public void setCommits(CommitInfos commits) {
		this.commits = commits;
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
			
			// if the given branch argument is an absolute branch, then it should start with this resource's current branch
			if (branch.startsWith(Branch.MAIN_PATH)) {
				Preconditions.checkArgument(branch.startsWith(branchPath), "Branch argument '%s' should start with Code System working branch '%s'.", branch, branchPath);
			}
			
			// strip the current working branch
			String relativePath = branch;
			if (branch.startsWith(branchPath)) {
				relativePath = branch.replaceFirst(branchPath, "").replaceFirst("/", "");
			}
			
			if (relativePath.isEmpty()) {
				return getResourceURI();
			}
			
			// support for timestamp based branch paths
			final int idx = relativePath.indexOf(RevisionIndex.AT_CHAR);
			if (idx < 0) {
				return getResourceURI()
					.withPath(relativePath);
			} else {
				return getResourceURI()
					.withPath(relativePath.substring(0, idx))
					.withTimestampPart(relativePath.substring(idx, relativePath.length()));
			}

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
				.bundleAncestorIds(getBundleAncestorIds())
				.bundleId(getBundleId())
				.branchPath(getBranchPath())
				.oid(getOid())
				.settings(getSettings())
				.toolingId(getToolingId())
				// since 8.12
				.dependencies(getDependencies() != null ? getDependencies().stream().map(Dependency::toDocument).collect(Collectors.toCollection(TreeSet::new)) : null)
				// XXX maintain old model fields until 9.0
				.upgradeOf(getUpgradeOf())
				.extensionOf(getExtensionOf())
				;
	}
	
}
