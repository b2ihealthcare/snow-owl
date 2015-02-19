/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;
import java.sql.Connection;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public interface IObjectTypeMapper
{
  public CDOClassifierRef getObjectType(IDBStoreAccessor accessor, CDOID id);

  public void putObjectType(IDBStoreAccessor accessor, long timeStamp, CDOID id, EClass type);

  public void removeObjectType(IDBStoreAccessor accessor, CDOID id);

  /**
   * Return the maximum object id managed by this cache.
   * 
   * @param connection
   *          the DB connection to use.
   * @return the maximum object ID.
   */
  public CDOID getMaxID(Connection connection, IIDHandler idHandler);

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException;

  public void rawImport(Connection connection, CDODataInput in, OMMonitor monitor) throws IOException;
}
