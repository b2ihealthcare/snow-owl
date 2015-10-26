/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.server.events;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RepositoryRequest;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSets;

/**
 * @since 4.5
 */
public abstract class SnomedRequests {

	private SnomedRequests() {
	}
	
	public static Request<ServiceProvider, SnomedReferenceSets> prepareGetReferenceSets(String branch) {
		return new RepositoryRequest<>("SNOMEDCT", branch, new SnomedRefSetReadAllRequest());
	}
	
	public static Request<ServiceProvider, SnomedReferenceSet> prepareGetReferenceSet(String branch, String referenceSetId) {
		return new RepositoryRequest<>("SNOMEDCT", branch, new SnomedRefSetReadRequest(referenceSetId));
	}

	public static Request<ServiceProvider, SnomedConcepts> prepareGetConcepts(String branch, int offset, int limit) {
		return new RepositoryRequest<>("SNOMEDCT", branch, new SnomedConceptReadAllRequest(offset, limit));
	}
	
}
