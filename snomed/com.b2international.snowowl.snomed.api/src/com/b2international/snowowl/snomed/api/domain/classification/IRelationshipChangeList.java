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
package com.b2international.snowowl.snomed.api.domain.classification;

import java.util.List;

/**
 * Holds a pageable list of suggested relationship changes. It includes the total number of changes, as
 * well as the subset of the actual data.
 */
public interface IRelationshipChangeList {

	/**
	 * Returns the requested segment of changed relationships.
	 * 
	 * @return a subset of the relationships which should be modified 
	 */
	List<IRelationshipChange> getItems();

	/**
	 * Returns the total number of relationship changes for this classification run.
	 * 
	 * @return the total number of changes
	 */
	int getTotal();
	
	/**
	 * Returns the requested limit number of relationship changes. 
	 * @return
	 */
	int getLimit();
	
	/**
	 * Returns the offset of this collection-like resource
	 * @return
	 */
	int getOffset();
}
