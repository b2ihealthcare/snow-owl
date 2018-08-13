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
package com.b2international.snowowl.datastore.importer;

import java.util.Collection;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Abstract exporter to export a set of terminology components.
 * 
 * @since 3.3
 */
public abstract class AbstractFilteredComponentsTerminologyExporter extends AbstractTerminologyExporter {

	private final Collection<String> componentIds;

	public AbstractFilteredComponentsTerminologyExporter(final String userId, final IBranchPath branchPath, final Collection<String> componentIds) {
		super(userId, branchPath);
		this.componentIds = componentIds;
	}
	
	/**
	 * Tells if a component needs to be exported based on its component ID.
	 * 
	 * @param componentId the unique ID of the component.
	 * @return <code>true</code> if the component needs to be exported.
	 */
	protected boolean isToExport(final String componentId) {
		return componentIds.contains(componentId);
	}

	protected Collection<String> getComponentIds() {
		return componentIds;
	}

}