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
final class SnomedInactivationReasonUpdateRequest extends BaseComponentMemberUpdateRequest {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedInactivationReasonUpdateRequest.class);

	private static final String CLEAR = "";

	private final String inactivationRefSetId;
	private String inactivationValueId;

	
	SnomedInactivationReasonUpdateRequest(final SnomedComponentDocument componentToUpdate, final String inactivationRefSetId) {
		super(componentToUpdate);
		this.inactivationRefSetId = inactivationRefSetId;
	}

	void setInactivationValueId(final String inactivationValueId) {
		this.inactivationValueId = inactivationValueId;
	}

	@Override
	protected boolean canUpdate(TransactionContext context) {
		// Null leaves inactivation reason unchanged, empty string clears existing inactivation reason
		return inactivationValueId != null;
	}
	
	@Override
	protected String getMemberType() {
		return "Attribute-value";
	}
	
	@Override
	protected void doExecute(TransactionContext context, SnomedComponentDocument componentToUpdate) {
		final List<SnomedReferenceSetMember> existingMembers = newArrayList(
			SnomedRequests.prepareSearchMember()
				.all()
				.filterByReferencedComponent(componentToUpdate.getId())
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
				removeOrDeactivate(context, existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());
				continue;
			}
			
			final String existingValueId = (String) existingMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID);
			if (Objects.equals(existingValueId, inactivationValueId)) {

				// Exact match, just make sure that the member is active
				ensureMemberActive(context, existingMember, updatedMember);

			} else if (!CLEAR.equals(inactivationValueId)) {
				// Re-use this member, if the intention was not to remove the existing value

				if (LOG.isDebugEnabled()) { 
					LOG.debug("Changing attribute-value member {} with value identifier from {} to {}.", 
							existingMember.getId(), 
							existingValueId, 
							inactivationValueId);
				}

				// Change inactivation value, set status to active if needed, place it in the supplied module
				ensureMemberActive(context, existingMember, updatedMember);
				updatedMember.field(SnomedRf2Headers.FIELD_VALUE_ID, inactivationValueId);
				updateModule(context, existingMember, updatedMember, context.service(ModuleIdProvider.class).apply(componentToUpdate));
				unsetEffectiveTime(existingMember, updatedMember);
				
			} else /* if (CLEAR.equals(inactivationValueId) */ {
				// Inactivation value is set to "no reason given", so remove or inactivate the member
				// If the member needs inactivation, place it in the supplied module
				removeOrDeactivate(context, existingMember, updatedMember);
			}

			// If we get to the end of this loop, the first member has been processed
			context.update(oldRevision, updatedMember.build());
			// By the end of this loop, the first member has been processed
			firstMemberFound = true;
		}

		// Add the new member if the intention was not to remove the existing value (which had already happened if so)
		if (!firstMemberFound && !CLEAR.equals(inactivationValueId)) {
			SnomedComponents.newAttributeValueMember()
				.withReferencedComponent(componentToUpdate.getId())
				.withRefSet(inactivationRefSetId)
				.withModule(context.service(ModuleIdProvider.class).apply(componentToUpdate))
				.withValueId(inactivationValueId)
				.addTo(context);
		}
	}
	
}
