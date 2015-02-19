/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 271444: [DB] Multiple refactorings
 *    Stefan Winkler - Bug 283998: [DB] Chunk reading for multiple chunks fails
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
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
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This abstract base class provides basic behavior needed for mapping many-valued attributes to tables.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractListTableMapping extends BasicAbstractListTableMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractListTableMapping.class);

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

  public AbstractListTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
    initTable();
    initSQLStrings();
  }

  private void initTable()
  {
    IMappingStrategy mappingStrategy = getMappingStrategy();
    String tableName = mappingStrategy.getTableName(getContainingClass(), getFeature());
    table = mappingStrategy.getStore().getDBSchema().addTable(tableName);

    // add fields for keys (cdo_id, version, feature_id)
    FieldInfo[] fields = getKeyFields();
    IDBField[] dbFields = new IDBField[fields.length + 1];

    for (int i = 0; i < fields.length; i++)
    {
      dbFields[i] = table.addField(fields[i].getName(), fields[i].getDbType());
    }

    // add field for list index
    dbFields[dbFields.length - 1] = table.addField(CDODBSchema.LIST_IDX, DBType.INTEGER);

    // add field for value
    typeMapping = mappingStrategy.createValueMapping(getFeature());
    typeMapping.createDBField(table, CDODBSchema.LIST_VALUE);

    // add table indexes
    table.addIndex(Type.UNIQUE, dbFields);
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
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_VALUE);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append(" WHERE "); //$NON-NLS-1$

    for (int i = 0; i < fields.length; i++)
    {
      builder.append(fields[i].getName());
      if (i + 1 < fields.length)
      {
        // more to come
        builder.append("=? AND "); //$NON-NLS-1$
      }
      else
      {
        // last one
        builder.append("=? "); //$NON-NLS-1$
      }
    }

    sqlSelectChunksPrefix = builder.toString();

    sqlOrderByIndex = " ORDER BY " + CDODBSchema.LIST_IDX; //$NON-NLS-1$

    // ----------------- INSERT - reference entry -----------------
    builder = new StringBuilder("INSERT INTO "); //$NON-NLS-1$
    builder.append(tableName);
    builder.append("("); //$NON-NLS-1$

    for (int i = 0; i < fields.length; i++)
    {
      builder.append(fields[i].getName());
      builder.append(", "); //$NON-NLS-1$
    }

    builder.append(CDODBSchema.LIST_IDX);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_VALUE);
    builder.append(") VALUES ("); //$NON-NLS-1$
    for (int i = 0; i < fields.length; i++)
    {
      builder.append("?, "); //$NON-NLS-1$
    }

    builder.append(" ?, ?)"); //$NON-NLS-1$
    sqlInsertEntry = builder.toString();
  }

  protected final IDBTable getTable()
  {
    return table;
  }

  protected final ITypeMapping getTypeMapping()
  {
    return typeMapping;
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
      TRACER.format("Reading list values for feature {0}.{1} of {2}v{3}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), revision.getID(), revision.getVersion());
    }

    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet resultSet = null;

    try
    {
      String sql = sqlSelectChunksPrefix + sqlOrderByIndex;
      stmt = statementCache.getPreparedStatement(sql, ReuseProbability.HIGH);
      setKeyFields(stmt, revision);

      if (TRACER.isEnabled())
      {
        TRACER.trace(stmt.toString());
      }

      if (listChunk != CDORevision.UNCHUNKED)
      {
        stmt.setMaxRows(listChunk); // optimization - don't read unneeded rows.
      }

      resultSet = stmt.executeQuery();

      int currentIndex = 0;
      while ((listChunk == CDORevision.UNCHUNKED || --listChunk >= 0) && resultSet.next())
      {
        Object value = typeMapping.readValue(resultSet);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for index {0} from result set: {1}", list.size(), value); //$NON-NLS-1$
        }

        list.set(currentIndex++, value);
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
      TRACER.format("Reading list values done for feature {0}.{1} of {2}v{3}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), revision.getID(), revision.getVersion());
    }
  }

  public final void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list chunk values for feature {0}.{1} of {2}v{3}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), chunkReader.getRevision().getID(), chunkReader.getRevision().getVersion());
    }

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
        Object value = typeMapping.readValue(resultSet);

        if (chunk == null)
        {
          chunk = chunks.get(chunkIndex++);
          chunkSize = chunk.size();

          if (TRACER.isEnabled())
          {
            TRACER.format("Current chunk no. {0} is [start = {1}, size = {2}]", chunkIndex - 1, chunk.getStartIndex(), //$NON-NLS-1$
                chunkSize);
          }
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for chunk index {0} from result set: {1}", indexInChunk, value); //$NON-NLS-1$
        }

        chunk.add(indexInChunk++, value);
        if (indexInChunk == chunkSize)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Chunk finished"); //$NON-NLS-1$
          }

          chunk = null;
          indexInChunk = 0;
        }
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Reading list chunk values done for feature {0}.{1} of {2}v{3}", getContainingClass().getName(), //$NON-NLS-1$
            getFeature().getName(), chunkReader.getRevision().getID(), chunkReader.getRevision().getVersion());
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
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing value for feature {0}.{1} index {2} of {3}v{4} : {5}", getContainingClass().getName(),
          getFeature().getName(), idx, revision.getID(), revision.getVersion(), value);
    }

    try
    {
      stmt = statementCache.getPreparedStatement(sqlInsertEntry, ReuseProbability.HIGH);

      setKeyFields(stmt, revision);
      int column = getKeyFields().length + 1;
      stmt.setInt(column++, idx);
      typeMapping.setValue(stmt, column++, value);

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

  public boolean queryXRefs(IDBStoreAccessor accessor, String mainTableName, String mainTableWhere,
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
        CDOID srcId = idHandler.getCDOID(resultSet, 1);
        CDOID targetId = idHandler.getCDOID(resultSet, 2);
        int idx = resultSet.getInt(3);

        boolean more = context.addXRef(targetId, srcId, (EReference)getFeature(), idx);
        if (TRACER.isEnabled())
        {
          TRACER.format("  add XRef to context: src={0}, tgt={1}, idx={2}", srcId, targetId, idx);
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
}
