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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attribute Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.AttributeGroup#getSlot <em>Slot</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.AttributeGroup#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getAttributeGroup()
 * @model
 * @generated
 */
public interface AttributeGroup extends EObject
{
  /**
   * Returns the value of the '<em><b>Slot</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Slot</em>' containment reference.
   * @see #setSlot(TemplateInformationSlot)
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getAttributeGroup_Slot()
   * @model containment="true"
   * @generated
   */
  TemplateInformationSlot getSlot();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.etl.etl.AttributeGroup#getSlot <em>Slot</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Slot</em>' containment reference.
   * @see #getSlot()
   * @generated
   */
  void setSlot(TemplateInformationSlot value);

  /**
   * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
   * The list contents are of type {@link com.b2international.snowowl.snomed.etl.etl.Attribute}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attributes</em>' containment reference list.
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#getAttributeGroup_Attributes()
   * @model containment="true"
   * @generated
   */
  EList<Attribute> getAttributes();

} // AttributeGroup
