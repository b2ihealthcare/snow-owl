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
import com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.EtlPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Concept Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.ConceptReferenceImpl#getSlot <em>Slot</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.ConceptReferenceImpl#getId <em>Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.etl.etl.impl.ConceptReferenceImpl#getTerm <em>Term</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConceptReferenceImpl extends AttributeValueImpl implements ConceptReference
{
  /**
   * The cached value of the '{@link #getSlot() <em>Slot</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSlot()
   * @generated
   * @ordered
   */
  protected ConceptReplacementSlot slot;

  /**
   * The default value of the '{@link #getId() <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getId()
   * @generated
   * @ordered
   */
  protected static final String ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getId()
   * @generated
   * @ordered
   */
  protected String id = ID_EDEFAULT;

  /**
   * The default value of the '{@link #getTerm() <em>Term</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTerm()
   * @generated
   * @ordered
   */
  protected static final String TERM_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTerm() <em>Term</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTerm()
   * @generated
   * @ordered
   */
  protected String term = TERM_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ConceptReferenceImpl()
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
    return EtlPackage.Literals.CONCEPT_REFERENCE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ConceptReplacementSlot getSlot()
  {
    return slot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSlot(ConceptReplacementSlot newSlot, NotificationChain msgs)
  {
    ConceptReplacementSlot oldSlot = slot;
    slot = newSlot;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EtlPackage.CONCEPT_REFERENCE__SLOT, oldSlot, newSlot);
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
  public void setSlot(ConceptReplacementSlot newSlot)
  {
    if (newSlot != slot)
    {
      NotificationChain msgs = null;
      if (slot != null)
        msgs = ((InternalEObject)slot).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EtlPackage.CONCEPT_REFERENCE__SLOT, null, msgs);
      if (newSlot != null)
        msgs = ((InternalEObject)newSlot).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EtlPackage.CONCEPT_REFERENCE__SLOT, null, msgs);
      msgs = basicSetSlot(newSlot, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.CONCEPT_REFERENCE__SLOT, newSlot, newSlot));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getId()
  {
    return id;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setId(String newId)
  {
    String oldId = id;
    id = newId;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.CONCEPT_REFERENCE__ID, oldId, id));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getTerm()
  {
    return term;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTerm(String newTerm)
  {
    String oldTerm = term;
    term = newTerm;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EtlPackage.CONCEPT_REFERENCE__TERM, oldTerm, term));
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
      case EtlPackage.CONCEPT_REFERENCE__SLOT:
        return basicSetSlot(null, msgs);
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
      case EtlPackage.CONCEPT_REFERENCE__SLOT:
        return getSlot();
      case EtlPackage.CONCEPT_REFERENCE__ID:
        return getId();
      case EtlPackage.CONCEPT_REFERENCE__TERM:
        return getTerm();
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
      case EtlPackage.CONCEPT_REFERENCE__SLOT:
        setSlot((ConceptReplacementSlot)newValue);
        return;
      case EtlPackage.CONCEPT_REFERENCE__ID:
        setId((String)newValue);
        return;
      case EtlPackage.CONCEPT_REFERENCE__TERM:
        setTerm((String)newValue);
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
      case EtlPackage.CONCEPT_REFERENCE__SLOT:
        setSlot((ConceptReplacementSlot)null);
        return;
      case EtlPackage.CONCEPT_REFERENCE__ID:
        setId(ID_EDEFAULT);
        return;
      case EtlPackage.CONCEPT_REFERENCE__TERM:
        setTerm(TERM_EDEFAULT);
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
      case EtlPackage.CONCEPT_REFERENCE__SLOT:
        return slot != null;
      case EtlPackage.CONCEPT_REFERENCE__ID:
        return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
      case EtlPackage.CONCEPT_REFERENCE__TERM:
        return TERM_EDEFAULT == null ? term != null : !TERM_EDEFAULT.equals(term);
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
    result.append(" (id: ");
    result.append(id);
    result.append(", term: ");
    result.append(term);
    result.append(')');
    return result.toString();
  }

} //ConceptReferenceImpl
