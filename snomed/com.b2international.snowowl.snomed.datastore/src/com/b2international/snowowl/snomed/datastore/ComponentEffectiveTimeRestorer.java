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
package com.b2international.snowowl.snomed.datastore;

import java.util.List;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;

public abstract class ComponentEffectiveTimeRestorer implements IEffectiveTimeRestorer<Component> {
	
	@Override
	public void tryRestoreEffectiveTime(String branchPath, Component componentToRestore) {
		final List<String> branchesForPreviousVersion = getAvailableVersionPaths(branchPath);
		SnomedComponent previousVersion = null;

		for (String branch : branchesForPreviousVersion) {

			try {

				previousVersion = getVersionedComponent(branch, componentToRestore.getId());

				if (previousVersion != null) {
					break;
				}

			} catch (NotFoundException e) {
				// check next available branch if possible
			}

		}
		
		if (previousVersion == null) {
			throw new IllegalStateException("Previous version of released component could not be found. ID: " + componentToRestore.getId() + ", branch: " + branchPath);
		} else {
			boolean canRestore = false;
			canRestore |= componentToRestore.isActive() ^ previousVersion.isActive();
			canRestore |= componentToRestore.getModule().getId().equals(previousVersion.getModuleId());

			if (canRestore && canRestoreComponentEffectiveTime(componentToRestore, previousVersion)) {
				componentToRestore.setEffectiveTime(previousVersion.getEffectiveTime());
			}

		}
	}
	
	/**
	 * Method to check specific properties on component types eg.: ({@link Relationship}, {@link Description}, {@link Concept}).
	 * 
	 * @param componentToRestore
	 * @param previousVersion the latest released version of the component above.
	 * @return
	 */
	protected abstract boolean canRestoreComponentEffectiveTime(Component componentToRestore, SnomedComponent previousVersion);

	protected abstract SnomedComponent getVersionedComponent(String branch, String componentId);
	
	protected IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
}
