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

/**
 * This class holds constants for remote task notifications.
 *
 */
public class TaskChangeTrigger {

	public static final String CONTEXT_CHANGED = "CONTEXT";

	public static final String TASK_ATTRIBUTE_CHANGED = "ATTRIBUTE";

	public static final String ATTACHMENT_OBSOLATION_CHANGED = "ATTACHMENT_OBSOLATE_STATE";

	public static final String AUTOMAP_FILE_SAVE = "AUTOMAP_SAVED";
}