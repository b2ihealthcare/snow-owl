/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;
import java.util.Map.Entry;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/** 
 * @since 4.5
 * @param <B>
 */
public abstract class BaseSnomedComponentUpdateRequest extends BaseRequest<TransactionContext, Void> {

	private final String componentId;
	
	private String moduleId;
	private Boolean active;
	
	protected BaseSnomedComponentUpdateRequest(String componentId) {
		this.componentId = componentId;
	}
	
	void setActive(Boolean active) {
		this.active = active;
	}
	
	void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	/**
	 * @deprecated - visibility will be reduced to protected in 4.6
	 * @return
	 */
	public Boolean isActive() {
		return active;
	}
	
	protected String getModuleId() {
		return moduleId;
	}
	
	protected String getComponentId() {
		return componentId;
	}
	
	@Override
	protected final Class<Void> getReturnType() {
		return Void.class;
	}
	
	protected boolean updateModule(final TransactionContext context, final Component component, final String newModuleId) {
		if (null == newModuleId) {
			return false;
		}

		final String currentModuleId = component.getModule().getId();
		if (!currentModuleId.equals(newModuleId)) {
			component.setModule(context.lookup(newModuleId, Concept.class));
			return true;
		} else {
			return false;
		}
	}

	protected boolean updateStatus(final TransactionContext context, final Component component, final Boolean newActive) {
		if (null == newActive) {
			return false;
		}

		if (component.isActive() != newActive) {
			component.setActive(newActive);
			return true;
		} else {
			return false;
		}
	}
	
	protected final void updateAssociationTargets(TransactionContext context, final Inactivatable component, final Multimap<AssociationType, String> newAssociationTargets) {
		
		if (null == newAssociationTargets) {
			return;
		}
		
		if (!(component instanceof Component)) {
			throw new IllegalArgumentException("Only concepts and descriptions can  can be inactivated");
		}
	
		final List<SnomedAssociationRefSetMember> associationMembers = ImmutableList.copyOf(component.getAssociationRefSetMembers());
		final Multimap<AssociationType, String> newAssociationTargetsToCreate = HashMultimap.create(newAssociationTargets);
	
		for (final SnomedAssociationRefSetMember associationMember : associationMembers) {
			if (!associationMember.isActive()) {
				continue;
			}
	
			final AssociationType type = AssociationType.getByConceptId(associationMember.getRefSetIdentifierId());
			if (null == type) {
				continue;
			}
	
			final String targetId = associationMember.getTargetComponentId();
			if (newAssociationTargets.containsEntry(type, targetId)) {
				newAssociationTargetsToCreate.remove(type, targetId);
			} else {
				SnomedModelExtensions.removeOrDeactivate(associationMember);
			}
		}
	
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
	
}
