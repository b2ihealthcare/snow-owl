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

import com.b2international.snowowl.dsl.escg.Concept;
import com.b2international.snowowl.dsl.escg.EscgPackage;
import com.b2international.snowowl.dsl.escg.NumericalAssignment;
import com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup;
import com.b2international.snowowl.dsl.escg.RValue;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Numerical Assignment Group</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentGroupImpl#getIngredientConcept <em>Ingredient Concept</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentGroupImpl#getSubstance <em>Substance</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentGroupImpl#getNumericValue <em>Numeric Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class NumericalAssignmentGroupImpl extends AttributeAssignmentImpl implements NumericalAssignmentGroup
{
  /**
   * The cached value of the '{@link #getIngredientConcept() <em>Ingredient Concept</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIngredientConcept()
   * @generated
   * @ordered
   */
  protected Concept ingredientConcept;

  /**
   * The cached value of the '{@link #getSubstance() <em>Substance</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSubstance()
   * @generated
   * @ordered
   */
  protected RValue substance;

  /**
   * The cached value of the '{@link #getNumericValue() <em>Numeric Value</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNumericValue()
   * @generated
   * @ordered
   */
  protected NumericalAssignment numericValue;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected NumericalAssignmentGroupImpl()
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
    return EscgPackage.Literals.NUMERICAL_ASSIGNMENT_GROUP;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Concept getIngredientConcept()
  {
    return ingredientConcept;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetIngredientConcept(Concept newIngredientConcept, NotificationChain msgs)
  {
    Concept oldIngredientConcept = ingredientConcept;
    ingredientConcept = newIngredientConcept;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT, oldIngredientConcept, newIngredientConcept);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setIngredientConcept(Concept newIngredientConcept)
  {
    if (newIngredientConcept != ingredientConcept)
    {
      NotificationChain msgs = null;
      if (ingredientConcept != null)
        msgs = ((InternalEObject)ingredientConcept).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT, null, msgs);
      if (newIngredientConcept != null)
        msgs = ((InternalEObject)newIngredientConcept).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT, null, msgs);
      msgs = basicSetIngredientConcept(newIngredientConcept, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT, newIngredientConcept, newIngredientConcept));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RValue getSubstance()
  {
    return substance;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSubstance(RValue newSubstance, NotificationChain msgs)
  {
    RValue oldSubstance = substance;
    substance = newSubstance;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE, oldSubstance, newSubstance);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSubstance(RValue newSubstance)
  {
    if (newSubstance != substance)
    {
      NotificationChain msgs = null;
      if (substance != null)
        msgs = ((InternalEObject)substance).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE, null, msgs);
      if (newSubstance != null)
        msgs = ((InternalEObject)newSubstance).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE, null, msgs);
      msgs = basicSetSubstance(newSubstance, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE, newSubstance, newSubstance));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NumericalAssignment getNumericValue()
  {
    return numericValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetNumericValue(NumericalAssignment newNumericValue, NotificationChain msgs)
  {
    NumericalAssignment oldNumericValue = numericValue;
    numericValue = newNumericValue;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE, oldNumericValue, newNumericValue);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNumericValue(NumericalAssignment newNumericValue)
  {
    if (newNumericValue != numericValue)
    {
      NotificationChain msgs = null;
      if (numericValue != null)
        msgs = ((InternalEObject)numericValue).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE, null, msgs);
      if (newNumericValue != null)
        msgs = ((InternalEObject)newNumericValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE, null, msgs);
      msgs = basicSetNumericValue(newNumericValue, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE, newNumericValue, newNumericValue));
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
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT:
        return basicSetIngredientConcept(null, msgs);
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE:
        return basicSetSubstance(null, msgs);
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE:
        return basicSetNumericValue(null, msgs);
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
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT:
        return getIngredientConcept();
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE:
        return getSubstance();
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE:
        return getNumericValue();
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
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT:
        setIngredientConcept((Concept)newValue);
        return;
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE:
        setSubstance((RValue)newValue);
        return;
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE:
        setNumericValue((NumericalAssignment)newValue);
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
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT:
        setIngredientConcept((Concept)null);
        return;
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE:
        setSubstance((RValue)null);
        return;
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE:
        setNumericValue((NumericalAssignment)null);
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
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT:
        return ingredientConcept != null;
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE:
        return substance != null;
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE:
        return numericValue != null;
    }
    return super.eIsSet(featureID);
  }

} //NumericalAssignmentGroupImpl