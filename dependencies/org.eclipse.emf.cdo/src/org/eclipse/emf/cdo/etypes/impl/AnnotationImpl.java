/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes.impl;

import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Annotation</b></em>'.
 * 
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients. <!-- end-user-doc -->
 *           <p>
 *           The following features are implemented:
 *           <ul>
 *           <li>{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl#getSource <em>Source</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl#getDetails <em>Details</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl#getModelElement <em>Model Element</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl#getContents <em>Contents</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl#getReferences <em>References</em>}</li>
 *           </ul>
 *           </p>
 * @generated
 */
public class AnnotationImpl extends ModelElementImpl implements Annotation
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected AnnotationImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EtypesPackage.Literals.ANNOTATION;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getSource()
  {
    return (String)eGet(EtypesPackage.Literals.ANNOTATION__SOURCE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setSource(String newSource)
  {
    eSet(EtypesPackage.Literals.ANNOTATION__SOURCE, newSource);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<String, String> getDetails()
  {
    return (EMap<String, String>)eGet(EtypesPackage.Literals.ANNOTATION__DETAILS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ModelElement getModelElement()
  {
    return (ModelElement)eGet(EtypesPackage.Literals.ANNOTATION__MODEL_ELEMENT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setModelElement(ModelElement newModelElement)
  {
    eSet(EtypesPackage.Literals.ANNOTATION__MODEL_ELEMENT, newModelElement);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<EObject> getContents()
  {
    return (EList<EObject>)eGet(EtypesPackage.Literals.ANNOTATION__CONTENTS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<EObject> getReferences()
  {
    return (EList<EObject>)eGet(EtypesPackage.Literals.ANNOTATION__REFERENCES, true);
  }

} // AnnotationImpl
