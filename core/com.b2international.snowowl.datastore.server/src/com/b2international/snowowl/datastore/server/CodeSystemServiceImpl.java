/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.ICodeSystemVersion.PATCHED_PREDICATE;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.synchronizedCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.LatestCodeSystemVersionUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
			final CDOBranch cdoBranch = getConnection(repositoryUuid).getBranch(branchPath);
			final CDOBranch branch = checkNotNull(cdoBranch, "Branch " + branchPath + " does not exist in '" + repositoryUuid + "'");
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
		return getAllTags(repositoryUuid, true);
	}
	
	@Override
	public Collection<ICodeSystemVersion> getAllTags(final String repositoryUuid) {
		return getAllTags(repositoryUuid, false);
	}
	
	@Override
	public List<ICodeSystemVersion> getAllTagsWithHeadDecorateWithPatched(final String repositoryUuid) {
		return getAllTagsWithHead(repositoryUuid, true);
	}
	
	@Override
	public List<ICodeSystemVersion> getAllTagsWithHead(final String repositoryUuid) {
		return getAllTagsWithHead(repositoryUuid, false);
	}
	
	@Override
	public List<ICodeSystemVersion> getAllPatchedTags(String repositoryUuid) {
		return newArrayList(filter(getAllTagsDecorateWithPatched(repositoryUuid), PATCHED_PREDICATE));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ICodeSystemVersion> Collection<T> decorateWithPatchedFlag(final String repositoryUuid, final Collection<? extends T> versions) {
		for (T version : versions) {
			if (version instanceof CodeSystemVersionEntry) {
				if (isPatched(repositoryUuid, BranchPathUtils.createPath(version.getPath()))) {
					((CodeSystemVersionEntry) version).setPatched(true);
				}
			}
		}
		return (Collection<T>) versions;
	}

	private Collection<ICodeSystemVersion> getAllTags(final String repositoryId, final boolean decorateWithPatched) {
		final CodeSystemVersions versions = CodeSystemRequests.prepareSearchCodeSystemVersion()
			.all()
			.build(repositoryId)
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync();
		
		return Collections.<ICodeSystemVersion>unmodifiableCollection(newHashSet(decorateWithPatched ? decorateWithPatchedFlag(repositoryId, versions.getItems()) : versions));
	}

	private List<ICodeSystemVersion> getAllTagsWithHead(final String repositoryUuid, final boolean decorateWithPatched) {
		final List<ICodeSystemVersion> $ = Lists.<ICodeSystemVersion>newArrayList(LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(repositoryUuid));
		final Collection<ICodeSystemVersion> allTags = getAllTags(repositoryUuid);
		final List<ICodeSystemVersion> versions = Lists.newArrayList(decorateWithPatched ? decorateWithPatchedFlag(repositoryUuid, allTags) : allTags);
	
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