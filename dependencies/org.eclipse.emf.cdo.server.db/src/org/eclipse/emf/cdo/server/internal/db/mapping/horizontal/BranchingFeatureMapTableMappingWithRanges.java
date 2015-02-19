/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation taken from AuditFeatureMapTableMappingWithRanges
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
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

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex.Type;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is a featuremap-table mapping for audit mode. It is optimized for frequent insert operations at the list's end,
 * which causes just 1 DB row to be changed. This is achieved by introducing a version range (columns
 * {@link CDODBSchema#LIST_REVISION_VERSION_ADDED cdo_version_added} and
 * {@link CDODBSchema#LIST_REVISION_VERSION_REMOVED cdo_version_removed}) which records for which revisions a particular
 * entry existed. Also, this mapping is mainly optimized for potentially very large lists: the need for having the
 * complete list stored in memory to do in-the-middle-moved and inserts is traded in for a few more DB access
 * operations.
 *
 * @author Eike Stepper
 * @author Stefan Winkler
 * @author Lothar Werzinger
 * @since 3.0
 */
public class BranchingFeatureMapTableMappingWithRanges extends BasicAbstractListTableMapping implements
    IListMappingDeltaSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG,
      BranchingFeatureMapTableMappingWithRanges.class);

  /**
   * Used to clean up lists for detached objects.
   */
  private static final int FINAL_VERSION = Integer.MAX_VALUE;

  /**
   * The table of this mapping.
   */
  private IDBTable table;

  /**
   * The tags mapped to column names
   */
  private HashMap<CDOID, String> tagMap;

  /**
   * Column name Set
   */
  private List<String> columnNames;

  /**
   * The type mappings for the value fields.
   */
  private Map<CDOID, ITypeMapping> typeMappings;

  private List<DBType> dbTypes;

  // --------- SQL strings - see initSQLStrings() -----------------
  private String sqlSelectChunksPrefix;

  private String sqlOrderByIndex;

  private String sqlInsert;

  private String sqlRemoveEntry;

  private String sqlDeleteEntry;

  private String sqlUpdateIndex;

  private String sqlGetValue;

  private String sqlClearList;

  public BranchingFeatureMapTableMappingWithRanges(IMappingStrategy mappingStrategy, EClass eClass,
      EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
    initDBTypes();
    initTable();
    initSQLStrings();
  }

  private void initDBTypes()
  {
    // TODO add annotation processing here ...
    ITypeMapping.Registry registry = ITypeMapping.Registry.INSTANCE;
    dbTypes = new ArrayList<DBType>(registry.getDefaultFeatureMapDBTypes());
  }

  private void initTable()
  {
    IDBStore store = getMappingStrategy().getStore();
    String tableName = getMappingStrategy().getTableName(getContainingClass(), getFeature());
    table = store.getDBSchema().addTable(tableName);

    // add fields for CDOID
    IDBField idField = table.addField(CDODBSchema.FEATUREMAP_REVISION_ID, store.getIDHandler().getDBType());

    IDBField branchField = table.addField(CDODBSchema.LIST_REVISION_BRANCH, DBType.INTEGER);

    // add fields for version range
    IDBField versionAddedField = table.addField(CDODBSchema.FEATUREMAP_VERSION_ADDED, DBType.INTEGER);
    IDBField versionRemovedField = table.addField(CDODBSchema.FEATUREMAP_VERSION_REMOVED, DBType.INTEGER);

    // add field for list index
    IDBField idxField = table.addField(CDODBSchema.FEATUREMAP_IDX, DBType.INTEGER);

    // add field for FeatureMap tag (MetaID for Feature in CDO registry)
    IDBField tagField = table.addField(CDODBSchema.FEATUREMAP_TAG, store.getIDHandler().getDBType());

    tagMap = new HashMap<CDOID, String>();
    typeMappings = new HashMap<CDOID, ITypeMapping>();
    columnNames = new ArrayList<String>();

    // create columns for all DBTypes
    for (DBType type : getDBTypes())
    {
      String column = CDODBSchema.FEATUREMAP_VALUE + "_" + type.name();
      table.addField(column, type);
      columnNames.add(column);
    }

    table.addIndex(Type.NON_UNIQUE, idField);
    table.addIndex(Type.NON_UNIQUE, branchField);
    table.addIndex(Type.NON_UNIQUE, versionAddedField);
    table.addIndex(Type.NON_UNIQUE, versionRemovedField);
    table.addIndex(Type.NON_UNIQUE, idxField);
    table.addIndex(Type.NON_UNIQUE, tagField);
  }

  public Collection<IDBTable> getDBTables()
  {
    return Arrays.asList(table);
  }

  private void initSQLStrings()
  {
    String tableName = getTable().getName();

    // ---------------- SELECT to read chunks ----------------------------
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$

    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_TAG);
    builder.append(", "); //$NON-NLS-1$

    Iterator<String> iter = columnNames.iterator();
    while (iter.hasNext())
    {
      builder.append(iter.next());
      if (iter.hasNext())
      {
        builder.append(", "); //$NON-NLS-1$
      }
    }

    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_ADDED);
    builder.append("<=? AND ("); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_REMOVED);
    builder.append(" IS NULL OR "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_REMOVED);
    builder.append(">?)"); //$NON-NLS-1$
    sqlSelectChunksPrefix = builder.toString();

    sqlOrderByIndex = " ORDER BY " + CDODBSchema.FEATUREMAP_IDX; //$NON-NLS-1$

    // ----------------- INSERT - prefix -----------------
    builder = new StringBuilder("INSERT INTO "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append("("); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_BRANCH);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_ADDED);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_REMOVED);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_TAG);

    for (int i = 0; i < columnNames.size(); i++)
    {
      builder.append(", "); //$NON-NLS-1$
      builder.append(columnNames.get(i));
    }

    builder.append(") VALUES (?, ?, ?, ?, ?, ?"); //$NON-NLS-1$
    for (int i = 0; i < columnNames.size(); i++)
    {
      builder.append(", ?"); //$NON-NLS-1$
    }

    builder.append(")"); //$NON-NLS-1$
    sqlInsert = builder.toString();

    // ----------------- remove current entry -----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_REMOVED);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_REMOVED);
    builder.append(" IS NULL"); //$NON-NLS-1$
    sqlRemoveEntry = builder.toString();

    // ----------------- delete temporary entry -----------------
    builder = new StringBuilder("DELETE FROM "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_ADDED);
    builder.append("=?"); //$NON-NLS-1$
    sqlDeleteEntry = builder.toString();

    // ----------------- update index -----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("=? WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_ADDED);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("=?"); //$NON-NLS-1$
    sqlUpdateIndex = builder.toString();

    // ----------------- get current value -----------------
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_TAG);
    builder.append(", "); //$NON-NLS-1$

    iter = columnNames.iterator();
    while (iter.hasNext())
    {
      builder.append(iter.next());
      if (iter.hasNext())
      {
        builder.append(", "); //$NON-NLS-1$
      }
    }

    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_REMOVED);
    builder.append(" IS NULL"); //$NON-NLS-1$
    sqlGetValue = builder.toString();

    // ----------- clear list items -------------------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_REMOVED);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_VERSION_REMOVED);
    builder.append(" IS NULL"); //$NON-NLS-1$
    sqlClearList = builder.toString();
  }

  protected List<DBType> getDBTypes()
  {
    return dbTypes;
  }

  protected final IDBTable getTable()
  {
    return table;
  }

  protected final List<String> getColumnNames()
  {
    return columnNames;
  }

  protected final Map<CDOID, ITypeMapping> getTypeMappings()
  {
    return typeMappings;
  }

  protected final Map<CDOID, String> getTagMap()
  {
    return tagMap;
  }

  public void readValues(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
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

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values for feature {0}.{1} of {2}", getContainingClass().getName(), getFeature() //$NON-NLS-1$
          .getName(), revision);
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    IStoreChunkReader baseReader = null;

    try
    {
      String sql = sqlSelectChunksPrefix + sqlOrderByIndex;

      CDOID id = revision.getID();
      int branchID = revision.getBranch().getID();

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

        CDOID tag = idHandler.getCDOID(resultSet, 2);
        Object value = getTypeMapping(tag).readValue(resultSet);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for index {0} from result set: {1}", currentIndex, value); //$NON-NLS-1$
        }

        list.set(currentIndex++, CDORevisionUtil.createFeatureMapEntry(getFeatureByTag(tag), value));
        valuesToRead--;
      }

      if (valuesToRead > 0)
      {
        if (baseReader == null)
        {
          baseReader = createBaseChunkReader(accessor, id, branchID);
        }

        baseReader.addRangedChunk(currentIndex, currentIndex + valuesToRead);
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
        TRACER.format("Reading base revision chunks for featureMap {0}.{1} of {2} from base revision {3}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), revision, baseReader.getRevision());
      }

      List<Chunk> baseChunks = baseReader.executeRead();
      for (Chunk chunk : baseChunks)
      {
        int startIndex = chunk.getStartIndex();
        for (int i = 0; i < chunk.size(); i++)
        {
          list.set(startIndex + i, chunk.get(i));
        }
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values done for feature {0}.{1} of {2}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), revision);
    }
  }

  private void addFeature(CDOID tag)
  {
    EStructuralFeature modelFeature = getFeatureByTag(tag);

    ITypeMapping typeMapping = getMappingStrategy().createValueMapping(modelFeature);
    String column = CDODBSchema.FEATUREMAP_VALUE + "_" + typeMapping.getDBType(); //$NON-NLS-1$

    tagMap.put(tag, column);
    typeMapping.setDBField(table, column);
    typeMappings.put(tag, typeMapping);
  }

  public final void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    CDORevision revision = chunkReader.getRevision();
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
      idHandler.setCDOID(stmt, 1, revision.getID());
      stmt.setInt(2, revision.getBranch().getID());
      stmt.setInt(3, revision.getVersion());
      stmt.setInt(4, revision.getVersion());

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
                baseReader = createBaseChunkReader(chunkReader.getAccessor(), chunkReader.getRevision().getID(),
                    chunkReader.getRevision().getBranch().getID());
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
            CDOID tag = idHandler.getCDOID(resultSet, 2);
            Object value = getTypeMapping(tag).readValue(resultSet);
            if (TRACER.isEnabled())
            {
              TRACER.format("ChunkReader read value for index {0} from result set: {1}", nextDBIndex, value); //$NON-NLS-1$
            }

            chunk.add(i, CDORevisionUtil.createFeatureMapEntry(getFeatureByTag(tag), value));

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
            baseReader = createBaseChunkReader(chunkReader.getAccessor(), chunkReader.getRevision().getID(),
                chunkReader.getRevision().getBranch().getID());
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

        // baseChunk now corresponds to this chunk, but startIndex of baseChunk may be higher.
        // therefore calculate offset
        int offset = thisChunk.getStartIndex() - baseStartIndex;

        // and copy values.
        for (int i = 0; i < baseChunk.size(); i++)
        {
          thisChunk.add(i + offset, baseChunk.get(i));
        }
      } // finally, continue with the next baseChunk

    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list chunk values done for feature {0}.{1} of {2}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature(), revision);
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

  protected final void writeValue(IDBStoreAccessor accessor, CDORevision revision, int idx, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Writing value for feature {0}.{1} index {2} of {3} : {4}", getContainingClass().getName(), getFeature(), idx, revision, value); //$NON-NLS-1$
    }

    addEntry(accessor, revision.getID(), revision.getBranch().getID(), revision.getVersion(), idx, value,
        revision.getTimeStamp());
  }

  /**
   * Get column name (lazy).
   *
   * @param tag
   *          The feature's MetaID in CDO
   * @return the column name where the values are stored
   */
  protected String getColumnName(CDOID tag)
  {
    String column = tagMap.get(tag);
    if (column == null)
    {
      addFeature(tag);
      column = tagMap.get(tag);
    }

    return column;
  }

  /**
   * Get type mapping (lazy).
   *
   * @param tag
   *          The feature's MetaID in CDO
   * @return the corresponding type mapping
   */
  protected ITypeMapping getTypeMapping(CDOID tag)
  {
    ITypeMapping typeMapping = typeMappings.get(tag);
    if (typeMapping == null)
    {
      addFeature(tag);
      typeMapping = typeMappings.get(tag);
    }

    return typeMapping;
  }

  /**
   * @param metaID
   * @return the column name where the values are stored
   */
  private EStructuralFeature getFeatureByTag(CDOID tag)
  {
    return (EStructuralFeature)getMappingStrategy().getStore().getMetaDataManager().getMetaInstance(tag);
  }

  /**
   * @param feature
   *          The EStructuralFeature
   * @return The feature's MetaID in CDO
   */
  protected CDOID getTagByFeature(EStructuralFeature feature, long created)
  {
    return getMappingStrategy().getStore().getMetaDataManager().getMetaID(feature, created);
  }

  /**
   * Clear a list of a given revision.
   *
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision from which to remove all items
   */
  public void clearList(IDBStoreAccessor accessor, CDOID id, int branchId, int oldVersion, int newVersion,
      int lastIndex, long timestamp)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmtDeleteTemp = null;
    PreparedStatement stmtClear = null;

    try
    {
      // check for each index if the value exists in the current branch
      for (int i = 0; i <= lastIndex; i++)
      {
        if (getValue(accessor, id, branchId, i, false) == null)
        {
          // if not, add a historic entry for missing ones.
          addHistoricEntry(accessor, id, branchId, 0, newVersion, i, getValueFromBase(accessor, id, branchId, i),
              timestamp);
        }
      }

      // clear rest of the list
      stmtClear = statementCache.getPreparedStatement(sqlClearList, ReuseProbability.HIGH);
      stmtClear.setInt(1, newVersion);
      idHandler.setCDOID(stmtClear, 2, id);
      stmtClear.setInt(3, branchId);

      int result = DBUtil.update(stmtClear, false);
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
      statementCache.releasePreparedStatement(stmtDeleteTemp);
      statementCache.releasePreparedStatement(stmtClear);
    }
  }

  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    InternalCDORevision revision = (InternalCDORevision)accessor.getTransaction().getRevision(id);
    int branchId = accessor.getTransaction().getBranch().getID();

    if (TRACER.isEnabled())
    {
      TRACER.format("objectDetached {1}", revision); //$NON-NLS-1$
    }

    clearList(accessor, id, branchId, revision.getVersion(), FINAL_VERSION, revision.getList(getFeature()).size() - 1,
        revised);
  }

  @Override
  public void rawDeleted(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version)
  {
    throw new UnsupportedOperationException("Raw deletion does not work in range-based mappings");
  }

  public void processDelta(final IDBStoreAccessor accessor, final CDOID id, final int branchId, int oldVersion,
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
    ListDeltaVisitor visitor = new ListDeltaVisitor(accessor, originalRevision, branchId, oldVersion, newVersion,
        created);

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

  private class ListDeltaVisitor implements CDOFeatureDeltaVisitor
  {
    private IDBStoreAccessor accessor;

    private InternalCDORevision originalRevision;

    private CDOID id;

    private int branchID;

    private int oldVersion;

    private int newVersion;

    private int lastIndex;

    private long timestamp;

    public ListDeltaVisitor(IDBStoreAccessor accessor, InternalCDORevision originalRevision, int targetBranchID,
        int oldVersion, int newVersion, long timestamp)
    {
      this.accessor = accessor;
      this.originalRevision = originalRevision;
      id = this.originalRevision.getID();
      branchID = targetBranchID;
      this.oldVersion = oldVersion;
      this.newVersion = newVersion;
      lastIndex = originalRevision.getList(getFeature()).size() - 1;
      this.timestamp = timestamp;
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
      removeEntry(accessor, id, branchID, oldVersion, newVersion, fromIdx, timestamp);

      // adjust indexes and shift either up or down
      if (fromIdx < toIdx)
      {
        moveOneUp(accessor, id, branchID, oldVersion, newVersion, fromIdx + 1, toIdx);
      }
      else
      { // fromIdx > toIdx here
        moveOneDown(accessor, id, branchID, oldVersion, newVersion, toIdx, fromIdx - 1);
      }

      // create the item
      addEntry(accessor, id, branchID, newVersion, toIdx, value, timestamp);
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
      addEntry(accessor, id, branchID, newVersion, startIndex, delta.getValue(), timestamp);

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
      removeEntry(accessor, id, branchID, oldVersion, newVersion, startIndex, timestamp);

      // make room for the new item
      moveOneUp(accessor, id, branchID, oldVersion, newVersion, startIndex + 1, endIndex);

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
      removeEntry(accessor, id, branchID, oldVersion, newVersion, index, timestamp);

      // create the item
      addEntry(accessor, id, branchID, newVersion, index, delta.getValue(), timestamp);
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

      clearList(accessor, id, branchID, oldVersion, newVersion, lastIndex, timestamp);
      lastIndex = -1;
    }

    public void visit(CDOListFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDOClearFeatureDelta delta)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Delta Clearing"); //$NON-NLS-1$
      }

      clearList(accessor, id, branchID, oldVersion, newVersion, lastIndex, timestamp);
      lastIndex = -1;
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
          idHandler.setCDOID(stmt, startIndex++, id);
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
          case 0:
            Object value = getValue(accessor, id, branchId, index, false);

            if (value != null)
            {
              if (TRACER.isEnabled())
              {
                TRACER.format("moveOneUp remove: {0}", index); //$NON-NLS-1$
              }

              removeEntry(accessor, id, branchId, oldVersion, newVersion, index, timestamp);
            }
            else
            {
              value = getValueFromBase(accessor, id, branchId, index);
              {
                TRACER.format("moveOneUp add historic entry at: {0}", index); //$NON-NLS-1$
              }

              addHistoricEntry(accessor, id, branchId, 0, newVersion, index, value, timestamp);
            }

            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneUp add: {0}", index - 1); //$NON-NLS-1$
            }

            addEntry(accessor, id, branchId, newVersion, index - 1, value, timestamp);
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

              removeEntry(accessor, id, branchId, oldVersion, newVersion, index, timestamp);
            }
            else
            {
              value = getValueFromBase(accessor, id, branchId, index);
              {
                TRACER.format("moveOneDown add historic entry at: {0}", index); //$NON-NLS-1$
              }

              addHistoricEntry(accessor, id, branchId, 0, newVersion, index, value, timestamp);
            }

            addEntry(accessor, id, branchId, newVersion, index + 1, value, timestamp);
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

  private void addEntry(IDBStoreAccessor accessor, CDOID id, int branchId, int version, int index, Object value,
      long timestamp)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    if (TRACER.isEnabled())
    {
      TRACER.format("Adding value for feature() {0}.{1} index {2} of {3}v{4} : {5}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, id, version, value);
    }

    try
    {
      FeatureMap.Entry entry = (FeatureMap.Entry)value;
      EStructuralFeature entryFeature = entry.getEStructuralFeature();
      CDOID tag = getTagByFeature(entryFeature, timestamp);
      String columnName = getColumnName(tag);

      stmt = statementCache.getPreparedStatement(sqlInsert, ReuseProbability.HIGH);

      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, branchId);
      stmt.setInt(column++, version);
      stmt.setNull(column++, DBType.INTEGER.getCode()); // versionRemoved
      stmt.setInt(column++, index);
      idHandler.setCDOID(stmt, column++, tag);

      for (int i = 0; i < columnNames.size(); i++)
      {
        if (columnNames.get(i).equals(columnName))
        {
          getTypeMapping(tag).setValue(stmt, column++, entry.getValue());
        }
        else
        {
          stmt.setNull(column++, getDBTypes().get(i).getCode());
        }
      }

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
      int versionRemoved, int index, Object value, long timestamp)
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
      FeatureMap.Entry entry = (FeatureMap.Entry)value;
      EStructuralFeature entryFeature = entry.getEStructuralFeature();
      CDOID tag = getTagByFeature(entryFeature, timestamp);
      String columnName = getColumnName(tag);

      stmt = statementCache.getPreparedStatement(sqlInsert, ReuseProbability.HIGH);

      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, branchId);
      stmt.setInt(column++, versionAdded);
      stmt.setNull(column++, versionRemoved);
      stmt.setInt(column++, index);
      idHandler.setCDOID(stmt, column++, tag);

      for (int i = 0; i < columnNames.size(); i++)
      {
        if (columnNames.get(i).equals(columnName))
        {
          getTypeMapping(tag).setValue(stmt, column++, entry.getValue());
        }
        else
        {
          stmt.setNull(column++, getDBTypes().get(i).getCode());
        }
      }

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

  private void removeEntry(IDBStoreAccessor accessor, CDOID id, int branchId, int oldVersion, int newVersion,
      int index, long timestamp)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    if (TRACER.isEnabled())
    {
      TRACER.format("Removing value for feature() {0}.{1} index {2} of {3}v{4}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, id, newVersion);
    }

    try
    {
      // try to delete a temporary entry first
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
          addHistoricEntry(accessor, id, branchId, 0, newVersion, index, value, timestamp);
        }
      }
    }
    catch (SQLException e)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removing value for feature() {0}.{1} index {2} of {3}v{4} FAILED {5}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), index, id, newVersion, e.getMessage());
      }

      throw new DBException(e);
    }
    catch (IllegalStateException e)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removing value for feature() {0}.{1} index {2} of {3}v{4} FAILED {5}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), index, id, newVersion, e.getMessage());
      }

      throw new DBException(e);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  private FeatureMap.Entry getValue(IDBStoreAccessor accessor, CDOID id, int branchId, int index, boolean getFromBase)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    FeatureMap.Entry result = null;

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
        CDOID tag = idHandler.getCDOID(resultSet, 1);
        Object value = getTypeMapping(tag).readValue(resultSet);
        result = CDORevisionUtil.createFeatureMapEntry(getFeatureByTag(tag), value);
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
      if (TRACER.isEnabled())
      {
        TRACER.format("Read value (index {0}) from result set: {1}", index, result); //$NON-NLS-1$
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
  private FeatureMap.Entry getValueFromBase(IDBStoreAccessor accessor, CDOID id, int branchID, int index)
  {
    IStoreChunkReader chunkReader = createBaseChunkReader(accessor, id, branchID);
    chunkReader.addSimpleChunk(index);
    List<Chunk> chunks = chunkReader.executeRead();
    return (FeatureMap.Entry)chunks.get(0).get(0);
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
    // must never be called (a feature map is not associated with an EReference feature, so XRefs are nor supported
    // here)
    throw new ImplementationError("Should never be called!");
  }
}
