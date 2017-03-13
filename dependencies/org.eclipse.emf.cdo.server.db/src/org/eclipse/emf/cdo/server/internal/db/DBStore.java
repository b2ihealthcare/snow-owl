/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 259402
 *    Stefan Winkler - Bug 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - Bug 249610: [DB] Support external references (Implementation)
 *    Stefan Winkler - Bug 289056: [DB] Exception "ERROR: relation "cdo_external_refs" does not exist" while executing test-suite for PostgreSQL
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.messages.Messages;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.DBSchema;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;

/**
 * @author Eike Stepper
 */
public class DBStore extends Store implements IDBStore, CDOAllRevisionsProvider
{
  public static final String TYPE = "db"; //$NON-NLS-1$

  private static final String PROP_REPOSITORY_CREATED = "org.eclipse.emf.cdo.server.db.repositoryCreated"; //$NON-NLS-1$

  private static final String PROP_REPOSITORY_STOPPED = "org.eclipse.emf.cdo.server.db.repositoryStopped"; //$NON-NLS-1$

  private static final String PROP_NEXT_LOCAL_CDOID = "org.eclipse.emf.cdo.server.db.nextLocalCDOID"; //$NON-NLS-1$

  private static final String PROP_LAST_CDOID = "org.eclipse.emf.cdo.server.db.lastCDOID"; //$NON-NLS-1$

  private static final String PROP_LAST_BRANCHID = "org.eclipse.emf.cdo.server.db.lastBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_LOCAL_BRANCHID = "org.eclipse.emf.cdo.server.db.lastLocalBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_COMMITTIME = "org.eclipse.emf.cdo.server.db.lastCommitTime"; //$NON-NLS-1$

  private static final String PROP_LAST_NONLOCAL_COMMITTIME = "org.eclipse.emf.cdo.server.db.lastNonLocalCommitTime"; //$NON-NLS-1$

  private static final String PROP_GRACEFULLY_SHUT_DOWN = "org.eclipse.emf.cdo.server.db.gracefullyShutDown"; //$NON-NLS-1$

  private long creationTime;

  private boolean firstTime;

  private Map<String, String> properties;

  private IIDHandler idHandler;

  private IMetaDataManager metaDataManager = new MetaDataManager(this);

  private DurableLockingManager durableLockingManager = new DurableLockingManager(this);

  private IMappingStrategy mappingStrategy;

  private IDBSchema dbSchema;

  private IDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  @ExcludeFromDump
  private transient ProgressDistributor accessorWriteDistributor = new ProgressDistributor.Geometric()
  {
    @Override
    public String toString()
    {
      String result = "accessorWriteDistributor"; //$NON-NLS-1$
      if (getRepository() != null)
      {
        result += ": " + getRepository().getName(); //$NON-NLS-1$
      }

      return result;
    }
  };

  @ExcludeFromDump
  private transient StoreAccessorPool readerPool;

  @ExcludeFromDump
  private transient StoreAccessorPool writerPool;

  @ExcludeFromDump
  private transient Timer connectionKeepAliveTimer;

