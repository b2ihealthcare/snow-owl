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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import com.b2international.snowowl.snomed.etl.etl.EtlPackage;
import com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate;
import com.b2international.snowowl.snomed.etl.etl.SubExpression;
import com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Expression Template</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.ExpressionTemplateImpl#isPrimitive <em>Primitive</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.ExpressionTemplateImpl#getSlot <em>Slot</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.ExpressionTemplateImpl#getExpression <em>Expression</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ExpressionTemplateImpl extends MinimalEObjectImpl.Container implements ExpressionTemplate
{
  /**
   * The default value of the '{@link #isPrimitive() <em>Primitive</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isPrimitive()
   * @generated
   * @ordered
   */
  protected static final boolean PRIMITIVE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isPrimitive() <em>Primitive</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isPrimitive()
   * @generated
   * @ordered
   */
  protected boolean primitive = PRIMITIVE_EDEFAULT;

  /**
   * The cached value of the '{@link #getSlot() <em>Slot</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSlot()
   * @generated
   * @ordered
   */
  protected TokenReplacementSlot slot;

  /**
   * The cached value of the '{@link #getExpression() <em>Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExpression()
   * @generated
   * @ordered
   */
  protected SubExpression expression;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ExpressionTemplateImpl()
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
    return EtlPackage.Literals.EXPRESSION_TEMPLATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isPrimitive()
  {
    return primitive;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPrimitive(boolean newPrimitive)
  {
    boolean oldPrimitive = primitive;
    primitive = newPrimitive;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.EXPRESSION_TEMPLATE__PRIMITIVE, oldPrimitive, primitive));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TokenReplacementSlot getSlot()
  {
    return slot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSlot(TokenReplacementSlot newSlot, NotificationChain msgs)
  {
    TokenReplacementSlot oldSlot = slot;
    slot = newSlot;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EtlPackage.EXPRESSION_TEMPLATE__SLOT, oldSlot, newSlot);
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
  public void setSlot(TokenReplacementSlot newSlot)
  {
    if (newSlot != slot)
    {
      NotificationChain msgs = null;
      if (slot != null)
        msgs = ((InternalEObject)slot).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EtlPackage.EXPRESSION_TEMPLATE__SLOT, null, msgs);
      if (newSlot != null)
        msgs = ((InternalEObject)newSlot).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EtlPackage.EXPRESSION_TEMPLATE__SLOT, null, msgs);
      msgs = basicSetSlot(newSlot, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.EXPRESSION_TEMPLATE__SLOT, newSlot, newSlot));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SubExpression getExpression()
  {
    return expression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetExpression(SubExpression newExpression, NotificationChain msgs)
  {
    SubExpression oldExpression = expression;
    expression = newExpression;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EtlPackage.EXPRESSION_TEMPLATE__EXPRESSION, oldExpression, newExpression);
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
  public void setExpression(SubExpression newExpression)
  {
    if (newExpression != expression)
    {
      NotificationChain msgs = null;
      if (expression != null)
        msgs = ((InternalEObject)expression).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EtlPackage.EXPRESSION_TEMPLATE__EXPRESSION, null, msgs);
      if (newExpression != null)
        msgs = ((InternalEObject)newExpression).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EtlPackage.EXPRESSION_TEMPLATE__EXPRESSION, null, msgs);
      msgs = basicSetExpression(newExpression, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.EXPRESSION_TEMPLATE__EXPRESSION, newExpression, newExpression));
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
      case EtlPackage.EXPRESSION_TEMPLATE__SLOT:
        return basicSetSlot(null, msgs);
      case EtlPackage.EXPRESSION_TEMPLATE__EXPRESSION:
        return basicSetExpression(null, msgs);
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
      case EtlPackage.EXPRESSION_TEMPLATE__PRIMITIVE:
        return isPrimitive();
      case EtlPackage.EXPRESSION_TEMPLATE__SLOT:
        return getSlot();
      case EtlPackage.EXPRESSION_TEMPLATE__EXPRESSION:
        return getExpression();
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
      case EtlPackage.EXPRESSION_TEMPLATE__PRIMITIVE:
        setPrimitive((Boolean)newValue);
        return;
      case EtlPackage.EXPRESSION_TEMPLATE__SLOT:
        setSlot((TokenReplacementSlot)newValue);
        return;
      case EtlPackage.EXPRESSION_TEMPLATE__EXPRESSION:
        setExpression((SubExpression)newValue);
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
      case EtlPackage.EXPRESSION_TEMPLATE__PRIMITIVE:
        setPrimitive(PRIMITIVE_EDEFAULT);
        return;
      case EtlPackage.EXPRESSION_TEMPLATE__SLOT:
        setSlot((TokenReplacementSlot)null);
        return;
      case EtlPackage.EXPRESSION_TEMPLATE__EXPRESSION:
        setExpression((SubExpression)null);
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
      case EtlPackage.EXPRESSION_TEMPLATE__PRIMITIVE:
        return primitive != PRIMITIVE_EDEFAULT;
      case EtlPackage.EXPRESSION_TEMPLATE__SLOT:
        return slot != null;
      case EtlPackage.EXPRESSION_TEMPLATE__EXPRESSION:
        return expression != null;
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
    result.append(" (primitive: ");
    result.append(primitive);
    result.append(')');
    return result.toString();
  }

} //ExpressionTemplateImpl
