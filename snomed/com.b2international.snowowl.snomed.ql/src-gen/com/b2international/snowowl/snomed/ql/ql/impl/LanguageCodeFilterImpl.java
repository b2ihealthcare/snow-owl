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
package com.b2international.snowowl.snomed.ql.ql.impl;

import com.b2international.snowowl.snomed.ql.ql.LanguageCodeFilter;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Language Code Filter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.LanguageCodeFilterImpl#getLanguageCode <em>Language Code</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LanguageCodeFilterImpl extends PropertyFilterImpl implements LanguageCodeFilter
{
  /**
   * The default value of the '{@link #getLanguageCode() <em>Language Code</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLanguageCode()
   * @generated
   * @ordered
   */
  protected static final String LANGUAGE_CODE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLanguageCode() <em>Language Code</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLanguageCode()
   * @generated
   * @ordered
   */
  protected String languageCode = LANGUAGE_CODE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LanguageCodeFilterImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return QlPackage.Literals.LANGUAGE_CODE_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLanguageCode()
  {
    return languageCode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLanguageCode(String newLanguageCode)
  {
    String oldLanguageCode = languageCode;
    languageCode = newLanguageCode;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.LANGUAGE_CODE_FILTER__LANGUAGE_CODE, oldLanguageCode, languageCode));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case QlPackage.LANGUAGE_CODE_FILTER__LANGUAGE_CODE:
        return getLanguageCode();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case QlPackage.LANGUAGE_CODE_FILTER__LANGUAGE_CODE:
        setLanguageCode((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case QlPackage.LANGUAGE_CODE_FILTER__LANGUAGE_CODE:
        setLanguageCode(LANGUAGE_CODE_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case QlPackage.LANGUAGE_CODE_FILTER__LANGUAGE_CODE:
        return LANGUAGE_CODE_EDEFAULT == null ? languageCode != null : !LANGUAGE_CODE_EDEFAULT.equals(languageCode);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (languageCode: ");
    result.append(languageCode);
    result.append(')');
    return result.toString();
  }

} //LanguageCodeFilterImpl