  public DBStore()
  {
    super(TYPE, null, set(ChangeFormat.REVISION, ChangeFormat.DELTA), //
        set(RevisionTemporality.AUDITING, RevisionTemporality.NONE), //
        set(RevisionParallelism.NONE, RevisionParallelism.BRANCHING));
    
    readerPool = new StoreAccessorPool(this, null);
    readerPool.setCapacity(7);
    
    writerPool = new StoreAccessorPool(this, null);
    writerPool.setCapacity(3);
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
    mappingStrategy.setStore(this);
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public void setDBAdapter(IDBAdapter dbAdapter)
  {
    this.dbAdapter = dbAdapter;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public IIDHandler getIDHandler()
  {
    return idHandler;
  }

  public Connection getConnection()
  {
    Connection connection = dbConnectionProvider.getConnection();
    if (connection == null)
    {
      throw new DBException("No connection from connection provider: " + dbConnectionProvider); //$NON-NLS-1$
    }

    try
    {
      connection.setAutoCommit(false);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex, "SET AUTO COMMIT = false");
    }

    return connection;
  }

  public void setDbConnectionProvider(IDBConnectionProvider dbConnectionProvider)
  {
    this.dbConnectionProvider = dbConnectionProvider;
  }

  public void setDataSource(DataSource dataSource)
  {
    dbConnectionProvider = DBUtil.createConnectionProvider(dataSource);
  }

  public IMetaDataManager getMetaDataManager()
  {
    return metaDataManager;
  }

  public DurableLockingManager getDurableLockingManager()
  {
    return durableLockingManager;
  }

  public Timer getConnectionKeepAliveTimer()
  {
    return connectionKeepAliveTimer;
  }

  @Override
  public Set<ChangeFormat> getSupportedChangeFormats()
  {
    if (mappingStrategy.hasDeltaSupport())
    {
      return set(ChangeFormat.DELTA);
    }

    return set(ChangeFormat.REVISION);
  }

  public ProgressDistributor getAccessorWriteDistributor()
  {
    return accessorWriteDistributor;
  }

  public IDBSchema getDBSchema()
  {
    return dbSchema;
  }

  public Map<String, String> getPersistentProperties(Set<String> names)
  {
    Connection connection = null;
    PreparedStatement selectStmt = null;
    String sql = null;

    try
    {
      connection = getConnection();
      Map<String, String> result = new HashMap<String, String>();
      boolean allProperties = names == null || names.isEmpty();
      if (allProperties)
      {
        sql = CDODBSchema.SQL_SELECT_ALL_PROPERTIES;
        selectStmt = connection.prepareStatement(sql);
        ResultSet resultSet = null;

        try
        {
          resultSet = selectStmt.executeQuery();
          while (resultSet.next())
          {
            String key = resultSet.getString(1);
            String value = resultSet.getString(2);
            result.put(key, value);
          }
        }
        finally
        {
          DBUtil.close(resultSet);
        }
      }
      else
      {
        sql = CDODBSchema.SQL_SELECT_PROPERTIES;
        selectStmt = connection.prepareStatement(sql);
        for (String name : names)
        {
          selectStmt.setString(1, name);
          ResultSet resultSet = null;

          try
          {
            resultSet = selectStmt.executeQuery();
            if (resultSet.next())
            {
              String value = resultSet.getString(1);
              result.put(name, value);
            }
          }
          finally
          {
            DBUtil.close(resultSet);
          }
        }
      }

      return result;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex, sql);
    }
    finally
    {
      DBUtil.close(selectStmt);
      DBUtil.close(connection);
    }
  }

