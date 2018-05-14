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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

/**
 * @since 6.5
 */
public final class SnomedCompositeDefinition extends SnomedConceptSetDefinition {

	private Set<SnomedConceptSetDefinition> children;

	public Set<SnomedConceptSetDefinition> getChildren() {
		return children;
	}
	
	public void setChildren(Set<SnomedConceptSetDefinition> children) {
		this.children = children;
	}

	@Override
	public String toEcl() {
		final Set<String> subExpressions = children.stream()
				.map(SnomedConceptSetDefinition::toEcl)
				.collect(Collectors.toSet());

		// Wrap each sub-expression into parentheses
		return "(" + Joiner.on(") OR (").join(subExpressions) + ")";
	}
	
	@Override
	public CompositeConceptSetDefinition createModel() {
		return MrcmFactory.eINSTANCE.createCompositeConceptSetDefinition();
	}
	
	@Override
	public CompositeConceptSetDefinition applyChangesTo(ConceptModelComponent existingModel) {
		final CompositeConceptSetDefinition updatedModel = (existingModel instanceof CompositeConceptSetDefinition)
				? (CompositeConceptSetDefinition) existingModel
				: createModel();
				
		updatedModel.setActive(isActive());
		updatedModel.setAuthor(getAuthor());

		/* 
		 * We will update this list in place; on an existing instance, it will be already populated by some definitions,
		 * on a new instance, it is completely empty.
		 */
		final List<ConceptSetDefinition> updatedModelChildren = updatedModel.getChildren();
		
		// Index concept set definition keys by list position
		final Map<String, Integer> existingDefinitionsByIdx = newHashMap();
		for (int i = 0; i < updatedModelChildren.size(); i++) {
			final ConceptSetDefinition existingDefinition = updatedModelChildren.get(i);
			existingDefinitionsByIdx.put(existingDefinition.getUuid(), i);
		}
		
		// Index new definitions by key
		final Map<String, SnomedConceptSetDefinition> updatedDefinitions = newHashMap(Maps.uniqueIndex(children, SnomedConceptSetDefinition::getId));
		
		// Iterate backwards over the list so that removals don't mess up the the list index map
		for (int j = updatedModelChildren.size() - 1; j >= 0; j--) {
			final ConceptSetDefinition existingDefinition = updatedModelChildren.get(j);
			final String uuid = existingDefinition.getUuid();
			
			// Consume entries from "updatedDefinitions" by using remove(Object key)
			final SnomedConceptSetDefinition updatedDefinition = updatedDefinitions.remove(uuid);
			
			// Was there a child with the same key? If not, remove the original from the list, if it is still there, update in place
			if (updatedDefinition == null) {
				updatedModelChildren.remove(j);
			} else {
				updatedModelChildren.set(j, updatedDefinition.applyChangesTo(existingDefinition));
			}
		}
		
		// Remaining entries in "updatedDefinitions" are new; add them to the end of the list
		for (SnomedConceptSetDefinition newChild : updatedDefinitions.values()) {
			updatedModelChildren.add(newChild.applyChangesTo(newChild.createModel()));
		}
		
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setUuid(getId());
		
		return updatedModel;
	}
}
