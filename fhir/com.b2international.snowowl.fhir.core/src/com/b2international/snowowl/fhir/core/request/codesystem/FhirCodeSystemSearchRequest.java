/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.request.codesystem;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Bundle.Builder;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * @since 8.0
 */
final class FhirCodeSystemSearchRequest extends SearchResourceRequest<RepositoryContext, Bundle> {

	private static final long serialVersionUID = 1L;

	@Override
	protected Bundle createEmptyResult(int limit) {
		return prepareBundle().total(0).build();
	}

	@Override
	protected Bundle doExecute(RepositoryContext context) throws IOException {
		CodeSystems internalCodeSystems = CodeSystemRequests.prepareSearchCodeSystem()
				.filterByIds(componentIds())
//				.filterByIdsOrUrls(componentIds()) // XXX componentId in FHIR Search Request can be either logicalId or url so either of them can match, apply to the search
				// TODO apply other FHIR search parameters
				.setLimit(limit())
				.setSearchAfter(searchAfter())
				.setExpand(expand())
				.setFields(fields())
				.setLocales(locales())
				.sortBy(sortBy())
				.build()
				.execute(context);
		
		return prepareBundle()
				.entry(internalCodeSystems.stream().map(codeSystem -> toFhirCodeSystemEntry(context, codeSystem)).collect(Collectors.toList()))
				.total(internalCodeSystems.getTotal())
				.build();
	}
	
	private Builder prepareBundle() {
		return Bundle.builder(UUID.randomUUID().toString())
				.type(BundleType.SEARCHSET);
	}

	private Entry toFhirCodeSystemEntry(RepositoryContext context, com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
		return new Entry(null, toFhirCodeSystem(context, codeSystem));
	}

	private CodeSystem toFhirCodeSystem(RepositoryContext context, com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
		return CodeSystem.builder()
				// TODO fill out Code System specific properties, if required use tooling specific extensions to fill all fields
				.id(codeSystem.getId())
				.url(codeSystem.getUrl())
				.build();
	}
	
}
