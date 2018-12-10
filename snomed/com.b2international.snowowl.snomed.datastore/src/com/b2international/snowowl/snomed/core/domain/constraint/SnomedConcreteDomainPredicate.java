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

import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.datastore.index.constraint.ConcreteDomainPredicateFragment;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public final class SnomedConcreteDomainPredicate extends SnomedPredicate {

	public static final String PROP_ATTRIBUTE = "attribute";
	public static final String PROP_RANGE = "range";
	public static final String PROP_CHARACTERISTIC_TYPE_ID = "characteristicTypeId";

	private SnomedConceptSetDefinition attribute;
	private DataType range;
	private String characteristicTypeId;

	public SnomedConceptSetDefinition getAttribute() {
		return attribute;
	}
	
	public void setAttribute(final SnomedConceptSetDefinition attribute) {
		this.attribute = attribute;
	}
	
	public DataType getRange() {
		return range;
	}

	public void setRange(final DataType range) {
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

	@Override
	public ConcreteDomainPredicateFragment createModel() {
		return new ConcreteDomainPredicateFragment(getId(), isActive(), getEffectiveTime(), getAuthor(), getAttribute().createModel(), getRange(), getCharacteristicTypeId());
	}
	
	@Override
	public SnomedConcreteDomainPredicate deepCopy(final Date date, final String userName) {
		final SnomedConcreteDomainPredicate copy = new SnomedConcreteDomainPredicate();

		copy.setActive(isActive());
		copy.setAttribute(getAttribute());
		copy.setAuthor(userName);
		copy.setCharacteristicTypeId(getCharacteristicTypeId());
		copy.setRange(getRange());
		copy.setEffectiveTime(date.getTime());
		copy.setId(UUID.randomUUID().toString());

		return copy;
	}

	@Override
	public void collectConceptIds(final Collection<String> conceptIds) {
		if (getAttribute() != null) { getAttribute().collectConceptIds(conceptIds); }
		if (!Strings.isNullOrEmpty(getCharacteristicTypeId())) { conceptIds.add(getCharacteristicTypeId()); }
	}

	@Override
	public String validate() {
		final String parentMessage = super.validate();

		if (parentMessage != null) {
			return parentMessage;
		}

		if (getAttribute() == null) { return String.format("Concrete domain attribute should be set on %s with UUID %s.", displayName(), getId()); }
		if (getRange() == null) { return String.format("Concrete domain range should be specified for %s with UUID %s.", displayName(), getId()); }

		return null;
	}

	@Override
	public int structuralHashCode() {
		return 31 * super.structuralHashCode() + structuralHashCode(characteristicTypeId, range, attribute);
	}

	@Override
	public boolean structurallyEquals(final SnomedConceptModelComponent obj) {
		if (this == obj) { return true; }
		if (!super.structurallyEquals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedConcreteDomainPredicate other = (SnomedConcreteDomainPredicate) obj;

		if (!Objects.equals(characteristicTypeId, other.characteristicTypeId)) { return false; }
		if (range != other.range) { return false; }
		if (!Objects.equals(attribute, other.attribute)) { return false; }
		return true;
	}
}
