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
package com.b2international.snowowl.snomed.api.impl;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.impl.AbstractComponentServiceImpl;
import com.b2international.snowowl.api.impl.domain.InternalComponentRef;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.api.ISnomedComponentService;
import com.b2international.snowowl.snomed.api.domain.AssociationType;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponent;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponentInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponentUpdate;
import com.b2international.snowowl.snomed.api.impl.domain.UserIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.datastore.services.SnomedBranchRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 */
public abstract class AbstractSnomedComponentServiceImpl<C extends ISnomedComponentInput, R extends ISnomedComponent, U extends ISnomedComponentUpdate, M extends Component>
extends AbstractComponentServiceImpl<C, R, U, SnomedEditingContext, M>
implements ISnomedComponentService<C, R, U> {

	protected final SnomedConceptLookupService snomedConceptLookupService = new SnomedConceptLookupService();
	protected final SnomedRefSetLookupService snomedRefSetLookupService = new SnomedRefSetLookupService();

	protected AbstractSnomedComponentServiceImpl(final String handledRepositoryUuid, final ComponentCategory handledCategory) {
		super(handledRepositoryUuid, handledCategory);
	}

	protected AbstractSnomedRefSetMembershipLookupService getMembershipLookupService(final IBranchPath branchPath) {
		return new SnomedBranchRefSetMembershipLookupService(branchPath);
	}

	protected Concept getModuleConcept(final ISnomedComponentInput input, final SnomedEditingContext editingContext) {
		return getConcept(input.getModuleId(), editingContext);
	}

	protected Concept getConcept(final String conceptId, final SnomedEditingContext editingContext) {
		final Concept concept = snomedConceptLookupService.getComponent(conceptId, editingContext.getTransaction());
		if (null == concept) {
			throw new ComponentNotFoundException(ComponentCategory.CONCEPT, conceptId);
		}

		return concept;
	}
	
	protected SnomedStructuralRefSet getStructuralRefSet(final String refSetId, final CDOTransaction transaction) {
		final SnomedStructuralRefSet structuralRefSet = (SnomedStructuralRefSet) snomedRefSetLookupService.getComponent(refSetId, transaction);
		if (null == structuralRefSet) {
			throw new BadRequestException("Reference set with identifier %s does not exist.", refSetId);
		}

		return structuralRefSet;
	}

	@Override
	protected boolean componentExists(final C input) {
		if (input.getIdGenerationStrategy() instanceof UserIdGenerationStrategy) {
			return componentExists(createComponentRef(input, input.getIdGenerationStrategy().getId())); 
		} else {
			return false;
		}
	}

	@Override
	protected AlreadyExistsException createDuplicateComponentException(final C input) {
		// XXX: If we arrive here, the component ID must have been given by the user, any other case does not make sense
		checkState(input.getIdGenerationStrategy() instanceof UserIdGenerationStrategy);
		return new AlreadyExistsException(handledCategory.getDisplayName(), input.getIdGenerationStrategy().getId());
	}

	@Override
	protected SnomedEditingContext createEditingContext(final IComponentRef ref) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(ref, InternalComponentRef.class);
		return new SnomedEditingContext(internalRef.getBranch().branchPath());
	}

	@Override
	protected String getComponentId(final M component) {
		return component.getId();
	}

	protected boolean updateModule(final String newModuleId, final Component component, final SnomedEditingContext editingContext) {
		if (null == newModuleId) {
			return false;
		}

		final String currentModuleId = component.getModule().getId();
		if (!currentModuleId.equals(newModuleId)) {
			component.setModule(getConcept(newModuleId, editingContext));
			return true;
		} else {
			return false;
		}
	}

	protected boolean updateStatus(final Boolean newActive, final Component component, final SnomedEditingContext editingContext) {
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

	// Taken from WidgetBeanUpdater
	protected void removeOrDeactivate(final SnomedRefSetMember member) {
		if (member.isReleased()) {
			member.setActive(false);
			member.unsetEffectiveTime();
		} else {
			EcoreUtil.remove(member);
		}
	}

	protected final void updateAssociationTargets(final Multimap<AssociationType, String> newAssociationTargets, final Inactivatable component, final SnomedEditingContext editingContext) {
	
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
				removeOrDeactivate(associationMember);
			}
		}
	
		for (final Entry<AssociationType, String> newAssociationEntry : newAssociationTargetsToCreate.entries()) {
	
			final SnomedAssociationRefSetMember newAssociationMember = createAssociationRefSetMember(
					newAssociationEntry.getKey().getConceptId(), 
					newAssociationEntry.getValue(),
					((Component) component).getId(),
					editingContext);
	
			component.getAssociationRefSetMembers().add(newAssociationMember);
		}
	}
	
	// Taken from SnomedInactivationPlan
	private SnomedAssociationRefSetMember createAssociationRefSetMember(final String refSetId, final String targetId, 
			final String conceptId, final SnomedEditingContext editingContext) {

		final SnomedRefSetEditingContext refSetEditingContext = editingContext.getRefSetEditingContext();
		final SnomedStructuralRefSet associationRefSet = getStructuralRefSet(refSetId, refSetEditingContext.getTransaction());
		final String moduleId = editingContext.getDefaultModuleConcept().getId();

		return refSetEditingContext.createAssociationRefSetMember(
				SnomedRefSetEditingContext.createConceptTypePair(conceptId), 
				SnomedRefSetEditingContext.createConceptTypePair(targetId), 
				moduleId, 
				associationRefSet);
	}
}