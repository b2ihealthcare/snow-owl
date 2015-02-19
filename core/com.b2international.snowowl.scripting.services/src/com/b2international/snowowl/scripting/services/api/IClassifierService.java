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
package com.b2international.snowowl.scripting.services.api;

import java.util.List;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.snomed.Concept;

/**
 * This service provides access to the classifier.
 * This service is specific to SNOMED CT.
 * 
 *
 */
public interface IClassifierService {
	
	/**
	 * Returns true if the concept exist based on its defining attributes such as
	 * defining relationships and concrete data types.
	 * 
	 * @param {@link Concept}
	 * @return
	 */
	boolean exist(final Concept concept);
	
	/**
	 * Returns a list of identical concepts to the {@link Concept} passed in as a parameter.
	 * The check is evaluated based on the concept's defining attributes such as defining relationships
	 * and concrete data types.
	 * 
	 * If none found the method returns an empty list.
	 * 
	 * Note: the argument concept should not be in a transaction.
	 * 
	 * @param {@link Concept}
	 * @return list of concepts as {@link IComponent} instances.
	 */
	List<IComponent<Concept>> findIdenticalConcepts(final Concept concept);

}