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

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;

/**
 * Updates the inactivation reason on the {@link Inactivatable} component specified by identifier.
 * <p>
 * Existing members are <b>removed</b> when:
 * <ul>
 * <li>{@link #inactivationValueId} is set to an empty string, signalling that any existing inactivation reasons 
 * should be removed ("no reason given");
 * <li>The member is unreleased.
 * </ul>
 * <p>
 * Existing members are <b>inactivated</b> when:
 * <ul>
 * <li>{@link #inactivationValueId} is set to an empty string, signalling that any existing inactivation reasons 
 * should be removed ("no reason given");
 * <li>The member is already part of a release.
 * </ul>
 * <p>
 * The first existing member is <b>updated</b> with a new value identifier when:
 * <ul>
 * <li>{@link #inactivationValueId} is set to a non-empty string, and the value ID presented here does not match 
 * the member's currently set value ID.
 * </ul>
 * <p>
 * New members are <b>created</b> when:
 * <ul>
 * <li>No previous inactivation reason member exists;
 * <li>{@link #inactivationValueId} is set to a non-empty string.
 * </ul>
 * <p>
 * Multiple inactivation reason reference set members are always reduced to a single item; unused existing members 
 * will be removed or deactivated, depending on whether they were already released.
 * <p>
 * Whenever an existing released member is modified, it is compared to its most recently versioned representation, 
 * and its effective time is restored to the original value if the final state matches the most recently versioned 
 * state.
 * 
 * @since 4.5
 */
final class SnomedInactivationReasonUpdateRequest implements Request<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedInactivationReasonUpdateRequest.class);

	private static final String CLEAR = "";

	private final String referencedComponentId;
	private final String inactivationRefSetId;
	private final Class<? extends SnomedComponentDocument> type;
	private String inactivationValueId;
	
	SnomedInactivationReasonUpdateRequest(final String referencedComponentId, final String inactivationRefSetId, final Class<? extends SnomedComponentDocument> type) {
		this.referencedComponentId = referencedComponentId;
		this.inactivationRefSetId = inactivationRefSetId;
		this.type = type;
	}

	void setInactivationValueId(final String inactivationValueId) {
		this.inactivationValueId = inactivationValueId;
	}

	@Override
	public Void execute(final TransactionContext context) {
		// Null leaves inactivation reason unchanged, empty string clears existing inactivation reason
		if (null == inactivationValueId) {
			return null;
		} else {
			updateInactivationReason(context);
			return null;
		}
	}

	private void updateInactivationReason(final TransactionContext context) {
		final SnomedComponentDocument inactivatable = context.lookup(referencedComponentId, type);
		final List<SnomedReferenceSetMember> existingMembers = newArrayList(
			SnomedRequests.prepareSearchMember()
				.all()
				.filterByReferencedComponent(referencedComponentId)
				.filterByRefSet(inactivationRefSetId)
				.build()
				.execute(context)
				.getItems()
		);
		boolean firstMemberFound = false;
		
		// Check if there is at least one existing member
		for (SnomedReferenceSetMember existingMember : existingMembers) {
			
			final SnomedRefSetMemberIndexEntry.Builder updatedMember = SnomedRefSetMemberIndexEntry.builder(existingMember);
			final SnomedRefSetMemberIndexEntry oldRevision = updatedMember.build();
			
			if (firstMemberFound) {
				// If we got through the first iteration, all other members can be removed
				removeOrDeactivate(context, inactivatable, existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());
				continue;
			}
			
			final String existingValueId = (String) existingMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID);
			if (Objects.equals(existingValueId, inactivationValueId)) {

				// Exact match, just make sure that the member is active
				ensureMemberActive(context, inactivatable, existingMember, updatedMember);

			} else if (!CLEAR.equals(inactivationValueId)) {
				// Re-use this member, if the intention was not to remove the existing value

				if (LOG.isDebugEnabled()) { 
					LOG.debug("Changing attribute-value member {} with value identifier from {} to {}.", 
							existingMember.getId(), 
							existingValueId, 
							inactivationValueId);
				}

				// Change inactivation value, set status to active if needed, place it in the supplied module
				updatedMember.field(SnomedRf2Headers.FIELD_VALUE_ID, inactivationValueId);
				ensureMemberActive(context, inactivatable, existingMember, updatedMember);
				unsetEffectiveTime(existingMember, updatedMember);
				
			} else /* if (CLEAR.equals(inactivationValueId) */ {
				// Inactivation value is set to "no reason given", so remove or inactivate the member
				// If the member needs inactivation, place it in the supplied module
				removeOrDeactivate(context, inactivatable, existingMember, updatedMember);
			}

			// If we get to the end of this loop, the first member has been processed
			context.update(oldRevision, updatedMember.build());
			// By the end of this loop, the first member has been processed
			firstMemberFound = true;
		}

		// Add the new member if the intention was not to remove the existing value (which had already happened if so)
		if (!firstMemberFound && !CLEAR.equals(inactivationValueId)) {
			SnomedComponents.newAttributeValueMember()
				.withReferencedComponent(referencedComponentId)
				.withRefSet(inactivationRefSetId)
				.withModule(context.service(ModuleIdProvider.class).apply(inactivatable))
				.withValueId(inactivationValueId)
				.addTo(context);
		}
	}

	private void ensureMemberActive(final TransactionContext context, final SnomedComponentDocument inactivatable, final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (!existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Reactivating attribute-value member {}.", existingMember.getId()); }
			existingMember.setActive(true);
			updateModule(context, existingMember, updatedMember, context.service(ModuleIdProvider.class).apply(inactivatable));
			unsetEffectiveTime(existingMember, updatedMember);
			
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Attribute-value member {} already active, not updating.", existingMember.getId()); }
		}
	}

	private void removeOrDeactivate(final TransactionContext context, final SnomedComponentDocument inactivatable, final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (!existingMember.isReleased()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Removing attribute-value member {}.", existingMember.getId()); }
			context.delete(updatedMember.build());
			
		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating attribute-value member {}.", existingMember.getId()); }
			existingMember.setActive(false);
			updateModule(context, existingMember, updatedMember, context.service(ModuleIdProvider.class).apply(inactivatable));
			unsetEffectiveTime(existingMember, updatedMember);
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Attribute-value member {} already inactive, not updating.", existingMember.getId()); }
			
		}
	}

	private void updateModule(final TransactionContext context, final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember, String moduleId) {

		if (!existingMember.getModuleId().equals(moduleId)) {
			
			if (LOG.isDebugEnabled()) { 
				LOG.debug("Changing attribute-value member {} module from {} to {}.", 
					existingMember.getId(),
					existingMember.getModuleId(),
					moduleId); 
			}
			
			updatedMember.moduleId(moduleId);
			
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Attribute-value member {} already in the expected module, not updating.", existingMember.getId()); }
		}
	}

	private void unsetEffectiveTime(final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (existingMember.getEffectiveTime() != null) {
			if (LOG.isDebugEnabled()) { LOG.debug("Unsetting effective time on attribute-value member {}.", existingMember.getId()); }
			updatedMember.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Effective time on attribute-value member {} already unset, not updating.", existingMember.getId()); }
		}
	}
}
