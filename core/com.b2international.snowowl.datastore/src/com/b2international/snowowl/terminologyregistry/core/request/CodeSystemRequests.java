/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.request;

import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.Repositories;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.ConceptSearchRequestBuilder;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.version.CodeSystemVersionCreateRequestBuilder;

/**
 * @since 4.7
 */
public class CodeSystemRequests {

	private CodeSystemRequests() {}
	
	public static CodeSystemCreateRequestBuilder prepareNewCodeSystem() {
		return new CodeSystemCreateRequestBuilder();
	}

	public static CodeSystemUpdateRequestBuilder prepareUpdateCodeSystem(final String uniqueId) {
		return new CodeSystemUpdateRequestBuilder(uniqueId);
	}

	public static CodeSystemGetRequestBuilder prepareGetCodeSystem(final String uniqeId) {
		return new CodeSystemGetRequestBuilder(uniqeId);
	}

	public static CodeSystemSearchRequestBuilder prepareSearchCodeSystem() {
		return new CodeSystemSearchRequestBuilder();
	}

	public static CodeSystemVersionSearchRequestBuilder prepareSearchCodeSystemVersion() {
		return new CodeSystemVersionSearchRequestBuilder();
	}
	
	public static CodeSystemVersionCreateRequestBuilder prepareNewCodeSystemVersion() {
		return new CodeSystemVersionCreateRequestBuilder();
	}
	
	/**
	 * Creates a new generic concept search request builder.
	 * 
	 * @return the builder to configure for generic concept search
	 */
	public static ConceptSearchRequestBuilder prepareSearchConcepts() {
		return new ConceptSearchRequestBuilder();
	}

	/**
	 * Returns all {@link CodeSystemEntry}s from all repositories.
	 * @param context
	 * @return
	 */
	public static List<CodeSystemEntry> getAllCodeSystems(ServiceProvider context) {
		final Repositories repositories = RepositoryRequests.prepareSearch()
			.all()
			.build()
			.execute(context);
		
		return repositories.getItems()
			.stream()
			.flatMap(repository -> {
				return CodeSystemRequests.prepareSearchCodeSystem()
						.all()
						.build(repository.id())
						.getRequest()
						.execute(context)
						.stream();
			})
			.collect(Collectors.toList());
	}

	public static CodeSystemEntry getCodeSystem(ServiceProvider context, String codeSystem) {
		final Repositories repositories = RepositoryRequests.prepareSearch()
				.all()
				.build()
				.execute(context);
			
		return repositories.getItems()
			.stream()
			.flatMap(repository -> {
				return CodeSystemRequests.prepareSearchCodeSystem()
						.one()
						.filterById(codeSystem)
						.build(repository.id())
						.getRequest()
						.execute(context)
						.stream();
			})
			.findFirst()
			.orElseThrow(() -> new BadRequestException("CodeSystem '%s' cannot be found", codeSystem));
	}

}
