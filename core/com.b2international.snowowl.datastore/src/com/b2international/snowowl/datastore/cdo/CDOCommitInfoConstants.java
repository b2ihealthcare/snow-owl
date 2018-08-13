/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.cdo;

import java.util.regex.Pattern;

/**
 * Constants for CDO commit info objects.
 * @deprecated - CDO has been removed in 7.x, this class is only used to migrate pre-7.x databases to the new version
 */
public abstract class CDOCommitInfoConstants {

	/**Initializer commit comment. {@value}*/ 
	public static final String INITIALIZER_COMMIT_COMMENT = "<initialize>";
	
	/**System user ID. {@value}*/
	public static final String SYSTEM_USER_ID = "CDO_SYSTEM";
	
	/**{@link Pattern Pattern} for a UUID.*/
	public static final Pattern UUID_PATETRN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
	
	private CDOCommitInfoConstants() { /*suppress instantiation*/ }
	
}