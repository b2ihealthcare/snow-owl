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

import org.eclipse.emf.cdo.common.lob.CDOLob;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO File Resource</b></em>'.
 *
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOFileResource()
 * @model abstract="true"
 * @generated
 */
public interface CDOFileResource<IO> extends CDOResourceLeaf
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" dataType="org.eclipse.emf.cdo.etypes.Lob" required="true"
   * @generated
   */
  CDOLob<IO> getContents();

} // CDOFileResource
