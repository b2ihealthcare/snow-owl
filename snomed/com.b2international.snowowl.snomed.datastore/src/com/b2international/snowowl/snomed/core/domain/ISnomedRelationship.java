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
package com.b2international.snowowl.snomed.core.domain;

import com.b2international.snowowl.core.domain.IComponentEdge;

/**
 * Represents a SNOMED CT description.
 * <p>
 * Information about relationship refinability can also be retrieved from this object.
 */
public interface ISnomedRelationship extends SnomedCoreComponent, IComponentEdge {

	/**
	 * Returns the source concept of this relationship.
	 * 
	 * @return
	 */
	ISnomedConcept getSourceConcept();

	/**
	 * Returns the destination concept of this relationship.
	 * 
	 * @return
	 */
	ISnomedConcept getDestinationConcept();

	/**
	 * Checks whether the destination concept's meaning should be negated ({@code ObjectComplementOf} semantics in OWL2).
	 * 
	 * @return {@code true} if the destination concept is negated, {@code false} if it should be interpreted normally
	 */
	boolean isDestinationNegated();

	/**
	 * Returns the type identifier of this relationship.
	 * 
	 * @return the relationship type identifier
	 */
	String getTypeId();

	/**
	 * Returns the type concept of this relationship.
	 * 
	 * @return
	 */
	ISnomedConcept getTypeConcept();

	/**
	 * Returns the relationship group number.
	 * 
	 * @return the relationship group, or 0 if this relationship can not be grouped, or is in an unnumbered, singleton group
	 */
	int getGroup();

	/**
	 * If multiple relationship destinations are to be taken as a disjunction, the relationships are assigned a common, positive union group number.
	 * 
	 * @return the relationship union group, or 0 if this relationship is not part of a disjunction
	 */
	int getUnionGroup();

	/**
	 * Returns the characteristic type of the relationship.
	 * 
	 * @return the relationship's characteristic type
	 */
	CharacteristicType getCharacteristicType();

	/**
	 * Returns the relationship's refinability reference set member value.
	 * 
	 * @return the refinability value for this relationship
	 */
	RelationshipRefinability getRefinability();

	/**
	 * Returns the relationship's modifier value.
	 * 
	 * @return the modifier of this relationship
	 */
	RelationshipModifier getModifier();
}
