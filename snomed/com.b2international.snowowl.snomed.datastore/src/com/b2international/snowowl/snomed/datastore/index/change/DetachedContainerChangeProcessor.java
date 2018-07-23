/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Set;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * Processes deleted container components and makes sure that components contained by the container will be deleted/inactivated etc. as well. 
 * 
 * @since 7.0
 */
public final class DetachedContainerChangeProcessor extends ChangeSetProcessorBase {

	public DetachedContainerChangeProcessor() {
		super("referring members");
	}

	@Override
	public void process(StagingArea staging, RevisionSearcher searcher) throws IOException {
		final Set<String> deletedCoreComponentIds = newHashSet();
		final Set<String> deletedConceptIds = newHashSet();
		
		staging.getRemovedObjects().values()
			.forEach(detachedObject -> {
				if (detachedObject instanceof SnomedComponentDocument) {
					String id = ((SnomedComponentDocument) detachedObject).getId();
					deletedCoreComponentIds.add(id);
					if (detachedObject instanceof SnomedConceptDocument) {
						deletedConceptIds.add(id);
					}
				}
			});
		
		if (deletedCoreComponentIds.isEmpty() && deletedConceptIds.isEmpty()) {
			return;
		}
		
		// deleting concepts should delete all of its descriptions, relationships, and inbound relationships
		for (Hits<SnomedDescriptionIndexEntry> hits : searcher.scroll(Query
				.select(SnomedDescriptionIndexEntry.class)
				.where(SnomedDescriptionIndexEntry.Expressions.concepts(deletedConceptIds))
				.limit(10_000)
				.build()))  {
			for (SnomedDescriptionIndexEntry description : hits) {
				deletedCoreComponentIds.add(description.getId());
				stageRemove(description);
			}
		}
		
		for (Hits<SnomedRelationshipIndexEntry> hits : searcher.scroll(Query
				.select(SnomedRelationshipIndexEntry.class)
				.where(Expressions.builder()
						.should(SnomedRelationshipIndexEntry.Expressions.sourceIds(deletedConceptIds))
						.should(SnomedRelationshipIndexEntry.Expressions.destinationIds(deletedConceptIds))
						.build())
				.limit(10_000)
				.build()))  {
			for (SnomedRelationshipIndexEntry relationship : hits) {
				deletedCoreComponentIds.add(relationship.getId());
				stageRemove(relationship);
			}
		}
		
		// deleting core components should delete all referring members as well
		for (Hits<SnomedRefSetMemberIndexEntry> hits : searcher.scroll(Query
				.select(SnomedRefSetMemberIndexEntry.class)
				.where(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(deletedCoreComponentIds))
				.limit(10_000)
				.build()))  {
			for (SnomedRefSetMemberIndexEntry member : hits) {
				stageRemove(member);
			}
		}
	}

}
