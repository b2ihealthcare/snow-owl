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
package com.b2international.snowowl.terminologyregistry.core.server;

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.commons.concurrent.ConcurrentCollectionUtils.forEach;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newConcurrentMap;
import static com.google.common.collect.Sets.newConcurrentHashSet;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;
import static java.util.Collections.synchronizedMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.b2international.commons.Pair;
import com.b2international.commons.collections.Procedure;
import com.b2international.commons.concurrent.ConcurrentCollectionUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.InternalTerminologyRegistryService;
import com.b2international.snowowl.datastore.LatestCodeSystemVersionUtils;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.server.index.InternalTerminologyRegistryServiceRegistry;
import com.google.common.collect.Maps;


/**
 * Server side service singleton for terminology metadata.
 * @deprecated use CodeSystemRequests instead
 */
@Deprecated
public enum TerminologyRegistryServiceImpl implements TerminologyRegistryService {

	INSTANCE;
	
	@Override
	public Collection<ICodeSystem> getCodeSystems(final IBranchPathMap branchPathMap) {
		final Collection<ICodeSystem> codeSystems = newConcurrentHashSet();
		forEach(getServices(branchPathMap), new Procedure<Pair<InternalTerminologyRegistryService, IBranchPath>>() {
			protected void doApply(final Pair<InternalTerminologyRegistryService, IBranchPath> pair) {
				final Collection<ICodeSystem> systems = pair.getA().getCodeSystems(pair.getB());
				if (!isEmpty(systems)) {
					codeSystems.addAll(systems);
				}
			}
		});
		return codeSystems;
	}

	@Override
	public Collection<ICodeSystemVersion> getCodeSystemVersions(final IBranchPathMap branchPathMap, final String codeSystemShortName) {
		final Collection<ICodeSystemVersion> versions = newConcurrentHashSet();
		forEach(getServices(branchPathMap), new Procedure<Pair<InternalTerminologyRegistryService, IBranchPath>>() {
			protected void doApply(final Pair<InternalTerminologyRegistryService, IBranchPath> pair) {
				final Collection<ICodeSystemVersion> codeSystemVersions = pair.getA().getCodeSystemVersions(pair.getB(), codeSystemShortName);
				if (!isEmpty(codeSystemVersions)) {
					versions.addAll(codeSystemVersions);
				}
			}
		});
		return versions;
	}

	@Override
	public ICodeSystem getCodeSystemByShortName(final IBranchPathMap branchPathMap, final String codeSystemShortName) {
		final Collection<ICodeSystem> codeSystems = newConcurrentHashSet();
		forEach(getServices(branchPathMap), new Procedure<Pair<InternalTerminologyRegistryService, IBranchPath>>() {
			protected void doApply(final Pair<InternalTerminologyRegistryService, IBranchPath> pair) {
				final ICodeSystem codeSystem = pair.getA().getCodeSystemByShortName(pair.getB(), codeSystemShortName);
				if (null != codeSystem) {
					codeSystems.add(codeSystem);
				}
			}
		});
		return getFirst(codeSystems, null); 
	}

	@Override
	public ICodeSystem getCodeSystemByOid(final IBranchPathMap branchPathMap, final String codeSystemOid) {
		final Collection<ICodeSystem> codeSystems = newConcurrentHashSet();
		forEach(getServices(branchPathMap), new Procedure<Pair<InternalTerminologyRegistryService, IBranchPath>>() {
			protected void doApply(final Pair<InternalTerminologyRegistryService, IBranchPath> pair) {
				final ICodeSystem codeSystem = pair.getA().getCodeSystemByOid(pair.getB(), codeSystemOid);
				if (null != codeSystem) {
					codeSystems.add(codeSystem);
				}
			}
		});
		return getFirst(codeSystems, null);
	}

	@Override
	public Map<String, ICodeSystem> getTerminologyComponentIdCodeSystemMap(final IBranchPathMap branchPathMap) {
		final Map<String, ICodeSystem> terminologyComponentIdCodeSystemMap = synchronizedMap(Maps.<String, ICodeSystem>newHashMap());
		forEach(getServices(branchPathMap), new Procedure<Pair<InternalTerminologyRegistryService, IBranchPath>>() {
			protected void doApply(final Pair<InternalTerminologyRegistryService, IBranchPath> pair) {
				final Map<String, ICodeSystem> codeSystemMap = pair.getA().getTerminologyComponentIdCodeSystemMap(pair.getB());
				if (!isEmpty(codeSystemMap)) {
					terminologyComponentIdCodeSystemMap.putAll(codeSystemMap);
				}
			}
		});
		return terminologyComponentIdCodeSystemMap;
	}

