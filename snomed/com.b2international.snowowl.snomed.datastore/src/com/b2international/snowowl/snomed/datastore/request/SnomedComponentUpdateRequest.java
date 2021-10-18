/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ComponentStatusConflictException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.core.domain.InactivationProperties;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/** 
 * @since 4.5
 */
public abstract class SnomedComponentUpdateRequest extends SnomedComponentUpdateRequestBase implements SnomedComponentRequest<Boolean> {

	private static final long serialVersionUID = 1L;

	private String moduleId;
	private Boolean active;
	private InactivationProperties inactivationProperties;
	private String effectiveTime;
	
	protected SnomedComponentUpdateRequest(String componentId) {
		super(componentId);
	}
	
	void setActive(Boolean active) {
		this.active = active;
	}
	
	void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	void setInactivationProperties(InactivationProperties inactivationProperties) {
		this.inactivationProperties = inactivationProperties;
	}
	
	void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
	protected Boolean isActive() {
		return active;
	}
	
	protected String getModuleId() {
		return moduleId;
	}
	
	protected InactivationProperties getInactivationProperties() {
		return inactivationProperties;
	}
	
	@Override
	protected final String effectiveTime() {
		return effectiveTime;
	}
	
	protected boolean updateModuleId(final TransactionContext context, final SnomedComponentDocument original, final SnomedComponentDocument.Builder<?, ?> component) {
		return updateProperty(moduleId, original::getModuleId, moduleId -> {
			component.moduleId(context.lookup(moduleId, SnomedConceptDocument.class).getId());
		});
	}

	protected boolean updateStatus(final TransactionContext context, final SnomedComponentDocument original, final SnomedComponentDocument.Builder<?, ?> component) {
		return updateProperty(active, original::isActive, component::active);
	}
	
	protected void checkUpdateOnReleased(SnomedComponentDocument component, String field, Object value) {
		if (component.isReleased()) {
			throw new BadRequestException("Cannot update '%s' to '%s' on released %s '%s'", field, value, component.getClass().getSimpleName(), component.getId());
		}
	}
	
	protected final <B extends SnomedComponentDocument.Builder<B, T>, T extends SnomedComponentDocument> boolean processInactivation(final TransactionContext context, final T component, final B updatedComponent) {
		if (null == isActive() && getInactivationProperties() == null) {
			return false;
		}
		
		final boolean currentStatus = component.isActive();
		final boolean newStatus = isActive() == null ? currentStatus : isActive();
		final String newInactivationIndicatorId = getInactivationProperties() == null || getInactivationProperties().getInactivationIndicatorId() == null ? "" : getInactivationProperties().getInactivationIndicatorId(); 
		final ImmutableMultimap.Builder<String, String> newAssociationTargets = ImmutableMultimap.builder();
		
		if (getInactivationProperties() != null && !CompareUtils.isEmpty(getInactivationProperties().getAssociationTargets())) {
			getInactivationProperties().getAssociationTargets().forEach(associationTarget -> {
				newAssociationTargets.put(associationTarget.getReferenceSetId(), associationTarget.getTargetComponentId());
			});
		}
		
		if (currentStatus && !newStatus) {
			
			// Active --> Inactive: concept inactivation, update indicator and association targets
			// (using default values if not given)
			
			inactivateComponent(context, component, updatedComponent);
			updateInactivationIndicator(context, component, newInactivationIndicatorId);
			updateAssociationTargets(context, component, newAssociationTargets.build());
			postInactivateComponent(context, component, updatedComponent);
			return true;
			
		} else if (!currentStatus && newStatus) {
			
			// Inactive --> Active: concept reactivation, clear indicator and association targets
			
			reactivateComponent(context, component, updatedComponent);
			updateInactivationIndicator(context, component, newInactivationIndicatorId);
			updateAssociationTargets(context, component, newAssociationTargets.build());
			postReactivateComponent(context, component, updatedComponent);
			return true;
			
		} else if (currentStatus == newStatus) {
			
			// Same status, allow indicator and/or association targets to be updated if required
			// (using original values that can be null)
			
			updateInactivationIndicator(context, component, getInactivationProperties() != null ? getInactivationProperties().getInactivationIndicatorId() : null);
			updateAssociationTargets(context, component, newAssociationTargets.build());
			return false;
			
		} else {
			return false;
		}
	}

	/**
	 * Subclasses may override this method to provide additional inactivation logic after inactivating the given component.
	 * @param <B>
	 * @param <T>
	 * @param context
	 * @param component
	 * @param updatedComponent
	 */
	protected <B extends SnomedComponentDocument.Builder<B, T>, T extends SnomedComponentDocument> void postInactivateComponent(TransactionContext context, T component, B updatedComponent) {
		// do nothing by default
	}

	/**
	 * Subclasses may override this method to provide additional reactivation logic after reactivating the given component.
	 * @param <B>
	 * @param <T>
	 * @param context
	 * @param component
	 * @param updatedComponent
	 */
	protected <B extends SnomedComponentDocument.Builder<B, T>, T extends SnomedComponentDocument> void postReactivateComponent(TransactionContext context, T component, B updatedComponent) {
		// do nothing by default
	}
	
	protected final void updateAssociationTargets(final TransactionContext context, SnomedComponentDocument concept, Multimap<String, String> associationTargets) {
		if (associationTargets == null) {
			return;
		}
		new SnomedAssociationTargetUpdateRequest(concept, associationTargets).execute(context);
	}

	protected final void updateInactivationIndicator(final TransactionContext context, final SnomedComponentDocument concept, final String newInactivationIndicatorId) {
		if (newInactivationIndicatorId == null) {
			return;
		}
		
		final SnomedInactivationReasonUpdateRequest inactivationUpdateRequest = new SnomedInactivationReasonUpdateRequest(concept, getInactivationIndicatorRefSetId(), false);
		inactivationUpdateRequest.setInactivationValueId(newInactivationIndicatorId);
		inactivationUpdateRequest.execute(context);
	}

	protected final <B extends SnomedComponentDocument.Builder<B, T>, T extends SnomedComponentDocument> void inactivateComponent(final TransactionContext context, final T component, final B updatedComponent) {
		if (!component.isActive()) {
			throw new ComponentStatusConflictException(component.getId(), component.isActive());
		}
		
		updatedComponent.active(false);
	}

	protected final <B extends SnomedComponentDocument.Builder<B, T>, T extends SnomedComponentDocument> void reactivateComponent(final TransactionContext context, final SnomedComponentDocument component, final B updatedConcept) {
		if (component.isActive()) {
			throw new ComponentStatusConflictException(component.getId(), component.isActive());
		}
		
		updatedConcept.active(true);
	}
	
	@JsonIgnore
	protected abstract String getInactivationIndicatorRefSetId();
	
}
