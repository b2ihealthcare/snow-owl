/**
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
 * A representation of the model object '<em><b>Attribute Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getCardinality <em>Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#isReversed <em>Reversed</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getAttribute <em>Attribute</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getComparison <em>Comparison</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getAttributeConstraint()
 * @model
 * @generated
 */
public interface AttributeConstraint extends Refinement
{
  /**
   * Returns the value of the '<em><b>Cardinality</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cardinality</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cardinality</em>' containment reference.
   * @see #setCardinality(Cardinality)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getAttributeConstraint_Cardinality()
   * @model containment="true"
   * @generated
   */
  Cardinality getCardinality();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getCardinality <em>Cardinality</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Cardinality</em>' containment reference.
   * @see #getCardinality()
   * @generated
   */
  void setCardinality(Cardinality value);

  /**
   * Returns the value of the '<em><b>Reversed</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Reversed</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Reversed</em>' attribute.
   * @see #setReversed(boolean)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getAttributeConstraint_Reversed()
   * @model
   * @generated
   */
  boolean isReversed();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#isReversed <em>Reversed</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Reversed</em>' attribute.
   * @see #isReversed()
   * @generated
   */
  void setReversed(boolean value);

  /**
   * Returns the value of the '<em><b>Attribute</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute</em>' containment reference.
   * @see #setAttribute(ExpressionConstraint)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getAttributeConstraint_Attribute()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getAttribute();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getAttribute <em>Attribute</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Attribute</em>' containment reference.
   * @see #getAttribute()
   * @generated
   */
  void setAttribute(ExpressionConstraint value);

  /**
   * Returns the value of the '<em><b>Comparison</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Comparison</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Comparison</em>' containment reference.
   * @see #setComparison(Comparison)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getAttributeConstraint_Comparison()
   * @model containment="true"
   * @generated
   */
  Comparison getComparison();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getComparison <em>Comparison</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Comparison</em>' containment reference.
   * @see #getComparison()
   * @generated
   */
  void setComparison(Comparison value);

} // AttributeConstraint
