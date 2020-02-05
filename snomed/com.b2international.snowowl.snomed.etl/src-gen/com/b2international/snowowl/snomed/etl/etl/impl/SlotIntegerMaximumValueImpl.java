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

import com.b2international.snowowl.snomed.etl.etl.EtlPackage;
import com.b2international.snowowl.snomed.etl.etl.SlotIntegerMaximumValue;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Slot Integer Maximum Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerMaximumValueImpl#isExclusive <em>Exclusive</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerMaximumValueImpl#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SlotIntegerMaximumValueImpl extends MinimalEObjectImpl.Container implements SlotIntegerMaximumValue
{
  /**
   * The default value of the '{@link #isExclusive() <em>Exclusive</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isExclusive()
   * @generated
   * @ordered
   */
  protected static final boolean EXCLUSIVE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isExclusive() <em>Exclusive</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isExclusive()
   * @generated
   * @ordered
   */
  protected boolean exclusive = EXCLUSIVE_EDEFAULT;

  /**
   * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected static final int VALUE_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected int value = VALUE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SlotIntegerMaximumValueImpl()
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
    return EtlPackage.Literals.SLOT_INTEGER_MAXIMUM_VALUE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isExclusive()
  {
    return exclusive;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setExclusive(boolean newExclusive)
  {
    boolean oldExclusive = exclusive;
    exclusive = newExclusive;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE__EXCLUSIVE, oldExclusive, exclusive));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setValue(int newValue)
  {
    int oldValue = value;
    value = newValue;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE__VALUE, oldValue, value));
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
      case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE__EXCLUSIVE:
        return isExclusive();
      case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE__VALUE:
        return getValue();
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
      case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE__EXCLUSIVE:
        setExclusive((Boolean)newValue);
        return;
      case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE__VALUE:
        setValue((Integer)newValue);
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
      case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE__EXCLUSIVE:
        setExclusive(EXCLUSIVE_EDEFAULT);
        return;
      case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE__VALUE:
        setValue(VALUE_EDEFAULT);
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
      case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE__EXCLUSIVE:
        return exclusive != EXCLUSIVE_EDEFAULT;
      case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE__VALUE:
        return value != VALUE_EDEFAULT;
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

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (exclusive: ");
    result.append(exclusive);
    result.append(", value: ");
    result.append(value);
    result.append(')');
    return result.toString();
  }

} //SlotIntegerMaximumValueImpl
