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
package com.b2international.snowowl.datastore;

import static com.b2international.commons.status.Statuses.error;
import static com.b2international.commons.status.Statuses.ok;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.CodeSystemUtils.TOOLING_FEATURE_NAME_COMPARATOR;
import static com.b2international.snowowl.datastore.ICodeSystemVersion.INITIAL_STATE;
import static com.b2international.snowowl.datastore.LatestCodeSystemVersionUtils.latestCodeSystemVersionPredicate;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newTreeMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.tasks.TaskManager;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Version configuration implementation that gets its initial state from the {@link TaskManager}.
 *
 */
public class VersionConfigurationImpl implements VersionConfiguration {

	private final IBranchPathMap taskBranchPathMap;
	private final Map<String, List<ICodeSystemVersion>> allVersions;
	private final Map<ICodeSystem, ICodeSystemVersion> currentVersions;

	public VersionConfigurationImpl() {
		taskBranchPathMap = getTaskBranchPathMap();
		allVersions = getAllVersions();
		currentVersions = initCurrentVersions();
	}

	@Override
	public IStatus update(final ICodeSystemVersion versionToSet) {
		final String repositoryUuid = checkNotNull(versionToSet, "versionToSet").getRepositoryUuid();
		final String versionId = versionToSet.getVersionId();
		
		final Map<ICodeSystem, ICodeSystemVersion> copyCurrentVersions = newHashMap(currentVersions);
		
		if (isLocked(versionToSet)) {
			return error("Version " + toVersionString(versionId) + " for " + getToolingFeatureNameForRepository(repositoryUuid) + " cannot be modified.");
		}

		final ICodeSystemVersion newVersionToSet = tryFindVersion(repositoryUuid, versionId);
		ICodeSystem matchingCodeSystem = CodeSystemUtils.findMatchingCodeSystem(versionToSet.getParentBranchPath(), repositoryUuid);
		
		if (null == newVersionToSet) {
			return error("Cannot find version " + toVersionString(versionId) + " for " + getToolingFeatureNameForRepository(repositoryUuid) + ".");
		}
		
		copyCurrentVersions.put(matchingCodeSystem, newVersionToSet);
		
		String slaveRepositoryUuid = getSlaveRepositoryUuid(repositoryUuid);
		while (null != slaveRepositoryUuid) {
			
			//same version is mandatory in slave repository.
			ICodeSystemVersion newSlaveVersion = tryFindVersion(slaveRepositoryUuid, versionId);
			if (null == newSlaveVersion) {
				return error("Cannot find dependent version " + toVersionString(versionId) + " for " + getToolingFeatureNameForRepository(slaveRepositoryUuid) + ".");
			}
			
				
			if (isLocked(newSlaveVersion)) {
				return error("Dependent version " + toVersionString(versionId) + " for " + getToolingFeatureNameForRepository(repositoryUuid) + " cannot be modified.");
			}
			
			copyCurrentVersions.put(CodeSystemUtils.findMatchingCodeSystem(newSlaveVersion.getParentBranchPath(), slaveRepositoryUuid), newSlaveVersion);
			
			slaveRepositoryUuid = getSlaveRepositoryUuid(slaveRepositoryUuid);
			
		}
		
		currentVersions.putAll(copyCurrentVersions);
		
		return ok();
	}

	@Override
	public Map<ICodeSystem, ICodeSystemVersion> getConfiguration() {
		final Map<ICodeSystem, ICodeSystemVersion> copyCurrentVersions = newTreeMap(TOOLING_FEATURE_NAME_COMPARATOR);
		copyCurrentVersions.putAll(currentVersions);
		for (final Iterator<ICodeSystem> itr = copyCurrentVersions.keySet().iterator(); itr.hasNext(); /**/) {
			if (isMeta(itr.next().getRepositoryUuid())) {
				itr.remove();
			}
		}
		return unmodifiableMap(copyCurrentVersions);
	}

	@Override
	public boolean isLocked(final ICodeSystemVersion version) {
		final String repositoryUuid = checkNotNull(version, "version").getRepositoryUuid();
		return taskBranchPathMap.getLockedEntries().containsKey(repositoryUuid);
	}
	
	@Override
	public boolean isSingleton(final ICodeSystemVersion version) {
		final String repositoryUuid = checkNotNull(version, "version").getRepositoryUuid();
		return allVersions.get(repositoryUuid).size() <= 1;
	}

	@Override
	public boolean isDirty() {
		return !currentVersions.equals(initCurrentVersions());
	}
	
	@Override
	public IBranchPathMap getConfigurationAsBranchPathMap() {
		
		final Map<String, IBranchPath> currentBranchPathMap = getCurrentBranchPathMap();
		
		for (final Entry<ICodeSystem, ICodeSystemVersion> entry : currentVersions.entrySet()) {
			if (!isLocked(entry.getValue())) {
				currentBranchPathMap.put(entry.getKey().getRepositoryUuid(), BranchPathUtils.createPath(entry.getValue().getPath()));
			}
		}
		return new UserBranchPathMap(currentBranchPathMap);
	}

	protected HashMap<String, IBranchPath> getCurrentBranchPathMap() {
		return newHashMap(taskBranchPathMap.asMap(getAllRepositoryUuids()));
	}

	@Override
	public List<ICodeSystemVersion> getAllVersionsForRepository(final ICodeSystemVersion version) {
		final String repositoryUuid = checkNotNull(version, "version").getRepositoryUuid();
		final List<ICodeSystemVersion> allVersionForRepository = newArrayList(allVersions.get(repositoryUuid));
		final ICodeSystemVersion initVersion = tryFindVersion(repositoryUuid, INITIAL_STATE);
		if (null != initVersion) {
			allVersionForRepository.remove(initVersion);
		}
		return unmodifiableList(allVersionForRepository);
	}
	
