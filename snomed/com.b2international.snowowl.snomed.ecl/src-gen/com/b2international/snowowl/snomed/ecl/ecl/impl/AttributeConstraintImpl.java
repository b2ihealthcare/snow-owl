/**
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ecl.ecl.impl;

import com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.Cardinality;
import com.b2international.snowowl.snomed.ecl.ecl.Comparison;
import com.b2international.snowowl.snomed.ecl.ecl.EclPackage;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute Constraint</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeConstraintImpl#getCardinality <em>Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeConstraintImpl#isReversed <em>Reversed</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeConstraintImpl#getAttribute <em>Attribute</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeConstraintImpl#getComparison <em>Comparison</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AttributeConstraintImpl extends RefinementImpl implements AttributeConstraint
{
  /**
   * The cached value of the '{@link #getCardinality() <em>Cardinality</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCardinality()
   * @generated
   * @ordered
   */
  protected Cardinality cardinality;

  /**
   * The default value of the '{@link #isReversed() <em>Reversed</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isReversed()
   * @generated
   * @ordered
   */
  protected static final boolean REVERSED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isReversed() <em>Reversed</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isReversed()
   * @generated
   * @ordered
   */
  protected boolean reversed = REVERSED_EDEFAULT;

  /**
   * The cached value of the '{@link #getAttribute() <em>Attribute</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttribute()
   * @generated
   * @ordered
   */
  protected ExpressionConstraint attribute;

  /**
   * The cached value of the '{@link #getComparison() <em>Comparison</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComparison()
   * @generated
   * @ordered
   */
  protected Comparison comparison;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AttributeConstraintImpl()
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
    return EclPackage.Literals.ATTRIBUTE_CONSTRAINT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Cardinality getCardinality()
  {
    return cardinality;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCardinality(Cardinality newCardinality, NotificationChain msgs)
  {
    Cardinality oldCardinality = cardinality;
    cardinality = newCardinality;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_CONSTRAINT__CARDINALITY, oldCardinality, newCardinality);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCardinality(Cardinality newCardinality)
  {
    if (newCardinality != cardinality)
    {
      NotificationChain msgs = null;
      if (cardinality != null)
        msgs = ((InternalEObject)cardinality).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EclPackage.ATTRIBUTE_CONSTRAINT__CARDINALITY, null, msgs);
      if (newCardinality != null)
        msgs = ((InternalEObject)newCardinality).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EclPackage.ATTRIBUTE_CONSTRAINT__CARDINALITY, null, msgs);
      msgs = basicSetCardinality(newCardinality, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_CONSTRAINT__CARDINALITY, newCardinality, newCardinality));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isReversed()
  {
    return reversed;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setReversed(boolean newReversed)
  {
    boolean oldReversed = reversed;
    reversed = newReversed;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_CONSTRAINT__REVERSED, oldReversed, reversed));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionConstraint getAttribute()
  {
    return attribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetAttribute(ExpressionConstraint newAttribute, NotificationChain msgs)
  {
    ExpressionConstraint oldAttribute = attribute;
    attribute = newAttribute;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_CONSTRAINT__ATTRIBUTE, oldAttribute, newAttribute);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAttribute(ExpressionConstraint newAttribute)
  {
    if (newAttribute != attribute)
    {
      NotificationChain msgs = null;
      if (attribute != null)
        msgs = ((InternalEObject)attribute).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EclPackage.ATTRIBUTE_CONSTRAINT__ATTRIBUTE, null, msgs);
      if (newAttribute != null)
        msgs = ((InternalEObject)newAttribute).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EclPackage.ATTRIBUTE_CONSTRAINT__ATTRIBUTE, null, msgs);
      msgs = basicSetAttribute(newAttribute, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_CONSTRAINT__ATTRIBUTE, newAttribute, newAttribute));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Comparison getComparison()
  {
    return comparison;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetComparison(Comparison newComparison, NotificationChain msgs)
  {
    Comparison oldComparison = comparison;
    comparison = newComparison;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_CONSTRAINT__COMPARISON, oldComparison, newComparison);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setComparison(Comparison newComparison)
  {
    if (newComparison != comparison)
    {
      NotificationChain msgs = null;
      if (comparison != null)
        msgs = ((InternalEObject)comparison).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EclPackage.ATTRIBUTE_CONSTRAINT__COMPARISON, null, msgs);
      if (newComparison != null)
        msgs = ((InternalEObject)newComparison).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EclPackage.ATTRIBUTE_CONSTRAINT__COMPARISON, null, msgs);
      msgs = basicSetComparison(newComparison, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_CONSTRAINT__COMPARISON, newComparison, newComparison));
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
      case EclPackage.ATTRIBUTE_CONSTRAINT__CARDINALITY:
        return basicSetCardinality(null, msgs);
      case EclPackage.ATTRIBUTE_CONSTRAINT__ATTRIBUTE:
        return basicSetAttribute(null, msgs);
      case EclPackage.ATTRIBUTE_CONSTRAINT__COMPARISON:
        return basicSetComparison(null, msgs);
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
      case EclPackage.ATTRIBUTE_CONSTRAINT__CARDINALITY:
        return getCardinality();
      case EclPackage.ATTRIBUTE_CONSTRAINT__REVERSED:
        return isReversed();
      case EclPackage.ATTRIBUTE_CONSTRAINT__ATTRIBUTE:
        return getAttribute();
      case EclPackage.ATTRIBUTE_CONSTRAINT__COMPARISON:
        return getComparison();
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
      case EclPackage.ATTRIBUTE_CONSTRAINT__CARDINALITY:
        setCardinality((Cardinality)newValue);
        return;
      case EclPackage.ATTRIBUTE_CONSTRAINT__REVERSED:
        setReversed((Boolean)newValue);
        return;
      case EclPackage.ATTRIBUTE_CONSTRAINT__ATTRIBUTE:
        setAttribute((ExpressionConstraint)newValue);
        return;
      case EclPackage.ATTRIBUTE_CONSTRAINT__COMPARISON:
        setComparison((Comparison)newValue);
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
      case EclPackage.ATTRIBUTE_CONSTRAINT__CARDINALITY:
        setCardinality((Cardinality)null);
        return;
      case EclPackage.ATTRIBUTE_CONSTRAINT__REVERSED:
        setReversed(REVERSED_EDEFAULT);
        return;
      case EclPackage.ATTRIBUTE_CONSTRAINT__ATTRIBUTE:
        setAttribute((ExpressionConstraint)null);
        return;
      case EclPackage.ATTRIBUTE_CONSTRAINT__COMPARISON:
        setComparison((Comparison)null);
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
      case EclPackage.ATTRIBUTE_CONSTRAINT__CARDINALITY:
        return cardinality != null;
      case EclPackage.ATTRIBUTE_CONSTRAINT__REVERSED:
        return reversed != REVERSED_EDEFAULT;
      case EclPackage.ATTRIBUTE_CONSTRAINT__ATTRIBUTE:
        return attribute != null;
      case EclPackage.ATTRIBUTE_CONSTRAINT__COMPARISON:
        return comparison != null;
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
    result.append(" (reversed: ");
    result.append(reversed);
    result.append(')');
    return result.toString();
  }

} //AttributeConstraintImpl
