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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.CoreTypeMappings;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class UUIDHandler extends Lifecycle implements IIDHandler
{
  public static final Set<ObjectType> OBJECT_ID_TYPES = Collections.singleton(ObjectType.UUID);

  private static final char NULL_CHAR = '0';

  private static final String NULL_STRING = Character.toString(NULL_CHAR);

  private static final char INTERNAL_CHAR = '@';

  private static final String INTERNAL_STRING = Character.toString(INTERNAL_CHAR);

  private DBStore store;

  public UUIDHandler(DBStore store)
  {
    this.store = store;
  }

  public DBStore getStore()
  {
    return store;
  }

  public int compare(CDOID id1, CDOID id2)
  {
    throw new UnsupportedOperationException();
  }

  public DBType getDBType()
  {
    return DBType.VARCHAR;
  }

  public Set<ObjectType> getObjectIDTypes()
  {
    return OBJECT_ID_TYPES;
  }

  public CDOID createCDOID(String val)
  {
    return create(INTERNAL_STRING + val);
  }

  public synchronized CDOID getLastObjectID()
  {
    throw new UnsupportedOperationException();
  }

  public synchronized void setLastObjectID(CDOID lastObjectID)
  {
    // Do nothing
  }

  public void adjustLastObjectID(CDOID maxID)
  {
    // Do nothing
  }

  public synchronized CDOID getNextLocalObjectID()
  {
    throw new UnsupportedOperationException();
  }

  public synchronized void setNextLocalObjectID(CDOID nextLocalObjectID)
  {
    // Do nothing
  }

  public synchronized CDOID getNextCDOID(CDORevision revision)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isLocalCDOID(CDOID id)
  {
    return false;
  }

  public ITypeMapping getObjectTypeMapping()
  {
    return new CoreTypeMappings.TMObject();
  }

  public void appendCDOID(StringBuilder builder, CDOID id)
  {
    builder.append("'");
    builder.append(value(id));
    builder.append("'");
  }

  public void setCDOIDRaw(PreparedStatement stmt, int column, Object rawID) throws SQLException
  {
    stmt.setString(column, (String)rawID);
  }

  public void setCDOID(PreparedStatement stmt, int column, CDOID id) throws SQLException
  {
    setCDOID(stmt, column, id, CDOBranchPoint.INVALID_DATE);
  }

  public void setCDOID(PreparedStatement stmt, int column, CDOID id, long commitTime) throws SQLException
  {
    stmt.setString(column, value(id));
  }

  public CDOID getCDOID(ResultSet resultSet, int column) throws SQLException
  {
    String id = resultSet.getString(column);
    if (resultSet.wasNull())
    {
      return null;
    }

    return create(id);
  }

  public CDOID getCDOID(ResultSet resultSet, String name) throws SQLException
  {
    String id = resultSet.getString(name);
    if (resultSet.wasNull())
    {
      return null;
    }

    return create(id);
  }

  public CDOID getMinCDOID()
  {
    throw new UnsupportedOperationException();
  }

  public CDOID getMaxCDOID()
  {
    throw new UnsupportedOperationException();
  }

  public CDOID mapURI(IDBStoreAccessor accessor, String uri, long commitTime)
  {
    return CDOIDUtil.createExternal(uri);
  }

  public String unmapURI(IDBStoreAccessor accessor, CDOID id)
  {
    return CDOIDUtil.getString(id);
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    // Do nothing
  }

  public void rawImport(Connection connection, CDODataInput in, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    // Do nothing
  }

  private static CDOID create(String id)
  {
    if (id == null)
    {
      return null;
    }

    int length = id.length();
    if (length == 0)
    {
      return null;
    }

    char firstChar = id.charAt(0);
    if (length == 1 && firstChar == NULL_CHAR)
    {
      return null;
    }

    if (firstChar == INTERNAL_CHAR)
    {
      byte[] bytes = CDOIDUtil.decodeUUID(id.substring(1));
      return CDOIDUtil.createUUID(bytes);
    }

    return CDOIDUtil.createExternal(id);
  }

  private static String value(CDOID id)
  {
    if (CDOIDUtil.isNull(id))
    {
      return NULL_STRING;
    }

    if (id.isExternal())
    {
      return CDOIDUtil.getString(id);
    }

    return INTERNAL_STRING + id.toURIFragment();
  }
}
