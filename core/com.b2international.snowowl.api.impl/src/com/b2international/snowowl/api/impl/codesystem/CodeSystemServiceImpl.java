/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.impl.codesystem;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.stream.Collectors;

import com.b2international.snowowl.api.codesystem.ICodeSystemService;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystem;
import com.b2international.snowowl.api.impl.codesystem.domain.CodeSystem;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public final class CodeSystemServiceImpl implements ICodeSystemService {

	private static final Ordering<ICodeSystem> SHORT_NAME_ORDERING = Ordering.natural().onResultOf(ICodeSystem::getShortName);

	@Override
	public List<ICodeSystem> getCodeSystems() {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem().all().build(repositoryId).execute(getBus()));
		}
		return Promise.all(getAllCodeSystems)
				.then(results -> {
					final List<ICodeSystem> codeSystems = newArrayList();
					for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
						codeSystems.addAll(Lists.transform(result.getItems(), input -> CodeSystem.builder(input).build()));
					}
					return SHORT_NAME_ORDERING.immutableSortedCopy(codeSystems);
				})
				.getSync();
	}

	private IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

	@Override
	public ICodeSystem getCodeSystemById(String shortNameOrOid) {
		checkNotNull(shortNameOrOid, "Shortname Or OID parameter may not be null.");
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem()
					.all()
					.filterById(shortNameOrOid)
					.build(repositoryId)
					.execute(getBus()));
		}
		return Promise.all(getAllCodeSystems)
				.then(results -> {
					for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
						if (!result.getItems().isEmpty()) {
							return CodeSystem.builder(Iterables.getOnlyElement(result.getItems())).build();
						}
					}
					throw new NotFoundException("CodeSystem", shortNameOrOid);
				})
				.getSync();
	}
	
	private List<String> getRepositoryIds() {
		return RepositoryRequests.prepareSearch()
				.all()
				.buildAsync()
				.execute(getBus())
				.then(repos -> repos.stream().map(RepositoryInfo::id).collect(Collectors.toList()))
				.getSync();
	}
	
}