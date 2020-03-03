/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.codesystem;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.CodeSystem;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.inject.Provider;

/**
 * @since 7.1
 */
@Component
public final class CodeSystemService {

	private static final Ordering<CodeSystem> SHORT_NAME_ORDERING = Ordering.natural().onResultOf(CodeSystem::getShortName);

	@Autowired
	private Provider<IEventBus> bus;
	
	/**
	 * Lists all registered code systems.
	 * 
	 * @return a list containing all registered code systems, ordered by short name (never {@code null})
	 */
	public List<CodeSystem> getCodeSystems() {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem().all().build(repositoryId).execute(bus.get()));
		}
		return Promise.all(getAllCodeSystems)
				.then(results -> {
					final List<CodeSystem> codeSystems = newArrayList();
					for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
						codeSystems.addAll(Lists.transform(result.getItems(), input -> CodeSystem.builder(input).build()));
					}
					return SHORT_NAME_ORDERING.immutableSortedCopy(codeSystems);
				})
				.getSync(1, TimeUnit.MINUTES);
	}

	/**
	 * Retrieves a single code system matches the given shortName or object identifier (OID) parameter, if it exists.
	 * 
	 * @param shortNameOrOid the code system short name or OID to look for, eg. "{@code SNOMEDCT}" or "{@code 3.4.5.6.10000}" (may not be {@code null})
	 * 
	 * @return the requested code system
	 * 
	 * @throws CodeSystemNotFoundException if a code system with the given short name or OID is not registered
	 */
	public CodeSystem getCodeSystemById(String shortNameOrOid) {
		checkNotNull(shortNameOrOid, "Shortname Or OID parameter may not be null.");
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem()
					.all()
					.filterById(shortNameOrOid)
					.build(repositoryId)
					.execute(bus.get()));
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
				.getSync(1, TimeUnit.MINUTES);
	}
	
	private List<String> getRepositoryIds() {
		return RepositoryRequests.prepareSearch()
				.all()
				.buildAsync()
				.execute(bus.get())
				.then(repos -> repos.stream().map(RepositoryInfo::id).collect(Collectors.toList()))
				.getSync(1, TimeUnit.MINUTES);
	}
	
}