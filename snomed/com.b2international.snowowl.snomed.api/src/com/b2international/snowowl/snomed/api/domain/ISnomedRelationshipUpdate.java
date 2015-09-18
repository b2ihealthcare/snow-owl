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
 * Holds updatable properties of SNOMED CT relationships.
 * <p>
 * The following properties (along with the ones in {@link ISnomedComponentUpdate}) are currently
 * updatable on a relationship:
 * <p>
 * <ul>
 * <li>group
 * <li>unionGroup
 * <li>characteristicType
 * <li>modifier
 * </ul>
 */
public interface ISnomedRelationshipUpdate extends ISnomedComponentUpdate {

	/**
	 * Returns the updated relationship group number.
	 * 
	 * @return the relationship group, 0 if this relationship can not be grouped, or is in an unnumbered, singleton
	 * group, or {@code null} if the existing group should not change
	 */
	Integer getGroup();

	/**
	 * Returns the updated relationship union group number.
	 * 
	 * @return the relationship union group, 0 if this relationship is not part of a disjunction, or {@code null} if
	 * the existing union group should not change
	 */
	Integer getUnionGroup();

	/**
	 * Returns the updated relationship characteristic type value.
	 * 
	 * @return the characteristic type
	 */
	CharacteristicType getCharacteristicType();

	/**
	 * Returns the updated relationship modifier value.
	 * 
	 * @return the relationship modifier
	 */
	RelationshipModifier getModifier();
}
