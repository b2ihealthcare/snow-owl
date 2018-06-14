/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons.snomed;

import java.math.BigDecimal;

import com.b2international.collections.PrimitiveSets;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * 
 * @since 6.4
 */
public abstract class DocumentBuilders {

	private DocumentBuilders() {}
	
	public static SnomedConceptDocument.Builder concept(final String id) {
		return SnomedConceptDocument.builder()
				.id(id)
				.iconId(Concepts.ROOT_CONCEPT)
				.active(true)
				.released(true)
				.exhaustive(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.primitive(true)
				.parents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.ancestors(PrimitiveSets.newLongOpenHashSet())
				.statedParents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet());
	}
	
	public static SnomedDescriptionIndexEntry.Builder description(final String id, final String type, final String term) {
		return SnomedDescriptionIndexEntry.builder()
				.id(id)
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.typeId(type)
				.languageCode("en")
				.term(term)
				.caseSignificanceId(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.getConceptId())
				.released(false);
	}
	
	public static SnomedRefSetMemberIndexEntry.Builder member(final String id, String referencedComponentId, short referencedComponentType, String referenceSetId) {
		return SnomedRefSetMemberIndexEntry.builder()
				.id(referenceSetId)
				.active(true)
				.referencedComponentId(referencedComponentId)
				.referencedComponentType(referencedComponentType)
				.referenceSetId(referenceSetId)
				.released(true);
	}
	
	public static SnomedRelationshipIndexEntry.Builder relationship(final String source, final String type, final String destination) {
		return relationship(source, type, destination, Concepts.INFERRED_RELATIONSHIP);
	}
	
	public static SnomedRelationshipIndexEntry.Builder relationship(final String source, final String type, final String destination, String characteristicTypeId) {
		return SnomedRelationshipIndexEntry.builder()
				.id(RandomSnomedIdentiferGenerator.generateRelationshipId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.sourceId(source)
				.typeId(type)
				.destinationId(destination)
				.characteristicTypeId(characteristicTypeId)
				.modifierId(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);
	}
	
	public static SnomedRefSetMemberIndexEntry.Builder decimalMember(final String referencedComponentId, final String attributeName, final BigDecimal value) {
		return concreteDomain(referencedComponentId, attributeName, value, DataType.DECIMAL);
	}
	
	public static SnomedRefSetMemberIndexEntry.Builder integerMember(final String referencedComponentId, final String attributeName, final int value) {
		return concreteDomain(referencedComponentId, attributeName, value, DataType.INTEGER);
	}
	
	public static SnomedRefSetMemberIndexEntry.Builder stringMember(final String referencedComponentId, final String attributeName, final String value) {
		return concreteDomain(referencedComponentId, attributeName, value, DataType.STRING);
	}

	public static SnomedRefSetMemberIndexEntry.Builder concreteDomain(final String referencedComponentId, final String attributeName, final Object value, final DataType type) {
		final short referencedComponentType = 
				SnomedIdentifiers.getComponentCategory(referencedComponentId) == ComponentCategory.CONCEPT 
					? SnomedTerminologyComponentConstants.CONCEPT_NUMBER 
					: SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
		return SnomedRefSetMemberIndexEntry.builder()
				.id(RandomSnomedIdentiferGenerator.generateRelationshipId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(referencedComponentId)
				.referencedComponentType(referencedComponentType)
				.referenceSetId(RandomSnomedIdentiferGenerator.generateConceptId())
				.referenceSetType(SnomedRefSetType.CONCRETE_DATA_TYPE)
				.field(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.INFERRED_RELATIONSHIP)
				.field(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, attributeName)
				.field(Fields.DATA_TYPE, type)
				.field(SnomedRf2Headers.FIELD_VALUE, value);
	}
	
}