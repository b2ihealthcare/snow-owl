/**
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.etl.etl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Focus Concept</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.FocusConcept#getSlot <em>Slot</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.FocusConcept#getConcept <em>Concept</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getFocusConcept()
 * @model
 * @generated
 */
public interface FocusConcept extends EObject
{
  /**
   * Returns the value of the '<em><b>Slot</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Slot</em>' containment reference.
   * @see #setSlot(TemplateInformationSlot)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getFocusConcept_Slot()
   * @model containment="true"
   * @generated
   */
  TemplateInformationSlot getSlot();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.FocusConcept#getSlot <em>Slot</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Slot</em>' containment reference.
   * @see #getSlot()
   * @generated
   */
  void setSlot(TemplateInformationSlot value);

  /**
   * Returns the value of the '<em><b>Concept</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Concept</em>' containment reference.
   * @see #setConcept(ConceptReference)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getFocusConcept_Concept()
   * @model containment="true"
   * @generated
   */
  ConceptReference getConcept();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.FocusConcept#getConcept <em>Concept</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Concept</em>' containment reference.
   * @see #getConcept()
   * @generated
   */
  void setConcept(ConceptReference value);

} // FocusConcept
