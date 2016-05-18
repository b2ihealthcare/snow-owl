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
package com.b2international.snowowl.snomed.datastore.server.request;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Updates association reference set members on the {@link Inactivatable} specified by identifier.
 * <p>
 * Existing members are <b>removed</b> when:
 * <ul>
 * <li>The member is unreleased;
 * <li>The candidate association target map does not include the member by reference set ID (so it can not be re-used 
 * with by setting a different target).
 * </ul>
 * <p>
 * Existing members are <b>inactivated</b> when:
 * <ul>
 * <li>The member is already part of an RF2 version and has been released;
 * <li>The candidate association target map does not include the member's reference set ID and target component ID.
 * </ul>
 * <p>
 * Existing members are <b>updated</b> with a new target component identifier when:
 * <ul>
 * <li>The member's reference set ID matches at least one entry in the association target map, but no exactly matching member exists
 * 
 * <p>
 * New members are <b>created</b> when:
 * <ul>
 * <li>All existing association reference set members have been visited;
 * <li>Entries are still present in the candidate association target map.
 * </ul>
 * <p>
 * The candidate map starts as a copy of the input map, and is trimmed gradually, whenever an existing reference set 
 * member is found that matches any entry in the map.
 * <p>
 * Existing released members are compared to their versioned reference representation, and their effective time is 
 * restored to the original value if the final state matches the versioned form. 
 * 
 * @param <C> the type of the component to update (must implement {@link Inactivatable} and {@link Component})
 * @since 4.5
 */
