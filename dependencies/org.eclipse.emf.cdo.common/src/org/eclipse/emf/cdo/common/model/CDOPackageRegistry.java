/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 332912 - Caching subtype-relationships in the CDOPackageRegistry
 */
package org.eclipse.emf.cdo.common.model;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;

/**
 * An EMF {@link Registry package registry} that is used by CDO {@link CDOCommonRepository repositories} and
 * {@link CDOCommonSession sessions}.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.composedOf {@link CDOPackageUnit}
 */
public interface CDOPackageRegistry extends EPackage.Registry
{
  public boolean isReplacingDescriptors();

  /**
   * Registers an {@link EPackage} with this package registry.
   */
  public Object putEPackage(EPackage ePackage);

  /**
   * @since 3.0
   */
  public CDOPackageUnit getPackageUnit(String id);

  public CDOPackageUnit getPackageUnit(EPackage ePackage);

  /**
   * Returns all package units that are registered in this package registry.
   * 
   * @since 3.0
   */
  public CDOPackageUnit[] getPackageUnits();

  /**
   * @since 3.0
   */
  public CDOPackageUnit[] getPackageUnits(long startTime, long endTime);

  public CDOPackageInfo getPackageInfo(EPackage ePackage);

  /**
   * Returns all package infos that are registered in this package registry.
   */
  public CDOPackageInfo[] getPackageInfos();

  /**
   * @since 4.0
   */
  public Map<EClass, List<EClass>> getSubTypes();

  /**
   * @since 4.0
   */
  public EEnumLiteral getEnumLiteralFor(Enumerator value);
}
