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
package org.eclipse.emf.cdo.internal.server.embedded;

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
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.util.CDOQueryQueue;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult;
import org.eclipse.emf.cdo.spi.common.CDORawReplicationContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @deprecated Not yet supported.
 */
@Deprecated
public class EmbeddedClientSessionProtocol extends Lifecycle implements CDOSessionProtocol
{
  private EmbeddedClientSession session;

  // A separate session protocol instance is required because the getSession() methods are ambiguous!
  private EmbeddedServerSessionProtocol serverSessionProtocol;

  private InternalRepository repository;

  public EmbeddedClientSessionProtocol(EmbeddedClientSession session)
  {
    this.session = session;
  }

  public EmbeddedClientSession getSession()
  {
    return session;
  }

  public EmbeddedServerSessionProtocol getServerSessionProtocol()
  {
    return serverSessionProtocol;
  }

  public InternalSession openSession(boolean passiveUpdateEnabled)
  {
    repository = session.getRepository();
    activate();
    return serverSessionProtocol.openSession(repository, passiveUpdateEnabled);
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    throw new UnsupportedOperationException();
  }

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    throw new UnsupportedOperationException();
  }

  public BranchInfo loadBranch(int branchID)
  {
    throw new UnsupportedOperationException();
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    throw new UnsupportedOperationException();
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public void deleteBranch(int branchID)
  {
    throw new UnsupportedOperationException();
  }

  public void renameBranch(int branchID, String newName)
  {
    throw new UnsupportedOperationException();
  }

  public RepositoryTimeResult getRepositoryTime()
  {
    RepositoryTimeResult result = new RepositoryTimeResult();
    long timeStamp = System.currentTimeMillis();
    result.setRequested(timeStamp);
    result.setIndicated(timeStamp);
    result.setResponded(timeStamp);
    result.setConfirmed(timeStamp);
    return result;
  }

  public CDOLockState[] getLockStates(int viewID, Collection<CDOID> ids)
  {
    throw new UnsupportedOperationException();
  }

  public void enableLockNotifications(int viewID, boolean enable)
  {
    throw new UnsupportedOperationException();
  }

  public void disablePassiveUpdate()
  {
    // serverSessionProtocol.getSession().setPassiveUpdateEnabled(passiveUpdateEnabled);
    // TODO: implement EmbeddedClientSessionProtocol.setPassiveUpdate(idAndVersions, initialChunkSize,
    // passiveUpdateEnabled)
    throw new UnsupportedOperationException();
  }

  public void setPassiveUpdateMode(PassiveUpdateMode mode)
  {
    // TODO: implement EmbeddedClientSessionProtocol.setPassiveUpdateMode(mode)
    throw new UnsupportedOperationException();
  }

  public void setLockNotificationMode(LockNotificationMode mode)
  {
    throw new UnsupportedOperationException();
  }

  public Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex)
  {
    throw new UnsupportedOperationException();
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    throw new UnsupportedOperationException();
  }

  public CDOCommitData loadCommitData(long timeStamp)
  {
    throw new UnsupportedOperationException();
  }

  public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
  {
    try
    {
      InternalSession session = serverSessionProtocol.getSession();
      StoreThreadLocal.setSession(session);
      return repository.getRevisionManager().getRevisionByVersion(id, branchVersion, referenceChunk, true);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  public List<InternalCDORevision> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint,
      int referenceChunk, int prefetchDepth)
  {
    try
    {
      InternalSession session = serverSessionProtocol.getSession();
      StoreThreadLocal.setSession(session);

      List<CDOID> ids = new ArrayList<CDOID>(infos.size());
      for (RevisionInfo info : infos)
      {
        ids.add(info.getID());
      }

      // @SuppressWarnings("unchecked")
      // List<InternalCDORevision> revisions = (List<InternalCDORevision>)(List<?>)repository.getRevisionManager()
      // .getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, true);

      // TODO: implement EmbeddedClientSessionProtocol.loadRevisions(infos, branchPoint, referenceChunk, prefetchDepth)
      throw new UnsupportedOperationException();
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  public RefreshSessionResult refresh(long lastUpdateTime,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions, int initialChunkSize,
      boolean enablePassiveUpdates)
  {
    throw new UnsupportedOperationException();
  }

  public void openView(int viewID, boolean readOnly, CDOBranchPoint branchPoint)
  {
    InternalSession session = serverSessionProtocol.getSession();
    if (readOnly)
    {
      session.openView(viewID, branchPoint);
    }
    else
    {
      session.openTransaction(viewID, branchPoint);
    }
  }

  public CDOBranchPoint openView(int viewID, boolean readOnly, String durableLockingID)
  {
    throw new UnsupportedOperationException();
  }

  public void switchTarget(int viewID, CDOBranchPoint branchPoint, List<InternalCDOObject> invalidObjects,
      List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects, OMMonitor monitor)
  {
    // TODO: implement EmbeddedClientSessionProtocol.changeView(viewID, branchPoint, invalidObjects, allChangedObjects,
    // allDetachedObjects, monitor)
    throw new UnsupportedOperationException();

    // try
    // {
    // monitor.begin();
    // Async async = monitor.forkAsync();
    //
    // try
    // {
    // InternalView view = serverSessionProtocol.getSession().getView(viewID);
    // if (view != null)
    // {
    // List<CDOID> ids = new ArrayList<CDOID>(invalidObjects.size());
    // for (InternalCDOObject object : invalidObjects)
    // {
    // ids.add(object.cdoID());
    // }
    //
    // view.changeTarget(branchPoint, ids, allChangedObjects, allDetachedObjects);
    // }
    // }
    // finally
    // {
    // async.stop();
    // }
    // }
    // finally
    // {
    // monitor.done();
    // }
  }

  public void closeView(int viewID)
  {
    InternalView view = serverSessionProtocol.getSession().getView(viewID);
    if (view != null)
    {
      view.close();
    }
  }

  public void changeSubscription(int viewID, List<CDOID> ids, boolean subscribeMode, boolean clear)
  {
    throw new UnsupportedOperationException();
  }

  public void query(CDOView view, AbstractQueryIterator<?> query)
  {
    InternalView serverView = serverSessionProtocol.getSession().getView(view.getViewID());
    InternalQueryManager queryManager = repository.getQueryManager();
    InternalQueryResult result = queryManager.execute(serverView, query.getQueryInfo());

    query.setQueryID(result.getQueryID());
    CDOQueryQueue<Object> resultQueue = query.getQueue();

    try
    {
      while (result.hasNext())
      {
        Object object = result.next();
        resultQueue.add(object);
      }
    }
    catch (RuntimeException ex)
    {
      resultQueue.setException(ex);
    }
    catch (Throwable throwable)
    {
      resultQueue.setException(new RuntimeException(throwable.getMessage(), throwable));
    }
    finally
    {
      resultQueue.close();
    }
  }

  public boolean cancelQuery(int queryID)
  {
    repository.getQueryManager().cancel(queryID);
    return true;
  }

  public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType, boolean byOthers)
  {
    throw new UnsupportedOperationException();
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
  public LockObjectsResult lockObjects2(List<CDORevisionKey> keys, int viewID, CDOBranch viewedBranch, LockType type,
      boolean recursive, long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public void unlockObjects(CDOView view, Collection<CDOID> ids, LockType type)
  {
    throw new UnsupportedOperationException();
  }

  public UnlockObjectsResult unlockObjects2(CDOView view, Collection<CDOID> ids, LockType type, boolean recursive)
  {
    throw new UnsupportedOperationException();
  }

  public LockObjectsResult delegateLockObjects(String lockAreaID, List<CDORevisionKey> keys, CDOBranch viewedBranch,
      LockType type, boolean recursive, long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  public UnlockObjectsResult delegateUnlockObjects(String lockAreaID, Collection<CDOID> ids, LockType type,
      boolean recursive)
  {
    throw new UnsupportedOperationException();
  }

  public String changeLockArea(CDOView view, boolean create)
  {
    throw new UnsupportedOperationException();
  }

  public List<byte[]> queryLobs(Set<byte[]> ids)
  {
    // TODO: implement EmbeddedClientSessionProtocol.queryLobs(ids)
    throw new UnsupportedOperationException();
  }

  public void loadLob(CDOLobInfo info, Object outputStreamOrWriter)
  {
    // TODO: implement EmbeddedClientSessionProtocol.loadLob(info, out)
    throw new UnsupportedOperationException();
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    // TODO: implement EmbeddedClientSessionProtocol.handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime,
    // handler)
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public CommitTransactionResult commitTransaction(int transactionID, String comment, boolean releaseLocks,
      CDOIDProvider idProvider, CDOCommitData commitData, Collection<CDOLob<?>> lobs, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitTransaction(InternalCDOCommitContext context, OMMonitor monitor)
  {
    monitor.begin(2);
    boolean success = false;
    InternalCommitContext serverCommitContext = null;
    CommitTransactionResult result = null;

    try
    {
      InternalCDOTransaction transaction = context.getTransaction();
      CDOCommitData commitData = context.getCommitData();

      int transactionID = transaction.getViewID();
      InternalTransaction serverTransaction = (InternalTransaction)serverSessionProtocol.getSession().getView(
          transactionID);
      serverCommitContext = serverTransaction.createCommitContext();
      serverCommitContext.preWrite();
      serverCommitContext.setAutoReleaseLocksEnabled(transaction.options().isAutoReleaseLocksEnabled());

      List<CDOPackageUnit> npu = commitData.getNewPackageUnits();
      serverCommitContext.setNewPackageUnits(npu.toArray(new InternalCDOPackageUnit[npu.size()]));

      List<CDOIDAndVersion> no = commitData.getNewObjects();
      InternalCDORevision[] array = new InternalCDORevision[no.size()];
      int index = 0;
      for (CDOIDAndVersion object : no)
      {
        InternalCDORevision revision = (InternalCDORevision)object;
        // revision.convertEObjects(clientTransaction);
        array[index++] = revision;
      }

      serverCommitContext.setNewObjects(array);

      List<CDORevisionKey> rd = commitData.getChangedObjects();
      serverCommitContext.setDirtyObjectDeltas(rd.toArray(new InternalCDORevisionDelta[rd.size()]));

      List<CDOIDAndVersion> detachedObjects = commitData.getDetachedObjects();
      serverCommitContext.setDetachedObjects(detachedObjects.toArray(new CDOID[detachedObjects.size()]));

      serverCommitContext.write(monitor.fork());
      success = serverCommitContext.getRollbackMessage() == null;
      if (success)
      {
        serverCommitContext.commit(monitor.fork());
      }
      else
      {
        monitor.worked();
      }

      // result = new CommitTransactionResult(commitData, serverCommitContext.getBranchPoint().getTimeStamp());
      // for (Entry<CDOID, CDOID> entry : serverCommitContext.getIDMappings().entrySet())
      // {
      // result.addIDMapping(entry.getKey(), entry.getValue());
      // }
    }
    finally
    {
      if (serverCommitContext != null)
      {
        serverCommitContext.postCommit(success);
      }

      monitor.done();
    }

    return result;
  }

  @Deprecated
  public CommitTransactionResult commitDelegation(CDOBranch branch, String userID, String comment,
      CDOCommitData commitData, Map<CDOID, EClass> detachedObjectTypes, Collection<CDOLob<?>> lobs, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitDelegation(InternalCDOCommitContext context, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitXATransactionCancel(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitXATransactionPhase1(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitXATransactionPhase2(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitXATransactionPhase3(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public List<CDORemoteSession> getRemoteSessions(InternalCDORemoteSessionManager manager, boolean subscribe)
  {
    throw new UnsupportedOperationException();
  }

  public Set<Integer> sendRemoteMessage(CDORemoteSessionMessage message, List<CDORemoteSession> recipients)
  {
    throw new UnsupportedOperationException();
  }

  public boolean unsubscribeRemoteSessions()
  {
    throw new UnsupportedOperationException();
  }

  public void replicateRepository(CDOReplicationContext context, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public void replicateRepositoryRaw(CDORawReplicationContext context, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CDOChangeSetData[] loadChangeSets(CDOBranchPointRange... ranges)
  {
    throw new UnsupportedOperationException();
  }

  public Set<CDOID> loadMergeData(CDORevisionAvailabilityInfo targetInfo, CDORevisionAvailabilityInfo sourceInfo,
      CDORevisionAvailabilityInfo targetBaseInfo, CDORevisionAvailabilityInfo sourceBaseInfo, final String... nsURIs)
  {
    throw new UnsupportedOperationException();
  }

  public CDOAuthenticationResult handleAuthenticationChallenge(byte[] randomToken) throws Exception
  {
    CDOAuthenticator authenticator = getSession().getAuthenticator();
    if (authenticator == null)
    {
      throw new IllegalStateException("No authenticator configured"); //$NON-NLS-1$
    }

    CDOAuthenticationResult result = authenticator.authenticate(randomToken);
    if (result == null)
    {
      throw new SecurityException("Not authenticated"); //$NON-NLS-1$
    }

    String userID = result.getUserID();
    if (userID == null)
    {
      throw new SecurityException("No user ID"); //$NON-NLS-1$
    }

    byte[] cryptedToken = result.getCryptedToken();
    if (cryptedToken == null)
    {
      throw new SecurityException("No crypted token"); //$NON-NLS-1$
    }

    return result;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    serverSessionProtocol = new EmbeddedServerSessionProtocol(this);
    serverSessionProtocol.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    serverSessionProtocol.deactivate();
    serverSessionProtocol = null;
    super.doDeactivate();
  }
}
