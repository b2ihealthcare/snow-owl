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
 * A representation of the model object '<em><b>Attribute Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.EclAttributeGroup#getCardinality <em>Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.EclAttributeGroup#getRefinement <em>Refinement</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getEclAttributeGroup()
 * @model
 * @generated
 */
public interface EclAttributeGroup extends EclRefinement
{
  /**
   * Returns the value of the '<em><b>Cardinality</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cardinality</em>' containment reference.
   * @see #setCardinality(Cardinality)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getEclAttributeGroup_Cardinality()
   * @model containment="true"
   * @generated
   */
  Cardinality getCardinality();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.EclAttributeGroup#getCardinality <em>Cardinality</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Cardinality</em>' containment reference.
   * @see #getCardinality()
   * @generated
   */
  void setCardinality(Cardinality value);

  /**
   * Returns the value of the '<em><b>Refinement</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Refinement</em>' containment reference.
   * @see #setRefinement(EclRefinement)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getEclAttributeGroup_Refinement()
   * @model containment="true"
   * @generated
   */
  EclRefinement getRefinement();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.EclAttributeGroup#getRefinement <em>Refinement</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Refinement</em>' containment reference.
   * @see #getRefinement()
   * @generated
   */
  void setRefinement(EclRefinement value);

} // EclAttributeGroup
