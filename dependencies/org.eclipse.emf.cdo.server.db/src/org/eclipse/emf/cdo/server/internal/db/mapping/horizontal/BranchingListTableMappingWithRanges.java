/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * This class has been derived from AbstractListTableMapping
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation taken from AuditListTableMappingWithRanges
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
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
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex.Type;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * This is a list-table mapping for audit mode. It is optimized for frequent insert operations at the list's end, which
 * causes just 1 DB row to be changed. This is achieved by introducing a version range (columns cdo_version_added and
 * cdo_version_removed) which records for which revisions a particular entry existed. Also, this mapping is mainly
 * optimized for potentially very large lists: the need for having the complete list stored in memopy to do
 * in-the-middle-moved and inserts is traded in for a few more DB access operations.
 *
 * @author Eike Stepper
 * @author Stefan Winkler
 * @author Lothar Werzinger
 */
public class BranchingListTableMappingWithRanges extends BasicAbstractListTableMapping implements
    IListMappingDeltaSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, BranchingListTableMappingWithRanges.class);

  /**
   * Used to clean up lists for detached objects.
   */
  private static final int FINAL_VERSION = Integer.MAX_VALUE;

  /**
   * The table of this mapping.
   */
  private IDBTable table;

  /**
   * The type mapping for the value field.
   */
  private ITypeMapping typeMapping;

  // --------- SQL strings - see initSQLStrings() -----------------
  private String sqlSelectChunksPrefix;

  private String sqlOrderByIndex;

  private String sqlInsertEntry;

  private String sqlDeleteEntry;

  private String sqlRemoveEntry;

  private String sqlUpdateIndex;

  private String sqlGetValue;

  private String sqlClearList;

  public BranchingListTableMappingWithRanges(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
    initTable();
    initSQLStrings();
  }

  private void initTable()
  {
    IDBStore store = getMappingStrategy().getStore();
    String tableName = getMappingStrategy().getTableName(getContainingClass(), getFeature());
    table = store.getDBSchema().addTable(tableName);

    IDBField[] dbFields = new IDBField[5];

    dbFields[0] = table.addField(CDODBSchema.LIST_REVISION_ID, store.getIDHandler().getDBType());
    dbFields[1] = table.addField(CDODBSchema.LIST_REVISION_BRANCH, DBType.INTEGER);
    dbFields[2] = table.addField(CDODBSchema.LIST_REVISION_VERSION_ADDED, DBType.INTEGER);
    dbFields[3] = table.addField(CDODBSchema.LIST_REVISION_VERSION_REMOVED, DBType.INTEGER);
    dbFields[4] = table.addField(CDODBSchema.LIST_IDX, DBType.INTEGER);

    // add field for value
    typeMapping = getMappingStrategy().createValueMapping(getFeature());
    typeMapping.createDBField(table, CDODBSchema.LIST_VALUE);

    // add table indexes
    for (IDBField dbField : dbFields)
    {
      table.addIndex(Type.NON_UNIQUE, dbField);
    }
  }

  public Collection<IDBTable> getDBTables()
  {
    return Arrays.asList(table);
  }

  private void initSQLStrings()
  {
    String tableName = getTable().getName();

    // ---------------- read chunks ----------------------------
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_VALUE);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_ADDED);
    builder.append("<=? AND ("); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append(" IS NULL OR "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append(">?)"); //$NON-NLS-1$
    sqlSelectChunksPrefix = builder.toString();

    sqlOrderByIndex = " ORDER BY " + CDODBSchema.LIST_IDX; //$NON-NLS-1$

    // ----------------- insert entry -----------------
    builder = new StringBuilder("INSERT INTO "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append("("); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append(","); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_BRANCH);
    builder.append(","); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_ADDED);
    builder.append(","); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append(","); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append(","); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_VALUE);
    builder.append(") VALUES (?, ?, ?, ?, ?, ?)"); //$NON-NLS-1$
    sqlInsertEntry = builder.toString();

    // ----------------- remove current entry -----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append(" IS NULL"); //$NON-NLS-1$
    sqlRemoveEntry = builder.toString();

    // ----------------- delete temporary entry -----------------
    builder = new StringBuilder("DELETE FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_ADDED);
    builder.append("=?"); //$NON-NLS-1$
    sqlDeleteEntry = builder.toString();

    // ----------------- update index -----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("=? WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_ADDED);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("=?"); //$NON-NLS-1$
    sqlUpdateIndex = builder.toString();

    // ----------------- get current value -----------------
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_VALUE);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append(" IS NULL"); //$NON-NLS-1$
    sqlGetValue = builder.toString();

    // ----------- clear list items -------------------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_VERSION_REMOVED);
    builder.append(" IS NULL"); //$NON-NLS-1$
    sqlClearList = builder.toString();
  }

  protected final IDBTable getTable()
  {
    return table;
  }

  protected final ITypeMapping getTypeMapping()
  {
    return typeMapping;
  }

  public void readValues(IDBStoreAccessor accessor, InternalCDORevision revision, final int listChunk)
  {
    MoveableList<Object> list = revision.getList(getFeature());
    int valuesToRead = list.size();
    if (listChunk != CDORevision.UNCHUNKED && listChunk < valuesToRead)
    {
      valuesToRead = listChunk;
    }

    if (valuesToRead == 0)
    {
      // nothing to read take shortcut
      return;
    }

    CDOID id = revision.getID();
    int branchID = revision.getBranch().getID();

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values for feature {0}.{1} of {2}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), revision);
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    IStoreChunkReader baseReader = null;
    try
    {
      String sql = sqlSelectChunksPrefix + sqlOrderByIndex;
      stmt = statementCache.getPreparedStatement(sql, ReuseProbability.HIGH);
      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, branchID);
      stmt.setInt(3, revision.getVersion());
      stmt.setInt(4, revision.getVersion());
      stmt.setMaxRows(valuesToRead); // optimization - don't read unneeded rows.

      resultSet = stmt.executeQuery();

      int currentIndex = 0;

      while (valuesToRead > 0 && resultSet.next())
      {
        int index = resultSet.getInt(1);
        if (index > currentIndex)
        {
          if (baseReader == null)
          {
            baseReader = createBaseChunkReader(accessor, id, branchID);
          }

          baseReader.addRangedChunk(currentIndex, index);
          if (TRACER.isEnabled())
          {
            TRACER.format("Scheduling range {0}-{1} to be read from base revision", currentIndex, index); //$NON-NLS-1$
          }

          valuesToRead -= index - currentIndex;
          currentIndex = index;
        }

        Object value = typeMapping.readValue(resultSet);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for index {0} from result set: {1}", currentIndex, value); //$NON-NLS-1$
        }

        list.set(currentIndex++, value);
        valuesToRead--;
      }

      if (valuesToRead > 0)
      {
        if (baseReader == null)
        {
          baseReader = createBaseChunkReader(accessor, id, branchID);
        }

        baseReader.addRangedChunk(currentIndex, currentIndex + valuesToRead);
        if (TRACER.isEnabled())
        {
          TRACER.format(
              "Scheduling range {0}-{1} to be read from base revision", currentIndex, currentIndex + valuesToRead); //$NON-NLS-1$
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(stmt);
    }

    if (baseReader != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading base revision chunks for feature {0}.{1} of {2} from base revision {3}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), revision, baseReader.getRevision());
      }

      List<Chunk> baseChunks = baseReader.executeRead();
      for (Chunk chunk : baseChunks)
      {
        int startIndex = chunk.getStartIndex();
        for (int i = 0; i < chunk.size(); i++)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Copying value {0} at chunk index {1}+{2} to index {3}", //$NON-NLS-1$
                chunk.get(i), startIndex, i, startIndex + i);
          }

          list.set(startIndex + i, chunk.get(i));
        }
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {3} list values done for feature {0}.{1} of {2}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), revision, list.size());
    }
  }

  public final void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    CDORevision revision = chunkReader.getRevision();
    CDOID id = revision.getID();
    int branchID = revision.getBranch().getID();

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list chunk values for feature {0}.{1} of {2}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), revision);
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = chunkReader.getAccessor().getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    IStoreChunkReader baseReader = null;

    try
    {
      StringBuilder builder = new StringBuilder(sqlSelectChunksPrefix);
      if (where != null)
      {
        builder.append(" AND "); //$NON-NLS-1$
        builder.append(where);
      }

      builder.append(sqlOrderByIndex);

      String sql = builder.toString();
      stmt = statementCache.getPreparedStatement(sql, ReuseProbability.LOW);
      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, branchID);
      stmt.setInt(3, revision.getVersion());
      stmt.setInt(4, revision.getVersion());

      if (TRACER.isEnabled())
      {
        TRACER.format("Readung Chunks: {0}", stmt); //$NON-NLS-1$
      }

      resultSet = stmt.executeQuery();

      int nextDBIndex = Integer.MAX_VALUE; // next available DB index
      if (resultSet.next())
      {
        nextDBIndex = resultSet.getInt(1);
      }

      for (Chunk chunk : chunks)
      {
        int startIndex = chunk.getStartIndex();
        int missingValueStartIndex = -1;

        for (int i = 0; i < chunk.size(); i++)
        {
          int nextListIndex = startIndex + i; // next expected list index

          if (nextDBIndex == nextListIndex)
          {
            // DB value is available. check first if missing indexes were present before.
            if (missingValueStartIndex != -1)
            {
              // read missing indexes from missingValueStartIndex to currentIndex
              if (baseReader == null)
              {
                baseReader = createBaseChunkReader(chunkReader.getAccessor(), id, branchID);
              }

              if (TRACER.isEnabled())
              {
                TRACER.format(
                    "Scheduling range {0}-{1} to be read from base revision", missingValueStartIndex, nextListIndex); //$NON-NLS-1$
              }

              baseReader.addRangedChunk(missingValueStartIndex, nextListIndex);

              // reset missingValueStartIndex
              missingValueStartIndex = -1;
            }

            // now read value and set to chunk
            Object value = typeMapping.readValue(resultSet);
            if (TRACER.isEnabled())
            {
              TRACER.format("ChunkReader read value for index {0} from result set: {1}", nextDBIndex, value); //$NON-NLS-1$
            }

            chunk.add(i, value);

            // advance DB cursor and read next available index
            if (resultSet.next())
            {
              nextDBIndex = resultSet.getInt(1);
            }
            else
            {
              // no more DB indexes available, but we have to continue checking for gaps, therefore set to MAX_VALUE
              nextDBIndex = Integer.MAX_VALUE;
            }
          }
          else
          {
            // gap between next DB index and next list index detected.
            // skip until end of chunk or until DB value becomes available
            if (missingValueStartIndex == -1)
            {
              missingValueStartIndex = nextListIndex;
            }
          }
        }

        // chunk complete. check for missing values at the end of the chunk.
        if (missingValueStartIndex != -1)
        {
          // read missing indexes from missingValueStartIndex to last chunk index
          if (baseReader == null)
          {
            baseReader = createBaseChunkReader(chunkReader.getAccessor(), id, branchID);
          }

          if (TRACER.isEnabled())
          {
            TRACER
                .format(
                    "Scheduling range {0}-{1} to be read from base revision", missingValueStartIndex, chunk.getStartIndex() + chunk.size()); //$NON-NLS-1$
          }

          baseReader.addRangedChunk(missingValueStartIndex, chunk.getStartIndex() + chunk.size());
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(stmt);
    }

    // now read missing values from base revision.
    if (baseReader != null)
    {
      List<Chunk> baseChunks = baseReader.executeRead();

      Iterator<Chunk> thisIterator = chunks.iterator();
      Chunk thisChunk = thisIterator.next();

      for (Chunk baseChunk : baseChunks)
      {
        int baseStartIndex = baseChunk.getStartIndex();

        while (baseStartIndex > thisChunk.getStartIndex() + thisChunk.size())
        {
          // advance thisChunk, because it does not match baseChunk
          thisChunk = thisIterator.next();
        }

        // baseChunk now corresponds to thisChunk, but startIndex of baseChunk may be higher.
        // therefore calculate offset
        int offset = baseStartIndex - thisChunk.getStartIndex();

        // and copy values.
        for (int i = 0; i < baseChunk.size(); i++)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Copying base chunk reader value {0} at index {1} to current chunk reader at index {2}.",
                baseChunk.get(i), baseChunk.getStartIndex() + i, thisChunk.getStartIndex() + i + offset);
          }

          thisChunk.add(i + offset, baseChunk.get(i));
        } // finally, continue with the next baseChunk
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list chunk values done for feature {0}.{1} of {2}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), revision);
    }
  }

  public void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    CDOList values = revision.getList(getFeature());

    int idx = 0;
    for (Object element : values)
    {
      writeValue(accessor, revision, idx++, element);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing done"); //$NON-NLS-1$
    }
  }

  protected final void writeValue(IDBStoreAccessor accessor, CDORevision revision, int index, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing value for feature {0}.{1} index {2} of {3} : {4}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, revision, value);
    }

    addEntry(accessor, revision.getID(), revision.getBranch().getID(), revision.getVersion(), index, value);
  }

  /**
   * Clear a list of a given revision.
   *
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision from which to remove all items
   * @param lastIndex
   */
  public void clearList(IDBStoreAccessor accessor, CDOID id, int branchId, int oldVersion, int newVersion, int lastIndex)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      // check for each index if the value exists in the current branch
      for (int i = 0; i <= lastIndex; i++)
      {
        if (getValue(accessor, id, branchId, i, false) == null)
        {
          // if not, add a historic entry for missing ones.
          addHistoricEntry(accessor, id, branchId, 0, newVersion, i, getValueFromBase(accessor, id, branchId, i));
        }
      }

      // clear rest of the list
      stmt = statementCache.getPreparedStatement(sqlClearList, ReuseProbability.HIGH);
      stmt.setInt(1, newVersion);
      getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, 2, id);
      stmt.setInt(3, branchId);

      int result = DBUtil.update(stmt, false);
      if (TRACER.isEnabled())
      {
        TRACER.format("ClearList result: {0}", result); //$NON-NLS-1$
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

  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    ITransaction transaction = accessor.getTransaction();
    InternalCDORevision revision = (InternalCDORevision)transaction.getRevision(id);
    if (revision == null)
    {
      // This must be an attempt to resurrect an object, i.e., revise its detached revision
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("objectDetached {1}", revision); //$NON-NLS-1$
    }

    int branchID = transaction.getBranch().getID();
    int version = revision.getVersion();
    int lastIndex = revision.getList(getFeature()).size() - 1;

    clearList(accessor, id, branchID, version, FINAL_VERSION, lastIndex);
  }

  @Override
  public void rawDeleted(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version)
  {
    throw new UnsupportedOperationException("Raw deletion does not work in range-based mappings");
  }

  public void processDelta(final IDBStoreAccessor accessor, final CDOID id, final int branchId, final int oldVersion,
      final int newVersion, long created, CDOListFeatureDelta delta)
  {
    List<CDOFeatureDelta> listChanges = delta.getListChanges();
    if (listChanges.size() == 0)
    {
      // nothing to do.
      return;
    }

    InternalCDORevision originalRevision = (InternalCDORevision)accessor.getTransaction().getRevision(id);
    int oldListSize = originalRevision.getList(getFeature()).size();

    if (TRACER.isEnabled())
    {
      TRACER.format("ListTableMapping.processDelta for revision {0} - previous list size: {1}", originalRevision, //$NON-NLS-1$
          oldListSize);
    }

    // let the visitor collect the changes
    ListDeltaVisitor visitor = new ListDeltaVisitor(accessor, originalRevision, branchId, oldVersion, newVersion);

    if (TRACER.isEnabled())
    {
      TRACER.format("Processing deltas..."); //$NON-NLS-1$
    }

    // optimization: it's only necessary to process deltas
    // starting with the last feature delta which clears the list
    // (any operation before the clear is cascaded by it anyway)
    int index = listChanges.size() - 1;
    while (index > 0)
    {
      CDOFeatureDelta listDelta = listChanges.get(index);
      if (listDelta instanceof CDOClearFeatureDelta || listDelta instanceof CDOUnsetFeatureDelta)
      {
        break;
      }
      index--;
    }
    while (index < listChanges.size())
    {
      listChanges.get(index++).accept(visitor);
    }
  }

  /**
   * @author Stefan Winkler
   * @author Andras Peteri
   */
  private class ListDeltaVisitor implements CDOFeatureDeltaVisitor
  {
    private IDBStoreAccessor accessor;

    private CDOID id;

    private int branchID;

    private int oldVersion;

    private int newVersion;

    private int lastIndex;

    public ListDeltaVisitor(IDBStoreAccessor accessor, InternalCDORevision originalRevision, int targetBranchID,
        int oldVersion, int newVersion)
    {
      this.accessor = accessor;
      id = originalRevision.getID();
      branchID = targetBranchID;
      this.oldVersion = oldVersion;
      this.newVersion = newVersion;
      lastIndex = originalRevision.getList(getFeature()).size() - 1;
    }

    public void visit(CDOAddFeatureDelta delta)
    {
      int startIndex = delta.getIndex();
      int endIndex = lastIndex;

      if (TRACER.isEnabled())
      {
        TRACER.format("Delta Adding at: {0}", startIndex); //$NON-NLS-1$
      }

      if (startIndex <= endIndex)
      {
        // make room for the new item
        moveOneDown(accessor, id, branchID, oldVersion, newVersion, startIndex, endIndex);
      }

      // create the item
      addEntry(accessor, id, branchID, newVersion, startIndex, delta.getValue());

      ++lastIndex;
    }

    public void visit(CDORemoveFeatureDelta delta)
    {
      int startIndex = delta.getIndex();
      int endIndex = lastIndex;

      if (TRACER.isEnabled())
      {
        TRACER.format("Delta Removing at: {0}", startIndex); //$NON-NLS-1$
      }

      // remove the item
      removeEntry(accessor, id, branchID, oldVersion, newVersion, startIndex);

      if (!delta.getFeature().isOrdered() && startIndex < endIndex - 1)
      {
        Object value = getValue(accessor, id, branchID, endIndex, true);
        removeEntry(accessor, id, branchID, oldVersion, newVersion, endIndex);
        addEntry(accessor, id, branchID, newVersion, startIndex, value);
      }
      else
      {
        // make room for the new item
        moveOneUp(accessor, id, branchID, oldVersion, newVersion, startIndex + 1, endIndex);
      }

      --lastIndex;
    }

    public void visit(CDOSetFeatureDelta delta)
    {
      int index = delta.getIndex();

      if (TRACER.isEnabled())
      {
        TRACER.format("Delta Setting at: {0}", index); //$NON-NLS-1$
      }

      // remove the item
      removeEntry(accessor, id, branchID, oldVersion, newVersion, index);

      // create the item
      addEntry(accessor, id, branchID, newVersion, index, delta.getValue());
    }

    public void visit(CDOUnsetFeatureDelta delta)
    {
      if (delta.getFeature().isUnsettable())
      {
        throw new ImplementationError("Should not be called"); //$NON-NLS-1$
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Delta Unsetting"); //$NON-NLS-1$
      }

      clearList(accessor, id, branchID, oldVersion, newVersion, lastIndex);
      lastIndex = -1;
    }

    public void visit(CDOClearFeatureDelta delta)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Delta Clearing"); //$NON-NLS-1$
      }

      clearList(accessor, id, branchID, oldVersion, newVersion, lastIndex);
      lastIndex = -1;
    }

    public void visit(CDOMoveFeatureDelta delta)
    {
      int fromIdx = delta.getOldPosition();
      int toIdx = delta.getNewPosition();

      if (TRACER.isEnabled())
      {
        TRACER.format("Delta Moving: {0} to {1}", fromIdx, toIdx); //$NON-NLS-1$
      }

      Object value = getValue(accessor, id, branchID, fromIdx, true);

      // remove the item
      removeEntry(accessor, id, branchID, oldVersion, newVersion, fromIdx);

      // adjust indexes and shift either up or down for regular moves
      if (fromIdx < toIdx)
      {
        moveOneUp(accessor, id, branchID, oldVersion, newVersion, fromIdx + 1, toIdx);
      }
      else
      { // fromIdx > toIdx here
        moveOneDown(accessor, id, branchID, oldVersion, newVersion, toIdx, fromIdx - 1);
      }

      // create the item
      addEntry(accessor, id, branchID, newVersion, toIdx, value);
    }

    public void visit(CDOListFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDOContainerFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    private void moveOneUp(IDBStoreAccessor accessor, CDOID id, int branchId, int oldVersion, int newVersion,
        int startIndex, int endIndex)
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      IPreparedStatementCache statementCache = accessor.getStatementCache();
      PreparedStatement stmt = null;

      try
      {
        stmt = statementCache.getPreparedStatement(sqlUpdateIndex, ReuseProbability.HIGH);

        for (int index = startIndex; index <= endIndex; ++index)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("moveOneUp moving: {0} -> {1}", index, index - 1); //$NON-NLS-1$
          }

          int column = 1;
          stmt.setInt(column++, index - 1);
          idHandler.setCDOID(stmt, column++, id);
          stmt.setInt(column++, branchId);
          stmt.setInt(column++, newVersion);
          stmt.setInt(column++, index);

          int result = DBUtil.update(stmt, false);
          switch (result)
          {
          case 1:
            // entry for current revision was already present.
            // index update succeeded.
            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneUp updated: {0} -> {1}", index, index - 1); //$NON-NLS-1$
            }

            break;
          // no entry for current revision there.
          case 0:
            Object value = getValue(accessor, id, branchId, index, false);

            if (value != null)
            {
              if (TRACER.isEnabled())
              {
                TRACER.format("moveOneUp remove: {0}", index); //$NON-NLS-1$
              }

              removeEntry(accessor, id, branchId, oldVersion, newVersion, index);
            }
            else
            {
              value = getValueFromBase(accessor, id, branchId, index);
              if (TRACER.isEnabled())
              {
                TRACER.format("moveOneUp add historic entry at: {0}", index); //$NON-NLS-1$
              }

              addHistoricEntry(accessor, id, branchId, 0, newVersion, index, value);
            }

            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneUp add: {0}", index - 1); //$NON-NLS-1$
            }

            addEntry(accessor, id, branchId, newVersion, index - 1, value);
            break;
          default:
            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneUp Too many results: {0} -> {1}: {2}", index, index + 1, result); //$NON-NLS-1$
            }

            throw new DBException("Too many results"); //$NON-NLS-1$
          }
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

    private void moveOneDown(IDBStoreAccessor accessor, CDOID id, int branchId, int oldVersion, int newVersion,
        int startIndex, int endIndex)
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      IPreparedStatementCache statementCache = accessor.getStatementCache();
      PreparedStatement stmt = null;

      try
      {
        stmt = statementCache.getPreparedStatement(sqlUpdateIndex, ReuseProbability.HIGH);

        for (int index = endIndex; index >= startIndex; --index)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("moveOneDown moving: {0} -> {1}", index, index + 1); //$NON-NLS-1$
          }

          int column = 1;
          stmt.setInt(column++, index + 1);
          idHandler.setCDOID(stmt, column++, id);
          stmt.setInt(column++, branchId);
          stmt.setInt(column++, newVersion);
          stmt.setInt(column++, index);

          int result = DBUtil.update(stmt, false);
          switch (result)
          {
          case 1:
            // entry for current revision was already present.
            // index update succeeded.

            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneDown updated: {0} -> {1}", index, index + 1); //$NON-NLS-1$
            }

            break;
          case 0:
            Object value = getValue(accessor, id, branchId, index, false);

            if (value != null)
            {
              if (TRACER.isEnabled())
              {
                TRACER.format("moveOneDown remove: {0}", index); //$NON-NLS-1$
              }

              removeEntry(accessor, id, branchId, oldVersion, newVersion, index);
            }
            else
            {
              value = getValueFromBase(accessor, id, branchId, index);
              {
                TRACER.format("moveOneDown add historic entry at: {0}", index); //$NON-NLS-1$
              }

              addHistoricEntry(accessor, id, branchId, 0, newVersion, index, value);
            }

            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneDown add: {0}", index + 1); //$NON-NLS-1$
            }

            addEntry(accessor, id, branchId, newVersion, index + 1, value);
            break;
          default:
            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneDown Too many results: {0} -> {1}: {2}", index, index + 1, result); //$NON-NLS-1$
            }

            throw new DBException("Too many results"); //$NON-NLS-1$
          }
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
  }

  private void addEntry(IDBStoreAccessor accessor, CDOID id, int branchId, int version, int index, Object value)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    if (TRACER.isEnabled())
    {
      TRACER.format("Adding value for feature {0}.{1} index {2} of {3}:{4}v{5} : {6}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, id, branchId, version, value);
    }

    try
    {
      stmt = statementCache.getPreparedStatement(sqlInsertEntry, ReuseProbability.HIGH);

      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, branchId);
      stmt.setInt(column++, version); // versionAdded
      stmt.setNull(column++, DBType.INTEGER.getCode()); // versionRemoved
      stmt.setInt(column++, index);
      typeMapping.setValue(stmt, column++, value);

      DBUtil.update(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    catch (IllegalStateException e)
    {
      throw new DBException(e);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  private void addHistoricEntry(IDBStoreAccessor accessor, CDOID id, int branchId, int versionAdded,
      int versionRemoved, int index, Object value)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    if (TRACER.isEnabled())
    {
      TRACER.format(
          "Adding historic value for feature {0}.{1} index {2} of {3}:{4}v{5}-v{6} : {7}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, id, branchId, versionAdded, versionRemoved,
          value);
    }

    try
    {
      stmt = statementCache.getPreparedStatement(sqlInsertEntry, ReuseProbability.HIGH);

      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, branchId);
      stmt.setInt(column++, versionAdded); // versionAdded
      stmt.setInt(column++, versionRemoved); // versionRemoved
      stmt.setInt(column++, index);
      typeMapping.setValue(stmt, column++, value);

      DBUtil.update(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    catch (IllegalStateException e)
    {
      throw new DBException(e);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  private void removeEntry(IDBStoreAccessor accessor, CDOID id, int branchId, int oldVersion, int newVersion, int index)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    if (TRACER.isEnabled())
    {
      TRACER.format("Removing value for feature {0}.{1} index {2} of {3}:{4}v{5}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, id, branchId, newVersion);
    }

    try
    {
      // Try to delete a temporary entry first
      stmt = statementCache.getPreparedStatement(sqlDeleteEntry, ReuseProbability.HIGH);

      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, branchId);
      stmt.setInt(column++, index);
      stmt.setInt(column++, newVersion);

      int result = DBUtil.update(stmt, false);
      if (result == 1)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("removeEntry deleted: {0}", index); //$NON-NLS-1$
        }
      }
      else if (result > 1)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("removeEntry Too many results: {0}: {1}", index, result); //$NON-NLS-1$
        }

        throw new DBException("Too many results"); //$NON-NLS-1$
      }
      else
      {
        // no temporary entry found, so mark the entry as removed
        statementCache.releasePreparedStatement(stmt);
        stmt = statementCache.getPreparedStatement(sqlRemoveEntry, ReuseProbability.HIGH);

        column = 1;
        stmt.setInt(column++, newVersion);
        idHandler.setCDOID(stmt, column++, id);
        stmt.setInt(column++, branchId);
        stmt.setInt(column++, index);

        result = DBUtil.update(stmt, false);

        if (result == 0)
        {
          // no entry removed -> this means that we are in a branch and
          // the entry has not been modified since the branch fork.
          // therefore, we have to copy the base value and mark it as removed
          Object value = getValueFromBase(accessor, id, branchId, index);
          addHistoricEntry(accessor, id, branchId, 0, newVersion, index, value);
        }
      }
    }
    catch (SQLException e)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removing value for feature {0}.{1} index {2} of {3}:{4}v{5} FAILED {6}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), index, id, branchId, newVersion, e.getMessage());
      }

      throw new DBException(e);
    }
    catch (IllegalStateException e)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removing value for feature {0}.{1} index {2} of {3}:{4}v{5} FAILED {6}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), index, id, branchId, newVersion, e.getMessage());
      }

      throw new DBException(e);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  /**
   * Read a single value from the current revision's list.
   *
   * @param accessor
   *          the store accessor
   * @param id
   *          the revision's ID
   * @param branchId
   *          the revision's branch ID
   * @param index
   *          the index from which to get the value
   * @param getFromBase
   *          if <code>true</code>, the value is recursively loaded from the base revision of a branch, if it is not
   *          present in the current branch (because it has not been changed since the branch fork). If
   *          <code>false</code>, <code>null</code> is returned in the former case.
   */
  private Object getValue(IDBStoreAccessor accessor, CDOID id, int branchId, int index, boolean getFromBase)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    Object result = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlGetValue, ReuseProbability.HIGH);
      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, branchId);
      stmt.setInt(column++, index);

      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next())
      {
        result = typeMapping.readValue(resultSet);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read value (index {0}) from result set: {1}", index, result); //$NON-NLS-1$
        }
      }
      else
      {
        // value is not in this branch.
        // -> read from base revision
        if (getFromBase)
        {
          result = getValueFromBase(accessor, id, branchId, index);
        } // else: result remains null
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

    return result;
  }

  /**
   * Read a single value (at a given index) from the base revision
   *
   * @param accessor
   *          the DBStoreAccessor
   * @param id
   *          the ID of the revision
   * @param branchID
   *          the ID of the current (child) branch
   * @param index
   *          the index to read the value from
   * @return the value which is at index <code>index</code> in revision with ID <code>id</code> in the parent branch at
   *         the base of this branch (indicated by <code>branchID</code>).
   */
  private Object getValueFromBase(IDBStoreAccessor accessor, CDOID id, int branchID, int index)
  {
    IStoreChunkReader chunkReader = createBaseChunkReader(accessor, id, branchID);
    chunkReader.addSimpleChunk(index);
    List<Chunk> chunks = chunkReader.executeRead();
    return chunks.get(0).get(0);
  }

  private IStoreChunkReader createBaseChunkReader(IDBStoreAccessor accessor, CDOID id, int branchID)
  {
    InternalRepository repository = (InternalRepository)accessor.getStore().getRepository();

    CDOBranchManager branchManager = repository.getBranchManager();
    CDOBranch branch = branchManager.getBranch(branchID);
    CDOBranchPoint base = branch.getBase();
    if (base.getBranch() == null)
    {
      // Branch is main branch!
      throw new IllegalArgumentException("Base of main branch is null");
    }

    InternalCDORevisionManager revisionManager = repository.getRevisionManager();
    InternalCDORevision baseRevision = revisionManager.getRevision(id, base, 0, CDORevision.DEPTH_NONE, true);

    return accessor.createChunkReader(baseRevision, getFeature());
  }

  public final boolean queryXRefs(IDBStoreAccessor accessor, String mainTableName, String mainTableWhere,
      QueryXRefsContext context, String idString)
  {

    String tableName = getTable().getName();
    String listJoin = getMappingStrategy().getListJoin("a_t", "l_t");

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT l_t."); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append(", l_t."); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_VALUE);
    builder.append(", l_t."); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append(" AS l_t, ");//$NON-NLS-1$
    builder.append(mainTableName);
    builder.append(" AS a_t WHERE ");//$NON-NLS-1$
    builder.append("a_t." + mainTableWhere);//$NON-NLS-1$
    builder.append(listJoin);
    builder.append(" AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_VALUE);
    builder.append(" IN "); //$NON-NLS-1$
    builder.append(idString);
    String sql = builder.toString();

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    ResultSet resultSet = null;
    Statement stmt = null;

    try
    {
      stmt = accessor.getConnection().createStatement();
      if (TRACER.isEnabled())
      {
        TRACER.format("Query XRefs (list): {0}", sql);
      }

      resultSet = stmt.executeQuery(sql);
      while (resultSet.next())
      {
        CDOID sourceID = idHandler.getCDOID(resultSet, 1);
        CDOID targetID = idHandler.getCDOID(resultSet, 2);
        int idx = resultSet.getInt(3);

        boolean more = context.addXRef(targetID, sourceID, (EReference)getFeature(), idx);
        if (TRACER.isEnabled())
        {
          TRACER.format("  add XRef to context: src={0}, tgt={1}, idx={2}", sourceID, targetID, idx);
        }

        if (!more)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("  result limit reached. Ignoring further results.");
          }

          return false;
        }
      }

      return true;
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
}
