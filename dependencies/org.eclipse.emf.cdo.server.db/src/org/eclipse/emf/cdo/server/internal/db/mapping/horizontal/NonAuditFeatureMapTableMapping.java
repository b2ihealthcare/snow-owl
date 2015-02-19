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
 *    Christopher Albert - 254455: [DB] Support FeatureMaps bug 254455
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * This is a featuremap-to-table mapping optimized for non-audit-mode. It doesn't care about version and has delta
 * support.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public class NonAuditFeatureMapTableMapping extends AbstractFeatureMapTableMapping implements IListMappingDeltaSupport
{
  private FieldInfo[] keyFields;

  private static final int TEMP_INDEX = -1;

  private static final int UNBOUNDED_MOVE = -1;

  private String sqlClear;

  private String sqlUpdateIndex;

  private String sqlUpdateValue;

  private String sqlDeleteItem;

  private String sqlMoveDownWithLimit;

  private String sqlMoveDown;

  private String sqlMoveUpWithLimit;

  private String sqlMoveUp;

  public NonAuditFeatureMapTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
    initSQLStrings();
  }

  private void initSQLStrings()
  {
    // TODO: add key fields length support

    StringBuilder builder = new StringBuilder();

    // ----------- clear list -------------------------

    builder.append("DELETE FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? "); //$NON-NLS-1$

    sqlClear = builder.toString();

    builder.append(" AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("=? "); //$NON-NLS-1$

    sqlDeleteItem = builder.toString();

    // ----------- update one item index --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("=? "); //$NON-NLS-1$
    sqlUpdateIndex = builder.toString();

    // ----------- update one item value --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$

    builder.append(CDODBSchema.FEATUREMAP_TAG);
    builder.append("=?,"); //$NON-NLS-1$

    Iterator<String> iter = getColumnNames().iterator();
    while (iter.hasNext())
    {
      String column = iter.next();
      builder.append(column);
      builder.append("=?"); //$NON-NLS-1$

      if (iter.hasNext())
      {
        builder.append(", "); //$NON-NLS-1$
      }
    }

    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("=? "); //$NON-NLS-1$
    sqlUpdateValue = builder.toString();

    // ----------- move down --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("="); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("-1 WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(">? "); //$NON-NLS-1$
    sqlMoveDown = builder.toString();

    builder.append(" AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("<=?"); //$NON-NLS-1$
    sqlMoveDownWithLimit = builder.toString();

    // ----------- move up --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("="); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("+1 WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(">=? "); //$NON-NLS-1$
    sqlMoveUp = builder.toString();

    builder.append(" AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("<?"); //$NON-NLS-1$
    sqlMoveUpWithLimit = builder.toString();
  }

  @Override
  protected FieldInfo[] getKeyFields()
  {
    if (keyFields == null)
    {
      DBType dbType = getMappingStrategy().getStore().getIDHandler().getDBType();
      keyFields = new FieldInfo[] { new FieldInfo(CDODBSchema.FEATUREMAP_REVISION_ID, dbType) };
    }

    return keyFields;
  }

  @Override
  protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, 1, revision.getID());
  }

  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    clearList(accessor, id);
  }

  /**
   * Clear a list of a given revision.
   *
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision from which to remove all items
   */
  public void clearList(IDBStoreAccessor accessor, CDOID id)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlClear, ReuseProbability.HIGH);
      getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, 1, id);
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
  public void rawDeleted(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version)
  {
    clearList(accessor, id);
  }

  /**
   * Insert a list item at a specified position.
   *
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision to insert the value
   * @param index
   *          the index where to insert the element
   * @param value
   *          the value to insert.
   */
  public void insertListItem(IDBStoreAccessor accessor, CDOID id, int index, Object value, long timestamp)
  {
    move1up(accessor, id, index, UNBOUNDED_MOVE);
    insertValue(accessor, id, index, value, timestamp);
  }

  private void insertValue(IDBStoreAccessor accessor, CDOID id, int index, Object value, long timestamp)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      FeatureMap.Entry entry = (FeatureMap.Entry)value;
      EStructuralFeature entryFeature = entry.getEStructuralFeature();
      CDOID tag = getTagByFeature(entryFeature, timestamp);
      String columnName = getColumnName(tag);

      String sql = sqlInsert;

      stmt = statementCache.getPreparedStatement(sql, ReuseProbability.HIGH);

      idHandler.setCDOID(stmt, 1, id);
      int column = getKeyFields().length + 1;

      for (int i = 0; i < getColumnNames().size(); i++)
      {
        if (getColumnNames().get(i).equals(columnName))
        {
          getTypeMapping(tag).setValue(stmt, column++, entry.getValue());
        }
        else
        {
          stmt.setNull(column++, getDBTypes().get(i).getCode());
        }
      }

      stmt.setInt(column++, index);
      idHandler.setCDOID(stmt, column++, tag);

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
  }

  /**
   * Move a list item from one position to another. Indices between both positions are updated so that the list remains
   * consistent.
   *
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision in which to move the item
   * @param oldPosition
   *          the old position of the item.
   * @param newPosition
   *          the new position of the item.
   */
  public void moveListItem(IDBStoreAccessor accessor, CDOID id, int oldPosition, int newPosition)
  {
    if (oldPosition == newPosition)
    {
      return;
    }

    // move element away temporarily
    updateOneIndex(accessor, id, oldPosition, TEMP_INDEX);

    // move elements in between
    if (oldPosition < newPosition)
    {
      move1down(accessor, id, oldPosition, newPosition);
    }
    else
    {
      // oldPosition > newPosition -- equal case is handled above
      move1up(accessor, id, newPosition, oldPosition);
    }

    // move temporary element to new position
    updateOneIndex(accessor, id, TEMP_INDEX, newPosition);
  }

  private void updateOneIndex(IDBStoreAccessor accessor, CDOID id, int oldIndex, int newIndex)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlUpdateIndex, ReuseProbability.HIGH);
      stmt.setInt(1, newIndex);
      idHandler.setCDOID(stmt, 2, id);
      stmt.setInt(3, oldIndex);
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
  }

  /**
   * Remove a list item from a specified a position.
   *
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision from which to remove the item
   * @param index
   *          the index of the item to remove
   */
  public void removeListItem(IDBStoreAccessor accessor, CDOID id, int index)
  {
    deleteItem(accessor, id, index);
    move1down(accessor, id, index, UNBOUNDED_MOVE);
  }

  /**
   * Move references downwards to close a gap at position <code>index</code>. Only indexes starting with
   * <code>index + 1</code> and ending with <code>upperIndex</code> are moved down.
   */
  private void move1down(IDBStoreAccessor accessor, CDOID id, int index, int upperIndex)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(upperIndex == UNBOUNDED_MOVE ? sqlMoveDown : sqlMoveDownWithLimit,
          ReuseProbability.HIGH);

      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, index);
      if (upperIndex != UNBOUNDED_MOVE)
      {
        stmt.setInt(3, upperIndex);
      }

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

  /**
   * Move references downwards to close a gap at position <code>index</code>. Only indexes starting with
   * <code>index + 1</code> and ending with <code>upperIndex</code> are moved down.
   */
  private void move1up(IDBStoreAccessor accessor, CDOID id, int index, int upperIndex)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(upperIndex == UNBOUNDED_MOVE ? sqlMoveUp : sqlMoveUpWithLimit,
          ReuseProbability.HIGH);
      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, index);
      if (upperIndex != UNBOUNDED_MOVE)
      {
        stmt.setInt(3, upperIndex);
      }

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

  private void deleteItem(IDBStoreAccessor accessor, CDOID id, int index)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlDeleteItem, ReuseProbability.HIGH);
      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, index);
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
  }

  /**
   * Set a value at a specified position to the given value.
   *
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision to set the value
   * @param index
   *          the index of the item to set
   * @param value
   *          the value to be set.
   */
  public void setListItem(IDBStoreAccessor accessor, CDOID id, int index, Object value, long timestamp)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    FeatureMap.Entry entry = (FeatureMap.Entry)value;
    EStructuralFeature entryFeature = entry.getEStructuralFeature();
    CDOID tag = getTagByFeature(entryFeature, timestamp);
    String columnName = getColumnName(tag);
    ITypeMapping mapping = getTypeMapping(tag);

    try
    {
      stmt = statementCache.getPreparedStatement(sqlUpdateValue, ReuseProbability.HIGH);
      idHandler.setCDOID(stmt, 1, tag);
      int column = 2;

      for (int i = 0; i < getColumnNames().size(); i++)
      {
        if (getColumnNames().get(i).equals(columnName))
        {
          mapping.setValue(stmt, column++, entry.getValue());
        }
        else
        {
          stmt.setNull(column++, getDBTypes().get(i).getCode());
        }
      }

      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, index);
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
  }

  public void processDelta(final IDBStoreAccessor accessor, final CDOID id, final int branchId, int oldVersion,
      final int newVersion, final long created, CDOListFeatureDelta listDelta)
  {
    CDOFeatureDeltaVisitor visitor = new CDOFeatureDeltaVisitor()
    {
      public void visit(CDOMoveFeatureDelta delta)
      {
        moveListItem(accessor, id, delta.getOldPosition(), delta.getNewPosition());
      }

      public void visit(CDOAddFeatureDelta delta)
      {
        insertListItem(accessor, id, delta.getIndex(), delta.getValue(), created);
      }

      public void visit(CDORemoveFeatureDelta delta)
      {
        removeListItem(accessor, id, delta.getIndex());
      }

      public void visit(CDOSetFeatureDelta delta)
      {
        setListItem(accessor, id, delta.getIndex(), delta.getValue(), created);
      }

      public void visit(CDOUnsetFeatureDelta delta)
      {
        if (delta.getFeature().isUnsettable())
        {
          throw new ImplementationError("Should not be called"); //$NON-NLS-1$
        }

        clearList(accessor, id);
      }

      public void visit(CDOListFeatureDelta delta)
      {
        throw new ImplementationError("Should not be called"); //$NON-NLS-1$
      }

      public void visit(CDOClearFeatureDelta delta)
      {
        clearList(accessor, id);
      }

      public void visit(CDOContainerFeatureDelta delta)
      {
        throw new ImplementationError("Should not be called"); //$NON-NLS-1$
      }
    };

    for (CDOFeatureDelta delta : listDelta.getListChanges())
    {
      delta.accept(visitor);
    }
  }
}