public class SnomedAssociationTargetUpdateRequest<C extends Inactivatable & Component> extends BaseRequest<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedAssociationTargetUpdateRequest.class);

	private final String componentId;
	private final Class<C> componentType;
	
	private final Function<TransactionContext, String> referenceBranchFunction = CacheBuilder.newBuilder().build(new CacheLoader<TransactionContext, String>() {
		@Override
		public String load(TransactionContext context) throws Exception {
			final TerminologyRegistryService registryService = context.service(TerminologyRegistryService.class);
			final List<ICodeSystemVersion> allVersions = registryService.getAllVersion(context.id());
			final ICodeSystemVersion systemVersion = allVersions.get(0);
			final IBranchPath branchPath = ICodeSystemVersion.TO_BRANCH_PATH_FUNC.apply(systemVersion);
			return branchPath.getPath();
		}
	});
	
	private Multimap<AssociationType, String> newAssociationTargets;

	SnomedAssociationTargetUpdateRequest(final String componentId, final Class<C> componentType) {
		this.componentId = componentId;
		this.componentType = componentType;
	}

	void setNewAssociationTargets(final Multimap<AssociationType, String> newAssociationTargets) {
		this.newAssociationTargets = newAssociationTargets;
	}

	@Override
	protected Class<Void> getReturnType() {
		return Void.class;
	}

	@Override
	public Void execute(final TransactionContext context) {
		final Inactivatable inactivatable = context.lookup(componentId, componentType);
		updateAssociationTargets(context, inactivatable);
		return null;
	}

	private void updateAssociationTargets(final TransactionContext context, final Inactivatable component) {
		// Null leaves targets unchanged, empty map clears all targets
		if (null == newAssociationTargets) {
			return;
		}

		if (!(component instanceof Component)) {
			throw new IllegalArgumentException(String.format("Specified component must be an instance of Component, was %s.", component.getClass().getSimpleName()));
		}

		final List<SnomedAssociationRefSetMember> existingMembers = Lists.newArrayList(component.getAssociationRefSetMembers());
		final Multimap<AssociationType, String> newAssociationTargetsToCreate = HashMultimap.create(newAssociationTargets);

		// Check if there are existing exact matches
		Iterator<SnomedAssociationRefSetMember> memberIterator = existingMembers.iterator();
		while (memberIterator.hasNext()) {
			
			final SnomedAssociationRefSetMember existingMember = memberIterator.next();
			final AssociationType associationType = AssociationType.getByConceptId(existingMember.getRefSetIdentifierId());
			
			if (null == associationType) {
				continue;
			}

			final String existingTargetId = existingMember.getTargetComponentId();
			if (newAssociationTargetsToCreate.remove(associationType, existingTargetId)) {
				// Exact match, just make sure that the member is active
				ensureMemberActive(context, existingMember);
				memberIterator.remove();
			}
		}
		
		// Check if any remaining existing members should be re-used and/or restored to active status
		for (SnomedAssociationRefSetMember existingMember : existingMembers) {
			
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

				existingMember.setTargetComponentId(newTargetId);
				ensureMemberActive(context, existingMember);
				memberIterator.remove();

			} else {
				
				// We have no use for this member -- remove or inactivate if released
				removeOrDeactivate(context, existingMember);
			}
		}

		// With all existing members processed, any remaining entries in the multimap will need to be added as members
		for (final Entry<AssociationType, String> newAssociationEntry : newAssociationTargetsToCreate.entries()) {
			
			final SnomedAssociationRefSetMember member = SnomedComponents
					.newAssociationMember()
					.withRefSet(newAssociationEntry.getKey().getConceptId())
					.withTargetComponentId(newAssociationEntry.getValue())
					.withReferencedComponent(((Component) component).getId())
					.withModule(((Component) component).getModule().getId())
					.addTo(context);

			component.getAssociationRefSetMembers().add(member);
		}
	}

	private String getLatestReleaseBranch(final TransactionContext context) {
		return referenceBranchFunction.apply(context);
	}

	private void ensureMemberActive(final TransactionContext context, final SnomedAssociationRefSetMember existingMember) {
		
		if (!existingMember.isActive()) {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Reactivating association member {}.", existingMember.getUuid()); }
			existingMember.setActive(true);
			updateEffectiveTime(context, getLatestReleaseBranch(context), existingMember);
			
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Association member {} already active, not updating.", existingMember.getUuid()); }
		}
	}

	private void removeOrDeactivate(final TransactionContext context, final SnomedAssociationRefSetMember existingMember) {
		
		if (!existingMember.isReleased()) {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Removing association member {}.", existingMember.getUuid()); }
			SnomedModelExtensions.remove(existingMember);

		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating association member {}.", existingMember.getUuid()); }
			existingMember.setActive(false);
			updateEffectiveTime(context, getLatestReleaseBranch(context), existingMember);
			
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Association member {} already inactive, not updating.", existingMember.getUuid()); }
		}
	}

	private void updateEffectiveTime(final TransactionContext context, final String referenceBranch, final SnomedAssociationRefSetMember existingMember) {
		
		if (existingMember.isReleased()) {
			
			final SnomedReferenceSetMember referenceMember = SnomedRequests.prepareGetMember()
					.setComponentId(existingMember.getUuid())
					.build(referenceBranch)
					.executeSync(context.service(IEventBus.class));

			boolean restoreEffectiveTime = true;
			restoreEffectiveTime = restoreEffectiveTime && existingMember.isActive() == referenceMember.isActive();
			restoreEffectiveTime = restoreEffectiveTime && existingMember.getModuleId().equals(referenceMember.getModuleId());
			restoreEffectiveTime = restoreEffectiveTime && existingMember.getTargetComponentId().equals(referenceMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID));

			if (restoreEffectiveTime) {

				if (LOG.isDebugEnabled()) { 
					LOG.debug("Restoring effective time on association member {} to reference value {}.", 
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
	
	private void unsetEffectiveTime(SnomedAssociationRefSetMember existingMember) {
		
		if (existingMember.isSetEffectiveTime()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Unsetting effective time on association member {}.", existingMember.getUuid()); }
			existingMember.unsetEffectiveTime();
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Effective time on association member {} already unset, not updating.", existingMember.getUuid()); }
		}
	}
}
