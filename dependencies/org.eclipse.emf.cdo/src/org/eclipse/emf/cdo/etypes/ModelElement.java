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
package org.eclipse.emf.cdo.etypes;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Model Element</b></em>'.
 * 
 * @since 4.0 <!-- end-user-doc -->
 *        <p>
 *        The following features are supported:
 *        <ul>
 *        <li>{@link org.eclipse.emf.cdo.etypes.ModelElement#getAnnotations <em>Annotations</em>}</li>
 *        </ul>
 *        </p>
 * @see org.eclipse.emf.cdo.etypes.EtypesPackage#getModelElement()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface ModelElement extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Annotations</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.etypes.Annotation}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.etypes.Annotation#getModelElement <em>Model Element</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Annotations</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Annotations</em>' containment reference list.
   * @see org.eclipse.emf.cdo.etypes.EtypesPackage#getModelElement_Annotations()
   * @see org.eclipse.emf.cdo.etypes.Annotation#getModelElement
   * @model opposite="modelElement" containment="true"
   * @generated
   */
  EList<Annotation> getAnnotations();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @model
   * @generated
   */
  Annotation getAnnotation(String source);

} // ModelElement
