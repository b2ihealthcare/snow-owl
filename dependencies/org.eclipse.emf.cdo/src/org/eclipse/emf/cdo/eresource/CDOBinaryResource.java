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

import org.eclipse.emf.cdo.common.lob.CDOBlob;

import java.io.InputStream;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Binary Resource</b></em>'.
 *
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients. <!-- end-user-doc -->
 *              <p>
 *              The following features are supported:
 *              <ul>
 *              <li>{@link org.eclipse.emf.cdo.eresource.CDOBinaryResource#getContents <em>Contents</em>}</li>
 *              </ul>
 *              </p>
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOBinaryResource()
 * @model
 * @generated
 */
public interface CDOBinaryResource extends CDOFileResource<InputStream>
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
   * @see #setContents(CDOBlob)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOBinaryResource_Contents()
   * @model dataType="org.eclipse.emf.cdo.etypes.Blob" required="true"
   * @generated
   */
  CDOBlob getContents();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOBinaryResource#getContents <em>Contents</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Contents</em>' attribute.
   * @see #getContents()
   * @generated
   */
  void setContents(CDOBlob value);

} // CDOBinaryResource
