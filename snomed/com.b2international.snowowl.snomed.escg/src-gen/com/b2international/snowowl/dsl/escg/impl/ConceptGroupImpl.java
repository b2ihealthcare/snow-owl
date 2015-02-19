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
import com.b2international.snowowl.dsl.escg.ConceptGroup;
import com.b2international.snowowl.dsl.escg.EscgPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Concept Group</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.escg.impl.ConceptGroupImpl#getConstraint <em>Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.escg.impl.ConceptGroupImpl#getConcept <em>Concept</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConceptGroupImpl extends LValueImpl implements ConceptGroup
{
  /**
   * The default value of the '{@link #getConstraint() <em>Constraint</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConstraint()
   * @generated
   * @ordered
   */
  protected static final String CONSTRAINT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getConstraint() <em>Constraint</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConstraint()
   * @generated
   * @ordered
   */
  protected String constraint = CONSTRAINT_EDEFAULT;

  /**
   * The cached value of the '{@link #getConcept() <em>Concept</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConcept()
   * @generated
   * @ordered
   */
  protected Concept concept;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ConceptGroupImpl()
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
    return EscgPackage.Literals.CONCEPT_GROUP;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getConstraint()
  {
    return constraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setConstraint(String newConstraint)
  {
    String oldConstraint = constraint;
    constraint = newConstraint;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EscgPackage.CONCEPT_GROUP__CONSTRAINT, oldConstraint, constraint));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Concept getConcept()
  {
    return concept;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetConcept(Concept newConcept, NotificationChain msgs)
  {
    Concept oldConcept = concept;
    concept = newConcept;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EscgPackage.CONCEPT_GROUP__CONCEPT, oldConcept, newConcept);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setConcept(Concept newConcept)
  {
    if (newConcept != concept)
    {
      NotificationChain msgs = null;
      if (concept != null)
        msgs = ((InternalEObject)concept).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EscgPackage.CONCEPT_GROUP__CONCEPT, null, msgs);
      if (newConcept != null)
        msgs = ((InternalEObject)newConcept).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EscgPackage.CONCEPT_GROUP__CONCEPT, null, msgs);
      msgs = basicSetConcept(newConcept, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EscgPackage.CONCEPT_GROUP__CONCEPT, newConcept, newConcept));
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
      case EscgPackage.CONCEPT_GROUP__CONCEPT:
        return basicSetConcept(null, msgs);
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
      case EscgPackage.CONCEPT_GROUP__CONSTRAINT:
        return getConstraint();
      case EscgPackage.CONCEPT_GROUP__CONCEPT:
        return getConcept();
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
      case EscgPackage.CONCEPT_GROUP__CONSTRAINT:
        setConstraint((String)newValue);
        return;
      case EscgPackage.CONCEPT_GROUP__CONCEPT:
        setConcept((Concept)newValue);
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
      case EscgPackage.CONCEPT_GROUP__CONSTRAINT:
        setConstraint(CONSTRAINT_EDEFAULT);
        return;
      case EscgPackage.CONCEPT_GROUP__CONCEPT:
        setConcept((Concept)null);
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
      case EscgPackage.CONCEPT_GROUP__CONSTRAINT:
        return CONSTRAINT_EDEFAULT == null ? constraint != null : !CONSTRAINT_EDEFAULT.equals(constraint);
      case EscgPackage.CONCEPT_GROUP__CONCEPT:
        return concept != null;
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
    result.append(" (constraint: ");
    result.append(constraint);
    result.append(')');
    return result.toString();
  }

} //ConceptGroupImpl