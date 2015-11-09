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
package com.b2international.snowowl.snomed.exporter.model;

import java.io.DataOutputStream;
import java.io.IOException;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;

/**
 * 
 */
public class ComponentIdSnomedDsvExportItem extends AbstractSnomedDsvExportItem {

	private final long componentId;
	
	public ComponentIdSnomedDsvExportItem(final SnomedDsvExportItemType type, final long componentId) {
		super(type);
		this.componentId = componentId;
	}
	
	public long getComponentId() {
		return componentId;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.exporter.model.AbstractSnomedDsvExportItem#writeToOutputStream(java.io.DataOutputStream)
	 */
	@Override
	public void writeToOutputStream(DataOutputStream outputStream) throws IOException {
		super.writeToOutputStream(outputStream);
		outputStream.writeLong(componentId);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.exporter.model.AbstractSnomedDsvExportItem#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		// FIXME: Any way to get to the branch path here?
		return ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class)
				.getComponentLabel(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), String.valueOf(getComponentId()));
	}
}
