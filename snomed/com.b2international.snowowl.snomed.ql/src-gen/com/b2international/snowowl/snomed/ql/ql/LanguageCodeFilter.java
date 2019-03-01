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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Language Code Filter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.LanguageCodeFilter#getLanguageCode <em>Language Code</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getLanguageCodeFilter()
 * @model
 * @generated
 */
public interface LanguageCodeFilter extends PropertyFilter
{
  /**
   * Returns the value of the '<em><b>Language Code</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Language Code</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Language Code</em>' attribute.
   * @see #setLanguageCode(String)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getLanguageCodeFilter_LanguageCode()
   * @model
   * @generated
   */
  String getLanguageCode();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.LanguageCodeFilter#getLanguageCode <em>Language Code</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Language Code</em>' attribute.
   * @see #getLanguageCode()
   * @generated
   */
  void setLanguageCode(String value);

} // LanguageCodeFilter
