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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.DependencyOperator;
import com.b2international.snowowl.snomed.mrcm.DependencyPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.google.common.collect.Maps;

/**
 * @since 6.5
 */
public final class SnomedDependencyPredicate extends SnomedPredicate {

	private GroupRule groupRule;
	private DependencyOperator dependencyOperator;
	private Set<SnomedPredicate> children = newHashSet();

	public GroupRule getGroupRule() {
		return groupRule;
	}
	
	public void setGroupRule(GroupRule groupRule) {
		this.groupRule = groupRule;
	}

	public DependencyOperator getDependencyOperator() {
		return dependencyOperator;
	}
	
	public void setDependencyOperator(DependencyOperator dependencyOperator) {
		this.dependencyOperator = dependencyOperator;
	}

	public Set<SnomedPredicate> getChildren() {
		return children;
	}
	
	public void setChildren(Set<SnomedPredicate> children) {
		this.children = children;
	}
	
	@Override
	public DependencyPredicate createModel() {
		return MrcmFactory.eINSTANCE.createDependencyPredicate();
	}
	
	@Override
	public DependencyPredicate applyChangesTo(ConceptModelComponent existingModel) {
		final DependencyPredicate updatedModel = (existingModel instanceof DependencyPredicate)
				? (DependencyPredicate) existingModel
				: createModel();
		
		updatedModel.setActive(isActive());
		updatedModel.setAuthor(getAuthor());

		/* 
		 * We will update this list in place; on an existing instance, it will be already populated by some predicates,
		 * on a new instance, it is completely empty.
		 */
		final List<ConceptModelPredicate> updatedModelChildren = updatedModel.getChildren();
		
		// Index predicate keys by list position
		final Map<String, Integer> existingPredicatesByIdx = newHashMap();
		for (int i = 0; i < updatedModelChildren.size(); i++) {
			final ConceptModelPredicate existingPredicate = updatedModelChildren.get(i);
			existingPredicatesByIdx.put(existingPredicate.getUuid(), i);
		}
		
		// Index new predicates by key
		final Map<String, SnomedPredicate> updatedPredicates = newHashMap(Maps.uniqueIndex(children, SnomedPredicate::getId));
		
		// Iterate backwards over the list so that removals don't mess up the the list index map
		for (int j = updatedModelChildren.size() - 1; j >= 0; j--) {
			final ConceptModelPredicate existingPredicate = updatedModelChildren.get(j);
			final String uuid = existingPredicate.getUuid();
			
			// Consume entries from "updatedPredicates" by using remove(Object key)
			final SnomedPredicate updatedPredicate = updatedPredicates.remove(uuid);
			
			// Was there a child with the same key? If not, remove the original from the list, if it is still there, update in place
			if (updatedPredicate == null) {
				updatedModelChildren.remove(j);
			} else {
				updatedModelChildren.set(j, updatedPredicate.applyChangesTo(existingPredicate));
			}
		}
		
		// Remaining entries in "updatedPredicates" are new; add them to the end of the list
		for (SnomedPredicate newChild : updatedPredicates.values()) {
			updatedModelChildren.add(newChild.applyChangesTo(newChild.createModel()));
		}
		
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setGroupRule(getGroupRule());
		updatedModel.setOperator(getDependencyOperator());
		updatedModel.setUuid(getId());
		
		return updatedModel;
	}
	
	@Override
	public SnomedDependencyPredicate deepCopy(Date date, String userName) {
		final SnomedDependencyPredicate copy = new SnomedDependencyPredicate();
		
		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setChildren(getChildren().stream()
				.map(p -> p.deepCopy(date, userName))
				.collect(Collectors.toSet()));
		copy.setDependencyOperator(getDependencyOperator());
		copy.setEffectiveTime(date.getTime());
		copy.setGroupRule(getGroupRule());
		copy.setId(UUID.randomUUID().toString());
		
		return copy;
	}
	
	@Override
	public void collectConceptIds(Collection<String> conceptIds) {
		children.forEach(p -> p.collectConceptIds(conceptIds));
	}
}
