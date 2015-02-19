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

import org.eclipse.emf.cdo.common.lob.CDOClob;

import java.io.Reader;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Text Resource</b></em>'.
 * 
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients. <!-- end-user-doc -->
 *           <p>
 *           The following features are supported:
 *           <ul>
 *           <li>{@link org.eclipse.emf.cdo.eresource.CDOTextResource#getContents <em>Contents</em>}</li>
 *           </ul>
 *           </p>
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOTextResource()
 * @model
 * @generated
 */
public interface CDOTextResource extends CDOFileResource<Reader>
{
  /**
   * Returns the value of the '<em><b>Contents</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Contents</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Contents</em>' attribute.
   * @see #setContents(CDOClob)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOTextResource_Contents()
   * @model dataType="org.eclipse.emf.cdo.etypes.Clob" required="true"
   * @generated
   */
  CDOClob getContents();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOTextResource#getContents <em>Contents</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Contents</em>' attribute.
   * @see #getContents()
   * @generated
   */
  void setContents(CDOClob value);

} // CDOTextResource
