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
package com.b2international.snowowl.snomed.api.rest.domain;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedConceptCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;

/**
 * @since 1.0
 */
public class SnomedConceptRestInput extends AbstractSnomedComponentRestInput<SnomedConceptCreateRequestBuilder> {

	private List<SnomedDescriptionRestInput> descriptions = Collections.emptyList();
	private String isAId;
	private String parentId;

	/**
	 * @return
	 */
	public List<SnomedDescriptionRestInput> getDescriptions() {
		return descriptions;
	}

	/**
	 * @return
	 */
	public String getIsAId() {
		return isAId;
	}

	/**
	 * @return
	 */
	public String getParentId() {
		return parentId;
	}

	public void setDescriptions(List<SnomedDescriptionRestInput> descriptions) {
		this.descriptions = descriptions;
	}

	public void setIsAId(final String isAId) {
		this.isAId = isAId;
	}

	public void setParentId(final String parentId) {
		this.parentId = parentId;
	}

	@Override
	protected SnomedConceptCreateRequestBuilder createComponentInput() {
		return SnomedRequests.prepareNewConcept();
	}
	
	@Override
	public SnomedConceptCreateRequestBuilder toComponentInput() {
		final SnomedConceptCreateRequestBuilder req = super.toComponentInput();
		req.setIsAId(createIdGenerationStrategy(getIsAId(), ComponentCategory.RELATIONSHIP));
		for (SnomedDescriptionRestInput restDescription : getDescriptions()) {
			// Propagate namespace from concept if present, and the description does not already have one
			if (null == restDescription.getNamespaceId()) {
				restDescription.setNamespaceId(getNamespaceId());
			}
			
			req.addDescription(restDescription.toComponentInput());
		}
		req.setParent(getParentId());
		return req;
	}

	@Override
	protected ComponentCategory getComponentCategory() {
		return ComponentCategory.CONCEPT;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedConceptRestInput [getId()=");
		builder.append(getId());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getDescriptions()=");
		builder.append(getDescriptions());
		builder.append(", getIsAId()=");
		builder.append(getIsAId());
		builder.append(", getParentId()=");
		builder.append(getParentId());
		builder.append("]");
		return builder.toString();
	}
}
