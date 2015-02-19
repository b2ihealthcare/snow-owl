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
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalBranchingMappingStrategy extends AbstractHorizontalMappingStrategy
{
  public HorizontalBranchingMappingStrategy()
  {
  }

  public boolean hasAuditSupport()
  {
    return true;
  }

  public boolean hasBranchingSupport()
  {
    return true;
  }

  public boolean hasDeltaSupport()
  {
    return false;
  }

  @Override
  public IClassMapping doCreateClassMapping(EClass eClass)
  {
    return new HorizontalBranchingClassMapping(this, eClass);
  }

  @Override
  public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new BranchingListTableMapping(this, containingClass, feature);
  }

  @Override
  public IListMapping doCreateFeatureMapMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new BranchingFeatureMapTableMapping(this, containingClass, feature);
  }

  @Override
  public String getListJoin(String attrTable, String listTable)
  {
    String join = getListJoinBasic(attrTable, listTable);
    return modifyListJoin(attrTable, listTable, join, false);
  }

  @Override
  protected String getListJoinForRawExport(String attrTable, String listTable)
  {
    String join = getListJoinBasic(attrTable, listTable);
    return modifyListJoin(attrTable, listTable, join, true);
  }

  protected String getListJoinBasic(String attrTable, String listTable)
  {
    return super.getListJoin(attrTable, listTable);
  }

  protected String modifyListJoin(String attrTable, String listTable, String join, boolean forRawExport)
  {
    join += " AND " + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION;
    join += "=" + listTable + "." + CDODBSchema.LIST_REVISION_VERSION;
    join += " AND " + attrTable + "." + CDODBSchema.ATTRIBUTES_BRANCH;
    join += "=" + listTable + "." + CDODBSchema.LIST_REVISION_BRANCH;
    return join;
  }

  @Override
  protected void rawImportReviseOldRevisions(Connection connection, IDBTable table, OMMonitor monitor)
  {
    String sqlUpdate = "UPDATE " + table + " SET " + CDODBSchema.ATTRIBUTES_REVISED + "=? WHERE "
        + CDODBSchema.ATTRIBUTES_ID + "=? AND " + CDODBSchema.ATTRIBUTES_BRANCH + "=? AND "
        + CDODBSchema.ATTRIBUTES_VERSION + "=?";

    String sqlQuery = "SELECT cdo1." + CDODBSchema.ATTRIBUTES_ID + ", cdo1." + CDODBSchema.ATTRIBUTES_BRANCH
        + ", cdo1." + CDODBSchema.ATTRIBUTES_VERSION + ", cdo2." + CDODBSchema.ATTRIBUTES_CREATED + " FROM " + table
        + " cdo1, " + table + " cdo2 WHERE cdo1." + CDODBSchema.ATTRIBUTES_ID + "=cdo2." + CDODBSchema.ATTRIBUTES_ID
        + " AND cdo1." + CDODBSchema.ATTRIBUTES_BRANCH + "=cdo2." + CDODBSchema.ATTRIBUTES_BRANCH + " AND (cdo1."
        + CDODBSchema.ATTRIBUTES_VERSION + "=cdo2." + CDODBSchema.ATTRIBUTES_VERSION + "-1 OR (cdo1."
        + CDODBSchema.ATTRIBUTES_VERSION + "+cdo2." + CDODBSchema.ATTRIBUTES_VERSION + "=-1 AND cdo1."
        + CDODBSchema.ATTRIBUTES_VERSION + ">cdo2." + CDODBSchema.ATTRIBUTES_VERSION + ")) AND cdo1."
        + CDODBSchema.ATTRIBUTES_REVISED + "=0";

    IIDHandler idHandler = getStore().getIDHandler();
    PreparedStatement stmtUpdate = null;
    PreparedStatement stmtQuery = null;
    ResultSet resultSet = null;

    try
    {
      stmtUpdate = connection.prepareStatement(sqlUpdate);
      stmtQuery = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      resultSet = stmtQuery.executeQuery();
      int size = DBUtil.getRowCount(resultSet);
      if (size == 0)
      {
        return;
      }

      monitor.begin(2 * size);
      while (resultSet.next())
      {
        CDOID id = idHandler.getCDOID(resultSet, 1);
        int branch = resultSet.getInt(2);
        int version = resultSet.getInt(3);
        long revised = resultSet.getLong(4) - 1L;

        stmtUpdate.setLong(1, revised);
        idHandler.setCDOID(stmtUpdate, 2, id);
        stmtUpdate.setInt(3, branch);
        stmtUpdate.setInt(4, version);
        stmtUpdate.addBatch();
        monitor.worked();
      }

      Async async = monitor.forkAsync(size);
      try
      {
        stmtUpdate.executeBatch();
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
      DBUtil.close(resultSet);
      DBUtil.close(stmtQuery);
      DBUtil.close(stmtUpdate);
      monitor.done();
    }
  }

  @Override
  protected void rawImportUnreviseNewRevisions(Connection connection, IDBTable table, long fromCommitTime,
      long toCommitTime, OMMonitor monitor)
  {
    String sqlUpdate = "UPDATE " + table + " SET " + CDODBSchema.ATTRIBUTES_REVISED + "=0 WHERE "
        + CDODBSchema.ATTRIBUTES_BRANCH + ">=0 AND " + CDODBSchema.ATTRIBUTES_CREATED + "<=" + toCommitTime + " AND "
        + CDODBSchema.ATTRIBUTES_REVISED + ">" + toCommitTime + " AND " + CDODBSchema.ATTRIBUTES_VERSION + ">0";

    PreparedStatement stmtUpdate = null;

    try
    {
      stmtUpdate = connection.prepareStatement(sqlUpdate);

      monitor.begin();
      Async async = monitor.forkAsync();

      try
      {
        stmtUpdate.executeUpdate();
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
      DBUtil.close(stmtUpdate);
      monitor.done();
    }
  }
}
