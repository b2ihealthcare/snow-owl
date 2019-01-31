/**
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.ql;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Exclusion</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.Exclusion#getLeft <em>Left</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.Exclusion#getRight <em>Right</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ql.QlPackage#getExclusion()
 * @model
 * @generated
 */
public interface Exclusion extends Constraint
{
  /**
   * Returns the value of the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Left</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Left</em>' containment reference.
   * @see #setLeft(Filter)
   * @see com.b2international.snowowl.snomed.ql.QlPackage#getExclusion_Left()
   * @model containment="true"
   * @generated
   */
  Filter getLeft();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.Exclusion#getLeft <em>Left</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Left</em>' containment reference.
   * @see #getLeft()
   * @generated
   */
  void setLeft(Filter value);

  /**
   * Returns the value of the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Right</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Right</em>' containment reference.
   * @see #setRight(Filter)
   * @see com.b2international.snowowl.snomed.ql.QlPackage#getExclusion_Right()
   * @model containment="true"
   * @generated
   */
  Filter getRight();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.Exclusion#getRight <em>Right</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Right</em>' containment reference.
   * @see #getRight()
   * @generated
   */
  void setRight(Filter value);

} // Exclusion
