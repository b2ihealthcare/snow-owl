/*
 * Copyright 2022-2023 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateResult;

/**
 * @since 8.2.0
 */
public final class FhirCodeSystemUpdateRequestBuilder implements ResourceRepositoryRequestBuilder<FhirResourceUpdateResult> {

	private CodeSystem fhirCodeSystem;
	private String author;
	private String owner;
	private String ownerProfileName;
	private LocalDate defaultEffectiveDate;
	private String bundleId = IComponent.ROOT_ID;

	public FhirCodeSystemUpdateRequestBuilder setFhirCodeSystem(final CodeSystem fhirCodeSystem) {
		this.fhirCodeSystem = fhirCodeSystem;
		return this;
	}

	public FhirCodeSystemUpdateRequestBuilder setAuthor(String author) {
		this.author = author;
		return this;
	}
	
	public FhirCodeSystemUpdateRequestBuilder setOwner(final String owner) {
		this.owner = owner;
		return this;
	}
	
	public FhirCodeSystemUpdateRequestBuilder setOwnerProfileName(final String ownerProfileName) {
		this.ownerProfileName = ownerProfileName;
		return this;
	}
	
	public FhirCodeSystemUpdateRequestBuilder setDefaultEffectiveDate(final LocalDate defaultEffectiveDate) {
		this.defaultEffectiveDate = defaultEffectiveDate;
		return this;
	}

	public FhirCodeSystemUpdateRequestBuilder setBundleId(final String bundleId) {
		if (!StringUtils.isEmpty(bundleId)) {
			this.bundleId = bundleId;
		} else {
			this.bundleId = IComponent.ROOT_ID;
		}
		return this;
	}

	@Override
	public Request<RepositoryContext, FhirResourceUpdateResult> build() {
		return new FhirCodeSystemUpdateRequest(fhirCodeSystem, author, owner, ownerProfileName, defaultEffectiveDate, bundleId);
	}
}
