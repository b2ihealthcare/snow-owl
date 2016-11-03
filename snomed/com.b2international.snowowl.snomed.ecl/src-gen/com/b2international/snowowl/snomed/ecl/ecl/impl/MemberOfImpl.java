/**
 */
package com.b2international.snowowl.snomed.ecl.ecl.impl;

import com.b2international.snowowl.snomed.ecl.ecl.EclPackage;
import com.b2international.snowowl.snomed.ecl.ecl.FocusConcept;
import com.b2international.snowowl.snomed.ecl.ecl.MemberOf;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Member Of</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl#getConcept <em>Concept</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MemberOfImpl extends FocusConceptImpl implements MemberOf
{
  /**
   * The cached value of the '{@link #getConcept() <em>Concept</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConcept()
   * @generated
   * @ordered
   */
  protected FocusConcept concept;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MemberOfImpl()
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
    return EclPackage.Literals.MEMBER_OF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FocusConcept getConcept()
  {
    return concept;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetConcept(FocusConcept newConcept, NotificationChain msgs)
  {
    FocusConcept oldConcept = concept;
    concept = newConcept;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EclPackage.MEMBER_OF__CONCEPT, oldConcept, newConcept);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setConcept(FocusConcept newConcept)
  {
    if (newConcept != concept)
    {
      NotificationChain msgs = null;
      if (concept != null)
        msgs = ((InternalEObject)concept).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EclPackage.MEMBER_OF__CONCEPT, null, msgs);
      if (newConcept != null)
        msgs = ((InternalEObject)newConcept).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EclPackage.MEMBER_OF__CONCEPT, null, msgs);
      msgs = basicSetConcept(newConcept, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EclPackage.MEMBER_OF__CONCEPT, newConcept, newConcept));
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
      case EclPackage.MEMBER_OF__CONCEPT:
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
      case EclPackage.MEMBER_OF__CONCEPT:
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
      case EclPackage.MEMBER_OF__CONCEPT:
        setConcept((FocusConcept)newValue);
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
      case EclPackage.MEMBER_OF__CONCEPT:
        setConcept((FocusConcept)null);
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
      case EclPackage.MEMBER_OF__CONCEPT:
        return concept != null;
    }
    return super.eIsSet(featureID);
  }

} //MemberOfImpl
