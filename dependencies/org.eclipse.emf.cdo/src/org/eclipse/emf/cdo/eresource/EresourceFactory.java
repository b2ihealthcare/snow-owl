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
package org.eclipse.emf.cdo.eresource;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the Eresource model. It provides a create method for each non-abstract
 * class of the model.
 * 
 * @apiviz.exclude
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage
 * @generated
 */
public interface EresourceFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  EresourceFactory eINSTANCE = org.eclipse.emf.cdo.eresource.impl.EresourceFactoryImpl.init();

  /**
   * Returns a new object of class '<em>CDO Resource Folder</em>'. <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @return a new object of class '<em>CDO Resource Folder</em>'.
   * @generated
   */
  CDOResourceFolder createCDOResourceFolder();

  /**
   * Returns a new object of class '<em>CDO Resource</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>CDO Resource</em>'.
   * @generated
   */
  CDOResource createCDOResource();

  /**
   * Returns a new object of class '<em>CDO Binary Resource</em>'. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return a new object of class '<em>CDO Binary Resource</em>'.
   * @generated
   */
  CDOBinaryResource createCDOBinaryResource();

  /**
   * Returns a new object of class '<em>CDO Text Resource</em>'. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return a new object of class '<em>CDO Text Resource</em>'.
   * @generated
   */
  CDOTextResource createCDOTextResource();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  EresourcePackage getEresourcePackage();

} // EresourceFactory
