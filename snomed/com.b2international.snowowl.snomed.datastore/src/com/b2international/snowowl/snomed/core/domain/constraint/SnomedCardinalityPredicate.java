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
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;

/**
 * @since 6.5
 */
public final class SnomedCardinalityPredicate extends SnomedPredicate {

	private int minCardinality;
	private int maxCardinality;
	private GroupRule groupRule;
	private SnomedPredicate predicate;

	public int getMinCardinality() {
		return minCardinality;
	}
	
	public void setMinCardinality(int minCardinality) {
		this.minCardinality = minCardinality;
	}

	public int getMaxCardinality() {
		return maxCardinality;
	}
	
	public void setMaxCardinality(int maxCardinality) {
		this.maxCardinality = maxCardinality;
	}

	public GroupRule getGroupRule() {
		return groupRule;
	}
	
	public void setGroupRule(GroupRule groupRule) {
		this.groupRule = groupRule;
	}

	public SnomedPredicate getPredicate() {
		return predicate;
	}
	
	public void setPredicate(SnomedPredicate predicate) {
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
}
