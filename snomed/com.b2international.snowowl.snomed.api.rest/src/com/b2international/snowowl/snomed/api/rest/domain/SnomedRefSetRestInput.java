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
package com.b2international.snowowl.snomed.api.rest.domain;

import com.b2international.snowowl.datastore.request.TransactionalRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.SnomedTransactionalRequestBuilder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public class SnomedRefSetRestInput {

	@JsonUnwrapped
	private SnomedConceptRestInput conceptRestInput;
	
	private SnomedRefSetType type;
	private String referencedComponentType;
	
	public SnomedRefSetType getType() {
		return type;
	}
	
	public String getReferencedComponentType() {
		return referencedComponentType;
	}
	
	public void setType(SnomedRefSetType type) {
		this.type = type;
	}
	
	public void setReferencedComponentType(String referencedComponentType) {
		this.referencedComponentType = referencedComponentType;
	}

	public SnomedConceptRestInput getConceptRestInput() {
		return conceptRestInput;
	}
	
	public void setConceptRestInput(SnomedConceptRestInput conceptRestInput) {
		this.conceptRestInput = conceptRestInput;
	}
	
	public SnomedTransactionalRequestBuilder<String> toRequestBuilder() {

		SnomedRefSetCreateRequestBuilder refsetCreateRequest = SnomedRequests.prepareNewRefSet()
				.setType(type)
				.setReferencedComponentType(referencedComponentType);
		
		if (!Strings.isNullOrEmpty(conceptRestInput.getId()) && conceptRestInput.getDescriptions().isEmpty()) {
			refsetCreateRequest.setIdentifierId(conceptRestInput.getId());
			return refsetCreateRequest;
		}
		
		SnomedConceptCreateRequestBuilder conceptRequest = conceptRestInput.toRequestBuilder();
		
		if (conceptRestInput.getRelationships().isEmpty()) {
			conceptRequest.addParent(SnomedRefSetUtil.getParentConceptId(getType()));
		}
		
		conceptRequest.setRefSet(refsetCreateRequest);
		
		return conceptRequest;
	}
	
}
