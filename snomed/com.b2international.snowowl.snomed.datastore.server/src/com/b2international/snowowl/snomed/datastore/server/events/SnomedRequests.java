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
import com.b2international.snowowl.datastore.request.TransactionalRequest;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
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
	
	public static Request<ServiceProvider, ISnomedConcept> prepareGetConcept(String branch, String componentId) {
		return new RepositoryRequest<>("SNOMEDCT", branch, new SnomedConceptReadRequest(componentId));
	}
	
	public static Request<ServiceProvider, Void> prepareDeleteConcept(String branch, String componentId, String userId, String commitComment) {
		return prepareDeleteComponent(branch, componentId, userId, commitComment, Concept.class);
	}
	
	public static Request<ServiceProvider, Void> prepareDeleteDescription(String branch, String componentId, String userId, String commitComment) {
		return prepareDeleteComponent(branch, componentId, userId, commitComment, Description.class);
	}
	
	public static Request<ServiceProvider, Void> prepareDeleteRelationship(String branch, String componentId, String userId, String commitComment) {
		return prepareDeleteComponent(branch, componentId, userId, commitComment, Relationship.class);
	}

	private static Request<ServiceProvider, Void> prepareDeleteComponent(String branch, String componentId, String userId, String commitComment, Class<? extends Component> type) {
		return new RepositoryRequest<>("SNOMEDCT", branch, new TransactionalRequest<>(userId, commitComment, new SnomedComponentDeleteRequest(componentId, type)));
	}
	
	public static Request<ServiceProvider, ISnomedConcept> prepareCreateConcept(String branch, String userId, String commitComment, SnomedConceptCreateRequest next) {
		return new RepositoryRequest<>("SNOMEDCT", branch, new TransactionalRequest<>(userId, commitComment, next));
	}
	
	public static Request<ServiceProvider, ISnomedDescription> prepareCreateDescription(String branch, String userId, String commitComment, SnomedDescriptionCreateRequest next) {
		return new RepositoryRequest<>("SNOMEDCT", branch, new TransactionalRequest<>(userId, commitComment, next));
	}
	
	public static Request<ServiceProvider, ISnomedRelationship> prepareCreateRelationship(String branch, String userId, String commitComment, SnomedRelationshipCreateRequest next) {
		return new RepositoryRequest<>("SNOMEDCT", branch, new TransactionalRequest<>(userId, commitComment, next));
	}

	public static Request<ServiceProvider, SnomedReferenceSet> prepareCreateRefSet(String branch, String userId, String commitComment, SnomedRefSetCreateRequest next) {
		return new RepositoryRequest<>("SNOMEDCT", branch, new TransactionalRequest<>(userId, commitComment, next));
	}
	
}
