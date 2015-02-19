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
public class StringIDHandler extends Lifecycle implements IIDHandler
{
  public static final Set<ObjectType> OBJECT_ID_TYPES = Collections.singleton(CDOID.ObjectType.STRING);

  public static final CDOID MIN = CDOID.NULL;

  public static final CDOID MAX = create(Long.toString(Long.MAX_VALUE));

  private DBStore store;

  private long lastObjectID = 0;

  private long nextLocalObjectID = Long.MAX_VALUE;

  public StringIDHandler(DBStore store)
  {
    this.store = store;
  }

  public DBStore getStore()
  {
    return store;
  }

  public int compare(CDOID id1, CDOID id2)
  {
    if (id1.getType() == CDOID.Type.OBJECT && id2.getType() == CDOID.Type.OBJECT)
    {
      return Long.valueOf(value(id1)).compareTo(Long.valueOf(value(id2)));
    }

    return id1.compareTo(id2);
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
    return create(val);
  }

  public synchronized CDOID getLastObjectID()
  {
    return CDOIDUtil.createString("" + lastObjectID);
  }

  public synchronized void setLastObjectID(CDOID lastObjectID)
  {
    this.lastObjectID = Long.parseLong(value(lastObjectID));
  }

  public void adjustLastObjectID(CDOID maxID)
  {
    // TODO: implement StringIDHandler.adjustLastObjectID(maxID)
    throw new UnsupportedOperationException();
  }

  public synchronized CDOID getNextLocalObjectID()
  {
    return CDOIDUtil.createString("" + nextLocalObjectID);
  }

  public synchronized void setNextLocalObjectID(CDOID nextLocalObjectID)
  {
    this.nextLocalObjectID = Long.parseLong(value(nextLocalObjectID));
  }

  public synchronized CDOID getNextCDOID(CDORevision revision)
  {
    if (revision.getBranch().isLocal())
    {
      return CDOIDUtil.createString("" + nextLocalObjectID--);
    }

    return CDOIDUtil.createString("" + ++lastObjectID);
  }

  public boolean isLocalCDOID(CDOID id)
  {
    if (id.getType() == CDOID.Type.OBJECT)
    {
      return Long.parseLong(value(id)) > nextLocalObjectID;
    }

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
    String value = value(id);
    stmt.setString(column, value == null || value.length() == 0 ? "0" : value);
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
    return MIN;
  }

  public CDOID getMaxCDOID()
  {
    return MAX;
  }

  public CDOID mapURI(IDBStoreAccessor accessor, String uri, long commitTime)
  {
    return create(uri);
  }

  public String unmapURI(IDBStoreAccessor accessor, CDOID id)
  {
    return value(id);
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
    if (length == 1 && firstChar == '0')
    {
      return null;
    }

    if (Character.isDigit(firstChar))
    {
      long value = Long.parseLong(id);
      if (value < 0)
      {
        throw new IllegalArgumentException("Illegal ID value: " + id);
      }

      return CDOIDUtil.createString(id);
    }

    return CDOIDUtil.createExternal(id);
  }

  private static String value(CDOID id)
  {
    return CDOIDUtil.getString(id);
  }
}
