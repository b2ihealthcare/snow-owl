/*
 * Copyright 2022-2024 B2i Healthcare, https://b2ihealthcare.com
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

import org.hl7.fhir.r5.model.CodeSystem;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotEmpty;

import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateRequest;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateResult;

/**
 * Updates an existing code system or creates a new one if no code system exists for the specified identifier.
 * 
 * @see <a href="https://hl7.org/fhir/http.html#update">Update interaction</a>
 * @since 8.2.0
 */
final class FhirCodeSystemUpdateRequest extends FhirResourceUpdateRequest {

	private static final long serialVersionUID = 1L;
	
	private static final int CONCEPT_LIMIT = 5000;

	@NotNull
	private final CodeSystem fhirCodeSystem;
	
	private final String author;
	private final String owner;
	private final String ownerProfileName;
	private final LocalDate defaultEffectiveDate;

	@NotEmpty
	private final String bundleId;

	public FhirCodeSystemUpdateRequest(
		final CodeSystem fhirCodeSystem,
		final String author,
		final String owner, 
		final String ownerProfileName,
		final LocalDate defaultEffectiveDate, 
		final String bundleId) {
		
		this.fhirCodeSystem = fhirCodeSystem;
		this.author = author;
		this.owner = owner;
		this.ownerProfileName = ownerProfileName;
		this.defaultEffectiveDate = defaultEffectiveDate;
		this.bundleId = bundleId;
	}

	private boolean conceptCountUnderLimit() {
		return Optional.ofNullable(fhirCodeSystem.getConcept())
			.map(concepts -> concepts.size() < CONCEPT_LIMIT)
			.orElse(Boolean.TRUE);
	}
	
	@Override
	public FhirResourceUpdateResult execute(final RepositoryContext context) {
		checkAuthorization(context, bundleId);
		checkArgument(conceptCountUnderLimit(), "Maintenance of code systems with more than %s codes is not supported.", CONCEPT_LIMIT);
		
		final Optional<FhirCodeSystemWriteSupport> codeSystemOperations = context.optionalService(FhirCodeSystemWriteSupport.class);

		return codeSystemOperations
			.orElseThrow(() -> new NotImplementedException("FHIR CodeSystem resource creation is not configured."))
			.update(context, fhirCodeSystem, author, owner, ownerProfileName, defaultEffectiveDate, bundleId);
	}
}
