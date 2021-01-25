/*
 * Copyright 2013-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.normalform;

/**
 * Represents any item in an ontology which can be compared for
 * expressiveness.
 */
interface NormalFormProperty {

	/**
	 * Special group number indicating that the next free group/union group number
	 * should be used when the fragments in this group/union group are converted into
	 * relationships.
	 */
	static final int UNKOWN_GROUP = -1;
	
	static final int ZERO_GROUP = 0;

	/**
	 * Checks if the specified item can be regarded as redundant when compared to
	 * the current item. An item is redundant with respect to another if it less
	 * specific, i.e. it describes a broader range of individuals.
	 *
	 * @param property the item to compare against
	 * 
	 * @return <code>true</code> if this item contains an equal or more specific
	 *         description when compared to the other item, <code>false</code>
	 *         otherwise
	 */
	public boolean isSameOrStrongerThan (NormalFormProperty property);
}
