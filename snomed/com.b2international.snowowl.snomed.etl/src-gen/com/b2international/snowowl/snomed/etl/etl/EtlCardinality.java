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
 * A representation of the model object '<em><b>Cardinality</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.EtlCardinality#getMin <em>Min</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.EtlCardinality#getMax <em>Max</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getEtlCardinality()
 * @model
 * @generated
 */
public interface EtlCardinality extends EObject
{
  /**
   * Returns the value of the '<em><b>Min</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Min</em>' attribute.
   * @see #setMin(int)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getEtlCardinality_Min()
   * @model
   * @generated
   */
  int getMin();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.EtlCardinality#getMin <em>Min</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Min</em>' attribute.
   * @see #getMin()
   * @generated
   */
  void setMin(int value);

  /**
   * Returns the value of the '<em><b>Max</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Max</em>' attribute.
   * @see #setMax(int)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getEtlCardinality_Max()
   * @model
   * @generated
   */
  int getMax();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.EtlCardinality#getMax <em>Max</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Max</em>' attribute.
   * @see #getMax()
   * @generated
   */
  void setMax(int value);

} // EtlCardinality
