/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 259402
 *    Stefan Winkler - redesign (prepared statements)
 *    Stefan Winkler - bug 276926
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class ObjectTypeTable extends AbstractObjectTypeMapper
{
  private IDBTable table;

  private IDBField idField;

  private IDBField typeField;

  private IDBField timeField;

  private String sqlDelete;

  private String sqlInsert;

  private String sqlSelect;

  public ObjectTypeTable()
  {
  }

  public final CDOClassifierRef getObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlSelect, ReuseProbability.MAX);
      idHandler.setCDOID(stmt, 1, id);
      DBUtil.trace(stmt.toString());
      ResultSet resultSet = stmt.executeQuery();

      if (!resultSet.next())
      {
        DBUtil.trace("ClassID for CDOID " + id + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
      }

      CDOID classID = idHandler.getCDOID(resultSet, 1);
      EClass eClass = (EClass)getMetaDataManager().getMetaInstance(classID);
      return new CDOClassifierRef(eClass);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  public final void putObjectType(IDBStoreAccessor accessor, long timeStamp, CDOID id, EClass type)
  {
    IDBStore store = getMappingStrategy().getStore();
    IIDHandler idHandler = store.getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlInsert, ReuseProbability.MAX);
      idHandler.setCDOID(stmt, 1, id);
      idHandler.setCDOID(stmt, 2, getMetaDataManager().getMetaID(type, timeStamp));
      stmt.setLong(3, timeStamp);
      DBUtil.trace(stmt.toString());
      int result = stmt.executeUpdate();

      if (result != 1)
      {
        throw new DBException("Object type could not be inserted: " + id); //$NON-NLS-1$
      }
    }
    catch (SQLException ex)
    {
      // Unique key violation can occur in rare cases (merging new objects from other branches)
      if (!store.getDBAdapter().isDuplicateKeyException(ex))
      {
        throw new DBException(ex);
      }
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  public final void removeObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlDelete, ReuseProbability.MAX);
      idHandler.setCDOID(stmt, 1, id);
      DBUtil.trace(stmt.toString());
      int result = stmt.executeUpdate();

      if (result != 1)
      {
        throw new DBException("Object type could not be deleted: " + id); //$NON-NLS-1$
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  public CDOID getMaxID(Connection connection, IIDHandler idHandler)
  {
    Statement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = connection.createStatement();
      resultSet = stmt.executeQuery("SELECT MAX(" + idField + ") FROM " + table);

      if (resultSet.next())
      {
        return idHandler.getCDOID(resultSet, 1);
      }

      return null;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    String where = " WHERE " + timeField + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, table, null, where);
  }

  public void rawImport(Connection connection, CDODataInput in, OMMonitor monitor) throws IOException
  {
    DBUtil.deserializeTable(in, connection, table, monitor);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    IDBStore store = getMappingStrategy().getStore();
    IDBSchema schema = store.getDBSchema();
    DBType dbType = store.getIDHandler().getDBType();

    table = schema.addTable(CDODBSchema.CDO_OBJECTS);
    idField = table.addField(CDODBSchema.ATTRIBUTES_ID, dbType);
    typeField = table.addField(CDODBSchema.ATTRIBUTES_CLASS, dbType);
    timeField = table.addField(CDODBSchema.ATTRIBUTES_CREATED, DBType.BIGINT);
    table.addIndex(IDBIndex.Type.UNIQUE, idField);

    IDBAdapter dbAdapter = store.getDBAdapter();
    IDBStoreAccessor writer = store.getWriter(null);
    Connection connection = writer.getConnection();
    Statement statement = null;

    try
    {
      statement = connection.createStatement();
      dbAdapter.createTable(table, statement);
      connection.commit();
    }
    catch (SQLException ex)
    {
      connection.rollback();
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(statement);
      writer.release();
    }

    sqlSelect = "SELECT " + typeField + " FROM " + table + " WHERE " + idField + "=?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    sqlInsert = "INSERT INTO " + table + "(" + idField + "," + typeField + "," + timeField + ") VALUES (?, ?, ?)";
    sqlDelete = "DELETE FROM " + table + " WHERE " + idField + "=?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    table = null;
    idField = null;
    typeField = null;
    super.doDeactivate();
  }
}
