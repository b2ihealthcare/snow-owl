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

import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;

import com.b2international.snowowl.snomed.ql.ql.PreferredInFilter;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Preferred In Filter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.PreferredInFilterImpl#getLanguageRefSetId <em>Language Ref Set Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PreferredInFilterImpl extends FilterImpl implements PreferredInFilter
{
  /**
   * The cached value of the '{@link #getLanguageRefSetId() <em>Language Ref Set Id</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLanguageRefSetId()
   * @generated
   * @ordered
   */
  protected ExpressionConstraint languageRefSetId;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PreferredInFilterImpl()
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
    return QlPackage.Literals.PREFERRED_IN_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionConstraint getLanguageRefSetId()
  {
    return languageRefSetId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetLanguageRefSetId(ExpressionConstraint newLanguageRefSetId, NotificationChain msgs)
  {
    ExpressionConstraint oldLanguageRefSetId = languageRefSetId;
    languageRefSetId = newLanguageRefSetId;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QlPackage.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID, oldLanguageRefSetId, newLanguageRefSetId);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLanguageRefSetId(ExpressionConstraint newLanguageRefSetId)
  {
    if (newLanguageRefSetId != languageRefSetId)
    {
      NotificationChain msgs = null;
      if (languageRefSetId != null)
        msgs = ((InternalEObject)languageRefSetId).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QlPackage.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID, null, msgs);
      if (newLanguageRefSetId != null)
        msgs = ((InternalEObject)newLanguageRefSetId).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QlPackage.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID, null, msgs);
      msgs = basicSetLanguageRefSetId(newLanguageRefSetId, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID, newLanguageRefSetId, newLanguageRefSetId));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case QlPackage.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID:
        return basicSetLanguageRefSetId(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
      case QlPackage.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID:
        return getLanguageRefSetId();
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
      case QlPackage.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID:
        setLanguageRefSetId((ExpressionConstraint)newValue);
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
      case QlPackage.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID:
        setLanguageRefSetId((ExpressionConstraint)null);
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
      case QlPackage.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID:
        return languageRefSetId != null;
    }
    return super.eIsSet(featureID);
  }

} //PreferredInFilterImpl
