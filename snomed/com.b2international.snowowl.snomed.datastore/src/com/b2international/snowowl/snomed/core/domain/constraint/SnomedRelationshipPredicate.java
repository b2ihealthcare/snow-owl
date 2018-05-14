/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.constraint;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;

/**
 * @since 6.5
 */
public final class SnomedRelationshipPredicate extends SnomedPredicate {

	private SnomedConceptSetDefinition attribute;
	private SnomedConceptSetDefinition range;
	private String characteristicTypeId;

	public SnomedConceptSetDefinition getAttribute() {
		return attribute;
	}
	
	public void setAttribute(SnomedConceptSetDefinition attribute) {
		this.attribute = attribute;
	}

	public SnomedConceptSetDefinition getRange() {
		return range;
	}
	
	public void setRange(SnomedConceptSetDefinition range) {
		this.range = range;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}
	
	public void setCharacteristicTypeId(String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	public String getAttributeExpression() {
		return attribute.toEcl();
	}

	public String getRangeExpression() {
		return range.toEcl();
	}
	
	@Override
	public RelationshipPredicate createModel() {
		return MrcmFactory.eINSTANCE.createRelationshipPredicate();
	}
	
	@Override
	public RelationshipPredicate applyChangesTo(ConceptModelComponent existingModel) {
		final RelationshipPredicate updatedModel = (existingModel instanceof RelationshipPredicate)
				? (RelationshipPredicate) existingModel
				: createModel();
		
		updatedModel.setActive(isActive());
		updatedModel.setAttribute(getAttribute().applyChangesTo(updatedModel.getAttribute()));
		updatedModel.setAuthor(getAuthor());
		updatedModel.setCharacteristicTypeConceptId(getCharacteristicTypeId());
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setRange(getRange().applyChangesTo(updatedModel.getAttribute()));
		updatedModel.setUuid(getId());
		
		return updatedModel;
	}
}
