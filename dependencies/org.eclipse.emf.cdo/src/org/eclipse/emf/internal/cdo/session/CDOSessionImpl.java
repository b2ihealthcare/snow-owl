/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSingleValueFeatureDeltaImpl;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureDeltaVisitorImpl;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.analyzer.NOOPFetchRuleManager;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.session.remote.CDORemoteSessionManagerImpl;
import org.eclipse.emf.internal.cdo.util.DefaultLocksChangedEvent;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.IRWOLockManager;
import org.eclipse.net4j.util.concurrent.QueueRunner;
import org.eclipse.net4j.util.concurrent.RWOLockManager;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.options.OptionsEvent;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class CDOSessionImpl extends CDOTransactionContainerImpl implements InternalCDOSession
{
  private ExceptionHandler exceptionHandler;

  private CDOIDGenerator idGenerator;

  private InternalCDOPackageRegistry packageRegistry;

  private InternalCDOBranchManager branchManager;

  private InternalCDORevisionManager revisionManager;

  private InternalCDOCommitInfoManager commitInfoManager;

  private CDOSessionProtocol sessionProtocol;

  @ExcludeFromDump
  private IListener sessionProtocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      sessionProtocolDeactivated();
    }
  };

  private int sessionID;

  private String userID;

  private long lastUpdateTime;

  @ExcludeFromDump
  private LastUpdateTimeLock lastUpdateTimeLock = new LastUpdateTimeLock();

  private CDOSession.Options options = createOptions();

  private OutOfSequenceInvalidations outOfSequenceInvalidations = new OutOfSequenceInvalidations();

  private QueueRunner invalidationRunner;

  private CDORepositoryInfo repositoryInfo;

  private CDOFetchRuleManager fetchRuleManager;

  private IRWOLockManager<CDOSessionImpl, Object> lockManager = new RWOLockManager<CDOSessionImpl, Object>();

  @ExcludeFromDump
  private Set<CDOSessionImpl> singletonCollection = Collections.singleton(this);

  private boolean mainBranchLocal;

  private CDOAuthenticator authenticator;

  private InternalCDORemoteSessionManager remoteSessionManager;

  /**
   * A map to track for every object that was committed since this session's last refresh, onto what CDOBranchPoint it
   * was committed. (Used only for sticky transactions, see bug 290032 - Sticky views.)
   */
  private Map<CDOID, CDOBranchPoint> committedSinceLastRefresh = new HashMap<CDOID, CDOBranchPoint>();

  static
  {
    // Ensure that these 3 packages are registered with the global package registry in stand-alone
    EcorePackage.eINSTANCE.getClass();
    EresourcePackage.eINSTANCE.getClass();
    EtypesPackage.eINSTANCE.getClass();
  }

  public CDOSessionImpl()
  {
  }

  public CDORepositoryInfo getRepositoryInfo()
  {
    return repositoryInfo;
  }

  public void setRepositoryInfo(CDORepositoryInfo repositoryInfo)
  {
    this.repositoryInfo = repositoryInfo;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public void setSessionID(int sessionID)
  {
    this.sessionID = sessionID;
  }

  public String getUserID()
  {
    return userID;
  }

  public void setUserID(String userID)
  {
    this.userID = userID;
  }

  public ExceptionHandler getExceptionHandler()
  {
    return exceptionHandler;
  }

  public void setExceptionHandler(ExceptionHandler exceptionHandler)
  {
    checkInactive();
    this.exceptionHandler = exceptionHandler;
  }

  public CDOIDGenerator getIDGenerator()
  {
    return idGenerator;
  }

  public void setIDGenerator(CDOIDGenerator idGenerator)
  {
    checkInactive();
    this.idGenerator = idGenerator;
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry)
  {
    this.packageRegistry = packageRegistry;
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

  public InternalCDORevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  public void setRevisionManager(InternalCDORevisionManager revisionManager)
  {
    checkInactive();
    this.revisionManager = revisionManager;
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

  public CDOSessionProtocol getSessionProtocol()
  {
    return sessionProtocol;
  }

  public void setSessionProtocol(CDOSessionProtocol sessionProtocol)
  {
    checkInactive();
    if (exceptionHandler == null)
    {
      this.sessionProtocol = sessionProtocol;
    }
    else
    {
      if (this.sessionProtocol instanceof DelegatingSessionProtocol)
      {
        ((DelegatingSessionProtocol)this.sessionProtocol).setDelegate(sessionProtocol);
      }
      else
      {
        this.sessionProtocol = new DelegatingSessionProtocol(sessionProtocol, exceptionHandler);
      }
    }
  }

  /**
   * @since 3.0
   */
  public CDOFetchRuleManager getFetchRuleManager()
  {
    return fetchRuleManager;
  }

  /**
   * @since 3.0
   */
  public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
  {
    if (fetchRuleManager == null)
    {
      fetchRuleManager = new NOOPFetchRuleManager()
      {
        public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
        {
          return options().getCollectionLoadingPolicy();
        }
      };
    }

    this.fetchRuleManager = fetchRuleManager;
  }

  public CDOAuthenticator getAuthenticator()
  {
    return authenticator;
  }

  public void setAuthenticator(CDOAuthenticator authenticator)
  {
    this.authenticator = authenticator;
  }

  public boolean isMainBranchLocal()
  {
    return mainBranchLocal;
  }

  public void setMainBranchLocal(boolean mainBranchLocal)
  {
    this.mainBranchLocal = mainBranchLocal;
  }

  public InternalCDORemoteSessionManager getRemoteSessionManager()
  {
    return remoteSessionManager;
  }

  public void setRemoteSessionManager(InternalCDORemoteSessionManager remoteSessionManager)
  {
    this.remoteSessionManager = remoteSessionManager;
  }

  public CDOLobStore getLobStore()
  {
    final CDOLobStore cache = options().getLobCache();
    return new CDOLobStore.Delegating()
    {
      @Override
      public InputStream getBinary(final CDOLobInfo info) throws IOException
      {
        for (;;)
        {
          try
          {
            return super.getBinary(info);
          }
          catch (FileNotFoundException couldNotBeRead)
          {
            try
            {
              loadBinary(info);
            }
            catch (FileNotFoundException couldNotBeCreated)
            {
              // Try to read again
            }
          }
        }
      }

      @Override
      public Reader getCharacter(CDOLobInfo info) throws IOException
      {
        for (;;)
        {
          try
          {
            return super.getCharacter(info);
          }
          catch (FileNotFoundException couldNotBeRead)
          {
            try
            {
              loadCharacter(info);
            }
            catch (FileNotFoundException couldNotBeCreated)
            {
              // Try to read again
            }
          }
        }
      }

      private void loadBinary(final CDOLobInfo info) throws IOException
      {
        final File file = getDelegate().getBinaryFile(info.getID());
        final FileOutputStream out = new FileOutputStream(file);

        loadLobAsync(info, new Runnable()
        {
          public void run()
          {
            try
            {
              getSessionProtocol().loadLob(info, out);
            }
            catch (Throwable t)
            {
              OM.LOG.error(t);
              IOUtil.delete(file);
            }
          }
        });
      }

      private void loadCharacter(final CDOLobInfo info) throws IOException
      {
        final File file = getDelegate().getCharacterFile(info.getID());
        final FileWriter out = new FileWriter(file);

        loadLobAsync(info, new Runnable()
        {
          public void run()
          {
            try
            {
              getSessionProtocol().loadLob(info, out);
            }
            catch (Throwable t)
            {
              OM.LOG.error(t);
              IOUtil.delete(file);
            }
          }
        });
      }

      @Override
      protected CDOLobStore getDelegate()
      {
        return cache;
      }
    };
  }

  protected void loadLobAsync(CDOLobInfo info, Runnable runnable)
  {
    new Thread(runnable, "LobLoader").start();
  }

  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  /**
   * @since 2.0
   */
  public boolean isClosed()
  {
    return !isActive();
  }

  /**
   * @since 2.0
   */
  public CDOSession.Options options()
  {
    return options;
  }

  /**
   * @since 2.0
   */
  protected CDOSession.Options createOptions()
  {
    return new OptionsImpl();
  }

  public Object processPackage(Object value)
  {
    CDOFactoryImpl.prepareDynamicEPackage(value);
    return value;
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    if (packageUnit.getOriginalType().isGenerated())
    {
      if (!options().isGeneratedPackageEmulationEnabled())
      {
        throw new CDOException(MessageFormat.format(Messages.getString("CDOSessionImpl.0"), packageUnit)); //$NON-NLS-1$
      }
    }

    return getSessionProtocol().loadPackages(packageUnit);
  }

  public void acquireAtomicRequestLock(Object key)
  {
    try
    {
      lockManager.lock(LockType.WRITE, key, this, IRWLockManager.WAIT);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public void releaseAtomicRequestLock(Object key)
  {
    lockManager.unlock(LockType.WRITE, key, singletonCollection);
  }

  @Override
  protected void initViewSynced(InternalCDOView view)
  {
    view.setSession(this);
    view.setLastUpdateTime(getLastUpdateTime());
  }

  @Override
  protected CDOBranch getMainBranch()
  {
    return getBranchManager().getMainBranch();
  }

  /**
   * @since 2.0
   */
  public long refresh()
  {
    checkActive();
    if (options().isPassiveUpdateEnabled())
    {
      return CDOBranchPoint.UNSPECIFIED_DATE;
    }

    return refresh(false);
  }

  private long refresh(boolean enablePassiveUpdates)
  {
    Map<CDOBranch, List<InternalCDOView>> views = new HashMap<CDOBranch, List<InternalCDOView>>();
    Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions = new HashMap<CDOBranch, Map<CDOID, InternalCDORevision>>();
    collectViewedRevisions(views, viewedRevisions);
    cleanupRevisionCache(viewedRevisions);

    CDOSessionProtocol sessionProtocol = getSessionProtocol();
    long lastUpdateTime = getLastUpdateTime();
    int initialChunkSize = options().getCollectionLoadingPolicy().getInitialChunkSize();

    RefreshSessionResult result = sessionProtocol.refresh(lastUpdateTime, viewedRevisions, initialChunkSize,
        enablePassiveUpdates);

    setLastUpdateTime(result.getLastUpdateTime());
    registerPackageUnits(result.getPackageUnits());

    for (Entry<CDOBranch, List<InternalCDOView>> entry : views.entrySet())
    {
      CDOBranch branch = entry.getKey();
      List<InternalCDOView> branchViews = entry.getValue();
      processRefreshSessionResult(result, branch, branchViews, viewedRevisions);
    }

    return result.getLastUpdateTime();
  }

  public void processRefreshSessionResult(RefreshSessionResult result, CDOBranch branch,
      List<InternalCDOView> branchViews, Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
  {
    Map<CDOID, InternalCDORevision> oldRevisions = viewedRevisions.get(branch);

    List<CDORevisionKey> changedObjects = new ArrayList<CDORevisionKey>();
    List<InternalCDORevision> newRevisions = result.getChangedObjects(branch);
    for (InternalCDORevision newRevision : newRevisions)
    {
      getRevisionManager().addRevision(newRevision);

      InternalCDORevision oldRevision = oldRevisions.get(newRevision.getID());
      InternalCDORevisionDelta delta = newRevision.compare(oldRevision);
      changedObjects.add(delta);
    }

    List<CDOIDAndVersion> detachedObjects = result.getDetachedObjects(branch);
    for (CDOIDAndVersion detachedObject : detachedObjects)
    {
      getRevisionManager().reviseLatest(detachedObject.getID(), branch);
    }

    for (InternalCDOView view : branchViews)
    {
      view.invalidate(view.getBranch(), result.getLastUpdateTime(), changedObjects, detachedObjects, oldRevisions,
          false);
    }
  }

  /*
   * Custom code, non-API
   */
  public CDOView openView(CDOBranch branch, long timeStamp, boolean shouldInvalidate)
  {
    checkActive();
    InternalCDOView view = new CDOViewImpl(branch, timeStamp, shouldInvalidate);
    initView(view, createResourceSet());
    return view;
  }

  private void collectViewedRevisions(Map<CDOBranch, List<InternalCDOView>> views,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
  {
    for (InternalCDOView view : getViews())
    {
      if (view.getTimeStamp() == CDOView.UNSPECIFIED_DATE)
      {
        CDOBranch branch = view.getBranch();
        Map<CDOID, InternalCDORevision> revisions = viewedRevisions.get(branch);
        boolean needNewMap = revisions == null;
        if (needNewMap)
        {
          revisions = new HashMap<CDOID, InternalCDORevision>();
        }

        view.collectViewedRevisions(revisions);
        if (!revisions.isEmpty())
        {
          List<InternalCDOView> list = views.get(branch);
          if (list == null)
          {
            list = new ArrayList<InternalCDOView>();
            views.put(branch, list);
          }

          list.add(view);

          if (needNewMap)
          {
            viewedRevisions.put(branch, revisions);
          }
        }
      }
    }
  }

  private void cleanupRevisionCache(Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
  {
    Set<InternalCDORevision> set = new HashSet<InternalCDORevision>();
    for (Map<CDOID, InternalCDORevision> revisions : viewedRevisions.values())
    {
      for (InternalCDORevision revision : revisions.values())
      {
        set.add(revision);
      }
    }

    InternalCDORevisionCache cache = getRevisionManager().getCache();
    List<CDORevision> currentRevisions = cache.getCurrentRevisions();
    for (CDORevision revision : currentRevisions)
    {
      if (!set.contains(revision))
      {
        cache.removeRevision(revision.getID(), revision);
      }
    }
  }

  public long getLastUpdateTime()
  {
    synchronized (lastUpdateTimeLock)
    {
      return lastUpdateTime;
    }
  }

  public void setLastUpdateTime(long lastUpdateTime)
  {
    synchronized (lastUpdateTimeLock)
    {
      if (this.lastUpdateTime < lastUpdateTime)
      {
        this.lastUpdateTime = lastUpdateTime;
      }

      lastUpdateTimeLock.notifyAll();
    }
  }

  public void waitForUpdate(long updateTime)
  {
    waitForUpdate(updateTime, NO_TIMEOUT);
  }

  public boolean waitForUpdate(long updateTime, long timeoutMillis)
  {
    long end = timeoutMillis == NO_TIMEOUT ? Long.MAX_VALUE : System.currentTimeMillis() + timeoutMillis;
    InternalCDOView views[] = getViews();
    if (views.length > 0)
    {
      for (CDOView view : views)
      {
        long viewTimeoutMillis = timeoutMillis == NO_TIMEOUT ? NO_TIMEOUT : end - System.currentTimeMillis();
        boolean ok = view.waitForUpdate(updateTime, viewTimeoutMillis);
        if (!ok)
        {
          return false;
        }
      }

      return true;
    }

    // Session without views
    for (;;)
    {
      synchronized (lastUpdateTimeLock)
      {
        if (lastUpdateTime >= updateTime)
        {
          return true;
        }

        long now = System.currentTimeMillis();
        if (now >= end)
        {
          return false;
        }

        try
        {
          lastUpdateTimeLock.wait(end - now);
        }
        catch (InterruptedException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }
  }

  /**
   * @since 3.0
   */
  public Object resolveElementProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex)
  {
    if (!((InternalCDORevision)revision).isUnchunked())
    {
      CDOCollectionLoadingPolicy policy = options().getCollectionLoadingPolicy();
      return policy.resolveProxy(revision, feature, accessIndex, serverIndex);
    }

    return revision.data().get(feature, accessIndex);
  }

  /**
   * @since 4.0
   */
  public void resolveAllElementProxies(CDORevision revision)
  {
    if (!((InternalCDORevision)revision).isUnchunked())
    {
      CDOCollectionLoadingPolicy policy = options().getCollectionLoadingPolicy();
      for (EStructuralFeature feature : revision.getEClass().getEAllStructuralFeatures())
      {
        if (feature instanceof EReference)
        {
          EReference reference = (EReference)feature;
          if (reference.isMany() && EMFUtil.isPersistent(reference))
          {
            CDOList list = ((InternalCDORevision)revision).getList(reference);
            for (Iterator<Object> it = list.iterator(); it.hasNext();)
            {
              Object element = it.next();
              if (element instanceof CDOElementProxy)
              {
                policy.resolveAllProxies(revision, reference);
                break;
              }
            }
          }
        }
      }

      ((InternalCDORevision)revision).setUnchunked();
    }
  }

  public void handleRepositoryTypeChanged(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType)
  {
    fireEvent(new RepositoryTypeChangedEvent(this, oldType, newType));
  }

  public void handleRepositoryStateChanged(CDOCommonRepository.State oldState, CDOCommonRepository.State newState)
  {
    fireEvent(new RepositoryStateChangedEvent(this, oldState, newState));
  }

  @Deprecated
  public void handleBranchNotification(InternalCDOBranch branch)
  {
    throw new UnsupportedOperationException();
  }

  public void handleCommitNotification(CDOCommitInfo commitInfo)
  {
    try
    {
      registerPackageUnits(commitInfo.getNewPackageUnits());
      invalidate(commitInfo, null);
    }
    catch (RuntimeException ex)
    {
      if (isActive())
      {
        OM.LOG.error(ex);
      }
      else
      {
        OM.LOG.info(Messages.getString("CDOSessionImpl.2")); //$NON-NLS-1$
      }
    }
  }

  public void handleLockNotification(CDOLockChangeInfo lockChangeInfo, InternalCDOView sender)
  {
    for (InternalCDOView view : getViews())
    {
      if (view != sender)
      {
        view.handleLockNotification(sender, lockChangeInfo);
      }
    }

    fireEvent(new LocksChangedEvent(sender, lockChangeInfo));
  }

  private void registerPackageUnits(List<CDOPackageUnit> packageUnits)
  {
    InternalCDOPackageRegistry packageRegistry = getPackageRegistry();
    for (CDOPackageUnit newPackageUnit : packageUnits)
    {
      packageRegistry.putPackageUnit((InternalCDOPackageUnit)newPackageUnit);
    }
  }

  private Map<CDOID, InternalCDORevision> reviseRevisions(CDOCommitInfo commitInfo)
  {
    Map<CDOID, InternalCDORevision> oldRevisions = null;
    CDOBranch newBranch = commitInfo.getBranch();
    long timeStamp = commitInfo.getTimeStamp();
    InternalCDORevisionManager revisionManager = getRevisionManager();

    // Cache new revisions
    for (CDOIDAndVersion key : commitInfo.getNewObjects())
    {
      if (key instanceof InternalCDORevision)
      {
        InternalCDORevision newRevision = (InternalCDORevision)key;
        revisionManager.addRevision(newRevision);
      }
    }

    // Apply deltas and cache the resulting new revisions, if possible...
    for (CDORevisionKey key : commitInfo.getChangedObjects())
    {
      // Add old values to revision deltas.
      if (key instanceof CDORevisionDelta)
      {
        final CDORevisionDelta revisionDelta = (CDORevisionDelta)key;
        final CDORevision oldRevision = revisionManager.getRevisionByVersion(revisionDelta.getID(), revisionDelta,
            CDORevision.UNCHUNKED, false);

        if (oldRevision != null)
        {
          CDOFeatureDeltaVisitor visitor = new CDOFeatureDeltaVisitorImpl()
          {
            private List<Object> workList;

            @Override
            public void visit(CDOAddFeatureDelta delta)
            {
              workList.add(delta.getIndex(), delta.getValue());
            }

            @Override
            public void visit(CDOClearFeatureDelta delta)
            {
              workList.clear();
            }

            @Override
            public void visit(CDOListFeatureDelta deltas)
            {
              @SuppressWarnings("unchecked")
              List<Object> list = (List<Object>)((InternalCDORevision)oldRevision).getValue(deltas.getFeature());
              if (list != null)
              {
                workList = new ArrayList<Object>(list);
                super.visit(deltas);
              }
            }

            @Override
            public void visit(CDOMoveFeatureDelta delta)
            {
              Object value = workList.get(delta.getOldPosition());
              ((CDOMoveFeatureDeltaImpl)delta).setValue(value);
              ECollections.move(workList, delta.getNewPosition(), delta.getOldPosition());
            }

            @Override
            public void visit(CDORemoveFeatureDelta delta)
            {
              Object oldValue = workList.remove(delta.getIndex());
              ((CDOSingleValueFeatureDeltaImpl)delta).setValue(oldValue);
            }

            @Override
            public void visit(CDOSetFeatureDelta delta)
            {
              EStructuralFeature feature = delta.getFeature();
              Object value = null;
              if (feature.isMany())
              {
                value = workList.set(delta.getIndex(), delta.getValue());
              }
              else
              {
                value = ((InternalCDORevision)oldRevision).getValue(feature);
              }

              ((CDOSetFeatureDeltaImpl)delta).setOldValue(value);
            }
          };

          for (CDOFeatureDelta featureDelta : revisionDelta.getFeatureDeltas())
          {
            featureDelta.accept(visitor);
          }
        }
      }

      CDOID id = key.getID();
      Pair<InternalCDORevision, InternalCDORevision> pair = createNewRevision(key, commitInfo);
      if (pair != null)
      {
        InternalCDORevision newRevision = pair.getElement2();
        revisionManager.addRevision(newRevision);
        if (oldRevisions == null)
        {
          oldRevisions = new HashMap<CDOID, InternalCDORevision>();
        }

        InternalCDORevision oldRevision = pair.getElement1();
        oldRevisions.put(id, oldRevision);
      }
      else
      {
        // ... otherwise try to revise old revision if it is in the same branch
        if (ObjectUtil.equals(key.getBranch(), newBranch))
        {
          revisionManager.reviseVersion(id, key, timeStamp);
        }
        else
        {
          revisionManager.reviseVersion(id, newBranch.getVersion(CDOBranchVersion.UNSPECIFIED_VERSION), timeStamp);
        }
      }
    }

    // Revise old revisions
    for (CDOIDAndVersion key : commitInfo.getDetachedObjects())
    {
      CDOID id = key.getID();
      int version = key.getVersion();
      if (version == CDOBranchVersion.UNSPECIFIED_VERSION)
      {
        revisionManager.reviseLatest(id, newBranch);
      }
      else
      {
        CDOBranchVersion branchVersion = newBranch.getVersion(version);
        revisionManager.reviseVersion(id, branchVersion, timeStamp);
      }
    }

    return oldRevisions;
  }

  private Pair<InternalCDORevision, InternalCDORevision> createNewRevision(CDORevisionKey potentialDelta,
      CDOCommitInfo commitInfo)
  {
    if (potentialDelta instanceof CDORevisionDelta)
    {
      CDORevisionDelta delta = (CDORevisionDelta)potentialDelta;
      CDOID id = delta.getID();

      InternalCDORevisionManager revisionManager = getRevisionManager();
      InternalCDORevision oldRevision = revisionManager.getRevisionByVersion(id, potentialDelta, CDORevision.UNCHUNKED,
          false);
      if (oldRevision != null)
      {
        InternalCDORevision newRevision = oldRevision.copy();
        newRevision.adjustForCommit(commitInfo.getBranch(), commitInfo.getTimeStamp());

        CDORevisable target = delta.getTarget();
        if (target != null)
        {
          newRevision.setVersion(target.getVersion());
        }

        delta.apply(newRevision);
        newRevision.freeze();
        return new Pair<InternalCDORevision, InternalCDORevision>(oldRevision, newRevision);
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public void invalidate(CDOCommitInfo commitInfo, InternalCDOTransaction sender)
  {
    long previousTimeStamp = commitInfo.getPreviousTimeStamp();
    long lastUpdateTime = getLastUpdateTime();

    if (previousTimeStamp < lastUpdateTime)
    {
      previousTimeStamp = lastUpdateTime;
    }

    synchronized (outOfSequenceInvalidations)
    {
      Pair<CDOCommitInfo, InternalCDOTransaction> oldPair = outOfSequenceInvalidations.put(previousTimeStamp,
          new Pair<CDOCommitInfo, InternalCDOTransaction>(commitInfo, sender));

      if (oldPair != null && oldPair.getElement1() != null && !(oldPair.getElement1() instanceof FailureCommitInfo))
      {
        if (commitInfo instanceof FailureCommitInfo)
        {
          // Restore non-failure commit info in case it is kicked out
          outOfSequenceInvalidations.put(previousTimeStamp, oldPair);
        }
        else
        {
          OM.LOG.error(MessageFormat.format("Commit infos registered on same timestamp {0}: {1}, {2}",
              previousTimeStamp, commitInfo, oldPair.getElement1()));
        }
      }
    }

    long nextPreviousTimeStamp = lastUpdateTime;
    for (;;)
    {
      synchronized (outOfSequenceInvalidations)
      {
        Pair<CDOCommitInfo, InternalCDOTransaction> currentPair = outOfSequenceInvalidations
            .remove(nextPreviousTimeStamp);

        // If we don't have the invalidation that follows the last one we processed,
        // then there is nothing we can do right now
        if (currentPair == null)
        {
          break;
        }
        final CDOCommitInfo currentCommitInfo = currentPair.getElement1();
        final InternalCDOTransaction currentSender = currentPair.getElement2();
        nextPreviousTimeStamp = currentCommitInfo.getTimeStamp();

        if (options.isPassiveUpdateEnabled())
        {
          setLastUpdateTime(currentCommitInfo.getTimeStamp());
        }

        if (sender == null)
        {
          QueueRunner invalidationRunner = getInvalidationRunner();
          invalidationRunner.addWork(new Runnable()
          {
            public void run()
            {
              invalidateOrdered(currentCommitInfo, currentSender);
            }
          });
        }
        else
        {
          invalidateOrdered(currentCommitInfo, currentSender);
        }
      }
    }
  }

  /**
   * This method is synchronized on outOfSequenceInvalidations by the caller!
   */
  private QueueRunner getInvalidationRunner()
  {
    if (invalidationRunner == null)
    {
      invalidationRunner = new QueueRunner();
      invalidationRunner.activate();
    }

    return invalidationRunner;
  }

  private void invalidateOrdered(CDOCommitInfo commitInfo, InternalCDOTransaction sender)
  {
    Map<CDOID, InternalCDORevision> oldRevisions = null;
    boolean success = commitInfo.getBranch() != null;
    if (success)
    {
      oldRevisions = reviseRevisions(commitInfo);
    }

    if (success)
    {
      fireInvalidationEvent(sender, commitInfo);
    }

    for (InternalCDOView view : getViews())
    {
      if (view != sender)
      {
        invalidateView(commitInfo, view, oldRevisions);
      }
      else
      {
        view.setLastUpdateTime(commitInfo.getTimeStamp());
      }
    }
  }

  private void invalidateView(CDOCommitInfo commitInfo, InternalCDOView view,
      Map<CDOID, InternalCDORevision> oldRevisions)
  {
    try
    {
      if (view.shouldInvalidate())
      {
        CDOBranch branch = commitInfo.getBranch();
        long lastUpdateTime = commitInfo.getTimeStamp();
        List<CDORevisionKey> allChangedObjects = commitInfo.getChangedObjects();
        List<CDOIDAndVersion> allDetachedObjects = commitInfo.getDetachedObjects();
        view.invalidate(branch, lastUpdateTime, allChangedObjects, allDetachedObjects, oldRevisions, true);
      }
    }
    catch (RuntimeException ex)
    {
      if (view.isActive())
      {
        OM.LOG.error(ex);
      }
      else
      {
        OM.LOG.info(Messages.getString("CDOSessionImpl.1")); //$NON-NLS-1$
      }
    }
  }

  /**
   * @since 2.0
   */
  public void fireInvalidationEvent(InternalCDOTransaction sender, CDOCommitInfo commitInfo)
  {
    fireEvent(new InvalidationEvent(sender, commitInfo));
  }

  @Override
  public String toString()
  {
    String name = repositoryInfo == null ? "?" : repositoryInfo.getName(); //$NON-NLS-1$
    return MessageFormat.format("Session{0} [{1}]", sessionID, name); //$NON-NLS-1$
  }

  public CDOBranchPoint getCommittedSinceLastRefresh(CDOID id)
  {
    if (isSticky())
    {
      return committedSinceLastRefresh.get(id);
    }

    return null;
  }

  public void setCommittedSinceLastRefresh(CDOID id, CDOBranchPoint branchPoint)
  {
    if (isSticky())
    {
      committedSinceLastRefresh.put(id, branchPoint);
    }
  }

  public void clearCommittedSinceLastRefresh()
  {
    if (isSticky())
    {
      committedSinceLastRefresh.clear();
    }
  }

  public boolean isSticky()
  {
    return !options().isPassiveUpdateEnabled() && getRepositoryInfo().isSupportingAudits();
  }

  public CDOChangeSetData compareRevisions(CDOBranchPoint source, CDOBranchPoint target, final String... nsURIs)
  {
    long now = getLastUpdateTime();

    if (target.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      target = target.getBranch().getPoint(now);
    }

    if (source.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      source = source.getBranch().getPoint(now);
    }

    CDORevisionAvailabilityInfo targetInfo = createRevisionAvailabilityInfo(target);
    CDORevisionAvailabilityInfo sourceInfo = createRevisionAvailabilityInfo(source);

    Set<CDOID> ids = sessionProtocol.loadMergeData(targetInfo, sourceInfo, null, null, nsURIs);

    cacheRevisions(targetInfo);
    cacheRevisions(sourceInfo);

    return CDORevisionUtil.createChangeSetData(ids, sourceInfo, targetInfo);
  }

  public CDORevisionAvailabilityInfo createRevisionAvailabilityInfo(CDOBranchPoint branchPoint)
  {
    CDORevisionAvailabilityInfo info = new CDORevisionAvailabilityInfo(branchPoint);

    InternalCDORevisionManager revisionManager = getRevisionManager();
    InternalCDORevisionCache cache = revisionManager.getCache();

    List<CDORevision> revisions = cache.getRevisions(branchPoint);
    for (CDORevision revision : revisions)
    {
      if (revision instanceof PointerCDORevision)
      {
        PointerCDORevision pointer = (PointerCDORevision)revision;
        CDOBranchVersion target = pointer.getTarget();
        if (target != null)
        {
          revision = cache.getRevisionByVersion(pointer.getID(), target);
        }
        else
        {
          // XXX (apeteri): a pointer revision without knowing the target does not a relevant revision make
          revision = null;
        }
      }
      else if (revision instanceof DetachedCDORevision)
      {
        revision = null;
      }

      if (revision != null)
      {
        resolveAllElementProxies(revision);
        info.addRevision(revision);
      }
    }

    return info;
  }

  public void cacheRevisions(CDORevisionAvailabilityInfo info)
  {
    InternalCDORevisionManager revisionManager = getRevisionManager();
    CDOBranch branch = info.getBranchPoint().getBranch();
    for (CDORevisionKey key : info.getAvailableRevisions().values())
    {
      CDORevision revision = (CDORevision)key;
      revisionManager.addRevision(revision);

      if (!ObjectUtil.equals(revision.getBranch(), branch))
      {
        CDOID id = revision.getID();
        CDORevision firstRevision = revisionManager.getCache().getRevisionByVersion(id,
            branch.getVersion(CDOBranchVersion.FIRST_VERSION));
        if (firstRevision != null)
        {
          long revised = firstRevision.getTimeStamp() - 1L;
          CDOBranchVersion target = CDOBranchUtil.copyBranchVersion(revision);
          PointerCDORevision pointer = new PointerCDORevision(revision.getEClass(), id, branch, revised, target);
          revisionManager.addRevision(pointer);
        }
      }
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    InternalCDORemoteSessionManager remoteSessionManager = new CDORemoteSessionManagerImpl();
    remoteSessionManager.setLocalSession(this);
    setRemoteSessionManager(remoteSessionManager);
    remoteSessionManager.activate();

    checkState(sessionProtocol, "sessionProtocol"); //$NON-NLS-1$
    checkState(remoteSessionManager, "remoteSessionManager"); //$NON-NLS-1$
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();

    LifecycleUtil.deactivate(invalidationRunner);
    outOfSequenceInvalidations.clear();

    unhookSessionProtocol();

    CDORemoteSessionManager remoteSessionManager = getRemoteSessionManager();
    setRemoteSessionManager(null);
    LifecycleUtil.deactivate(remoteSessionManager);

    CDOSessionProtocol sessionProtocol = getSessionProtocol();
    LifecycleUtil.deactivate(sessionProtocol);
    setSessionProtocol(null);
  }

  /**
   * Makes this session start listening to its protocol
   */
  protected CDOSessionProtocol hookSessionProtocol()
  {
    EventUtil.addListener(sessionProtocol, sessionProtocolListener);
    return sessionProtocol;
  }

  /**
   * Makes this session stop listening to its protocol
   */
  protected void unhookSessionProtocol()
  {
    EventUtil.removeListener(sessionProtocol, sessionProtocolListener);
  }

  protected void sessionProtocolDeactivated()
  {
    deactivate();
  }

  /**
   * A separate class for better monitor debugging.
   *
   * @author Eike Stepper
   */
  private static final class LastUpdateTimeLock
  {
  }

  /**
   * @author Eike Stepper
   */
  private static final class OutOfSequenceInvalidations extends
      HashMap<Long, Pair<CDOCommitInfo, InternalCDOTransaction>>
  {
    private static final long serialVersionUID = 1L;
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  protected class OptionsImpl extends Notifier implements Options
  {
    private boolean generatedPackageEmulationEnabled;

    private boolean passiveUpdateEnabled = true;

    private PassiveUpdateMode passiveUpdateMode = PassiveUpdateMode.INVALIDATIONS;

    private LockNotificationMode lockNotificationMode = LockNotificationMode.IF_REQUIRED_BY_VIEWS;

    private CDOCollectionLoadingPolicy collectionLoadingPolicy;

    private CDOLobStore lobCache = CDOLobStoreImpl.INSTANCE;

    public OptionsImpl()
    {
      setCollectionLoadingPolicy(null); // Init default
    }

    public CDOSession getContainer()
    {
      return CDOSessionImpl.this;
    }

    public boolean isGeneratedPackageEmulationEnabled()
    {
      return generatedPackageEmulationEnabled;
    }

    public synchronized void setGeneratedPackageEmulationEnabled(boolean generatedPackageEmulationEnabled)
    {
      this.generatedPackageEmulationEnabled = generatedPackageEmulationEnabled;
      if (this.generatedPackageEmulationEnabled != generatedPackageEmulationEnabled)
      {
        this.generatedPackageEmulationEnabled = generatedPackageEmulationEnabled;
        // TODO Check inconsistent state if switching off?

        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new GeneratedPackageEmulationEventImpl(), listeners);
        }
      }
    }

    public boolean isPassiveUpdateEnabled()
    {
      return passiveUpdateEnabled;
    }

    public synchronized void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
    {
      if (this.passiveUpdateEnabled != passiveUpdateEnabled)
      {
        this.passiveUpdateEnabled = passiveUpdateEnabled;
        CDOSessionProtocol protocol = getSessionProtocol();
        if (protocol != null)
        {
          if (passiveUpdateEnabled)
          {
            refresh(true);
          }
          else
          {
            protocol.disablePassiveUpdate();
          }

          IListener[] listeners = getListeners();
          if (listeners != null)
          {
            fireEvent(new PassiveUpdateEventImpl(!passiveUpdateEnabled, passiveUpdateEnabled, passiveUpdateMode,
                passiveUpdateMode), listeners);
          }
        }
      }
    }

    public PassiveUpdateMode getPassiveUpdateMode()
    {
      return passiveUpdateMode;
    }

    public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
    {
      checkArg(passiveUpdateMode, "passiveUpdateMode"); //$NON-NLS-1$
      if (this.passiveUpdateMode != passiveUpdateMode)
      {
        PassiveUpdateMode oldMode = this.passiveUpdateMode;
        this.passiveUpdateMode = passiveUpdateMode;
        CDOSessionProtocol protocol = getSessionProtocol();
        if (protocol != null)
        {
          protocol.setPassiveUpdateMode(passiveUpdateMode);

          IListener[] listeners = getListeners();
          if (listeners != null)
          {
            fireEvent(
                new PassiveUpdateEventImpl(passiveUpdateEnabled, passiveUpdateEnabled, oldMode, passiveUpdateMode),
                listeners);
          }
        }
      }
    }

    public LockNotificationMode getLockNotificationMode()
    {
      return lockNotificationMode;
    }

    public void setLockNotificationMode(LockNotificationMode lockNotificationMode)
    {
      checkArg(lockNotificationMode, "lockNotificationMode"); //$NON-NLS-1$
      if (this.lockNotificationMode != lockNotificationMode)
      {
        LockNotificationMode oldMode = this.lockNotificationMode;
        this.lockNotificationMode = lockNotificationMode;
        CDOSessionProtocol protocol = getSessionProtocol();
        if (protocol != null)
        {
          protocol.setLockNotificationMode(lockNotificationMode);

          IListener[] listeners = getListeners();
          if (listeners != null)
          {
            fireEvent(new LockNotificationModeEventImpl(oldMode, lockNotificationMode), listeners);
          }
        }
      }
      this.lockNotificationMode = lockNotificationMode;
    }

    public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
    {
      synchronized (this)
      {
        return collectionLoadingPolicy;
      }
    }

    public void setCollectionLoadingPolicy(CDOCollectionLoadingPolicy policy)
    {
      if (policy == null)
      {
        policy = CDOUtil.createCollectionLoadingPolicy(CDORevision.UNCHUNKED, CDORevision.UNCHUNKED);
      }

      CDOSession oldSession = policy.getSession();
      if (oldSession != null)
      {
        throw new IllegalArgumentException("Policy is already associated with " + oldSession);
      }

      policy.setSession(CDOSessionImpl.this);

      IListener[] listeners = getListeners();
      IEvent event = null;

      synchronized (this)
      {
        if (collectionLoadingPolicy != policy)
        {
          collectionLoadingPolicy = policy;
          if (listeners != null)
          {
            event = new CollectionLoadingPolicyEventImpl();
          }
        }
      }

      if (event != null)
      {
        fireEvent(event, listeners);
      }
    }

    public CDOLobStore getLobCache()
    {
      synchronized (this)
      {
        return lobCache;
      }
    }

    public void setLobCache(CDOLobStore cache)
    {
      if (cache == null)
      {
        cache = CDOLobStoreImpl.INSTANCE;
      }

      IListener[] listeners = getListeners();
      IEvent event = null;

      synchronized (this)
      {
        if (lobCache != cache)
        {
          lobCache = cache;
          if (listeners != null)
          {
            event = new LobCacheEventImpl();
          }
        }
      }

      if (event != null)
      {
        fireEvent(event, listeners);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class GeneratedPackageEmulationEventImpl extends OptionsEvent implements
        GeneratedPackageEmulationEvent
    {
      private static final long serialVersionUID = 1L;

      public GeneratedPackageEmulationEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class PassiveUpdateEventImpl extends OptionsEvent implements PassiveUpdateEvent
    {
      private static final long serialVersionUID = 1L;

      private boolean oldEnabled;

      private boolean newEnabled;

      private PassiveUpdateMode oldMode;

      private PassiveUpdateMode newMode;

      public PassiveUpdateEventImpl(boolean oldEnabled, boolean newEnabled, PassiveUpdateMode oldMode,
          PassiveUpdateMode newMode)
      {
        super(OptionsImpl.this);
        this.oldEnabled = oldEnabled;
        this.newEnabled = newEnabled;
        this.oldMode = oldMode;
        this.newMode = newMode;
      }

      public boolean getOldEnabled()
      {
        return oldEnabled;
      }

      public boolean getNewEnabled()
      {
        return newEnabled;
      }

      public PassiveUpdateMode getOldMode()
      {
        return oldMode;
      }

      public PassiveUpdateMode getNewMode()
      {
        return newMode;
      }
    }

    /**
     * @author Caspar De Groot
     */
    private final class LockNotificationModeEventImpl extends OptionsEvent implements LockNotificationModeEvent
    {
      private static final long serialVersionUID = 1L;

      private LockNotificationMode oldMode, newMode;

      public LockNotificationModeEventImpl(LockNotificationMode oldMode, LockNotificationMode newMode)
      {
        super(OptionsImpl.this);
        this.oldMode = oldMode;
        this.newMode = newMode;
      }

      public LockNotificationMode getOldMode()
      {
        return oldMode;
      }

      public LockNotificationMode getNewMode()
      {
        return newMode;
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class CollectionLoadingPolicyEventImpl extends OptionsEvent implements CollectionLoadingPolicyEvent
    {
      private static final long serialVersionUID = 1L;

      public CollectionLoadingPolicyEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class LobCacheEventImpl extends OptionsEvent implements LobCacheEvent
    {
      private static final long serialVersionUID = 1L;

      public LobCacheEventImpl()
      {
        super(OptionsImpl.this);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class InvalidationEvent extends Event implements CDOSessionInvalidationEvent
  {
    private static final long serialVersionUID = 1L;

    private InternalCDOTransaction sender;

    private CDOCommitInfo commitInfo;

    public InvalidationEvent(InternalCDOTransaction sender, CDOCommitInfo commitInfo)
    {
      super(CDOSessionImpl.this);
      this.sender = sender;
      this.commitInfo = commitInfo;
    }

    @Override
    public CDOSession getSource()
    {
      return (CDOSession)super.getSource();
    }

    public CDOCommitInfoManager getCommitInfoManager()
    {
      return commitInfo.getCommitInfoManager();
    }

    public CDOTransaction getLocalTransaction()
    {
      return sender;
    }

    @Deprecated
    public InternalCDOView getView()
    {
      return sender;
    }

    public boolean isRemote()
    {
      return sender == null;
    }

    public CDOBranch getBranch()
    {
      return commitInfo.getBranch();
    }

    public long getTimeStamp()
    {
      return commitInfo.getTimeStamp();
    }

    public long getPreviousTimeStamp()
    {
      return commitInfo.getPreviousTimeStamp();
    }

    public String getUserID()
    {
      return commitInfo.getUserID();
    }

    public String getComment()
    {
      return commitInfo.getComment();
    }

    public boolean isEmpty()
    {
      return false;
    }

    public CDOChangeSetData copy()
    {
      return commitInfo.copy();
    }

    public void merge(CDOChangeSetData changeSetData)
    {
      commitInfo.merge(changeSetData);
    }

    public List<CDOPackageUnit> getNewPackageUnits()
    {
      return commitInfo.getNewPackageUnits();
    }

    public List<CDOIDAndVersion> getNewObjects()
    {
      return commitInfo.getNewObjects();
    }

    public List<CDORevisionKey> getChangedObjects()
    {
      return commitInfo.getChangedObjects();
    }

    public List<CDOIDAndVersion> getDetachedObjects()
    {
      return commitInfo.getDetachedObjects();
    }

    public Map<CDOID, CDOChangeKind> getChangeKinds()
    {
      return commitInfo.getChangeKinds();
    }

    public CDOChangeKind getChangeKind(CDOID id)
    {
      return commitInfo.getChangeKind(id);
    }

    @Override
    public String toString()
    {
      return "CDOSessionInvalidationEvent[" + commitInfo + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * @author Caspar De Groot
   * @since 4.1
   */
  private final class LocksChangedEvent extends DefaultLocksChangedEvent implements CDOSessionLocksChangedEvent
  {
    private static final long serialVersionUID = 1L;

    public LocksChangedEvent(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
    {
      super(CDOSessionImpl.this, sender, lockChangeInfo);
    }

    @Override
    public CDOSession getSource()
    {
      return (CDOSession)super.getSource();
    }
  }
}
