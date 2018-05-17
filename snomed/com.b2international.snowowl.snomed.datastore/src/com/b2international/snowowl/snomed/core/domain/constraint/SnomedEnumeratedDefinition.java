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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.google.common.base.Joiner;

/**
 * @since 6.5
 */
public final class SnomedEnumeratedDefinition extends SnomedConceptSetDefinition {

	private Set<String> conceptIds;

	public Set<String> getConceptIds() {
		return conceptIds;
	}
	
	public void setConceptIds(Set<String> conceptIds) {
		this.conceptIds = conceptIds;
	}
	
	@Override
	public String toEcl() {
		return Joiner.on(" OR ").join(conceptIds);
	}
	
	@Override
	public EnumeratedConceptSetDefinition createModel() {
		return MrcmFactory.eINSTANCE.createEnumeratedConceptSetDefinition();
	}
	
	@Override
	public EnumeratedConceptSetDefinition applyChangesTo(ConceptModelComponent existingModel) {
		final EnumeratedConceptSetDefinition updatedModel = (existingModel instanceof EnumeratedConceptSetDefinition)
				? (EnumeratedConceptSetDefinition) existingModel
				: createModel();
				
		updatedModel.setActive(isActive());
		updatedModel.setAuthor(getAuthor());

		/* 
		 * We will update this list in place; on an existing instance, it will be already populated by some concept IDs,
		 * on a new instance, it is completely empty.
		 */
		final List<String> updatedConceptIds = updatedModel.getConceptIds();
		final Set<String> conceptIdsToAdd = newHashSet(conceptIds);
		
		// Iterate backwards over the list so that removals don't mess up list indexes
		for (int j = updatedConceptIds.size() - 1; j >= 0; j--) {
			final String existingConceptId = updatedConceptIds.get(j);
			
			// Was there a child with the same key? If not, remove the original from the list, if it is still there, remove from the set instead
			if (!conceptIdsToAdd.contains(existingConceptId)) {
				updatedConceptIds.remove(j);
			} else {
				conceptIdsToAdd.remove(existingConceptId);
			}
		}
		
		// Remaining entries in "conceptIdsToAdd" are new; add them to the end of the list
		for (String newChild : conceptIdsToAdd) {
			updatedConceptIds.add(newChild);
		}
		
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setUuid(getId());
		
		return updatedModel;
	}
	
	@Override
	public SnomedEnumeratedDefinition deepCopy(Date date, String userName) {
		final SnomedEnumeratedDefinition copy = new SnomedEnumeratedDefinition();
		
		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setConceptIds(newHashSet(getConceptIds()));
		copy.setEffectiveTime(date.getTime());
		copy.setId(UUID.randomUUID().toString());
		
		return copy;
	}
	
	@Override
	public void collectConceptIds(Collection<String> conceptIds) {
		conceptIds.addAll(getConceptIds());
	}
}
