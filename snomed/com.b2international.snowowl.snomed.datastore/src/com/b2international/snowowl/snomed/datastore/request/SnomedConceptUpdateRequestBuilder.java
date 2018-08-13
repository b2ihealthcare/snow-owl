/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
public final class SnomedConceptUpdateRequestBuilder extends BaseSnomedComponentUpdateRequestBuilder<SnomedConceptUpdateRequestBuilder, SnomedConceptUpdateRequest> {

	private DefinitionStatus definitionStatus;
	private SubclassDefinitionStatus subclassDefinitionStatus;
	private Multimap<AssociationType, String> associationTargets;
	private InactivationIndicator inactivationIndicator;
	private List<SnomedDescription> descriptions;
	private List<SnomedRelationship> relationships;
	private List<SnomedReferenceSetMember> members;
	private SnomedReferenceSet refSet;

	SnomedConceptUpdateRequestBuilder(String componentId) {
		super(componentId);
	}
	
	@Override
	protected SnomedConceptUpdateRequest create(String componentId) {
		return new SnomedConceptUpdateRequest(componentId);
	}
	
	public SnomedConceptUpdateRequestBuilder setAssociationTargets(Multimap<AssociationType, String> associationTargets) {
		this.associationTargets = associationTargets;
		return getSelf();
	}
	
	public SnomedConceptUpdateRequestBuilder setDefinitionStatus(DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
		return getSelf();
	}
	
	public SnomedConceptUpdateRequestBuilder setInactivationIndicator(InactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
		return getSelf();
	}
	
	public SnomedConceptUpdateRequestBuilder setSubclassDefinitionStatus(SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
		return getSelf();
	}
	
	public SnomedConceptUpdateRequestBuilder setDescriptions(Iterable<? extends SnomedDescription> descriptions) {
		this.descriptions = descriptions != null ? ImmutableList.copyOf(descriptions) : null;
		return getSelf();
	}
	
	public SnomedConceptUpdateRequestBuilder setRelationships(Iterable<? extends SnomedRelationship> relationships) {
		this.relationships = relationships != null ? ImmutableList.copyOf(relationships) : null;
		return getSelf();
	}
	
	public SnomedConceptUpdateRequestBuilder setMembers(Iterable<? extends SnomedReferenceSetMember> members) {
		this.members = members != null ? ImmutableList.copyOf(members) : null;
		return getSelf();
	}
	
	public SnomedConceptUpdateRequestBuilder clearRefSet(boolean force) {
		this.refSet = force ? SnomedReferenceSet.FORCE_DELETE : SnomedReferenceSet.DELETE;
		return getSelf();
	}
	
	@Override
	protected void init(SnomedConceptUpdateRequest req) {
		super.init(req);
		req.setDefinitionStatus(definitionStatus);
		req.setSubclassDefinitionStatus(subclassDefinitionStatus);
		req.setAssociationTargets(associationTargets);
		req.setInactivationIndicator(inactivationIndicator);
		req.setDescriptions(descriptions);
		req.setRelationships(relationships);
		req.setMembers(members);
		req.setRefSet(refSet);
	}
	
}
