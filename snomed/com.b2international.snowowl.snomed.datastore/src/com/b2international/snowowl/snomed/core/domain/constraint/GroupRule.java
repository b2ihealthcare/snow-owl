/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.constraint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumeration for SNOMED&nbsp;CT MRCM grouping rules. 
 */
public enum GroupRule {
	/**
	 * The '<em><b>UNGROUPED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Test cardinality or interdependency between ungrouped relationships.
	 * <!-- end-model-doc -->
	 * @see #UNGROUPED_VALUE
	 * @generated
	 * @ordered
	 */
	UNGROUPED(0, "UNGROUPED", "UNGROUPED"),

	/**
	 * The '<em><b>SINGLE GROUP</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Test cardinality or interdependency between Relationships in each relationship group.
	 * <!-- end-model-doc -->
	 * @see #SINGLE_GROUP_VALUE
	 * @generated
	 * @ordered
	 */
	SINGLE_GROUP(1, "SINGLE_GROUP", "SINGLE_GROUP"),

	/**
	 * The '<em><b>ALL GROUPS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Test cardinality or interdependency between all relationships irrespective of whether they are grouped or ungrouped.
	 * <!-- end-model-doc -->
	 * @see #ALL_GROUPS_VALUE
	 * @generated
	 * @ordered
	 */
	ALL_GROUPS(2, "ALL_GROUPS", "ALL_GROUPS"),

	/**
	 * The '<em><b>MULTIPLE GROUPS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Test cardinality or interdependency between relationship groups that match the predicates in the referenced dependency predicate.
	 * <!-- end-model-doc -->
	 * @see #MULTIPLE_GROUPS_VALUE
	 * @generated
	 * @ordered
	 */
	MULTIPLE_GROUPS(3, "MULTIPLE_GROUPS", "MULTIPLE_GROUPS");

	/**
	 * The '<em><b>UNGROUPED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Test cardinality or interdependency between ungrouped relationships.
	 * <!-- end-model-doc -->
	 * @see #UNGROUPED
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int UNGROUPED_VALUE = 0;

	/**
	 * The '<em><b>SINGLE GROUP</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Test cardinality or interdependency between Relationships in each relationship group.
	 * <!-- end-model-doc -->
	 * @see #SINGLE_GROUP
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SINGLE_GROUP_VALUE = 1;

	/**
	 * The '<em><b>ALL GROUPS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Test cardinality or interdependency between all relationships irrespective of whether they are grouped or ungrouped.
	 * <!-- end-model-doc -->
	 * @see #ALL_GROUPS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ALL_GROUPS_VALUE = 2;

	/**
	 * The '<em><b>MULTIPLE GROUPS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Test cardinality or interdependency between relationship groups that match the predicates in the referenced dependency predicate.
	 * <!-- end-model-doc -->
	 * @see #MULTIPLE_GROUPS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int MULTIPLE_GROUPS_VALUE = 3;

	/**
	 * An array of all the '<em><b>Group Rule</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final GroupRule[] VALUES_ARRAY =
		new GroupRule[] {
			UNGROUPED,
			SINGLE_GROUP,
			ALL_GROUPS,
			MULTIPLE_GROUPS,
		};

	/**
	 * A public read-only list of all the '<em><b>Group Rule</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<GroupRule> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Group Rule</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static GroupRule get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			GroupRule result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Group Rule</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static GroupRule getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			GroupRule result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Group Rule</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static GroupRule get(int value) {
		switch (value) {
			case UNGROUPED_VALUE: return UNGROUPED;
			case SINGLE_GROUP_VALUE: return SINGLE_GROUP;
			case ALL_GROUPS_VALUE: return ALL_GROUPS;
			case MULTIPLE_GROUPS_VALUE: return MULTIPLE_GROUPS;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private GroupRule(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //GroupRule
