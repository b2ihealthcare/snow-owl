/**
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.etl.etl.impl;

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

import com.b2international.snowowl.snomed.etl.etl.Attribute;
import com.b2international.snowowl.snomed.etl.etl.AttributeGroup;
import com.b2international.snowowl.snomed.etl.etl.EtlPackage;
import com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute Group</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.AttributeGroupImpl#getSlot <em>Slot</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.AttributeGroupImpl#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AttributeGroupImpl extends MinimalEObjectImpl.Container implements AttributeGroup
{
  /**
   * The cached value of the '{@link #getSlot() <em>Slot</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSlot()
   * @generated
   * @ordered
   */
  protected TemplateInformationSlot slot;

  /**
   * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributes()
   * @generated
   * @ordered
   */
  protected EList<Attribute> attributes;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AttributeGroupImpl()
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
    return EtlPackage.Literals.ATTRIBUTE_GROUP;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TemplateInformationSlot getSlot()
  {
    return slot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSlot(TemplateInformationSlot newSlot, NotificationChain msgs)
  {
    TemplateInformationSlot oldSlot = slot;
    slot = newSlot;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EtlPackage.ATTRIBUTE_GROUP__SLOT, oldSlot, newSlot);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSlot(TemplateInformationSlot newSlot)
  {
    if (newSlot != slot)
    {
      NotificationChain msgs = null;
      if (slot != null)
        msgs = ((InternalEObject)slot).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EtlPackage.ATTRIBUTE_GROUP__SLOT, null, msgs);
      if (newSlot != null)
        msgs = ((InternalEObject)newSlot).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EtlPackage.ATTRIBUTE_GROUP__SLOT, null, msgs);
      msgs = basicSetSlot(newSlot, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.ATTRIBUTE_GROUP__SLOT, newSlot, newSlot));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Attribute> getAttributes()
  {
    if (attributes == null)
    {
      attributes = new EObjectContainmentEList<Attribute>(Attribute.class, this, EtlPackage.ATTRIBUTE_GROUP__ATTRIBUTES);
    }
    return attributes;
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
      case EtlPackage.ATTRIBUTE_GROUP__SLOT:
        return basicSetSlot(null, msgs);
      case EtlPackage.ATTRIBUTE_GROUP__ATTRIBUTES:
        return ((InternalEList<?>)getAttributes()).basicRemove(otherEnd, msgs);
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
      case EtlPackage.ATTRIBUTE_GROUP__SLOT:
        return getSlot();
      case EtlPackage.ATTRIBUTE_GROUP__ATTRIBUTES:
        return getAttributes();
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
      case EtlPackage.ATTRIBUTE_GROUP__SLOT:
        setSlot((TemplateInformationSlot)newValue);
        return;
      case EtlPackage.ATTRIBUTE_GROUP__ATTRIBUTES:
        getAttributes().clear();
        getAttributes().addAll((Collection<? extends Attribute>)newValue);
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
      case EtlPackage.ATTRIBUTE_GROUP__SLOT:
        setSlot((TemplateInformationSlot)null);
        return;
      case EtlPackage.ATTRIBUTE_GROUP__ATTRIBUTES:
        getAttributes().clear();
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
      case EtlPackage.ATTRIBUTE_GROUP__SLOT:
        return slot != null;
      case EtlPackage.ATTRIBUTE_GROUP__ATTRIBUTES:
        return attributes != null && !attributes.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //AttributeGroupImpl
