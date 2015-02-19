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
package com.b2international.snowowl.datastore.tasks;

import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.editor.notification.NotificationMessage;

/**
 * A {@link NotificationMessage} sent when a task has been left idle for too long and its associate index folders are removed to save space.
 */
public class TaskHibernatedNotificationMessage extends NotificationMessage<String> implements ITaskNotificationMessage {

	private static final long serialVersionUID = 1L;
	
	public TaskHibernatedNotificationMessage(String taskId, String message) {
		super(taskId, message);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskNotificationMessage#getTaskId()
	 */
	@Override
	public String getTaskId() {
		return getData();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskNotificationMessage#getClosingUserId()
	 */
	@Override
	public String getInitiatingUserId() {
		// TODO: the system itself is responsible for hibernated tasks -- does this make sense?
		return SpecialUserStore.SYSTEM_USER_NAME;
	}
}