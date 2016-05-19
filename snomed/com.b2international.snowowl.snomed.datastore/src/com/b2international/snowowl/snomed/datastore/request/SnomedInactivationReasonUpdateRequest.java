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
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;

/**
 * @param <C> the type of the component to update (must implement {@link Inactivatable} and {@link Component})
 * @since 4.5
 */
public class SnomedInactivationReasonUpdateRequest<C extends Inactivatable & Component> extends BaseRequest<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedInactivationReasonUpdateRequest.class);

	private static final String CLEAR = "";

	private final String componentId;
	private final Class<C> componentType;
	private final String inactivationRefSetId;

	private final Function<TransactionContext, String> referenceBranchFunction = CacheBuilder.newBuilder().build(new CacheLoader<TransactionContext, String>() {
		@Override
		public String load(final TransactionContext context) throws Exception {
			final TerminologyRegistryService registryService = context.service(TerminologyRegistryService.class);
			final List<ICodeSystemVersion> allVersions = registryService.getAllVersion(context.id());
			final ICodeSystemVersion systemVersion = allVersions.get(0);
			final IBranchPath branchPath = ICodeSystemVersion.TO_BRANCH_PATH_FUNC.apply(systemVersion);
			return branchPath.getPath();
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
	protected Class<Void> getReturnType() {
		return Void.class;
	}

	@Override
	public Void execute(final TransactionContext context) {
		final Inactivatable inactivatable = context.lookup(componentId, componentType);
		updateInactivationReason(context, inactivatable);
		return null;
	}

	private void updateInactivationReason(final TransactionContext context, final Inactivatable component) {
		// Null leaves inactivation reason unchanged, empty string clears existing inactivation reason
		if (null == inactivationValueId) {
			return;
		}

		final List<SnomedAttributeValueRefSetMember> existingMembers = Lists.newArrayList(component.getInactivationIndicatorRefSetMembers());
		final Iterator<SnomedAttributeValueRefSetMember> memberIterator = existingMembers.iterator();

		// Check if there is at least one existing member
		if (memberIterator.hasNext()) {

			final SnomedAttributeValueRefSetMember existingMember = memberIterator.next();
			if (existingMember.getValueId().equals(inactivationValueId)) {

				// Exact match, just make sure that the member is active
				ensureMemberActive(context, existingMember);

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
			} else {
				removeOrDeactivate(context, existingMember);
			}

			// Any other members can be removed
			while (memberIterator.hasNext()) {
				removeOrDeactivate(context, memberIterator.next());
			}

		} else {

			// Add the new member if the intention was not to remove the existing value (which had already happened if so)
			if (!CLEAR.equals(inactivationValueId)) {

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
	}

	private String getLatestReleaseBranch(final TransactionContext context) {
		return referenceBranchFunction.apply(context);
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

		if (existingMember.isReleased()) {

			final SnomedReferenceSetMember referenceMember = SnomedRequests.prepareGetMember()
					.setComponentId(existingMember.getUuid())
					.build(referenceBranch)
					.executeSync(context.service(IEventBus.class));

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
