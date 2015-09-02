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
package com.b2international.snowowl.snomed.datastore.index;

import static com.b2international.snowowl.datastore.cdo.CDOIDUtils.asLong;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.INFERRED_RELATIONSHIP;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.UNIVERSAL_RESTRICTION_MODIFIER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_INFERRED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNION_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID;
import static java.lang.Long.parseLong;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericDocValuesField;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * Mapping strategy for SNOMED CT relationships.
 * 
 */
public class SnomedRelationshipIndexMappingStrategy extends AbstractIndexMappingStrategy {

	private final Relationship relationship;

	public SnomedRelationshipIndexMappingStrategy(final Relationship relationship) {
		this.relationship = relationship;
	}

	@Override
	public Document createDocument() {

		final long relationshipId = parseLong(relationship.getId());
		final boolean active = relationship.isActive();
		final long storageKey = asLong(relationship.cdoID());
		final int group = relationship.getGroup();
		final int unionGroup = relationship.getUnionGroup();
		final long sourceId = parseLong(relationship.getSource().getId());
		final long destinationId = parseLong(relationship.getDestination().getId());
		final long typeId = parseLong(relationship.getType().getId());
		final long characteristicTypeId = parseLong(relationship.getCharacteristicType().getId());
		final boolean inferred = INFERRED_RELATIONSHIP.equals(relationship.getCharacteristicType().getId());
		final boolean universal = UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship.getModifier().getId());
		final long moduleId = parseLong(relationship.getModule().getId());

		final Document doc = SnomedMappings.doc()
				.id(relationshipId)
				.type(RELATIONSHIP_NUMBER)
				.storageKey(storageKey)
				.active(active)
				.module(moduleId)
				.relationshipType(typeId)
				.relationshipCharacteristicType(characteristicTypeId)
				.docValuesField(RELATIONSHIP_OBJECT_ID, sourceId)
				.docValuesField(RELATIONSHIP_VALUE_ID, destinationId)
				.storedOnly(COMPONENT_RELEASED, relationship.isReleased() ? 1 : 0)
				.storedOnly(RELATIONSHIP_GROUP, group)
				.storedOnly(RELATIONSHIP_UNION_GROUP, unionGroup)
				.storedOnly(RELATIONSHIP_DESTINATION_NEGATED, relationship.isDestinationNegated() ? 1 : 0)
				.storedOnly(RELATIONSHIP_INFERRED, inferred ? 1 : 0)
				.storedOnly(RELATIONSHIP_UNIVERSAL, universal ? 1 : 0)
				.field(RELATIONSHIP_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(relationship.getEffectiveTime()))
				.build();

		// TODO replace
		doc.add(new NumericDocValuesField(RELATIONSHIP_GROUP, group));
		doc.add(new NumericDocValuesField(RELATIONSHIP_UNION_GROUP, unionGroup));
		doc.add(new NumericDocValuesField(RELATIONSHIP_UNIVERSAL, universal ? 1 : 0));
		doc.add(new NumericDocValuesField(RELATIONSHIP_DESTINATION_NEGATED, relationship.isDestinationNegated() ? 1 : 0));
		
		return doc;
	}

	@Override
	protected long getStorageKey() {
		return asLong(relationship.cdoID());
	}
}
