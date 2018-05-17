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

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public final class SnomedRelationshipPredicate extends SnomedPredicate {

	public static final String PROP_CHARACTERISTIC_TYPE_ID = "characteristicTypeId";

	private SnomedConceptSetDefinition attribute;
	private SnomedConceptSetDefinition range;
	private String characteristicTypeId;

	public SnomedConceptSetDefinition getAttribute() {
		return attribute;
	}

	public void setAttribute(final SnomedConceptSetDefinition attribute) {
		this.attribute = attribute;
	}

	public SnomedConceptSetDefinition getRange() {
		return range;
	}

	public void setRange(final SnomedConceptSetDefinition range) {
		this.range = range;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public void setCharacteristicTypeId(final String characteristicTypeId) {
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
	public RelationshipPredicate applyChangesTo(final ConceptModelComponent existingModel) {
		final RelationshipPredicate updatedModel = (existingModel instanceof RelationshipPredicate)
				? (RelationshipPredicate) existingModel
				: createModel();

		updatedModel.setActive(isActive());
		updatedModel.setAttribute(getAttribute().applyChangesTo(updatedModel.getAttribute()));
		updatedModel.setAuthor(getAuthor());
		updatedModel.setCharacteristicTypeConceptId(getCharacteristicTypeId());
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setRange(getRange().applyChangesTo(updatedModel.getRange()));
		updatedModel.setUuid(getId());

		return updatedModel;
	}

	@Override
	public SnomedRelationshipPredicate deepCopy(final Date date, final String userName) {
		final SnomedRelationshipPredicate copy = new SnomedRelationshipPredicate();

		copy.setActive(isActive());
		if (getAttribute() != null) { copy.setAttribute(getAttribute().deepCopy(date, userName)); }
		copy.setAuthor(userName);
		copy.setCharacteristicTypeId(getCharacteristicTypeId());
		copy.setEffectiveTime(date.getTime());
		copy.setId(UUID.randomUUID().toString());
		if (getRange() != null) { copy.setRange(getRange().deepCopy(date, userName)); }

		return copy;
	}

	@Override
	public void collectConceptIds(final Collection<String> conceptIds) {
		if (getAttribute() != null) { getAttribute().collectConceptIds(conceptIds); }
		if (getRange() != null) { getRange().collectConceptIds(conceptIds); }
		if (!Strings.isNullOrEmpty(getCharacteristicTypeId())) { conceptIds.add(getCharacteristicTypeId()); }
	}

	@Override
	public String validate() {
		final String parentMessage = super.validate();

		if (parentMessage != null) {
			return parentMessage;
		}

		if (getAttribute() == null) { return String.format("Relationship attribute definition should be specified for %s with UUID %s.", displayName(), getId()); }
		if (getRange() == null) { return String.format("Relationship range should be specified for %s with UUID %s.", displayName(), getId()); }

		final String attributeMessage = getAttribute().validate();
		if (attributeMessage != null) { return attributeMessage; }
		final String rangeMessage = getRange().validate();
		if (rangeMessage != null) { return rangeMessage; }

		return null;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(attribute, characteristicTypeId, range);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedRelationshipPredicate other = (SnomedRelationshipPredicate) obj;

		if (!Objects.equals(attribute, other.attribute)) { return false; }
		if (!Objects.equals(characteristicTypeId, other.characteristicTypeId)) { return false; }
		if (!Objects.equals(range, other.range)) { return false; }
		return true;
	}
}
