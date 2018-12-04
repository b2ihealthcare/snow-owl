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
 * A representation of the literals of the enumeration '<em><b>Constraint Strength</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Indicates the strength and applicability of a constraint.
 * <!-- end-model-doc -->
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConstraintStrength()
 * @model
 * @generated
 */
public enum ConstraintStrength implements Enumerator {
	/**
	 * The '<em><b>MANDATORY CM</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Mandatory concept model constraint.
	 * <!-- end-model-doc -->
	 * @see #MANDATORY_CM_VALUE
	 * @generated
	 * @ordered
	 */
	MANDATORY_CM(0, "MANDATORY_CM", "MANDATORY_CM"),

	/**
	 * The '<em><b>RECOMMENDED CM</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Recommended concept model constraint based on best practice editorial rules.
	 * <!-- end-model-doc -->
	 * @see #RECOMMENDED_CM_VALUE
	 * @generated
	 * @ordered
	 */
	RECOMMENDED_CM(1, "RECOMMENDED_CM", "RECOMMENDED_CM"),

	/**
	 * The '<em><b>ADVISORY CM</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Advisory concept model constraint based on editorial conventions.
	 * <!-- end-model-doc -->
	 * @see #ADVISORY_CM_VALUE
	 * @generated
	 * @ordered
	 */
	ADVISORY_CM(2, "ADVISORY_CM", "ADVISORY_CM"),

	/**
	 * The '<em><b>MANDATORY PC</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Mandatory constraint on post-coordinated refinement.
	 * <!-- end-model-doc -->
	 * @see #MANDATORY_PC_VALUE
	 * @generated
	 * @ordered
	 */
	MANDATORY_PC(3, "MANDATORY_PC", "MANDATORY_PC"),

	/**
	 * The '<em><b>INFORMATION MODEL PC</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Constraint of post-coordinated refinement that is specific to use in a particular information model. The information model is identified by the 'scope' attribute.
	 * <!-- end-model-doc -->
	 * @see #INFORMATION_MODEL_PC_VALUE
	 * @generated
	 * @ordered
	 */
	INFORMATION_MODEL_PC(4, "INFORMATION_MODEL_PC", "INFORMATION_MODEL_PC"),

	/**
	 * The '<em><b>USE CASE SPECIFIC PC</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Constraint of post-coordinated refinement that is specific to a particular use case. The use case is identified by the 'scope' attribute.
	 * <!-- end-model-doc -->
	 * @see #USE_CASE_SPECIFIC_PC_VALUE
	 * @generated
	 * @ordered
	 */
	USE_CASE_SPECIFIC_PC(5, "USE_CASE_SPECIFIC_PC", "USE_CASE_SPECIFIC_PC"),

	/**
	 * The '<em><b>IMPLEMENTATION SPECIFIC PC</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Constraint of post-coordinated refinement that is specific to a particular application or local implementation. The implementation is identified by the 'scope' attribute.
	 * <!-- end-model-doc -->
	 * @see #IMPLEMENTATION_SPECIFIC_PC_VALUE
	 * @generated
	 * @ordered
	 */
	IMPLEMENTATION_SPECIFIC_PC(6, "IMPLEMENTATION_SPECIFIC_PC", "IMPLEMENTATION_SPECIFIC_PC");

	/**
	 * The '<em><b>MANDATORY CM</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Mandatory concept model constraint.
	 * <!-- end-model-doc -->
	 * @see #MANDATORY_CM
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int MANDATORY_CM_VALUE = 0;

	/**
	 * The '<em><b>RECOMMENDED CM</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Recommended concept model constraint based on best practice editorial rules.
	 * <!-- end-model-doc -->
	 * @see #RECOMMENDED_CM
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int RECOMMENDED_CM_VALUE = 1;

	/**
	 * The '<em><b>ADVISORY CM</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Advisory concept model constraint based on editorial conventions.
	 * <!-- end-model-doc -->
	 * @see #ADVISORY_CM
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ADVISORY_CM_VALUE = 2;

	/**
	 * The '<em><b>MANDATORY PC</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Mandatory constraint on post-coordinated refinement.
	 * <!-- end-model-doc -->
	 * @see #MANDATORY_PC
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int MANDATORY_PC_VALUE = 3;

	/**
	 * The '<em><b>INFORMATION MODEL PC</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Constraint of post-coordinated refinement that is specific to use in a particular information model. The information model is identified by the 'scope' attribute.
	 * <!-- end-model-doc -->
	 * @see #INFORMATION_MODEL_PC
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int INFORMATION_MODEL_PC_VALUE = 4;

	/**
	 * The '<em><b>USE CASE SPECIFIC PC</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Constraint of post-coordinated refinement that is specific to a particular use case. The use case is identified by the 'scope' attribute.
	 * <!-- end-model-doc -->
	 * @see #USE_CASE_SPECIFIC_PC
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int USE_CASE_SPECIFIC_PC_VALUE = 5;

	/**
	 * The '<em><b>IMPLEMENTATION SPECIFIC PC</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Constraint of post-coordinated refinement that is specific to a particular application or local implementation. The implementation is identified by the 'scope' attribute.
	 * <!-- end-model-doc -->
	 * @see #IMPLEMENTATION_SPECIFIC_PC
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int IMPLEMENTATION_SPECIFIC_PC_VALUE = 6;

	/**
	 * An array of all the '<em><b>Constraint Strength</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final ConstraintStrength[] VALUES_ARRAY =
		new ConstraintStrength[] {
			MANDATORY_CM,
			RECOMMENDED_CM,
			ADVISORY_CM,
			MANDATORY_PC,
			INFORMATION_MODEL_PC,
			USE_CASE_SPECIFIC_PC,
			IMPLEMENTATION_SPECIFIC_PC,
		};

	/**
	 * A public read-only list of all the '<em><b>Constraint Strength</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<ConstraintStrength> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Constraint Strength</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ConstraintStrength get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ConstraintStrength result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Constraint Strength</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ConstraintStrength getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ConstraintStrength result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Constraint Strength</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ConstraintStrength get(int value) {
		switch (value) {
			case MANDATORY_CM_VALUE: return MANDATORY_CM;
			case RECOMMENDED_CM_VALUE: return RECOMMENDED_CM;
			case ADVISORY_CM_VALUE: return ADVISORY_CM;
			case MANDATORY_PC_VALUE: return MANDATORY_PC;
			case INFORMATION_MODEL_PC_VALUE: return INFORMATION_MODEL_PC;
			case USE_CASE_SPECIFIC_PC_VALUE: return USE_CASE_SPECIFIC_PC;
			case IMPLEMENTATION_SPECIFIC_PC_VALUE: return IMPLEMENTATION_SPECIFIC_PC;
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
	private ConstraintStrength(int value, String name, String literal) {
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
	
} //ConstraintStrength
