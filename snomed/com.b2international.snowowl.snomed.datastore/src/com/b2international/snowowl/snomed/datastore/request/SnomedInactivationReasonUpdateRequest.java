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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdFunction;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
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
			final C inactivatable = context.lookup(componentId, componentType);
			updateInactivationReason(context, inactivatable);
			return null;
		}
	}

	private void updateInactivationReason(final TransactionContext context, final C component) {
		final List<SnomedAttributeValueRefSetMember> existingMembers = ImmutableList.copyOf(component.getInactivationIndicatorRefSetMembers());
		final ModuleIdFunction moduleIdSupplier = context.service(ModuleIdFunction.class);
		boolean firstMemberProcessed = false;
		
		// Check if there is at least one existing member
		for (SnomedAttributeValueRefSetMember existingMember : existingMembers) {
			
			if (firstMemberProcessed) {
				// If we got through the first iteration, any remaining members can be removed or deactivated
				
				final boolean changed = removeOrDeactivate(context, existingMember);
				// If the member needs inactivation, place it in the supplied module
				if (changed) {
					updateModule(context, existingMember, moduleIdSupplier.apply(component));
					unsetEffectiveTime(existingMember);
				}
				
			} else if (existingMember.getValueId().equals(inactivationValueId)) {
				// Exact match, just make sure that the member is active

				final boolean changed = ensureMemberActive(context, existingMember);
				// If the member status needs to be changed back to active, place it in the supplied module
				if (changed) {
					updateModule(context, existingMember, moduleIdSupplier.apply(component));
					unsetEffectiveTime(existingMember);
				}

			} else if (!CLEAR.equals(inactivationValueId)) {
				// Re-use this member, if the intention was not to remove the existing value

				if (LOG.isDebugEnabled()) { 
					LOG.debug("Changing attribute-value member {} with value identifier from {} to {}.", 
							existingMember.getUuid(), 
							existingMember.getValueId(), 
							inactivationValueId);
				}

				// Change inactivation value, set status to active if needed, place it in the supplied module
				existingMember.setValueId(inactivationValueId);
				ensureMemberActive(context, existingMember);
				updateModule(context, existingMember, moduleIdSupplier.apply(component));
				unsetEffectiveTime(existingMember);
				
			} else /* if (CLEAR.equals(inactivationValueId) */ {
				// Inactivation value is set to "no reason given", so remove or inactivate the member
				
				final boolean changed = removeOrDeactivate(context, existingMember);
				// If the member needs inactivation, place it in the supplied module
				if (changed) {
					updateModule(context, existingMember, moduleIdSupplier.apply(component));
					unsetEffectiveTime(existingMember);
				}
			}

			// By the end of this loop, the first member has been processed
			firstMemberProcessed = true;
		}

		// If there weren't any members, and the value is not "no reason given", add a new member
		if (!firstMemberProcessed && !CLEAR.equals(inactivationValueId)) {

			final SnomedAttributeValueRefSetMember member = SnomedComponents.newAttributeValueMember()
					.withReferencedComponent(componentId)
					.withRefSet(inactivationRefSetId)
					.withModule(moduleIdSupplier.apply(component))
					.withValueId(inactivationValueId)
					.addTo(context);

			component.getInactivationIndicatorRefSetMembers().add(member);
		}
	}

	private boolean ensureMemberActive(final TransactionContext context, final SnomedAttributeValueRefSetMember existingMember) {

		if (!existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Reactivating attribute-value member {}.", existingMember.getUuid()); }
			existingMember.setActive(true);
			return true;

		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Attribute-value member {} already active, not updating.", existingMember.getUuid()); }
			return false;
		}
	}

	private boolean removeOrDeactivate(final TransactionContext context, final SnomedAttributeValueRefSetMember existingMember) {

		if (!existingMember.isReleased()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Removing attribute-value member {}.", existingMember.getUuid()); }
			SnomedModelExtensions.remove(existingMember);
			return false;

		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating attribute-value member {}.", existingMember.getUuid()); }
			existingMember.setActive(false);
			return true;

		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Attribute-value member {} already inactive, not updating.", existingMember.getUuid()); }
			return false;
		}
	}

	private boolean updateModule(final TransactionContext context, final SnomedAttributeValueRefSetMember existingMember, String moduleId) {

		if (!existingMember.getModuleId().equals(moduleId)) {
			
			if (LOG.isDebugEnabled()) { 
				LOG.debug("Changing attribute-value member {} module from {} to {}.", 
					existingMember.getUuid(),
					existingMember.getModuleId(),
					moduleId); 
			}
			
			existingMember.setModuleId(moduleId);
			return true;
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Attribute-value member {} already in the expected module, not updating.", existingMember.getUuid()); }
			return false;
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
