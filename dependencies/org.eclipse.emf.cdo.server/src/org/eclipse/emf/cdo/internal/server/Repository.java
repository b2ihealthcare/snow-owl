/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233273
 *    Simon McDuff - bug 233490
 *    Stefan Winkler - changed order of determining audit and revision delta support.
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.internal.server;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStore.CanHandleClientAssignedIDs;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationInfo;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader2;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.server.ContainerQueryHandlerProvider;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalCommitManager;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.internal.cdo.object.CDOFactoryImpl;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockObjectsResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.transaction.TransactionException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Repository extends Container<Object> implements InternalRepository
{
  private String name;

  private String uuid;

  private InternalStore store;

  private Type type = Type.MASTER;

  private State state = State.ONLINE;

  private Map<String, String> properties;

  private boolean supportingAudits;

  private boolean supportingBranches;

  private boolean supportingEcore;

  private boolean ensuringReferentialIntegrity;

  private IDGenerationLocation idGenerationLocation;

  /**
   * Must not be thread-bound to support XA commits.
   */
  private Semaphore packageRegistryCommitLock = new Semaphore(1);

  private InternalCDOPackageRegistry packageRegistry;

  private InternalCDOBranchManager branchManager;

  private InternalCDORevisionManager revisionManager;

  private InternalCDOCommitInfoManager commitInfoManager;

  private InternalSessionManager sessionManager;

  private InternalQueryManager queryManager;

  private InternalCommitManager commitManager;

  private InternalLockManager lockingManager;

  private IQueryHandlerProvider queryHandlerProvider;

  private List<ReadAccessHandler> readAccessHandlers = new ArrayList<ReadAccessHandler>();

  private List<WriteAccessHandler> writeAccessHandlers = new ArrayList<WriteAccessHandler>();

  private List<CDOCommitInfoHandler> commitInfoHandlers = new ArrayList<CDOCommitInfoHandler>();

  private EPackage[] initialPackages;

  // Bugzilla 297940
  private TimeStampAuthority timeStampAuthority = new TimeStampAuthority(this);

  @ExcludeFromDump
  private transient Object createBranchLock = new Object();

  private boolean skipInitialization;

  private CDOID rootResourceID;

  public Repository()
  {
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getUUID()
  {
    if (uuid == null)
    {
      uuid = getProperties().get(Props.OVERRIDE_UUID);
      if (uuid == null)
      {
        uuid = UUID.randomUUID().toString();
      }
      else if (uuid.length() == 0)
      {
        uuid = getName();
      }
    }

    return uuid;
  }

  public InternalStore getStore()
  {
    return store;
  }

  public void setStore(InternalStore store)
  {
    this.store = store;
  }

  public Type getType()
  {
    return type;
  }

  public void setType(Type type)
  {
    checkArg(type, "type"); //$NON-NLS-1$
    if (this.type != type)
    {
      changingType(this.type, type);
    }
  }

  protected void changingType(Type oldType, Type newType)
  {
    type = newType;
    fireEvent(new RepositoryTypeChangedEvent(this, oldType, newType));

    if (sessionManager != null)
    {
      sessionManager.sendRepositoryTypeNotification(oldType, newType);
    }
  }

  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    checkArg(state, "state"); //$NON-NLS-1$
    if (this.state != state)
    {
      changingState(this.state, state);
    }
  }

  protected void changingState(State oldState, State newState)
  {
    state = newState;
    fireEvent(new RepositoryStateChangedEvent(this, oldState, newState));

    if (sessionManager != null)
    {
      sessionManager.sendRepositoryStateNotification(oldState, newState, getRootResourceID());
    }
  }

  public synchronized Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap<String, String>();
    }

    return properties;
  }

  public synchronized void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public boolean isSupportingAudits()
  {
    return supportingAudits;
  }

  public boolean isSupportingBranches()
  {
    return supportingBranches;
  }

  public boolean isSupportingEcore()
  {
    return supportingEcore;
  }

  public boolean isEnsuringReferentialIntegrity()
  {
    return ensuringReferentialIntegrity;
  }

  public IDGenerationLocation getIDGenerationLocation()
  {
    return idGenerationLocation;
  }

  public String getStoreType()
  {
    return store.getType();
  }

  public Set<CDOID.ObjectType> getObjectIDTypes()
  {
    return store.getObjectIDTypes();
  }

  public CDOID getRootResourceID()
  {
    return rootResourceID;
  }

  public void setRootResourceID(CDOID rootResourceID)
  {
    this.rootResourceID = rootResourceID;
  }

  public Object processPackage(Object value)
  {
    CDOFactoryImpl.prepareDynamicEPackage(value);
    return value;
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadPackageUnit((InternalCDOPackageUnit)packageUnit);
  }

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    if (!isSupportingBranches())
    {
      throw new IllegalStateException("Branching is not supported by " + this);
    }

    long timeStamp = getTimeStamp();
    long baseTimeStamp = branchInfo.getBaseTimeStamp();
    if (baseTimeStamp == CDOBranchPoint.UNSPECIFIED_DATE || baseTimeStamp > timeStamp)
    {
      baseTimeStamp = timeStamp;
      branchInfo = new BranchInfo(branchInfo.getName(), branchInfo.getBaseBranchID(), baseTimeStamp);
    }

    synchronized (createBranchLock)
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      return accessor.createBranch(branchID, branchInfo);
    }
  }

  public BranchInfo loadBranch(int branchID)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadBranch(branchID);
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadSubBranches(branchID);
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadBranches(startID, endID, branchHandler);
  }

  @Deprecated
  public void deleteBranch(int branchID)
  {
    throw new UnsupportedOperationException();
  }

  public void renameBranch(int branchID, String newName)
  {
    if (!isSupportingBranches())
    {
      throw new IllegalStateException("Branching is not supported by " + this);
    }

    if (branchID == CDOBranch.MAIN_BRANCH_ID)
    {
      throw new IllegalArgumentException("Renaming of the MAIN branch is not supported");
    }

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (!(accessor instanceof BranchLoader2))
    {
      throw new UnsupportedOperationException("Branch renaming is not supported by " + this);
    }

    synchronized (createBranchLock)
    {
      ((BranchLoader2)accessor).renameBranch(branchID, newName);
    }
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.loadCommitInfos(branch, startTime, endTime, handler);
  }

  public CDOCommitData loadCommitData(long timeStamp)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadCommitData(timeStamp);
  }

  public List<InternalCDORevision> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint,
      int referenceChunk, int prefetchDepth)
  {
    for (RevisionInfo info : infos)
    {
      CDOID id = info.getID();
      RevisionInfo.Type type = info.getType();
      switch (type)
      {
      case AVAILABLE_NORMAL: // direct == false
      {
        RevisionInfo.Available.Normal availableInfo = (RevisionInfo.Available.Normal)info;
        checkArg(availableInfo.isDirect() == false, "Load is not needed");
        break;
      }

      case AVAILABLE_POINTER: // direct == false || target == null
      {
        RevisionInfo.Available.Pointer pointerInfo = (RevisionInfo.Available.Pointer)info;
        boolean needsTarget = !pointerInfo.hasTarget();
        checkArg(pointerInfo.isDirect() == false || needsTarget, "Load is not needed");

        if (needsTarget)
        {
          CDOBranchVersion targetBranchVersion = pointerInfo.getTargetBranchVersion();
          InternalCDORevision target = loadRevisionByVersion(id, targetBranchVersion, referenceChunk);
          PointerCDORevision pointer = new PointerCDORevision(target.getEClass(), id, pointerInfo
              .getAvailableBranchVersion().getBranch(), CDORevision.UNSPECIFIED_DATE, target);

          info.setResult(target);
          info.setSynthetic(pointer);
          continue;
        }

        break;
      }

      case AVAILABLE_DETACHED: // direct == false
      {
        RevisionInfo.Available.Detached detachedInfo = (RevisionInfo.Available.Detached)info;
        checkArg(detachedInfo.isDirect() == false, "Load is not needed");
        break;
      }

      case MISSING:
      {
        break;
      }

      default:
        throw new IllegalStateException("Invalid revision info type: " + type);
      }

      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      InternalCDORevision revision = accessor.readRevision(id, branchPoint, referenceChunk, revisionManager);
      if (revision == null)
      {
        if (isSupportingAudits())
        {
          InternalCDORevision target = loadRevisionTarget(id, branchPoint, referenceChunk, accessor);
          if (target != null)
          {
            if (referenceChunk == CDORevision.UNCHUNKED)
            {
              target.setUnchunked();
            }

            CDOBranch branch = branchPoint.getBranch();
            long revised = loadRevisionRevised(id, branch);
            PointerCDORevision pointer = new PointerCDORevision(target.getEClass(), id, branch, revised, target);
            info.setSynthetic(pointer);
          }

          info.setResult(target);
        }
        else
        {
          DetachedCDORevision detachedRevision = new DetachedCDORevision(EcorePackage.Literals.ECLASS, id,
              branchPoint.getBranch(), 0, CDORevision.UNSPECIFIED_DATE);
          info.setSynthetic(detachedRevision);
        }
      }
      else if (revision instanceof DetachedCDORevision)
      {
        DetachedCDORevision detached = (DetachedCDORevision)revision;
        info.setSynthetic(detached);
      }
      else
      {
        if (referenceChunk == CDORevision.UNCHUNKED)
        {
          revision.setUnchunked();
        }

        revision.freeze();
        info.setResult(revision);
      }
    }

    return null;
  }

  private InternalCDORevision loadRevisionTarget(CDOID id, CDOBranchPoint branchPoint, int referenceChunk,
      IStoreAccessor accessor)
  {
    CDOBranch branch = branchPoint.getBranch();
    while (!branch.isMainBranch())
    {
      branchPoint = branch.getBase();
      branch = branchPoint.getBranch();

      InternalCDORevision revision = accessor.readRevision(id, branchPoint, referenceChunk, revisionManager);
      if (revision != null)
      {
        revision.freeze();
        return revision;
      }
    }

    return null;
  }

  private long loadRevisionRevised(CDOID id, CDOBranch branch)
  {
    InternalCDORevision revision = loadRevisionByVersion(id, branch.getVersion(CDORevision.FIRST_VERSION),
        CDORevision.UNCHUNKED);
    if (revision != null)
    {
      return revision.getTimeStamp() - 1;
    }

    return CDORevision.UNSPECIFIED_DATE;
  }

  public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.readRevisionByVersion(id, branchVersion, referenceChunk, revisionManager);
  }

  /**
   * @deprecated Not used.
   */
  @Deprecated
  protected void ensureChunks(InternalCDORevision revision, int referenceChunk, IStoreAccessor accessor)
  {
    EClass eClass = revision.getEClass();
    EStructuralFeature[] features = CDOModelUtil.getAllPersistentFeatures(eClass);
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature.isMany())
      {
        MoveableList<Object> list = revision.getList(feature);
        int chunkEnd = Math.min(referenceChunk, list.size());
        accessor = ensureChunk(revision, feature, accessor, list, 0, chunkEnd);
      }
    }
  }

  public void ensureChunks(InternalCDORevision revision)
  {
    if (!revision.isUnchunked())
    {
      for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(revision.getEClass()))
      {
        if (feature.isMany())
        {
          ensureChunk(revision, feature, 0, revision.getList(feature).size());
        }
      }

      revision.setUnchunked();
    }
  }

  public IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature, int chunkStart,
      int chunkEnd)
  {
    if (!revision.isUnchunked())
    {
      MoveableList<Object> list = revision.getList(feature);
      chunkEnd = Math.min(chunkEnd, list.size());
      return ensureChunk(revision, feature, StoreThreadLocal.getAccessor(), list, chunkStart, chunkEnd);
    }

    return null;
  }

  protected IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature,
      IStoreAccessor accessor, MoveableList<Object> list, int chunkStart, int chunkEnd)
  {
    IStoreChunkReader chunkReader = null;
    int fromIndex = -1;
    for (int j = chunkStart; j < chunkEnd; j++)
    {
      if (list.get(j) == InternalCDOList.UNINITIALIZED)
      {
        if (fromIndex == -1)
        {
          fromIndex = j;
        }
      }
      else
      {
        if (fromIndex != -1)
        {
          if (chunkReader == null)
          {
            if (accessor == null)
            {
              accessor = StoreThreadLocal.getAccessor();
            }

            chunkReader = accessor.createChunkReader(revision, feature);
          }

          int toIndex = j;
          if (fromIndex == toIndex - 1)
          {
            chunkReader.addSimpleChunk(fromIndex);
          }
          else
          {
            chunkReader.addRangedChunk(fromIndex, toIndex);
          }

          fromIndex = -1;
        }
      }
    }

    // Add last chunk
    if (fromIndex != -1)
    {
      if (chunkReader == null)
      {
        if (accessor == null)
        {
          accessor = StoreThreadLocal.getAccessor();
        }

        chunkReader = accessor.createChunkReader(revision, feature);
      }

      int toIndex = chunkEnd;
      if (fromIndex == toIndex - 1)
      {
        chunkReader.addSimpleChunk(fromIndex);
      }
      else
      {
        chunkReader.addRangedChunk(fromIndex, toIndex);
      }
    }

    if (chunkReader != null)
    {
      InternalCDOList cdoList = list instanceof InternalCDOList ? (InternalCDOList)list : null;

      List<Chunk> chunks = chunkReader.executeRead();
      for (Chunk chunk : chunks)
      {
        int startIndex = chunk.getStartIndex();
        for (int indexInChunk = 0; indexInChunk < chunk.size(); indexInChunk++)
        {
          Object id = chunk.get(indexInChunk);
          if (cdoList != null)
          {
            cdoList.setWithoutFrozenCheck(startIndex + indexInChunk, id);
          }
          else
          {
            list.set(startIndex + indexInChunk, id);
          }
        }
      }
    }

    return accessor;
  }

  public InternalCDOPackageRegistry getPackageRegistry(boolean considerCommitContext)
  {
    if (considerCommitContext)
    {
      IStoreAccessor.CommitContext commitContext = StoreThreadLocal.getCommitContext();
      if (commitContext != null)
      {
        InternalCDOPackageRegistry contextualPackageRegistry = commitContext.getPackageRegistry();
        if (contextualPackageRegistry != null)
        {
          return contextualPackageRegistry;
        }
      }
    }

    return packageRegistry;
  }

  public Semaphore getPackageRegistryCommitLock()
  {
    return packageRegistryCommitLock;
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return getPackageRegistry(true);
  }

  public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry)
  {
    checkInactive();
    this.packageRegistry = packageRegistry;
  }

  public InternalSessionManager getSessionManager()
  {
    return sessionManager;
  }

  /**
   * @since 2.0
   */
  public void setSessionManager(InternalSessionManager sessionManager)
  {
    checkInactive();
    this.sessionManager = sessionManager;
  }

  public InternalCDOBranchManager getBranchManager()
  {
    return branchManager;
  }

  public void setBranchManager(InternalCDOBranchManager branchManager)
  {
    checkInactive();
    this.branchManager = branchManager;
  }

  public InternalCDOCommitInfoManager getCommitInfoManager()
  {
    return commitInfoManager;
  }

  public void setCommitInfoManager(InternalCDOCommitInfoManager commitInfoManager)
  {
    checkInactive();
    this.commitInfoManager = commitInfoManager;
  }

  public InternalCDORevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  /**
   * @since 2.0
   */
  public void setRevisionManager(InternalCDORevisionManager revisionManager)
  {
    checkInactive();
    this.revisionManager = revisionManager;
  }

  /**
   * @since 2.0
   */
  public InternalQueryManager getQueryManager()
  {
    return queryManager;
  }

  /**
   * @since 2.0
   */
  public void setQueryManager(InternalQueryManager queryManager)
  {
    checkInactive();
    this.queryManager = queryManager;
  }

  /**
   * @since 2.0
   */
  public InternalCommitManager getCommitManager()
  {
    return commitManager;
  }

  /**
   * @since 2.0
   */
  public void setCommitManager(InternalCommitManager commitManager)
  {
    checkInactive();
    this.commitManager = commitManager;
  }

  /**
   * @since 2.0
   * @deprecated
   */
  @Deprecated
  public InternalLockManager getLockManager()
  {
    return getLockingManager();
  }

  public InternalLockManager getLockingManager()
  {
    return lockingManager;
  }

  /**
   * @since 2.0
   */
  public void setLockingManager(InternalLockManager lockingManager)
  {
    checkInactive();
    this.lockingManager = lockingManager;
  }

  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    return new TransactionCommitContext(transaction);
  }

  public long getLastCommitTimeStamp()
  {
    return timeStampAuthority.getLastFinishedTimeStamp();
  }

  public void setLastCommitTimeStamp(long lastCommitTimeStamp)
  {
    timeStampAuthority.setLastFinishedTimeStamp(lastCommitTimeStamp);
  }

  public long waitForCommit(long timeout)
  {
    return timeStampAuthority.waitForCommit(timeout);
  }

  public long[] createCommitTimeStamp(OMMonitor monitor)
  {
    return timeStampAuthority.startCommit(CDOBranchPoint.UNSPECIFIED_DATE, monitor);
  }

  public long[] forceCommitTimeStamp(long override, OMMonitor monitor)
  {
    return timeStampAuthority.startCommit(override, monitor);
  }

  public void endCommit(long timestamp)
  {
    timeStampAuthority.endCommit(timestamp);
  }

  public void failCommit(long timestamp)
  {
    timeStampAuthority.failCommit(timestamp);
  }

  public CDOCommitInfoHandler[] getCommitInfoHandlers()
  {
    synchronized (commitInfoHandlers)
    {
      return commitInfoHandlers.toArray(new CDOCommitInfoHandler[commitInfoHandlers.size()]);
    }
  }

  /**
   * @since 4.0
   */
  public void addCommitInfoHandler(CDOCommitInfoHandler handler)
  {
    synchronized (commitInfoHandlers)
    {
      if (!commitInfoHandlers.contains(handler))
      {
        commitInfoHandlers.add(handler);
      }
    }
  }

  /**
   * @since 4.0
   */
  public void removeCommitInfoHandler(CDOCommitInfoHandler handler)
  {
    synchronized (commitInfoHandlers)
    {
      commitInfoHandlers.remove(handler);
    }
  }

  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo)
  {
    sessionManager.sendCommitNotification(sender, commitInfo);

    for (CDOCommitInfoHandler handler : getCommitInfoHandlers())
    {
      try
      {
        handler.handleCommitInfo(commitInfo);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * @since 2.0
   */
  public IQueryHandlerProvider getQueryHandlerProvider()
  {
    return queryHandlerProvider;
  }

  /**
   * @since 2.0
   */
  public void setQueryHandlerProvider(IQueryHandlerProvider queryHandlerProvider)
  {
    this.queryHandlerProvider = queryHandlerProvider;
  }

  /**
   * @since 2.0
   */
  public synchronized IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    String language = info.getQueryLanguage();
    if (CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES.equals(language))
    {
      return new ResourcesQueryHandler();
    }

    if (CDOProtocolConstants.QUERY_LANGUAGE_XREFS.equals(language))
    {
      return new XRefsQueryHandler();
    }

    IStoreAccessor storeAccessor = StoreThreadLocal.getAccessor();
    if (storeAccessor != null)
    {
      IQueryHandler handler = storeAccessor.getQueryHandler(info);
      if (handler != null)
      {
        return handler;
      }
    }

    if (queryHandlerProvider == null)
    {
      queryHandlerProvider = new ContainerQueryHandlerProvider(IPluginContainer.INSTANCE);
    }

    IQueryHandler handler = queryHandlerProvider.getQueryHandler(info);
    if (handler != null)
    {
      return handler;
    }

    return null;
  }

  public Object[] getElements()
  {
    final Object[] elements = { packageRegistry, branchManager, revisionManager, sessionManager, queryManager,
        commitManager, commitInfoManager, getLockingManager(), store };
    return elements;
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  /**
   * @since 2.0
   */
  public long getCreationTime()
  {
    return store.getCreationTime();
  }

  /**
   * @since 2.0
   */
  public void validateTimeStamp(long timeStamp) throws IllegalArgumentException
  {
    long creationTimeStamp = getCreationTime();
    if (timeStamp < creationTimeStamp)
    {
      throw new IllegalArgumentException(
          MessageFormat
              .format(
                  "timeStamp ({0}) < repository creation time ({1})", CDOCommonUtil.formatTimeStamp(timeStamp), CDOCommonUtil.formatTimeStamp(creationTimeStamp))); //$NON-NLS-1$
    }

    long currentTimeStamp = getTimeStamp();
    if (timeStamp > currentTimeStamp)
    {
      throw new IllegalArgumentException(
          MessageFormat
              .format(
                  "timeStamp ({0}) > current time ({1})", CDOCommonUtil.formatTimeStamp(timeStamp), CDOCommonUtil.formatTimeStamp(currentTimeStamp))); //$NON-NLS-1$
    }
  }

  public long getTimeStamp()
  {
    return System.currentTimeMillis();
  }

  public Set<Handler> getHandlers()
  {
    Set<Handler> handlers = new HashSet<Handler>();

    synchronized (readAccessHandlers)
    {
      handlers.addAll(readAccessHandlers);
    }

    synchronized (writeAccessHandlers)
    {
      handlers.addAll(writeAccessHandlers);
    }

    return handlers;
  }

  /**
   * @since 2.0
   */
  public void addHandler(Handler handler)
  {
    if (handler instanceof ReadAccessHandler)
    {
      synchronized (readAccessHandlers)
      {
        if (!readAccessHandlers.contains(handler))
        {
          readAccessHandlers.add((ReadAccessHandler)handler);
        }
      }
    }

    if (handler instanceof WriteAccessHandler)
    {
      synchronized (writeAccessHandlers)
      {
        if (!writeAccessHandlers.contains(handler))
        {
          writeAccessHandlers.add((WriteAccessHandler)handler);
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  public void removeHandler(Handler handler)
  {
    if (handler instanceof ReadAccessHandler)
    {
      synchronized (readAccessHandlers)
      {
        readAccessHandlers.remove(handler);
      }
    }

    if (handler instanceof WriteAccessHandler)
    {
      synchronized (writeAccessHandlers)
      {
        writeAccessHandlers.remove(handler);
      }
    }
  }

  /**
   * @since 2.0
   */
  public void notifyReadAccessHandlers(InternalSession session, CDORevision[] revisions,
      List<CDORevision> additionalRevisions)
  {
    ReadAccessHandler[] handlers;
    synchronized (readAccessHandlers)
    {
      int size = readAccessHandlers.size();
      if (size == 0)
      {
        return;
      }

      handlers = readAccessHandlers.toArray(new ReadAccessHandler[size]);
    }

    for (ReadAccessHandler handler : handlers)
    {
      // Do *not* protect against unchecked exceptions from handlers!
      handler.handleRevisionsBeforeSending(session, revisions, additionalRevisions);
    }
  }

  public void notifyWriteAccessHandlers(ITransaction transaction, IStoreAccessor.CommitContext commitContext,
      boolean beforeCommit, OMMonitor monitor)
  {
    WriteAccessHandler[] handlers;
    synchronized (writeAccessHandlers)
    {
      int size = writeAccessHandlers.size();
      if (size == 0)
      {
        return;
      }

      handlers = writeAccessHandlers.toArray(new WriteAccessHandler[size]);
    }

    try
    {
      monitor.begin(handlers.length);
      for (WriteAccessHandler handler : handlers)
      {
        try
        {
          if (beforeCommit)
          {
            handler.handleTransactionBeforeCommitting(transaction, commitContext, monitor.fork());
          }
          else
          {
            handler.handleTransactionAfterCommitted(transaction, commitContext, monitor.fork());
          }
        }
        catch (RuntimeException ex)
        {
          if (!beforeCommit)
          {
            OM.LOG.error(ex);
          }
          else
          {
            // Do *not* protect against unchecked exceptions from handlers on before case!
            throw ex;
          }
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public void rollbackWriteAccessHandlers(ITransaction transaction, IStoreAccessor.CommitContext commitContext)
  {
    WriteAccessHandler[] handlers;
    synchronized (writeAccessHandlers)
    {
      int size = writeAccessHandlers.size();
      if (size == 0)
      {
        return;
      }

      handlers = writeAccessHandlers.toArray(new WriteAccessHandler[size]);
    }

    for (WriteAccessHandler handler : handlers)
    {
      try
      {
        handler.handleTransactionRollback(transaction, commitContext);
      }
      catch (RuntimeException ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  public void setInitialPackages(EPackage... initialPackages)
  {
    checkInactive();
    this.initialPackages = initialPackages;
  }

  public CDOReplicationInfo replicateRaw(CDODataOutput out, int lastReplicatedBranchID, long lastReplicatedCommitTime)
      throws IOException
  {
    final int fromBranchID = lastReplicatedBranchID + 1;
    final int toBranchID = getStore().getLastBranchID();

    final long fromCommitTime = lastReplicatedCommitTime + 1L;
    final long toCommitTime = getStore().getLastCommitTime();

    out.writeInt(toBranchID);
    out.writeLong(toCommitTime);

    IStoreAccessor.Raw accessor = (IStoreAccessor.Raw)StoreThreadLocal.getAccessor();
    accessor.rawExport(out, fromBranchID, toBranchID, fromCommitTime, toCommitTime);

    return new CDOReplicationInfo()
    {
      public int getLastReplicatedBranchID()
      {
        return toBranchID;
      }

      public long getLastReplicatedCommitTime()
      {
        return toCommitTime;
      }

      public String[] getLockAreaIDs()
      {
        return null; // TODO (CD) Raw replication of lockAreas
      }
    };
  }

  public void replicate(CDOReplicationContext context)
  {
    int startID = context.getLastReplicatedBranchID() + 1;
    branchManager.getBranches(startID, 0, context);

    long startTime = context.getLastReplicatedCommitTime();
    commitInfoManager.getCommitInfos(null, startTime + 1L, CDOBranchPoint.UNSPECIFIED_DATE, context);

    getLockingManager().getLockAreas(null, context);
  }

  public CDOChangeSetData getChangeSet(CDOBranchPoint startPoint, CDOBranchPoint endPoint)
  {
    CDOChangeSetSegment[] segments = CDOChangeSetSegment.createFrom(startPoint, endPoint);

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    Set<CDOID> ids = accessor.readChangeSet(new Monitor(), /* no model restriction */new String[0], segments);

    return CDORevisionUtil.createChangeSetData(ids, startPoint, endPoint, revisionManager);
  }

  public Set<CDOID> getMergeData(CDORevisionAvailabilityInfo targetInfo, CDORevisionAvailabilityInfo sourceInfo,
      CDORevisionAvailabilityInfo targetBaseInfo, CDORevisionAvailabilityInfo sourceBaseInfo, String[] nsURIs,
      OMMonitor monitor)
  {
    CDOBranchPoint target = targetInfo.getBranchPoint();
    CDOBranchPoint source = sourceInfo.getBranchPoint();

    monitor.begin(5);

    try
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      Set<CDOID> ids = new HashSet<CDOID>();

      if (targetBaseInfo == null && sourceBaseInfo == null)
      {
        if (CDOBranchUtil.isContainedBy(source, target))
        {
          ids.addAll(accessor.readChangeSet(monitor.fork(), nsURIs, CDOChangeSetSegment.createFrom(source, target)));
        }
        else if (CDOBranchUtil.isContainedBy(target, source))
        {
          ids.addAll(accessor.readChangeSet(monitor.fork(), nsURIs, CDOChangeSetSegment.createFrom(target, source)));
        }
        else
        {
          CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(target, source);
          ids.addAll(accessor.readChangeSet(monitor.fork(), nsURIs, CDOChangeSetSegment.createFrom(ancestor, target)));
          ids.addAll(accessor.readChangeSet(monitor.fork(), nsURIs, CDOChangeSetSegment.createFrom(ancestor, source)));
        }
      }
      else
      {
        CDORevisionAvailabilityInfo sourceBaseInfoToUse = sourceBaseInfo == null ? targetBaseInfo : sourceBaseInfo;

        ids.addAll(accessor.readChangeSet(monitor.fork(), nsURIs,
            CDOChangeSetSegment.createFrom(targetBaseInfo.getBranchPoint(), target)));

        ids.addAll(accessor.readChangeSet(monitor.fork(), nsURIs,
            CDOChangeSetSegment.createFrom(sourceBaseInfoToUse.getBranchPoint(), source)));
      }

      loadMergeData(ids, targetInfo, monitor.fork());
      loadMergeData(ids, sourceInfo, monitor.fork());

      if (targetBaseInfo != null)
      {
        loadMergeData(ids, targetBaseInfo, monitor.fork());
      }

      if (sourceBaseInfo != null)
      {
        loadMergeData(ids, sourceBaseInfo, monitor.fork());
      }

      return ids;
    }
    finally
    {
      monitor.done();
    }
  }

  private void loadMergeData(Set<CDOID> ids, CDORevisionAvailabilityInfo info, OMMonitor monitor)
  {
    int size = ids.size();
    monitor.begin(size);

    try
    {
      final List<CDOID> revisionsToLoad = new ArrayList<>();	
      CDOBranchPoint branchPoint = info.getBranchPoint();
      for (CDOID id : ids)
      {
        if (info.containsRevision(id))
        {
          info.removeRevision(id);
          monitor.worked();
        }
        else
        {
          revisionsToLoad.add(id);	
        }

      }
      
      List<CDORevision> loadedRevisions = getRevisionFromBranch(revisionsToLoad, branchPoint);
      final Map<CDOID, CDORevision> loadedRevisionsById = new HashMap<>();
      for (CDORevision rev : loadedRevisions) {
    	  if (rev != null) {
    		  loadedRevisionsById.put(rev.getID(), rev);
    	  }
      }
      
      for (CDOID id : revisionsToLoad) {
    	  InternalCDORevision revision = (InternalCDORevision) loadedRevisionsById.get(id);
          if (revision != null)
          {
            ensureChunks(revision);
            info.addRevision(revision);
          }
          else
          {
            info.removeRevision(id);
          }
          
          monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private List<CDORevision> getRevisionFromBranch(List<CDOID> ids, CDOBranchPoint branchPoint)
  {
    return revisionManager.getRevisions(ids, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
  }

  public void queryLobs(List<byte[]> ids)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.queryLobs(ids);
  }

  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.handleLobs(fromTime, toTime, handler);
  }

  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.loadLob(id, out);
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime,
      final CDORevisionHandler handler)
  {
    CDORevisionHandler wrapper = handler;
    if (!exactBranch && !branch.isMainBranch())
    {
      if (exactTime && timeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
      {
        throw new IllegalArgumentException("Time stamp must be specified if exactBranch==false and exactTime==true");
      }

      wrapper = new CDORevisionHandler()
      {
        private Set<CDOID> handled = new HashSet<CDOID>();

        public boolean handleRevision(CDORevision revision)
        {
          CDOID id = revision.getID();
          if (handled.add(id))
          {
            return handler.handleRevision(revision);
          }

          return true;
        }
      };
    }

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    while (branch != null)
    {
      accessor.handleRevisions(eClass, branch, timeStamp, exactTime, wrapper);
      if (exactBranch)
      {
        break;
      }

      CDOBranchPoint base = branch.getBase();
      branch = base.getBranch();
      timeStamp = base.getTimeStamp();
    }
  }

  public static List<Object> revisionKeysToObjects(List<CDORevisionKey> revisionKeys, CDOBranch viewedBranch,
      boolean isSupportingBranches)
  {
    List<Object> lockables = new ArrayList<Object>();
    for (CDORevisionKey revKey : revisionKeys)
    {
      CDOID id = revKey.getID();
      if (isSupportingBranches)
      {
        lockables.add(CDOIDUtil.createIDAndBranch(id, viewedBranch));
      }
      else
      {
        lockables.add(id);
      }
    }
    return lockables;
  }

  public LockObjectsResult lock(InternalView view, LockType lockType, List<CDORevisionKey> revKeys, boolean recursive,
      long timeout)
  {
    List<Object> lockables = revisionKeysToObjects(revKeys, view.getBranch(), isSupportingBranches());
    return lock(view, lockType, lockables, revKeys, recursive, timeout);
  }

  protected LockObjectsResult lock(InternalView view, LockType type, List<Object> lockables,
      List<CDORevisionKey> loadedRevs, boolean recursive, long timeout)
  {
    List<LockState<Object, IView>> newLockStates = null;
    try
    {
      newLockStates = getLockingManager().lock2(true, type, view, lockables, recursive, timeout);
    }
    catch (TimeoutRuntimeException ex)
    {
      return new LockObjectsResult(false, true, false, 0, new CDORevisionKey[0], new CDOLockState[0], getTimeStamp());
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }

    long[] requiredTimestamp = { 0L };
    CDORevisionKey[] staleRevisionsArray = null;

    try
    {
      staleRevisionsArray = checkStaleRevisions(view, loadedRevs, lockables, type, requiredTimestamp);
    }
    catch (IllegalArgumentException e)
    {
      getLockingManager().unlock2(true, type, view, lockables, recursive);
      throw e;
    }

    // If some of the clients' revisions are stale and it has passiveUpdates disabled,
    // then the locks are useless so we release them and report the stale revisions
    //
    InternalSession session = view.getSession();
    boolean staleNoUpdate = staleRevisionsArray.length > 0 && !session.isPassiveUpdateEnabled();
    if (staleNoUpdate)
    {
      getLockingManager().unlock2(true, type, view, lockables, recursive);
      return new LockObjectsResult(false, false, false, requiredTimestamp[0], staleRevisionsArray, new CDOLockState[0],
          getTimeStamp());
    }

    CDOLockState[] cdoLockStates = toCDOLockStates(newLockStates);
    sendLockNotifications(view, Operation.LOCK, type, cdoLockStates);

    boolean waitForUpdate = staleRevisionsArray.length > 0;
    return new LockObjectsResult(true, false, waitForUpdate, requiredTimestamp[0], staleRevisionsArray, cdoLockStates,
        getTimeStamp());
  }

  private CDORevisionKey[] checkStaleRevisions(InternalView view, List<CDORevisionKey> revisionKeys,
      List<Object> objectsToLock, LockType lockType, long[] requiredTimestamp)
  {
    List<CDORevisionKey> staleRevisions = new LinkedList<CDORevisionKey>();
    if (revisionKeys != null)
    {
      InternalCDORevisionManager revManager = getRevisionManager();
      CDOBranch viewedBranch = view.getBranch();
      for (CDORevisionKey revKey : revisionKeys)
      {
        CDOID id = revKey.getID();
        InternalCDORevision rev = revManager.getRevision(id, viewedBranch.getHead(), CDORevision.UNCHUNKED,
            CDORevision.DEPTH_NONE, true);

        if (rev == null)
        {
          throw new IllegalArgumentException(String.format("Object %s not found in branch %s (possibly detached)", id,
              viewedBranch));
        }

        if (!revKey.equals(rev))
        {
          staleRevisions.add(revKey);
          requiredTimestamp[0] = Math.max(requiredTimestamp[0], rev.getTimeStamp());
        }
      }
    }

    // Convert the list to an array, to satisfy the API later
    //
    CDORevisionKey[] staleRevisionsArray = new CDORevisionKey[staleRevisions.size()];
    staleRevisions.toArray(staleRevisionsArray);

    return staleRevisionsArray;
  }

  private void sendLockNotifications(IView view, Operation operation, LockType lockType, CDOLockState[] cdoLockStates)
  {
    long timestamp = getTimeStamp();
    CDOLockChangeInfo lockChangeInfo = CDOLockUtil.createLockChangeInfo(timestamp, view, view.getBranch(), operation,
        lockType, cdoLockStates);
    getSessionManager().sendLockNotification((InternalSession)view.getSession(), lockChangeInfo);
  }

  // TODO (CD) This doesn't really belong here.. but getting it into CDOLockUtil isn't possible
  public static CDOLockState[] toCDOLockStates(List<LockState<Object, IView>> lockStates)
  {
    CDOLockState[] cdoLockStates = new CDOLockState[lockStates.size()];
    int i = 0;

    for (LockState<Object, ? extends CDOCommonView> lockState : lockStates)
    {
      CDOLockState cdoLockState = CDOLockUtil.createLockState(lockState);
      cdoLockStates[i++] = cdoLockState;
    }

    return cdoLockStates;
  }

  public UnlockObjectsResult unlock(InternalView view, LockType lockType, List<CDOID> objectIDs, boolean recursive)
  {
    List<Object> unlockables = null;
    if (objectIDs != null)
    {
      unlockables = new ArrayList<Object>(objectIDs.size());
      CDOBranch branch = view.getBranch();
      for (CDOID id : objectIDs)
      {
        Object key = supportingBranches ? CDOIDUtil.createIDAndBranch(id, branch) : id;
        unlockables.add(key);
      }
    }

    return doUnlock(view, lockType, unlockables, recursive);
  }

  protected UnlockObjectsResult doUnlock(InternalView view, LockType lockType, List<Object> unlockables,
      boolean recursive)
  {
    List<LockState<Object, IView>> newLockStates = null;
    if (lockType == null) // Signals an unlock-all operation
    {
      newLockStates = getLockingManager().unlock2(true, view);
    }
    else
    {
      newLockStates = getLockingManager().unlock2(true, lockType, view, unlockables, recursive);
    }

    long timestamp = getTimeStamp();
    CDOLockState[] cdoLockStates = toCDOLockStates(newLockStates);
    sendLockNotifications(view, Operation.UNLOCK, lockType, cdoLockStates);

    return new UnlockObjectsResult(cdoLockStates, timestamp);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Repository[{0}]", name); //$NON-NLS-1$
  }

  public boolean isSkipInitialization()
  {
    return skipInitialization;
  }

  public void setSkipInitialization(boolean skipInitialization)
  {
    this.skipInitialization = skipInitialization;
  }

  protected void initProperties()
  {
    String valueAudits = properties.get(Props.SUPPORTING_AUDITS);
    if (valueAudits != null)
    {
      supportingAudits = Boolean.valueOf(valueAudits);
    }
    else
    {
      supportingAudits = store.getRevisionTemporality() == IStore.RevisionTemporality.AUDITING;
    }

    String valueBranches = properties.get(Props.SUPPORTING_BRANCHES);
    if (valueBranches != null)
    {
      supportingBranches = Boolean.valueOf(valueBranches);
    }
    else
    {
      supportingBranches = store.getRevisionParallelism() == IStore.RevisionParallelism.BRANCHING;
    }

    String valueEcore = properties.get(Props.SUPPORTING_ECORE);
    if (valueEcore != null)
    {
      supportingEcore = Boolean.valueOf(valueEcore);
    }

    String valueIntegrity = properties.get(Props.ENSURE_REFERENTIAL_INTEGRITY);
    if (valueIntegrity != null)
    {
      ensuringReferentialIntegrity = Boolean.valueOf(valueIntegrity);
    }

    String valueIDLocation = properties.get(Props.ID_GENERATION_LOCATION);
    if (valueIDLocation != null)
    {
      idGenerationLocation = IDGenerationLocation.valueOf(valueIDLocation);
    }

    if (idGenerationLocation == null)
    {
      idGenerationLocation = IDGenerationLocation.STORE;
    }
  }

  public void initSystemPackages()
  {
    IStoreAccessor writer = store.getWriter(null);
    StoreThreadLocal.setAccessor(writer);

    try
    {
      List<InternalCDOPackageUnit> units = new ArrayList<InternalCDOPackageUnit>();
      units.add(initSystemPackage(EcorePackage.eINSTANCE));
      units.add(initSystemPackage(EresourcePackage.eINSTANCE));
      units.add(initSystemPackage(EtypesPackage.eINSTANCE));

      if (initialPackages != null)
      {
        for (EPackage initialPackage : initialPackages)
        {
          if (!packageRegistry.containsKey(initialPackage.getNsURI()))
          {
            units.add(initSystemPackage(initialPackage));
          }
        }
      }

      InternalCDOPackageUnit[] systemUnits = units.toArray(new InternalCDOPackageUnit[units.size()]);
      writer.writePackageUnits(systemUnits, new Monitor());
      writer.commit(new Monitor());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  protected InternalCDOPackageUnit initSystemPackage(EPackage ePackage)
  {
    EMFUtil.registerPackage(ePackage, packageRegistry);
    InternalCDOPackageInfo packageInfo = packageRegistry.getPackageInfo(ePackage);

    InternalCDOPackageUnit packageUnit = packageInfo.getPackageUnit();
    packageUnit.setTimeStamp(store.getCreationTime());
    packageUnit.setState(CDOPackageUnit.State.LOADED);
    return packageUnit;
  }

  public void initMainBranch(InternalCDOBranchManager branchManager, long timeStamp)
  {
    branchManager.initMainBranch(false, timeStamp);
  }

  protected void initRootResource()
  {
    CDOBranchPoint head = branchManager.getMainBranch().getHead();

    CDORevisionFactory factory = getRevisionManager().getFactory();
    InternalCDORevision rootResource = (InternalCDORevision)factory
        .createRevision(EresourcePackage.Literals.CDO_RESOURCE);

    rootResource.setBranchPoint(head);
    rootResource.setContainerID(CDOID.NULL);
    rootResource.setContainingFeatureID(0);

    CDOID id = createRootResourceID();
    rootResource.setID(id);
    rootResource.setResourceID(id);

    InternalSession session = getSessionManager().openSession(null);
    InternalTransaction transaction = session.openTransaction(1, head);
    InternalCommitContext commitContext = new TransactionCommitContext(transaction)
    {
      @Override
      protected long[] createTimeStamp(OMMonitor monitor)
      {
        InternalRepository repository = getTransaction().getSession().getManager().getRepository();
        return repository.forceCommitTimeStamp(store.getCreationTime(), monitor);
      }

      @Override
      public String getUserID()
      {
        return SYSTEM_USER_ID;
      }

      @Override
      public String getCommitComment()
      {
        return "<initialize>"; //$NON-NLS-1$
      }
    };

    commitContext.setNewObjects(new InternalCDORevision[] { rootResource });
    commitContext.preWrite();

    commitContext.write(new Monitor());
    commitContext.commit(new Monitor());

    String rollbackMessage = commitContext.getRollbackMessage();
    if (rollbackMessage != null)
    {
      throw new TransactionException(rollbackMessage);
    }

    rootResourceID = id instanceof CDOIDTemp ? commitContext.getIDMappings().get(id) : id;

    commitContext.postCommit(true);
    session.close();
  }

  protected CDOID createRootResourceID()
  {
    if (getIDGenerationLocation() == IDGenerationLocation.STORE)
    {
      return CDOIDUtil.createTempObject(1);
    }

    return CDOIDGenerator.UUID.generateCDOID(null);
  }

  protected void readRootResource()
  {
    IStoreAccessor reader = store.getReader(null);
    StoreThreadLocal.setAccessor(reader);

    try
    {
      CDOBranchPoint head = branchManager.getMainBranch().getHead();
      rootResourceID = reader.readResourceID(CDOID.NULL, null, head);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  protected void readPackageUnits()
  {
    IStoreAccessor reader = store.getReader(null);
    StoreThreadLocal.setAccessor(reader);

    try
    {
      Collection<InternalCDOPackageUnit> packageUnits = reader.readPackageUnits();
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        packageRegistry.putPackageUnit(packageUnit);
      }
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(store, "store"); //$NON-NLS-1$
    checkState(!StringUtil.isEmpty(name), "name is empty"); //$NON-NLS-1$
    checkState(packageRegistry, "packageRegistry"); //$NON-NLS-1$
    checkState(sessionManager, "sessionManager"); //$NON-NLS-1$
    checkState(branchManager, "branchManager"); //$NON-NLS-1$
    checkState(revisionManager, "revisionManager"); //$NON-NLS-1$
    checkState(queryManager, "queryManager"); //$NON-NLS-1$
    checkState(commitInfoManager, "commitInfoManager"); //$NON-NLS-1$
    checkState(commitManager, "commitManager"); //$NON-NLS-1$
    checkState(getLockingManager(), "lockingManager"); //$NON-NLS-1$

    packageRegistry.setReplacingDescriptors(true);
    packageRegistry.setPackageProcessor(this);
    packageRegistry.setPackageLoader(this);

    branchManager.setBranchLoader(this);
    branchManager.setTimeProvider(this);

    revisionManager.setRevisionLoader(this);
    sessionManager.setRepository(this);
    queryManager.setRepository(this);
    commitInfoManager.setCommitInfoLoader(this);
    commitManager.setRepository(this);
    getLockingManager().setRepository(this);
    store.setRepository(this);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    initProperties();
    if (idGenerationLocation == IDGenerationLocation.CLIENT && !(store instanceof CanHandleClientAssignedIDs))
    {
      throw new IllegalStateException("Store can not handle client assigned IDs: " + store);
    }

    store.setRevisionTemporality(supportingAudits ? IStore.RevisionTemporality.AUDITING
        : IStore.RevisionTemporality.NONE);
    store.setRevisionParallelism(supportingBranches ? IStore.RevisionParallelism.BRANCHING
        : IStore.RevisionParallelism.NONE);
    revisionManager.setSupportingAudits(supportingAudits);
    revisionManager.setSupportingBranches(supportingBranches);

    LifecycleUtil.activate(store);
    LifecycleUtil.activate(packageRegistry);
    LifecycleUtil.activate(sessionManager);
    LifecycleUtil.activate(revisionManager);
    LifecycleUtil.activate(branchManager);
    LifecycleUtil.activate(queryManager);
    LifecycleUtil.activate(commitInfoManager);
    LifecycleUtil.activate(commitManager);
    LifecycleUtil.activate(queryHandlerProvider);

    if (!skipInitialization)
    {
      long lastCommitTimeStamp = Math.max(store.getCreationTime(), store.getLastCommitTime());
      timeStampAuthority.setLastFinishedTimeStamp(lastCommitTimeStamp);
      initMainBranch(branchManager, lastCommitTimeStamp);

      if (store.isFirstStart())
      {
        initSystemPackages();
        initRootResource();
      }
      else
      {
        readPackageUnits();
        readRootResource();
      }

      // This check does not work for CDOWorkspace:
      // if (CDOIDUtil.isNull(rootResourceID))
      // {
      // throw new IllegalStateException("Root resource ID is null");
      // }
    }

    LifecycleUtil.activate(getLockingManager()); // Needs an initialized main branch / branch manager
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(getLockingManager());
    LifecycleUtil.deactivate(queryHandlerProvider);
    LifecycleUtil.deactivate(commitManager);
    LifecycleUtil.deactivate(commitInfoManager);
    LifecycleUtil.deactivate(queryManager);
    LifecycleUtil.deactivate(revisionManager);
    LifecycleUtil.deactivate(sessionManager);
    LifecycleUtil.deactivate(store);
    LifecycleUtil.deactivate(branchManager);
    LifecycleUtil.deactivate(packageRegistry);
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static class Default extends Repository
  {
    public Default()
    {
    }

    @Override
    protected void doBeforeActivate() throws Exception
    {
      if (getPackageRegistry(false) == null)
      {
        setPackageRegistry(createPackageRegistry());
      }

      if (getSessionManager() == null)
      {
        setSessionManager(createSessionManager());
      }

      if (getBranchManager() == null)
      {
        setBranchManager(createBranchManager());
      }

      if (getRevisionManager() == null)
      {
        setRevisionManager(createRevisionManager());
      }

      if (getQueryManager() == null)
      {
        setQueryManager(createQueryManager());
      }

      if (getCommitInfoManager() == null)
      {
        setCommitInfoManager(createCommitInfoManager());
      }

      if (getCommitManager() == null)
      {
        setCommitManager(createCommitManager());
      }

      if (getLockManager() == null)
      {
        setLockingManager(createLockManager());
      }

      super.doBeforeActivate();
    }

    protected InternalCDOPackageRegistry createPackageRegistry()
    {
      return new CDOPackageRegistryImpl();
    }

    protected InternalSessionManager createSessionManager()
    {
      return new SessionManager();
    }

    protected InternalCDOBranchManager createBranchManager()
    {
      return CDOBranchUtil.createBranchManager();
    }

    protected InternalCDORevisionManager createRevisionManager()
    {
      return (InternalCDORevisionManager)CDORevisionUtil.createRevisionManager();
    }

    protected InternalQueryManager createQueryManager()
    {
      return new QueryManager();
    }

    protected InternalCDOCommitInfoManager createCommitInfoManager()
    {
      return CDOCommitInfoUtil.createCommitInfoManager();
    }

    protected InternalCommitManager createCommitManager()
    {
      return new CommitManager();
    }

    @Deprecated
    protected InternalLockManager createLockManager()
    {
      return createLockingManager();
    }

    public LockingManager createLockingManager()
    {
      return new LockingManager();
    }
  }
}
