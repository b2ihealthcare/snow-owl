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

import com.b2international.snowowl.snomed.ql.ql.ActiveFilter;
import com.b2international.snowowl.snomed.ql.ql.Domain;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Active Filter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.ActiveFilterImpl#getDomain <em>Domain</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.ActiveFilterImpl#isActive <em>Active</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActiveFilterImpl extends PropertyFilterImpl implements ActiveFilter
{
  /**
   * The default value of the '{@link #getDomain() <em>Domain</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDomain()
   * @generated
   * @ordered
   */
  protected static final Domain DOMAIN_EDEFAULT = Domain.CONCEPT;

  /**
   * The cached value of the '{@link #getDomain() <em>Domain</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDomain()
   * @generated
   * @ordered
   */
  protected Domain domain = DOMAIN_EDEFAULT;

  /**
   * The default value of the '{@link #isActive() <em>Active</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isActive()
   * @generated
   * @ordered
   */
  protected static final boolean ACTIVE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isActive() <em>Active</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isActive()
   * @generated
   * @ordered
   */
  protected boolean active = ACTIVE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ActiveFilterImpl()
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
    return QlPackage.Literals.ACTIVE_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Domain getDomain()
  {
    return domain;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDomain(Domain newDomain)
  {
    Domain oldDomain = domain;
    domain = newDomain == null ? DOMAIN_EDEFAULT : newDomain;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.ACTIVE_FILTER__DOMAIN, oldDomain, domain));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isActive()
  {
    return active;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setActive(boolean newActive)
  {
    boolean oldActive = active;
    active = newActive;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.ACTIVE_FILTER__ACTIVE, oldActive, active));
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
      case QlPackage.ACTIVE_FILTER__DOMAIN:
        return getDomain();
      case QlPackage.ACTIVE_FILTER__ACTIVE:
        return isActive();
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
      case QlPackage.ACTIVE_FILTER__DOMAIN:
        setDomain((Domain)newValue);
        return;
      case QlPackage.ACTIVE_FILTER__ACTIVE:
        setActive((Boolean)newValue);
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
      case QlPackage.ACTIVE_FILTER__DOMAIN:
        setDomain(DOMAIN_EDEFAULT);
        return;
      case QlPackage.ACTIVE_FILTER__ACTIVE:
        setActive(ACTIVE_EDEFAULT);
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
      case QlPackage.ACTIVE_FILTER__DOMAIN:
        return domain != DOMAIN_EDEFAULT;
      case QlPackage.ACTIVE_FILTER__ACTIVE:
        return active != ACTIVE_EDEFAULT;
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
    result.append(" (domain: ");
    result.append(domain);
    result.append(", active: ");
    result.append(active);
    result.append(')');
    return result.toString();
  }

} //ActiveFilterImpl
