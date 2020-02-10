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
package com.b2international.snowowl.snomed.ecl.ecl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Refined Expression Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getConstraint <em>Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getRefinement <em>Refinement</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getRefinedExpressionConstraint()
 * @model
 * @generated
 */
public interface RefinedExpressionConstraint extends ExpressionConstraint
{
  /**
   * Returns the value of the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Constraint</em>' containment reference.
   * @see #setConstraint(ExpressionConstraint)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getRefinedExpressionConstraint_Constraint()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getConstraint();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getConstraint <em>Constraint</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Constraint</em>' containment reference.
   * @see #getConstraint()
   * @generated
   */
  void setConstraint(ExpressionConstraint value);

  /**
   * Returns the value of the '<em><b>Refinement</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Refinement</em>' containment reference.
   * @see #setRefinement(EclRefinement)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getRefinedExpressionConstraint_Refinement()
   * @model containment="true"
   * @generated
   */
  EclRefinement getRefinement();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getRefinement <em>Refinement</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Refinement</em>' containment reference.
   * @see #getRefinement()
   * @generated
   */
  void setRefinement(EclRefinement value);

} // RefinedExpressionConstraint
