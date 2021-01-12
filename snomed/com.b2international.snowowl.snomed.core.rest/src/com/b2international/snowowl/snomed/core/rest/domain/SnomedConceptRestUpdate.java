/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.domain;

import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptUpdateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 4.0
 */
public class SnomedConceptRestUpdate extends AbstractSnomedComponentRestUpdate {

	private String definitionStatusId;
	private SubclassDefinitionStatus subclassDefinitionStatus;
	private SnomedDescriptions descriptions;
	private SnomedRelationships relationships;
	private SnomedReferenceSetMembers members;

	public String getDefinitionStatusId() {
		return definitionStatusId;
	}

	public SubclassDefinitionStatus getSubclassDefinitionStatus() {
		return subclassDefinitionStatus;
	}

	public void setDefinitionStatusId(final String definitionStatusId) {
		this.definitionStatusId = definitionStatusId;
	}

	public void setSubclassDefinitionStatus(final SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
	}

	public SnomedDescriptions getDescriptions() {
		return descriptions;
	}
	
	public SnomedReferenceSetMembers getMembers() {
		return members;
	}
	
	public SnomedRelationships getRelationships() {
		return relationships;
	}
	
	public void setDescriptions(SnomedDescriptions descriptions) {
		this.descriptions = descriptions;
	}
	
	public void setMembers(SnomedReferenceSetMembers members) {
		this.members = members;
	}
	
	public void setRelationships(SnomedRelationships relationships) {
		this.relationships = relationships;
	}

	public SnomedConceptUpdateRequestBuilder toRequestBuilder(String conceptId) {
		return SnomedRequests
				.prepareUpdateConcept(conceptId)
				.setActive(isActive())
				.setEffectiveTime(getEffectiveTime())
				.setModuleId(getModuleId())
				.setDefinitionStatusId(getDefinitionStatusId())
				.setSubclassDefinitionStatus(getSubclassDefinitionStatus())
				.setInactivationProperties(getInactivationProperties())
				.setMembers(getMembers())
				.setRelationships(getRelationships())
				.setDescriptions(getDescriptions());
	}

}