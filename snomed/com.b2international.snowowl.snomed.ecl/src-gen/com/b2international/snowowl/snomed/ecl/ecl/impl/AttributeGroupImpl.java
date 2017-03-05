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

import com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup;
import com.b2international.snowowl.snomed.ecl.ecl.Cardinality;
import com.b2international.snowowl.snomed.ecl.ecl.EclPackage;
import com.b2international.snowowl.snomed.ecl.ecl.Refinement;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute Group</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeGroupImpl#getCardinality <em>Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeGroupImpl#getRefinement <em>Refinement</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AttributeGroupImpl extends RefinementImpl implements AttributeGroup
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
   * The cached value of the '{@link #getRefinement() <em>Refinement</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRefinement()
   * @generated
   * @ordered
   */
  protected Refinement refinement;

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
    return EclPackage.Literals.ATTRIBUTE_GROUP;
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_GROUP__CARDINALITY, oldCardinality, newCardinality);
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
        msgs = ((InternalEObject)cardinality).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EclPackage.ATTRIBUTE_GROUP__CARDINALITY, null, msgs);
      if (newCardinality != null)
        msgs = ((InternalEObject)newCardinality).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EclPackage.ATTRIBUTE_GROUP__CARDINALITY, null, msgs);
      msgs = basicSetCardinality(newCardinality, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_GROUP__CARDINALITY, newCardinality, newCardinality));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Refinement getRefinement()
  {
    return refinement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetRefinement(Refinement newRefinement, NotificationChain msgs)
  {
    Refinement oldRefinement = refinement;
    refinement = newRefinement;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_GROUP__REFINEMENT, oldRefinement, newRefinement);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRefinement(Refinement newRefinement)
  {
    if (newRefinement != refinement)
    {
      NotificationChain msgs = null;
      if (refinement != null)
        msgs = ((InternalEObject)refinement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EclPackage.ATTRIBUTE_GROUP__REFINEMENT, null, msgs);
      if (newRefinement != null)
        msgs = ((InternalEObject)newRefinement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EclPackage.ATTRIBUTE_GROUP__REFINEMENT, null, msgs);
      msgs = basicSetRefinement(newRefinement, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EclPackage.ATTRIBUTE_GROUP__REFINEMENT, newRefinement, newRefinement));
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
      case EclPackage.ATTRIBUTE_GROUP__CARDINALITY:
        return basicSetCardinality(null, msgs);
      case EclPackage.ATTRIBUTE_GROUP__REFINEMENT:
        return basicSetRefinement(null, msgs);
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
      case EclPackage.ATTRIBUTE_GROUP__CARDINALITY:
        return getCardinality();
      case EclPackage.ATTRIBUTE_GROUP__REFINEMENT:
        return getRefinement();
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
      case EclPackage.ATTRIBUTE_GROUP__CARDINALITY:
        setCardinality((Cardinality)newValue);
        return;
      case EclPackage.ATTRIBUTE_GROUP__REFINEMENT:
        setRefinement((Refinement)newValue);
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
      case EclPackage.ATTRIBUTE_GROUP__CARDINALITY:
        setCardinality((Cardinality)null);
        return;
      case EclPackage.ATTRIBUTE_GROUP__REFINEMENT:
        setRefinement((Refinement)null);
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
      case EclPackage.ATTRIBUTE_GROUP__CARDINALITY:
        return cardinality != null;
      case EclPackage.ATTRIBUTE_GROUP__REFINEMENT:
        return refinement != null;
    }
    return super.eIsSet(featureID);
  }

} //AttributeGroupImpl
