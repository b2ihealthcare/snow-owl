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

import com.b2international.snowowl.snomed.ql.ql.DomainQuery;
import com.b2international.snowowl.snomed.ql.ql.Filter;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Domain Query</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.DomainQueryImpl#getEcl <em>Ecl</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ql.ql.impl.DomainQueryImpl#getFilter <em>Filter</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DomainQueryImpl extends SubQueryImpl implements DomainQuery
{
  /**
   * The cached value of the '{@link #getEcl() <em>Ecl</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEcl()
   * @generated
   * @ordered
   */
  protected ExpressionConstraint ecl;

  /**
   * The cached value of the '{@link #getFilter() <em>Filter</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFilter()
   * @generated
   * @ordered
   */
  protected Filter filter;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DomainQueryImpl()
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
    return QlPackage.Literals.DOMAIN_QUERY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionConstraint getEcl()
  {
    return ecl;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetEcl(ExpressionConstraint newEcl, NotificationChain msgs)
  {
    ExpressionConstraint oldEcl = ecl;
    ecl = newEcl;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QlPackage.DOMAIN_QUERY__ECL, oldEcl, newEcl);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEcl(ExpressionConstraint newEcl)
  {
    if (newEcl != ecl)
    {
      NotificationChain msgs = null;
      if (ecl != null)
        msgs = ((InternalEObject)ecl).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QlPackage.DOMAIN_QUERY__ECL, null, msgs);
      if (newEcl != null)
        msgs = ((InternalEObject)newEcl).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QlPackage.DOMAIN_QUERY__ECL, null, msgs);
      msgs = basicSetEcl(newEcl, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.DOMAIN_QUERY__ECL, newEcl, newEcl));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Filter getFilter()
  {
    return filter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetFilter(Filter newFilter, NotificationChain msgs)
  {
    Filter oldFilter = filter;
    filter = newFilter;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QlPackage.DOMAIN_QUERY__FILTER, oldFilter, newFilter);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFilter(Filter newFilter)
  {
    if (newFilter != filter)
    {
      NotificationChain msgs = null;
      if (filter != null)
        msgs = ((InternalEObject)filter).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QlPackage.DOMAIN_QUERY__FILTER, null, msgs);
      if (newFilter != null)
        msgs = ((InternalEObject)newFilter).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QlPackage.DOMAIN_QUERY__FILTER, null, msgs);
      msgs = basicSetFilter(newFilter, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, QlPackage.DOMAIN_QUERY__FILTER, newFilter, newFilter));
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
      case QlPackage.DOMAIN_QUERY__ECL:
        return basicSetEcl(null, msgs);
      case QlPackage.DOMAIN_QUERY__FILTER:
        return basicSetFilter(null, msgs);
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
      case QlPackage.DOMAIN_QUERY__ECL:
        return getEcl();
      case QlPackage.DOMAIN_QUERY__FILTER:
        return getFilter();
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
      case QlPackage.DOMAIN_QUERY__ECL:
        setEcl((ExpressionConstraint)newValue);
        return;
      case QlPackage.DOMAIN_QUERY__FILTER:
        setFilter((Filter)newValue);
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
      case QlPackage.DOMAIN_QUERY__ECL:
        setEcl((ExpressionConstraint)null);
        return;
      case QlPackage.DOMAIN_QUERY__FILTER:
        setFilter((Filter)null);
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
      case QlPackage.DOMAIN_QUERY__ECL:
        return ecl != null;
      case QlPackage.DOMAIN_QUERY__FILTER:
        return filter != null;
    }
    return super.eIsSet(featureID);
  }

} //DomainQueryImpl
