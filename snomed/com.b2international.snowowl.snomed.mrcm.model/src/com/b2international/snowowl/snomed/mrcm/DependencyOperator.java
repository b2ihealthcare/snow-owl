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
package com.b2international.snowowl.snomed.mrcm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Dependency Operator</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Indicates how many child predicates of a dependency predicate are required to be true.
 * <!-- end-model-doc -->
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getDependencyOperator()
 * @model
 * @generated
 */
public enum DependencyOperator implements Enumerator {
	/**
	 * The '<em><b>ONE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ONE_VALUE
	 * @generated
	 * @ordered
	 */
	ONE(0, "ONE", "ONE"),

	/**
	 * The '<em><b>SOME</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SOME_VALUE
	 * @generated
	 * @ordered
	 */
	SOME(1, "SOME", "SOME"),

	/**
	 * The '<em><b>ALL</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ALL_VALUE
	 * @generated
	 * @ordered
	 */
	ALL(2, "ALL", "ALL");

	/**
	 * The '<em><b>ONE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Return true if one and only one child predicate in the composite predicate returns true.
	 * <!-- end-model-doc -->
	 * @see #ONE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ONE_VALUE = 0;

	/**
	 * The '<em><b>SOME</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Return true if at least one child predicate in the composite predicate returns true.
	 * <!-- end-model-doc -->
	 * @see #SOME
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SOME_VALUE = 1;

	/**
	 * The '<em><b>ALL</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Return true if all tests in the composite predicate return true.
	 * <!-- end-model-doc -->
	 * @see #ALL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ALL_VALUE = 2;

	/**
	 * An array of all the '<em><b>Dependency Operator</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final DependencyOperator[] VALUES_ARRAY =
		new DependencyOperator[] {
			ONE,
			SOME,
			ALL,
		};

	/**
	 * A public read-only list of all the '<em><b>Dependency Operator</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<DependencyOperator> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Dependency Operator</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static DependencyOperator get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DependencyOperator result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Dependency Operator</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static DependencyOperator getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DependencyOperator result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Dependency Operator</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static DependencyOperator get(int value) {
		switch (value) {
			case ONE_VALUE: return ONE;
			case SOME_VALUE: return SOME;
			case ALL_VALUE: return ALL;
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
	private DependencyOperator(int value, String name, String literal) {
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
	
} //DependencyOperator
