/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.snomedrefset;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * Enumeration for SNOMED&nbsp;CT reference set types. 
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetType()
 * @model
 * @generated
 */
public enum SnomedRefSetType implements Enumerator {
	/**
	 * Simple type.
	 * @see #SIMPLE_VALUE
	 * @generated
	 * @ordered
	 */
	SIMPLE(0, "SIMPLE", "SIMPLE"),

	/**
	 * Simple map type.
	 * @see #SIMPLE_MAP_VALUE
	 * @generated
	 * @ordered
	 */
	SIMPLE_MAP(1, "SIMPLE_MAP", "SIMPLE_MAP"),

	/**
	 * Language type.
	 * @see #LANGUAGE_VALUE
	 * @generated
	 * @ordered
	 */
	LANGUAGE(2, "LANGUAGE", "LANGUAGE"),

	/**
	 * Attribute value type.
	 * @see #ATTRIBUTE_VALUE_VALUE
	 * @generated
	 * @ordered
	 */
	ATTRIBUTE_VALUE(3, "ATTRIBUTE_VALUE", "ATTRIBUTE_VALUE"),

	/**
	 * Query specification type.
	 * @see #QUERY_VALUE
	 * @generated
	 * @ordered
	 */
	QUERY(4, "QUERY", "QUERY"),

	/**
	 * Complex map type.
	 * @see #COMPLEX_MAP_VALUE
	 * @generated
	 * @ordered
	 */
	COMPLEX_MAP(5, "COMPLEX_MAP", "COMPLEX_MAP"),

	/**
	 * Description type.
	 * @see #DESCRIPTION_TYPE_VALUE
	 * @generated
	 * @ordered
	 */
	DESCRIPTION_TYPE(6, "DESCRIPTION_TYPE", "DESCRIPTION_TYPE"),

	/**
	 * Concrete data type.
	 * @see #CONCRETE_DATA_TYPE_VALUE
	 * @generated
	 * @ordered
	 */
	CONCRETE_DATA_TYPE(7, "CONCRETE_DATA_TYPE", "CONCRETE_DATA_TYPE"),

	/**
	 * Association value type.
	 * @see #ASSOCIATION_VALUE
	 * @generated
	 * @ordered
	 */
	ASSOCIATION(8, "ASSOCIATION", "ASSOCIATION"), 
	
	/**
	 * Module dependency value type.
	 * @see #MODULE_DEPENDENCY_VALUE
	 * @generated
	 * @ordered
	 */
	MODULE_DEPENDENCY(9, "MODULE_DEPENDENCY", "MODULE_DEPENDENCY"),
	
	/**
	 * Extended map type.
	 * @see #EXTENDED_MAP_VALUE
	 * @generated
	 * @ordered
	 */
	EXTENDED_MAP(10, "EXTENDED_MAP", "EXTENDED_MAP"), 
	
	/**
	 * Simple map type with map target description included.
	 * @see #SIMPLE_MAP_WITH_DESCRIPTION_VALUE
	 * @generated
	 * @ordered
	 */
	SIMPLE_MAP_WITH_DESCRIPTION(11, "SIMPLE_MAP_WITH_DESCRIPTION", "SIMPLE_MAP_WITH_DESCRIPTION");

