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
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_CHARACTERISTIC_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_INFERRED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNION_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID;
import static java.lang.Long.parseLong;
import static org.apache.lucene.document.Field.Store.YES;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;

import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.field.ComponentIdLongField;
import com.b2international.snowowl.snomed.Relationship;

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

		final Document doc = new Document();
		new ComponentIdLongField(relationshipId).addTo(doc);
		doc.add(new IntField(CommonIndexConstants.COMPONENT_TYPE, RELATIONSHIP_NUMBER, YES));
		doc.add(new StoredField(COMPONENT_RELEASED, relationship.isReleased() ? 1 : 0));
		doc.add(new IntField(COMPONENT_ACTIVE, active ? 1 : 0, YES));
		doc.add(new LongField(CommonIndexConstants.COMPONENT_STORAGE_KEY, storageKey, YES));
		doc.add(new LongField(RELATIONSHIP_OBJECT_ID, sourceId, YES));
		doc.add(new LongField(RELATIONSHIP_ATTRIBUTE_ID, typeId, YES));
		doc.add(new LongField(RELATIONSHIP_VALUE_ID, destinationId, YES));
		doc.add(new LongField(RELATIONSHIP_CHARACTERISTIC_TYPE_ID, characteristicTypeId, YES));
		doc.add(new StoredField(RELATIONSHIP_GROUP, group));
		doc.add(new StoredField(RELATIONSHIP_UNION_GROUP, unionGroup));
		doc.add(new StoredField(RELATIONSHIP_DESTINATION_NEGATED, relationship.isDestinationNegated() ? 1 : 0));
		doc.add(new StoredField(RELATIONSHIP_INFERRED, inferred ? 1 : 0));
		doc.add(new StoredField(RELATIONSHIP_UNIVERSAL, universal ? 1 : 0));
		doc.add(new LongField(RELATIONSHIP_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(relationship.getEffectiveTime()), YES));
		doc.add(new LongField(COMPONENT_MODULE_ID, moduleId, YES));

		doc.add(new NumericDocValuesField(CommonIndexConstants.COMPONENT_STORAGE_KEY, storageKey));
		doc.add(new NumericDocValuesField(RELATIONSHIP_VALUE_ID, destinationId));
		doc.add(new NumericDocValuesField(RELATIONSHIP_OBJECT_ID, sourceId));
		doc.add(new NumericDocValuesField(RELATIONSHIP_ATTRIBUTE_ID, typeId));
		doc.add(new NumericDocValuesField(RELATIONSHIP_CHARACTERISTIC_TYPE_ID, characteristicTypeId));
		doc.add(new NumericDocValuesField(RELATIONSHIP_GROUP, group));
		doc.add(new NumericDocValuesField(RELATIONSHIP_UNION_GROUP, unionGroup));
		doc.add(new NumericDocValuesField(RELATIONSHIP_UNIVERSAL, universal ? 1 : 0));
		doc.add(new NumericDocValuesField(RELATIONSHIP_DESTINATION_NEGATED, relationship.isDestinationNegated() ? 1 : 0));
		doc.add(new NumericDocValuesField(COMPONENT_MODULE_ID, moduleId));

		return doc;
	}

	@Override
	protected long getStorageKey() {
		return asLong(relationship.cdoID());
	}
}
