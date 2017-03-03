/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.server;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.b2international.commons.AlphaNumericComparator;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.LatestCodeSystemVersionUtils;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * Server side service singleton for terminology metadata.
 *
 */
public enum TerminologyRegistryServiceImpl implements TerminologyRegistryService {

	INSTANCE;
	
	private IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
	
	@Override
	public Collection<ICodeSystem> getCodeSystems(final IBranchPathMap branchPathMap) {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem().all().build(repositoryId).execute(getBus()));
		}
		return Promise.all(getAllCodeSystems)
				.then(new Function<List<Object>, Collection<ICodeSystem>>() {
					@Override
					public Collection<ICodeSystem> apply(List<Object> results) {
						final List<ICodeSystem> codeSystems = newArrayList();
						for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
							codeSystems.addAll(result.getItems());
						}
						return codeSystems;
					}
				})
				.getSync();
	}

	@Override
	public Collection<ICodeSystemVersion> getCodeSystemVersions(final IBranchPathMap branchPathMap, final String codeSystemShortName) {
		final List<Promise<CodeSystemVersions>> getAllCodeSystemVersions = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystemVersions.add(CodeSystemRequests.prepareSearchCodeSystemVersion().all()
					.build(repositoryId).execute(getBus()));
		}
		return Promise.all(getAllCodeSystemVersions)
				.then(new Function<List<Object>, Collection<ICodeSystemVersion>>() {
					@Override
					public Collection<ICodeSystemVersion> apply(List<Object> results) {
						final List<ICodeSystemVersion> codeSystems = newArrayList();
						for (CodeSystemVersions result : Iterables.filter(results, CodeSystemVersions.class)) {
							codeSystems.addAll(result.getItems());
						}
						return codeSystems;
					}
				})
				.getSync();
	}

	@Override
	public ICodeSystem getCodeSystemByShortName(final IBranchPathMap branchPathMap, final String codeSystemShortName) {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem().all().filterById(codeSystemShortName)
					.build(repositoryId).execute(getBus()));
		}
		return Promise.all(getAllCodeSystems)
				.then(new Function<List<Object>, ICodeSystem>() {
					@Override
					public ICodeSystem apply(List<Object> results) {
						for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
							if (!result.getItems().isEmpty()) {
								return Iterables.getOnlyElement(result.getItems());
							}
						}
						return null;
					}
				})
				.getSync();
	}

	@Override
	public ICodeSystem getCodeSystemByOid(final IBranchPathMap branchPathMap, final String codeSystemOid) {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem().all().filterById(codeSystemOid)
					.build(repositoryId).execute(getBus()));
		}
		return Promise.all(getAllCodeSystems)
				.then(new Function<List<Object>, ICodeSystem>() {
					@Override
					public ICodeSystem apply(List<Object> results) {
						for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
							if (!result.getItems().isEmpty()) {
								return Iterables.getOnlyElement(result.getItems());
							}
						}
						return null;
					}
				})
				.getSync();
	}

	@Override
	public String getTerminologyComponentIdByShortName(final IBranchPathMap branchPathMap, final String codeSystemShortName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getVersionId(final IBranchPathMap branchPathMap, final ICodeSystem codeSystem) {
		if (null == codeSystem) {
			return null;
		}
		
		String version = "";
		final AlphaNumericComparator comparator = new AlphaNumericComparator();
		
		for (final ICodeSystemVersion codeSystemVersion : getCodeSystemVersions(branchPathMap, codeSystem.getShortName())) {
			final String versionId = codeSystemVersion.getVersionId();
			if (comparator.compare(versionId, version) > 0) {
				version = versionId;
			}
		}
		
		
		return Strings.isNullOrEmpty(version) ? String.valueOf(0) : version;
	}
	
	@Override
	public Map<String, List<ICodeSystemVersion>> getAllVersion() {
		final List<Promise<CodeSystemVersions>> getAllVersions = newArrayList();
		final List<String> repositoryIds = getRepositoryIds();
		for (String repositoryId : repositoryIds) {
			getAllVersions.add(CodeSystemRequests.prepareSearchCodeSystemVersion().all().build(repositoryId).execute(getBus()));
		}
		
		return Promise.all(getAllVersions)
				.then(new Function<List<Object>, Map<String, List<ICodeSystemVersion>>>() {
					@Override
					public Map<String, List<ICodeSystemVersion>> apply(List<Object> input) {
						final Map<String, List<ICodeSystemVersion>> versionMap = newHashMap();
						for (int i = 0; i < repositoryIds.size(); i++) {
							final String repositoryId = repositoryIds.get(i);
							final List<CodeSystemVersionEntry> versions = ((CodeSystemVersions) input.get(i)).getItems();
							final List<ICodeSystemVersion> existingVersions = Lists.<ICodeSystemVersion>newArrayList(getServiceForClass(CodeSystemService.class).decorateWithPatchedFlag(repositoryId, versions));
							sort(existingVersions, reverseOrder(ICodeSystemVersion.VERSION_IMPORT_DATE_COMPARATOR));
							existingVersions.add(0, LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(repositoryId));
							versionMap.put(repositoryId, existingVersions);
						}
						return versionMap;
					}
				})
				.getSync();
	}
	
	private List<String> getRepositoryIds() {
		final List<String> repositories = newArrayList();
		for (Repository repository : ApplicationContext.getServiceForClass(RepositoryManager.class).repositories()) {
			repositories.add(repository.id());
		}
		return repositories;
	}
	
}