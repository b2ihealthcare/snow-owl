/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.ObjectId;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.index.revision.StagingArea.RevisionDiff;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
		final Set<String> inactivatedComponentIds = newHashSet(); 
		final Set<String> inactivatedConceptIds = newHashSet();
		
		staging.getChangedRevisions(SnomedComponentDocument.class)
			.filter(diff -> {
				if (diff.hasRevisionPropertyDiff(SnomedRf2Headers.FIELD_ACTIVE)) {
					Boolean newValue = Boolean.valueOf(diff.getRevisionPropertyDiff(SnomedRf2Headers.FIELD_ACTIVE).getNewValue());
					return !newValue;
				} else {
					return false;
				}
			})
			.forEach(diff -> {
				if (diff.newRevision instanceof SnomedComponentDocument) {
					inactivatedComponentIds.add(diff.newRevision.getId());
					if (diff.newRevision instanceof SnomedConceptDocument) {
						inactivatedConceptIds.add(diff.newRevision.getId());						
					}
				}
			});
		
		// inactivate descriptions of inactivated concepts, take current description changes into account
		if (!inactivatedConceptIds.isEmpty()) {
			
			Multimap<String, RevisionDiff> changedMembersByReferencedComponentId = HashMultimap.create();
			staging.getChangedRevisions(SnomedRefSetMemberIndexEntry.class).forEach(diff -> {
				changedMembersByReferencedComponentId.put(((SnomedRefSetMemberIndexEntry) diff.newRevision).getReferencedComponentId(), diff);
			});
			
			for (Hits<String[]> hits : searcher.scroll(Query.select(String[].class)
					.from(SnomedDescriptionIndexEntry.class)
					.fields(SnomedDescriptionIndexEntry.Fields.ID, SnomedDescriptionIndexEntry.Fields.MODULE_ID)
					.where(SnomedDescriptionIndexEntry.Expressions.concepts(inactivatedConceptIds))
					.limit(PAGE_SIZE)
					.build())) {
				// TODO exclude descriptions that are already present in the tx or apply 
				hits.forEach(description -> {
					final String descriptionId = description[0];
					
					SnomedRefSetMemberIndexEntry existingInactivationMember = changedMembersByReferencedComponentId.get(descriptionId).stream()
							.map(diff -> diff.newRevision)
							.map(SnomedRefSetMemberIndexEntry.class::cast)
							.filter(member -> Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(member.getReferenceSetId()))
							.findFirst()
							.orElse(null);
					
					SnomedRefSetMemberIndexEntry.Builder inactivationMember;
					if (existingInactivationMember == null) {
						inactivationMember = SnomedRefSetMemberIndexEntry.builder()
							.id(UUID.randomUUID().toString())
							.active(true)
							.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR)
							.referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE)
							.referencedComponentId(descriptionId)
							.moduleId(description[1]);
					} else {
						inactivationMember = SnomedRefSetMemberIndexEntry.builder(existingInactivationMember);
					}
					
					// set to concept non current
					inactivationMember.field(SnomedRf2Headers.FIELD_VALUE_ID, Concepts.CONCEPT_NON_CURRENT);
					
					stageNew(inactivationMember.build());
				});
			}
			
			// inactivate relationships of inactivated concepts
			final Map<ObjectId, RevisionDiff> changedRevisions = staging.getChangedRevisions();
			for (Hits<SnomedRelationshipIndexEntry> hits : searcher.scroll(Query.select(SnomedRelationshipIndexEntry.class)
					.where(Expressions.builder()
							.should(SnomedRelationshipIndexEntry.Expressions.sourceIds(inactivatedConceptIds))
							.should(SnomedRelationshipIndexEntry.Expressions.destinationIds(inactivatedConceptIds))
							.build())
					.limit(PAGE_SIZE)
					.build())) {
				hits.forEach(relationship -> {
					inactivatedComponentIds.add(relationship.getId());
					if (changedRevisions.containsKey(relationship.getObjectId())) {
						stageChange(relationship, SnomedRelationshipIndexEntry.builder((SnomedRelationshipIndexEntry) changedRevisions.get(relationship.getObjectId()).newRevision)
								.active(false)
								.build());
					} else {
						stageChange(relationship, SnomedRelationshipIndexEntry.builder(relationship).active(false).build());
					}
				});
			}
		}
		
		if (inactivatedComponentIds.isEmpty()) {
			return;
		}
		
		// inactivate referring members of all inactivated core component, and all members of inactivated refsets
		final Map<ObjectId, RevisionDiff> changedRevisions = staging.getChangedRevisions();
		for (Hits<SnomedRefSetMemberIndexEntry> hits : searcher.scroll(Query.select(SnomedRefSetMemberIndexEntry.class)
				.where(Expressions.builder()
						.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
						.should(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(inactivatedComponentIds))
						.should(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(inactivatedComponentIds))
						.setMinimumNumberShouldMatch(1)
						.build())
				.limit(PAGE_SIZE)
				.build())) {
			hits.forEach(member -> {
				// XXX: setting effectiveTime to -1L here should be undone by SnomedRepositoryPreCommitHook's effective time restorer, if needed 
				if (changedRevisions.containsKey(member.getObjectId())) {
//					stageChange(member, SnomedRefSetMemberIndexEntry.builder((SnomedRefSetMemberIndexEntry) changedRevisions.get(member.getObjectId()).newRevision)
//							.active(false)
//							.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
//							.build());
				} else {
					stageChange(member, SnomedRefSetMemberIndexEntry.builder(member)
							.active(false)
							.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
							.build());
				}
			});
		}
	}

}
