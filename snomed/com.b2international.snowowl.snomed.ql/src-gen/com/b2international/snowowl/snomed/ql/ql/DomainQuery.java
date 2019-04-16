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
 * A representation of the model object '<em><b>Domain Query</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.DomainQuery#getEcl <em>Ecl</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.DomainQuery#getFilter <em>Filter</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDomainQuery()
 * @model
 * @generated
 */
public interface DomainQuery extends SubQuery
{
  /**
   * Returns the value of the '<em><b>Ecl</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ecl</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ecl</em>' containment reference.
   * @see #setEcl(ExpressionConstraint)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDomainQuery_Ecl()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getEcl();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.DomainQuery#getEcl <em>Ecl</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ecl</em>' containment reference.
   * @see #getEcl()
   * @generated
   */
  void setEcl(ExpressionConstraint value);

  /**
   * Returns the value of the '<em><b>Filter</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Filter</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Filter</em>' containment reference.
   * @see #setFilter(Filter)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDomainQuery_Filter()
   * @model containment="true"
   * @generated
   */
  Filter getFilter();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.DomainQuery#getFilter <em>Filter</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Filter</em>' containment reference.
   * @see #getFilter()
   * @generated
   */
  void setFilter(Filter value);

} // DomainQuery
