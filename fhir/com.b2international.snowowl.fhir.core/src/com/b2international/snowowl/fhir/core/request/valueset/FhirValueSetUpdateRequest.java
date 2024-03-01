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
package com.b2international.snowowl.fhir.core.request.valueset;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotEmpty;

import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateRequest;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateResult;

/**
 * Updates an existing value set or creates a new one if no value set exists for the specified identifier.
 * 
 * @see <a href="https://hl7.org/fhir/http.html#update">Update interaction</a>
 * @since 8.2.0
 */
final class FhirValueSetUpdateRequest extends FhirResourceUpdateRequest {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	private final ValueSet fhirValueSet;
	
	@NotNull
	private final Map<String, ResourceURI> systemUriOverrides;
	
	private final String author;
	private final String owner;
	private final String ownerProfileName;
	private final LocalDate defaultEffectiveDate;

	@NotEmpty
	private final String bundleId;

	public FhirValueSetUpdateRequest(
		final ValueSet fhirValueSet,
		final Map<String, ResourceURI> systemUriOverrides,
		final String author,
		final String owner, 
		final String ownerProfileName,
		final LocalDate defaultEffectiveDate, 
		final String bundleId) {
		
		this.fhirValueSet = fhirValueSet;
		this.systemUriOverrides = systemUriOverrides;
		this.author = author;
		this.owner = owner;
		this.ownerProfileName = ownerProfileName;
		this.defaultEffectiveDate = defaultEffectiveDate;
		this.bundleId = bundleId;
	}
	
	@Override
	public FhirResourceUpdateResult execute(final RepositoryContext context) {
		checkAuthorization(context, bundleId);
		final Optional<FhirValueSetWriteSupport> valueSetWriteSupport = context.optionalService(FhirValueSetWriteSupport.class);
		
		return valueSetWriteSupport
			.orElseThrow(() -> new NotImplementedException("FHIR ValueSet resource creation is not configured."))
			.update(context, fhirValueSet, systemUriOverrides, author, owner, ownerProfileName, defaultEffectiveDate, bundleId);
	}
}
