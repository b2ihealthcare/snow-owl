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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the Etypes model. It provides a create method for each non-abstract
 * class of the model.
 * 
 * @apiviz.uses {@link org.eclipse.emf.cdo.common.lob.CDOBlob} - - provides
 * @apiviz.uses {@link org.eclipse.emf.cdo.common.lob.CDOClob} - - provides
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.etypes.EtypesPackage
 * @generated
 */
public interface EtypesFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  EtypesFactory eINSTANCE = org.eclipse.emf.cdo.etypes.impl.EtypesFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Annotation</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Annotation</em>'.
   * @generated
   */
  Annotation createAnnotation();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  EtypesPackage getEtypesPackage();

} // EtypesFactory
