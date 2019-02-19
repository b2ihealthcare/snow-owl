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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Description Filter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getTermFilter <em>Term Filter</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getActive <em>Active</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getType <em>Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getRegex <em>Regex</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getAcceptableIn <em>Acceptable In</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getPreferredIn <em>Preferred In</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getLanguageRefSet <em>Language Ref Set</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDescriptionFilter()
 * @model
 * @generated
 */
public interface DescriptionFilter extends EObject
{
  /**
   * Returns the value of the '<em><b>Term Filter</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Term Filter</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Term Filter</em>' containment reference.
   * @see #setTermFilter(TermFilter)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDescriptionFilter_TermFilter()
   * @model containment="true"
   * @generated
   */
  TermFilter getTermFilter();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getTermFilter <em>Term Filter</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Term Filter</em>' containment reference.
   * @see #getTermFilter()
   * @generated
   */
  void setTermFilter(TermFilter value);

  /**
   * Returns the value of the '<em><b>Active</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Active</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Active</em>' containment reference.
   * @see #setActive(ActiveTerm)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDescriptionFilter_Active()
   * @model containment="true"
   * @generated
   */
  ActiveTerm getActive();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getActive <em>Active</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Active</em>' containment reference.
   * @see #getActive()
   * @generated
   */
  void setActive(ActiveTerm value);

  /**
   * Returns the value of the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type</em>' containment reference.
   * @see #setType(Descriptiontype)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDescriptionFilter_Type()
   * @model containment="true"
   * @generated
   */
  Descriptiontype getType();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getType <em>Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' containment reference.
   * @see #getType()
   * @generated
   */
  void setType(Descriptiontype value);

  /**
   * Returns the value of the '<em><b>Regex</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Regex</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Regex</em>' containment reference.
   * @see #setRegex(RegularExpression)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDescriptionFilter_Regex()
   * @model containment="true"
   * @generated
   */
  RegularExpression getRegex();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getRegex <em>Regex</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Regex</em>' containment reference.
   * @see #getRegex()
   * @generated
   */
  void setRegex(RegularExpression value);

  /**
   * Returns the value of the '<em><b>Acceptable In</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Acceptable In</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Acceptable In</em>' containment reference.
   * @see #setAcceptableIn(AcceptableIn)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDescriptionFilter_AcceptableIn()
   * @model containment="true"
   * @generated
   */
  AcceptableIn getAcceptableIn();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getAcceptableIn <em>Acceptable In</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Acceptable In</em>' containment reference.
   * @see #getAcceptableIn()
   * @generated
   */
  void setAcceptableIn(AcceptableIn value);

  /**
   * Returns the value of the '<em><b>Preferred In</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Preferred In</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Preferred In</em>' containment reference.
   * @see #setPreferredIn(PreferredIn)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDescriptionFilter_PreferredIn()
   * @model containment="true"
   * @generated
   */
  PreferredIn getPreferredIn();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getPreferredIn <em>Preferred In</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Preferred In</em>' containment reference.
   * @see #getPreferredIn()
   * @generated
   */
  void setPreferredIn(PreferredIn value);

  /**
   * Returns the value of the '<em><b>Language Ref Set</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Language Ref Set</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Language Ref Set</em>' containment reference.
   * @see #setLanguageRefSet(LanguageRefSet)
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#getDescriptionFilter_LanguageRefSet()
   * @model containment="true"
   * @generated
   */
  LanguageRefSet getLanguageRefSet();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getLanguageRefSet <em>Language Ref Set</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Language Ref Set</em>' containment reference.
   * @see #getLanguageRefSet()
   * @generated
   */
  void setLanguageRefSet(LanguageRefSet value);

} // DescriptionFilter
