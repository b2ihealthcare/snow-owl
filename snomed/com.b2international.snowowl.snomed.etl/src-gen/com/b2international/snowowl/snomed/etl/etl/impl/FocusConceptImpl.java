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

import com.b2international.snowowl.snomed.etl.etl.ConceptReference;
import com.b2international.snowowl.snomed.etl.etl.EtlPackage;
import com.b2international.snowowl.snomed.etl.etl.FocusConcept;
import com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Focus Concept</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.FocusConceptImpl#getSlot <em>Slot</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.FocusConceptImpl#getConcept <em>Concept</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FocusConceptImpl extends MinimalEObjectImpl.Container implements FocusConcept
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
   * The cached value of the '{@link #getConcept() <em>Concept</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConcept()
   * @generated
   * @ordered
   */
  protected ConceptReference concept;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FocusConceptImpl()
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
    return EtlPackage.Literals.FOCUS_CONCEPT;
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EtlPackage.FOCUS_CONCEPT__SLOT, oldSlot, newSlot);
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
        msgs = ((InternalEObject)slot).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EtlPackage.FOCUS_CONCEPT__SLOT, null, msgs);
      if (newSlot != null)
        msgs = ((InternalEObject)newSlot).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EtlPackage.FOCUS_CONCEPT__SLOT, null, msgs);
      msgs = basicSetSlot(newSlot, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.FOCUS_CONCEPT__SLOT, newSlot, newSlot));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ConceptReference getConcept()
  {
    return concept;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetConcept(ConceptReference newConcept, NotificationChain msgs)
  {
    ConceptReference oldConcept = concept;
    concept = newConcept;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EtlPackage.FOCUS_CONCEPT__CONCEPT, oldConcept, newConcept);
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
  public void setConcept(ConceptReference newConcept)
  {
    if (newConcept != concept)
    {
      NotificationChain msgs = null;
      if (concept != null)
        msgs = ((InternalEObject)concept).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EtlPackage.FOCUS_CONCEPT__CONCEPT, null, msgs);
      if (newConcept != null)
        msgs = ((InternalEObject)newConcept).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EtlPackage.FOCUS_CONCEPT__CONCEPT, null, msgs);
      msgs = basicSetConcept(newConcept, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.FOCUS_CONCEPT__CONCEPT, newConcept, newConcept));
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
      case EtlPackage.FOCUS_CONCEPT__SLOT:
        return basicSetSlot(null, msgs);
      case EtlPackage.FOCUS_CONCEPT__CONCEPT:
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
      case EtlPackage.FOCUS_CONCEPT__SLOT:
        return getSlot();
      case EtlPackage.FOCUS_CONCEPT__CONCEPT:
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
      case EtlPackage.FOCUS_CONCEPT__SLOT:
        setSlot((TemplateInformationSlot)newValue);
        return;
      case EtlPackage.FOCUS_CONCEPT__CONCEPT:
        setConcept((ConceptReference)newValue);
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
      case EtlPackage.FOCUS_CONCEPT__SLOT:
        setSlot((TemplateInformationSlot)null);
        return;
      case EtlPackage.FOCUS_CONCEPT__CONCEPT:
        setConcept((ConceptReference)null);
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
      case EtlPackage.FOCUS_CONCEPT__SLOT:
        return slot != null;
      case EtlPackage.FOCUS_CONCEPT__CONCEPT:
        return concept != null;
    }
    return super.eIsSet(featureID);
  }

} //FocusConceptImpl
