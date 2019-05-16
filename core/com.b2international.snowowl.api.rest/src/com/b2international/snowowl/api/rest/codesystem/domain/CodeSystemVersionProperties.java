/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.codesystem.domain;

import java.util.Date;

/**
 * Base properties of a code system version.
 * 
 * @since 1.0
 */
public interface CodeSystemVersionProperties {

	/**
	 * Returns the description of this code system version.
	 * 
	 * @return the description of this code system version, eg. "{@code International RF2 Release 2014-07-31}"
	 */
	String getDescription();

	/**
	 * Returns the identifier of this code system version, which is unique within the containing code system.
	 *  
	 * @return the code system version identifier, eg. "{@code V3}" or "{@code 2014-07-31}"
	 */
	String getVersion();

	/**
	 * Returns the date on which this code system version will become effective.
	 *  
	 * @return the effective date of this code system version (can be {@code null})
	 */
	Date getEffectiveDate();
	
}
