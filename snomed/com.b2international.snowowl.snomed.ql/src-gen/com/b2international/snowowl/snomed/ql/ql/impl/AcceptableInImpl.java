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

import com.b2international.snowowl.snomed.ql.ql.AcceptableIn;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Acceptable In</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.AcceptableInImpl#getAcceptable <em>Acceptable</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AcceptableInImpl extends MinimalEObjectImpl.Container implements AcceptableIn
{
  /**
   * The cached value of the '{@link #getAcceptable() <em>Acceptable</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAcceptable()
   * @generated
   * @ordered
   */
  protected Script acceptable;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AcceptableInImpl()
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
    return QlPackage.Literals.ACCEPTABLE_IN;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Script getAcceptable()
  {
    return acceptable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetAcceptable(Script newAcceptable, NotificationChain msgs)
  {
    Script oldAcceptable = acceptable;
    acceptable = newAcceptable;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QlPackage.ACCEPTABLE_IN__ACCEPTABLE, oldAcceptable, newAcceptable);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAcceptable(Script newAcceptable)
  {
    if (newAcceptable != acceptable)
    {
      NotificationChain msgs = null;
      if (acceptable != null)
        msgs = ((InternalEObject)acceptable).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QlPackage.ACCEPTABLE_IN__ACCEPTABLE, null, msgs);
      if (newAcceptable != null)
        msgs = ((InternalEObject)newAcceptable).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QlPackage.ACCEPTABLE_IN__ACCEPTABLE, null, msgs);
      msgs = basicSetAcceptable(newAcceptable, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.ACCEPTABLE_IN__ACCEPTABLE, newAcceptable, newAcceptable));
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
      case QlPackage.ACCEPTABLE_IN__ACCEPTABLE:
        return basicSetAcceptable(null, msgs);
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
      case QlPackage.ACCEPTABLE_IN__ACCEPTABLE:
        return getAcceptable();
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
      case QlPackage.ACCEPTABLE_IN__ACCEPTABLE:
        setAcceptable((Script)newValue);
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
      case QlPackage.ACCEPTABLE_IN__ACCEPTABLE:
        setAcceptable((Script)null);
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
      case QlPackage.ACCEPTABLE_IN__ACCEPTABLE:
        return acceptable != null;
    }
    return super.eIsSet(featureID);
  }

} //AcceptableInImpl
