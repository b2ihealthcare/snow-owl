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
package com.b2international.snowowl.datastore;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;

import org.eclipse.core.expressions.PropertyTester;

import com.b2international.snowowl.datastore.tasks.TaskManager;

/**
 * Property tester checking if any of the task is currently active or not. 
 * Returns {@code true} only and if only {@link TaskManager} is available and there are no active tasks.
 *
 */
public class ActiveBranchPropertyTester extends PropertyTester {

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		final TaskManager taskManager = getServiceForClass(TaskManager.class);
		return null != taskManager && null == taskManager.getActiveTask();
	}

}