  public void setPersistentProperties(Map<String, String> properties)
  {
    Connection connection = null;
    PreparedStatement deleteStmt = null;
    PreparedStatement insertStmt = null;
    String sql = null;

    try
    {
      connection = getConnection();
      deleteStmt = connection.prepareStatement(CDODBSchema.SQL_DELETE_PROPERTIES);
      insertStmt = connection.prepareStatement(CDODBSchema.SQL_INSERT_PROPERTIES);

      for (Entry<String, String> entry : properties.entrySet())
      {
        String name = entry.getKey();
        String value = entry.getValue();

        sql = CDODBSchema.SQL_DELETE_PROPERTIES;
        deleteStmt.setString(1, name);
        deleteStmt.executeUpdate();

        sql = CDODBSchema.SQL_INSERT_PROPERTIES;
        insertStmt.setString(1, name);
        insertStmt.setString(2, value);
        insertStmt.executeUpdate();
      }

      sql = null;
      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex, sql);
    }
    finally
    {
      DBUtil.close(insertStmt);
      DBUtil.close(deleteStmt);
      DBUtil.close(connection);
    }
  }

  public void removePersistentProperties(Set<String> names)
  {
    Connection connection = null;
    PreparedStatement deleteStmt = null;

    try
    {
      connection = getConnection();
      deleteStmt = connection.prepareStatement(CDODBSchema.SQL_DELETE_PROPERTIES);

      for (String name : names)
      {
        deleteStmt.setString(1, name);
        deleteStmt.executeUpdate();
      }

      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex, CDODBSchema.SQL_DELETE_PROPERTIES);
    }
    finally
    {
      DBUtil.close(deleteStmt);
      DBUtil.close(connection);
    }
  }

  @Override
  public DBStoreAccessor getReader(ISession session)
  {
    return (DBStoreAccessor)super.getReader(session);
  }

  @Override
  public DBStoreAccessor getWriter(ITransaction transaction)
  {
    return (DBStoreAccessor)super.getWriter(transaction);
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    return readerPool;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    return writerPool;
  }

  @Override
  protected DBStoreAccessor createReader(ISession session) throws DBException
  {
    return new DBStoreAccessor(this, session);
  }

  @Override
  protected DBStoreAccessor createWriter(ITransaction transaction) throws DBException
  {
    return new DBStoreAccessor(this, transaction);
  }

  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    final Map<CDOBranch, List<CDORevision>> result = new HashMap<CDOBranch, List<CDORevision>>();
    IDBStoreAccessor accessor = getReader(null);
    StoreThreadLocal.setAccessor(accessor);

    try
    {
      accessor.handleRevisions(null, null, CDOBranchPoint.UNSPECIFIED_DATE, true,
          new CDORevisionHandler.Filtered.Undetached(new CDORevisionHandler()
          {
            public boolean handleRevision(CDORevision revision)
            {
              CDOBranch branch = revision.getBranch();
              List<CDORevision> list = result.get(branch);
              if (list == null)
              {
                list = new ArrayList<CDORevision>();
                result.put(branch, list);
              }

              list.add(revision);
              return true;
            }
          }));
    }
    finally
    {
      StoreThreadLocal.release();
    }

    return result;
  }

  public void setIdHandler(IIDHandler idHandler)
  {
    this.idHandler = idHandler;
  }

  public CDOID createObjectID(String val)
  {
    return idHandler.createCDOID(val);
  }

  public boolean isLocal(CDOID id)
  {
    return idHandler.isLocalCDOID(id);
  }

  public CDOID getNextCDOID(LongIDStoreAccessor accessor, CDORevision revision)
  {
    return idHandler.getNextCDOID(revision);
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  public void setCreationTime(long creationTime)
  {
    this.creationTime = creationTime;

    Map<String, String> map = new HashMap<String, String>();
    map.put(PROP_REPOSITORY_CREATED, Long.toString(creationTime));
    setPersistentProperties(map);
  }

  public boolean isFirstStart()
  {
    return firstTime;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkNull(mappingStrategy, Messages.getString("DBStore.2")); //$NON-NLS-1$
    checkNull(dbAdapter, Messages.getString("DBStore.1")); //$NON-NLS-1$
    checkNull(dbConnectionProvider, Messages.getString("DBStore.0")); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    if (null == idHandler)
    {

      if (getRepository().getIDGenerationLocation() == IDGenerationLocation.CLIENT)
      {
        idHandler = new UUIDHandler(this);
      }
      else
      {
        idHandler = new LongIDHandler(this);
      }

    }

    setObjectIDTypes(idHandler.getObjectIDTypes());
    connectionKeepAliveTimer = new Timer("Connection-Keep-Alive-" + this); //$NON-NLS-1$

    Set<IDBTable> createdTables = null;
    Connection connection = getConnection();

    try
    {
      if (isDropAllDataOnActivate())
      {
        OM.LOG.info("Dropping all tables from repository " + getRepository().getName() + "...");
        DBUtil.dropAllTables(connection, null);
        connection.commit();
      }

      createdTables = CDODBSchema.INSTANCE.create(dbAdapter, connection);
      connection.commit();
    }
    finally
    {
      DBUtil.close(connection);
    }

    dbSchema = createSchema();

    LifecycleUtil.activate(idHandler);
    LifecycleUtil.activate(metaDataManager);
    LifecycleUtil.activate(durableLockingManager);
    LifecycleUtil.activate(mappingStrategy);

    setRevisionTemporality(mappingStrategy.hasAuditSupport() ? RevisionTemporality.AUDITING : RevisionTemporality.NONE);
    setRevisionParallelism(mappingStrategy.hasBranchingSupport() ? RevisionParallelism.BRANCHING
        : RevisionParallelism.NONE);

    if (isFirstStart(createdTables))
    {
      firstStart();
    }
    else
    {
      reStart();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(mappingStrategy);
    LifecycleUtil.deactivate(durableLockingManager);
    LifecycleUtil.deactivate(metaDataManager);
    LifecycleUtil.deactivate(idHandler);

    Map<String, String> map = new HashMap<String, String>();
    map.put(PROP_GRACEFULLY_SHUT_DOWN, Boolean.TRUE.toString());
    map.put(PROP_REPOSITORY_STOPPED, Long.toString(getRepository().getTimeStamp()));

    if (getRepository().getIDGenerationLocation() == IDGenerationLocation.STORE)
    {
      map.put(PROP_NEXT_LOCAL_CDOID, Store.idToString(idHandler.getNextLocalObjectID()));
      map.put(PROP_LAST_CDOID, Store.idToString(idHandler.getLastObjectID()));
    }

    map.put(PROP_LAST_BRANCHID, Integer.toString(getLastBranchID()));
    map.put(PROP_LAST_LOCAL_BRANCHID, Integer.toString(getLastLocalBranchID()));
    map.put(PROP_LAST_COMMITTIME, Long.toString(getLastCommitTime()));
    map.put(PROP_LAST_NONLOCAL_COMMITTIME, Long.toString(getLastNonLocalCommitTime()));
    setPersistentProperties(map);

    if (readerPool != null)
    {
      readerPool.dispose();
    }

    if (writerPool != null)
    {
      writerPool.dispose();
    }

    connectionKeepAliveTimer.cancel();
    connectionKeepAliveTimer = null;

    super.doDeactivate();
  }

  protected boolean isFirstStart(Set<IDBTable> createdTables)
  {
    if (createdTables.contains(CDODBSchema.PROPERTIES))
    {
      return true;
    }

    Set<String> names = new HashSet<String>();
    names.add(PROP_REPOSITORY_CREATED);

    Map<String, String> map = getPersistentProperties(names);
    return map.get(PROP_REPOSITORY_CREATED) == null;
  }

  protected void firstStart()
  {
    InternalRepository repository = getRepository();
    setCreationTime(repository.getTimeStamp());
    firstTime = true;
  }

  protected void reStart()
  {
    Set<String> names = new HashSet<String>();
    names.add(PROP_REPOSITORY_CREATED);
    names.add(PROP_GRACEFULLY_SHUT_DOWN);

    Map<String, String> map = getPersistentProperties(names);
    creationTime = Long.valueOf(map.get(PROP_REPOSITORY_CREATED));

    if (map.containsKey(PROP_GRACEFULLY_SHUT_DOWN))
    {
      names.clear();

      InternalRepository repository = getRepository();
      boolean generatingIDs = repository.getIDGenerationLocation() == IDGenerationLocation.STORE;
      if (generatingIDs)
      {
        names.add(PROP_NEXT_LOCAL_CDOID);
        names.add(PROP_LAST_CDOID);
      }

      names.add(PROP_LAST_BRANCHID);
      names.add(PROP_LAST_LOCAL_BRANCHID);
      names.add(PROP_LAST_COMMITTIME);
      names.add(PROP_LAST_NONLOCAL_COMMITTIME);
      map = getPersistentProperties(names);

      if (generatingIDs)
      {
        idHandler.setNextLocalObjectID(Store.stringToID(map.get(PROP_NEXT_LOCAL_CDOID)));
        idHandler.setLastObjectID(Store.stringToID(map.get(PROP_LAST_CDOID)));
      }

      setLastBranchID(Integer.valueOf(map.get(PROP_LAST_BRANCHID)));
      setLastLocalBranchID(Integer.valueOf(map.get(PROP_LAST_LOCAL_BRANCHID)));
      setLastCommitTime(Long.valueOf(map.get(PROP_LAST_COMMITTIME)));
      setLastNonLocalCommitTime(Long.valueOf(map.get(PROP_LAST_NONLOCAL_COMMITTIME)));
    }
    else
    {
      repairAfterCrash();
    }

    removePersistentProperties(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));
  }

  protected void repairAfterCrash()
  {
    String name = getRepository().getName();
    OM.LOG.warn(MessageFormat.format(Messages.getString("DBStore.9"), name)); //$NON-NLS-1$

    Connection connection = getConnection();
    boolean oldAutoCommit = true;
    boolean oldReadOnly = false;

    try
    {
      oldAutoCommit = connection.getAutoCommit();
      oldReadOnly = connection.isReadOnly();
      connection.setAutoCommit(false);
      connection.setReadOnly(true);

      mappingStrategy.repairAfterCrash(dbAdapter, connection); // Must update the idHandler

      boolean storeIDs = getRepository().getIDGenerationLocation() == IDGenerationLocation.STORE;
      CDOID lastObjectID = storeIDs ? idHandler.getLastObjectID() : CDOID.NULL;
      CDOID nextLocalObjectID = storeIDs ? idHandler.getNextLocalObjectID() : CDOID.NULL;

      int branchID = DBUtil.selectMaximumInt(connection, CDODBSchema.BRANCHES_ID);
      setLastBranchID(branchID > 0 ? branchID : 0);

      int localBranchID = DBUtil.selectMinimumInt(connection, CDODBSchema.BRANCHES_ID);
      setLastLocalBranchID(localBranchID < 0 ? localBranchID : 0);

      long lastCommitTime = DBUtil.selectMaximumLong(connection, CDODBSchema.COMMIT_INFOS_TIMESTAMP);
      setLastCommitTime(lastCommitTime);

      long lastNonLocalCommitTime = DBUtil.selectMaximumLong(connection, CDODBSchema.COMMIT_INFOS_TIMESTAMP,
          CDOBranch.MAIN_BRANCH_ID + "<=" + CDODBSchema.COMMIT_INFOS_BRANCH);
      setLastNonLocalCommitTime(lastNonLocalCommitTime);

      if (storeIDs)
      {
        OM.LOG
            .info(MessageFormat.format(
                Messages.getString("DBStore.10"), name, lastObjectID, nextLocalObjectID, getLastBranchID(), getLastCommitTime(), getLastNonLocalCommitTime())); //$NON-NLS-1$
      }
      else
      {
        OM.LOG
            .info(MessageFormat.format(
                Messages.getString("DBStore.10b"), name, getLastBranchID(), getLastCommitTime(), getLastNonLocalCommitTime())); //$NON-NLS-1$
      }
    }
    catch (SQLException e)
    {
      OM.LOG.error(MessageFormat.format(Messages.getString("DBStore.11"), name), e); //$NON-NLS-1$
      throw new DBException(e);
    }
    finally
    {
      disposeConnection(connection, oldAutoCommit, oldReadOnly);
    }
  }

  private void disposeConnection(Connection connection, boolean oldAutoCommit, boolean oldReadOnly)
  {
    try
    {
      connection.setAutoCommit(oldAutoCommit);
      connection.setReadOnly(oldReadOnly);
    }
    catch (SQLException e)
    {
      // XXX (apeteri): Don't rethrow this exception; an earlier one may be shadowed by it
      OM.LOG.error(MessageFormat.format(Messages.getString("DBStore.12"), getRepository().getName()), e); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  protected IDBSchema createSchema()
  {
    String name = getRepository().getName();
    return new DBSchema(name);
  }
}
