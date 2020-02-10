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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Expression Template</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#isPrimitive <em>Primitive</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#getSlot <em>Slot</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#getExpression <em>Expression</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getExpressionTemplate()
 * @model
 * @generated
 */
public interface ExpressionTemplate extends EObject
{
  /**
   * Returns the value of the '<em><b>Primitive</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Primitive</em>' attribute.
   * @see #setPrimitive(boolean)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getExpressionTemplate_Primitive()
   * @model
   * @generated
   */
  boolean isPrimitive();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#isPrimitive <em>Primitive</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Primitive</em>' attribute.
   * @see #isPrimitive()
   * @generated
   */
  void setPrimitive(boolean value);

  /**
   * Returns the value of the '<em><b>Slot</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Slot</em>' containment reference.
   * @see #setSlot(TokenReplacementSlot)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getExpressionTemplate_Slot()
   * @model containment="true"
   * @generated
   */
  TokenReplacementSlot getSlot();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#getSlot <em>Slot</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Slot</em>' containment reference.
   * @see #getSlot()
   * @generated
   */
  void setSlot(TokenReplacementSlot value);

  /**
   * Returns the value of the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expression</em>' containment reference.
   * @see #setExpression(SubExpression)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getExpressionTemplate_Expression()
   * @model containment="true"
   * @generated
   */
  SubExpression getExpression();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#getExpression <em>Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expression</em>' containment reference.
   * @see #getExpression()
   * @generated
   */
  void setExpression(SubExpression value);

} // ExpressionTemplate
