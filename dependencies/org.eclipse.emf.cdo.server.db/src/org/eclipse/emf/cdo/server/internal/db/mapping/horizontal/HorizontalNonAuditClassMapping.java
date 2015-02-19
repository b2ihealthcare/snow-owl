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
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalNonAuditClassMapping extends AbstractHorizontalClassMapping implements IClassMappingDeltaSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalNonAuditClassMapping.class);

  private String sqlSelectAllObjectIDs;

  private String sqlSelectCurrentAttributes;

  private String sqlInsertAttributes;

  private String sqlUpdateAffix;

  private String sqlUpdatePrefix;

  private String sqlUpdateContainerPart;

  private ThreadLocal<FeatureDeltaWriter> deltaWriter = new ThreadLocal<FeatureDeltaWriter>()
  {
    @Override
    protected FeatureDeltaWriter initialValue()
    {
      return new FeatureDeltaWriter();
    }
  };

  public HorizontalNonAuditClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass);

    initSQLStrings();
  }

  private void initSQLStrings()
  {
    Map<EStructuralFeature, String> unsettableFields = getUnsettableFields();
    Map<EStructuralFeature, String> listSizeFields = getListSizeFields();

    // ----------- Select Revision ---------------------------
    StringBuilder builder = new StringBuilder();

    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_FEATURE);

    for (ITypeMapping singleMapping : getValueMappings())
    {
      builder.append(", "); //$NON-NLS-1$
      builder.append(singleMapping.getField());
    }

    if (unsettableFields != null)
    {
      for (String fieldName : unsettableFields.values())
      {
        builder.append(", "); //$NON-NLS-1$
        builder.append(fieldName);
      }
    }

    if (listSizeFields != null)
    {
      for (String fieldName : listSizeFields.values())
      {
        builder.append(", "); //$NON-NLS-1$
        builder.append(fieldName);
      }
    }

    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=?"); //$NON-NLS-1$

    sqlSelectCurrentAttributes = builder.toString();

    // ----------- Insert Attributes -------------------------
    builder = new StringBuilder();
    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(getTable());

    builder.append("("); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_FEATURE);

    for (ITypeMapping singleMapping : getValueMappings())
    {
      builder.append(", "); //$NON-NLS-1$
      builder.append(singleMapping.getField());
    }

    if (unsettableFields != null)
    {
      for (String fieldName : unsettableFields.values())
      {
        builder.append(", "); //$NON-NLS-1$
        builder.append(fieldName);
      }
    }

    if (listSizeFields != null)
    {
      for (String fieldName : listSizeFields.values())
      {
        builder.append(", "); //$NON-NLS-1$
        builder.append(fieldName);
      }
    }

    builder.append(") VALUES (?, ?, ?, ?, ?, ?, ?"); //$NON-NLS-1$
    for (int i = 0; i < getValueMappings().size(); i++)
    {
      builder.append(", ?"); //$NON-NLS-1$
    }

    if (unsettableFields != null)
    {
      for (int i = 0; i < unsettableFields.size(); i++)
      {
        builder.append(", ?"); //$NON-NLS-1$
      }
    }

    if (listSizeFields != null)
    {
      for (int i = 0; i < listSizeFields.size(); i++)
      {
        builder.append(", ?"); //$NON-NLS-1$
      }
    }

    builder.append(")"); //$NON-NLS-1$
    sqlInsertAttributes = builder.toString();

    // ----------- Select all unrevised Object IDs ------
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    sqlSelectAllObjectIDs = builder.toString();

    // ----------- Update attributes --------------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append("=? ,"); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append("=? "); //$NON-NLS-1$
    sqlUpdatePrefix = builder.toString();

    builder = new StringBuilder(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
    builder.append("=? ,"); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append("=? ,"); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_FEATURE);
    builder.append("=? "); //$NON-NLS-1$
    sqlUpdateContainerPart = builder.toString();

    builder = new StringBuilder(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=? "); //$NON-NLS-1$
    sqlUpdateAffix = builder.toString();
  }

  @Override
  protected void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      int column = 1;
      stmt = statementCache.getPreparedStatement(sqlInsertAttributes, ReuseProbability.HIGH);
      idHandler.setCDOID(stmt, column++, revision.getID());
      stmt.setInt(column++, revision.getVersion());
      stmt.setLong(column++, revision.getTimeStamp());
      stmt.setLong(column++, revision.getRevised());
      idHandler.setCDOID(stmt, column++, revision.getResourceID());
      idHandler.setCDOID(stmt, column++, (CDOID)revision.getContainerID());
      stmt.setInt(column++, revision.getContainingFeatureID());

      int isSetCol = column + getValueMappings().size();

      for (ITypeMapping mapping : getValueMappings())
      {
        EStructuralFeature feature = mapping.getFeature();
        if (feature.isUnsettable())
        {
          if (revision.getValue(feature) == null)
          {
            stmt.setBoolean(isSetCol++, false);

            // also set value column to default value
            mapping.setDefaultValue(stmt, column++);
            continue;
          }

          stmt.setBoolean(isSetCol++, true);
        }

        mapping.setValueFromRevision(stmt, column++, revision);
      }

      Map<EStructuralFeature, String> listSizeFields = getListSizeFields();
      if (listSizeFields != null)
      {
        // isSetCol now points to the first listTableSize-column
        column = isSetCol;

        for (EStructuralFeature feature : listSizeFields.keySet())
        {
          CDOList list = revision.getList(feature);
          stmt.setInt(column++, list.size());
        }
      }

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

  public PreparedStatement createObjectIDStatement(IDBStoreAccessor accessor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Created ObjectID Statement : {0}", sqlSelectAllObjectIDs); //$NON-NLS-1$
    }

    IPreparedStatementCache statementCache = accessor.getStatementCache();
    return statementCache.getPreparedStatement(sqlSelectAllObjectIDs, ReuseProbability.HIGH);
  }

  public PreparedStatement createResourceQueryStatement(IDBStoreAccessor accessor, CDOID folderId, String name,
      boolean exactMatch, CDOBranchPoint branchPoint)
  {
    long timeStamp = branchPoint.getTimeStamp();
    if (timeStamp != CDORevision.UNSPECIFIED_DATE)
    {
      throw new IllegalArgumentException("Non-audit store does not support explicit timeStamp in resource query"); //$NON-NLS-1$
    }

    EStructuralFeature nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

    ITypeMapping nameValueMapping = getValueMapping(nameFeature);
    if (nameValueMapping == null)
    {
      throw new ImplementationError(nameFeature + " not found in ClassMapping " + this); //$NON-NLS-1$
    }

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(">0 AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(nameValueMapping.getField());
    if (name == null)
    {
      builder.append(" IS NULL"); //$NON-NLS-1$
    }
    else
    {
      builder.append(exactMatch ? "=? " : " LIKE ? "); //$NON-NLS-1$ //$NON-NLS-2$
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      int column = 1;

      stmt = statementCache.getPreparedStatement(builder.toString(), ReuseProbability.MEDIUM);
      idHandler.setCDOID(stmt, column++, folderId);

      if (name != null)
      {
        String queryName = exactMatch ? name : name + "%"; //$NON-NLS-1$
        nameValueMapping.setValue(stmt, column++, queryName);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Created Resource Query: {0}", stmt.toString()); //$NON-NLS-1$
      }

      return stmt;
    }
    catch (SQLException ex)
    {
      statementCache.releasePreparedStatement(stmt); // only release on error
      throw new DBException(ex);
    }
  }

  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    long timeStamp = revision.getTimeStamp();
    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      throw new UnsupportedOperationException("Mapping strategy does not support audits"); //$NON-NLS-1$
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlSelectCurrentAttributes, ReuseProbability.HIGH);
      idHandler.setCDOID(stmt, 1, revision.getID());

      // Read singleval-attribute table always (even without modeled attributes!)
      boolean success = readValuesFromStatement(stmt, revision, accessor);

      // Read multival tables only if revision exists
      if (success)
      {
        readLists(accessor, revision, listChunk);
      }

      return success;
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

  @Override
  protected void detachAttributes(IDBStoreAccessor accessor, CDOID id, int version, CDOBranch branch, long timeStamp,
      OMMonitor mon)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlUpdatePrefix + sqlUpdateAffix, ReuseProbability.HIGH);
      stmt.setInt(1, -version);
      stmt.setLong(2, timeStamp);
      idHandler.setCDOID(stmt, 3, id);

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

  @Override
  protected void rawDeleteAttributes(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version, OMMonitor fork)
  {
    // Not called because CDOWorkspace uses an auditing local repo
  }

  public void writeRevisionDelta(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created,
      OMMonitor monitor)
  {
    Async async = null;
    monitor.begin();

    try
    {
      try
      {
        async = monitor.forkAsync();
        FeatureDeltaWriter writer = deltaWriter.get();
        writer.process(accessor, delta, created);
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * @author Eike Stepper
   */
  private class FeatureDeltaWriter implements CDOFeatureDeltaVisitor
  {
    private CDOID id;

    private int oldVersion;

    private long created;

    private IDBStoreAccessor accessor;

    private boolean updateContainer;

    private List<Pair<ITypeMapping, Object>> attributeChanges;

    private List<Pair<EStructuralFeature, Integer>> listSizeChanges;

    private int newContainingFeatureID;

    private CDOID newContainerID;

    private CDOID newResourceID;

    private int branchId;

    private int newVersion;

    /*
     * this is a temporary copy of the revision to track list size changes...
     */
    private InternalCDORevision tempRevision;

    public FeatureDeltaWriter()
    {
      attributeChanges = new ArrayList<Pair<ITypeMapping, Object>>();
      listSizeChanges = new ArrayList<Pair<EStructuralFeature, Integer>>();
    }

    protected void reset()
    {
      attributeChanges.clear();
      listSizeChanges.clear();
      updateContainer = false;
    }

    public void process(IDBStoreAccessor a, CDORevisionDelta d, long c)
    {
      // set context
      id = d.getID();

      branchId = d.getBranch().getID();
      oldVersion = d.getVersion();
      newVersion = oldVersion + 1;
      created = c;
      accessor = a;

      tempRevision = (InternalCDORevision)accessor.getTransaction().getRevision(id).copy();

      // process revision delta tree
      d.accept(this);

      updateAttributes();
      // clean up
      reset();
    }

    public void visit(CDOMoveFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDOSetFeatureDelta delta)
    {
      if (delta.getFeature().isMany())
      {
        throw new ImplementationError("Should not be called"); //$NON-NLS-1$
      }

      ITypeMapping am = getValueMapping(delta.getFeature());
      if (am == null)
      {
        throw new IllegalArgumentException("AttributeMapping for " + delta.getFeature() + " is null!"); //$NON-NLS-1$ //$NON-NLS-2$
      }

      attributeChanges.add(new Pair<ITypeMapping, Object>(am, delta.getValue()));
    }

    public void visit(CDOUnsetFeatureDelta delta)
    {
      // TODO: correct this when DBStore implements unsettable features
      // see Bugs 259868 and 263010
      ITypeMapping tm = getValueMapping(delta.getFeature());
      attributeChanges.add(new Pair<ITypeMapping, Object>(tm, null));
    }

    public void visit(CDOListFeatureDelta delta)
    {
      EStructuralFeature feature = delta.getFeature();

      IListMappingDeltaSupport listMapping = (IListMappingDeltaSupport)getListMapping(feature);
      listMapping.processDelta(accessor, id, branchId, oldVersion, oldVersion + 1, created, delta);

      int oldSize = tempRevision.getList(feature).size();
      delta.apply(tempRevision);
      int newSize = tempRevision.getList(feature).size();

      if (oldSize != newSize)
      {
        listSizeChanges.add(new Pair<EStructuralFeature, Integer>(feature, newSize));
      }
    }

    public void visit(CDOClearFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDOAddFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDORemoveFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDOContainerFeatureDelta delta)
    {
      newContainingFeatureID = delta.getContainerFeatureID();
      newContainerID = (CDOID)delta.getContainerID();
      newResourceID = delta.getResourceID();
      updateContainer = true;
    }

    private void updateAttributes()
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      IPreparedStatementCache statementCache = accessor.getStatementCache();
      PreparedStatement stmt = null;

      try
      {
        int column = 1;

        stmt = statementCache.getPreparedStatement(buildUpdateStatement(), ReuseProbability.MEDIUM);
        stmt.setInt(column++, newVersion);
        stmt.setLong(column++, created);
        if (updateContainer)
        {
          idHandler.setCDOID(stmt, column++, newResourceID, created);
          idHandler.setCDOID(stmt, column++, newContainerID, created);
          stmt.setInt(column++, newContainingFeatureID);
        }

        column = setUpdateAttributeValues(attributeChanges, stmt, column);
        column = setUpdateListSizeChanges(listSizeChanges, stmt, column);

        idHandler.setCDOID(stmt, column++, id);

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

    private String buildUpdateStatement()
    {
      StringBuilder builder = new StringBuilder(sqlUpdatePrefix);
      if (updateContainer)
      {
        builder.append(sqlUpdateContainerPart);
      }

      for (Pair<ITypeMapping, Object> change : attributeChanges)
      {
        builder.append(", "); //$NON-NLS-1$
        ITypeMapping typeMapping = change.getElement1();
        builder.append(typeMapping.getField());
        builder.append("=?"); //$NON-NLS-1$

        if (typeMapping.getFeature().isUnsettable())
        {
          builder.append(", "); //$NON-NLS-1$
          builder.append(getUnsettableFields().get(typeMapping.getFeature()));
          builder.append("=?"); //$NON-NLS-1$
        }
      }

      for (Pair<EStructuralFeature, Integer> change : listSizeChanges)
      {
        builder.append(", "); //$NON-NLS-1$
        EStructuralFeature feature = change.getElement1();
        builder.append(getListSizeFields().get(feature));
        builder.append("=?"); //$NON-NLS-1$
      }

      builder.append(sqlUpdateAffix);
      return builder.toString();
    }

    private int setUpdateAttributeValues(List<Pair<ITypeMapping, Object>> attributeChanges, PreparedStatement stmt,
        int col) throws SQLException
    {
      for (Pair<ITypeMapping, Object> change : attributeChanges)
      {
        ITypeMapping typeMapping = change.getElement1();
        Object value = change.getElement2();
        if (typeMapping.getFeature().isUnsettable())
        {
          // feature is unsettable
          if (value == null)
          {
            // feature is unset
            typeMapping.setDefaultValue(stmt, col++);
            stmt.setBoolean(col++, false);
          }
          else
          {
            // feature is set
            typeMapping.setValue(stmt, col++, value);
            stmt.setBoolean(col++, true);
          }
        }
        else
        {
          typeMapping.setValue(stmt, col++, change.getElement2());
        }
      }

      return col;
    }

    private int setUpdateListSizeChanges(List<Pair<EStructuralFeature, Integer>> attributeChanges,
        PreparedStatement stmt, int col) throws SQLException
    {
      for (Pair<EStructuralFeature, Integer> change : listSizeChanges)
      {
        stmt.setInt(col++, change.getElement2());
      }

      return col;
    }
  }

  @Override
  protected void reviseOldRevision(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, long timeStamp)
  {
    // do nothing
  }

  @Override
  protected String getListXRefsWhere(QueryXRefsContext context)
  {
    if (CDORevision.UNSPECIFIED_DATE != context.getTimeStamp())
    {
      throw new IllegalArgumentException("Non-audit mode does not support timestamp specification");
    }

    if (!context.getBranch().isMainBranch())
    {
      throw new IllegalArgumentException("Non-audit mode does not support branch specification");
    }

    return CDODBSchema.ATTRIBUTES_REVISED + "=0";
  }
}
