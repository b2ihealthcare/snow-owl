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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sub Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.escg.SubExpression#getLValues <em>LValues</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.escg.SubExpression#getRefinements <em>Refinements</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.dsl.escg.EscgPackage#getSubExpression()
 * @model
 * @generated
 */
public interface SubExpression extends EObject
{
  /**
   * Returns the value of the '<em><b>LValues</b></em>' containment reference list.
   * The list contents are of type {@link com.b2international.snowowl.dsl.escg.LValue}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>LValues</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>LValues</em>' containment reference list.
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#getSubExpression_LValues()
   * @model containment="true"
   * @generated
   */
  EList<LValue> getLValues();

  /**
   * Returns the value of the '<em><b>Refinements</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Refinements</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Refinements</em>' containment reference.
   * @see #setRefinements(Refinements)
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#getSubExpression_Refinements()
   * @model containment="true"
   * @generated
   */
  Refinements getRefinements();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.dsl.escg.SubExpression#getRefinements <em>Refinements</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Refinements</em>' containment reference.
   * @see #getRefinements()
   * @generated
   */
  void setRefinements(Refinements value);

} // SubExpression