	@Override
	public Map<String, Collection<ICodeSystem>> getTerminologyComponentIdWithMultipleCodeSystemsMap(final IBranchPathMap branchPathMap) {
		final Map<String, Collection<ICodeSystem>> terminologyComponentIdCodeSystemMap = synchronizedMap(Maps.<String, Collection<ICodeSystem>>newHashMap());
		ConcurrentCollectionUtils.forEach(getServices(branchPathMap), new Procedure<Pair<InternalTerminologyRegistryService, IBranchPath>>() {
			protected void doApply(final Pair<InternalTerminologyRegistryService, IBranchPath> pair) {
				final Map<String, Collection<ICodeSystem>> codeSystemsMap = pair.getA().getTerminologyComponentIdWithMultipleCodeSystemsMap(pair.getB());
				if (!isEmpty(codeSystemsMap)) {
					terminologyComponentIdCodeSystemMap.putAll(codeSystemsMap);
				}
			}
		});
		return terminologyComponentIdCodeSystemMap;
	}

	@Override
	public String getTerminologyComponentIdByShortName(final IBranchPathMap branchPathMap, final String codeSystemShortName) {
		final Collection<String> terminologyComponentIds = newConcurrentHashSet();
		forEach(getServices(branchPathMap), new Procedure<Pair<InternalTerminologyRegistryService, IBranchPath>>() {
			protected void doApply(final Pair<InternalTerminologyRegistryService, IBranchPath> pair) {
				final String terminologyComponent = pair.getA().getTerminologyComponentIdByShortName(pair.getB(), codeSystemShortName);
				if (!isEmpty(terminologyComponent)) {
					terminologyComponentIds.add(terminologyComponent);
				}
			}
		});
		return getFirst(terminologyComponentIds, null);
	}

	@Override
	public String getVersionId(final IBranchPathMap branchPathMap, final ICodeSystem codeSystem) {
		final Collection<String> versions = newConcurrentHashSet();
		forEach(getServices(branchPathMap), new Procedure<Pair<InternalTerminologyRegistryService, IBranchPath>>() {
			protected void doApply(final Pair<InternalTerminologyRegistryService, IBranchPath> pair) {
				final String versionId = pair.getA().getVersionId(pair.getB(), codeSystem);
				if (!isEmpty(versionId)) {
					versions.add(versionId);
				}
			}
		});
		return getFirst(versions, null);
	}
	
	@Override
	public Map<String, List<ICodeSystemVersion>> getAllVersion() {
		final Map<String, List<ICodeSystemVersion>> versions = newConcurrentMap();
		forEach(getServiceForClass(ICDOConnectionManager.class).uuidKeySet(), new Procedure<String>() {
			protected void doApply(final String repositoryUuid) {
				final InternalTerminologyRegistryService registryService = InternalTerminologyRegistryServiceRegistry.INSTANCE.getService(repositoryUuid);
				List<ICodeSystemVersion> existingVersion = newArrayList(registryService.getCodeSystemVersionsFromRepositoryWithInitVersion(createMainPath(), repositoryUuid));
				existingVersion = newArrayList(getServiceForClass(CodeSystemService.class).decorateWithPatchedFlag(repositoryUuid, existingVersion));
				sort(existingVersion, reverseOrder(ICodeSystemVersion.VERSION_IMPORT_DATE_COMPARATOR));
				existingVersion.add(0, LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(repositoryUuid));
				versions.put(repositoryUuid, existingVersion);
			}
		});
		
		return versions;
	}
	
	@Override
	public List<ICodeSystemVersion> getAllVersion(final String repositoryUuid) {
		checkNotNull(repositoryUuid, "repositoryUuid");
		final List<ICodeSystemVersion> versions = newArrayList(InternalTerminologyRegistryServiceRegistry.INSTANCE.getService(repositoryUuid) //
				.getCodeSystemVersionsFromRepository(createMainPath(), repositoryUuid));
		sort(versions, reverseOrder(ICodeSystemVersion.VERSION_IMPORT_DATE_COMPARATOR));
		return versions;
	}
	
	private Iterable<Pair<InternalTerminologyRegistryService, IBranchPath>> getServices(final IBranchPathMap branchPathMap) {
		return InternalTerminologyRegistryServiceRegistry.INSTANCE.getServices(branchPathMap);
	}
	
}