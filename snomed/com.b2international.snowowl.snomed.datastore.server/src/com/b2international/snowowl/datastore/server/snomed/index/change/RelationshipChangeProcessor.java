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
package com.b2international.snowowl.datastore.server.snomed.index.change;

import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.datastore.index.ComponentBaseUpdater;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.update.ComponentModuleUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.RelationshipImmutablePropertyUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.RelationshipMutablePropertyUpdater;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.3
 */
public class RelationshipChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {

	public RelationshipChangeProcessor() {
		super("relationship changes");
	}

	@Override
	protected void indexDocuments(ICDOCommitChangeSet commitChangeSet) {
		final Iterable<Relationship> newRelationships = FluentIterable.from(commitChangeSet.getNewComponents()).filter(Relationship.class);
		for (Relationship relationship : newRelationships) {
			registerImmutablePropertyUpdates(relationship);
			registerMutablePropertyUpdates(relationship);
		}
	}
	
	@Override
	protected void updateDocuments(ICDOCommitChangeSet commitChangeSet) {
		final Iterable<Relationship> dirtyRelationships = FluentIterable.from(commitChangeSet.getDirtyComponents()).filter(Relationship.class);
		for (Relationship relationship : dirtyRelationships) {
			registerMutablePropertyUpdates(relationship);
		}
	}
	
	@Override
	protected void deleteDocuments(ICDOCommitChangeSet commitChangeSet) {
		registerDeletions(getDetachedComponents(commitChangeSet, SnomedPackage.Literals.RELATIONSHIP));
	}

	private void registerImmutablePropertyUpdates(Relationship relationship) {
		final String id = relationship.getId();
		registerUpdate(id, new RelationshipImmutablePropertyUpdater(relationship));
		registerUpdate(id, new ComponentBaseUpdater<SnomedDocumentBuilder>(id, SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship.cdoID()));
	}
	
	private void registerMutablePropertyUpdates(Relationship relationship) {
		final String id = relationship.getId();
		registerUpdate(id, new ComponentModuleUpdater(relationship));
		registerUpdate(id, new RelationshipMutablePropertyUpdater(relationship));
	}
	
}
