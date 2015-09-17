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

import org.apache.lucene.document.Document;

import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.ComponentBaseUpdater;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.update.ComponentModuleUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.RelationshipImmutablePropertyUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.RelationshipMutablePropertyUpdater;

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
		return SnomedMappings.doc()
				.with(new ComponentBaseUpdater<SnomedDocumentBuilder>(relationship.getId(), SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship.cdoID()))
				.with(new ComponentModuleUpdater(relationship))
				.with(new RelationshipMutablePropertyUpdater(relationship))
				.with(new RelationshipImmutablePropertyUpdater(relationship))
				.build();
	}

	@Override
	protected long getStorageKey() {
		return asLong(relationship.cdoID());
	}
}
