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

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
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
 * @param <C> the type of the component to update (must implement {@link Inactivatable} and {@link Component})
 * @since 4.5
 */
final class SnomedAssociationTargetUpdateRequest<C extends Inactivatable & Component> implements Request<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedAssociationTargetUpdateRequest.class);

	private final String componentId;
	private final Class<C> componentType;
	
	private Multimap<AssociationType, String> newAssociationTargets;

	SnomedAssociationTargetUpdateRequest(final String componentId, final Class<C> componentType) {
		this.componentId = componentId;
		this.componentType = componentType;
	}

	void setNewAssociationTargets(final Multimap<AssociationType, String> newAssociationTargets) {
		this.newAssociationTargets = newAssociationTargets;
	}

	@Override
	public Void execute(final TransactionContext context) {
		// Null leaves targets unchanged, empty map clears all targets
		if (null == newAssociationTargets) {
			return null;
		} else {
			final C inactivatable = context.lookup(componentId, componentType);
			updateAssociationTargets(context, inactivatable);
			return null;
		}
	}

	private void updateAssociationTargets(final TransactionContext context, final C component) {
		final List<SnomedAssociationRefSetMember> existingMembers = Lists.newArrayList(component.getAssociationRefSetMembers());
		final Multimap<AssociationType, String> newAssociationTargetsToCreate = HashMultimap.create(newAssociationTargets);
		final ModuleIdProvider moduleIdFunction = context.service(ModuleIdProvider.class);
		
		final Iterator<SnomedAssociationRefSetMember> memberIterator = existingMembers.iterator();
		while (memberIterator.hasNext()) {
			
			final SnomedAssociationRefSetMember existingMember = memberIterator.next();
			final AssociationType associationType = AssociationType.getByConceptId(existingMember.getRefSetIdentifierId());
			if (null == associationType) {
				continue;
			}

			final String existingTargetId = existingMember.getTargetComponentId();
			if (newAssociationTargetsToCreate.remove(associationType, existingTargetId)) {
				// Exact match: make sure that the member is active
				
				final boolean changed = ensureMemberActive(context, existingMember);
				// If the member status needs to be changed back to active, place it in the supplied module
				if (changed) {
					updateModule(context, existingMember, moduleIdFunction.apply(component));
					unsetEffectiveTime(existingMember);
				}
				
				// Remove it from the working list, as we have found a match
				memberIterator.remove();
			}
		}

		/* 
		 * Remaining entries in the multimap have no exact match, but there might be other members with the 
		 * same association type; attempt re-using them, changing the association target in the process 
		 */
		for (final SnomedAssociationRefSetMember existingMember : existingMembers) {
			
			final AssociationType associationType = AssociationType.getByConceptId(existingMember.getRefSetIdentifierId());
			if (null == associationType) {
				continue;
			}
			
			if (newAssociationTargetsToCreate.containsKey(associationType)) {
				// We can re-use the member by changing the target component identifier, and checking that it is active

				final Iterator<String> targetIterator = newAssociationTargetsToCreate.get(associationType).iterator();
				final String newTargetId = targetIterator.next();
				targetIterator.remove();

				if (LOG.isDebugEnabled()) { 
					LOG.debug("Changing association member {} with type {} and target component identifier from {} to {}.", 
							existingMember.getUuid(), 
							associationType, 
							existingMember.getTargetComponentId(), 
							newTargetId);
				}

				// Change target component, set status to active if needed, place it in the supplied module
				existingMember.setTargetComponentId(newTargetId);
				ensureMemberActive(context, existingMember);
				updateModule(context, existingMember, moduleIdFunction.apply(component));
				unsetEffectiveTime(existingMember);

			} else {
				// We have no use for this member -- remove or inactivate if already released
				
				final boolean changed = removeOrDeactivate(context, existingMember);
				// If the member needs inactivation, place it in the supplied module
				if (changed) {
					updateModule(context, existingMember, moduleIdFunction.apply(component));
					unsetEffectiveTime(existingMember); 
				}
			}
		}

		/* 
		 * With all existing members processed, the last set of entries in the multimap will need 
		 * to be added as new members; defaultModuleId is only used if there is at least a single
		 * new entry. 
		 */
		for (final Entry<AssociationType, String> newAssociationEntry : newAssociationTargetsToCreate.entries()) {
			final SnomedAssociationRefSetMember member = SnomedComponents
					.newAssociationMember()
					.withRefSet(newAssociationEntry.getKey().getConceptId())
					.withTargetComponentId(newAssociationEntry.getValue())
					.withReferencedComponent(component.getId())
					.withModule(moduleIdFunction.apply(component))
					.addTo(context);

			component.getAssociationRefSetMembers().add(member);
		}
	}

	private boolean ensureMemberActive(final TransactionContext context, final SnomedAssociationRefSetMember existingMember) {
		
		if (!existingMember.isActive()) {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Reactivating association member {}.", existingMember.getUuid()); }
			existingMember.setActive(true);
			return true;
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Association member {} already active, not updating.", existingMember.getUuid()); }
			return false;
		}
	}

	private boolean removeOrDeactivate(final TransactionContext context, final SnomedAssociationRefSetMember existingMember) {
		
		if (!existingMember.isReleased()) {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Removing association member {}.", existingMember.getUuid()); }
			SnomedModelExtensions.remove(existingMember);
			return false;

		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating association member {}.", existingMember.getUuid()); }
			existingMember.setActive(false);
			return true;
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Association member {} is released and already inactive, not updating.", existingMember.getUuid()); }
			return false;
		}
	}

	private boolean updateModule(final TransactionContext context, final SnomedAssociationRefSetMember existingMember, String moduleId) {

		if (!existingMember.getModuleId().equals(moduleId)) {
			
			if (LOG.isDebugEnabled()) { 
				LOG.debug("Changing association member {} module from {} to {}.", 
					existingMember.getUuid(),
					existingMember.getModuleId(),
					moduleId); 
			}
			
			existingMember.setModuleId(moduleId);
			return true;
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Association member {} already in the expected module, not updating.", existingMember.getUuid()); }
			return false;
		}
	}
	
	private void unsetEffectiveTime(final SnomedAssociationRefSetMember existingMember) {
		
		if (existingMember.isSetEffectiveTime()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Unsetting effective time on association member {}.", existingMember.getUuid()); }
			existingMember.unsetEffectiveTime();
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Effective time on association member {} already unset, not updating.", existingMember.getUuid()); }
		}
	}
}
