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
package com.b2international.snowowl.snomed.datastore;

import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.api.ILookupServiceProvider;
import com.b2international.snowowl.core.api.ITerminologyComponentIdProvider;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

/**
 * Task context for authoring SNOMED&nbsp;CT reference sets.
 *
 */
public class SnomedRefSetAuthoringTaskContext extends SnomedTaskContext implements ILookupServiceProvider, ITerminologyComponentIdProvider {

	private static final long serialVersionUID = 2082662996714848960L;

	/**Unique task context identifier.*/
	public static final String ID = "com.b2international.snowowl.snomed.datastore.SnomedRefSetAuthoringTaskContext";
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskContext#getLabel()
	 */
	@Override
	public String getLabel() {
		return "SNOMED CT reference set authoring";
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskContext#getContextId()
	 */
	@Override
	public String getContextId() {
		return ID;
	}

	@Override
	public boolean isComponentScoped() {
		return true;
	}

	@Override
	public ILookupService<String, SnomedRefSet, CDOView> getLookupService() {
		return new SnomedRefSetLookupService();
	}

	@Override
	public EPackage getEPackage() {
		return SnomedRefSetPackage.eINSTANCE;
	}

	@Override
	public boolean isMetadataPropertyProvider() {
		return false;
	}

	@Override
	public String getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.REFSET;
	}
}