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
 * A representation of the model object '<em><b>Refinements</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.escg.Refinements#getAttributeSet <em>Attribute Set</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.escg.Refinements#getAttributeGroups <em>Attribute Groups</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.dsl.escg.EscgPackage#getRefinements()
 * @model
 * @generated
 */
public interface Refinements extends EObject
{
  /**
   * Returns the value of the '<em><b>Attribute Set</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute Set</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute Set</em>' containment reference.
   * @see #setAttributeSet(AttributeSet)
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#getRefinements_AttributeSet()
   * @model containment="true"
   * @generated
   */
  AttributeSet getAttributeSet();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.dsl.escg.Refinements#getAttributeSet <em>Attribute Set</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Attribute Set</em>' containment reference.
   * @see #getAttributeSet()
   * @generated
   */
  void setAttributeSet(AttributeSet value);

  /**
   * Returns the value of the '<em><b>Attribute Groups</b></em>' containment reference list.
   * The list contents are of type {@link com.b2international.snowowl.dsl.escg.AttributeGroup}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute Groups</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute Groups</em>' containment reference list.
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#getRefinements_AttributeGroups()
   * @model containment="true"
   * @generated
   */
  EList<AttributeGroup> getAttributeGroups();

} // Refinements