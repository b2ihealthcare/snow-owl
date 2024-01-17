/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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

import java.io.Serializable;

/**
* Bare minimum representation of a SNOMED&nbsp;CT IS_A relationship.
*
* @see Serializable
*/
public interface IsAStatement extends Serializable {

	/**
	 * @return the ID of the destination concept.
	 */
	long getDestinationId();

	/**
	 * @return the ID of the source concept.
	 */
	long getSourceId();
	
	/**
	 * Bare minimum representation of a SNOMED&nbsp;CT relationship.
	 */
	public static interface Statement extends IsAStatement {
		
		/**Returns with the type concept ID of the relationship.*/
		long getTypeId();
		
	}
	
}