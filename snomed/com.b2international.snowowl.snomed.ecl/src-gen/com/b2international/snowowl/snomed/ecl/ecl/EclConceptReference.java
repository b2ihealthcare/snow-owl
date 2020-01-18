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
package com.b2international.snowowl.snomed.ecl.ecl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Concept Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.EclConceptReference#getId <em>Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.EclConceptReference#getTerm <em>Term</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getEclConceptReference()
 * @model
 * @generated
 */
public interface EclConceptReference extends ExpressionConstraint
{
  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' attribute.
   * @see #setId(String)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getEclConceptReference_Id()
   * @model
   * @generated
   */
  String getId();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.EclConceptReference#getId <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Id</em>' attribute.
   * @see #getId()
   * @generated
   */
  void setId(String value);

  /**
   * Returns the value of the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Term</em>' attribute.
   * @see #setTerm(String)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getEclConceptReference_Term()
   * @model
   * @generated
   */
  String getTerm();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.EclConceptReference#getTerm <em>Term</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Term</em>' attribute.
   * @see #getTerm()
   * @generated
   */
  void setTerm(String value);

} // EclConceptReference
