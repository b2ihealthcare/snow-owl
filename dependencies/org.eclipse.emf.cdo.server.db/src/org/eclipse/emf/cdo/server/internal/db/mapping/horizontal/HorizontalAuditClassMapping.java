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
 *    Stefan Winkler - Bug 249610: [DB] Support external references (Implementation)
 *    Lothar Werzinger - Bug 296440: [DB] Change RDB schema to improve scalability of to-many references in audit mode
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
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
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingAuditSupport;
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
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalAuditClassMapping extends AbstractHorizontalClassMapping implements IClassMappingAuditSupport,
    IClassMappingDeltaSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalAuditClassMapping.class);

  private String sqlInsertAttributes;

  private String sqlSelectCurrentAttributes;

  private String sqlSelectAllObjectIDs;

  private String sqlSelectAttributesByTime;

  private String sqlSelectAttributesByVersion;

  private String sqlReviseAttributes;

  private String sqlRawDeleteAttributes;

  private ThreadLocal<FeatureDeltaWriter> deltaWriter = new ThreadLocal<FeatureDeltaWriter>()
  {
    @Override
    protected FeatureDeltaWriter initialValue()
    {
      return new FeatureDeltaWriter();
    }
  };

  public HorizontalAuditClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
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
    builder.append("=? AND ("); //$NON-NLS-1$

    String sqlSelectAttributesPrefix = builder.toString();

    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=0)"); //$NON-NLS-1$

    sqlSelectCurrentAttributes = builder.toString();

    builder = new StringBuilder(sqlSelectAttributesPrefix);

    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append("<=? AND ("); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=0 OR "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(">=?))"); //$NON-NLS-1$

    sqlSelectAttributesByTime = builder.toString();

    builder = new StringBuilder(sqlSelectAttributesPrefix);

    builder.append("ABS(");
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(")=?)"); //$NON-NLS-1$

    sqlSelectAttributesByVersion = builder.toString();

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

    // ----------- Update to set revised ----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=? WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=0"); //$NON-NLS-1$
    sqlReviseAttributes = builder.toString();

    // ----------- Select all unrevised Object IDs ------
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=0"); //$NON-NLS-1$
    sqlSelectAllObjectIDs = builder.toString();

    // ----------- Raw delete one specific revision ------
    builder = new StringBuilder("DELETE FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append("=?"); //$NON-NLS-1$
    sqlRawDeleteAttributes = builder.toString();
  }

  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      long timeStamp = revision.getTimeStamp();
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        stmt = statementCache.getPreparedStatement(sqlSelectAttributesByTime, ReuseProbability.MEDIUM);
        idHandler.setCDOID(stmt, 1, revision.getID());
        stmt.setLong(2, timeStamp);
        stmt.setLong(3, timeStamp);
      }
      else
      {
        stmt = statementCache.getPreparedStatement(sqlSelectCurrentAttributes, ReuseProbability.HIGH);
        idHandler.setCDOID(stmt, 1, revision.getID());
      }

      // Read singleval-attribute table always (even without modeled attributes!)
      boolean success = readValuesFromStatement(stmt, revision, accessor);

      // Read multival tables only if revision exists
      if (success && revision.getVersion() >= CDOBranchVersion.FIRST_VERSION)
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

  public boolean readRevisionByVersion(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlSelectAttributesByVersion, ReuseProbability.HIGH);
      idHandler.setCDOID(stmt, 1, revision.getID());
      stmt.setInt(2, revision.getVersion());

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

  public PreparedStatement createResourceQueryStatement(IDBStoreAccessor accessor, CDOID folderId, String name,
      boolean exactMatch, CDOBranchPoint branchPoint)
  {
    EStructuralFeature nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();
    long timeStamp = branchPoint.getTimeStamp();

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

    builder.append(" AND ("); //$NON-NLS-1$

    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0)"); //$NON-NLS-1$
    }
    else
    {
      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
      builder.append("<=? AND ("); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0 OR "); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(">=?))"); //$NON-NLS-1$
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

      if (timeStamp != CDORevision.UNSPECIFIED_DATE)
      {
        stmt.setLong(column++, timeStamp);
        stmt.setLong(column++, timeStamp);
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

  public PreparedStatement createObjectIDStatement(IDBStoreAccessor accessor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Created ObjectID Statement : {0}", sqlSelectAllObjectIDs); //$NON-NLS-1$
    }

    IPreparedStatementCache statementCache = accessor.getStatementCache();
    return statementCache.getPreparedStatement(sqlSelectAllObjectIDs, ReuseProbability.HIGH);
  }

  @Override
  protected final void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
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

  @Override
  protected void detachAttributes(IDBStoreAccessor accessor, CDOID id, int version, CDOBranch branch, long timeStamp,
      OMMonitor mon)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlInsertAttributes, ReuseProbability.HIGH);

      int column = 1;

      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, -version); // cdo_version
      stmt.setLong(column++, timeStamp); // cdo_created
      stmt.setLong(column++, CDOBranchPoint.UNSPECIFIED_DATE); // cdo_revised
      idHandler.setCDOID(stmt, column++, CDOID.NULL); // resource
      idHandler.setCDOID(stmt, column++, CDOID.NULL); // container
      stmt.setInt(column++, 0); // containing feature ID

      int isSetCol = column + getValueMappings().size();

      for (ITypeMapping mapping : getValueMappings())
      {
        EStructuralFeature feature = mapping.getFeature();
        if (feature.isUnsettable())
        {
          stmt.setBoolean(isSetCol++, false);
        }

        mapping.setDefaultValue(stmt, column++);
      }

      Map<EStructuralFeature, String> listSizeFields = getListSizeFields();
      if (listSizeFields != null)
      {
        // list size columns begin after isSet-columns
        column = isSetCol;

        for (int i = 0; i < listSizeFields.size(); i++)
        {
          stmt.setInt(column++, 0);
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

  @Override
  protected void rawDeleteAttributes(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version, OMMonitor fork)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlRawDeleteAttributes, ReuseProbability.HIGH);
      getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, 1, id);
      stmt.setInt(2, version);
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
  protected void reviseOldRevision(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, long revised)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlReviseAttributes, ReuseProbability.HIGH);

      stmt.setLong(1, revised);
      idHandler.setCDOID(stmt, 2, id);

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

  @Override
  protected String getListXRefsWhere(QueryXRefsContext context)
  {
    if (CDOBranch.MAIN_BRANCH_ID != context.getBranch().getID())
    {
      throw new IllegalArgumentException("Non-audit mode does not support branch specification");
    }

    StringBuilder builder = new StringBuilder();
    long timeStamp = context.getTimeStamp();
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0"); //$NON-NLS-1$
    }
    else
    {
      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
      builder.append("<=");
      builder.append(timeStamp);
      builder.append(" AND ("); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0 OR "); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(">=");
      builder.append(timeStamp);
      builder.append(")"); //$NON-NLS-1$
    }

    return builder.toString();
  }

  /**
   * @author Stefan Winkler
   */
  private class FeatureDeltaWriter implements CDOFeatureDeltaVisitor
  {
    private IDBStoreAccessor accessor;

    private long created;

    private CDOID id;

    private int oldVersion;

    private InternalCDORevision newRevision;

    private int branchId;

    public void process(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created)
    {
      this.accessor = accessor;
      this.created = created;
      id = delta.getID();
      branchId = delta.getBranch().getID();
      oldVersion = delta.getVersion();

      if (TRACER.isEnabled())
      {
        TRACER.format("FeatureDeltaWriter: old version: {0}, new version: {1}", oldVersion, oldVersion + 1); //$NON-NLS-1$
      }

      InternalCDORevision originalRevision = (InternalCDORevision)accessor.getStore().getRepository()
          .getRevisionManager().getRevisionByVersion(id, delta, 0, true);

      newRevision = originalRevision.copy();

      newRevision.setVersion(oldVersion + 1);
      newRevision.setBranchPoint(delta.getBranch().getPoint(created));

      // process revision delta tree
      delta.accept(this);

      long revised = newRevision.getTimeStamp() - 1;
      reviseOldRevision(accessor, id, delta.getBranch(), revised);

      writeValues(accessor, newRevision);
    }

    public void visit(CDOMoveFeatureDelta delta)
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

    public void visit(CDOSetFeatureDelta delta)
    {
      delta.apply(newRevision);
    }

    public void visit(CDOUnsetFeatureDelta delta)
    {
      delta.apply(newRevision);
    }

    public void visit(CDOListFeatureDelta delta)
    {
      delta.apply(newRevision);
      IListMappingDeltaSupport listMapping = (IListMappingDeltaSupport)getListMapping(delta.getFeature());
      listMapping.processDelta(accessor, id, branchId, oldVersion, oldVersion + 1, created, delta);
    }

    public void visit(CDOClearFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDOContainerFeatureDelta delta)
    {
      delta.apply(newRevision);
    }
  }
}
