/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.CodeType;
import org.hl7.fhir.r5.model.UriType;

import com.b2international.commons.http.AcceptLanguageHeader;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

/**
 * Superclass for FHIR requests that need an associated code system resource for completion.
 * 
 * @param <R> - the response type
 * @since 8.0
 */
public abstract class FhirRequest<R> implements Request<ServiceProvider, R> {

	private static final long serialVersionUID = 1L;

	@NotNull
	private UriType system;

	// @Nullable
	private String version;

	protected void setSystem(final UriType system) {
		this.system = system;
	}
	
	protected void setVersion(final String version) {
		this.version = version;
	}

	@Override
	public final R execute(final ServiceProvider context) {
		final String idOrUrl = system.asStringValue();
		
		// First attempt: use "system" in an URL filter
		final Optional<CodeSystem> codeSystemByUrl = FhirRequests.codeSystems().prepareSearch()
			.one()
			.filterByUrl(idOrUrl)
			.filterByVersion(version)
			.setSummary(configureSummary())
			.buildAsync()
			.getRequest()
			.execute(context)
			.getEntry()
			.stream()
			.findFirst()
			.map(ec -> (CodeSystem) ec.getResource());

		// Second attempt: treat "system" as an identifier
		final Optional<CodeSystem> codeSystemByIdOrUrl = codeSystemByUrl.or(() -> FhirRequests.codeSystems().prepareSearch()
			.one()
			.filterById(idOrUrl)
			.filterByVersion(version)
			.setSummary(configureSummary())
			.buildAsync()
			.getRequest()
			.execute(context)
			.getEntry()
			.stream()
			.findFirst()
			.map(ec -> (CodeSystem) ec.getResource()));

		// Ensure that either of the exist
		final CodeSystem codeSystem = codeSystemByIdOrUrl.orElseThrow(() -> new ResourceNotFoundException(
			String.format("Code system with ID or URL '%s' could not be found.", idOrUrl)
		)); 

		return doExecute(context, codeSystem);
	}

	protected SummaryEnum configureSummary() {
		return SummaryEnum.TRUE;
	}

	public static final String extractLocale(final CodeType displayLanguage) {
		if (displayLanguage != null && !displayLanguage.isEmpty()) {
			return displayLanguage.getValueAsString();
		} else {
			return AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER;
		}
	}

	protected abstract R doExecute(ServiceProvider context, CodeSystem codeSystem);
}
