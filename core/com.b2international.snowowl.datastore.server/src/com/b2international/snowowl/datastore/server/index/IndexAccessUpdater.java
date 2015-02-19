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
package com.b2international.snowowl.datastore.server.index;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.server.tasks.TaskStateManager;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;

/**
 * Default index access updates.
 */
public class IndexAccessUpdater implements IIndexAccessUpdater {

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.index.IIndexAccessUpdater#registerAccessAndRecordUsage(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public void registerAccessAndRecordUsage(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		if (ApplicationContext.getInstance().exists(ITaskStateManager.class)) {
			final String taskId = branchPath.lastSegment();
			getTaskStateManager().touch(checkNotNull(taskId, "taskId"));
		}
	}
	
	private TaskStateManager getTaskStateManager() {
		return (TaskStateManager) ApplicationContext.getInstance().getService(ITaskStateManager.class);
	}

}