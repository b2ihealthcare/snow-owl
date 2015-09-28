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

import static java.lang.Long.parseLong;

import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;

/**
 * @since 4.3
 */
public class RelationshipImmutablePropertyUpdater extends DocumentUpdaterBase<SnomedDocumentBuilder> {

	private Relationship relationship;

	public RelationshipImmutablePropertyUpdater(Relationship relationship) {
		super(relationship.getId());
		this.relationship = relationship;
	}

	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
		final long typeId = parseLong(relationship.getType().getId());
		final long sourceId = parseLong(relationship.getSource().getId());
		final long destinationId = parseLong(relationship.getDestination().getId());
		doc
			.relationshipType(typeId)
			.docValuesField(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID, sourceId)
			.docValuesField(SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID, destinationId);
	}
}
