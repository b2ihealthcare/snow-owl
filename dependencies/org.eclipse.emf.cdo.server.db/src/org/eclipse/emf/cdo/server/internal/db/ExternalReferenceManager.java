/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Stefan Winkler - bug 249610: [DB] Support external references (Implementation)
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Stefan Winkler
 */
public class ExternalReferenceManager extends Lifecycle
{
  private static final int NULL = 0;

  private IDBTable table;

  private IDBField idField;

  private IDBField uriField;

  private IDBField timestampField;

  private final IIDHandler idHandler;

  private AtomicLong lastMappedID = new AtomicLong(NULL);

  @ExcludeFromDump
  private transient String sqlSelectByLongID;

  @ExcludeFromDump
  private transient String sqlSelectByURI;

  @ExcludeFromDump
  private transient String sqlInsert;

  public ExternalReferenceManager(IIDHandler idHandler)
  {
    this.idHandler = idHandler;
  }

  public IIDHandler getIDHandler()
  {
    return idHandler;
  }

  public long mapExternalReference(CDOIDExternal id, long commitTime)
  {
    IDBStoreAccessor accessor = getAccessor();
    return mapURI(accessor, id.getURI(), commitTime);
  }

  public CDOIDExternal unmapExternalReference(long mappedId)
  {
    IDBStoreAccessor accessor = getAccessor();
    return CDOIDUtil.createExternal(unmapURI(accessor, mappedId));
  }

  public long mapURI(IDBStoreAccessor accessor, String uri, long commitTime)
  {
    long result = lookupByURI(accessor, uri);
    if (result < NULL)
    {
      // mapping found
      return result;
    }

    return insertNew(accessor, uri, commitTime);
  }

  public String unmapURI(IDBStoreAccessor accessor, long mappedId)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlSelectByLongID, ReuseProbability.HIGH);
      stmt.setLong(1, mappedId);
      resultSet = stmt.executeQuery();

      if (!resultSet.next())
      {
        OM.LOG.error("External ID " + mappedId + " not found. Database inconsistent!");
        throw new IllegalStateException("External ID " + mappedId + " not found. Database inconsistent!");
      }

      return resultSet.getString(1);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(stmt);
    }
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    String where = " WHERE " + timestampField + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, table, null, where);
  }

  public void rawImport(Connection connection, CDODataInput in, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    DBUtil.deserializeTable(in, connection, table, monitor);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    IDBStore store = idHandler.getStore();
    table = store.getDBSchema().addTable("cdo_external_refs"); //$NON-NLS-1$
    idField = table.addField("id", idHandler.getDBType()); //$NON-NLS-1$
    uriField = table.addField("uri", DBType.VARCHAR, 1024); //$NON-NLS-1$
    timestampField = table.addField("committime", DBType.BIGINT); //$NON-NLS-1$

    table.addIndex(IDBIndex.Type.PRIMARY_KEY, idField);
    table.addIndex(IDBIndex.Type.NON_UNIQUE, uriField);

    IDBStoreAccessor writer = store.getWriter(null);
    Connection connection = writer.getConnection();
    Statement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.createStatement();
      store.getDBAdapter().createTable(table, statement);
      connection.commit();

      String sql = "SELECT MIN(" + idField + ") FROM " + table;
      resultSet = statement.executeQuery(sql);

      if (resultSet.next())
      {
        lastMappedID.set(resultSet.getLong(1));
      }

      // else: resultSet is empty => table is empty
      // and lastMappedId stays 0 - as initialized.
    }
    catch (SQLException ex)
    {
      connection.rollback();
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(statement);
      writer.release();
    }

    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(table);
    builder.append("(");
    builder.append(idField);
    builder.append(",");
    builder.append(uriField);
    builder.append(",");
    builder.append(timestampField);
    builder.append(") VALUES (?, ?, ?)");
    sqlInsert = builder.toString();

    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(idField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(uriField);
    builder.append("=?"); //$NON-NLS-1$
    sqlSelectByURI = builder.toString();

    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(uriField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(idField);
    builder.append("=?"); //$NON-NLS-1$
    sqlSelectByLongID = builder.toString();
  }

  private long insertNew(IDBStoreAccessor accessor, String uri, long commitTime)
  {
    long newMappedID = lastMappedID.decrementAndGet();

    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlInsert, ReuseProbability.MEDIUM);
      stmt.setLong(1, newMappedID);
      stmt.setString(2, uri);
      stmt.setLong(3, commitTime);

      DBUtil.update(stmt, true);
      return newMappedID;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  private long lookupByURI(IDBStoreAccessor accessor, String uri)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlSelectByURI, ReuseProbability.HIGH);
      stmt.setString(1, uri);

      resultSet = stmt.executeQuery();

      if (resultSet.next())
      {
        return resultSet.getLong(1);
      }

      // Not found ...
      return NULL;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(stmt);
    }
  }

  private static IDBStoreAccessor getAccessor()
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (accessor == null)
    {
      throw new IllegalStateException("Can only be called from within a valid IDBStoreAccessor context");
    }

    return (IDBStoreAccessor)accessor;
  }
}
