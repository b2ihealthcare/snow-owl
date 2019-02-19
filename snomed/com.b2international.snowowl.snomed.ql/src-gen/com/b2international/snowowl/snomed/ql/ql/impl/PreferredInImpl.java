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

import com.b2international.snowowl.snomed.ql.ql.PreferredIn;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Preferred In</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.PreferredInImpl#getPreferred <em>Preferred</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PreferredInImpl extends MinimalEObjectImpl.Container implements PreferredIn
{
  /**
   * The cached value of the '{@link #getPreferred() <em>Preferred</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPreferred()
   * @generated
   * @ordered
   */
  protected Script preferred;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PreferredInImpl()
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
    return QlPackage.Literals.PREFERRED_IN;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Script getPreferred()
  {
    return preferred;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetPreferred(Script newPreferred, NotificationChain msgs)
  {
    Script oldPreferred = preferred;
    preferred = newPreferred;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QlPackage.PREFERRED_IN__PREFERRED, oldPreferred, newPreferred);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPreferred(Script newPreferred)
  {
    if (newPreferred != preferred)
    {
      NotificationChain msgs = null;
      if (preferred != null)
        msgs = ((InternalEObject)preferred).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QlPackage.PREFERRED_IN__PREFERRED, null, msgs);
      if (newPreferred != null)
        msgs = ((InternalEObject)newPreferred).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QlPackage.PREFERRED_IN__PREFERRED, null, msgs);
      msgs = basicSetPreferred(newPreferred, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.PREFERRED_IN__PREFERRED, newPreferred, newPreferred));
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
      case QlPackage.PREFERRED_IN__PREFERRED:
        return basicSetPreferred(null, msgs);
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
      case QlPackage.PREFERRED_IN__PREFERRED:
        return getPreferred();
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
      case QlPackage.PREFERRED_IN__PREFERRED:
        setPreferred((Script)newValue);
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
      case QlPackage.PREFERRED_IN__PREFERRED:
        setPreferred((Script)null);
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
      case QlPackage.PREFERRED_IN__PREFERRED:
        return preferred != null;
    }
    return super.eIsSet(featureID);
  }

} //PreferredInImpl
