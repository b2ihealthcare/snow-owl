/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.dsl.escg.impl;

import com.b2international.snowowl.dsl.escg.EscgPackage;
import com.b2international.snowowl.dsl.escg.LValue;
import com.b2international.snowowl.dsl.escg.Refinements;
import com.b2international.snowowl.dsl.escg.SubExpression;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sub Expression</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.escg.impl.SubExpressionImpl#getLValues <em>LValues</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.escg.impl.SubExpressionImpl#getRefinements <em>Refinements</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SubExpressionImpl extends MinimalEObjectImpl.Container implements SubExpression
{
  /**
   * The cached value of the '{@link #getLValues() <em>LValues</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLValues()
   * @generated
   * @ordered
   */
  protected EList<LValue> lValues;

  /**
   * The cached value of the '{@link #getRefinements() <em>Refinements</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRefinements()
   * @generated
   * @ordered
   */
  protected Refinements refinements;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SubExpressionImpl()
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
    return EscgPackage.Literals.SUB_EXPRESSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<LValue> getLValues()
  {
    if (lValues == null)
    {
      lValues = new EObjectContainmentEList<LValue>(LValue.class, this, EscgPackage.SUB_EXPRESSION__LVALUES);
    }
    return lValues;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Refinements getRefinements()
  {
    return refinements;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetRefinements(Refinements newRefinements, NotificationChain msgs)
  {
    Refinements oldRefinements = refinements;
    refinements = newRefinements;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EscgPackage.SUB_EXPRESSION__REFINEMENTS, oldRefinements, newRefinements);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRefinements(Refinements newRefinements)
  {
    if (newRefinements != refinements)
    {
      NotificationChain msgs = null;
      if (refinements != null)
        msgs = ((InternalEObject)refinements).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EscgPackage.SUB_EXPRESSION__REFINEMENTS, null, msgs);
      if (newRefinements != null)
        msgs = ((InternalEObject)newRefinements).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EscgPackage.SUB_EXPRESSION__REFINEMENTS, null, msgs);
      msgs = basicSetRefinements(newRefinements, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EscgPackage.SUB_EXPRESSION__REFINEMENTS, newRefinements, newRefinements));
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
      case EscgPackage.SUB_EXPRESSION__LVALUES:
        return ((InternalEList<?>)getLValues()).basicRemove(otherEnd, msgs);
      case EscgPackage.SUB_EXPRESSION__REFINEMENTS:
        return basicSetRefinements(null, msgs);
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
      case EscgPackage.SUB_EXPRESSION__LVALUES:
        return getLValues();
      case EscgPackage.SUB_EXPRESSION__REFINEMENTS:
        return getRefinements();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case EscgPackage.SUB_EXPRESSION__LVALUES:
        getLValues().clear();
        getLValues().addAll((Collection<? extends LValue>)newValue);
        return;
      case EscgPackage.SUB_EXPRESSION__REFINEMENTS:
        setRefinements((Refinements)newValue);
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
      case EscgPackage.SUB_EXPRESSION__LVALUES:
        getLValues().clear();
        return;
      case EscgPackage.SUB_EXPRESSION__REFINEMENTS:
        setRefinements((Refinements)null);
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
      case EscgPackage.SUB_EXPRESSION__LVALUES:
        return lValues != null && !lValues.isEmpty();
      case EscgPackage.SUB_EXPRESSION__REFINEMENTS:
        return refinements != null;
    }
    return super.eIsSet(featureID);
  }

} //SubExpressionImpl