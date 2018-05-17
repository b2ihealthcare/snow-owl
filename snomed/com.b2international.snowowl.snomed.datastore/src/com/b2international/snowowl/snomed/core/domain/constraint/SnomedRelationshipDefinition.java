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
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public final class SnomedRelationshipDefinition extends SnomedConceptSetDefinition {

	public static final String PROP_TYPE_ID = "typeId";
	public static final String PROP_DESTINATION_ID = "typeId";

	private String typeId;
	private String destinationId;

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	@Override
	public String toEcl() {
		// Attribute refinement; any descendant of the SNOMED CT root concept is applicable 
		return String.format("<<%s:%s=%s", Concepts.ROOT_CONCEPT, typeId, destinationId);
	}

	@Override
	public RelationshipConceptSetDefinition createModel() {
		return MrcmFactory.eINSTANCE.createRelationshipConceptSetDefinition();
	}

	@Override
	public RelationshipConceptSetDefinition applyChangesTo(final ConceptModelComponent existingModel) {
		final RelationshipConceptSetDefinition updatedModel = (existingModel instanceof RelationshipConceptSetDefinition)
				? (RelationshipConceptSetDefinition) existingModel
				: createModel();

		updatedModel.setActive(isActive());
		updatedModel.setAuthor(getAuthor());
		updatedModel.setTypeConceptId(getTypeId());
		updatedModel.setDestinationConceptId(getDestinationId());
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setUuid(getId());

		return updatedModel;
	}

	@Override
	public SnomedRelationshipDefinition deepCopy(final Date date, final String userName) {
		final SnomedRelationshipDefinition copy = new SnomedRelationshipDefinition();

		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setDestinationId(getDestinationId());
		copy.setEffectiveTime(date.getTime());
		copy.setId(UUID.randomUUID().toString());
		copy.setTypeId(getTypeId());

		return copy;
	}

	@Override
	public void collectConceptIds(final Collection<String> conceptIds) {
		if (!Strings.isNullOrEmpty(getTypeId())) { conceptIds.add(getTypeId()); }
		if (!Strings.isNullOrEmpty(getDestinationId())) {conceptIds.add(getDestinationId()); }
	}

	@Override
	public String validate() {
		final String parentMessage = super.validate();

		if (parentMessage != null) {
			return parentMessage;
		}

		if (Strings.isNullOrEmpty(getTypeId())) { return String.format("Relationship type ID should be specified for %s with UUID %s.", displayName(), getId()); }
		if (Strings.isNullOrEmpty(getDestinationId())) { return String.format("Relationship destination ID should be specified for %s with UUID %s.", displayName(), getId()); }

		return null;
	}

	@Override
	public int structuralHashCode() {
		return 31 * super.hashCode() + Objects.hash(destinationId, typeId);
	}

	@Override
	public boolean structurallyEquals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedRelationshipDefinition other = (SnomedRelationshipDefinition) obj;
		if (!Objects.equals(destinationId, other.destinationId)) { return false; }
		if (!Objects.equals(typeId, other.typeId)) { return false; }
		return true;
	}
}
