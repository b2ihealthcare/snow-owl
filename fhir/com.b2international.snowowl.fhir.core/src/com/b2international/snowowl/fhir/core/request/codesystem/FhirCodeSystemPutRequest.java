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

import org.elasticsearch.common.Strings;

import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * @since 8.2.0
 */
final class FhirCodeSystemPutRequest implements Request<RepositoryContext, Boolean> {

	private static final long serialVersionUID = 1L;
	
	private final CodeSystem codeSystem;
	
	public FhirCodeSystemPutRequest(CodeSystem codeSystem) {
		this.codeSystem = codeSystem;
	}

	@Override
	public Boolean execute(RepositoryContext context) {
		checkArgument(!Strings.isNullOrEmpty(codeSystem.getToolingId()), "Cannot create/update code system without tooling id.");

		FhirCodeSystemCUDSupport cudSupport = context.service(RepositoryManager.class).get(codeSystem.getToolingId())
				.optionalService(FhirCodeSystemCUDSupport.class)
				.orElse(FhirCodeSystemCUDSupport.DEFAULT);

		cudSupport.updateOrCreateCodeSystem(context, codeSystem);
		return Boolean.TRUE;
	}
	
}
