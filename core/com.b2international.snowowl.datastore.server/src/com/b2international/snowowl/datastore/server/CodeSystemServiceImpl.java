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
package com.b2international.snowowl.datastore.server;

import static com.b2international.commons.collections.Collections3.forEach;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.datastore.ICodeSystemVersion.PATCHED_PREDICATE;
import static com.b2international.snowowl.datastore.ICodeSystemVersion.TO_PARENT_BRANCH_PATH_FUNC;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.synchronizedCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.LatestCodeSystemVersionUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.server.index.InternalTerminologyRegistryServiceRegistry;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * Code system service implementation.
 * <p>This service is registered to the application context as {@link CodeSystemService}.
 *
 */
public class CodeSystemServiceImpl implements CodeSystemService {

	private final Collection<RepositoryUuidAndBranchPair> patchedBranchesCache = synchronizedCollection(Sets.<RepositoryUuidAndBranchPair>newHashSet());
	private final Collection<RepositoryUuidAndBranchPair> taggedBranchesCache = synchronizedCollection(Sets.<RepositoryUuidAndBranchPair>newHashSet());
	
	@Override
	public boolean isTagged(final CDOBranch branch) {
		Preconditions.checkNotNull(branch, "Branch argument cannot be null.");
		return isTagged(
				getConnectionManager().get(branch).getUuid(), 
				BranchPathUtils.createPath(branch));
	}
	
	@Override
	public boolean isTagged(final String repositoryUuid, final IBranchPath branchPath) {
		
		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		//shortcut since MAIN branch cannot be maintenance branch as well.
		if (BranchPathUtils.isMain(branchPath)) {
			return false;
		}
		
		final RepositoryUuidAndBranchPair repositoryUuidAndBranchPair = new RepositoryUuidAndBranchPair(repositoryUuid, branchPath);
		if (taggedBranchesCache.contains(repositoryUuidAndBranchPair)) {
			return true;
		}
		
		for (final ICodeSystemVersion version : getAllTags(repositoryUuid)) {
			
			if (Strings.nullToEmpty(version.getVersionId()).equals(branchPath.lastSegment())) {
				taggedBranchesCache.add(repositoryUuidAndBranchPair);
				return true;
			}
			
		}
		
		return false;
	}

	@Override
	public boolean isPatched(final String repositoryUuid, final IBranchPath branchPath) {
		
		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final RepositoryUuidAndBranchPair repositoryUuidAndBranchPair = new RepositoryUuidAndBranchPair(repositoryUuid, branchPath);
		if (patchedBranchesCache.contains(repositoryUuidAndBranchPair)) {
			return true;
		}
		
		if (isTagged(repositoryUuid, branchPath)) {
			final CDOBranch branch = checkNotNull(getConnection(repositoryUuid).getBranch(branchPath), "Branch " + branchPath + "does not exist in '" + repositoryUuid + "'");
			final boolean patched = Long.MIN_VALUE != CDOServerUtils.getLastCommitTime(branch);
			if (patched) {
				patchedBranchesCache.add(repositoryUuidAndBranchPair);
			}
			return patched;
		}
		
		return false;
	}
	
	@Override
	public Collection<ICodeSystemVersion> getAllTagsDecorateWithPatched(final EPackage ePackage) {
		return getAllTagsDecorateWithPatched(getRepositoryUuidForEPackage(checkNotNull(ePackage, "ePackage")));
	}
	
	@Override
	public Collection<ICodeSystemVersion> getAllTags(final EPackage ePackage) {
		return getAllTags(getRepositoryUuidForEPackage(checkNotNull(ePackage, "ePackage")));
	}

	@Override
	public Collection<ICodeSystemVersion> getAllTagsDecorateWithPatched(final String repositoryUuid) {
		return getAllTags(checkNotNull(repositoryUuid, "repositoryUuid"), true);
	}
	
	@Override
	public Collection<ICodeSystemVersion> getAllTags(final String repositoryUuid) {
		return getAllTags(checkNotNull(repositoryUuid, "repositoryUuid"), false);
	}
	
	@Override
	public List<ICodeSystemVersion> getAllTagsWithHeadDecorateWithPatched(final String repositoryUuid) {
		return getAllTagsWithHead(checkNotNull(repositoryUuid, "repositoryUuid"), true);
	}
	
	@Override
	public List<ICodeSystemVersion> getAllTagsWithHead(final String repositoryUuid) {
		return getAllTagsWithHead(checkNotNull(repositoryUuid, "repositoryUuid"), false);
	}
	
	@Override
	public List<ICodeSystemVersion> getAllPatchedTags(String repositoryUuid) {
		return newArrayList(filter(getAllTagsDecorateWithPatched(checkNotNull(repositoryUuid, "repositoryUuid")), PATCHED_PREDICATE));
	}
	
	@Override
	public ICodeSystemVersion getCurrentVersionForRepository(final String userId, final EPackage ePackage) {
		
		Preconditions.checkNotNull(userId, "User ID argument cannot be null.");
		Preconditions.checkNotNull(ePackage, "Package argument cannot be null.");
		
		final String repositoryUuid = getRepositoryUuidForEPackage(ePackage);
		return getCurrentVersionForRepository(userId, repositoryUuid);
	}
	
