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
package com.b2international.snowowl.dsl.escg;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Numerical Assignment Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getIngredientConcept <em>Ingredient Concept</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getSubstance <em>Substance</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getNumericValue <em>Numeric Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.dsl.escg.EscgPackage#getNumericalAssignmentGroup()
 * @model
 * @generated
 */
public interface NumericalAssignmentGroup extends AttributeAssignment
{
  /**
   * Returns the value of the '<em><b>Ingredient Concept</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ingredient Concept</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ingredient Concept</em>' containment reference.
   * @see #setIngredientConcept(Concept)
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#getNumericalAssignmentGroup_IngredientConcept()
   * @model containment="true"
   * @generated
   */
  Concept getIngredientConcept();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getIngredientConcept <em>Ingredient Concept</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ingredient Concept</em>' containment reference.
   * @see #getIngredientConcept()
   * @generated
   */
  void setIngredientConcept(Concept value);

  /**
   * Returns the value of the '<em><b>Substance</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Substance</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Substance</em>' containment reference.
   * @see #setSubstance(RValue)
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#getNumericalAssignmentGroup_Substance()
   * @model containment="true"
   * @generated
   */
  RValue getSubstance();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getSubstance <em>Substance</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Substance</em>' containment reference.
   * @see #getSubstance()
   * @generated
   */
  void setSubstance(RValue value);

  /**
   * Returns the value of the '<em><b>Numeric Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Numeric Value</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Numeric Value</em>' containment reference.
   * @see #setNumericValue(NumericalAssignment)
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#getNumericalAssignmentGroup_NumericValue()
   * @model containment="true"
   * @generated
   */
  NumericalAssignment getNumericValue();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getNumericValue <em>Numeric Value</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Numeric Value</em>' containment reference.
   * @see #getNumericValue()
   * @generated
   */
  void setNumericValue(NumericalAssignment value);

} // NumericalAssignmentGroup