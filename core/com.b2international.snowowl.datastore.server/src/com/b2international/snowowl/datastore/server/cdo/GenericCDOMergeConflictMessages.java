/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.cdo;

/**
 * @since 4.7
 */
public class GenericCDOMergeConflictMessages {

	public static final String CHANGED_IN_SOURCE_AND_TARGET_MESSAGE = "%s was changed both in source and target branch.";
	public static final String CHANGED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE = "%s was changed in source branch but detached in target branch.";
	public static final String CHANGED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE = "%s was changed in target branch but detached in source branch.";
	public static final String ADDED_IN_SOURCE_AND_TARGET_SOURCE_MESSAGE = "%s with ID '%s' was introduced on source branch and has ID conflict on target branch.";
	public static final String ADDED_IN_SOURCE_AND_TARGET_TARGET_MESSAGE = "%s with ID '%s' was introduced on target branch and has ID conflict on source branch.";
	public static final String ADDED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE = "%s with ID '%s' added on source branch referencing %s with ID '%s' which was detached on target branch.";
	public static final String ADDED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE = "%s with ID '%s' added on target branch referencing %s with ID '%s' which was detached on source branch.";

	private GenericCDOMergeConflictMessages() { /* prevent instantiation */ }
}
