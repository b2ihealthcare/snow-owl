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
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public final class SnomedConcreteDomainPredicate extends SnomedPredicate {

	public static final String PROP_DATA_TYPE = "dataType";
	public static final String PROP_NAME = "name";
	public static final String PROP_LABEL = "label";
	public static final String PROP_CHARACTERISTIC_TYPE_ID = "characteristicTypeId";

	private String label; // "Vaccine"
	private String name; // "canBeTaggedWithVaccine"
	private DataType dataType;
	private String characteristicTypeId;

	public String getLabel() {
		return label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(final DataType dataType) {
		this.dataType = dataType;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public void setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	@Override
	public ConcreteDomainElementPredicate createModel() {
		return MrcmFactory.eINSTANCE.createConcreteDomainElementPredicate();
	}

	@Override
	public ConcreteDomainElementPredicate applyChangesTo(final ConceptModelComponent existingModel) {
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

	@Override
	public SnomedConcreteDomainPredicate deepCopy(final Date date, final String userName) {
		final SnomedConcreteDomainPredicate copy = new SnomedConcreteDomainPredicate();

		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setCharacteristicTypeId(getCharacteristicTypeId());
		copy.setDataType(getDataType());
		copy.setEffectiveTime(date.getTime());
		copy.setId(UUID.randomUUID().toString());
		copy.setLabel(getLabel());
		copy.setName(getName());

		return copy;
	}

	@Override
	public void collectConceptIds(final Collection<String> conceptIds) {
		return;
	}

	@Override
	public String validate() {
		final String parentMessage = super.validate();

		if (parentMessage != null) {
			return parentMessage;
		}

		if (Strings.isNullOrEmpty(getName())) { return String.format("Concrete domain name should be set on %s with UUID %s.", displayName(), getId()); }
		if (getDataType() == null) { return String.format("Concrete domain type should be specified for %s with UUID %s.", displayName(), getId()); }

		return null;
	}

	@Override
	public int structuralHashCode() {
		return 31 * super.hashCode() + Objects.hash(characteristicTypeId, dataType, label, name);
	}

	@Override
	public boolean structurallyEquals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.structurallyEquals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedConcreteDomainPredicate other = (SnomedConcreteDomainPredicate) obj;

		if (!Objects.equals(characteristicTypeId, other.characteristicTypeId)) { return false; }
		if (dataType != other.dataType) { return false; }
		if (!Objects.equals(label, other.label)) { return false; }
		if (!Objects.equals(name, other.name)) { return false; }
		return true;
	}
}
