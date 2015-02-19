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
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea.Handler;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaAlreadyExistsException;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class DurableLockingManager extends Lifecycle
{
  private DBStore store;

  private InternalCDOBranchManager branchManager;

  private IIDHandler idHandler;

  private IDBTable lockAreas;

  private IDBField lockAreasID;

  private IDBField lockAreasUser;

  private IDBField lockAreasBranch;

  private IDBField lockAreasTime;

  private IDBField lockAreasReadOnly;

  private IDBTable locks;

  private IDBField locksArea;

  private IDBField locksObject;

  private IDBField locksGrade;

  private String sqlInsertLockArea;

  private String sqlSelectLockArea;

  private String sqlSelectAllLockAreas;

  private String sqlSelectLockAreas;

  private String sqlDeleteLockArea;

  private String sqlDeleteLockAreas;

  private String sqlSelectLocks;

  private String sqlSelectLock;

  private String sqlInsertLock;

  private String sqlUpdateLock;

  private String sqlDeleteLock;

  private String sqlDeleteLocks;

  public DurableLockingManager(DBStore store)
  {
    this.store = store;
  }

  public synchronized LockArea createLockArea(DBStoreAccessor accessor, String durableLockingID, String userID,
      CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    try
    {
      if (durableLockingID == null)
      {
        durableLockingID = getNextDurableLockingID(accessor);
      }
      else
      {
        // If the caller is specifying the ID, make sure there is no area with this ID yet
        //
        try
        {
          getLockArea(accessor, durableLockingID);
          throw new LockAreaAlreadyExistsException(durableLockingID);
        }
        catch (LockAreaNotFoundException good)
        {
        }
      }

      IPreparedStatementCache statementCache = accessor.getStatementCache();
      PreparedStatement stmt = null;

      try
      {
        stmt = statementCache.getPreparedStatement(sqlInsertLockArea, ReuseProbability.LOW);
        stmt.setString(1, durableLockingID);
        stmt.setString(2, userID);
        stmt.setInt(3, branchPoint.getBranch().getID());
        stmt.setLong(4, branchPoint.getTimeStamp());
        stmt.setBoolean(5, readOnly);

        DBUtil.update(stmt, true);
      }
      catch (SQLException e)
      {
        throw new DBException(e);
      }
      finally
      {
        statementCache.releasePreparedStatement(stmt);
      }

      if (!locks.isEmpty())
      {
        insertLocks(accessor, durableLockingID, locks);
      }

      accessor.getConnection().commit();

      return CDOLockUtil.createLockArea(durableLockingID, userID, branchPoint, readOnly, locks);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  private void insertLocks(DBStoreAccessor accessor, String durableLockingID, Map<CDOID, LockGrade> locks)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlInsertLock, ReuseProbability.MEDIUM);
      stmt.setString(1, durableLockingID);

      for (Entry<CDOID, LockGrade> entry : locks.entrySet())
      {
        CDOID id = entry.getKey();
        int grade = entry.getValue().getValue();

        idHandler.setCDOID(stmt, 2, id);
        stmt.setInt(3, grade);

        DBUtil.update(stmt, true);
      }
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

  public LockArea getLockArea(DBStoreAccessor accessor, String durableLockingID) throws LockAreaNotFoundException
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlSelectLockArea, ReuseProbability.MEDIUM);
      stmt.setString(1, durableLockingID);
      resultSet = stmt.executeQuery();

      if (!resultSet.next())
      {
        throw new LockAreaNotFoundException(durableLockingID);
      }

      String userID = resultSet.getString(1);
      int branchID = resultSet.getInt(2);
      long timeStamp = resultSet.getLong(3);
      boolean readOnly = resultSet.getBoolean(4);

      return makeLockArea(accessor, durableLockingID, userID, branchID, timeStamp, readOnly);
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

  public void getLockAreas(DBStoreAccessor accessor, String userIDPrefix, Handler handler)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    try
    {
      if (userIDPrefix.length() == 0)
      {
        stmt = statementCache.getPreparedStatement(sqlSelectAllLockAreas, ReuseProbability.MEDIUM);
      }
      else
      {
        stmt = statementCache.getPreparedStatement(sqlSelectLockAreas, ReuseProbability.MEDIUM);
        stmt.setString(1, userIDPrefix + "%");
      }

      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        String durableLockingID = resultSet.getString(1);
        String userID = resultSet.getString(2);
        int branchID = resultSet.getInt(3);
        long timeStamp = resultSet.getLong(4);
        boolean readOnly = resultSet.getBoolean(5);

        LockArea area = makeLockArea(accessor, durableLockingID, userID, branchID, timeStamp, readOnly);
        if (!handler.handleLockArea(area))
        {
          break;
        }
      }
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

  public void deleteLockArea(DBStoreAccessor accessor, String durableLockingID)
  {
    try
    {
      unlockWithoutCommit(accessor, durableLockingID);

      IPreparedStatementCache statementCache = accessor.getStatementCache();
      PreparedStatement stmt = null;

      try
      {
        stmt = statementCache.getPreparedStatement(sqlDeleteLockArea, ReuseProbability.LOW);
        stmt.setString(1, durableLockingID);

        DBUtil.update(stmt, true);
      }
      catch (SQLException e)
      {
        throw new DBException(e);
      }
      finally
      {
        statementCache.releasePreparedStatement(stmt);
      }

      accessor.getConnection().commit();
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
  }

  public void updateLockArea(DBStoreAccessor accessor, LockArea area)
  {
    try
    {
      String areaID = area.getDurableLockingID();
      unlockWithoutCommit(accessor, areaID);
      insertLocks(accessor, areaID, area.getLocks());

      accessor.getConnection().commit();
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
  }

  public void lock(DBStoreAccessor accessor, String durableLockingID, LockType type,
      Collection<? extends Object> objectsToLock)
  {
    changeLocks(accessor, durableLockingID, type, objectsToLock, true);
  }

  public void unlock(DBStoreAccessor accessor, String durableLockingID, LockType type,
      Collection<? extends Object> objectsToUnlock)
  {
    changeLocks(accessor, durableLockingID, type, objectsToUnlock, false);
  }

  public void unlock(DBStoreAccessor accessor, String durableLockingID)
  {
    try
    {
      unlockWithoutCommit(accessor, durableLockingID);
      accessor.getConnection().commit();
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
  }

  private void unlockWithoutCommit(DBStoreAccessor accessor, String durableLockingID)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlDeleteLocks, ReuseProbability.MEDIUM);
      stmt.setString(1, durableLockingID);

      DBUtil.update(stmt, false);
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

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    branchManager = store.getRepository().getBranchManager();
    idHandler = store.getIDHandler();

    IDBSchema schema = store.getDBSchema();

    // Lock areas
    lockAreas = schema.addTable("cdo_lock_areas");
    lockAreasID = lockAreas.addField("id", DBType.VARCHAR);
    lockAreasUser = lockAreas.addField("user_id", DBType.VARCHAR);
    lockAreasBranch = lockAreas.addField("view_branch", DBType.INTEGER);
    lockAreasTime = lockAreas.addField("view_time", DBType.BIGINT);
    lockAreasReadOnly = lockAreas.addField("read_only", DBType.BOOLEAN);

    lockAreas.addIndex(IDBIndex.Type.PRIMARY_KEY, lockAreasID);
    lockAreas.addIndex(IDBIndex.Type.NON_UNIQUE, lockAreasUser);

    // Locks
    locks = schema.addTable("cdo_locks");
    locksArea = locks.addField("area_id", DBType.VARCHAR);
    locksObject = locks.addField("object_id", idHandler.getDBType());
    locksGrade = locks.addField("lock_grade", DBType.INTEGER);

    locks.addIndex(IDBIndex.Type.PRIMARY_KEY, locksArea, locksObject);
    locks.addIndex(IDBIndex.Type.NON_UNIQUE, locksArea);

    IDBStoreAccessor writer = store.getWriter(null);
    Connection connection = writer.getConnection();
    Statement statement = null;

    try
    {
      statement = connection.createStatement();
      store.getDBAdapter().createTable(lockAreas, statement);
      store.getDBAdapter().createTable(locks, statement);
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

    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(lockAreas);
    builder.append("("); //$NON-NLS-1$
    builder.append(lockAreasID);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasUser);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasBranch);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasTime);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasReadOnly);
    builder.append(") VALUES (?, ?, ?, ?, ?)"); //$NON-NLS-1$
    sqlInsertLockArea = builder.toString();

    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(lockAreasUser);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasBranch);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasTime);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasReadOnly);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(lockAreas);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(lockAreasID);
    builder.append("=?"); //$NON-NLS-1$
    sqlSelectLockArea = builder.toString();

    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(lockAreasID);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasUser);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasBranch);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasTime);
    builder.append(","); //$NON-NLS-1$
    builder.append(lockAreasReadOnly);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(lockAreas);
    sqlSelectAllLockAreas = builder.toString();

    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(lockAreasUser);
    builder.append(" LIKE ?"); //$NON-NLS-1$
    sqlSelectLockAreas = builder.toString();

    builder = new StringBuilder();
    builder.append("DELETE FROM "); //$NON-NLS-1$
    builder.append(lockAreas);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(lockAreasID);
    builder.append("=?"); //$NON-NLS-1$
    sqlDeleteLockArea = builder.toString();

    builder = new StringBuilder();
    builder.append("DELETE FROM ");
    builder.append(lockAreas);
    builder.append(" WHERE EXISTS (SELECT * FROM ");
    builder.append(locks);
    builder.append(" WHERE ");
    builder.append(locks);
    builder.append(".");
    builder.append(locksArea);
    builder.append("=");
    builder.append(lockAreas);
    builder.append(".");
    builder.append(lockAreasID);
    builder.append(")");
    sqlDeleteLockAreas = builder.toString();

    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(locksObject);
    builder.append(","); //$NON-NLS-1$
    builder.append(locksGrade);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(locks);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(locksArea);
    builder.append("=?"); //$NON-NLS-1$
    sqlSelectLocks = builder.toString();

    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(locksGrade);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(locks);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(locksArea);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(locksObject);
    builder.append("=?"); //$NON-NLS-1$
    sqlSelectLock = builder.toString();

    builder = new StringBuilder();
    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(locks);
    builder.append("("); //$NON-NLS-1$
    builder.append(locksArea);
    builder.append(","); //$NON-NLS-1$
    builder.append(locksObject);
    builder.append(","); //$NON-NLS-1$
    builder.append(locksGrade);
    builder.append(") VALUES (?, ?, ?)"); //$NON-NLS-1$
    sqlInsertLock = builder.toString();

    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(locks);
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(locksGrade);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(locksArea);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(locksObject);
    builder.append("=?"); //$NON-NLS-1$
    sqlUpdateLock = builder.toString();

    builder = new StringBuilder();
    builder.append("DELETE FROM "); //$NON-NLS-1$
    builder.append(locks);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(locksArea);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(locksObject);
    builder.append("=?"); //$NON-NLS-1$
    sqlDeleteLock = builder.toString();

    builder = new StringBuilder();
    builder.append("DELETE FROM "); //$NON-NLS-1$
    builder.append(locks);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(locksArea);
    builder.append("=?"); //$NON-NLS-1$
    sqlDeleteLocks = builder.toString();
  }

  private String getNextDurableLockingID(DBStoreAccessor accessor)
  {
    for (;;)
    {
      String durableLockingID = CDOLockUtil.createDurableLockingID();

      try
      {
        getLockArea(accessor, durableLockingID); // Check uniqueness
        // Not unique; try once more...
      }
      catch (LockAreaNotFoundException ex)
      {
        return durableLockingID;
      }
    }
  }

  private LockArea makeLockArea(DBStoreAccessor accessor, String durableLockingID, String userID, int branchID,
      long timeStamp, boolean readOnly)
  {
    CDOBranchPoint branchPoint = branchManager.getBranch(branchID).getPoint(timeStamp);
    Map<CDOID, LockGrade> lockMap = getLockMap(accessor, durableLockingID);
    return CDOLockUtil.createLockArea(durableLockingID, userID, branchPoint, readOnly, lockMap);
  }

  private Map<CDOID, LockGrade> getLockMap(DBStoreAccessor accessor, String durableLockingID)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlSelectLocks, ReuseProbability.MEDIUM);
      stmt.setString(1, durableLockingID);
      resultSet = stmt.executeQuery();

      Map<CDOID, LockGrade> lockMap = new HashMap<CDOID, LockGrade>();
      while (resultSet.next())
      {
        CDOID id = idHandler.getCDOID(resultSet, 1);
        int grade = resultSet.getInt(2);

        lockMap.put(id, LockGrade.get(grade));
      }

      return lockMap;
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

  private void changeLocks(DBStoreAccessor accessor, String durableLockingID, LockType type,
      Collection<? extends Object> keys, boolean on)
  {
    if (keys.isEmpty())
    {
      return;
    }

    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmtSelect = null;
    PreparedStatement stmtInsertOrDelete = null;
    PreparedStatement stmtUpdate = null;
    ResultSet resultSet = null;

    try
    {
      stmtSelect = statementCache.getPreparedStatement(sqlSelectLock, ReuseProbability.MEDIUM);
      stmtSelect.setString(1, durableLockingID);

      String sql = on ? sqlInsertLock : sqlDeleteLock;
      stmtInsertOrDelete = statementCache.getPreparedStatement(sql, ReuseProbability.MEDIUM);
      stmtInsertOrDelete.setString(1, durableLockingID);

      stmtUpdate = statementCache.getPreparedStatement(sqlUpdateLock, ReuseProbability.MEDIUM);
      stmtUpdate.setString(2, durableLockingID);

      InternalLockManager lockManager = accessor.getStore().getRepository().getLockingManager();
      for (Object key : keys)
      {
        CDOID id = lockManager.getLockKeyID(key);
        idHandler.setCDOID(stmtSelect, 2, id);
        resultSet = stmtSelect.executeQuery();

        LockGrade oldGrade = LockGrade.NONE;
        if (resultSet.next())
        {
          oldGrade = LockGrade.get(resultSet.getInt(1));
        }

        LockGrade newGrade = oldGrade.getUpdated(type, on);
        if (on && oldGrade == LockGrade.NONE)
        {
          idHandler.setCDOID(stmtInsertOrDelete, 2, id);
          stmtInsertOrDelete.setInt(3, newGrade.getValue());
          DBUtil.update(stmtInsertOrDelete, true);
        }
        else if (!on && newGrade == LockGrade.NONE)
        {
          idHandler.setCDOID(stmtInsertOrDelete, 2, id);
          DBUtil.update(stmtInsertOrDelete, true);
        }
        else
        {
          stmtUpdate.setInt(1, newGrade.getValue());
          idHandler.setCDOID(stmtUpdate, 3, id);
          DBUtil.update(stmtUpdate, true);
        }
      }

      accessor.getConnection().commit();
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(stmtUpdate);
      statementCache.releasePreparedStatement(stmtInsertOrDelete);
      statementCache.releasePreparedStatement(stmtSelect);
    }
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    DBUtil.serializeTable(out, connection, lockAreas, null, null);
    DBUtil.serializeTable(out, connection, locks, null, null);
  }

  public void rawImport(Connection connection, CDODataInput in, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    monitor.begin(4);

    try
    {
      // Delete all non-empty lock areas
      DBUtil.update(connection, sqlDeleteLockAreas);
      monitor.worked();

      DBUtil.deserializeTable(in, connection, lockAreas, monitor.fork());

      DBUtil.clearTable(connection, locks);
      monitor.worked();

      DBUtil.deserializeTable(in, connection, locks, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }
}
