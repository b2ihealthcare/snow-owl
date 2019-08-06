/**
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ql.ql;

import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module Filter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.ModuleFilter#getDomain <em>Domain</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.ModuleFilter#getModuleId <em>Module Id</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getModuleFilter()
 * @model
 * @generated
 */
public interface ModuleFilter extends PropertyFilter
{
  /**
   * Returns the value of the '<em><b>Domain</b></em>' attribute.
   * The literals are from the enumeration {@link com.b2international.snowowl.snomed.ql.ql.Domain}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Domain</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Domain</em>' attribute.
   * @see com.b2international.snowowl.snomed.ql.ql.Domain
   * @see #setDomain(Domain)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getModuleFilter_Domain()
   * @model
   * @generated
   */
  Domain getDomain();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.ModuleFilter#getDomain <em>Domain</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Domain</em>' attribute.
   * @see com.b2international.snowowl.snomed.ql.ql.Domain
   * @see #getDomain()
   * @generated
   */
  void setDomain(Domain value);

  /**
   * Returns the value of the '<em><b>Module Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Module Id</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Module Id</em>' containment reference.
   * @see #setModuleId(ExpressionConstraint)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getModuleFilter_ModuleId()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getModuleId();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.ModuleFilter#getModuleId <em>Module Id</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Module Id</em>' containment reference.
   * @see #getModuleId()
   * @generated
   */
  void setModuleId(ExpressionConstraint value);

} // ModuleFilter