	/**
	 * The '<em><b>SIMPLE</b></em>' literal value.
	 * @see #SIMPLE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SIMPLE_VALUE = 0;

	/**
	 * The '<em><b>SIMPLE MAP</b></em>' literal value.
	 * @see #SIMPLE_MAP
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SIMPLE_MAP_VALUE = 1;

	/**
	 * The '<em><b>LANGUAGE</b></em>' literal value.
	 * @see #LANGUAGE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int LANGUAGE_VALUE = 2;

	/**
	 * The '<em><b>ATTRIBUTE VALUE</b></em>' literal value.
	 * @see #ATTRIBUTE_VALUE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ATTRIBUTE_VALUE_VALUE = 3;

	/**
	 * The '<em><b>QUERY</b></em>' literal value.
	 * @see #QUERY
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int QUERY_VALUE = 4;

	/**
	 * The '<em><b>COMPLEX MAP</b></em>' literal value.
	 * @see #COMPLEX_MAP
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int COMPLEX_MAP_VALUE = 5;

	/**
	 * The '<em><b>DESCRIPTION TYPE</b></em>' literal value.
	 * @see #DESCRIPTION_TYPE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int DESCRIPTION_TYPE_VALUE = 6;

	/**
	 * The '<em><b>CONCRETE DATA TYPE</b></em>' literal value.
	 * @see #CONCRETE_DATA_TYPE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CONCRETE_DATA_TYPE_VALUE = 7;

	/**
	 * The '<em><b>ASSOCIATION</b></em>' literal value.
	 * @see #ASSOCIATION
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ASSOCIATION_VALUE = 8;

	/**
	 * The '<em><b>MODULE DEPENDENCY</b></em>' literal value.
	 * @see #MODULE_DEPENDENCY
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int MODULE_DEPENDENCY_VALUE = 9;

	/**
	 * The '<em><b>EXTENDED MAP</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>EXTENDED MAP</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #EXTENDED_MAP
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int EXTENDED_MAP_VALUE = 10;

	/**
	 * The '<em><b>SIMPLE MAP WITH DESCRIPTION</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>SIMPLE MAP WITH DESCRIPTION</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SIMPLE_MAP_WITH_DESCRIPTION
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SIMPLE_MAP_WITH_DESCRIPTION_VALUE = 11;

	/**
	 * An array of all the '<em><b>Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final SnomedRefSetType[] VALUES_ARRAY =
		new SnomedRefSetType[] {
			SIMPLE,
			SIMPLE_MAP,
			LANGUAGE,
			ATTRIBUTE_VALUE,
			QUERY,
			COMPLEX_MAP,
			DESCRIPTION_TYPE,
			CONCRETE_DATA_TYPE,
			ASSOCIATION,
			MODULE_DEPENDENCY,
			EXTENDED_MAP,
			SIMPLE_MAP_WITH_DESCRIPTION,
		};

	/**
	 * A public read-only list of all the '<em><b>Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<SnomedRefSetType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Type</b></em>' literal with the specified literal value.
	 * @generated
	 */
	public static SnomedRefSetType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			SnomedRefSetType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Type</b></em>' literal with the specified name.
	 * @generated
	 */
	public static SnomedRefSetType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			SnomedRefSetType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Type</b></em>' literal with the specified integer value.
	 * @generated
	 */
	public static SnomedRefSetType get(int value) {
		switch (value) {
			case SIMPLE_VALUE: return SIMPLE;
			case SIMPLE_MAP_VALUE: return SIMPLE_MAP;
			case LANGUAGE_VALUE: return LANGUAGE;
			case ATTRIBUTE_VALUE_VALUE: return ATTRIBUTE_VALUE;
			case QUERY_VALUE: return QUERY;
			case COMPLEX_MAP_VALUE: return COMPLEX_MAP;
			case DESCRIPTION_TYPE_VALUE: return DESCRIPTION_TYPE;
			case CONCRETE_DATA_TYPE_VALUE: return CONCRETE_DATA_TYPE;
			case ASSOCIATION_VALUE: return ASSOCIATION;
			case MODULE_DEPENDENCY_VALUE: return MODULE_DEPENDENCY;
			case EXTENDED_MAP_VALUE: return EXTENDED_MAP;
			case SIMPLE_MAP_WITH_DESCRIPTION_VALUE: return SIMPLE_MAP_WITH_DESCRIPTION;
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
	private SnomedRefSetType(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * Returns with the value of the type.
	 * @generated
	 */
	public int getValue() {
	  return value;
	}

	/**
	 * Returns with the human readable name of the type.
	 * @generated
	 */
	public String getName() {
	  return name;
	}

	/**
	 * Returns with the literal of the type.
	 * @generated
	 */
	public String getLiteral() {
	  return literal;
	}

	/**
	 * See: {@link #getLiteral()}.
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //SnomedRefSetType