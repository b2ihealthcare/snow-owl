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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

final class FhirCodeSystemRequest<R> extends DelegatingRequest<ServiceProvider, FhirCodeSystemContext, R> {

	private final String codeSystemId;

	public FhirCodeSystemRequest(Request<FhirCodeSystemContext, R> next, String codeSystemId) {
		super(next);
		this.codeSystemId = codeSystemId;
	}

	@Override
	public R execute(ServiceProvider context) {
		// first try to find the code system by treating the ID as logicalID
		CodeSystem codeSystem = null;
		try {
			codeSystem = FhirRequests.prepareGetCodeSystem(codeSystemId)
				.build()
				.execute(context);
		} catch (FhirException e) {
			// if not found then treat it as system URI
			codeSystem = FhirRequests.prepareSearchCodeSystem()
					.one()
					.filterBySystem(codeSystemId)
					.build()
					.execute(context)
					.getEntries()
					.stream()
					.findFirst()
					.map(Entry::getResource)
					.map(CodeSystem.class::cast)
					.orElseThrow(() -> FhirException.createFhirError(String.format("No code system found for logicalId/system '%s'", codeSystemId), OperationOutcomeCode.MSG_PARAM_INVALID, "CodeSystem"));
		}
		
		return next(new FhirCodeSystemContext(context, codeSystem));
	}

}
