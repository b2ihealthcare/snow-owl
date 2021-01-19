/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.mrcm.SingletonConceptSetDefinition;
import com.google.common.base.Strings;

/**
 * @since 6.21.0
 */
public final class SnomedSingletonDefinition extends SnomedConceptSetDefinition {

	public static final String PROP_CONCEPT_ID = "conceptId";

	private String conceptId;

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(final String conceptId) {
		this.conceptId = conceptId;
	}

	@Override
	public String toEcl() {
		return conceptId;
	}

	@Override
	public SingletonConceptSetDefinition createModel() {
		return MrcmFactory.eINSTANCE.createSingletonConceptSetDefinition();
	}

	@Override
	public SingletonConceptSetDefinition applyChangesTo(final ConceptModelComponent existingModel) {
		final SingletonConceptSetDefinition updatedModel = (existingModel instanceof SingletonConceptSetDefinition)
				? (SingletonConceptSetDefinition) existingModel
				: createModel();

		updatedModel.setActive(isActive());
		updatedModel.setAuthor(getAuthor());
		updatedModel.setConceptId(getConceptId());
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setUuid(getId());

		return updatedModel;
	}

	@Override
	public SnomedSingletonDefinition deepCopy(final Date date, final String userName) {
		final SnomedSingletonDefinition copy = new SnomedSingletonDefinition();
		
		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setConceptId(getConceptId());
		copy.setEffectiveTime(date.getTime());
		copy.setId(UUID.randomUUID().toString());
		
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
		
		return null;
	}
	
	@Override
	public int structuralHashCode() {
		return 31 * super.structuralHashCode() + structuralHashCode(conceptId);
	}
	
	@Override
	public boolean structurallyEquals(final SnomedConceptModelComponent obj) {
		if (this == obj) { return true; }
		if (!super.structurallyEquals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		
		final SnomedSingletonDefinition other = (SnomedSingletonDefinition) obj;
		
		if (!Objects.equals(conceptId, other.conceptId)) { return false; }
		return true;
	}
}
