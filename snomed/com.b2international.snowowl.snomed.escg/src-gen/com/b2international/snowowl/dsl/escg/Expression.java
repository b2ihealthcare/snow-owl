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
 * A representation of the model object '<em><b>Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.escg.Expression#getSubExpression <em>Sub Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.dsl.escg.EscgPackage#getExpression()
 * @model
 * @generated
 */
public interface Expression extends EObject
{
  /**
   * Returns the value of the '<em><b>Sub Expression</b></em>' containment reference list.
   * The list contents are of type {@link com.b2international.snowowl.dsl.escg.SubExpression}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sub Expression</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Sub Expression</em>' containment reference list.
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#getExpression_SubExpression()
   * @model containment="true"
   * @generated
   */
  EList<SubExpression> getSubExpression();

} // Expression