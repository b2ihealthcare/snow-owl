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

import com.b2international.snowowl.datastore.tasks.ITaskContextBranchManagementPolicy;
import com.b2international.snowowl.datastore.tasks.TaskContext;
import com.b2international.snowowl.datastore.tasks.TaskContextBranchManagementPolicy;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * Abstract implementation for representing a task context for SNOMED&nbsp;CT ontology.
 */
public abstract class SnomedTaskContext extends TaskContext {

	private static final long serialVersionUID = -822165431709331358L;

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskContext#getToolingId()
	 */
	@Override
	public String getToolingId() {
		return SnomedTerminologyComponentConstants.TERMINOLOGY_ID;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskContext#getPolicy()
	 */
	@Override
	public ITaskContextBranchManagementPolicy getPolicy() {
		return new TaskContextBranchManagementPolicy(SnomedDatastoreActivator.REPOSITORY_UUID);
	}

}