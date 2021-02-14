/**
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Token Replacement Slot</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot#getTokens <em>Tokens</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getTokenReplacementSlot()
 * @model
 * @generated
 */
public interface TokenReplacementSlot extends EObject
{
  /**
   * Returns the value of the '<em><b>Tokens</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Tokens</em>' attribute list.
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getTokenReplacementSlot_Tokens()
   * @model unique="false"
   * @generated
   */
  EList<String> getTokens();

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getTokenReplacementSlot_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // TokenReplacementSlot
