/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.CodeType;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.http.AcceptLanguageHeader;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.uri.ResourceURLSchemaSupport;
import com.b2international.snowowl.fhir.core.Summary;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.google.common.base.Strings;

/**
 * @since 8.0
 * @param <R>
 */
public abstract class FhirRequest<R> implements Request<ServiceProvider, R> {

	private static final long serialVersionUID = 1L;
	
	private final String system;
	
	private final String version;

	public FhirRequest(String system, String version) {
		this.system = system;
		this.version = version;
	}
	
	@Override
	public final R execute(ServiceProvider context) {
		// try as is via the URL + version (optional) config
		CodeSystem codeSystem = fetchCodeSystemByUrlAndVersion(context)
				.or(() -> fetchCodeSystemByIdAndVersion(context))
				.or(() -> {
					// perform the third step only if there is a version specified
					if (Strings.isNullOrEmpty(version)) {
						return Optional.empty();
					} else {
						return fetchCodeSystemByUrl(context, system)
								// if there is a codesystem with the specified URL then construct a versioned form using its official URL schema from its tooling
								.flatMap((cs) -> fetchCodeSystemByUrl(context, context.service(RepositoryManager.class).get((String) cs.getUserData("toolingId")).service(ResourceURLSchemaSupport.class).withVersion(system, version, null)));
					}
				})
				.orElseThrow(() -> new NotFoundException("CodeSystem", system));
		
		return doExecute(context, codeSystem);
	}

	private Optional<? extends CodeSystem> fetchCodeSystemByIdAndVersion(ServiceProvider context) {
		return FhirRequests
			.codeSystems().prepareSearch()
			.one()
			.filterById(system)
			.filterByVersion(version)
			.setSummary(configureSummary())
			.buildAsync()
			.getRequest()
			.execute(context)
			.getEntry().stream().findFirst()
			.map(Bundle.BundleEntryComponent.class::cast)
			.map(Bundle.BundleEntryComponent::getResource)
			.map(CodeSystem.class::cast);
	}

	private Optional<CodeSystem> fetchCodeSystemByUrlAndVersion(ServiceProvider context) {
		return FhirRequests
				.codeSystems().prepareSearch()
				.one()
				.filterByUrl(system)
				.filterByVersion(version)
				.setSummary(configureSummary())
				.buildAsync()
				.getRequest()
				.execute(context)
				.getEntry().stream().findFirst()
				.map(Bundle.BundleEntryComponent.class::cast)
				.map(Bundle.BundleEntryComponent::getResource)
				.map(CodeSystem.class::cast);
	}
	
	private Optional<CodeSystem> fetchCodeSystemByUrl(ServiceProvider context, String url) {
		return FhirRequests
				.codeSystems().prepareSearch()
				.one()
				.filterByUrl(url)
				.setSummary(configureSummary())
				.buildAsync()
				.getRequest()
				.execute(context)
				.getEntry().stream().findFirst()
				.map(Bundle.BundleEntryComponent.class::cast)
				.map(Bundle.BundleEntryComponent::getResource)
				.map(CodeSystem.class::cast);
	}
	
	protected String configureSummary() {
		return Summary.TRUE;
	}

	public static final String extractLocales(CodeType displayLanguage) {
		String locales = displayLanguage != null ? displayLanguage.getCode() : null;
		if (CompareUtils.isEmpty(locales)) {
			locales = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER;
		}
		return locales;
	}

	protected abstract R doExecute(ServiceProvider context, CodeSystem codeSystem);

}
