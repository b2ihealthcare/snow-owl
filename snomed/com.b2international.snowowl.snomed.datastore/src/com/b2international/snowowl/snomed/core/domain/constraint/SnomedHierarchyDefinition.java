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
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public final class SnomedHierarchyDefinition extends SnomedConceptSetDefinition {

	public static final String PROP_CONCEPT_ID = "conceptId";
	public static final String PROP_INCLUSION_TYPE = "inclusionType";

	private String conceptId;
	private HierarchyInclusionType inclusionType;

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(final String conceptId) {
		this.conceptId = conceptId;
	}

	public HierarchyInclusionType getInclusionType() {
		return inclusionType;
	}

	public void setInclusionType(final HierarchyInclusionType inclusionType) {
		this.inclusionType = inclusionType;
	}

	@Override
	public String toEcl() {
		switch (inclusionType) {
		case SELF: 
			return conceptId;
		case DESCENDANT: 
			return String.format("<%s", conceptId); 
		case SELF_OR_DESCENDANT: 
			return String.format("<<%s", conceptId); 
		default: 
			throw new IllegalArgumentException("Unknown inclusion type: " + inclusionType);
		}
	}

	@Override
	public HierarchyConceptSetDefinition createModel() {
		return MrcmFactory.eINSTANCE.createHierarchyConceptSetDefinition();
	}

	@Override
	public HierarchyConceptSetDefinition applyChangesTo(final ConceptModelComponent existingModel) {
		final HierarchyConceptSetDefinition updatedModel = (existingModel instanceof HierarchyConceptSetDefinition)
				? (HierarchyConceptSetDefinition) existingModel
				: createModel();

		updatedModel.setActive(isActive());
		updatedModel.setAuthor(getAuthor());
		updatedModel.setConceptId(getConceptId());
		updatedModel.setInclusionType(getInclusionType());
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setUuid(getId());

		return updatedModel;
	}

	@Override
	public SnomedHierarchyDefinition deepCopy(final Date date, final String userName) {
		final SnomedHierarchyDefinition copy = new SnomedHierarchyDefinition();

		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setConceptId(getConceptId());
		copy.setEffectiveTime(date.getTime());
		copy.setId(UUID.randomUUID().toString());
		copy.setInclusionType(getInclusionType());

		return copy;
	}

	@Override
	public void collectConceptIds(final Collection<String> conceptIds) {
		if (!Strings.isNullOrEmpty(getConceptId())) { conceptIds.add(getConceptId()); }
	}

	@Override
	public String validate() {
		final String parentMessage = super.validate();

		if (parentMessage != null) {
			return parentMessage;
		}

		if (Strings.isNullOrEmpty(getConceptId())) { return String.format("Concept ID should be specified for %s with UUID %s.", displayName(), getId()); }
		if (inclusionType == null) { return String.format("Inclusion type should be set for %s with UUID %s.", displayName(), getId()); }

		return null;
	}

	@Override
	public int structuralHashCode() {
		return 31 * super.hashCode() + Objects.hash(conceptId, inclusionType);
	}

	@Override
	public boolean structurallyEquals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedHierarchyDefinition other = (SnomedHierarchyDefinition) obj;

		if (Objects.equals(conceptId, other.conceptId)) { return false; }
		if (inclusionType != other.inclusionType) { return false; }
		return true;
	}
}
