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
import static com.google.common.collect.Iterables.concat;
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
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Version configuration implementation that gets its initial state from the {@link TaskManager}.
 *
 */
public class VersionConfigurationImpl implements VersionConfiguration {

	private final IBranchPathMap taskBranchPathMap;
	private final Map<ICodeSystem, List<ICodeSystemVersion>> allVersions;
	private final Map<ICodeSystem, ICodeSystemVersion> currentVersions;

	private boolean dirty = false;
	
	public VersionConfigurationImpl(List<ICodeSystem> codeSystems, List<ICodeSystemVersion> codeSystemVersions) {
		taskBranchPathMap = getTaskBranchPathMap();
		allVersions = getAllVersions(codeSystems, codeSystemVersions);
		currentVersions = initCurrentVersions(codeSystems, codeSystemVersions);
	}

	@Override
	public IStatus update(final ICodeSystemVersion versionToSet) {
		final String repositoryUuid = checkNotNull(versionToSet, "versionToSet").getRepositoryUuid();
		final String versionId = versionToSet.getVersionId();
		
		final Map<ICodeSystem, ICodeSystemVersion> copyCurrentVersions = newHashMap(currentVersions);
		
		if (isLocked(versionToSet)) {
			return error("Version " + toVersionString(versionId) + " for " + getToolingFeatureNameForRepository(repositoryUuid) + " cannot be modified.");
		}

		final Optional<ICodeSystemVersion>  existingVersionOptional = tryFindVersion(versionToSet);

		if (!existingVersionOptional.isPresent()) {
			return error("Cannot find version " + toVersionString(versionId) + " for " + getToolingFeatureNameForRepository(repositoryUuid) + ".");
		}
		
		Map<ICodeSystem, List<ICodeSystemVersion>> filteredEntries = filterVersionsMapForMatchingCodeSystems(repositoryUuid, existingVersionOptional.get());
		
		ICodeSystem matchingCodeSystem = CodeSystemUtils.findMatchingCodeSystem(versionToSet.getParentBranchPath(), repositoryUuid, filteredEntries.keySet());
		
		
		copyCurrentVersions.put(matchingCodeSystem, existingVersionOptional.get());
		
		String slaveRepositoryUuid = getSlaveRepositoryUuid(repositoryUuid);
		while (null != slaveRepositoryUuid) {
			
			//same version is mandatory in slave repository.
			Optional<ICodeSystemVersion> newSlaveVersionOptional = tryFindVersion(slaveRepositoryUuid, versionToSet.getCodeSystemShortName(), versionId);
			if (!newSlaveVersionOptional.isPresent()) {
				return error("Cannot find dependent version " + toVersionString(versionId) + " for " + getToolingFeatureNameForRepository(slaveRepositoryUuid) + ".");
			}
			
				
			ICodeSystemVersion newSlaveVersion = newSlaveVersionOptional.get();
			if (isLocked(newSlaveVersion)) {
				return error("Dependent version " + toVersionString(versionId) + " for " + getToolingFeatureNameForRepository(repositoryUuid) + " cannot be modified.");
			}
			
			filteredEntries = filterVersionsMapForMatchingCodeSystems(slaveRepositoryUuid, newSlaveVersion);
			copyCurrentVersions.put(CodeSystemUtils.findMatchingCodeSystem(newSlaveVersion.getParentBranchPath(), slaveRepositoryUuid, filteredEntries.keySet()), newSlaveVersion);
			
			slaveRepositoryUuid = getSlaveRepositoryUuid(slaveRepositoryUuid);
			
		}
		dirty = !currentVersions.equals(copyCurrentVersions);
		currentVersions.putAll(copyCurrentVersions);
		
		return ok();
	}

	protected Map<ICodeSystem, List<ICodeSystemVersion>> filterVersionsMapForMatchingCodeSystems(final String repositoryUuid, 
			final ICodeSystemVersion existingVersion) {
		
		return Maps.filterEntries(allVersions, new Predicate<Entry<ICodeSystem, List<ICodeSystemVersion>>>() {
			@Override
			
			public boolean apply(Entry<ICodeSystem, List<ICodeSystemVersion>> input) {
				return sameRepositoryUuid(input, repositoryUuid) && shortNameEquals(existingVersion, input);
			}
			
			private boolean sameRepositoryUuid(Entry<ICodeSystem, List<ICodeSystemVersion>> input, String repositoryUuid) {
				return Objects.equal(input.getKey().getRepositoryUuid(), repositoryUuid);
			}

			private boolean shortNameEquals(final ICodeSystemVersion existingVersion,
					Entry<ICodeSystem, List<ICodeSystemVersion>> input) {
				return Objects.equal(input.getKey().getShortName(), existingVersion.getCodeSystemShortName());
			}
		});
	}

	protected Optional<ICodeSystemVersion> tryFindVersion(final String repositoryUuid, final String codeSystemShortName, final String versionId) {
		return FluentIterable.<ICodeSystemVersion> from(concat(allVersions.values()))
				.filter(new CSVRepositoryUuidPredicate(repositoryUuid))
				.filter(new CSVShortNamePredicate(codeSystemShortName))
				.filter(new CSVVersionIdPredicate(versionId)).first();
	}
	protected Optional<ICodeSystemVersion> tryFindVersion(final ICodeSystemVersion versionToSet) {
		final String repositoryUuid = versionToSet.getRepositoryUuid();
		final String codeSystemShortName = versionToSet.getCodeSystemShortName();
		final String versionId = versionToSet.getVersionId();
		return tryFindVersion(repositoryUuid, codeSystemShortName, versionId);
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
		Map<ICodeSystem, List<ICodeSystemVersion>> singleCodeSystemToVersionsMap = filterVersionsMapForMatchingCodeSystems(version.getRepositoryUuid(), version);
		
		if (singleCodeSystemToVersionsMap.isEmpty())
			return true;
		
		List<ICodeSystemVersion> codeSystemVersions = Iterables.getOnlyElement(singleCodeSystemToVersionsMap.values());
		return codeSystemVersions.size() <= 1;
	}

