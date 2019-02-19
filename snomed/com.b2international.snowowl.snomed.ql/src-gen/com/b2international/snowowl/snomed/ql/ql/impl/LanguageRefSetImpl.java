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

import com.b2international.snowowl.snomed.ecl.ecl.Script;

import com.b2international.snowowl.snomed.ql.ql.LanguageRefSet;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Language Ref Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.LanguageRefSetImpl#getRefset <em>Refset</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LanguageRefSetImpl extends MinimalEObjectImpl.Container implements LanguageRefSet
{
  /**
   * The cached value of the '{@link #getRefset() <em>Refset</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRefset()
   * @generated
   * @ordered
   */
  protected Script refset;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LanguageRefSetImpl()
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
    return QlPackage.Literals.LANGUAGE_REF_SET;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Script getRefset()
  {
    return refset;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetRefset(Script newRefset, NotificationChain msgs)
  {
    Script oldRefset = refset;
    refset = newRefset;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QlPackage.LANGUAGE_REF_SET__REFSET, oldRefset, newRefset);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRefset(Script newRefset)
  {
    if (newRefset != refset)
    {
      NotificationChain msgs = null;
      if (refset != null)
        msgs = ((InternalEObject)refset).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QlPackage.LANGUAGE_REF_SET__REFSET, null, msgs);
      if (newRefset != null)
        msgs = ((InternalEObject)newRefset).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QlPackage.LANGUAGE_REF_SET__REFSET, null, msgs);
      msgs = basicSetRefset(newRefset, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.LANGUAGE_REF_SET__REFSET, newRefset, newRefset));
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
      case QlPackage.LANGUAGE_REF_SET__REFSET:
        return basicSetRefset(null, msgs);
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
      case QlPackage.LANGUAGE_REF_SET__REFSET:
        return getRefset();
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
      case QlPackage.LANGUAGE_REF_SET__REFSET:
        setRefset((Script)newValue);
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
      case QlPackage.LANGUAGE_REF_SET__REFSET:
        setRefset((Script)null);
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
      case QlPackage.LANGUAGE_REF_SET__REFSET:
        return refset != null;
    }
    return super.eIsSet(featureID);
  }

} //LanguageRefSetImpl
