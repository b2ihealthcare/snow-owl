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

/**
 * Contains properties required for creating SNOMED CT relationships.
 */
public interface ISnomedRelationshipInput extends ISnomedComponentInput {

	/**
	 * Returns the new relationship's source concept identifier.
	 * 
	 * @return the source identifier
	 */
	String getSourceId();

	/**
	 * Returns the new relationship's destination concept identifier.
	 * 
	 * @return the destination identifier
	 */
	String getDestinationId();

	/**
	 * Returns the negation flag for the new relationship's destination concept.
	 * 
	 * @return {@code true} if the destination concept should be considered negated, {@code false} otherwise
	 */
	boolean isDestinationNegated();

	/**
	 * Returns the new relationship's type identifier.
	 * 
	 * @return the type identifier
	 */
	String getTypeId();

	/**
	 * Returns the new relationship's group number.
	 * 
	 * @return the relationship group, or 0 if this relationship can not be grouped, or is in an unnumbered, singleton
	 * group
	 */
	int getGroup();

	/**
	 * Returns the new relationship's union group number.
	 * 
	 * @return the relationship union group, or 0 if this relationship is not part of a disjunction
	 */
	int getUnionGroup();

	/**
	 * Returns the new relationship's characteristic type value.
	 * 
	 * @return the characteristic type
	 */
	CharacteristicType getCharacteristicType();

	/**
	 * Returns the new relationship's modifier value.
	 * 
	 * @return the relationship modifier
	 */
	RelationshipModifier getModifier();
}
