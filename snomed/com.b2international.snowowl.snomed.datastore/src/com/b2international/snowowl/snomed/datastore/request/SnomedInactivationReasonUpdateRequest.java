/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableList;

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
 * @param <C> the type of the component to update (must implement {@link Inactivatable} and {@link Component})
 * @since 4.5
 */
final class SnomedInactivationReasonUpdateRequest<C extends Inactivatable & Component> implements Request<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedInactivationReasonUpdateRequest.class);

	private static final String CLEAR = "";

	private final String componentId;
	private final Class<C> componentType;
	private final String inactivationRefSetId;
	
	private final Function<TransactionContext, String> referenceBranchFunction = CacheBuilder.newBuilder().build(new CacheLoader<TransactionContext, String>() {
		@Override
		public String load(final TransactionContext context) throws Exception {
			final String latestReleaseBranch = SnomedComponentUpdateRequest.getLatestReleaseBranch(context);
			if (latestReleaseBranch == null) {
				return Branch.MAIN_PATH;
			}
			return latestReleaseBranch;
		}
	});

	private String inactivationValueId;

	SnomedInactivationReasonUpdateRequest(final String componentId, final Class<C> componentType, final String inactivationRefSetId) {
		this.componentId = componentId;
		this.componentType = componentType;
		this.inactivationRefSetId = inactivationRefSetId;
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
			final Inactivatable inactivatable = context.lookup(componentId, componentType);
			updateInactivationReason(context, inactivatable);
			return null;
		}
	}

	private void updateInactivationReason(final TransactionContext context, final Inactivatable component) {
		final List<SnomedAttributeValueRefSetMember> existingMembers = ImmutableList.copyOf(component.getInactivationIndicatorRefSetMembers());
		boolean firstMemberFound = false;
		
		// Check if there is at least one existing member
		for (SnomedAttributeValueRefSetMember existingMember : existingMembers) {
			
			if (firstMemberFound) {
				// If we got through the first iteration, all other members can be removed
				removeOrDeactivate(context, existingMember);
				continue;
			}
			
			if (existingMember.getValueId().equals(inactivationValueId)) {

				// Exact match, just make sure that the member is active
				ensureMemberActive(context, existingMember);
				firstMemberFound = true;

			} else if (!CLEAR.equals(inactivationValueId)) {

				// Re-use, if the intention was not to remove the existing value
				if (LOG.isDebugEnabled()) { 
					LOG.debug("Changing attribute-value member {} with value identifier from {} to {}.", 
							existingMember.getUuid(), 
							existingMember.getValueId(), 
							inactivationValueId);
				}

				existingMember.setValueId(inactivationValueId);
				ensureMemberActive(context, existingMember);
				updateEffectiveTime(context, getLatestReleaseBranch(context), existingMember);
				
			} else /* if (CLEAR.equals(inactivationValueId) */ {
				
				// Inactivation value is "no reason given", remove this member
				removeOrDeactivate(context, existingMember);
			}

			// If we get to the end of this loop, the first member has been processed
			firstMemberFound = true;
		}

		// Add the new member if the intention was not to remove the existing value (which had already happened if so)
		if (!firstMemberFound && !CLEAR.equals(inactivationValueId)) {

			final SnomedAttributeValueRefSetMember member = SnomedComponents
					.newAttributeValueMember()
					.withReferencedComponent(componentId)
					.withRefSet(inactivationRefSetId)
					.withModule(((Component) component).getModule().getId())
					.withValueId(inactivationValueId)
					.addTo(context);

			component.getInactivationIndicatorRefSetMembers().add(member);
		}
	}

	private String getLatestReleaseBranch(final TransactionContext context) {
		final String latestVersion = referenceBranchFunction.apply(context);
		return  latestVersion == Branch.MAIN_PATH ? null : latestVersion;
	}

	private void ensureMemberActive(final TransactionContext context, final SnomedAttributeValueRefSetMember existingMember) {

		if (!existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Reactivating attribute-value member {}.", existingMember.getUuid()); }
			existingMember.setActive(true);
			updateEffectiveTime(context, getLatestReleaseBranch(context), existingMember);

		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Attribute-value member {} already active, not updating.", existingMember.getUuid()); }
		}
	}

	private void removeOrDeactivate(final TransactionContext context, final SnomedAttributeValueRefSetMember existingMember) {

		if (!existingMember.isReleased()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Removing attribute-value member {}.", existingMember.getUuid()); }
			SnomedModelExtensions.remove(existingMember);

		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating attribute-value member {}.", existingMember.getUuid()); }
			existingMember.setActive(false);
			updateEffectiveTime(context, getLatestReleaseBranch(context), existingMember);

		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Attribute-value member {} already inactive, not updating.", existingMember.getUuid()); }
		}
	}

	private void updateEffectiveTime(final TransactionContext context, final String referenceBranch, final SnomedAttributeValueRefSetMember existingMember) {

		if (existingMember.isReleased() &&  !Strings.isNullOrEmpty(referenceBranch)) {

			final SnomedReferenceSetMember referenceMember = SnomedRequests.prepareGetMember(existingMember.getUuid())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, referenceBranch)
					.execute(context.service(IEventBus.class))
					.getSync();

			boolean restoreEffectiveTime = true;
			restoreEffectiveTime = restoreEffectiveTime && existingMember.isActive() == referenceMember.isActive();
			restoreEffectiveTime = restoreEffectiveTime && existingMember.getModuleId().equals(referenceMember.getModuleId());
			restoreEffectiveTime = restoreEffectiveTime && existingMember.getValueId().equals(referenceMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));

			if (restoreEffectiveTime) {

				if (LOG.isDebugEnabled()) { 
					LOG.debug("Restoring effective time on attribute-value member {} to reference value {}.", 
							existingMember.getUuid(), 
							EffectiveTimes.format(referenceMember.getEffectiveTime(), DateFormats.SHORT));
				}

				existingMember.setEffectiveTime(referenceMember.getEffectiveTime());

			} else {
				unsetEffectiveTime(existingMember);
			}

		} else {
			unsetEffectiveTime(existingMember);
		}
	}

	private void unsetEffectiveTime(final SnomedAttributeValueRefSetMember existingMember) {

		if (existingMember.isSetEffectiveTime()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Unsetting effective time on attribute-value member {}.", existingMember.getUuid()); }
			existingMember.unsetEffectiveTime();
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Effective time on attribute-value member {} already unset, not updating.", existingMember.getUuid()); }
		}
	}
}
