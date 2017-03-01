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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.ReservingIdStrategy;

/**
 * <i>Builder</i> class to build requests responsible for creating SNOMED CT concepts.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * 
 * @since 4.5
 */
public final class SnomedConceptCreateRequestBuilder extends SnomedComponentCreateRequestBuilder<SnomedConceptCreateRequestBuilder> {

	private DefinitionStatus definitionStatus = DefinitionStatus.PRIMITIVE;
	private String parentId;
	private IdGenerationStrategy isAIdGenerationStrategy;
	private List<SnomedDescriptionCreateRequest> descriptions = newArrayList();
	
	SnomedConceptCreateRequestBuilder(String repositoryId) {
		super(repositoryId, ComponentCategory.CONCEPT);
	}

	public SnomedConceptCreateRequestBuilder setParent(String parentId) {
		this.parentId = parentId;
		return getSelf();
	}
	
	public SnomedConceptCreateRequestBuilder setIsAId(IdGenerationStrategy idGenerationStrategy) {
		this.isAIdGenerationStrategy = idGenerationStrategy;
		return getSelf();
	}
	
	public SnomedConceptCreateRequestBuilder setDefinitionStatus(DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
		return getSelf();
	}
	
	public SnomedConceptCreateRequestBuilder addDescription(SnomedDescriptionCreateRequest description) {
		this.descriptions.add(description);
		return getSelf();
	}
	
	public SnomedConceptCreateRequestBuilder addDescription(SnomedDescriptionCreateRequestBuilder description) {
		return addDescription((SnomedDescriptionCreateRequest) description.build());
	}
	
	@Override
	protected BaseSnomedComponentCreateRequest createRequest() {
		return new SnomedConceptCreateRequest();
	}

	@Override
	protected void init(BaseSnomedComponentCreateRequest request) {
		final SnomedConceptCreateRequest req = (SnomedConceptCreateRequest) request;
		req.setDefinitionStatus(definitionStatus);
		// TODO use default namespace???
		req.setIsAIdGenerationStrategy(isAIdGenerationStrategy == null ? new ReservingIdStrategy(ComponentCategory.RELATIONSHIP) : isAIdGenerationStrategy);
		req.setDescriptions(descriptions);
		req.setParentId(parentId);
	}

}
