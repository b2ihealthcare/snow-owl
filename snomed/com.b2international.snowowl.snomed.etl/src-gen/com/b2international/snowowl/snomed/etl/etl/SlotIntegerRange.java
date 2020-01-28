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
 * A representation of the model object '<em><b>Slot Integer Range</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange#getMinimum <em>Minimum</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange#getMaximum <em>Maximum</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getSlotIntegerRange()
 * @model
 * @generated
 */
public interface SlotIntegerRange extends SlotInteger
{
  /**
   * Returns the value of the '<em><b>Minimum</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Minimum</em>' containment reference.
   * @see #setMinimum(SlotIntegerMinimumValue)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getSlotIntegerRange_Minimum()
   * @model containment="true"
   * @generated
   */
  SlotIntegerMinimumValue getMinimum();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange#getMinimum <em>Minimum</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Minimum</em>' containment reference.
   * @see #getMinimum()
   * @generated
   */
  void setMinimum(SlotIntegerMinimumValue value);

  /**
   * Returns the value of the '<em><b>Maximum</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Maximum</em>' containment reference.
   * @see #setMaximum(SlotIntegerMaximumValue)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getSlotIntegerRange_Maximum()
   * @model containment="true"
   * @generated
   */
  SlotIntegerMaximumValue getMaximum();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange#getMaximum <em>Maximum</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Maximum</em>' containment reference.
   * @see #getMaximum()
   * @generated
   */
  void setMaximum(SlotIntegerMaximumValue value);

} // SlotIntegerRange
