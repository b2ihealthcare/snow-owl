/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2.terminology;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.importer.ImportAction;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.importer.rf2.csv.RelationshipRow;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

/**
 * Updates relationship union groups on universal has active ingredient relationships.
 */
public class SnomedUnionGroupImporter extends SnomedRelationshipImporter {
	
	private static class UniversalActiveIngredientFilter implements Predicate<Relationship> {
		private final String characteristicTypeId;

		private UniversalActiveIngredientFilter(String characteristicTypeId) {
			this.characteristicTypeId = characteristicTypeId;
		}

		@Override
		public boolean apply(Relationship input) {
			return input.isActive()
					&& input.getType().getId().equals(Concepts.HAS_ACTIVE_INGREDIENT) 
					&& input.getModifier().getId().equals(Concepts.UNIVERSAL_RESTRICTION_MODIFIER)
					&& input.getCharacteristicType().getId().equals(characteristicTypeId);
		}
	}

	private final Set<String> conceptIds = Sets.newHashSet();
	
	public SnomedUnionGroupImporter(SnomedImportContext importContext, InputStream releaseFileStream, String releaseFileIdentifier) {
		super(importContext, releaseFileStream, releaseFileIdentifier, ComponentImportType.RELATIONSHIP_UNION_GROUP, false);
	}
	
	@Override
	protected void importRow(RelationshipRow currentRow) {
		/* 
		 * XXX: Not testing status here, as an inactive relationship may mean that the union group has 
		 * to be updated on remaining active HAI relationships.
		 */
		if (hasActiveIngredientType(currentRow) && isUniversal(currentRow) && isStatedOrInferred(currentRow)) {
			conceptIds.add(currentRow.getSourceId());
		}
	}

	private boolean hasActiveIngredientType(RelationshipRow currentRow) {
		return Concepts.HAS_ACTIVE_INGREDIENT.equals(currentRow.getTypeId());
	}

	private boolean isUniversal(RelationshipRow currentRow) {
		return Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(currentRow.getModifierId());
	}

	private boolean isStatedOrInferred(RelationshipRow currentRow) {
		return Concepts.STATED_RELATIONSHIP.equals(currentRow.getCharacteristicTypeId()) || Concepts.INFERRED_RELATIONSHIP.equals(currentRow.getCharacteristicTypeId());
	}

	@Override
	protected ImportAction commit(SubMonitor subMonitor, String formattedEffectiveTime) {
		for (String conceptId : conceptIds) {
			updateUnionGroup(conceptId, Concepts.STATED_RELATIONSHIP);
			updateUnionGroup(conceptId, Concepts.INFERRED_RELATIONSHIP);
		}
		
		conceptIds.clear();
		return super.commit(subMonitor, formattedEffectiveTime);
	}

	private void updateUnionGroup(final String conceptId, final String characteristicTypeId) {
		final Concept concept = getConcept(conceptId);
		final List<Relationship> activeUniversalHAI = FluentIterable.from(concept.getOutboundRelationships())
				.filter(new UniversalActiveIngredientFilter(characteristicTypeId))
				.toList();
		
		if (activeUniversalHAI.isEmpty()) {
			return;
		}
		
		// If there is more than a single universal HAI relationship on the concept, set the union group number 
		final int unionGroup = activeUniversalHAI.size() > 1 ? 1 : 0;
		
		for (Relationship relationship : activeUniversalHAI) {
			if (relationship.getUnionGroup() != unionGroup) {
				relationship.setUnionGroup(unionGroup);
			}
		}
		
		return;
	}
	
	
	@Override
	protected String getCommitMessage(String effectiveTimeKey) {
		if (!UNPUBLISHED_KEY.equals(effectiveTimeKey)) {
			return MessageFormat.format("Updated union groups on SNOMED CT relationships with effective {0}.", effectiveTimeKey);
		} else {
			return "Updated union groups on unpublished SNOMED CT relationships.";
		}
	}
}
