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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;

/**
 * <i>Builder</i> class to build requests responsible for creating SNOMED CT concepts.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * 
 * @since 4.5
 */
public final class SnomedConceptCreateRequestBuilder extends SnomedComponentCreateRequestBuilder<SnomedConceptCreateRequestBuilder> {

	private DefinitionStatus definitionStatus = DefinitionStatus.PRIMITIVE;
	private List<SnomedDescriptionCreateRequest> descriptions = newArrayList();
	private List<SnomedRelationshipCreateRequest> relationships = newArrayList();
	private SnomedRefSetCreateRequest refSet;
	private SubclassDefinitionStatus subclassDefinitionStatus = SubclassDefinitionStatus.NON_DISJOINT_SUBCLASSES;
	
	SnomedConceptCreateRequestBuilder() { 
		super();
	}
	
	// Relationship List builders
	
	public SnomedConceptCreateRequestBuilder addParent(String parentId) {
		return addRelationship(SnomedRequests.prepareNewRelationship()
				.setIdFromNamespace(getIdGenerationStrategy().getNamespace())
				.setDestinationId(parentId)
				.setTypeId(Concepts.IS_A));
	}
	
	public SnomedConceptCreateRequestBuilder addRelationship(SnomedRelationshipCreateRequestBuilder relationship) {
		return addRelationship((SnomedRelationshipCreateRequest) relationship.build());
	}
	
	public SnomedConceptCreateRequestBuilder addRelationship(SnomedRelationshipCreateRequest relationship) {
		this.relationships.add(relationship);
		return getSelf();
	}
	
	public SnomedConceptCreateRequestBuilder addRelationship(SnomedRelationship relationship) {
		return addRelationship((SnomedRelationshipCreateRequest) relationship.toCreateRequest());
	}
	
	public SnomedConceptCreateRequestBuilder addRelationships(Iterable<? extends SnomedRelationship> relationships) {
		relationships.forEach(this::addRelationship);
		return getSelf();
	}
	
	// Description List builders
	
	public SnomedConceptCreateRequestBuilder addDescription(SnomedDescriptionCreateRequestBuilder description) {
		return addDescription((SnomedDescriptionCreateRequest) description.build());
	}
	
	public SnomedConceptCreateRequestBuilder addDescription(SnomedDescriptionCreateRequest description) {
		this.descriptions.add(description);
		return getSelf();
	}
	
	public SnomedConceptCreateRequestBuilder addDescription(SnomedDescription description) {
		return addDescription((SnomedDescriptionCreateRequest) description.toCreateRequest());
	}
	
	public SnomedConceptCreateRequestBuilder addDescriptions(Iterable<? extends SnomedDescription> descriptions) {
		descriptions.forEach(this::addDescription);
		return getSelf();
	}
	
	// Concept property builders

	public SnomedConceptCreateRequestBuilder setDefinitionStatus(DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
		return getSelf();
	}
	
	public SnomedConceptCreateRequestBuilder setSubclassDefinitionStatus(SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
		return getSelf();
	}
	
	// Reference set builder
	
	public SnomedConceptCreateRequestBuilder setRefSet(SnomedRefSetCreateRequestBuilder refSet) {
		return setRefSet((SnomedRefSetCreateRequest) refSet.build());
	}
	
	public SnomedConceptCreateRequestBuilder setRefSet(SnomedRefSetCreateRequest refSet) {
		this.refSet = refSet;
		return getSelf();
	}
	
	@Override
	protected BaseSnomedComponentCreateRequest createRequest() {
		return new SnomedConceptCreateRequest();
	}

	@Override
	protected void init(BaseSnomedComponentCreateRequest request) {
		final SnomedConceptCreateRequest req = (SnomedConceptCreateRequest) request;
		req.setDefinitionStatus(definitionStatus);
		req.setSubclassDefinitionStatus(subclassDefinitionStatus);
		req.setDescriptions(descriptions);
		req.setRelationships(relationships);
		req.setRefSet(refSet);
	}

}
