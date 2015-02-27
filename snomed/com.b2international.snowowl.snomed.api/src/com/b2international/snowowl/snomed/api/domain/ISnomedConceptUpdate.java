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

import com.google.common.collect.Multimap;

/**
 * Holds updatable properties of SNOMED CT concepts.
 */
public interface ISnomedConceptUpdate extends ISnomedComponentUpdate {

	/**
	 * Returns the desired new definition status of the concept.
	 * 
	 * @return {@link DefinitionStatus#PRIMITIVE} if the concept should be changed to primitive,
	 * {@link DefinitionStatus#FULLY_DEFINED} if the status should be changed to fully defined, {@code null} if the
	 * existing status should be left unchanged
	 */
	DefinitionStatus getDefinitionStatus();

	/**
	 * Returns the desired new subclass definition status of the concept.
	 * 
	 * @return {@link SubclassDefinitionStatus#DISJOINT_SUBCLASSES} if subclasses should be considered a disjoint union,
	 * {@link SubclassDefinitionStatus#NON_DISJOINT_SUBCLASSES} to retract such assertions, {@code null} to leave the
	 * existing status unchanged
	 */
	SubclassDefinitionStatus getSubclassDefinitionStatus();

	/**
	 * Returns the desired new inactivation indicator value for the concept's inactivation indicator member, to be used
	 * when the concept is deactivated ({@link #isActive()} set to {@code false}).
	 * 
	 * @return the inactivation indicator member value after the update
	 */
	InactivationIndicator getInactivationIndicator();

	/**
	 * Returns the desired new association target component identifiers, keyed by association type.
	 * <p>
	 * Conflicting existing associations will be removed or deactivated (if they were part of a previous release of SNOMED CT). 
	 * 
	 * @return the association target components after the update
	 */
	Multimap<AssociationType, String> getAssociationTargets();
}
