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

import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;

/**
 * Contains a subset of relationship properties which makes it possible to identify an existing redundant relationship
 * for deletion/deactivation, or create a inferred relationship based on it.
 */
public interface IRelationshipChange {

	/**
	 * Returns the nature of the change (inferred or redundant).
	 * 
	 * @return the change nature
	 */
	ChangeNature getChangeNature();

	/**
	 * Returns the source concept identifier of this relationship.
	 * 
	 * @return the source concept identifier
	 */
	String getSourceId();

	/**
	 * Returns the type identifier of this relationship.
	 * 
	 * @return the type identifier
	 */
	String getTypeId();

	/**
	 * Returns the destination concept identifier of this relationship.
	 * 
	 * @return the destination concept identifier
	 */
	String getDestinationId();

	/**
	 * Returns the negation flag for the relationship's destination concept.
	 * 
	 * @return {@code true} if the destination concept should be considered negated, {@code false} otherwise
	 */
	boolean isDestinationNegated();

	/**
	 * Returns the characteristicType concept identifier of this relationship.
	 * 
	 * @return the characteristicType concept identifier
	 */
	String getCharacteristicTypeId();

	/**
	 * Returns the relationship's group number.
	 * 
	 * @return the relationship group, or 0 if this relationship can not be grouped, or is in an unnumbered, singleton
	 * group
	 */
	int getGroup();

	/**
	 * Returns the relationship's union group number.
	 * 
	 * @return the relationship union group, or 0 if this relationship is not part of a disjunction
	 */
	int getUnionGroup();

	/**
	 * Returns the relationship's modifier value.
	 * 
	 * @return the relationship modifier
	 */
	RelationshipModifier getModifier();
}
