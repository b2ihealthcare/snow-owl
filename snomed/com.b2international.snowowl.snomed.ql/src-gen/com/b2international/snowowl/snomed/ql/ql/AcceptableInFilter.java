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
package com.b2international.snowowl.snomed.ql.ql;

import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Acceptable In Filter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter#getLanguageRefSetId <em>Language Ref Set Id</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getAcceptableInFilter()
 * @model
 * @generated
 */
public interface AcceptableInFilter extends PropertyFilter
{
  /**
   * Returns the value of the '<em><b>Language Ref Set Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Language Ref Set Id</em>' containment reference.
   * @see #setLanguageRefSetId(ExpressionConstraint)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getAcceptableInFilter_LanguageRefSetId()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getLanguageRefSetId();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter#getLanguageRefSetId <em>Language Ref Set Id</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Language Ref Set Id</em>' containment reference.
   * @see #getLanguageRefSetId()
   * @generated
   */
  void setLanguageRefSetId(ExpressionConstraint value);

} // AcceptableInFilter
