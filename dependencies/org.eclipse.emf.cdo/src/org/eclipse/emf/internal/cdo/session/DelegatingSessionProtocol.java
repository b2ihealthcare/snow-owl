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
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.CDORawReplicationContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DelegatingSessionProtocol extends Lifecycle implements CDOSessionProtocol
{
  private CDOSessionProtocol delegate;

  private ExceptionHandler exceptionHandler;

  @ExcludeFromDump
  private IListener delegateListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      DelegatingSessionProtocol.this.deactivate();
    }
  };

  public DelegatingSessionProtocol(CDOSessionProtocol delegate, ExceptionHandler handler)
  {
    this.delegate = delegate;
    exceptionHandler = handler;
    activate();
  }

  public CDOSessionProtocol getDelegate()
  {
    return delegate;
  }

  public void setDelegate(CDOSessionProtocol delegate)
  {
    if (delegate != null)
    {
      unhookDelegate();
      if (LifecycleUtil.isActive(this.delegate))
      {
        LifecycleUtil.deactivate(this.delegate);
      }
    }

    this.delegate = delegate;
    hookDelegate();
  }

  public CDOSession getSession()
  {
    return (CDOSession)delegate.getSession();
  }

  public boolean cancelQuery(int queryId)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.cancelQuery(queryId);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void changeSubscription(int viewID, List<CDOID> ids, boolean subscribeMode, boolean clear)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.changeSubscription(viewID, ids, subscribeMode, clear);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void openView(int viewID, boolean readOnly, CDOBranchPoint branchPoint)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.openView(viewID, readOnly, branchPoint);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public CDOBranchPoint openView(int viewID, boolean readOnly, String durableLockingID)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.openView(viewID, readOnly, durableLockingID);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void switchTarget(int viewID, CDOBranchPoint branchPoint, List<InternalCDOObject> invalidObjects,
      List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects, OMMonitor monitor)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.switchTarget(viewID, branchPoint, invalidObjects, allChangedObjects, allDetachedObjects, monitor);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void closeView(int viewID)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        if (delegate != null)
        {
          delegate.closeView(viewID);
        }

        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public List<byte[]> queryLobs(Set<byte[]> ids)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.queryLobs(ids);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void loadLob(CDOLobInfo info, Object outputStreamOrWriter)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.loadLob(info, outputStreamOrWriter);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, handler);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  @Deprecated
  public CommitTransactionResult commitTransaction(int transactionID, String comment, boolean releaseLocks,
      CDOIDProvider idProvider, CDOCommitData commitData, Collection<CDOLob<?>> lobs, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitTransaction(InternalCDOCommitContext context, OMMonitor monitor)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.commitTransaction(context, monitor);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  @Deprecated
  public CommitTransactionResult commitDelegation(CDOBranch branch, String userID, String comment,
      CDOCommitData commitData, Map<CDOID, EClass> detachedObjectTypes, Collection<CDOLob<?>> lobs, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitDelegation(InternalCDOCommitContext context, OMMonitor monitor)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.commitDelegation(context, monitor);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public CommitTransactionResult commitXATransactionCancel(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.commitXATransactionCancel(xaContext, monitor);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public CommitTransactionResult commitXATransactionPhase1(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.commitXATransactionPhase1(xaContext, monitor);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public CommitTransactionResult commitXATransactionPhase2(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.commitXATransactionPhase2(xaContext, monitor);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public CommitTransactionResult commitXATransactionPhase3(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.commitXATransactionPhase3(xaContext, monitor);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public RepositoryTimeResult getRepositoryTime()
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.getRepositoryTime();
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public CDOLockState[] getLockStates(int viewID, Collection<CDOID> ids)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.getLockStates(viewID, ids);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void enableLockNotifications(int viewID, boolean enable)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.enableLockNotifications(viewID, enable);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType, boolean byOthers)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.isObjectLocked(view, object, lockType, byOthers);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public String changeLockArea(CDOView view, boolean create)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.changeLockArea(view, create);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.loadPackages(packageUnit);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.createBranch(branchID, branchInfo);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public BranchInfo loadBranch(int branchID)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.loadBranch(branchID);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.loadSubBranches(branchID);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.loadBranches(startID, endID, branchHandler);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.loadCommitInfos(branch, startTime, endTime, handler);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public CDOCommitData loadCommitData(long timeStamp)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.loadCommitData(timeStamp);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.loadChunk(revision, feature, accessIndex, fetchIndex, fromIndex, toIndex);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public List<InternalCDORevision> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint,
      int referenceChunk, int prefetchDepth)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.loadRevisions(infos, branchPoint, referenceChunk, prefetchDepth);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.loadRevisionByVersion(id, branchVersion, referenceChunk);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  @Deprecated
  public LockObjectsResult lockObjects(List<InternalCDORevision> viewedRevisions, int viewID, CDOBranch viewedBranch,
      LockType lockType, long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 4.1
   */
  public LockObjectsResult lockObjects2(List<CDORevisionKey> revisionKeys, int viewID, CDOBranch viewedBranch,
      LockType lockType, boolean recursive, long timeout) throws InterruptedException

  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.lockObjects2(revisionKeys, viewID, viewedBranch, lockType, recursive, timeout);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public LockObjectsResult delegateLockObjects(String lockAreaID, List<CDORevisionKey> revisionKeys,
      CDOBranch viewedBranch, LockType lockType, boolean recursive, long timeout) throws InterruptedException
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.delegateLockObjects(lockAreaID, revisionKeys, viewedBranch, lockType, recursive, timeout);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public UnlockObjectsResult delegateUnlockObjects(String lockAreaID, Collection<CDOID> objectIDs, LockType lockType,
      boolean recursive)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.delegateUnlockObjects(lockAreaID, objectIDs, lockType, recursive);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void query(CDOView view, AbstractQueryIterator<?> queryResult)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.query(view, queryResult);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void disablePassiveUpdate()
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.disablePassiveUpdate();
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void setPassiveUpdateMode(PassiveUpdateMode mode)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.setPassiveUpdateMode(mode);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void setLockNotificationMode(LockNotificationMode mode)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.setLockNotificationMode(mode);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public RefreshSessionResult refresh(long lastUpdateTime,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions, int initialChunkSize,
      boolean enablePassiveUpdates)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.refresh(lastUpdateTime, viewedRevisions, initialChunkSize, enablePassiveUpdates);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  @Deprecated
  public void unlockObjects(CDOView view, Collection<CDOID> objectIDs, LockType lockType)
  {
    throw new UnsupportedOperationException();
  }

  public UnlockObjectsResult unlockObjects2(CDOView view, Collection<CDOID> objectIDs, LockType lockType,
      boolean recursive)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.unlockObjects2(view, objectIDs, lockType, recursive);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public List<CDORemoteSession> getRemoteSessions(InternalCDORemoteSessionManager manager, boolean subscribe)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.getRemoteSessions(manager, subscribe);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public Set<Integer> sendRemoteMessage(CDORemoteSessionMessage message, List<CDORemoteSession> recipients)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.sendRemoteMessage(message, recipients);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public boolean unsubscribeRemoteSessions()
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.unsubscribeRemoteSessions();
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void replicateRepository(CDOReplicationContext context, OMMonitor monitor)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.replicateRepository(context, monitor);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public void replicateRepositoryRaw(CDORawReplicationContext context, OMMonitor monitor)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        delegate.replicateRepositoryRaw(context, monitor);
        return;
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public CDOChangeSetData[] loadChangeSets(CDOBranchPointRange... ranges)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.loadChangeSets(ranges);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  public Set<CDOID> loadMergeData(CDORevisionAvailabilityInfo targetInfo, CDORevisionAvailabilityInfo sourceInfo,
      CDORevisionAvailabilityInfo targetBaseInfo, CDORevisionAvailabilityInfo sourceBaseInfo, final String... nsURIs)
  {
    int attempt = 0;
    for (;;)
    {
      try
      {
        return delegate.loadMergeData(targetInfo, sourceInfo, targetBaseInfo, sourceBaseInfo, nsURIs);
      }
      catch (Exception ex)
      {
        handleException(++attempt, ex);
      }
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    hookDelegate();
  }

  private void hookDelegate()
  {
    EventUtil.addListener(delegate, delegateListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    unhookDelegate();
    LifecycleUtil.deactivate(delegate);
    delegate = null;
    super.doDeactivate();
  }

  private void unhookDelegate()
  {
    EventUtil.removeListener(delegate, delegateListener);
  }

  private void handleException(int attempt, Exception exception)
  {
    try
    {
      exceptionHandler.handleException(getSession(), attempt, exception);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }
}
