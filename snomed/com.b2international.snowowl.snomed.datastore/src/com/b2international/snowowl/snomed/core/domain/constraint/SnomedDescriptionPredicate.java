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
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public final class SnomedDescriptionPredicate extends SnomedPredicate {

	public static final String PROP_TYPE_ID = "typeId";

	private String typeId;

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	@Override
	public DescriptionPredicate createModel() {
		return MrcmFactory.eINSTANCE.createDescriptionPredicate();
	}

	@Override
	public DescriptionPredicate applyChangesTo(final ConceptModelComponent existingModel) {
		final DescriptionPredicate updatedModel = (existingModel instanceof DescriptionPredicate)
				? (DescriptionPredicate) existingModel
				: createModel();

		updatedModel.setActive(isActive());
		updatedModel.setAuthor(getAuthor());
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setTypeId(getTypeId());
		updatedModel.setUuid(getId());

		return updatedModel;
	}

	@Override
	public SnomedDescriptionPredicate deepCopy(final Date date, final String userName) {
		final SnomedDescriptionPredicate copy = new SnomedDescriptionPredicate();

		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setEffectiveTime(date.getTime());
		copy.setId(UUID.randomUUID().toString());
		copy.setTypeId(getTypeId());

		return copy;
	}

	@Override
	public void collectConceptIds(final Collection<String> conceptIds) {
		if (!Strings.isNullOrEmpty(typeId)) { conceptIds.add(getTypeId()); }
	}

	@Override
	public String validate() {
		final String parentMessage = super.validate();

		if (parentMessage != null) {
			return parentMessage;
		}

		if (Strings.isNullOrEmpty(getTypeId())) { return String.format("Description type ID should be specified for %s with UUID %s.", displayName(), getId()); }

		return null;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(typeId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedDescriptionPredicate other = (SnomedDescriptionPredicate) obj;

		if (!Objects.equals(typeId, other.typeId)) { return false; }
		return true;
	}
}
