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
package org.eclipse.emf.cdo.internal.server.syncing;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeKindCache;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalRepositorySynchronizer;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.spi.server.SyncingUtil;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.IndexedList;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockObjectsResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * TODO:
 * <ul>
 * <li>Handle new package units that had been committed during offline (testDisconnectAndCommitAndMergeWithNewPackages).
 * <li>Make CDOIDs of new objects temporary when merging out of temp branch.
 * <li>Provide custom branching strategies.
 * <li>Consider non-auditing masters.
 * <li>Test out-of-order commits.
 * <li>Don't create branches table if branching not supported.
 * <li>Implement raw replication for NUMERIC and DECIMAL.
 * <li>Notify new branches during raw replication.
 * </ul>
 *
 * @author Eike Stepper
 */
public abstract class SynchronizableRepository extends Repository.Default implements InternalSynchronizableRepository
{
  protected static final CDOCommonRepository.Type MASTER = CDOCommonRepository.Type.MASTER;

  protected static final CDOCommonRepository.Type BACKUP = CDOCommonRepository.Type.BACKUP;

  protected static final CDOCommonRepository.Type CLONE = CDOCommonRepository.Type.CLONE;

  protected static final CDOCommonRepository.State INITIAL = CDOCommonRepository.State.INITIAL;

  protected static final CDOCommonRepository.State OFFLINE = CDOCommonRepository.State.OFFLINE;

  protected static final CDOCommonRepository.State SYNCING = CDOCommonRepository.State.SYNCING;

  protected static final CDOCommonRepository.State ONLINE = CDOCommonRepository.State.ONLINE;

  private static final String PROP_LAST_REPLICATED_BRANCH_ID = "org.eclipse.emf.cdo.server.lastReplicatedBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_REPLICATED_COMMIT_TIME = "org.eclipse.emf.cdo.server.lastReplicatedCommitTime"; //$NON-NLS-1$

  private static final String PROP_GRACEFULLY_SHUT_DOWN = "org.eclipse.emf.cdo.server.gracefullyShutDown"; //$NON-NLS-1$

  private InternalRepositorySynchronizer synchronizer;

  private InternalSession replicatorSession;

  private int lastReplicatedBranchID = CDOBranch.MAIN_BRANCH_ID;

  private long lastReplicatedCommitTime = CDOBranchPoint.UNSPECIFIED_DATE;

  private int lastTransactionID;

  private ReadLock writeThroughCommitLock;

  private WriteLock handleCommitInfoLock;

  public SynchronizableRepository()
  {
    ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    writeThroughCommitLock = rwLock.readLock();
    handleCommitInfoLock = rwLock.writeLock();
  }

  public InternalRepositorySynchronizer getSynchronizer()
  {
    return synchronizer;
  }

  public void setSynchronizer(InternalRepositorySynchronizer synchronizer)
  {
    checkInactive();
    this.synchronizer = synchronizer;
  }

  public InternalSession getReplicatorSession()
  {
    return replicatorSession;
  }

  @Override
  public Object[] getElements()
  {
    List<Object> list = Arrays.asList(super.getElements());
    list.add(synchronizer);
    return list.toArray();
  }

  public int getLastReplicatedBranchID()
  {
    return lastReplicatedBranchID;
  }

  public void setLastReplicatedBranchID(int lastReplicatedBranchID)
  {
    if (this.lastReplicatedBranchID < lastReplicatedBranchID)
    {
      this.lastReplicatedBranchID = lastReplicatedBranchID;
    }
  }

  public long getLastReplicatedCommitTime()
  {
    return lastReplicatedCommitTime;
  }

  public void setLastReplicatedCommitTime(long lastReplicatedCommitTime)
  {
    if (this.lastReplicatedCommitTime < lastReplicatedCommitTime)
    {
      this.lastReplicatedCommitTime = lastReplicatedCommitTime;
    }
  }

