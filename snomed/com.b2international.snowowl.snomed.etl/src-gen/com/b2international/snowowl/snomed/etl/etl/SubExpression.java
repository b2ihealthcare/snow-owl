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
package com.b2international.snowowl.snomed.etl.etl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sub Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.SubExpression#getFocusConcepts <em>Focus Concepts</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.SubExpression#getRefinement <em>Refinement</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getSubExpression()
 * @model
 * @generated
 */
public interface SubExpression extends AttributeValue
{
  /**
   * Returns the value of the '<em><b>Focus Concepts</b></em>' containment reference list.
   * The list contents are of type {@link com.b2international.snowowl.snomed.etl.etl.FocusConcept}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Focus Concepts</em>' containment reference list.
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getSubExpression_FocusConcepts()
   * @model containment="true"
   * @generated
   */
  EList<FocusConcept> getFocusConcepts();

  /**
   * Returns the value of the '<em><b>Refinement</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Refinement</em>' containment reference.
   * @see #setRefinement(Refinement)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getSubExpression_Refinement()
   * @model containment="true"
   * @generated
   */
  Refinement getRefinement();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.SubExpression#getRefinement <em>Refinement</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Refinement</em>' containment reference.
   * @see #getRefinement()
   * @generated
   */
  void setRefinement(Refinement value);

} // SubExpression
