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
package com.b2international.snowowl.fhir.core.request.conceptmap;

import org.hl7.fhir.r5.model.Parameters;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.fhir.core.operations.ConceptMapTranslateParameters;
import com.b2international.snowowl.fhir.core.operations.ConceptMapTranslateResultParameters;

/**
 * @since 8.0
 */
public final class FhirConceptMapTranslateRequestBuilder 
		extends BaseRequestBuilder<FhirConceptMapTranslateRequestBuilder, ServiceProvider, ConceptMapTranslateResultParameters>
		implements SystemRequestBuilder<ConceptMapTranslateResultParameters> {

	private ConceptMapTranslateParameters parameters;
	
	public FhirConceptMapTranslateRequestBuilder setParameters(Parameters parameters) {
		return setParameters(new ConceptMapTranslateParameters(parameters));
	}
	
	public FhirConceptMapTranslateRequestBuilder setParameters(ConceptMapTranslateParameters parameters) {
		this.parameters = parameters;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, ConceptMapTranslateResultParameters> doBuild() {
		return new FhirConceptMapTranslateRequest(parameters);
	}
	
	
}