	@Override
	public boolean isDirty() {
		return dirty;
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
	public List<ICodeSystemVersion> getAllVersionsForCodeSystem(final ICodeSystem codeSystem) {
		final List<ICodeSystemVersion> allVersionForRepository = newArrayList(allVersions.get(codeSystem));
		Iterables.removeIf(allVersionForRepository, new CSVVersionIdPredicate(INITIAL_STATE));
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
	 * @param codeSystemVersions 
	 * @param codeSystems 
	 * @return a mapping between repository UUIDs and all versions.
	 */
	protected Map<ICodeSystem, List<ICodeSystemVersion>> getAllVersions(List<ICodeSystem> codeSystems, List<ICodeSystemVersion> codeSystemVersions) {
		final Map<ICodeSystem, List<ICodeSystemVersion>> allVersionsFromServer = Maps.newHashMap();
		for (ICodeSystem codeSystem : codeSystems) {
			ImmutableList<ICodeSystemVersion> list = FluentIterable.<ICodeSystemVersion> from(codeSystemVersions)
															.filter(new CSVRepositoryUuidPredicate(codeSystem.getRepositoryUuid()))
															.filter(new CSVShortNamePredicate(codeSystem.getShortName()))
														.toList();
			allVersionsFromServer.put(codeSystem, list);
		}
		
		//it might happen that server has for example UMLS store but client does not have dependency
		//hence connection for UMLS.
		final Collection<String> availableTerminologyRepositoriesOnClient =  getServiceForClass(ICDOConnectionManager.class).uuidKeySet();
		for (final Iterator<ICodeSystem> itr  = allVersionsFromServer.keySet().iterator(); itr.hasNext(); /**/) {
			final ICodeSystem codeSystem = itr.next();
			if (!availableTerminologyRepositoriesOnClient.contains(codeSystem.getRepositoryUuid())) {
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

	private Map<ICodeSystem, ICodeSystemVersion> initCurrentVersions(List<ICodeSystem> codeSystems, List<ICodeSystemVersion> codeSystemVersions) {
		final Map<ICodeSystem, ICodeSystemVersion> currentVersion = newHashMap();
		
		Iterable<ICodeSystemVersion> versions = Iterables.concat(allVersions.values());
		Iterable<ICodeSystemVersion> fakeRefHeadVersions = Iterables.filter(versions, latestCodeSystemVersionPredicate());
		Iterable<ICodeSystemVersion> existingVersions = Iterables.filter(versions, not(in(newArrayList(fakeRefHeadVersions))));
		
		for (final String repositoryUuid : Sets.newHashSet(Iterables.transform(existingVersions, ICodeSystemVersion.TO_REPOSITORY_UUID_FUNC))) {
			
			// this branchPath can be: a task branchPath; version/tag branchPath; codeSystem branchPath 
			final IBranchPath branchPath = taskBranchPathMap.getBranchPath(repositoryUuid);
			
			final ICodeSystem  codeSystem = CodeSystemUtils.findMatchingCodeSystem(branchPath, repositoryUuid, codeSystems);
			final ICodeSystemVersion version = findMatchingCodeSystemVersion(branchPath, codeSystem);
			currentVersion.put(codeSystem, version != null ? version : LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(codeSystem));
		}
		return currentVersion;
	}

	private ICodeSystemVersion findMatchingCodeSystemVersion(final IBranchPath branchPath, final ICodeSystem codeSystem) {
		if (Objects.equal(branchPath.getPath(), codeSystem.getBranchPath()))
			return LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(codeSystem);
		return CodeSystemUtils.findMatchingVersion(branchPath, allVersions.get(codeSystem));
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

	
	private static class CSVRepositoryUuidPredicate implements Predicate<ICodeSystemVersion> {

		private final String respositoryUuid;
		
		public CSVRepositoryUuidPredicate(String repositoryUuid) {
			this.respositoryUuid = repositoryUuid;
		}

		@Override
		public boolean apply(ICodeSystemVersion input) {
			return Objects.equal(input.getRepositoryUuid(), respositoryUuid);
		}
		
	} 
	
	private static class CSVVersionIdPredicate implements Predicate<ICodeSystemVersion> {
		
		private final String versionId;
		
		public CSVVersionIdPredicate(String versionId) {
			this.versionId = versionId;
		}
		
		@Override
		public boolean apply(ICodeSystemVersion input) {
			return Objects.equal(input.getVersionId(), versionId);
		}
		
	}
	
	
	private static class CSVShortNamePredicate implements Predicate<ICodeSystemVersion> {
		
		private final String codeSystemShortName;
		
		public CSVShortNamePredicate(String codeSystemShortName) {
			this.codeSystemShortName = codeSystemShortName;
		}
		
		@Override
		public boolean apply(ICodeSystemVersion input) {
			return Objects.equal(input.getCodeSystemShortName(), codeSystemShortName);
		}
		
	}
}