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
package com.b2international.snowowl.fhir.core.request.codesystem;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * @since 8.0
 */
public interface FhirCodeSystemResourceConverter {

	FhirCodeSystemResourceConverter DEFAULT = new FhirCodeSystemResourceConverter() {
	};

	/**
	 * Implementers may count the number of concepts in the given resource. This method by default uses the generic concept search API to provide the count value.
	 *  
	 * @param resourceUri
	 * @return
	 */
	default int count(ServiceProvider context, ResourceURI resourceUri) {
		return CodeSystemRequests.prepareSearchConcepts()
				.setLimit(0)
				.build(resourceUri)
				.getRequest()
				.execute(context)
				.getTotal();
	}

	/**
	 * Implementers may expand the FHIR CodeSystem with additional code system specific information, such as concept properties, filters and override
	 * any defaults set by the {@link FhirCodeSystemSearchRequest}. This method by default does nothing.
	 * 
	 * @param context - the context to use when expanding additional information
	 * @param entry - the entry to expand with additional information
	 * @param codeSystem - the base CodeSystem to use
	 */
	default void expand(ServiceProvider context, CodeSystem.Builder entry, com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
	}

}
