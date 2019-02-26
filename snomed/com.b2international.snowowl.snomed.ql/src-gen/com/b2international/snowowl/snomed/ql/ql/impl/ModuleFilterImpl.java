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

import com.b2international.snowowl.snomed.ql.ql.Domain;
import com.b2international.snowowl.snomed.ql.ql.ModuleFilter;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module Filter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.ModuleFilterImpl#getDomain <em>Domain</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.ModuleFilterImpl#getModuleId <em>Module Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModuleFilterImpl extends PropertyFilterImpl implements ModuleFilter
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
   * The cached value of the '{@link #getModuleId() <em>Module Id</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModuleId()
   * @generated
   * @ordered
   */
  protected ExpressionConstraint moduleId;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ModuleFilterImpl()
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
    return QlPackage.Literals.MODULE_FILTER;
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
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.MODULE_FILTER__DOMAIN, oldDomain, domain));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionConstraint getModuleId()
  {
    return moduleId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetModuleId(ExpressionConstraint newModuleId, NotificationChain msgs)
  {
    ExpressionConstraint oldModuleId = moduleId;
    moduleId = newModuleId;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QlPackage.MODULE_FILTER__MODULE_ID, oldModuleId, newModuleId);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setModuleId(ExpressionConstraint newModuleId)
  {
    if (newModuleId != moduleId)
    {
      NotificationChain msgs = null;
      if (moduleId != null)
        msgs = ((InternalEObject)moduleId).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QlPackage.MODULE_FILTER__MODULE_ID, null, msgs);
      if (newModuleId != null)
        msgs = ((InternalEObject)newModuleId).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QlPackage.MODULE_FILTER__MODULE_ID, null, msgs);
      msgs = basicSetModuleId(newModuleId, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.MODULE_FILTER__MODULE_ID, newModuleId, newModuleId));
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
      case QlPackage.MODULE_FILTER__MODULE_ID:
        return basicSetModuleId(null, msgs);
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
      case QlPackage.MODULE_FILTER__DOMAIN:
        return getDomain();
      case QlPackage.MODULE_FILTER__MODULE_ID:
        return getModuleId();
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
      case QlPackage.MODULE_FILTER__DOMAIN:
        setDomain((Domain)newValue);
        return;
      case QlPackage.MODULE_FILTER__MODULE_ID:
        setModuleId((ExpressionConstraint)newValue);
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
      case QlPackage.MODULE_FILTER__DOMAIN:
        setDomain(DOMAIN_EDEFAULT);
        return;
      case QlPackage.MODULE_FILTER__MODULE_ID:
        setModuleId((ExpressionConstraint)null);
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
      case QlPackage.MODULE_FILTER__DOMAIN:
        return domain != DOMAIN_EDEFAULT;
      case QlPackage.MODULE_FILTER__MODULE_ID:
        return moduleId != null;
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
    result.append(')');
    return result.toString();
  }

} //ModuleFilterImpl
