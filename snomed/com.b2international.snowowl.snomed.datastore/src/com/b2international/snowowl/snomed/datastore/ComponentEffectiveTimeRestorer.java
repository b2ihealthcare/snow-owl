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

import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.snomed.Component;

public abstract class ComponentEffectiveTimeRestorer implements IEffectiveTimeRestorer<Component> {
	
	@Override
	public void tryRestoreEffectiveTime(String branchPath, Component componentToRestore) {
		final List<String> branchesForPreviousVersion = getAvailableVersionPaths(branchPath);
		Component previousVersion = null;

		for (String branch : branchesForPreviousVersion) {

			try {

				previousVersion = getVersionedComponent(branch);

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
			if (componentToRestore.isActive() && previousVersion.isActive()) {
				canRestore = true;
			}
			
			if (componentToRestore.getModule().getId().equals(previousVersion.getModule().getId())) {
				canRestore = true;
			}

			if (canRestore && canRestoreComponentEffectiveTime(componentToRestore, previousVersion)) {
				componentToRestore.setEffectiveTime(previousVersion.getEffectiveTime());
			}

		}
	}
	
	protected abstract boolean canRestoreComponentEffectiveTime(Component componentToRestore, Component previousVersion);

	protected abstract Component getVersionedComponent(String branch);
}
