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

import javax.validation.constraints.NotNull;

import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RepositoryRequest;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.fhir.core.codesystems.FhirInternalCode;
import com.b2international.snowowl.fhir.core.codesystems.FhirInternalCodeSystem;
import com.b2international.snowowl.fhir.core.codesystems.FhirInternalCodeSystemRegistry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.provider.FhirCodeSystemExtension;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * @since 7.2
 */
final class FhirLookupRequest extends FhirBaseRequest<FhirCodeSystemContext, LookupResult> {

	@NotNull
	@JsonUnwrapped
	private LookupRequest request;
	
	FhirLookupRequest(LookupRequest request) {
		this.request = request;
	}
	
	@Override
	public LookupResult execute(FhirCodeSystemContext context) {
		final CodeSystem cs = context.codeSystem();
		final FhirCodeSystemExtension ext = FhirCodeSystemExtension.Registry.getCodeSystemExtension(cs.toolingId());
		if (FhirInternalCodeSystemRegistry.TERMINOLOGY_ID.equals(cs.toolingId())) {
			// TODO lookup the code in the enum literals
			throw new NotImplementedException();
		} else {
			// execute as code system revision search, if not internal code system
			return new RevisionIndexRequestBuilder<LookupResult>() {
				@Override
				public Request<BranchContext, LookupResult> build() {
					return context -> ext.lookup(context, request);
				}
			}.build(cs.repositoryId(), cs.branchPath()).getRequest().execute(context);
		}
	}

}
