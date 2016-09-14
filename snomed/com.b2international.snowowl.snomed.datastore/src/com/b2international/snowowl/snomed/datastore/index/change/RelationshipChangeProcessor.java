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
package com.b2international.snowowl.snomed.datastore.index.change;

import java.io.IOException;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Builder;

/**
 * @since 4.3
 */
public class RelationshipChangeProcessor extends ChangeSetProcessorBase {

	public RelationshipChangeProcessor() {
		super("relationship changes");
	}

	@Override
	public void process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) throws IOException {
		deleteRevisions(SnomedRelationshipIndexEntry.class, commitChangeSet.getDetachedComponents(SnomedPackage.Literals.RELATIONSHIP));
		
		for (Relationship relationship : commitChangeSet.getNewComponents(Relationship.class)) {
			final Builder doc = SnomedRelationshipIndexEntry.builder(relationship);
			indexNewRevision(relationship.cdoID(), doc.build());
		}
		
		for (Relationship relationship : commitChangeSet.getDirtyComponents(Relationship.class)) {
			final Builder doc = SnomedRelationshipIndexEntry.builder(relationship);
			indexChangedRevision(relationship.cdoID(), doc.build());
		}
	}
	
}
