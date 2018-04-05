/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;

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
		return values[4];
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

}