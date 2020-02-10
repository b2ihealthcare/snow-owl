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
import com.b2international.snowowl.snomed.etl.etl.FocusConcept;
import com.b2international.snowowl.snomed.etl.etl.Refinement;
import com.b2international.snowowl.snomed.etl.etl.SubExpression;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sub Expression</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.SubExpressionImpl#getFocusConcepts <em>Focus Concepts</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.SubExpressionImpl#getRefinement <em>Refinement</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SubExpressionImpl extends AttributeValueImpl implements SubExpression
{
  /**
   * The cached value of the '{@link #getFocusConcepts() <em>Focus Concepts</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFocusConcepts()
   * @generated
   * @ordered
   */
  protected EList<FocusConcept> focusConcepts;

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
    return EtlPackage.Literals.SUB_EXPRESSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<FocusConcept> getFocusConcepts()
  {
    if (focusConcepts == null)
    {
      focusConcepts = new EObjectContainmentEList<FocusConcept>(FocusConcept.class, this, EtlPackage.SUB_EXPRESSION__FOCUS_CONCEPTS);
    }
    return focusConcepts;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EtlPackage.SUB_EXPRESSION__REFINEMENT, oldRefinement, newRefinement);
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
  public void setRefinement(Refinement newRefinement)
  {
    if (newRefinement != refinement)
    {
      NotificationChain msgs = null;
      if (refinement != null)
        msgs = ((InternalEObject)refinement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EtlPackage.SUB_EXPRESSION__REFINEMENT, null, msgs);
      if (newRefinement != null)
        msgs = ((InternalEObject)newRefinement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EtlPackage.SUB_EXPRESSION__REFINEMENT, null, msgs);
      msgs = basicSetRefinement(newRefinement, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.SUB_EXPRESSION__REFINEMENT, newRefinement, newRefinement));
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
      case EtlPackage.SUB_EXPRESSION__FOCUS_CONCEPTS:
        return ((InternalEList<?>)getFocusConcepts()).basicRemove(otherEnd, msgs);
      case EtlPackage.SUB_EXPRESSION__REFINEMENT:
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
      case EtlPackage.SUB_EXPRESSION__FOCUS_CONCEPTS:
        return getFocusConcepts();
      case EtlPackage.SUB_EXPRESSION__REFINEMENT:
        return getRefinement();
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
      case EtlPackage.SUB_EXPRESSION__FOCUS_CONCEPTS:
        getFocusConcepts().clear();
        getFocusConcepts().addAll((Collection<? extends FocusConcept>)newValue);
        return;
      case EtlPackage.SUB_EXPRESSION__REFINEMENT:
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
      case EtlPackage.SUB_EXPRESSION__FOCUS_CONCEPTS:
        getFocusConcepts().clear();
        return;
      case EtlPackage.SUB_EXPRESSION__REFINEMENT:
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
      case EtlPackage.SUB_EXPRESSION__FOCUS_CONCEPTS:
        return focusConcepts != null && !focusConcepts.isEmpty();
      case EtlPackage.SUB_EXPRESSION__REFINEMENT:
        return refinement != null;
    }
    return super.eIsSet(featureID);
  }

} //SubExpressionImpl
