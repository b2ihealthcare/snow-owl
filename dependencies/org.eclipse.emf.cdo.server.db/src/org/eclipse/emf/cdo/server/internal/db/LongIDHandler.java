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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.CoreTypeMappings;
import org.eclipse.emf.cdo.spi.server.LongIDStore;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LongIDHandler extends Lifecycle implements IIDHandler
{
  public static final CDOID MIN = CDOID.NULL;

  public static final CDOID MAX = create(Long.MAX_VALUE);

  private DBStore store;

  private ExternalReferenceManager externalReferenceManager;

  private CDOID lastObjectID = MIN;

  private CDOID nextLocalObjectID = MAX;

  public LongIDHandler(DBStore store)
  {
    this.store = store;
    externalReferenceManager = new ExternalReferenceManager(this);
  }

  public DBStore getStore()
  {
    return store;
  }

  public Set<ObjectType> getObjectIDTypes()
  {
    return LongIDStore.OBJECT_ID_TYPES;
  }

  public CDOID getMinCDOID()
  {
    return MIN;
  }

  public CDOID getMaxCDOID()
  {
    return MAX;
  }

  public int compare(CDOID id1, CDOID id2)
  {
    return id1.compareTo(id2);
  }

  public CDOID createCDOID(String val)
  {
    Long id = Long.valueOf(val);
    return create(id);
  }

  public synchronized CDOID getLastObjectID()
  {
    return lastObjectID;
  }

  public synchronized void setLastObjectID(CDOID lastObjectID)
  {
    this.lastObjectID = lastObjectID;
  }

  public synchronized void adjustLastObjectID(CDOID maxID)
  {
    if (compare(maxID, lastObjectID) > 0)
    {
      lastObjectID = maxID;
    }
  }

  public synchronized CDOID getNextLocalObjectID()
  {
    return nextLocalObjectID;
  }

  public synchronized void setNextLocalObjectID(CDOID nextLocalObjectID)
  {
    this.nextLocalObjectID = nextLocalObjectID;
  }

  public synchronized CDOID getNextCDOID(CDORevision revision)
  {
    if (revision.getBranch().isLocal())
    {
      CDOID result = nextLocalObjectID;
      nextLocalObjectID = create(value(result) - 1);
      return result;
    }

    lastObjectID = create(value(lastObjectID) + 1);
    return lastObjectID;
  }

  public boolean isLocalCDOID(CDOID id)
  {
    return compare(id, nextLocalObjectID) > 0;
  }

  public DBType getDBType()
  {
    return DBType.BIGINT;
  }

  public ITypeMapping getObjectTypeMapping()
  {
    return new CoreTypeMappings.TMObject();
  }

  public void appendCDOID(StringBuilder builder, CDOID id)
  {
    long value = value(id);
    builder.append(value);
  }

  public void setCDOIDRaw(PreparedStatement stmt, int column, Object rawID) throws SQLException
  {
    stmt.setLong(column, (Long)rawID);
  }

  public void setCDOID(PreparedStatement stmt, int column, CDOID id) throws SQLException
  {
    setCDOID(stmt, column, id, CDOBranchPoint.INVALID_DATE);
  }

  public void setCDOID(PreparedStatement stmt, int column, CDOID id, long commitTime) throws SQLException
  {
    long value;
    if (id.getType() == CDOID.Type.EXTERNAL_OBJECT)
    {
      if (commitTime == CDOBranchPoint.INVALID_DATE)
      {
        CommitContext commitContext = StoreThreadLocal.getCommitContext();
        commitTime = commitContext != null ? commitContext.getBranchPoint().getTimeStamp()
            : CDOBranchPoint.UNSPECIFIED_DATE; // Happens on rawStore for workspace checkouts
      }

      value = externalReferenceManager.mapExternalReference((CDOIDExternal)id, commitTime);
    }
    else
    {
      value = value(id);
    }

    stmt.setLong(column, value);
  }

  public CDOID getCDOID(ResultSet resultSet, int column) throws SQLException
  {
    long id = resultSet.getLong(column);
    if (resultSet.wasNull())
    {
      return null;
    }

    return unmapExternalReference(id);
  }

  public CDOID getCDOID(ResultSet resultSet, String name) throws SQLException
  {
    long id = resultSet.getLong(name);
    if (resultSet.wasNull())
    {
      return null;
    }

    return unmapExternalReference(id);
  }

  private CDOID unmapExternalReference(long id)
  {
    if (id < 0)
    {
      return externalReferenceManager.unmapExternalReference(id);
    }

    return create(id);
  }

  public CDOID mapURI(IDBStoreAccessor accessor, String uri, long commitTime)
  {
    return create(externalReferenceManager.mapURI(accessor, uri, commitTime));
  }

  public String unmapURI(IDBStoreAccessor accessor, CDOID id)
  {
    if (id.getType() == CDOID.Type.EXTERNAL_OBJECT)
    {
      return ((CDOIDExternal)id).getURI();
    }

    return externalReferenceManager.unmapURI(accessor, value(id));
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    externalReferenceManager.rawExport(connection, out, fromCommitTime, toCommitTime);
  }

  public void rawImport(Connection connection, CDODataInput in, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    externalReferenceManager.rawImport(connection, in, fromCommitTime, toCommitTime, monitor);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    externalReferenceManager.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    externalReferenceManager.deactivate();
    super.doDeactivate();
  }

  private static CDOID create(long id)
  {
    return CDOIDUtil.createLong(id);
  }

  private static long value(CDOID id)
  {
    return CDOIDUtil.getLong(id);
  }
}
