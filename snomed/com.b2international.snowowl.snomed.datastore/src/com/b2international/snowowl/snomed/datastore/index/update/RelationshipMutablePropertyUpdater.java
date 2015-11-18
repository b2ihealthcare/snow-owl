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
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_INFERRED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNION_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL;

import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;

/**
 * @since 4.3
 */
public class RelationshipMutablePropertyUpdater extends ComponentMutablePropertyUpdater {

	public RelationshipMutablePropertyUpdater(Component component) {
		super(component);
	}
	
	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
		super.doUpdate(doc);
		doc
			.removeAll(Mappings.storedOnlyIntFieldWithDocValues(RELATIONSHIP_UNIVERSAL))
			.removeAll(Mappings.storedOnlyIntFieldWithDocValues(RELATIONSHIP_GROUP))
			.removeAll(Mappings.storedOnlyIntFieldWithDocValues(RELATIONSHIP_UNION_GROUP))
			.removeAll(Mappings.storedOnlyIntFieldWithDocValues(RELATIONSHIP_DESTINATION_NEGATED))
			.removeAll(Mappings.storedOnlyIntField(RELATIONSHIP_INFERRED));
		
		final long characteristicTypeId = Long.parseLong(getComponent().getCharacteristicType().getId());
		final int group = getComponent().getGroup();
		final int unionGroup = getComponent().getUnionGroup();
		final boolean inferred = INFERRED_RELATIONSHIP.equals(getComponent().getCharacteristicType().getId());
		final boolean universal = UNIVERSAL_RESTRICTION_MODIFIER.equals(getComponent().getModifier().getId());
		doc
			.relationshipCharacteristicType(characteristicTypeId)
			.storedOnlyWithDocValues(RELATIONSHIP_GROUP, group)
			.storedOnlyWithDocValues(RELATIONSHIP_UNION_GROUP, unionGroup)
			.storedOnlyWithDocValues(RELATIONSHIP_DESTINATION_NEGATED, getComponent().isDestinationNegated() ? 1 : 0)
			.storedOnly(RELATIONSHIP_INFERRED, inferred ? 1 : 0)
			.storedOnlyWithDocValues(RELATIONSHIP_UNIVERSAL, universal ? 1 : 0);
	}
	
	@Override
	protected Relationship getComponent() {
		return (Relationship) super.getComponent();
	}

}
