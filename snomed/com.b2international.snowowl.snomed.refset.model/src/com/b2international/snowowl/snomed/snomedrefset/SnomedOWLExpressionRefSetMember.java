/**
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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Snomed OWL Expression Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedOWLExpressionRefSetMember#getOwlExpression <em>Owl Expression</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedOWLExpressionRefSetMember()
 * @model
 * @generated
 */
public interface SnomedOWLExpressionRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns the value of the '<em><b>Owl Expression</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Owl Expression</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Owl Expression</em>' attribute.
	 * @see #setOwlExpression(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedOWLExpressionRefSetMember_OwlExpression()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getOwlExpression();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedOWLExpressionRefSetMember#getOwlExpression <em>Owl Expression</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Owl Expression</em>' attribute.
	 * @see #getOwlExpression()
	 * @generated
	 */
	void setOwlExpression(String value);

} // SnomedOWLExpressionRefSetMember
