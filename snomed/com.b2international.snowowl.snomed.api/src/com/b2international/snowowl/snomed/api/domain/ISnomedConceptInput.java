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
package com.b2international.snowowl.snomed.api.domain;

import java.util.List;

/**
 * Contains properties required for creating SNOMED CT concepts.
 */
public interface ISnomedConceptInput extends ISnomedComponentInput {

	/**
	 * Returns the list of descriptions to create along with the concept. Must contain at least one fully specified name and one
	 * synonym with preferred acceptability.
	 * 
	 * @return the list of descriptions to create
	 */
	List<ISnomedDescriptionInput> getDescriptions();

	/**
	 * Returns the identifier of a parent concept. The new concept will be attached to the existing concept graph via a new {@code IS A}
	 * relationship, which will have this concept as the destination.
	 * <p>
	 * Extra relationships can be added in a separate call. 
	 * 
	 * @return the parent concept identifier
	 */
	String getParentId();

	/**
	 * Returns the identifier generation strategy for the new {@code IS A} relationship.
	 * 
	 * @return the {@code IS A} relationship identifier generation strategy
	 */
	IdGenerationStrategy getIsAIdGenerationStrategy();
	
	/**
	 * Returns the desired definition status of the concept.
	 * 
	 * @return {@link DefinitionStatus#PRIMITIVE} if the concept should be created as primitive,
	 * {@link DefinitionStatus#FULLY_DEFINED} if the status should be set to fully defined.
	 */
	DefinitionStatus getDefinitionStatus();

}
