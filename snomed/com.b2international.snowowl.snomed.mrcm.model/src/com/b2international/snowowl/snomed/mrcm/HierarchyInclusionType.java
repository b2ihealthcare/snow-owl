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
 * A representation of the literals of the enumeration '<em><b>Hierarchy Inclusion Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Indicates whether the given concept and/or its subtypes are included in a concept set definition.
 * <!-- end-model-doc -->
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getHierarchyInclusionType()
 * @model
 * @generated
 */
public enum HierarchyInclusionType implements Enumerator {
	/**
	 * The '<em><b>SELF</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Include the specified concept.
	 * <!-- end-model-doc -->
	 * @see #SELF_VALUE
	 * @generated
	 * @ordered
	 */
	SELF(0, "SELF", "SELF"),

	/**
	 * The '<em><b>DESCENDANT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Include subtype descendants of the specified concept.
	 * <!-- end-model-doc -->
	 * @see #DESCENDANT_VALUE
	 * @generated
	 * @ordered
	 */
	DESCENDANT(1, "DESCENDANT", "DESCENDANT"),

	/**
	 * The '<em><b>SELF OR DESCENDANT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Include the specified concept and all its subtype concepts.
	 * <!-- end-model-doc -->
	 * @see #SELF_OR_DESCENDANT_VALUE
	 * @generated
	 * @ordered
	 */
	SELF_OR_DESCENDANT(2, "SELF_OR_DESCENDANT", "SELF_OR_DESCENDANT"), /**
	 * The '<em><b>CHILDREN</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CHILDREN_VALUE
	 * @generated
	 * @ordered
	 */
	CHILDREN(3, "CHILDREN", "CHILDREN");

	/**
	 * The '<em><b>SELF</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Include the specified concept.
	 * <!-- end-model-doc -->
	 * @see #SELF
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SELF_VALUE = 0;

	/**
	 * The '<em><b>DESCENDANT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Include subtype descendants of the specified concept.
	 * <!-- end-model-doc -->
	 * @see #DESCENDANT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int DESCENDANT_VALUE = 1;

	/**
	 * The '<em><b>SELF OR DESCENDANT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Include the specified concept and all its subtype concepts.
	 * <!-- end-model-doc -->
	 * @see #SELF_OR_DESCENDANT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SELF_OR_DESCENDANT_VALUE = 2;

	/**
	 * The '<em><b>CHILDREN</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>CHILDREN</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CHILDREN
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CHILDREN_VALUE = 3;

	/**
	 * An array of all the '<em><b>Hierarchy Inclusion Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final HierarchyInclusionType[] VALUES_ARRAY =
		new HierarchyInclusionType[] {
			SELF,
			DESCENDANT,
			SELF_OR_DESCENDANT,
			CHILDREN,
		};

	/**
	 * A public read-only list of all the '<em><b>Hierarchy Inclusion Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<HierarchyInclusionType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Hierarchy Inclusion Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static HierarchyInclusionType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			HierarchyInclusionType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Hierarchy Inclusion Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static HierarchyInclusionType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			HierarchyInclusionType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Hierarchy Inclusion Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static HierarchyInclusionType get(int value) {
		switch (value) {
			case SELF_VALUE: return SELF;
			case DESCENDANT_VALUE: return DESCENDANT;
			case SELF_OR_DESCENDANT_VALUE: return SELF_OR_DESCENDANT;
			case CHILDREN_VALUE: return CHILDREN;
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
	private HierarchyInclusionType(int value, String name, String literal) {
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
	
} //HierarchyInclusionType
