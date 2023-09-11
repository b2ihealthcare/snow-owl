/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.hl7.fhir.r5.model.CodeSystem;

import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequestProperties;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.google.common.collect.ImmutableSet;

import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;

/**
 * Performs the lookup operation based on the parameter-based lookup request.
 * 
 * @since 8.0
 */
final class FhirLookupRequest extends FhirRequest<LookupResult> {

	private static final long serialVersionUID = 1L;
	
	private static final Set<String> LOOKUP_REQUEST_PROPS = Arrays.stream(LookupRequestProperties.values())
		.map(prop -> prop.getCode().getValueAsString())
		.collect(Collectors.toSet());

	@Valid
	private final LookupRequest request;

	FhirLookupRequest(final LookupRequest request) {
		super(request.getInputSystem().getValueAsString(), request.getInputVersion());
		this.request = request;
	}

	@Override
	protected LookupResult doExecute(ServiceProvider context, CodeSystem codeSystem) {
		validateProperties(codeSystem);
		
		final String toolingId = codeSystem.getUserString("toolingId");
		final FhirCodeSystemLookupConverter converter = Optional.ofNullable(toolingId)
			.map(ti -> context.service(RepositoryManager.class).get(ti))
			.flatMap(sp -> sp.optionalService(FhirCodeSystemLookupConverter.class))
			.orElse(FhirCodeSystemLookupConverter.DEFAULT);
		
		final String conceptId = request.getInputCode().getValueAsString();
		final String acceptLanguage = extractLocale(request.getDisplayLanguage());
		final String conceptExpand = converter.configureConceptExpand(request);
		
		final Concept concept = CodeSystemRequests.prepareSearchConcepts()
			.one()
			.filterByCodeSystem(codeSystem.getName())
			.filterById(conceptId)
			.setLocales(acceptLanguage)
			.setExpand(conceptExpand)
			.buildAsync()
			.execute(context)
			.first()
			.orElseThrow(() -> new InvalidRequestException(String.format("Code '%s' not found", conceptId)));
		
		final LookupResult result = new LookupResult();
		
		if (request.containsProperty(LookupRequestProperties.SYSTEM.getCode())) {
			result.setSystem(new UriDt(codeSystem.getUrl()));
		}
		
		result.setName(codeSystem.getTitle());
		result.setVersion(codeSystem.getVersion());
		result.setDisplay(concept.getTerm());
		// result.setDefinition(...) is not set, for now

		result.setDesignations(converter.expandDesignations(context, codeSystem, concept, request, acceptLanguage));
		result.setProperties(converter.expandProperties(context, codeSystem, concept, request));

		return result;
	}
	
	private void validateProperties(CodeSystem codeSystem) {
		final Set<String> unsupportedProperties = newHashSet(request.getPropertyCodes());

		// Properties defined by the specification are all supported
		unsupportedProperties.removeAll(LOOKUP_REQUEST_PROPS);
		
		// Also the ones listed on the code system
		final Set<String> conceptProperties = codeSystem.getProperty()
			.stream()
			.map(pc -> pc.getCode())
			.collect(Collectors.toSet());
		
		unsupportedProperties.removeAll(conceptProperties);
		
		// XXX: In the past we allowed property URIs through, but these should be pure codes only!
		if (!unsupportedProperties.isEmpty()) {
			final Set<String> supportedProperties = ImmutableSet.<String>builder()
				.addAll(LOOKUP_REQUEST_PROPS)
				.addAll(conceptProperties)
				.build();
			
			throw new InvalidRequestException(String.format("Unrecognized propert%s %s. Supported properties are: %s.", 
				(unsupportedProperties.size() > 1 ? "ies" : "y"), unsupportedProperties, supportedProperties));
		}
	}
}
