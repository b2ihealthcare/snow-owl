/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.browser;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.snomed.api.rest.domain.AbstractSnomedComponentRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedRelationshipRestInput;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 1.0
 */
public class SnomedBrowserConceptRestInput extends AbstractSnomedComponentRestInput<SnomedConceptCreateRequestBuilder> {

	private List<SnomedDescriptionRestInput> descriptions = Collections.emptyList();
	private List<SnomedRelationshipRestInput> relationships = Collections.emptyList();

	@Override
	protected SnomedConceptCreateRequestBuilder createRequestBuilder() {
		return SnomedRequests.prepareNewConcept();
	}

	@Override
	public SnomedConceptCreateRequestBuilder toRequestBuilder() {
		final SnomedConceptCreateRequestBuilder req = super.toRequestBuilder();

		for (SnomedRelationshipRestInput restRelationship : getRelationships()) {
			if (null == restRelationship.getNamespaceId()) {
				restRelationship.setNamespaceId(getNamespaceId());
			}
			
			req.addRelationship(restRelationship.toRequestBuilder());
		}
		
		for (SnomedDescriptionRestInput restDescription : getDescriptions()) {
			// Propagate namespace from concept if present, and the description does not already have one
			if (null == restDescription.getNamespaceId()) {
				restDescription.setNamespaceId(getNamespaceId());
			}
			
			req.addDescription(restDescription.toRequestBuilder());
		}

		return req;
	}

	public List<SnomedDescriptionRestInput> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<SnomedDescriptionRestInput> descriptions) {
		this.descriptions = descriptions;
	}

	public List<SnomedRelationshipRestInput> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<SnomedRelationshipRestInput> relationships) {
		this.relationships = relationships;
	}

}
