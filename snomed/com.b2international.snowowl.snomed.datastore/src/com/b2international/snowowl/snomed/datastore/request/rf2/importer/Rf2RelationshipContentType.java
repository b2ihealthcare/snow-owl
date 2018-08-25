/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationDefects;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationIssueReporter;

/**
 * @since 6.0.0
 */
final class Rf2RelationshipContentType implements Rf2ContentType<SnomedRelationship> {

	@Override
	public SnomedRelationship create() {
		return new SnomedRelationship();
	}

	@Override
	public String getContainerId(String[] values) {
		final String sourceId = values[4];
		return sourceId;
	}

	@Override
	public String[] getHeaderColumns() {
		return SnomedRf2Headers.RELATIONSHIP_HEADER;
	}

	@Override
	public void resolve(SnomedRelationship component, String[] values) {
		component.setSourceId(values[4]);
		component.setDestinationId(values[5]);
		component.setGroup(Integer.parseInt(values[6]));
		component.setTypeId(values[7]);
		component.setCharacteristicType(CharacteristicType.getByConceptId(values[8]));
		component.setModifier(RelationshipModifier.getByConceptId(values[9]));
		component.setUnionGroup(0);
	}

	@Override
	public String getType() {
		return "relationship";
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(
			Long.parseLong(values[3]), 
			Long.parseLong(values[4]),
			Long.parseLong(values[5]),
			Long.parseLong(values[7]),
			Long.parseLong(values[8]),
			Long.parseLong(values[9])
		);
	}
	
	@Override
	public void validateByContentType(Rf2ValidationIssueReporter reporter, String[] values) {
		final String relationshipId = values[0];
		final String sourceId = values[4];
		final String destinationId = values[5];
		final String typeId = values[7];
		final String characteristicTypeId = values[8];
		final String modifierId = values[9];
		
		if (sourceId.equals(destinationId)) {
			reporter.error(Rf2ValidationDefects.RELATIONSHIP_SOURCE_DESTINATION_EQUALS.getLabel());
		}
		
		try {
			SnomedIdentifiers.validate(relationshipId);
			validateByComponentCategory(relationshipId, reporter, ComponentCategory.RELATIONSHIP);
		} catch (IllegalArgumentException e) {
			reporter.error(String.format("%s %s", relationshipId, Rf2ValidationDefects.INVALID_ID.getLabel()));
		}
		
		validateConceptIds(reporter, sourceId, destinationId, typeId, characteristicTypeId, modifierId);
	}

}