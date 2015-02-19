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

import com.b2international.snowowl.dsl.escg.AttributeGroup;
import com.b2international.snowowl.dsl.escg.AttributeSet;
import com.b2international.snowowl.dsl.escg.EscgPackage;
import com.b2international.snowowl.dsl.escg.Refinements;

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
 * An implementation of the model object '<em><b>Refinements</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.escg.impl.RefinementsImpl#getAttributeSet <em>Attribute Set</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.escg.impl.RefinementsImpl#getAttributeGroups <em>Attribute Groups</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RefinementsImpl extends MinimalEObjectImpl.Container implements Refinements
{
  /**
   * The cached value of the '{@link #getAttributeSet() <em>Attribute Set</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributeSet()
   * @generated
   * @ordered
   */
  protected AttributeSet attributeSet;

  /**
   * The cached value of the '{@link #getAttributeGroups() <em>Attribute Groups</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributeGroups()
   * @generated
   * @ordered
   */
  protected EList<AttributeGroup> attributeGroups;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RefinementsImpl()
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
    return EscgPackage.Literals.REFINEMENTS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeSet getAttributeSet()
  {
    return attributeSet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetAttributeSet(AttributeSet newAttributeSet, NotificationChain msgs)
  {
    AttributeSet oldAttributeSet = attributeSet;
    attributeSet = newAttributeSet;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EscgPackage.REFINEMENTS__ATTRIBUTE_SET, oldAttributeSet, newAttributeSet);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAttributeSet(AttributeSet newAttributeSet)
  {
    if (newAttributeSet != attributeSet)
    {
      NotificationChain msgs = null;
      if (attributeSet != null)
        msgs = ((InternalEObject)attributeSet).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EscgPackage.REFINEMENTS__ATTRIBUTE_SET, null, msgs);
      if (newAttributeSet != null)
        msgs = ((InternalEObject)newAttributeSet).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EscgPackage.REFINEMENTS__ATTRIBUTE_SET, null, msgs);
      msgs = basicSetAttributeSet(newAttributeSet, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EscgPackage.REFINEMENTS__ATTRIBUTE_SET, newAttributeSet, newAttributeSet));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AttributeGroup> getAttributeGroups()
  {
    if (attributeGroups == null)
    {
      attributeGroups = new EObjectContainmentEList<AttributeGroup>(AttributeGroup.class, this, EscgPackage.REFINEMENTS__ATTRIBUTE_GROUPS);
    }
    return attributeGroups;
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
      case EscgPackage.REFINEMENTS__ATTRIBUTE_SET:
        return basicSetAttributeSet(null, msgs);
      case EscgPackage.REFINEMENTS__ATTRIBUTE_GROUPS:
        return ((InternalEList<?>)getAttributeGroups()).basicRemove(otherEnd, msgs);
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
      case EscgPackage.REFINEMENTS__ATTRIBUTE_SET:
        return getAttributeSet();
      case EscgPackage.REFINEMENTS__ATTRIBUTE_GROUPS:
        return getAttributeGroups();
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
      case EscgPackage.REFINEMENTS__ATTRIBUTE_SET:
        setAttributeSet((AttributeSet)newValue);
        return;
      case EscgPackage.REFINEMENTS__ATTRIBUTE_GROUPS:
        getAttributeGroups().clear();
        getAttributeGroups().addAll((Collection<? extends AttributeGroup>)newValue);
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
      case EscgPackage.REFINEMENTS__ATTRIBUTE_SET:
        setAttributeSet((AttributeSet)null);
        return;
      case EscgPackage.REFINEMENTS__ATTRIBUTE_GROUPS:
        getAttributeGroups().clear();
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
      case EscgPackage.REFINEMENTS__ATTRIBUTE_SET:
        return attributeSet != null;
      case EscgPackage.REFINEMENTS__ATTRIBUTE_GROUPS:
        return attributeGroups != null && !attributeGroups.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //RefinementsImpl