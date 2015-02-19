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
package com.b2international.snowowl.snomed.dsl.query.queryast;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Unary RValue</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getUnaryRValue()
 * @model abstract="true"
 * @generated
 */
public interface UnaryRValue extends RValue {
	/**
	 * Returns the value of the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' containment reference.
	 * @see #setValue(RValue)
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getUnaryRValue_Value()
	 * @model containment="true" required="true"
	 * @generated
	 */
	RValue getValue();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue#getValue <em>Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' containment reference.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(RValue value);

} // UnaryRValue