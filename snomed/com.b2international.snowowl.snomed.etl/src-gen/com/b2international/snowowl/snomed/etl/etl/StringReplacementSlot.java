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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>String Replacement Slot</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.StringReplacementSlot#getValues <em>Values</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getStringReplacementSlot()
 * @model
 * @generated
 */
public interface StringReplacementSlot extends ConcreteValueReplacementSlot
{
  /**
   * Returns the value of the '<em><b>Values</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Values</em>' attribute list.
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getStringReplacementSlot_Values()
   * @model unique="false"
   * @generated
   */
  EList<String> getValues();

} // StringReplacementSlot
