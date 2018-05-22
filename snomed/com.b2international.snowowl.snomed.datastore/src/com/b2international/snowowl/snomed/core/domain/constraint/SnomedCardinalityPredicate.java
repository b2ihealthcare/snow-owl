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
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;

/**
 * @since 6.5
 */
public final class SnomedCardinalityPredicate extends SnomedPredicate {

	public static final String PROP_GROUP_RULE = "groupRule";
	public static final String PROP_MIN_CARDINALITY = "minCardinality";
	public static final String PROP_MAX_CARDINALITY = "maxCardinality";

	private int minCardinality;
	private int maxCardinality;
	private GroupRule groupRule;
	private SnomedPredicate predicate;

	public int getMinCardinality() {
		return minCardinality;
	}

	public void setMinCardinality(final int minCardinality) {
		this.minCardinality = minCardinality;
	}

	public int getMaxCardinality() {
		return maxCardinality;
	}

	public void setMaxCardinality(final int maxCardinality) {
		this.maxCardinality = maxCardinality;
	}

	public GroupRule getGroupRule() {
		return groupRule;
	}

	public void setGroupRule(final GroupRule groupRule) {
		this.groupRule = groupRule;
	}

	public SnomedPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(final SnomedPredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public CardinalityPredicate createModel() {
		return MrcmFactory.eINSTANCE.createCardinalityPredicate();
	}

	@Override
	public CardinalityPredicate applyChangesTo(final ConceptModelComponent existingModel) {
		final CardinalityPredicate updatedModel = (existingModel instanceof CardinalityPredicate)
				? (CardinalityPredicate) existingModel
				: createModel();

		updatedModel.setActive(isActive());
		updatedModel.setAuthor(getAuthor());
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setGroupRule(getGroupRule());
		updatedModel.setMaxCardinality(getMaxCardinality());
		updatedModel.setMinCardinality(getMinCardinality());
		updatedModel.setPredicate(getPredicate().applyChangesTo(updatedModel.getPredicate()));
		updatedModel.setUuid(getId());

		return updatedModel;
	}

	@Override
	public SnomedCardinalityPredicate deepCopy(final Date date, final String userName) {
		final SnomedCardinalityPredicate copy = new SnomedCardinalityPredicate();

		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setEffectiveTime(date.getTime());
		copy.setGroupRule(getGroupRule());
		copy.setId(UUID.randomUUID().toString());
		copy.setMaxCardinality(getMaxCardinality());
		copy.setMinCardinality(getMinCardinality());
		if (getPredicate() != null) { copy.setPredicate(getPredicate().deepCopy(date, userName)); }

		return copy;
	}

	@Override
	public void collectConceptIds(final Collection<String> conceptIds) {
		if (getPredicate() != null) { getPredicate().collectConceptIds(conceptIds); }
	}

	@Override
	public String validate() {
		final String parentMessage = super.validate();

		if (parentMessage != null) {
			return parentMessage;
		}

		if (getGroupRule() == null) { return String.format("Group rule should be set on %s with UUID %s.", displayName(), getId()); }
		if (getPredicate() == null) { return String.format("A predicate should be specified for %s with UUID %s.", displayName(), getId()); }

		final String predicateMessage = getPredicate().validate();
		if (predicateMessage != null) { return predicateMessage; }

		return null;
	}

	@Override
	public int structuralHashCode() {
		return 31 * super.structuralHashCode() + structuralHashCode(groupRule, maxCardinality, minCardinality, predicate);
	}

	@Override
	public boolean structurallyEquals(final SnomedConceptModelComponent obj) {
		if (this == obj) { return true; }
		if (!super.structurallyEquals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedCardinalityPredicate other = (SnomedCardinalityPredicate) obj;

		if (groupRule != other.groupRule) { return false; }
		if (maxCardinality != other.maxCardinality) { return false; }
		if (minCardinality != other.minCardinality) { return false; }
		if (!structurallyEquals(predicate, other.predicate)) { return false; }
		return true;
	}
}
