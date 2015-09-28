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

import java.util.Set;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.State;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

/**
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOPackageRegistry extends CDOPackageRegistry, ILifecycle
{
  public void setReplacingDescriptors(boolean replacingDescriptors);

  public PackageProcessor getPackageProcessor();

  public void setPackageProcessor(PackageProcessor packageProcessor);

  public PackageLoader getPackageLoader();

  public void setPackageLoader(PackageLoader packageLoader);

  public Object basicPut(String nsURI, Object value);

  public void putPackageUnit(InternalCDOPackageUnit packageUnit);

  /**
   * @since 4.0
   */
  public void putPackageUnits(InternalCDOPackageUnit[] packageUnits, State state);

  /**
   * @since 4.0
   */
  public InternalCDOPackageUnit createPackageUnit();

  /**
   * @since 3.0
   */
  public InternalCDOPackageUnit getPackageUnit(String id);

  public InternalCDOPackageUnit getPackageUnit(EPackage ePackage);

  public InternalCDOPackageUnit[] getPackageUnits();

  /**
   * @since 4.0
   */
  public InternalCDOPackageUnit[] getPackageUnits(boolean withSystemPackages);

  /**
   * @since 3.0
   */
  public InternalCDOPackageUnit[] getPackageUnits(long startTime, long endTime);

  public InternalCDOPackageInfo getPackageInfo(EPackage ePackage);

  public InternalCDOPackageInfo[] getPackageInfos();

  public EPackage[] getEPackages();

  /**
   * @since 3.0
   */
  public Set<String> getAllKeys();

  /**
   * @since 3.0
   */
  public Object getWithDelegation(String nsURI, boolean resolve);

  /**
   * @author Eike Stepper
   */
  public interface PackageProcessor
  {
    public Object processPackage(Object value);
  }

  /**
   * @author Eike Stepper
   */
  public interface PackageLoader
  {
    public EPackage[] loadPackages(CDOPackageUnit packageUnit);
  }
}
