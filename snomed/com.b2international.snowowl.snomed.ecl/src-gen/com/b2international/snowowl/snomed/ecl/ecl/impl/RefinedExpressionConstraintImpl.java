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

import com.b2international.snowowl.snomed.ecl.ecl.EclPackage;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.Refinement;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Refined Expression Constraint</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.impl.RefinedExpressionConstraintImpl#getConstraint <em>Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.impl.RefinedExpressionConstraintImpl#getRefinement <em>Refinement</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RefinedExpressionConstraintImpl extends ExpressionConstraintImpl implements RefinedExpressionConstraint
{
  /**
   * The cached value of the '{@link #getConstraint() <em>Constraint</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConstraint()
   * @generated
   * @ordered
   */
  protected ExpressionConstraint constraint;

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
  protected RefinedExpressionConstraintImpl()
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
    return EclPackage.Literals.REFINED_EXPRESSION_CONSTRAINT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionConstraint getConstraint()
  {
    return constraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetConstraint(ExpressionConstraint newConstraint, NotificationChain msgs)
  {
    ExpressionConstraint oldConstraint = constraint;
    constraint = newConstraint;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EclPackage.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT, oldConstraint, newConstraint);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setConstraint(ExpressionConstraint newConstraint)
  {
    if (newConstraint != constraint)
    {
      NotificationChain msgs = null;
      if (constraint != null)
        msgs = ((InternalEObject)constraint).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EclPackage.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT, null, msgs);
      if (newConstraint != null)
        msgs = ((InternalEObject)newConstraint).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EclPackage.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT, null, msgs);
      msgs = basicSetConstraint(newConstraint, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EclPackage.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT, newConstraint, newConstraint));
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EclPackage.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT, oldRefinement, newRefinement);
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
        msgs = ((InternalEObject)refinement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EclPackage.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT, null, msgs);
      if (newRefinement != null)
        msgs = ((InternalEObject)newRefinement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EclPackage.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT, null, msgs);
      msgs = basicSetRefinement(newRefinement, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EclPackage.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT, newRefinement, newRefinement));
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
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT:
        return basicSetConstraint(null, msgs);
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT:
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
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT:
        return getConstraint();
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT:
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
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT:
        setConstraint((ExpressionConstraint)newValue);
        return;
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT:
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
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT:
        setConstraint((ExpressionConstraint)null);
        return;
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT:
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
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT:
        return constraint != null;
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT:
        return refinement != null;
    }
    return super.eIsSet(featureID);
  }

} //RefinedExpressionConstraintImpl
