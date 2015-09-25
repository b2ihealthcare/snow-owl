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
package org.eclipse.emf.cdo.spi.common.model;

import java.io.IOException;

import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOPackageUnit extends CDOPackageUnit
{
  public InternalCDOPackageRegistry getPackageRegistry();

  public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry);

  public void setState(State state);

  public void setOriginalType(Type originalType);

  public void setTimeStamp(long timeStamp);

  public InternalCDOPackageInfo getTopLevelPackageInfo();

  public InternalCDOPackageInfo getPackageInfo(String packageURI);

  public InternalCDOPackageInfo[] getPackageInfos();

  public void setPackageInfos(InternalCDOPackageInfo[] packageInfos);

  /**
   * @since 4.0
   */
  public void load(boolean resolve);

  /**
   * @since 4.0
   */
  public void load(PackageLoader packageLoader, boolean resolve);

  /**
   * @since 3.0
   */
  public void write(CDODataOutput out, boolean withPackages) throws IOException;

  /**
   * @since 3.0
   */
  public void read(CDODataInput in, ResourceSet resourceSet) throws IOException;

  public void init(EPackage ePackage);

  public void dispose();
}
