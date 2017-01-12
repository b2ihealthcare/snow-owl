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
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;

/**
 * @since 4.5
 */
public final class SnomedConceptCreateRequestBuilder extends SnomedComponentCreateRequestBuilder<SnomedConceptCreateRequestBuilder> {

	private DefinitionStatus definitionStatus = DefinitionStatus.PRIMITIVE;
	private List<SnomedDescriptionCreateRequest> descriptions = newArrayList();
	private List<SnomedRelationshipCreateRequest> relationships = newArrayList();
	private List<SnomedRefSetMemberCreateRequest> members = newArrayList();
	
	SnomedConceptCreateRequestBuilder() {
		super(ComponentCategory.CONCEPT);
	}

	public SnomedConceptCreateRequestBuilder addParent(String parentId) {
		return addRelationship(SnomedRequests.prepareNewRelationship()
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
	
	public SnomedConceptCreateRequestBuilder addDescription(SnomedDescriptionCreateRequestBuilder description) {
		return addDescription((SnomedDescriptionCreateRequest) description.build());
	}
	
	public SnomedConceptCreateRequestBuilder addDescription(SnomedDescriptionCreateRequest description) {
		this.descriptions.add(description);
		return getSelf();
	}
	
	public SnomedConceptCreateRequestBuilder addMember(SnomedRefSetMemberCreateRequestBuilder member) {
		return addMember((SnomedRefSetMemberCreateRequest) member.build());
	}
	
	public SnomedConceptCreateRequestBuilder addMember(SnomedRefSetMemberCreateRequest member) {
		this.members.add(member);
		return getSelf();
	}

	public SnomedConceptCreateRequestBuilder setDefinitionStatus(DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
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
		req.setDescriptions(descriptions);
		req.setRelationships(relationships);
		req.setMembers(members);
	}

}
