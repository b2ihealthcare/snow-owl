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
 * A representation of the model object '<em><b>Concept Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.escg.ConceptGroup#getConstraint <em>Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.escg.ConceptGroup#getConcept <em>Concept</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.dsl.escg.EscgPackage#getConceptGroup()
 * @model
 * @generated
 */
public interface ConceptGroup extends LValue
{
  /**
   * Returns the value of the '<em><b>Constraint</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Constraint</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Constraint</em>' attribute.
   * @see #setConstraint(String)
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#getConceptGroup_Constraint()
   * @model
   * @generated
   */
  String getConstraint();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.dsl.escg.ConceptGroup#getConstraint <em>Constraint</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Constraint</em>' attribute.
   * @see #getConstraint()
   * @generated
   */
  void setConstraint(String value);

  /**
   * Returns the value of the '<em><b>Concept</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Concept</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Concept</em>' containment reference.
   * @see #setConcept(Concept)
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#getConceptGroup_Concept()
   * @model containment="true"
   * @generated
   */
  Concept getConcept();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.dsl.escg.ConceptGroup#getConcept <em>Concept</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Concept</em>' containment reference.
   * @see #getConcept()
   * @generated
   */
  void setConcept(Concept value);

} // ConceptGroup