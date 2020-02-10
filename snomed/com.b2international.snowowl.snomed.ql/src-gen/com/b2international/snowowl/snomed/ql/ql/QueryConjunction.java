/**
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ql.ql;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query Conjunction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.QueryConjunction#getLeft <em>Left</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.QueryConjunction#getRight <em>Right</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getQueryConjunction()
 * @model
 * @generated
 */
public interface QueryConjunction extends QueryConstraint
{
  /**
   * Returns the value of the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Left</em>' containment reference.
   * @see #setLeft(QueryConstraint)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getQueryConjunction_Left()
   * @model containment="true"
   * @generated
   */
  QueryConstraint getLeft();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.QueryConjunction#getLeft <em>Left</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Left</em>' containment reference.
   * @see #getLeft()
   * @generated
   */
  void setLeft(QueryConstraint value);

  /**
   * Returns the value of the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Right</em>' containment reference.
   * @see #setRight(QueryConstraint)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getQueryConjunction_Right()
   * @model containment="true"
   * @generated
   */
  QueryConstraint getRight();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.QueryConjunction#getRight <em>Right</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Right</em>' containment reference.
   * @see #getRight()
   * @generated
   */
  void setRight(QueryConstraint value);

} // QueryConjunction
