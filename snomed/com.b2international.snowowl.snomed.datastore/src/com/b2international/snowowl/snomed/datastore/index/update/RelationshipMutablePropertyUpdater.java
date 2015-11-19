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
package com.b2international.snowowl.snomed.datastore.index.update;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.INFERRED_RELATIONSHIP;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.UNIVERSAL_RESTRICTION_MODIFIER;

import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;

/**
 * @since 4.3
 */
public class RelationshipMutablePropertyUpdater extends ComponentMutablePropertyUpdater {

	public RelationshipMutablePropertyUpdater(Relationship relationship) {
		super(relationship);
	}
	
	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
		super.doUpdate(doc);
		
		final long characteristicTypeId = Long.parseLong(getComponent().getCharacteristicType().getId());
		final int group = getComponent().getGroup();
		final int unionGroup = getComponent().getUnionGroup();
		final boolean inferred = INFERRED_RELATIONSHIP.equals(getComponent().getCharacteristicType().getId());
		final boolean universal = UNIVERSAL_RESTRICTION_MODIFIER.equals(getComponent().getModifier().getId());
		
		doc
			.relationshipCharacteristicType(characteristicTypeId)
			.relationshipGroup(group)
			.relationshipUnionGroup(unionGroup)
			.relationshipDestinationNegated(getComponent().isDestinationNegated())
			.relationshipInferred(inferred)
			.relationshipUniversal(universal);
	}
	
	@Override
	protected Relationship getComponent() {
		return (Relationship) super.getComponent();
	}
}
