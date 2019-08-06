/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;

/**
 * @since 7.1
 */
abstract class BaseComponentMemberUpdateRequest implements Request<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(BaseComponentMemberUpdateRequest.class);
	private final SnomedComponentDocument componentToUpdate;

	public BaseComponentMemberUpdateRequest(SnomedComponentDocument componentToUpdate) {
		this.componentToUpdate = componentToUpdate;
	}
	
	@Override
	public Void execute(TransactionContext context) {
		if (canUpdate(context)) {
			doExecute(context, componentToUpdate);
		}
		return null;
	}

	protected abstract void doExecute(TransactionContext context, SnomedComponentDocument componentToUpdate);

	protected abstract boolean canUpdate(TransactionContext context);
	
	protected abstract String getMemberType();

	protected final void ensureMemberActive(final TransactionContext context, final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (!existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Reactivating {} member {}.", getMemberType(), existingMember.getId()); }
			updatedMember.active(true);
			updateModule(context, existingMember, updatedMember, context.service(ModuleIdProvider.class).apply(componentToUpdate));
			unsetEffectiveTime(existingMember, updatedMember);
			
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("{} member {} already active, not updating.", getMemberType(), existingMember.getId()); }
		}
	}

	protected final void removeOrDeactivate(final TransactionContext context, final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (!existingMember.isReleased()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Removing {} member {}.", getMemberType(), existingMember.getId()); }
			context.delete(updatedMember.build());
			
		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating {} member {}.", getMemberType(), existingMember.getId()); }
			updatedMember.active(true);
			updateModule(context, existingMember, updatedMember, context.service(ModuleIdProvider.class).apply(componentToUpdate));
			unsetEffectiveTime(existingMember, updatedMember);
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("{} member {} already inactive, not updating.", getMemberType(), existingMember.getId()); }
			
		}
	}

	protected final void updateModule(final TransactionContext context, final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember, String moduleId) {

		if (!existingMember.getModuleId().equals(moduleId)) {
			
			if (LOG.isDebugEnabled()) { 
				LOG.debug("Changing {} member {} module from {} to {}.",
					getMemberType(),
					existingMember.getId(),
					existingMember.getModuleId(),
					moduleId); 
			}
			
			updatedMember.moduleId(moduleId);
			
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("{} member {} already in the expected module, not updating.", getMemberType(), existingMember.getId()); }
		}
	}

	protected final void unsetEffectiveTime(final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (existingMember.getEffectiveTime() != null) {
			if (LOG.isDebugEnabled()) { LOG.debug("Unsetting effective time on {} member {}.", getMemberType(), existingMember.getId()); }
			updatedMember.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Effective time on {} member {} already unset, not updating.", getMemberType(), existingMember.getId()); }
		}
	}
	
}