	/**
	 * Returns with a set of available repository UUIDs.
	 * @return all UUIDs of the available repositories.
	 */
	protected Set<String> getAllRepositoryUuids() {
		return getConnectionManager().uuidKeySet();
	}

	/**
	 * Returns with a mapping of all available version. Keys are the repository UUIDs
	 * and the values are the available code system versions.
	 * @return a mapping between repository UUIDs and all versions.
	 */
	protected Map<String, List<ICodeSystemVersion>> getAllVersions() {
		final Map<String, List<ICodeSystemVersion>> allVersionsFromServer = getTerminologyRegistryService().getAllVersion();
		//it might happen that server has for example UMLS store but client does not have dependency
		//hence connection for UMLS.
		final Collection<String> availableTerminologiesOnClient =  getServiceForClass(ICDOConnectionManager.class).uuidKeySet();
		for (final Iterator<String> itr  = allVersionsFromServer.keySet().iterator(); itr.hasNext(); /**/) {
			final String repositoryUuid = itr.next();
			if (!availableTerminologiesOnClient.contains(repositoryUuid)) {
				itr.remove();
			}
		}
		return allVersionsFromServer;
	}

	/**
	 * Returns with the user's task aware branch path map.
	 * @return a branch path map representing the task aware branch configuration of a use.r
	 */
	protected IBranchPathMap getTaskBranchPathMap() {
		return getServiceForClass(TaskManager.class).getBranchPathMap();
	}
	
	/**
	 * Returns with the repository UUID of the slave repository. May return with 
	 * {@code null} if the repository does not have any slave. 
	 * @param repositoryUuid the master repository UUID.
	 * @return the slave repository UUID or {@code null} if the master repository does not have a slave.
	 */
	protected String getSlaveRepositoryUuid(final String repositoryUuid) {
		return getConnectionManager().getSlaveUuid(repositoryUuid);
	}
	
	/**
	 * Returns with the human readable name of the tooling feature associated with the
	 * repository UUID argument.
	 * @param repositoryUuid the repository UUID.
	 * @return the human readable name of the tooling feature.
	 */
	protected String getToolingFeatureNameForRepository(final String repositoryUuid) {
		return CodeSystemUtils.getSnowOwlToolingName(repositoryUuid);
	}
	
	/**
	 * Returns with {@code true} if a repository given with the repository UUID argument is a meta repository.
	 * Otherwise {@code false}.
	 * @param repositoryUuid the UUID of the repository to check.
	 * @return {@code true} if the repository is meta, otherwise {@code false}.
	 */
	protected boolean isMeta(final String repositoryUuid) {
		return getConnectionManager().isMeta(checkNotNull(repositoryUuid, "repositoryUuid"));
	}

	private ICodeSystemVersion tryFindVersion(final String repositoryUuid, final String versionId) {
		return find(allVersions.get(repositoryUuid), new Predicate<ICodeSystemVersion>() {
			@Override public boolean apply(final ICodeSystemVersion version) {
				return versionId.equals(version.getVersionId());
			}
		}, null);
	}

	private Map<ICodeSystem, ICodeSystemVersion> initCurrentVersions() {
		final Map<ICodeSystem, ICodeSystemVersion> currentVersion = newHashMap();
		
		Iterable<ICodeSystemVersion> versions = Iterables.concat(allVersions.values());
		Iterable<ICodeSystemVersion> fakeRefHeadVersions = Iterables.filter(versions, latestCodeSystemVersionPredicate());
		Iterable<ICodeSystemVersion> existingVersions = Iterables.filter(versions, not(in(newArrayList(fakeRefHeadVersions))));
		
		for (final String repositoryUuid : Sets.newHashSet(Iterables.transform(existingVersions, ICodeSystemVersion.TO_REPOSITORY_UUID_FUNC))) {
			
			// this branchPath can be: a task branch; version/tag branch Path; codeSystem branchPath 
			final IBranchPath branchPath = taskBranchPathMap.getBranchPath(repositoryUuid);
			
			final ICodeSystem  codeSystem = CodeSystemUtils.findMatchingCodeSystem(branchPath, repositoryUuid);
			final ICodeSystemVersion version = CodeSystemUtils.findMatchingVersion(branchPath, Iterables.filter(allVersions.get(repositoryUuid), new Predicate<ICodeSystemVersion>() {
				@Override
				public boolean apply(ICodeSystemVersion input) {
					return Objects.equal(input.getCodeSystemShortName(), codeSystem.getShortName());
				}
			}));
			currentVersion.put(codeSystem, version != null ? version : LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(repositoryUuid, codeSystem.getBranchPath()));
		}
		return currentVersion;
	}
	
	
	protected TerminologyRegistryService getTerminologyRegistryService() {
		return getServiceForClass(TerminologyRegistryService.class);
	}

	private ICDOConnectionManager getConnectionManager() {
		return getServiceForClass(ICDOConnectionManager.class);
	}
	
	private String toVersionString(final String versionId) {
		return ICodeSystemVersion.INITIAL_STATE.equals(versionId) ? "initial state" : "'" + versionId + "'";
	}
	
	/**
	 * @param versionString
	 * @return full branch path
	 */
	protected IBranchPath createVersionPath(String versionString) {
		return BranchPathUtils.createPath(IBranchPath.MAIN_BRANCH + IBranchPath.SEPARATOR_CHAR + versionString);
	}

}