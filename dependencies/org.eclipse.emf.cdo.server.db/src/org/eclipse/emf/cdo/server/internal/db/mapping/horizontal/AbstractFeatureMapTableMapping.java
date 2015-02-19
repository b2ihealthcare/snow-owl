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
 *    Victor Roldan Betancort - Bug 283998: [DB] Chunk reading for multiple chunks fails
 *    Stefan Winkler - Bug 285426: [DB] Implement user-defined typeMapping support
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

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
 * This abstract base class provides basic behavior needed for mapping many-valued attributes to tables.
 * 
 * @author Eike Stepper
 * @since 3.0
 */
public abstract class AbstractFeatureMapTableMapping extends BasicAbstractListTableMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractFeatureMapTableMapping.class);

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

  // --------- SQL strings - see initSQLStrings() -----------------
  private String sqlSelectChunksPrefix;

  private String sqlOrderByIndex;

  protected String sqlInsert;

  private List<DBType> dbTypes;

  public AbstractFeatureMapTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
    initDBTypes();
    initTable();
    initSQLStrings();
  }

  private void initDBTypes()
  {
    // TODO add annotation processing here ...
    ITypeMapping.Registry registry = getTypeMappingRegistry();
    dbTypes = new ArrayList<DBType>(registry.getDefaultFeatureMapDBTypes());
  }

  protected ITypeMapping.Registry getTypeMappingRegistry()
  {
    return ITypeMapping.Registry.INSTANCE;
  }

  private void initTable()
  {
    IDBStore store = getMappingStrategy().getStore();
    String tableName = getMappingStrategy().getTableName(getContainingClass(), getFeature());
    table = store.getDBSchema().addTable(tableName);

    // add fields for keys (cdo_id, version, feature_id)
    FieldInfo[] fields = getKeyFields();
    IDBField[] dbFields = new IDBField[fields.length];

    for (int i = 0; i < fields.length; i++)
    {
      dbFields[i] = table.addField(fields[i].getName(), fields[i].getDbType());
    }

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

    table.addIndex(Type.NON_UNIQUE, dbFields);
    table.addIndex(Type.NON_UNIQUE, idxField);
    table.addIndex(Type.NON_UNIQUE, tagField);
  }

  protected abstract FieldInfo[] getKeyFields();

  protected abstract void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException;

  public Collection<IDBTable> getDBTables()
  {
    return Arrays.asList(table);
  }

  private void initSQLStrings()
  {
    String tableName = getTable().getName();
    FieldInfo[] fields = getKeyFields();

    // ---------------- SELECT to read chunks ----------------------------
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");

    builder.append(CDODBSchema.FEATUREMAP_TAG);
    builder.append(", ");

    Iterator<String> iter = columnNames.iterator();
    while (iter.hasNext())
    {
      builder.append(iter.next());
      if (iter.hasNext())
      {
        builder.append(", ");
      }
    }

    builder.append(" FROM ");
    builder.append(tableName);
    builder.append(" WHERE ");

    for (int i = 0; i < fields.length; i++)
    {
      builder.append(fields[i].getName());
      if (i + 1 < fields.length)
      {
        // more to come
        builder.append("=? AND ");
      }
      else
      {
        // last one
        builder.append("=? ");
      }
    }

    sqlSelectChunksPrefix = builder.toString();

    sqlOrderByIndex = " ORDER BY " + CDODBSchema.FEATUREMAP_IDX; //$NON-NLS-1$

    // INSERT with dynamic field name
    // TODO: Better: universal INSERT-Statement, because of stmt caching!

    // ----------------- INSERT - prefix -----------------
    builder = new StringBuilder("INSERT INTO ");
    builder.append(tableName);
    builder.append(" ("); //$NON-NLS-1$
    for (int i = 0; i < fields.length; i++)
    {
      builder.append(fields[i].getName());
      builder.append(", "); //$NON-NLS-1$
    }

    for (int i = 0; i < columnNames.size(); i++)
    {
      builder.append(columnNames.get(i));
      builder.append(", "); //$NON-NLS-1$
    }

    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_TAG);
    builder.append(") VALUES ("); //$NON-NLS-1$
    for (int i = 0; i < fields.length + columnNames.size(); i++)
    {
      builder.append("?, ");
    }

    builder.append("?, ?)");
    sqlInsert = builder.toString();
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
    if (listChunk == 0 || list.size() == 0)
    {
      // nothing to read take shortcut
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values for feature {0}.{1} of {2}v{3}", getContainingClass().getName(), getFeature()
          .getName(), revision.getID(), revision.getVersion());
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    try
    {
      String sql = sqlSelectChunksPrefix + sqlOrderByIndex;
      stmt = statementCache.getPreparedStatement(sql, ReuseProbability.HIGH);
      setKeyFields(stmt, revision);

      if (listChunk != CDORevision.UNCHUNKED)
      {
        stmt.setMaxRows(listChunk); // optimization - don't read unneeded rows.
      }

      resultSet = stmt.executeQuery();
      int currentIndex = 0;

      while ((listChunk == CDORevision.UNCHUNKED || --listChunk >= 0) && resultSet.next())
      {
        CDOID tag = idHandler.getCDOID(resultSet, 1);
        Object value = getTypeMapping(tag).readValue(resultSet);

        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for index {0} from result set: {1}", list.size(), value);
        }

        list.set(currentIndex++, CDORevisionUtil.createFeatureMapEntry(getFeatureByTag(tag), value));
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

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values done for feature {0}.{1} of {2}v{3}", getContainingClass().getName(),
          getFeature().getName(), revision.getID(), revision.getVersion());
    }
  }

  private void addFeature(CDOID tag)
  {
    EStructuralFeature modelFeature = getFeatureByTag(tag);

    ITypeMapping typeMapping = getMappingStrategy().createValueMapping(modelFeature);
    String column = CDODBSchema.FEATUREMAP_VALUE + "_" + typeMapping.getDBType();

    tagMap.put(tag, column);
    typeMapping.setDBField(table, column);
    typeMappings.put(tag, typeMapping);
  }

  public final void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list chunk values for feature {0}.{1} of {2}v{3}", getContainingClass().getName(),
          getFeature().getName(), chunkReader.getRevision().getID(), chunkReader.getRevision().getVersion());
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = chunkReader.getAccessor().getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

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
      setKeyFields(stmt, chunkReader.getRevision());

      resultSet = stmt.executeQuery();

      Chunk chunk = null;
      int chunkSize = 0;
      int chunkIndex = 0;
      int indexInChunk = 0;

      while (resultSet.next())
      {
        CDOID tag = idHandler.getCDOID(resultSet, 1);
        Object value = getTypeMapping(tag).readValue(resultSet);

        if (chunk == null)
        {
          chunk = chunks.get(chunkIndex++);
          chunkSize = chunk.size();

          if (TRACER.isEnabled())
          {
            TRACER.format("Current chunk no. {0} is [start = {1}, size = {2}]", chunkIndex - 1, chunk.getStartIndex(),
                chunkSize);
          }
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for chunk index {0} from result set: {1}", indexInChunk, value);
        }

        chunk.add(indexInChunk++, CDORevisionUtil.createFeatureMapEntry(getFeatureByTag(tag), value));
        if (indexInChunk == chunkSize)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Chunk finished");
          }

          chunk = null;
          indexInChunk = 0;
        }
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Reading list chunk values done for feature {0}.{1} of {2}", getContainingClass().getName(),
            getFeature(), chunkReader.getRevision());
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
  }

  public void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    CDOList values = revision.getList(getFeature());

    int idx = 0;
    for (Object element : values)
    {
      writeValue(accessor, revision, idx++, element);
    }
  }

  protected final void writeValue(IDBStoreAccessor accessor, CDORevision revision, int idx, Object value)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Writing value for feature {0}.{1} index {2} of {3} : {4}", getContainingClass().getName(), getFeature(), idx, revision, value); //$NON-NLS-1$
    }

    try
    {
      FeatureMap.Entry entry = (FeatureMap.Entry)value;
      EStructuralFeature entryFeature = entry.getEStructuralFeature();
      CDOID tag = getTagByFeature(entryFeature, revision.getTimeStamp());
      String columnName = getColumnName(tag);

      stmt = statementCache.getPreparedStatement(sqlInsert, ReuseProbability.HIGH);
      setKeyFields(stmt, revision);
      int column = getKeyFields().length + 1;

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

      stmt.setInt(column++, idx);
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
   * Get column name (lazy)
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
   * Get type mapping (lazy)
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
  protected CDOID getTagByFeature(EStructuralFeature feature, long timeStamp)
  {
    return getMappingStrategy().getStore().getMetaDataManager().getMetaID(feature, timeStamp);
  }

  /**
   * Used by subclasses to indicate which fields should be in the table. I.e. just a pair of name and DBType ...
   * 
   * @author Stefan Winkler
   */
  protected static class FieldInfo
  {
    private String name;

    private DBType dbType;

    public FieldInfo(String name, DBType dbType)
    {
      this.name = name;
      this.dbType = dbType;
    }

    public String getName()
    {
      return name;
    }

    public DBType getDbType()
    {
      return dbType;
    }
  }

  public final boolean queryXRefs(IDBStoreAccessor accessor, String mainTableName, String mainTableWhere,
      QueryXRefsContext context, String idString)
  {
    /*
     * must never be called (a feature map is not associated with an EReference feature, so XRefs are nor supported
     * here)
     */
    throw new ImplementationError("Should never be called!");
  }

}