	@Override
	public ICodeSystemVersion getCurrentVersionForRepository(final String userId, final String repositoryUuid) {
		Preconditions.checkNotNull(userId, "User ID argument cannot be null.");
		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
		
		final List<ICodeSystemVersion> versions = getAllTagsWithHead(repositoryUuid);
		Preconditions.checkState(!CompareUtils.isEmpty(versions), "No versions are available for " + repositoryUuid);
		
		//no versions (yet)
		if (1 == versions.size()) {
			return LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(repositoryUuid, IBranchPath.MAIN_BRANCH);
		}
		
		IBranchPath branchPath = getUserBranchPathForRepository(userId, repositoryUuid);
		
		if (BranchPathUtils.isMain(branchPath)) {
			return versions.get(1); //first not MAIN (most recently created version)
		}
		
		
		

		final boolean hasActiveTask = null != getTaskStateManager().getActiveTaskId(userId);
		if (hasActiveTask) {
			branchPath = branchPath.getParent();
		}

		final String versionId = branchPath.lastSegment();
		for (final ICodeSystemVersion version : versions) {
			if (versionId.equals(version.getVersionId())) {
				return version;
			}
		}

		final String path = branchPath.getPath();
		boolean userBranchPathIsCodeSystemBranchPath = Iterables.any(versions, new Predicate<ICodeSystemVersion>() {
			@Override
			public boolean apply(ICodeSystemVersion input) {
				return input.getParentBranchPath().equals(path);
			}
		});
		
		if (userBranchPathIsCodeSystemBranchPath) {
			return LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(repositoryUuid, branchPath.getPath());
		}
		
		
		return LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(repositoryUuid, IBranchPath.MAIN_BRANCH);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ICodeSystemVersion> Collection<T> decorateWithPatchedFlag(final String repositoryUuid, final Collection<? extends T> versions) {
		forEach(versions, new Procedure<ICodeSystemVersion>() {
			@Override protected void doApply(final ICodeSystemVersion version) {
				if (version instanceof CodeSystemVersionEntry) {
					if (isPatched(repositoryUuid, BranchPathUtils.createPath(version.getPath()))) {
						((CodeSystemVersionEntry) version).setPatched();
					}
				}
			}
		});
		return (Collection<T>) versions;
	}

	private Collection<ICodeSystemVersion> getAllTags(final String repositoryUuid, final boolean decorateWithPatched) {
		
		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
		final Collection<ICodeSystemVersion> versions = InternalTerminologyRegistryServiceRegistry.INSTANCE. //
				getService(repositoryUuid).getCodeSystemVersionsFromRepository(createMainPath(), repositoryUuid);
		
		return Collections.unmodifiableCollection(newHashSet(decorateWithPatched ? decorateWithPatchedFlag(repositoryUuid, versions) : versions));
	}

	private List<ICodeSystemVersion> getAllTagsWithHead(final String repositoryUuid, final boolean decorateWithPatched) {
		final List<ICodeSystemVersion> $ = new ArrayList<>(); 
		
		final Collection<ICodeSystemVersion> allTags = getAllTags(repositoryUuid);
		final List<ICodeSystemVersion> versions = Lists.newArrayList(decorateWithPatched ? decorateWithPatchedFlag(repositoryUuid, allTags) : allTags);
		
		
		for (IBranchPath branchPath : Multimaps.index(versions, TO_PARENT_BRANCH_PATH_FUNC).keySet()) {
			$.add(LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(repositoryUuid, branchPath.getPath()));
		}
		
		Collections.sort(versions, reverseOrder(ICodeSystemVersion.VERSION_IMPORT_DATE_COMPARATOR));
		$.addAll(versions);
		
		return Collections.unmodifiableList($);
	}

	private String getRepositoryUuidForEPackage(final EPackage ePackage) {
		return getConnection(ePackage).getUuid();
	}

	private ICDOConnection getConnection(final String repositoryUuid) {
		return getConnectionManager().getByUuid(repositoryUuid);
	}

	
	private ICDOConnection getConnection(final EPackage ePackage) {
		return getConnectionManager().get(ePackage);
	}

	private ICDOConnectionManager getConnectionManager() {
		return getServiceForClass(ICDOConnectionManager.class);
	}

	private IBranchPath getUserBranchPathForRepository(final String userId, final String repositoryUuid) {
		return getBranchPathMapConfiguration(userId).getBranchPath(repositoryUuid);
	}

	private IBranchPathMap getBranchPathMapConfiguration(final String userId) {
		return getTaskStateManager().getBranchPathMapConfiguration(userId, true);
	}

	private ITaskStateManager getTaskStateManager() {
		return ApplicationContext.getInstance().getService(ITaskStateManager.class);
	}

	/**
	 * Class for wrapping a repository UUID and a version branch in the repository.
	 * Used for uniquely identifying an existing version.
	 *
	 */
	private static final class RepositoryUuidAndBranchPair {
		private final String repositoryUuid;
		private final IBranchPath versionPath;
		
		private RepositoryUuidAndBranchPair(final String repositoryUuid, final IBranchPath versionPath) {
			this.repositoryUuid = repositoryUuid;
			this.versionPath = versionPath;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((repositoryUuid == null) ? 0 : repositoryUuid.hashCode());
			result = prime * result + ((versionPath == null) ? 0 : versionPath.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final RepositoryUuidAndBranchPair other = (RepositoryUuidAndBranchPair) obj;
			if (repositoryUuid == null) {
				if (other.repositoryUuid != null)
					return false;
			} else if (!repositoryUuid.equals(other.repositoryUuid))
				return false;
			if (versionPath == null) {
				if (other.versionPath != null)
					return false;
			} else if (!versionPath.equals(other.versionPath))
				return false;
			return true;
		}
		
	}
	
}