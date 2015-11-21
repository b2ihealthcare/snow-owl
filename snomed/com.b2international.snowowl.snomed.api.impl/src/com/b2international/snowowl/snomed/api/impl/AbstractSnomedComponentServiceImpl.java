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

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.server.components.AbstractComponentServiceImpl;
import com.b2international.snowowl.datastore.server.domain.InternalStorageRef;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.api.ISnomedComponentService;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.datastore.services.SnomedBranchRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

public abstract class AbstractSnomedComponentServiceImpl<R extends SnomedComponent, M extends Component>
extends AbstractComponentServiceImpl<R, SnomedEditingContext, M>
implements ISnomedComponentService<R> {

	protected final SnomedConceptLookupService snomedConceptLookupService = new SnomedConceptLookupService();
	protected final SnomedRefSetLookupService snomedRefSetLookupService = new SnomedRefSetLookupService();

	protected AbstractSnomedComponentServiceImpl(final String handledRepositoryUuid, final ComponentCategory handledCategory) {
		super(handledRepositoryUuid, handledCategory);
	}

	protected AbstractSnomedRefSetMembershipLookupService getMembershipLookupService(final IBranchPath branchPath) {
		return new SnomedBranchRefSetMembershipLookupService(branchPath);
	}

//	@Override
//	protected boolean componentExists(final C input) {
//		if (input.getIdGenerationStrategy() instanceof UserIdGenerationStrategy) {
//			return componentExists(createComponentRef(input, input.getIdGenerationStrategy().getId())); 
//		} else {
//			return false;
//		}
//	}

//	@Override
//	protected AlreadyExistsException createDuplicateComponentException(final C input) {
//		// XXX: If we arrive here, the component ID must have been given by the user, any other case does not make sense
//		checkState(input.getIdGenerationStrategy() instanceof UserIdGenerationStrategy);
//		return new AlreadyExistsException(handledCategory.getDisplayName(), input.getIdGenerationStrategy().getId());
//	}

	@Override
	protected String getComponentId(final M component) {
		return component.getId();
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
	
	protected InternalStorageRef createStorageRef(String branchPath) {
		return super.createStorageRef("SNOMEDCT", branchPath);
	}

}