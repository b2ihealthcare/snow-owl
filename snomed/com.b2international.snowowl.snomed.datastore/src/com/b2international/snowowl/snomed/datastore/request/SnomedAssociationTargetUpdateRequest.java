/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Builder;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Updates association reference set members on the {@link Inactivatable} specified by identifier.
 * <p>
 * Existing members are <b>removed</b> when:
 * <ul>
 * <li>The candidate association target map does not contain the member's reference set ID as a key (so it can 
 * not be re-used by setting a different target component ID on the member);
 * <li>The member is unreleased.
 * </ul>
 * <p>
 * Existing members are <b>inactivated</b> when:
 * <ul>
 * <li>The candidate association target map does not contain the member's reference set ID as a key;
 * <li>The member is already part of a release.
 * </ul>
 * <p>
 * Existing members are <b>updated</b> with a new target component identifier when:
 * <ul>
 * <li>The candidate association target map contains the member's reference set as a key, but no value exists in 
 * the candidate map for this key that would match the member's currently set target component ID.
 * </ul>
 * <p>
 * New members are <b>created</b> when:
 * <ul>
 * <li>All existing association reference set members have been processed;
 * <li>Entries are still present in the candidate association target map.
 * </ul>
 * <p>
 * The candidate map starts as a copy of the input map, and is trimmed gradually, whenever an existing reference set 
 * member is found that matches any entry in the map.
 * <p>
 * Whenever an existing released member is modified, it is compared to its most recently versioned representation, 
 * and its effective time is restored to the original value if the final state matches the most recently versioned 
 * state. 
 * 
 * @since 4.5
 */
final class SnomedAssociationTargetUpdateRequest extends BaseComponentMemberUpdateRequest {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedAssociationTargetUpdateRequest.class);

	private Multimap<AssociationType, String> newAssociationTargets;

	SnomedAssociationTargetUpdateRequest(final SnomedComponentDocument componentToUpdate) {
		super(componentToUpdate);
	}

	void setNewAssociationTargets(final Multimap<AssociationType, String> newAssociationTargets) {
		this.newAssociationTargets = newAssociationTargets;
	}
	
	@Override
	protected String getMemberType() {
		return "Association-member";
	}

	@Override
	protected boolean canUpdate(TransactionContext context) {
		// Null leaves targets unchanged, empty map clears all targets
		return newAssociationTargets != null;
	}
	
	@Override
	protected void doExecute(TransactionContext context, SnomedComponentDocument componentToUpdate) {
		final List<SnomedReferenceSetMember> existingMembers = newArrayList(
			SnomedRequests.prepareSearchMember()
				.all()
				.filterByReferencedComponent(componentToUpdate.getId())
				.filterByRefSet(Arrays.asList(AssociationType.values()).stream().map(AssociationType::getConceptId).collect(Collectors.toSet()))
				.build()
				.execute(context)
				.getItems()
		);
		final Multimap<AssociationType, String> newAssociationTargetsToCreate = HashMultimap.create(newAssociationTargets);
		final ModuleIdProvider moduleIdFunction = context.service(ModuleIdProvider.class);
		final Iterator<SnomedReferenceSetMember> memberIterator = existingMembers.iterator();
		while (memberIterator.hasNext()) {
			
			final SnomedReferenceSetMember existingMember = memberIterator.next();
			final AssociationType associationType = AssociationType.getByConceptId(existingMember.getReferenceSetId());
			
			if (null == associationType) {
				continue;
			}

			final String existingTargetId = ((SnomedComponent) existingMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_COMPONENT)).getId();
			
			if (newAssociationTargetsToCreate.remove(associationType, existingTargetId)) {
				// Exact match, just make sure that the member is active and remove it from the working list
				final Builder updatedMember = SnomedRefSetMemberIndexEntry.builder(existingMember);
				final SnomedRefSetMemberIndexEntry oldRevision = updatedMember.build();
				ensureMemberActive(context, existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());
				memberIterator.remove();
			}
		}

		/* 
		 * Remaining entries in the multimap have no exact match, but there might be other members with the 
		 * same association type; attempt re-using them, changing the association target in the process 
		 */
		for (final SnomedReferenceSetMember existingMember : existingMembers) {

			final AssociationType associationType = AssociationType.getByConceptId(existingMember.getReferenceSetId());
			if (null == associationType) {
				continue;
			}
			
			final Builder updatedMember = SnomedRefSetMemberIndexEntry.builder(existingMember);
			
			if (newAssociationTargetsToCreate.containsKey(associationType)) {
				// We can re-use the member by changing the target component identifier, and checking that it is active

				final Iterator<String> targetIterator = newAssociationTargetsToCreate.get(associationType).iterator();
				final String newTargetId = targetIterator.next();
				targetIterator.remove();

				if (LOG.isDebugEnabled()) { 
					LOG.debug("Changing association member {} with type {} and target component identifier from {} to {}.", 
							existingMember.getId(), 
							associationType, 
							((SnomedComponent) existingMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_COMPONENT)).getId(), 
							newTargetId);
				}

				// Change target component, set status to active if needed, place it in the supplied module
				SnomedRefSetMemberIndexEntry oldRevision = updatedMember.build();
				updatedMember.field(SnomedRf2Headers.FIELD_TARGET_COMPONENT, newTargetId);
				ensureMemberActive(context, existingMember, updatedMember);
				updateModule(context, existingMember, updatedMember, moduleIdFunction.apply(componentToUpdate));
				unsetEffectiveTime(existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());

			} else {
				// We have no use for this member -- remove or inactivate if already released
				SnomedRefSetMemberIndexEntry oldRevision = updatedMember.build();
				removeOrDeactivate(context, existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());
			}
		}

		/* 
		 * With all existing members processed, the last set of entries in the multimap will need 
		 * to be added as new members; defaultModuleId is only used if there is at least a single
		 * new entry. 
		 */
		for (final Entry<AssociationType, String> newAssociationEntry : newAssociationTargetsToCreate.entries()) {
			SnomedComponents.newAssociationMember()
					.withRefSet(newAssociationEntry.getKey().getConceptId())
					.withTargetComponentId(newAssociationEntry.getValue())
					.withReferencedComponent(componentToUpdate.getId())
					.withModule(moduleIdFunction.apply(componentToUpdate))
					.addTo(context);
		}
	}

}
