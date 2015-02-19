/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 271444: [DB] Multiple refactorings
 *    Kai Schlamp - bug 282976: [DB] Influence Mappings through EAnnotations
 *    Stefan Winkler - bug 282976: [DB] Influence Mappings through EAnnotations
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;

/**
 * Manages the {@link CDOPackageUnit elements} of the meta model level of a CDO {@link IRepository repository}.
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IMetaDataManager
{
  /**
   * Returns the meta ID of the given {@link EModelElement}. <code> getMetaID(getMetaInstance(x))</code> yields
   * <code>x</code>
   *
   * @param modelElement
   *          the element
   * @return the corresponding ID
   * @since 4.0
   */
  public CDOID getMetaID(EModelElement modelElement, long commitTime);

  /**
   * Returns the {@link EModelElement} referred to by the given ID. <code> getMetaInstance(getMetaID(m))</code> yields
   * <code>m</code>
   *
   * @since 4.0
   */
  public EModelElement getMetaInstance(CDOID id);

  /**
   * Loads a package unit from the database.
   *
   * @param connection
   *          the DB connection to read from.
   * @param packageUnit
   *          the package unit to load.
   * @return the loaded package unit.
   * @since 2.0
   */
  public EPackage[] loadPackageUnit(Connection connection, InternalCDOPackageUnit packageUnit);

  /**
   * @since 4.0
   */
  public void clearMetaIDMappings();

  /**
   * Reads information about package units present in the database.
   *
   * @param connection
   *          the DB connection to read from.
   * @return a collection of package unit information records which can be passed to
   *         {@link IMetaDataManager#loadPackageUnit(Connection, InternalCDOPackageUnit)} in order to read the EPackage.
   * @since 2.0
   */
  public Collection<InternalCDOPackageUnit> readPackageUnits(Connection connection);

  /**
   * Write package units to the database.
   *
   * @param connection
   *          the DB connection to write to.
   * @param packageUnits
   *          the package units to write.
   * @param monitor
   *          the monitor to indicate progress.
   * @since 2.0
   */
  public void writePackageUnits(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor);

  /**
   * @since 3.0
   */
  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException;

  /**
   * @since 4.0
   */
  public Collection<InternalCDOPackageUnit> rawImport(Connection connection, CDODataInput in, long fromCommitTime,
      long toCommitTime, OMMonitor monitor) throws IOException;

}
