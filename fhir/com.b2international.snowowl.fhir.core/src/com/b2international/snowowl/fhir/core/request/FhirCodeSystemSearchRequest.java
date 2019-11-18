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
package com.b2international.snowowl.fhir.core.request;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.provider.ICodeSystemApiProvider;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.Maps;

/**
 * @since 7.2
 */
public final class FhirCodeSystemSearchRequest extends SearchResourceRequest<ServiceProvider, Bundle> {

	/**
	 * Most of these defined in the <a href="https://www.hl7.org/fhir/codesystem.html#search">official FHIR CodeSystem search docs</a>.
	 * @since 7.2
	 */
	public enum OptionKey {
		
		/**
		 * Filter codesystems by system URI.
		 */
		SYSTEM
		
	}
	
	@NotEmpty
	private String uri;
	
	FhirCodeSystemSearchRequest() {
	}
	
	void setUri(String uri) {
		this.uri = uri;
	}
	
	@Override
	protected Bundle createEmptyResult(int limit) {
		return Bundle.builder()
				.total(limit)
				.build();
	}

	@Override
	protected Bundle doExecute(ServiceProvider context) throws IOException {
		final Bundle.Builder bundle = Bundle.builder().type(BundleType.SEARCHSET);
		// get all available code systems from our internal codesystem registry
		Promise.all(fetchCodeSystems(context))
				.getSync()
				.stream()
				.map(List.class::cast)
				.flatMap(list -> (Stream<CodeSystem>) list.stream())
				.forEach(cs -> {
					String resourceUrl = String.format("%s/%s", uri, cs.getId().getIdValue());
					Entry entry = new Entry(new Uri(resourceUrl), cs);
					bundle.addEntry(entry);
				});
		
		// append all default FHIR codesystems
		
		
		return bundle.build();
	}
	
	private Collection<Promise<List<CodeSystem>>> fetchCodeSystems(ServiceProvider context) {
		final IEventBus bus = context.service(IEventBus.class);
		return context.service(RepositoryManager.class)
				.repositories()
				.stream()
				.map(Repository::id)
				.map(repositoryId -> {
					// TODO filter/expand options
					return CodeSystemRequests.prepareSearchCodeSystem()
							.all()
							.build(repositoryId)
							.execute(bus)
							.thenWith(cs -> fetchVersions(context, repositoryId, cs));
				})
				.collect(Collectors.toList());
	}

	private Promise<List<CodeSystem>> fetchVersions(ServiceProvider context, String repositoryId, CodeSystems codeSystems) {
		final IEventBus bus = context.service(IEventBus.class);
		// fetch all the versions and generate FHIR CodeSystem for each code system and its versions (N * M FHIR codeSystem at the end)
		return CodeSystemRequests.prepareSearchCodeSystemVersion()
				.all()
				.filterByCodeSystemShortNames(codeSystems.stream().map(com.b2international.snowowl.datastore.CodeSystem::getShortName).collect(Collectors.toSet()))
				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build(repositoryId)
				.execute(bus)
				.then(versions -> toFhirCodeSystem(context, codeSystems, versions));
	}

	private List<CodeSystem> toFhirCodeSystem(ServiceProvider context, CodeSystems codeSystems, CodeSystemVersions versions) {
		final IEventBus bus = context.service(IEventBus.class);
		final Map<String, com.b2international.snowowl.datastore.CodeSystem> codeSystemsById = Maps.uniqueIndex(codeSystems, com.b2international.snowowl.datastore.CodeSystem::getShortName);
		return versions.stream().map(version -> {
			final com.b2international.snowowl.datastore.CodeSystem codeSystem = codeSystemsById.get(version.getCodeSystemShortName());
			return ICodeSystemApiProvider.Registry.getProvider(bus, locales(), codeSystem.getToolingId()).createFhirCodeSystem(codeSystem, version);
		}).collect(Collectors.toList());
	}

}
