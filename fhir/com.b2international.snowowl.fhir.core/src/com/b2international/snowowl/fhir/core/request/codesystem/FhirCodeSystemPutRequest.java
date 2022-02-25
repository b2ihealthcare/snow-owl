/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.stream.Collectors;

import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * @since 8.2.0
 */
final class FhirCodeSystemPutRequest implements Request<RepositoryContext, Boolean> {

	private static final long serialVersionUID = 1L;
	private static final int CONCEPT_LIMIT = 5000;
	
	private final CodeSystem codeSystem;
	
	public FhirCodeSystemPutRequest(CodeSystem codeSystem) {
		this.codeSystem = codeSystem;
	}

	@Override
	public Boolean execute(RepositoryContext context) {
		checkArgument(codeSystem.getConcepts() == null || codeSystem.getConcepts().size() < CONCEPT_LIMIT, "Maintenance of code systems with more than %d codes is not supported.", CONCEPT_LIMIT);
		
		FhirCodeSystemCUDSupport cudSupport =	context.service(RepositoryManager.class)
				.repositories()
				.stream()
				.filter(r -> r.optionalService(FhirCodeSystemCUDSupport.class).isPresent())
				.map(r -> r.service(FhirCodeSystemCUDSupport.class))
				.collect(Collectors.toList())
				.get(0);
		
		cudSupport.updateOrCreateCodeSystem(context, codeSystem);
		return Boolean.TRUE;			
	}
	
}
