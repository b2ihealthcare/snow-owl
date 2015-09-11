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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOInvalidationPolicy;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOStaleReferencePolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.session.SessionUtil;
import org.eclipse.emf.internal.cdo.view.AbstractCDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.ref.ReferenceType;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ServerCDOView extends AbstractCDOView implements org.eclipse.emf.cdo.view.CDOView.Options
{
  private static final CDOAdapterPolicy[] ADAPTER_POLICIES = new CDOAdapterPolicy[0];

  private static final CDORevisionPrefetchingPolicy REVISION_PREFETCHING = CDOUtil
      .createRevisionPrefetchingPolicy(NO_REVISION_PREFETCHING);

  private InternalCDOSession session;

  private CDORevisionProvider revisionProvider;

  public ServerCDOView(InternalSession session, CDOBranchPoint branchPoint, boolean legacyModeEnabled,
      CDORevisionProvider revisionProvider)
  {
    super(branchPoint, legacyModeEnabled);
    this.session = new ServerCDOSession(session);
    this.revisionProvider = revisionProvider;

    setViewSet(SessionUtil.prepareResourceSet(new ResourceSetImpl()));
    setObjects(new ReferenceValueMap.Weak<CDOID, InternalCDOObject>());
    activate();
  }

  public int getViewID()
  {
    return 1;
  }

  public InternalCDOSession getSession()
  {
    return session;
  }

  public long getLastUpdateTime()
  {
    return getTimeStamp();
  }

  public void setLastUpdateTime(long lastUpdateTime)
  {
    throw new UnsupportedOperationException();
  }

  public Options options()
  {
    return this;
  }

  public synchronized InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    return (InternalCDORevision)revisionProvider.getRevision(id);
  }

  @Override
  protected synchronized void excludeNewObject(CDOID id)
  {
    // Do nothing
  }

  public boolean isInvalidationRunnerActive()
  {
    return false;
  }

  public boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
    throw new UnsupportedOperationException();
  }

  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout)
      throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout, boolean recursive)
      throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType)
  {
    throw new UnsupportedOperationException();
  }

  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType, boolean recursive)
  {
    throw new UnsupportedOperationException();
  }

  public void unlockObjects()
  {
    throw new UnsupportedOperationException();
  }

  public boolean waitForUpdate(long updateTime, long timeoutMillis)
  {
    throw new UnsupportedOperationException();
  }

  public void setViewID(int viewId)
  {
    throw new UnsupportedOperationException();
  }

  public void setSession(InternalCDOSession session)
  {
    throw new UnsupportedOperationException();
  }

  public int getSessionID()
  {
    return session.getSessionID();
  }

  public boolean isDurableView()
  {
    return false;
  }

  public String getDurableLockingID()
  {
    return null;
  }

  @Deprecated
  public String enableDurableLocking(boolean enable)
  {
    throw new UnsupportedOperationException();
  }

  public String enableDurableLocking()
  {
    throw new UnsupportedOperationException();
  }

  public void disableDurableLocking(boolean releaseLocks)
  {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("deprecation")
  public CDOFeatureAnalyzer getFeatureAnalyzer()
  {
    return CDOFeatureAnalyzer.NOOP;
  }

  @SuppressWarnings("deprecation")
  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer)
  {
    throw new UnsupportedOperationException();
  }

  public InternalCDOTransaction toTransaction()
  {
    throw new UnsupportedOperationException();
  }

  public void invalidate(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects,
      List<CDOIDAndVersion> allDetachedObjects, Map<CDOID, InternalCDORevision> oldRevisions, boolean async)
  {
    throw new UnsupportedOperationException();
  }

  public void handleLockNotification(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
  {
    // Do nothing
  }

  public void prefetchRevisions(CDOID id, int depth)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isObjectLocked(CDOObject object, LockType lockType, boolean byOthers)
  {
    return false;
  }

  public void handleAddAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    // Do nothing
  }

  public void handleRemoveAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    // Do nothing
  }

  public void subscribe(EObject eObject, Adapter adapter)
  {
    throw new UnsupportedOperationException();
  }

  public void unsubscribe(EObject eObject, Adapter adapter)
  {
    throw new UnsupportedOperationException();
  }

  public boolean hasSubscription(CDOID id)
  {
    return false;
  }

  public CDOView getContainer()
  {
    return this;
  }

  public ReferenceType getCacheReferenceType()
  {
    return ReferenceType.WEAK;
  }

  public boolean setCacheReferenceType(ReferenceType referenceType)
  {
    throw new UnsupportedOperationException();
  }

  public CDOInvalidationPolicy getInvalidationPolicy()
  {
    return CDOInvalidationPolicy.DEFAULT;
  }

  public void setInvalidationPolicy(CDOInvalidationPolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isDetachmentNotificationEnabled()
  {
    return false;
  }

  public void setDetachmentNotificationEnabled(boolean enabled)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isInvalidationNotificationEnabled()
  {
    return false;
  }

  public void setInvalidationNotificationEnabled(boolean enabled)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isLoadNotificationEnabled()
  {
    return false;
  }

  public void setLoadNotificationEnabled(boolean enabled)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isLockNotificationEnabled()
  {
    return false;
  }

  public void setLockNotificationEnabled(boolean enabled)
  {
    throw new UnsupportedOperationException();
  }

  public CDOAdapterPolicy[] getChangeSubscriptionPolicies()
  {
    return ADAPTER_POLICIES;
  }

  public void addChangeSubscriptionPolicy(CDOAdapterPolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  public void removeChangeSubscriptionPolicy(CDOAdapterPolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  public CDOAdapterPolicy getStrongReferencePolicy()
  {
    return CDOAdapterPolicy.ALL;
  }

  public void setStrongReferencePolicy(CDOAdapterPolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public CDOStaleReferencePolicy getStaleReferenceBehaviour()
  {
    return getStaleReferencePolicy();
  }

  @Deprecated
  public void setStaleReferenceBehaviour(CDOStaleReferencePolicy policy)
  {
    setStaleReferencePolicy(policy);
  }

  public CDOStaleReferencePolicy getStaleReferencePolicy()
  {
    return CDOStaleReferencePolicy.EXCEPTION;
  }

  public void setStaleReferencePolicy(CDOStaleReferencePolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  public CDORevisionPrefetchingPolicy getRevisionPrefetchingPolicy()
  {
    return REVISION_PREFETCHING;
  }

  public void setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy prefetchingPolicy)
  {
    throw new UnsupportedOperationException();
  }

  public CDOLockState[] getLockStates(Collection<CDOID> ids)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @author Eike Stepper
   */
  public final class ServerCDOSession implements InternalCDOSession, CDORepositoryInfo
  {
    private InternalSession internalSession;

    private InternalRepository repository;

    public ServerCDOSession(InternalSession internalSession)
    {
      this.internalSession = internalSession;
      repository = internalSession.getManager().getRepository();
    }
    
    /** @since Snow Owl 4.3 */
    public InternalRepository getRepository() {
		return repository;
	}
    
    /** @since Snow Owl 4.3 */
    public InternalSession getInternalSession() {
		return internalSession;
	}

    public CDOView[] getElements()
    {
      return new ServerCDOView[] { ServerCDOView.this };
    }

    public InternalCDOTransaction getTransaction(int viewID)
    {
      return null;
    }

    public InternalCDOTransaction[] getTransactions()
    {
      return new InternalCDOTransaction[0];
    }

    public CDOView[] getViews()
    {
      return getElements();
    }

    public CDOView getView(int viewID)
    {
      return viewID == getViewID() ? ServerCDOView.this : null;
    }

    public CDOSessionProtocol getSessionProtocol()
    {
      throw new UnsupportedOperationException();
    }

    public CDOLobStore getLobStore()
    {
      throw new UnsupportedOperationException();
    }

    public InternalCDORevisionManager getRevisionManager()
    {
      return repository.getRevisionManager();
    }

    public InternalCDOPackageRegistry getPackageRegistry()
    {
      if (revisionProvider instanceof IStoreAccessor.CommitContext)
      {
        IStoreAccessor.CommitContext context = (IStoreAccessor.CommitContext)revisionProvider;
        return context.getPackageRegistry();
      }

      return repository.getPackageRegistry(false);
    }

    public InternalCDOCommitInfoManager getCommitInfoManager()
    {
      return repository.getCommitInfoManager();
    }

    public InternalCDOBranchManager getBranchManager()
    {
      return repository.getBranchManager();
    }

    public void setMainBranchLocal(boolean mainBranchLocal)
    {
      // Do nothing
    }

    public boolean hasListeners()
    {
      return false;
    }

    public IListener[] getListeners()
    {
      return null;
    }

    public void addListener(IListener listener)
    {
      // Do nothing
    }

    public void removeListener(IListener listener)
    {
      // Do nothing
    }

    public void activate() throws LifecycleException
    {
      throw new UnsupportedOperationException();
    }

    public Exception deactivate()
    {
      return ServerCDOView.this.deactivate();
    }

    public LifecycleState getLifecycleState()
    {
      return LifecycleState.ACTIVE;
    }

    public boolean isActive()
    {
      return ServerCDOView.this.isActive();
    }

    public boolean isClosed()
    {
      return !isActive();
    }

    public void close()
    {
      deactivate();
    }

    public CDORepositoryInfo getRepositoryInfo()
    {
      return this;
    }

    public String getName()
    {
      return repository.getName();
    }

    public String getUUID()
    {
      return repository.getUUID();
    }

    public Type getType()
    {
      return repository.getType();
    }

    public State getState()
    {
      return repository.getState();
    }

    public long getCreationTime()
    {
      return repository.getCreationTime();
    }

    public long getTimeStamp()
    {
      return repository.getTimeStamp();
    }

    public long getTimeStamp(boolean forceRefresh)
    {
      return getTimeStamp();
    }

    public String getStoreType()
    {
      return repository.getStoreType();
    }

    public Set<ObjectType> getObjectIDTypes()
    {
      return repository.getObjectIDTypes();
    }

    public CDOID getRootResourceID()
    {
      return repository.getRootResourceID();
    }

    public boolean isSupportingAudits()
    {
      return repository.isSupportingAudits();
    }

    public boolean isSupportingBranches()
    {
      return repository.isSupportingBranches();
    }

    public boolean isSupportingEcore()
    {
      return repository.isSupportingEcore();
    }

    public boolean isEnsuringReferentialIntegrity()
    {
      return repository.isEnsuringReferentialIntegrity();
    }

    public IDGenerationLocation getIDGenerationLocation()
    {
      return repository.getIDGenerationLocation();
    }

    public void handleRepositoryTypeChanged(Type oldType, Type newType)
    {
    }

    public void handleRepositoryStateChanged(State oldState, State newState)
    {
    }

    public EPackage[] loadPackages(CDOPackageUnit packageUnit)
    {
      return null;
    }

    public void releaseAtomicRequestLock(Object key)
    {
      // Do nothing
    }

    public void acquireAtomicRequestLock(Object key)
    {
      // Do nothing
    }

    public Object processPackage(Object value)
    {
      return value;
    }

    public boolean isEmpty()
    {
      return false;
    }

    public boolean waitForUpdate(long updateTime, long timeoutMillis)
    {
      throw new UnsupportedOperationException();
    }

    public void waitForUpdate(long updateTime)
    {
      throw new UnsupportedOperationException();
    }

    public long getLastUpdateTime()
    {
      return getBranchPoint().getTimeStamp();
    }

    public String getUserID()
    {
      return null;
    }

    public int getSessionID()
    {
      return internalSession.getSessionID();
    }

    public long refresh()
    {
      throw new UnsupportedOperationException();
    }

    public Options options()
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(String durableLockingID)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(String durableLockingID, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView()
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(long timeStamp)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(CDOBranch branch)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(CDOBranch branch, long timeStamp)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(CDOBranch branch, long timeStamp, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction(CDOBranchPoint target, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction(CDOBranchPoint target)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(CDOBranchPoint target, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(CDOBranchPoint target)
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction(String durableLockingID)
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction(String durableLockingID, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction()
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction(CDOBranch branch)
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction(ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction(CDOBranch branch, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOFetchRuleManager getFetchRuleManager()
    {
      return null;
    }

    public ExceptionHandler getExceptionHandler()
    {
      return null;
    }

    public CDOIDGenerator getIDGenerator()
    {
      return null;
    }

    public void viewDetached(InternalCDOView view)
    {
      // Do nothing
    }

    public void setUserID(String userID)
    {
      throw new UnsupportedOperationException();
    }

    public void setSessionProtocol(CDOSessionProtocol sessionProtocol)
    {
      throw new UnsupportedOperationException();
    }

    public void setSessionID(int sessionID)
    {
      throw new UnsupportedOperationException();
    }

    public void setRepositoryInfo(CDORepositoryInfo repositoryInfo)
    {
      throw new UnsupportedOperationException();
    }

    public void setRemoteSessionManager(InternalCDORemoteSessionManager remoteSessionManager)
    {
      throw new UnsupportedOperationException();
    }

    public void setLastUpdateTime(long lastUpdateTime)
    {
      throw new UnsupportedOperationException();
    }

    public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
    {
      throw new UnsupportedOperationException();
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler)
    {
      throw new UnsupportedOperationException();
    }

    public void setIDGenerator(CDOIDGenerator idGenerator)
    {
      throw new UnsupportedOperationException();
    }

    public Object resolveElementProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex)
    {
      throw new UnsupportedOperationException();
    }

    public void resolveAllElementProxies(CDORevision revision)
    {
      throw new UnsupportedOperationException();
    }

    public void processRefreshSessionResult(RefreshSessionResult result, CDOBranch branch,
        List<InternalCDOView> branchViews, Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
    {
      throw new UnsupportedOperationException();
    }

    public void invalidate(CDOCommitInfo commitInfo, InternalCDOTransaction sender)
    {
      throw new UnsupportedOperationException();
    }

    public void handleCommitNotification(CDOCommitInfo commitInfo)
    {
      throw new UnsupportedOperationException();
    }

    public void handleLockNotification(CDOLockChangeInfo lockChangeInfo, InternalCDOView sender)
    {
      throw new UnsupportedOperationException();
    }

    public void handleBranchNotification(InternalCDOBranch branch)
    {
      throw new UnsupportedOperationException();
    }

    public InternalCDORemoteSessionManager getRemoteSessionManager()
    {
      throw new UnsupportedOperationException();
    }

    public CDOAuthenticator getAuthenticator()
    {
      throw new UnsupportedOperationException();
    }

    public void setAuthenticator(CDOAuthenticator authenticator)
    {
      throw new UnsupportedOperationException();
    }

    public void setRevisionManager(InternalCDORevisionManager revisionManager)
    {
      throw new UnsupportedOperationException();
    }

    public void setBranchManager(InternalCDOBranchManager branchManager)
    {
      throw new UnsupportedOperationException();
    }

    public void setCommitInfoManager(InternalCDOCommitInfoManager commitInfoManager)
    {
      throw new UnsupportedOperationException();
    }

    public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isSticky()
    {
      return false;
    }

    public CDOBranchPoint getCommittedSinceLastRefresh(CDOID id)
    {
      throw new UnsupportedOperationException();
    }

    public void setCommittedSinceLastRefresh(CDOID id, CDOBranchPoint branchPoint)
    {
      throw new UnsupportedOperationException();
    }

    public void clearCommittedSinceLastRefresh()
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @since Snow Owl 2.6
     */
    public CDOChangeSetData compareRevisions(CDOBranchPoint source, CDOBranchPoint target, String... nsURIs)
    {
      throw new UnsupportedOperationException();
    }

    public CDORevisionAvailabilityInfo createRevisionAvailabilityInfo(CDOBranchPoint branchPoint)
    {
      throw new UnsupportedOperationException();
    }

    public void cacheRevisions(CDORevisionAvailabilityInfo info)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(CDOBranch branch, long timeStamp, boolean shouldInvalidate)
    {
      return null;
    }
  }

  /**
   * @since 4.0.1
   */
  public boolean shouldInvalidate()
  {
    return false;
  }
}
