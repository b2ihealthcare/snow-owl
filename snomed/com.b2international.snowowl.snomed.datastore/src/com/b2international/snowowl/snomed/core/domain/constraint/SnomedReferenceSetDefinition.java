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
import java.util.UUID;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public final class SnomedReferenceSetDefinition extends SnomedConceptSetDefinition {

	public static final String PROP_REFSET_ID = "refSetId";
	
	private String refSetId;

	public String getRefSetId() {
		return refSetId;
	}
	
	public void setRefSetId(String refSetId) {
		this.refSetId = refSetId;
	}
	
	@Override
	public String toEcl() {
		return String.format("^%s", refSetId);
	}
	
	@Override
	public ReferenceSetConceptSetDefinition createModel() {
		return MrcmFactory.eINSTANCE.createReferenceSetConceptSetDefinition();
	}
	
	@Override
	public ReferenceSetConceptSetDefinition applyChangesTo(ConceptModelComponent existingModel) {
		final ReferenceSetConceptSetDefinition updatedModel = (existingModel instanceof ReferenceSetConceptSetDefinition)
				? (ReferenceSetConceptSetDefinition) existingModel
				: createModel();
				
		updatedModel.setActive(isActive());
		updatedModel.setAuthor(getAuthor());
		updatedModel.setRefSetIdentifierConceptId(getRefSetId());
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setUuid(getId());
		
		return updatedModel;
	}
	
	@Override
	public SnomedReferenceSetDefinition deepCopy(Date date, String userName) {
		final SnomedReferenceSetDefinition copy = new SnomedReferenceSetDefinition();
		
		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setEffectiveTime(date.getTime());
		copy.setId(UUID.randomUUID().toString());
		copy.setRefSetId(getRefSetId());

		return copy;
	}
	
	@Override
	public void collectConceptIds(Collection<String> conceptIds) {
		if (!Strings.isNullOrEmpty(getRefSetId())) { conceptIds.add(getRefSetId()); }
	}
}
