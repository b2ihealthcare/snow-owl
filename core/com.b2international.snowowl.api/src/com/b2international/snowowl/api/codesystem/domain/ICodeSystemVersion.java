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
package com.b2international.snowowl.api.codesystem.domain;

import java.util.Date;

/**
 * Encapsulates information about released and pending code system versions of a code system.
 */
public interface ICodeSystemVersion extends ICodeSystemVersionProperties {

	/**
	 * Returns the date on which this code system version was imported into the server.
	 * 
	 * @return the import date of this code system version
	 */
	Date getImportDate();

	/**
	 * Returns the date on which this code system version was last modified.
	 * 
	 * @return the last modification date of this code system version (can be {@code null})
	 */
	Date getLastModificationDate();
	
	/**
	 * Returns the parent branch path where the version branch is forked off
	 * @return parent branch path
	 */
	String getParentBranchPath();

	/**
	 * Indicates if any modifications have been made on this code system version after releasing it.
	 *  
	 * @return {@code true} if this code system version includes retroactive modifications, {@code false} otherwise
	 */
	boolean isPatched();
}
