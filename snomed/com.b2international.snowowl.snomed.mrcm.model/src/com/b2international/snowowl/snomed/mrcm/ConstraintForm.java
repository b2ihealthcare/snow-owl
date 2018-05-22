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
 * A representation of the literals of the enumeration '<em><b>Constraint Form</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Indicates the model or post-coordinated form to which a constraint applies.
 * <!-- end-model-doc -->
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConstraintForm()
 * @model
 * @generated
 */
public enum ConstraintForm implements Enumerator {
	/**
	 * The '<em><b>ALL FORMS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ALL_FORMS_VALUE
	 * @generated
	 * @ordered
	 */
	ALL_FORMS(0, "ALL_FORMS", "ALL_FORMS"),

	/**
	 * The '<em><b>DISTRIBUTION FORM</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DISTRIBUTION_FORM_VALUE
	 * @generated
	 * @ordered
	 */
	DISTRIBUTION_FORM(1, "DISTRIBUTION_FORM", "DISTRIBUTION_FORM"),

	/**
	 * The '<em><b>STATED FORM</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #STATED_FORM_VALUE
	 * @generated
	 * @ordered
	 */
	STATED_FORM(2, "STATED_FORM", "STATED_FORM"),

	/**
	 * The '<em><b>CLOSE TO USER FORM</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CLOSE_TO_USER_FORM_VALUE
	 * @generated
	 * @ordered
	 */
	CLOSE_TO_USER_FORM(3, "CLOSE_TO_USER_FORM", "CLOSE_TO_USER_FORM"),

	/**
	 * The '<em><b>LONG NORMAL FORM</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LONG_NORMAL_FORM_VALUE
	 * @generated
	 * @ordered
	 */
	LONG_NORMAL_FORM(4, "LONG_NORMAL_FORM", "LONG_NORMAL_FORM"),

	/**
	 * The '<em><b>SHORT NORMAL FORM</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SHORT_NORMAL_FORM_VALUE
	 * @generated
	 * @ordered
	 */
	SHORT_NORMAL_FORM(5, "SHORT_NORMAL_FORM", "SHORT_NORMAL_FORM");

	/**
	 * The '<em><b>ALL FORMS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The constraint should be true in all forms.
	 * <!-- end-model-doc -->
	 * @see #ALL_FORMS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ALL_FORMS_VALUE = 0;

	/**
	 * The '<em><b>DISTRIBUTION FORM</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The constraint should be tested in the distribution form.
	 * <!-- end-model-doc -->
	 * @see #DISTRIBUTION_FORM
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int DISTRIBUTION_FORM_VALUE = 1;

	/**
	 * The '<em><b>STATED FORM</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The constraint should be tested in the stated form.
	 * <!-- end-model-doc -->
	 * @see #STATED_FORM
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int STATED_FORM_VALUE = 2;

	/**
	 * The '<em><b>CLOSE TO USER FORM</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The constraint should be tested in the close to user form.
	 * <!-- end-model-doc -->
	 * @see #CLOSE_TO_USER_FORM
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CLOSE_TO_USER_FORM_VALUE = 3;

	/**
	 * The '<em><b>LONG NORMAL FORM</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The constraint should be tested in the long normal form.
	 * <!-- end-model-doc -->
	 * @see #LONG_NORMAL_FORM
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int LONG_NORMAL_FORM_VALUE = 4;

	/**
	 * The '<em><b>SHORT NORMAL FORM</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The constraint should be tested in the short normal form.
	 * <!-- end-model-doc -->
	 * @see #SHORT_NORMAL_FORM
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SHORT_NORMAL_FORM_VALUE = 5;

	/**
	 * An array of all the '<em><b>Constraint Form</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final ConstraintForm[] VALUES_ARRAY =
		new ConstraintForm[] {
			ALL_FORMS,
			DISTRIBUTION_FORM,
			STATED_FORM,
			CLOSE_TO_USER_FORM,
			LONG_NORMAL_FORM,
			SHORT_NORMAL_FORM,
		};

	/**
	 * A public read-only list of all the '<em><b>Constraint Form</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<ConstraintForm> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Constraint Form</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ConstraintForm get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ConstraintForm result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Constraint Form</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ConstraintForm getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ConstraintForm result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Constraint Form</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ConstraintForm get(int value) {
		switch (value) {
			case ALL_FORMS_VALUE: return ALL_FORMS;
			case DISTRIBUTION_FORM_VALUE: return DISTRIBUTION_FORM;
			case STATED_FORM_VALUE: return STATED_FORM;
			case CLOSE_TO_USER_FORM_VALUE: return CLOSE_TO_USER_FORM;
			case LONG_NORMAL_FORM_VALUE: return LONG_NORMAL_FORM;
			case SHORT_NORMAL_FORM_VALUE: return SHORT_NORMAL_FORM;
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
	private ConstraintForm(int value, String name, String literal) {
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
	
} //ConstraintForm
