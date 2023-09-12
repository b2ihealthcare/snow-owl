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
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.branch.Branch;
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
		 * Constant denoting a dependency as the base resource of this resource. Points to the resource (or versioned resource) that this resource is
		 * the extension of.
		 */
		public static final String EXTENSION_OF = "extensionOf";

		/**
		 * Constant denoting a dependency as the development version of this upgrade resource. Points to the resource that this resource is the
		 * upgrade of.
		 */
		public static final String UPGRADE_OF = "upgradeOf";
	}

	/**
	 * @since 8.0
	 */
	public static abstract class Expand extends Resource.Expand {

		public static final String VERSIONS = "versions";
		
		public static final String COMMITS = "commits";
		// Expand parameters for expand option "commits" (both inclusive) 
		public static final String TIMESTAMP_FROM_OPTION_KEY = "timestampFrom";
		public static final String TIMESTAMP_TO_OPTION_KEY = "timestampTo";

		/**
		 * Expand option to expand dependencies of a resource.
		 */
		public static final String DEPENDENCIES = "dependencies";

		/**
		 * Expand option to expand the current latest branch information based on the current {@link TerminologyResource#getBranchPath()} value.
		 */
		public static final String BRANCH = "branch";

	}

	// standard oid
	private String oid;

	// the current working branch
	private String branchPath;

	// identifies the tooling behind this resource, never null, should be picked from the available toolings/schemas
	private String toolingId;

	// identifies a set of resource dependencies this resource depends on
	private List<Dependency> dependencies;

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
	 * @return the dependencies of this terminology resource, if <code>null</code> or empty there are no dependencies from this resource to other
	 *         resources
	 */
	public List<Dependency> getDependencies() {
		return dependencies;
	}

	/**
	 * Searches the dependency array for the first dependency that has the matching scope.
	 * 
	 * @param scope
	 * @return an {@link Optional} value of a {@link Dependency} entry.
	 * @see Dependency#find(List, String)
	 */
	public Optional<Dependency> getDependency(String scope) {
		return Dependency.find(getDependencies(), scope);
	}
	
	/**
	 * Searches the dependency array for the dependency URI that has the matching scope. If there is no such dependency in the dependency
	 * array then the system takes the current settings object into account and tries to find the appropriate settingsKeyed value as fallback
	 * dependency URI. If none of them can be found this method returns <code>null</code>.
	 * 
	 * @param scope
	 * @param settingsKey
	 * @return
	 * @deprecated - replaced by {@link #getDependency(String)} as settings is no longer accepted as source of dependencies  
	 */
	public ResourceURI getDependency(String scope, String settingsKey) {
		return getDependency(scope)
				.map(Dependency::getUri)
				.map(ResourceURIWithQuery::getResourceUri)
				.orElseGet(() -> (getSettings() == null || !getSettings().containsKey(settingsKey)) ? null : new ResourceURIWithQuery((String) getSettings().get(settingsKey)).getResourceUri());
	}

	/**
	 * @param scope
	 * @param settingsKey
	 * @return <code>true</code> if this resource has a dependency entry with the given scope or a settings entry with the given settings key,
	 *         otherwise <code>false</code>.
	 */
	public boolean hasDependency(String scope, String settingsKey) {
		return getDependency(scope, settingsKey) != null;
	}

	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
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
	 * @return the {@link ResourceURI} of this resource uri at the given branch, if the branch is empty it returns the HEAD of this
	 *         {@link TerminologyResource}.
	 */
	@JsonIgnore
	public ResourceURI getResourceURI(String branch) {
		Preconditions.checkNotNull(branch, "Branch argument should not be null");
		if (!Strings.isNullOrEmpty(branch)) {

			// if the given branch argument is an absolute branch, then it should start with this resource's current branch
			if (branch.startsWith(Branch.MAIN_PATH)) {
				Preconditions.checkArgument(branch.startsWith(branchPath), "Branch argument '%s' should start with Code System working branch '%s'.",
						branch, branchPath);
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
				return getResourceURI().withPath(relativePath);
			} else {
				return getResourceURI().withPath(relativePath.substring(0, idx))
						.withTimestampPart(relativePath.substring(idx, relativePath.length()));
			}

		} else {
			return getResourceURI();
		}
	}
	
	/**
	 * @return the {@link ResourceURI} pointing to a resource this resource is an extension of
	 */
	@JsonIgnore
	public ResourceURI getExtensionOf() {
		return getDependency(TerminologyResource.DependencyScope.EXTENSION_OF).map(Dependency::getUri).map(ResourceURIWithQuery::getResourceUri).orElse(null);
	}

	/**
	 * @return the {@link ResourceURIWithQuery} pointing to a resource this resource is an upgrade of, this usually references the current non-upgrade point in
	 *         time of the same resource
	 */
	@JsonIgnore
	public ResourceURI getUpgradeOf() {
		return getDependency(TerminologyResource.DependencyScope.UPGRADE_OF).map(Dependency::getUri).map(ResourceURIWithQuery::getResourceUri).orElse(null);
	}

	@Override
	public Builder toDocumentBuilder() {
		return ResourceDocument.builder()
				// generic resource fields
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
				// by default resources converted from domain representations are not hidden
				.hidden(false)
				.bundleAncestorIds(getBundleAncestorIds())
				.bundleId(getBundleId())
				.settings(getSettings())
				// terminology resource fields
				.branchPath(getBranchPath())
				.oid(getOid())
				.toolingId(getToolingId())
				// since 8.12
				.dependencies(getDependencies() != null ? getDependencies().stream().map(Dependency::toDocument).collect(Collectors.toCollection(TreeSet::new)) : null);
	}

}
