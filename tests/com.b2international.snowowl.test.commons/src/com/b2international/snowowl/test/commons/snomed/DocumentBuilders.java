/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.b2international.collections.PrimitiveSets;
import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.constraint.ConstraintForm;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintPredicateType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

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
	
	public static SnomedConstraintDocument.Builder constraint() {
		return SnomedConstraintDocument.builder()
				.id(UUID.randomUUID().toString())
				.predicateType(SnomedConstraintPredicateType.RELATIONSHIP)
				.form(ConstraintForm.ALL_FORMS)
				.active(true);
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
				.id(id)
				.active(true)
				.referencedComponentId(referencedComponentId)
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
	
	public static SnomedRefSetMemberIndexEntry.Builder classAxioms(final String sourceId, final Object...axioms) {
		checkArgument(!CompareUtils.isEmpty(axioms), "At least one axiom must be provided");
		checkArgument(axioms.length % 3 == 0, "Each axiom should have 3 arguments [typeId:String, destinationId:String, group:Integer].");
		int numberOfAxioms = (int) axioms.length / 3;
		final List<SnomedOWLRelationshipDocument> classAxioms = newArrayListWithCapacity(numberOfAxioms);
		for (int i = 0; i < numberOfAxioms; i++) {
			int offset = i * 3;
			classAxioms.add(new SnomedOWLRelationshipDocument((String) axioms[offset], (String) axioms[offset + 1], (int) axioms[offset + 2]));
		}
		
		return SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(sourceId)
				.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.referenceSetId(Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(classAxioms);
	}
	
	public static SnomedRefSetMemberIndexEntry.Builder decimalMember(final String referencedComponentId, final String typeId, final BigDecimal value, final String characteristicTypeId) {
		return concreteDomain(referencedComponentId, typeId, value, DataType.DECIMAL, characteristicTypeId);
	}
	
	public static SnomedRefSetMemberIndexEntry.Builder integerMember(final String referencedComponentId, final String typeId, final int value, final String characteristicTypeId) {
		return concreteDomain(referencedComponentId, typeId, value, DataType.INTEGER, characteristicTypeId);
	}
	
	public static SnomedRefSetMemberIndexEntry.Builder stringMember(final String referencedComponentId, final String typeId, final String value, final String characteristicTypeId) {
		return concreteDomain(referencedComponentId, typeId, value, DataType.STRING, characteristicTypeId);
	}

	public static SnomedRefSetMemberIndexEntry.Builder concreteDomain(final String referencedComponentId, final String typeId, final Object value, final DataType type, final String characteristicTypeId) {
		return SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(referencedComponentId)
				.referenceSetId(RandomSnomedIdentiferGenerator.generateConceptId())
				.referenceSetType(SnomedRefSetType.CONCRETE_DATA_TYPE)
				.field(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, characteristicTypeId)
				.field(SnomedRf2Headers.FIELD_TYPE_ID, typeId)
				.field(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, 0)
				.field(Fields.DATA_TYPE, type)
				.field(SnomedRf2Headers.FIELD_VALUE, value);
	}
}
