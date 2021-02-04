/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.ObjectId;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.index.revision.StagingArea.RevisionDiff;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.repository.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.*;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

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
		final Set<String> reactivatedComponentIds = newHashSet();
		final Set<String> reactivatedConceptIds = newHashSet();
		
		staging.getChangedRevisions(SnomedComponentDocument.class)
			.filter(diff -> diff.hasRevisionPropertyChanges(SnomedRf2Headers.FIELD_ACTIVE))
			.filter(diff -> diff.newRevision instanceof SnomedComponentDocument)
			.forEach(diff -> {
				RevisionPropertyDiff propDiff = diff.getRevisionPropertyDiff(SnomedRf2Headers.FIELD_ACTIVE);
				boolean oldValue = Boolean.parseBoolean(propDiff.getOldValue());
				boolean newValue = Boolean.parseBoolean(propDiff.getNewValue());
				
				// inactivation
				if (oldValue && !newValue) {
					inactivatedComponentIds.add(diff.newRevision.getId());
					if (diff.newRevision instanceof SnomedConceptDocument) {
						inactivatedConceptIds.add(diff.newRevision.getId());						
					}
				} else if (!oldValue && newValue) {
					reactivatedComponentIds.add(diff.newRevision.getId());
					if (diff.newRevision instanceof SnomedConceptDocument) {
						reactivatedConceptIds.add(diff.newRevision.getId());						
					}
				}
				
			});
		
		processInactivations(staging, searcher, inactivatedConceptIds, inactivatedComponentIds);
		processReactivations(staging, searcher, reactivatedConceptIds, reactivatedComponentIds);
	}

	private void processInactivations(StagingArea staging, RevisionSearcher searcher, Set<String> inactivatedConceptIds, Set<String> inactivatedComponentIds) throws IOException {
		// inactivate descriptions of inactivated concepts, take current description changes into account
		
		ServiceProvider context = (ServiceProvider) staging.getContext();
		ModuleIdProvider moduleIdProvider = context.service(ModuleIdProvider.class);
		
		if (!inactivatedConceptIds.isEmpty()) {
			
			Multimap<String, RevisionDiff> changedMembersByReferencedComponentId = HashMultimap.create();
			staging.getChangedRevisions(SnomedRefSetMemberIndexEntry.class).forEach(diff -> {
				changedMembersByReferencedComponentId.put(((SnomedRefSetMemberIndexEntry) diff.newRevision).getReferencedComponentId(), diff);
			});
			
			SnomedRefSetMemberIndexEntry memberForModule = staging.getChangedRevisions(SnomedRefSetMemberIndexEntry.class).findFirst().map(diff -> (SnomedRefSetMemberIndexEntry) diff.newRevision).get();
			String moduleId = moduleIdProvider.apply(memberForModule);
			
			for (Hits<String> hits : searcher.scroll(Query.select(String.class)
					.from(SnomedDescriptionIndexEntry.class)
					.fields(SnomedDescriptionIndexEntry.Fields.ID)
					.where(Expressions.builder()
							.filter(SnomedDescriptionIndexEntry.Expressions.active())
							.filter(SnomedDescriptionIndexEntry.Expressions.concepts(inactivatedConceptIds))
							.build())
					.limit(PAGE_SIZE)
					.build())) {
				final Set<String> descriptionIds = Sets.newHashSet(hits);
				
				// load existing indicator reference set members from index
				final Multimap<String, SnomedRefSetMemberIndexEntry> existingIndicatorReferenceSetMembers = HashMultimap.create();
				searcher.search(Query.select(SnomedRefSetMemberIndexEntry.class)
						.where(Expressions.builder()
								.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
								.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(descriptionIds))
								.build())
						.limit(Integer.MAX_VALUE)
						.build())
						.forEach(existingIndicatorMember -> {
							existingIndicatorReferenceSetMembers.put(existingIndicatorMember.getReferencedComponentId(), existingIndicatorMember);
						});
				
				// override members with the ones that present in the staging area
				for (String descriptionIdToCheck : hits) {
					// get the persisted, existing members
					// get the current members from the tx
					final Collection<SnomedRefSetMemberIndexEntry> transactionMembers = changedMembersByReferencedComponentId.get(descriptionIdToCheck).stream()
							.map(diff -> diff.newRevision)
							.map(SnomedRefSetMemberIndexEntry.class::cast)
							.filter(member -> Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(member.getReferenceSetId()))
							.collect(Collectors.toList());
					// if there were no registered member changes to this description
					if (transactionMembers.isEmpty()) {
						// apply CONCEPT_NON_CURRENT to all existing members or generate a new one 
						final SnomedRefSetMemberIndexEntry existingMember = existingIndicatorReferenceSetMembers.get(descriptionIdToCheck)
								.stream()
								.filter(member -> {
									// reusable member, if it was inactivated earlier
									// was active and used one of the active description attribute values
									return !member.isActive() 
											|| SnomedRefSetUtil.ATTRIBUTE_VALUES_FOR_ACTIVE_DESCRIPTIONS.contains(member.getValueId());
								})
								.findFirst()
								.orElse(null);
						if (existingMember == null) {
							SnomedRefSetMemberIndexEntry inactivationMember = SnomedRefSetMemberIndexEntry.builder()
								.id(UUID.randomUUID().toString())
								.active(true)
								.released(false)
								.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR)
								.referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE)
								.referencedComponentId(descriptionIdToCheck)
								.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
								.moduleId(moduleId)
								.field(SnomedRf2Headers.FIELD_VALUE_ID, Concepts.CONCEPT_NON_CURRENT)
								.build();
							stageNew(inactivationMember);
						} else {
							// update the existing member only, if it was active, registered as PENDING_MOVE
							final SnomedRefSetMemberIndexEntry updated = SnomedRefSetMemberIndexEntry.builder(existingMember)
									.active(true) // ensure active
									.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME) // ensure unpublished
									.field(SnomedRf2Headers.FIELD_VALUE_ID, Concepts.CONCEPT_NON_CURRENT) // ensure non-current
									.moduleId(moduleIdProvider.apply(existingMember))
									.build();
							stageChange(existingMember, updated);
						}
						
					}
				}
			}
			
			// inactivate relationships of inactivated concepts
			
			final Map<ObjectId, RevisionDiff> changedRevisions = staging.getChangedRevisions();
			for (Hits<SnomedRelationshipIndexEntry> hits : searcher.scroll(Query.select(SnomedRelationshipIndexEntry.class)
					.where(Expressions.builder()
							.filter(SnomedRelationshipIndexEntry.Expressions.active())
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
								.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
								.moduleId(moduleIdProvider.apply(relationship))
								.build());
					} else {
						stageChange(relationship, SnomedRelationshipIndexEntry.builder(relationship)
								.active(false)
								.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
								.moduleId(moduleIdProvider.apply(relationship))
								.build());
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
							.moduleId(moduleIdProvider.apply(member))
							.build());
				}
			});
		}		
	}
	
	private void processReactivations(StagingArea staging, RevisionSearcher searcher, Set<String> reactivatedConceptIds, Set<String> reactivatedComponentIds) throws IOException {
		for (Hits<String> hits : searcher.scroll(Query.select(String.class)
				.from(SnomedDescriptionIndexEntry.class)
				.fields(SnomedDescriptionIndexEntry.Fields.ID)
				// active descriptions, with active membership in the indicator refset on reactivated concepts
				.where(Expressions.builder()
						.filter(SnomedDescriptionIndexEntry.Expressions.active())
						.filter(SnomedDescriptionIndexEntry.Expressions.concepts(reactivatedConceptIds))
						.filter(SnomedDescriptionIndexEntry.Expressions.activeMemberOf(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
						.build())
				.limit(PAGE_SIZE)
				.build())) {
			
			final Set<String> descriptionIds = ImmutableSet.copyOf(hits.getHits());
			
			final Set<String> stagedDescriptionIndicators = staging.getChangedObjects(SnomedRefSetMemberIndexEntry.class)
				.filter(member -> Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(member.getReferenceSetId()))
				.filter(member -> descriptionIds.contains(member.getReferencedComponentId()))
				.map(SnomedRefSetMemberIndexEntry::getId)
				.collect(Collectors.toSet());
			
			// search for all active indicator refset members with concept non-current
			Hits<SnomedRefSetMemberIndexEntry> members = searcher.search(Query.select(SnomedRefSetMemberIndexEntry.class)
					.where(Expressions.builder()
							.mustNot(SnomedRefSetMemberIndexEntry.Expressions.ids(stagedDescriptionIndicators))
							.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
							.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
							.filter(SnomedRefSetMemberIndexEntry.Expressions.valueIds(ImmutableSet.of(Concepts.CONCEPT_NON_CURRENT)))
							.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(descriptionIds))
							.build())
					.limit(Integer.MAX_VALUE) // we limit the number of possible members with the above query, even with duplicates we should not get more than 20k
					.build());
			
			for (SnomedRefSetMemberIndexEntry indicatorMember : members) {
				// check if this member is present in the transaction, if yes, do not auto-update/delete it
				if (indicatorMember.isReleased() != null && indicatorMember.isReleased()) {
					stageChange(indicatorMember, SnomedRefSetMemberIndexEntry.builder(indicatorMember).active(false)
							.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
							.build());
				} else {
					stageRemove(indicatorMember);
				}
			}
			
		}
	}

}
