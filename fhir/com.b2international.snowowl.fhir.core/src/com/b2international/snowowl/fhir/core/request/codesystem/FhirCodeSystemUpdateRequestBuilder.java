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

import java.time.LocalDate;

import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * @since 8.2.0
 */
public final class FhirCodeSystemUpdateRequestBuilder implements ResourceRepositoryRequestBuilder<Boolean> {

	private CodeSystem fhirCodeSystem;
	private String author;
	private String authorDisplayName;
	private LocalDate defaultEffectiveDate;
	private String bundleId = IComponent.ROOT_ID;

	public FhirCodeSystemUpdateRequestBuilder setFhirCodeSystem(final CodeSystem fhirCodeSystem) {
		this.fhirCodeSystem = fhirCodeSystem;
		return this;
	}

	public FhirCodeSystemUpdateRequestBuilder setAuthor(final String author) {
		this.author = author;
		return this;
	}
	
	public FhirCodeSystemUpdateRequestBuilder setAuthorDisplayName(final String authorDisplayName) {
		this.authorDisplayName = authorDisplayName;
		return this;
	}
	
	public FhirCodeSystemUpdateRequestBuilder setDefaultEffectiveDate(final LocalDate defaultEffectiveDate) {
		this.defaultEffectiveDate = defaultEffectiveDate;
		return this;
	}

	public FhirCodeSystemUpdateRequestBuilder setBundleId(final String bundleId) {
		this.bundleId = bundleId;
		return this;
	}

	@Override
	public Request<RepositoryContext, Boolean> build() {
		return new FhirCodeSystemUpdateRequest(fhirCodeSystem, author, authorDisplayName, defaultEffectiveDate, bundleId);
	}
}
