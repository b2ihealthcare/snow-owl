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
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * @since 6.5
 */
public final class SnomedConcreteDomainPredicate extends SnomedPredicate {

	private String label; // "Vaccine"
	private String name; // "canBeTaggedWithVaccine"
	private DataType dataType;
	private String characteristicTypeId;

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public DataType getDataType() {
		return dataType;
	}
	
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}
	
	public void setCharacteristicTypeId(String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}
	
	@Override
	public ConcreteDomainElementPredicate createModel() {
		return MrcmFactory.eINSTANCE.createConcreteDomainElementPredicate();
	}
	
	@Override
	public ConcreteDomainElementPredicate applyChangesTo(ConceptModelComponent existingModel) {
		final ConcreteDomainElementPredicate updateModel = (existingModel instanceof ConcreteDomainElementPredicate)
				? (ConcreteDomainElementPredicate) existingModel
				: createModel();
		
		updateModel.setActive(isActive());
		updateModel.setAuthor(getAuthor());
		updateModel.setCharacteristicTypeConceptId(getCharacteristicTypeId());
		updateModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updateModel.setLabel(getLabel());
		updateModel.setName(getName());
		updateModel.setType(getDataType());
		updateModel.setUuid(getId());
		
		return updateModel;
	}
}