  public String[] getLockAreaIDs()
  {
    try
    {
      StoreThreadLocal.setSession(replicatorSession);
      final List<String> areaIDs = new LinkedList<String>();
      getLockingManager().getLockAreas(null, new LockArea.Handler()
      {
        public boolean handleLockArea(LockArea area)
        {
          areaIDs.add(area.getDurableLockingID());
          return true;
        }
      });
      return areaIDs.toArray(new String[areaIDs.size()]);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  public void handleBranch(CDOBranch branch)
  {
    if (branch.isLocal())
    {
      return;
    }

    int branchID = branch.getID();
    String name = branch.getName();

    CDOBranchPoint base = branch.getBase();
    InternalCDOBranch baseBranch = (InternalCDOBranch)base.getBranch();
    long baseTimeStamp = base.getTimeStamp();

    InternalCDOBranchManager branchManager = getBranchManager();
    branchManager.createBranch(branchID, name, baseBranch, baseTimeStamp);
    setLastReplicatedBranchID(branchID);
  }

  public void handleCommitInfo(CDOCommitInfo commitInfo)
  {
    CDOBranch branch = commitInfo.getBranch();
    if (branch.isLocal())
    {
      return;
    }

    long timeStamp = commitInfo.getTimeStamp();
    CDOBranchPoint head = branch.getHead();

    InternalTransaction transaction = replicatorSession.openTransaction(++lastTransactionID, head);
    ReplicatorCommitContext commitContext = new ReplicatorCommitContext(transaction, commitInfo);
    commitContext.preWrite();
    boolean success = false;

    try
    {
      handleCommitInfoLock.lock();

      commitContext.write(new Monitor());
      commitContext.commit(new Monitor());

      setLastCommitTimeStamp(timeStamp);
      setLastReplicatedCommitTime(timeStamp);
      success = true;
    }
    finally
    {
      handleCommitInfoLock.unlock();
      commitContext.postCommit(success);
      transaction.close();
    }
  }

  public void handleLockChangeInfo(CDOLockChangeInfo lockChangeInfo)
  {
    CDOLockOwner owner = lockChangeInfo.getLockOwner();
    String durableLockingID = owner.getDurableLockingID();
    CDOBranch viewedBranch = lockChangeInfo.getBranch();
    InternalLockManager lockManager = getLockingManager();
    LockType lockType = lockChangeInfo.getLockType();

    InternalView view = null;

    try
    {
      view = SyncingUtil.openViewWithLockArea(replicatorSession, lockManager, viewedBranch, durableLockingID);
      List<Object> lockables = new LinkedList<Object>();

      for (CDOLockState lockState : lockChangeInfo.getLockStates())
      {
        lockables.add(lockState.getLockedObject());
      }

      if (lockChangeInfo.getOperation() == Operation.LOCK)
      {
        // If we can't lock immediately, there's a conflict, which means we're in big
        // trouble: somehow locks were obtained on the clone but not on the master. What to do?
        // TODO (CD) Consider this problem further
        long timeout = 0;

        super.lock(view, lockType, lockables, null, false, timeout);
      }
      else if (lockChangeInfo.getOperation() == Operation.UNLOCK)
      {
        super.doUnlock(view, lockType, lockables, false);
      }
      else
      {
        throw new IllegalStateException("Unexpected: " + lockChangeInfo.getOperation());
      }
    }
    finally
    {
      LifecycleUtil.deactivate(view);
    }
  }

  public boolean handleLockArea(LockArea area)
  {
    try
    {
      StoreThreadLocal.setSession(replicatorSession);
      getLockingManager().updateLockArea(area);

      getSessionManager().sendLockNotification(null, CDOLockUtil.createLockChangeInfo());
      return true;
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  public void replicateRaw(CDODataInput in, OMMonitor monitor) throws IOException
  {
    try
    {
      int fromBranchID = lastReplicatedBranchID + 1;
      int toBranchID = in.readInt();
      long fromCommitTime = lastReplicatedCommitTime + 1L;
      long toCommitTime = in.readLong();

      StoreThreadLocal.setSession(replicatorSession);
      IStoreAccessor.Raw accessor = (IStoreAccessor.Raw)StoreThreadLocal.getAccessor();
      accessor.rawImport(in, fromBranchID, toBranchID, fromCommitTime, toCommitTime, monitor);

      replicateRawReviseRevisions();
      replicateRawReloadLocks();
      replicateRawNotifyClients(lastReplicatedCommitTime, toCommitTime);

      setLastReplicatedBranchID(toBranchID);
      setLastReplicatedCommitTime(toCommitTime);
      setLastCommitTimeStamp(toCommitTime);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  public void goOnline()
  {
    if (getState() == OFFLINE)
    {
      LifecycleUtil.activate(synchronizer);
      // Do not set the state to ONLINE yet; the synchronizer will set it to SYNCING first,
      // and then to ONLINE after a succesful replication.
    }
  }

  public void goOffline()
  {
    if (getState() != OFFLINE)
    {
      LifecycleUtil.deactivate(synchronizer);
      setState(OFFLINE);
    }
  }

  private void replicateRawReviseRevisions()
  {
    InternalCDORevisionCache cache = getRevisionManager().getCache();
    for (CDORevision revision : cache.getCurrentRevisions())
    {
      cache.removeRevision(revision.getID(), revision);
    }
  }

  private void replicateRawReloadLocks()
  {
    getLockingManager().reloadLocks();
  }

  private void replicateRawNotifyClients(long fromCommitTime, long toCommitTime)
  {
    InternalCDOCommitInfoManager manager = getCommitInfoManager();
    InternalSessionManager sessionManager = getSessionManager();

    Map<CDOBranch, TimeRange> branches = replicateRawGetBranches(fromCommitTime, toCommitTime);
    for (Entry<CDOBranch, TimeRange> entry : branches.entrySet())
    {
      CDOBranch branch = entry.getKey();
      TimeRange range = entry.getValue();
      fromCommitTime = range.getTime1();
      toCommitTime = range.getTime2();

      CDOBranchPoint startPoint = branch.getPoint(fromCommitTime);
      CDOBranchPoint endPoint = branch.getPoint(toCommitTime);
      CDOChangeSetData changeSet = getChangeSet(startPoint, endPoint);

      List<CDOPackageUnit> newPackages = Collections.emptyList(); // TODO Notify about new packages
      List<CDOIDAndVersion> newObjects = changeSet.getNewObjects();
      List<CDORevisionKey> changedObjects = changeSet.getChangedObjects();
      List<CDOIDAndVersion> detachedObjects = changeSet.getDetachedObjects();

      CDOCommitData data = new CDOCommitDataImpl(newPackages, newObjects, changedObjects, detachedObjects);

      String comment = "<replicate raw commits>"; //$NON-NLS-1$
      CDOCommitInfo commitInfo = manager.createCommitInfo(branch, toCommitTime, fromCommitTime, SYSTEM_USER_ID,
          comment, data);
      sessionManager.sendCommitNotification(replicatorSession, commitInfo);
    }

    CDOLockChangeInfo lockChangeInfo = CDOLockUtil.createLockChangeInfo();
    sessionManager.sendLockNotification(replicatorSession, lockChangeInfo);
  }

  private Map<CDOBranch, TimeRange> replicateRawGetBranches(long fromCommitTime, long toCommitTime)
  {
    final Map<CDOBranch, TimeRange> branches = new HashMap<CDOBranch, TimeRange>();
    CDOCommitInfoHandler handler = new CDOCommitInfoHandler()
    {
      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        CDOBranch branch = commitInfo.getBranch();
        long timeStamp = commitInfo.getTimeStamp();
        TimeRange range = branches.get(branch);
        if (range == null)
        {
          branches.put(branch, new TimeRange(timeStamp));
        }
        else
        {
          range.update(timeStamp);
        }
      }
    };

    getCommitInfoManager().getCommitInfos(null, fromCommitTime, toCommitTime, handler);
    return branches;
  }

  @Override
  public abstract InternalCommitContext createCommitContext(InternalTransaction transaction);

  protected InternalCommitContext createNormalCommitContext(InternalTransaction transaction)
  {
    return super.createCommitContext(transaction);
  }

  protected InternalCommitContext createWriteThroughCommitContext(InternalTransaction transaction)
  {
    return new WriteThroughCommitContext(transaction);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(synchronizer, "synchronizer"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    // Makes setRootResource() being called later in RepositorySynchronizer.ConnectRunnable
    setState(INITIAL);

    InternalStore store = getStore();
    if (!store.isFirstStart())
    {
      Map<String, String> map = store.getPersistentProperties(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));
      if (!map.containsKey(PROP_GRACEFULLY_SHUT_DOWN))
      {
        setReplicationCountersToLatest();
      }
      else
      {
        Set<String> names = new HashSet<String>();
        names.add(PROP_LAST_REPLICATED_BRANCH_ID);
        names.add(PROP_LAST_REPLICATED_COMMIT_TIME);

        map = store.getPersistentProperties(names);
        setLastReplicatedBranchID(Integer.valueOf(map.get(PROP_LAST_REPLICATED_BRANCH_ID)));
        setLastReplicatedCommitTime(Long.valueOf(map.get(PROP_LAST_REPLICATED_COMMIT_TIME)));
      }
    }

    store.removePersistentProperties(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));

    if (getType() != MASTER)
    {
      startSynchronization();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    stopSynchronization();

    Map<String, String> map = new HashMap<String, String>();
    map.put(PROP_LAST_REPLICATED_BRANCH_ID, Integer.toString(lastReplicatedBranchID));
    map.put(PROP_LAST_REPLICATED_COMMIT_TIME, Long.toString(lastReplicatedCommitTime));
    map.put(PROP_GRACEFULLY_SHUT_DOWN, Boolean.TRUE.toString());

    InternalStore store = getStore();
    store.setPersistentProperties(map);

    super.doDeactivate();
  }

  protected void startSynchronization()
  {
    replicatorSession = getSessionManager().openSession(null);
    replicatorSession.options().setPassiveUpdateEnabled(false);
    replicatorSession.options().setLockNotificationMode(LockNotificationMode.OFF);

    synchronizer.setLocalRepository(this);
    synchronizer.activate();
  }

  protected void stopSynchronization()
  {
    if (synchronizer != null)
    {
      synchronizer.deactivate();
    }
  }

  protected void setReplicationCountersToLatest()
  {
    setLastReplicatedBranchID(getStore().getLastBranchID());
    setLastReplicatedCommitTime(getStore().getLastNonLocalCommitTime());
  }

  protected void doInitRootResource()
  {
    super.initRootResource();
  }

  @Override
  protected void initRootResource()
  {
    // Do nothing
  }

  @Override
  public LockObjectsResult lock(InternalView view, LockType lockType, List<CDORevisionKey> revisionKeys,
      boolean recursive, long timeout)
  {
    if (view.getBranch().isLocal())
    {
      return super.lock(view, lockType, revisionKeys, recursive, timeout);
    }

    if (getState() != ONLINE)
    {
      throw new CDOException("Cannot lock in a non-local branch when clone is not connected to master");
    }

    return lockThrough(view, lockType, revisionKeys, false, timeout);
  }

  private LockObjectsResult lockOnMaster(InternalView view, LockType type, List<CDORevisionKey> revKeys,
      boolean recursive, long timeout) throws InterruptedException
  {
    // Delegate locking to the master
    InternalCDOSession remoteSession = getSynchronizer().getRemoteSession();
    CDOSessionProtocol sessionProtocol = remoteSession.getSessionProtocol();

    String areaID = view.getDurableLockingID();
    if (areaID == null)
    {
      throw new IllegalStateException("Durable locking is not enabled for view " + view);
    }

    LockObjectsResult masterLockingResult = sessionProtocol.delegateLockObjects(areaID, revKeys, view.getBranch(),
        type, recursive, timeout);

    if (masterLockingResult.isSuccessful() && masterLockingResult.isWaitForUpdate())
    {
      if (!getSynchronizer().getRemoteSession().options().isPassiveUpdateEnabled())
      {
        throw new AssertionError(
            "Master lock result requires clone to wait, but clone does not have passiveUpdates enabled.");
      }

      long requiredTimestamp = masterLockingResult.getRequiredTimestamp();
      remoteSession.waitForUpdate(requiredTimestamp);
    }

    return masterLockingResult;
  }

  private LockObjectsResult lockThrough(InternalView view, LockType type, List<CDORevisionKey> keys, boolean recursive,
      long timeout)
  {
    try
    {
      LockObjectsResult masterLockingResult = lockOnMaster(view, type, keys, recursive, timeout);
      if (!masterLockingResult.isSuccessful())
      {
        return masterLockingResult;
      }

      LockObjectsResult localLockingResult = super.lock(view, type, keys, recursive, timeout);
      return localLockingResult;
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public UnlockObjectsResult unlock(InternalView view, LockType lockType, List<CDOID> objectIDs, boolean recursive)
  {
    if (view.getBranch().isLocal())
    {
      super.unlock(view, lockType, objectIDs, recursive);
    }

    if (getState() != ONLINE)
    {
      throw new CDOException("Cannot unlock in a non-local branch when clone is not connected to master");
    }

    return unlockThrough(view, lockType, objectIDs, recursive);
  }

  private void unlockOnMaster(InternalView view, LockType lockType, List<CDOID> objectIDs, boolean recursive)
  {
    InternalCDOSession remoteSession = getSynchronizer().getRemoteSession();
    CDOSessionProtocol sessionProtocol = remoteSession.getSessionProtocol();

    String lockAreaID = view.getDurableLockingID();
    if (lockAreaID == null)
    {
      throw new IllegalStateException("Durable locking is not enabled for view " + view);
    }

    sessionProtocol.delegateUnlockObjects(lockAreaID, objectIDs, lockType, recursive);
  }

  private UnlockObjectsResult unlockThrough(InternalView view, LockType lockType, List<CDOID> objectIDs,
      boolean recursive)
  {
    unlockOnMaster(view, lockType, objectIDs, recursive);
    return super.unlock(view, lockType, objectIDs, recursive);
  }

  /**
   * @author Eike Stepper
   */
  private static final class TimeRange
  {
    private long time1;

    private long time2;

    public TimeRange(long time)
    {
      time1 = time;
      time2 = time;
    }

    public void update(long time)
    {
      if (time < time1)
      {
        time1 = time;
      }

      if (time > time2)
      {
        time2 = time;
      }
    }

    public long getTime1()
    {
      return time1;
    }

    public long getTime2()
    {
      return time2;
    }

    @Override
    public String toString()
    {
      return "[" + CDOCommonUtil.formatTimeStamp(time1) + " - " + CDOCommonUtil.formatTimeStamp(time1) + "]";
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static final class CommitContextData implements CDOCommitData
  {
    private InternalCommitContext commitContext;

    private CDOChangeKindCache changeKindCache;

    public CommitContextData(InternalCommitContext commitContext)
    {
      this.commitContext = commitContext;
    }

    public boolean isEmpty()
    {
      return false;
    }

    public CDOChangeSetData copy()
    {
      throw new UnsupportedOperationException();
    }

    public void merge(CDOChangeSetData changeSetData)
    {
      throw new UnsupportedOperationException();
    }

    public List<CDOPackageUnit> getNewPackageUnits()
    {
      final InternalCDOPackageUnit[] newPackageUnits = commitContext.getNewPackageUnits();
      return new IndexedList<CDOPackageUnit>()
      {
        @Override
        public CDOPackageUnit get(int index)
        {
          return newPackageUnits[index];
        }

        @Override
        public int size()
        {
          return newPackageUnits.length;
        }
      };
    }

    public List<CDOIDAndVersion> getNewObjects()
    {
      final InternalCDORevision[] newObjects = commitContext.getNewObjects();
      return new IndexedList<CDOIDAndVersion>()
      {
        @Override
        public CDOIDAndVersion get(int index)
        {
          return newObjects[index];
        }

        @Override
        public int size()
        {
          return newObjects.length;
        }
      };
    }

    public List<CDORevisionKey> getChangedObjects()
    {
      final InternalCDORevisionDelta[] changedObjects = commitContext.getDirtyObjectDeltas();
      return new IndexedList<CDORevisionKey>()
      {
        @Override
        public CDORevisionKey get(int index)
        {
          return changedObjects[index];
        }

        @Override
        public int size()
        {
          return changedObjects.length;
        }
      };
    }

    public List<CDOIDAndVersion> getDetachedObjects()
    {
      final CDOID[] detachedObjects = commitContext.getDetachedObjects();
      return new IndexedList<CDOIDAndVersion>()
      {
        @Override
        public CDOIDAndVersion get(int index)
        {
          return CDOIDUtil.createIDAndVersion(detachedObjects[index], CDOBranchVersion.UNSPECIFIED_VERSION);
        }

        @Override
        public int size()
        {
          return detachedObjects.length;
        }
      };
    }

    public synchronized Map<CDOID, CDOChangeKind> getChangeKinds()
    {
      if (changeKindCache == null)
      {
        changeKindCache = new CDOChangeKindCache(this);
      }

      return changeKindCache;
    }

    public CDOChangeKind getChangeKind(CDOID id)
    {
      return getChangeKinds().get(id);
    }
  }

  /**
   * @author Eike Stepper
   */
  protected final class WriteThroughCommitContext extends TransactionCommitContext
  {
    private static final int ARTIFICIAL_VIEW_ID = 0;

    public WriteThroughCommitContext(InternalTransaction transaction)
    {
      super(transaction);
    }

    @Override
    public void preWrite()
    {
      // Do nothing
    }

    @Override
    public void write(OMMonitor monitor)
    {
      // Do nothing
    }

    @Override
    public void commit(OMMonitor monitor)
    {
      // Prepare commit to the master
      final CDOCommitData commitData = new CommitContextData(this);

      InternalCDOCommitContext ctx = new InternalCDOCommitContext()
      {
        public boolean isPartialCommit()
        {
          return false;
        }

        public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
        {
          throw new UnsupportedOperationException();
        }

        public List<CDOPackageUnit> getNewPackageUnits()
        {
          return commitData.getNewPackageUnits();
        }

        public Map<CDOID, CDOObject> getNewObjects()
        {
          throw new UnsupportedOperationException();
        }

        public Collection<CDOLockState> getLocksOnNewObjects()
        {
          CDOLockState[] locksOnNewObjectsArr = WriteThroughCommitContext.this.getLocksOnNewObjects();
          Collection<CDOLockState> locksOnNewObjects = Arrays.asList(locksOnNewObjectsArr);
          return locksOnNewObjects;
        }

        public Collection<CDOLob<?>> getLobs()
        {
          return Collections.emptySet(); // TODO (CD) Did we forget to support this earlier?
        }

        public Map<CDOID, CDOObject> getDirtyObjects()
        {
          throw new UnsupportedOperationException();
        }

        public Map<CDOID, CDOObject> getDetachedObjects()
        {
          throw new UnsupportedOperationException();
        }

        public void preCommit()
        {
          throw new UnsupportedOperationException();
        }

        public void postCommit(CommitTransactionResult result)
        {
          throw new UnsupportedOperationException();
        }

        public InternalCDOTransaction getTransaction()
        {
          return null;
        }

        public CDOCommitData getCommitData()
        {
          return commitData;
        }

        public int getViewID()
        {
          return ARTIFICIAL_VIEW_ID;
        }

        public String getUserID()
        {
          return WriteThroughCommitContext.this.getUserID();
        }

        public boolean isAutoReleaseLocks()
        {
          return WriteThroughCommitContext.this.isAutoReleaseLocksEnabled();
        }

        public String getCommitComment()
        {
          return WriteThroughCommitContext.this.getCommitComment();
        }

        public CDOBranch getBranch()
        {
          return WriteThroughCommitContext.this.getTransaction().getBranch();
        }
      };

      // Delegate commit to the master
      CDOSessionProtocol sessionProtocol = getSynchronizer().getRemoteSession().getSessionProtocol();
      CommitTransactionResult result = sessionProtocol.commitDelegation(ctx, monitor);

      // Stop if commit to master failed
      String rollbackMessage = result.getRollbackMessage();
      if (rollbackMessage != null)
      {
        throw new TransactionException(rollbackMessage);
      }

      // Prepare data needed for commit result and commit notifications
      long timeStamp = result.getTimeStamp();
      setTimeStamp(timeStamp);
      addIDMappings(result.getIDMappings());
      applyIDMappings(new Monitor());

      try
      {
        writeThroughCommitLock.lock();

        // Commit to the local repository
        super.preWrite();
        super.write(new Monitor());
        super.commit(new Monitor());
      }
      finally
      {
        writeThroughCommitLock.unlock();
      }

      // Remember commit time in the local repository
      setLastCommitTimeStamp(timeStamp);
      setLastReplicatedCommitTime(timeStamp);

      // Remember commit time in the replicator session.
      getSynchronizer().getRemoteSession().setLastUpdateTime(timeStamp);
    }

    @Override
    protected long[] createTimeStamp(OMMonitor monitor)
    {
      // Already set after commit to the master.
      // Do not call getTimeStamp() of the enclosing Repo class!!!
      InternalRepository repository = getTransaction().getSession().getManager().getRepository();
      return repository.forceCommitTimeStamp(WriteThroughCommitContext.this.getTimeStamp(), monitor);
    }

    @Override
    protected void lockObjects() throws InterruptedException
    {
      // Do nothing
    }

    private void addIDMappings(Map<CDOID, CDOID> idMappings)
    {
      for (Map.Entry<CDOID, CDOID> idMapping : idMappings.entrySet())
      {
        CDOID oldID = idMapping.getKey();
        CDOID newID = idMapping.getValue();
        addIDMapping(oldID, newID);
      }
    }
  }
}
