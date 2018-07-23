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

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * @since 7.0
 */
final class ComponentInactivationChangeProcessor extends ChangeSetProcessorBase {

	ComponentInactivationChangeProcessor() {
		super("component inactivations");
	}

	@Override
	public void process(StagingArea staging, RevisionSearcher searcher) throws IOException {
		// inactivating a concept should inactivate all of its descriptions, relationships, inbound relationships, and members
		final Set<String> inactivatedConceptIds = staging.getChangedRevisions(SnomedConceptDocument.class)
			.filter(diff -> {
				if (diff.hasRevisionPropertyDiff(SnomedRf2Headers.FIELD_ACTIVE)) {
					Boolean newValue = Boolean.valueOf(diff.getRevisionPropertyDiff(SnomedRf2Headers.FIELD_ACTIVE).getNewValue());
					return !newValue;
				} else {
					return false;
				}
			})
			.map(diff -> diff.newRevision.getId())
			.collect(Collectors.toSet());
		
		if (inactivatedConceptIds.isEmpty()) {
			return;
		}
		
		// inactivate descriptions of inactivated concepts
		for (Hits<String[]> hits : searcher.scroll(Query.select(String[].class)
				.from(SnomedDescriptionIndexEntry.class)
				.fields(SnomedDescriptionIndexEntry.Fields.ID, SnomedDescriptionIndexEntry.Fields.MODULE_ID)
				.where(SnomedDescriptionIndexEntry.Expressions.concepts(inactivatedConceptIds))
				.limit(10_000)
				.build())) {
			hits.forEach(description -> {
				SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
					.id(UUID.randomUUID().toString())
					.active(true)
					.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR)
					.referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE)
					.referencedComponentId(description[0])
					.referencedComponentType(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER)
					.field(SnomedRf2Headers.FIELD_VALUE_ID, Concepts.CONCEPT_NON_CURRENT)
					.moduleId(description[1])
					.build();
				
				stageNew(member);
			});
		}
		
		// inactivate relationships of inactivated concepts
		for (Hits<SnomedRelationshipIndexEntry> hits : searcher.scroll(Query.select(SnomedRelationshipIndexEntry.class)
				.where(Expressions.builder()
						.should(SnomedRelationshipIndexEntry.Expressions.sourceIds(inactivatedConceptIds))
						.should(SnomedRelationshipIndexEntry.Expressions.destinationIds(inactivatedConceptIds))
						.build())
				.limit(10_000)
				.build())) {
			hits.forEach(relationship -> {
				stageChange(relationship, SnomedRelationshipIndexEntry.builder(relationship).active(false).build());
			});
		}
	}

}
