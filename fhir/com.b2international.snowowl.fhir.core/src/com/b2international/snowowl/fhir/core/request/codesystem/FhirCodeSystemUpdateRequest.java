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

import java.time.LocalDate;
import java.util.Optional;

import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * Updates an existing code system or creates a new one if no code system exists for the specified identifier.
 * 
 * @see <a href="https://hl7.org/fhir/http.html#update">Update interaction</a>
 * @since 8.2.0
 */
final class FhirCodeSystemUpdateRequest implements Request<RepositoryContext, Boolean> {

	private static final long serialVersionUID = 1L;
	private static final int CONCEPT_LIMIT = 5000;

	private final CodeSystem fhirCodeSystem;
	private final String author;
	private final String authorProfileName;
	private final LocalDate defaultEffectiveDate;
	private final String bundleId;

	public FhirCodeSystemUpdateRequest(
		final CodeSystem fhirCodeSystem, 
		final String author, 
		final String authorProfileName,
		final LocalDate defaultEffectiveDate, 
		final String bundleId) {
		
		this.fhirCodeSystem = fhirCodeSystem;
		this.author = author;
		this.authorProfileName = authorProfileName;
		this.defaultEffectiveDate = defaultEffectiveDate;
		this.bundleId = bundleId;
	}

	@Override
	public Boolean execute(final RepositoryContext context) {
		checkArgument(
			fhirCodeSystem.getConcepts() == null || fhirCodeSystem.getConcepts().size() < CONCEPT_LIMIT, 
			"Maintenance of code systems with more than %s codes is not supported.", CONCEPT_LIMIT);

		final Optional<FhirCodeSystemOperations> codeSystemOperations = context.optionalService(FhirCodeSystemOperations.class);

		codeSystemOperations
			.orElseThrow(() -> new NotImplementedException("FHIR CodeSystem resource creation is currently unavailable."))
			.update(context, fhirCodeSystem, author, authorProfileName, defaultEffectiveDate, bundleId);

		return Boolean.TRUE;			
	}
}
