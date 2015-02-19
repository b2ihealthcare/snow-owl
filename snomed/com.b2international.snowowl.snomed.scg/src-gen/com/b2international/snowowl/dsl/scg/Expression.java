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
package com.b2international.snowowl.dsl.scg;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.scg.Expression#getConcepts <em>Concepts</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.scg.Expression#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.scg.Expression#getGroups <em>Groups</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.dsl.scg.ScgPackage#getExpression()
 * @model
 * @generated
 */
public interface Expression extends AttributeValue
{
  /**
   * Returns the value of the '<em><b>Concepts</b></em>' containment reference list.
   * The list contents are of type {@link com.b2international.snowowl.dsl.scg.Concept}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Concepts</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Concepts</em>' containment reference list.
   * @see com.b2international.snowowl.dsl.scg.ScgPackage#getExpression_Concepts()
   * @model containment="true"
   * @generated
   */
  EList<Concept> getConcepts();

  /**
   * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
   * The list contents are of type {@link com.b2international.snowowl.dsl.scg.Attribute}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attributes</em>' containment reference list.
   * @see com.b2international.snowowl.dsl.scg.ScgPackage#getExpression_Attributes()
   * @model containment="true"
   * @generated
   */
  EList<Attribute> getAttributes();

  /**
   * Returns the value of the '<em><b>Groups</b></em>' containment reference list.
   * The list contents are of type {@link com.b2international.snowowl.dsl.scg.Group}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Groups</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Groups</em>' containment reference list.
   * @see com.b2international.snowowl.dsl.scg.ScgPackage#getExpression_Groups()
   * @model containment="true"
   * @generated
   */
  EList<Group> getGroups();

} // Expression