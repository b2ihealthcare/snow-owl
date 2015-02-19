/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.DBUtil.DeserializeRowHandler;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalBranchingMappingStrategyWithRanges extends HorizontalBranchingMappingStrategy
{
  private boolean copyOnBranch;

  public HorizontalBranchingMappingStrategyWithRanges()
  {
  }

  @Override
  public boolean hasDeltaSupport()
  {
    return true;
  }

  public boolean shallCopyOnBranch()
  {
    return copyOnBranch;
  }

  @Override
  public IClassMapping doCreateClassMapping(EClass eClass)
  {
    return new HorizontalBranchingClassMapping(this, eClass);
  }

  @Override
  public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new BranchingListTableMappingWithRanges(this, containingClass, feature);
  }

  @Override
  public IListMapping doCreateFeatureMapMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new BranchingFeatureMapTableMappingWithRanges(this, containingClass, feature);
  }

  @Override
  protected void rawExportList(CDODataOutput out, Connection connection, IListMapping listMapping, IDBTable attrTable,
      String attrSuffix) throws IOException
  {
    super.rawExportList(out, connection, listMapping, attrTable, attrSuffix);

    for (IDBTable table : listMapping.getDBTables())
    {
      rawExportListPostProcess(out, connection, attrTable, attrSuffix, table);
    }
  }

  private void rawExportListPostProcess(CDODataOutput out, Connection connection, IDBTable attrTable,
      String attrSuffix, IDBTable table) throws IOException
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT l_t.");
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append(", l_t.");
    builder.append(CDODBSchema.LIST_REVISION_BRANCH);
    builder.append(", l_t.");
    builder.append(CDODBSchema.LIST_REVISION_VERSION_ADDED);
    builder.append(", l_t.");
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append(", l_t.");
    builder.append(CDODBSchema.LIST_IDX);
    builder.append(" FROM ");
    builder.append(table);
    builder.append(" l_t, ");
    builder.append(attrTable);
    builder.append(" a_t");
    builder.append(attrSuffix);
    builder.append(getListJoinForPostProcess("a_t", "l_t"));
    builder.append(" AND l_t.");
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append(" IS NOT NULL");
    String sql = DBUtil.trace(builder.toString());

    IIDHandler idHandler = getStore().getIDHandler();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      resultSet = stmt.executeQuery();

      // Write resultSet size for progress monitoring
      int size = DBUtil.getRowCount(resultSet);
      out.writeInt(size);
      if (size == 0)
      {
        return;
      }

      while (resultSet.next())
      {
        CDOID source = idHandler.getCDOID(resultSet, 1);
        int branch = resultSet.getInt(2);
        int versionAdded = resultSet.getInt(3);
        int versionRemoved = resultSet.getInt(4);
        int idx = resultSet.getInt(5);

        out.writeCDOID(source);
        out.writeInt(branch);
        out.writeInt(versionAdded);
        out.writeInt(versionRemoved);
        out.writeInt(idx);
      }
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

  @Override
  protected void rawImportList(CDODataInput in, Connection connection, IListMapping listMapping, OMMonitor monitor)
      throws IOException
  {
    Collection<IDBTable> tables = listMapping.getDBTables();
    int size = tables.size();
    if (size == 0)
    {
      return;
    }

    monitor.begin(2 * size);

    try
    {
      super.rawImportList(in, connection, listMapping, monitor.fork(size));

      for (IDBTable table : tables)
      {
        rawImportListPostProcess(in, connection, table, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void rawImportListPostProcess(CDODataInput in, Connection connection, IDBTable table, OMMonitor monitor)
      throws IOException
  {
    int size = in.readInt();
    if (size == 0)
    {
      return;
    }

    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append("=? WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_ADDED);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("=?"); //$NON-NLS-1$
    String sql = DBUtil.trace(builder.toString());

    IIDHandler idHandler = getStore().getIDHandler();
    PreparedStatement stmt = null;

    monitor.begin(1 + 2 * size);

    try
    {
      stmt = connection.prepareStatement(sql);
      monitor.worked();

      for (int row = 0; row < size; row++)
      {
        CDOID source = in.readCDOID();
        int branch = in.readInt();
        int versionAdded = in.readInt();
        int versionRemoved = in.readInt();
        int idx = in.readInt();

        stmt.setInt(1, versionRemoved);
        idHandler.setCDOID(stmt, 2, source);
        stmt.setInt(3, branch);
        stmt.setInt(4, versionAdded);
        stmt.setInt(5, idx);

        stmt.addBatch();
        monitor.worked();
      }

      Async async = monitor.forkAsync(size);

      try
      {
        stmt.executeBatch();
      }
      finally
      {
        async.stop();
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
      monitor.done();
    }
  }

  protected String getListJoinForPostProcess(String attrTable, String listTable)
  {
    String join = getListJoinBasic(attrTable, listTable);
    return modifyListJoin2(attrTable, listTable, join, true, true);
  }

  @Override
  protected String modifyListJoin(String attrTable, String listTable, String join, boolean forRawExport)
  {
    return modifyListJoin2(attrTable, listTable, join, forRawExport, false);
  }

  private String modifyListJoin2(String attrTable, String listTable, String join, boolean forRawExport,
      boolean forPostProcess)
  {
    join += " AND " + listTable + ".";

    if (forRawExport)
    {
      if (forPostProcess)
      {
        join += CDODBSchema.LIST_REVISION_VERSION_REMOVED;
      }
      else
      {
        join += CDODBSchema.LIST_REVISION_VERSION_ADDED;
      }

      join += "=" + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION;
    }
    else
    {
      join += CDODBSchema.LIST_REVISION_VERSION_ADDED;
      join += "<=" + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION;
      join += " AND (" + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_REMOVED;
      join += " IS NULL OR " + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_REMOVED;
      join += ">" + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION + ")";
    }

    join += " AND " + attrTable + "." + CDODBSchema.ATTRIBUTES_BRANCH;
    join += "=" + listTable + "." + CDODBSchema.LIST_REVISION_BRANCH;

    if (forRawExport && !forPostProcess)
    {
      join += " ORDER BY " + listTable + "." + CDODBSchema.LIST_REVISION_ID;
      join += ", " + listTable + "." + CDODBSchema.LIST_REVISION_BRANCH;
      join += ", " + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_ADDED;
      join += ", " + listTable + "." + CDODBSchema.LIST_IDX;
    }

    return join;
  }

  @Override
  protected DeserializeRowHandler getImportListHandler()
  {
    return new ImportListHandler();
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();

    String value = getProperties().get(CDODBUtil.PROP_COPY_ON_BRANCH);
    copyOnBranch = value == null ? false : Boolean.valueOf(value);
  }

  /**
   * @author Eike Stepper
   */
  private final class ImportListHandler implements DeserializeRowHandler
  {
    private final IIDHandler idHandler = getStore().getIDHandler();

    private PreparedStatement stmt;

    public void handleRow(ExtendedDataInput in, Connection connection, IDBField[] fields, Object[] values)
        throws SQLException, IOException
    {
      int versionAdded = (Integer)values[2];
      if (versionAdded == CDOBranchVersion.FIRST_VERSION)
      {
        return;
      }

      if (stmt == null)
      {
        String sql = "UPDATE " + fields[0].getTable() //
            + " SET " + CDODBSchema.LIST_REVISION_VERSION_REMOVED + "=?" //
            + " WHERE " + CDODBSchema.LIST_REVISION_ID + "=?" //
            + " AND " + CDODBSchema.LIST_REVISION_BRANCH + "=?" //
            + " AND " + CDODBSchema.LIST_IDX + "=?" //
            + " AND " + CDODBSchema.LIST_REVISION_VERSION_ADDED + "<?" //
            + " AND " + CDODBSchema.LIST_REVISION_VERSION_REMOVED + " IS NULL";
        stmt = connection.prepareStatement(sql);
      }

      Object sourceID = values[0];
      int branch = (Integer)values[1];
      int index = (Integer)values[4];

      stmt.setInt(1, versionAdded);
      idHandler.setCDOIDRaw(stmt, 2, sourceID);
      stmt.setInt(3, branch);
      stmt.setInt(4, index);
      stmt.setInt(5, versionAdded);

      stmt.addBatch();
    }

    public void done(boolean successful) throws SQLException, IOException
    {
      if (stmt != null)
      {
        try
        {
          if (successful)
          {
            stmt.executeBatch();
          }
        }
        finally
        {
          DBUtil.close(stmt);
          stmt = null;
        }
      }
    }
  }
}
