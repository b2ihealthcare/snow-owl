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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Concept Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference#getSlot <em>Slot</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference#getId <em>Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference#getTerm <em>Term</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getConceptReference()
 * @model
 * @generated
 */
public interface ConceptReference extends AttributeValue
{
  /**
   * Returns the value of the '<em><b>Slot</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Slot</em>' containment reference.
   * @see #setSlot(ConceptReplacementSlot)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getConceptReference_Slot()
   * @model containment="true"
   * @generated
   */
  ConceptReplacementSlot getSlot();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference#getSlot <em>Slot</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Slot</em>' containment reference.
   * @see #getSlot()
   * @generated
   */
  void setSlot(ConceptReplacementSlot value);

  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' attribute.
   * @see #setId(String)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getConceptReference_Id()
   * @model
   * @generated
   */
  String getId();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference#getId <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Id</em>' attribute.
   * @see #getId()
   * @generated
   */
  void setId(String value);

  /**
   * Returns the value of the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Term</em>' attribute.
   * @see #setTerm(String)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getConceptReference_Term()
   * @model
   * @generated
   */
  String getTerm();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference#getTerm <em>Term</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Term</em>' attribute.
   * @see #getTerm()
   * @generated
   */
  void setTerm(String value);

} // ConceptReference
