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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Decimal Range</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.DecimalRange#getMinimum <em>Minimum</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.DecimalRange#getMaximum <em>Maximum</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getDecimalRange()
 * @model
 * @generated
 */
public interface DecimalRange extends DecimalValues
{
  /**
   * Returns the value of the '<em><b>Minimum</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Minimum</em>' containment reference.
   * @see #setMinimum(DecimalMinimumValue)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getDecimalRange_Minimum()
   * @model containment="true"
   * @generated
   */
  DecimalMinimumValue getMinimum();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.DecimalRange#getMinimum <em>Minimum</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Minimum</em>' containment reference.
   * @see #getMinimum()
   * @generated
   */
  void setMinimum(DecimalMinimumValue value);

  /**
   * Returns the value of the '<em><b>Maximum</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Maximum</em>' containment reference.
   * @see #setMaximum(DecimalMaximumValue)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getDecimalRange_Maximum()
   * @model containment="true"
   * @generated
   */
  DecimalMaximumValue getMaximum();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.DecimalRange#getMaximum <em>Maximum</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Maximum</em>' containment reference.
   * @see #getMaximum()
   * @generated
   */
  void setMaximum(DecimalMaximumValue value);

} // DecimalRange
