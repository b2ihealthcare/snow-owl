/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest;

import java.util.Set;

import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.google.common.collect.ImmutableList;
import com.google.inject.Provider;

import ca.uhn.fhir.rest.annotation.Elements;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;

public class CodeSystemResourceProvider implements IResourceProvider {

	@Autowired
	private Provider<IEventBus> bus;
	
	@Override
	public Class<CodeSystem> getResourceType() {
		return CodeSystem.class;
	}

	@Read
	public CodeSystem getCodeSystemById(@IdParam IdType codeSystemId, SummaryEnum summary, @Elements Set<String> elements, RequestDetails requestDetails) {
		
		try {
			
			return FhirRequests.codeSystems().prepareGet(codeSystemId.getIdPart())
				.setSummary(summary)
				.setElements(elements != null ? ImmutableList.copyOf(elements) : null)
				.setLocales(requestDetails.getHeader("Accept-Language"))
				.buildAsync()
				.execute(bus.get())
				.getSync();
			
		} catch (NotFoundException e) {
			return null;
		}
	}
}
