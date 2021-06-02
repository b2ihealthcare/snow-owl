/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.springframework.beans.factory.annotation.Autowired;

import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 8.0
 */
public abstract class AbstractFhirController extends AbstractRestService {

	public static final String APPLICATION_FHIR_JSON = "application/fhir+json;charset=utf-8";
	
	@Autowired
	protected ObjectMapper mapper;
	
	protected final <T> T toRequest(Parameters.Fhir in, Class<T> request) {
		return mapper.convertValue(in.toJson(), request);
	}
	
	protected final Parameters.Fhir toResponse(Object response) {
		return new Parameters.Fhir(Parameters.from(response));
	}
	
}
