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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationInfo;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
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

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockObjectsResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * @author Eike Stepper
 */
public abstract class DelegatingRepository implements InternalRepository
{
  public DelegatingRepository()
  {
  }

  protected abstract InternalRepository getDelegate();

  public void addHandler(Handler handler)
  {
    getDelegate().addHandler(handler);
  }

  public void addListener(IListener listener)
  {
    getDelegate().addListener(listener);
  }

  public long[] createCommitTimeStamp(OMMonitor monitor)
  {
    return getDelegate().createCommitTimeStamp(monitor);
  }

  public IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature, int chunkStart,
      int chunkEnd)
  {
    return getDelegate().ensureChunk(revision, feature, chunkStart, chunkEnd);
  }

  public InternalCommitManager getCommitManager()
  {
    return getDelegate().getCommitManager();
  }

  public long getCreationTime()
  {
    return getDelegate().getCreationTime();
  }

  public Object[] getElements()
  {
    return getDelegate().getElements();
  }

  public long getLastCommitTimeStamp()
  {
    return getDelegate().getLastCommitTimeStamp();
  }

  public IListener[] getListeners()
  {
    return getDelegate().getListeners();
  }

  @Deprecated
  public InternalLockManager getLockManager()
  {
    return getDelegate().getLockingManager();
  }

  public String getName()
  {
    return getDelegate().getName();
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return getDelegate().getPackageRegistry();
  }

  public InternalCDOPackageRegistry getPackageRegistry(boolean considerCommitContext)
  {
    return getDelegate().getPackageRegistry(considerCommitContext);
  }

  public Map<String, String> getProperties()
  {
    return getDelegate().getProperties();
  }

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    return getDelegate().getQueryHandler(info);
  }

  public IQueryHandlerProvider getQueryHandlerProvider()
  {
    return getDelegate().getQueryHandlerProvider();
  }

  public InternalQueryManager getQueryManager()
  {
    return getDelegate().getQueryManager();
  }

  public InternalCDORevisionManager getRevisionManager()
  {
    return getDelegate().getRevisionManager();
  }

  public InternalSessionManager getSessionManager()
  {
    return getDelegate().getSessionManager();
  }

  public InternalStore getStore()
  {
    return getDelegate().getStore();
  }

  public String getUUID()
  {
    return getDelegate().getUUID();
  }

  public boolean hasListeners()
  {
    return getDelegate().hasListeners();
  }

  public boolean isEmpty()
  {
    return getDelegate().isEmpty();
  }

  public boolean isSupportingAudits()
  {
    return getDelegate().isSupportingAudits();
  }

  public boolean isSupportingBranches()
  {
    return getDelegate().isSupportingBranches();
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    return getDelegate().loadPackages(packageUnit);
  }

  public InternalCDOBranchManager getBranchManager()
  {
    return getDelegate().getBranchManager();
  }

  public void setBranchManager(InternalCDOBranchManager branchManager)
  {
    getDelegate().setBranchManager(branchManager);
  }

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    return getDelegate().createBranch(branchID, branchInfo);
  }

  public BranchInfo loadBranch(int branchID)
  {
    return getDelegate().loadBranch(branchID);
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    return getDelegate().loadSubBranches(branchID);
  }

  public List<InternalCDORevision> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint,
      int referenceChunk, int prefetchDepth)
  {
    return getDelegate().loadRevisions(infos, branchPoint, referenceChunk, prefetchDepth);
  }

  public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
  {
    return getDelegate().loadRevisionByVersion(id, branchVersion, referenceChunk);
  }

  public void notifyReadAccessHandlers(InternalSession session, CDORevision[] revisions,
      List<CDORevision> additionalRevisions)
  {
    getDelegate().notifyReadAccessHandlers(session, revisions, additionalRevisions);
  }

  public void notifyWriteAccessHandlers(ITransaction transaction, CommitContext commitContext, boolean beforeCommit,
      OMMonitor monitor)
  {
    getDelegate().notifyWriteAccessHandlers(transaction, commitContext, beforeCommit, monitor);
  }

  public void rollbackWriteAccessHandlers(ITransaction transaction, CommitContext commitContext)
  {
    getDelegate().rollbackWriteAccessHandlers(transaction, commitContext);
  }

  public void removeHandler(Handler handler)
  {
    getDelegate().removeHandler(handler);
  }

  public void removeListener(IListener listener)
  {
    getDelegate().removeListener(listener);
  }

  public void setName(String name)
  {
    getDelegate().setName(name);
  }

  public void setProperties(Map<String, String> properties)
  {
    getDelegate().setProperties(properties);
  }

  public void setQueryHandlerProvider(IQueryHandlerProvider queryHandlerProvider)
  {
    getDelegate().setQueryHandlerProvider(queryHandlerProvider);
  }

  public void setRevisionManager(InternalCDORevisionManager revisionManager)
  {
    getDelegate().setRevisionManager(revisionManager);
  }

  public void setSessionManager(InternalSessionManager sessionManager)
  {
    getDelegate().setSessionManager(sessionManager);
  }

  public void setStore(InternalStore store)
  {
    getDelegate().setStore(store);
  }

  public long getTimeStamp()
  {
    return getDelegate().getTimeStamp();
  }

  public void validateTimeStamp(long timeStamp) throws IllegalArgumentException
  {
    getDelegate().validateTimeStamp(timeStamp);
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    getDelegate().loadCommitInfos(branch, startTime, endTime, handler);
  }

  public CDOCommitData loadCommitData(long timeStamp)
  {
    return getDelegate().loadCommitData(timeStamp);
  }

  public Type getType()
  {
    return getDelegate().getType();
  }

  public State getState()
  {
    return getDelegate().getState();
  }

  public String getStoreType()
  {
    return getDelegate().getStoreType();
  }

  public Set<ObjectType> getObjectIDTypes()
  {
    return getDelegate().getObjectIDTypes();
  }

  public IDGenerationLocation getIDGenerationLocation()
  {
    return getDelegate().getIDGenerationLocation();
  }

  public CDOID getRootResourceID()
  {
    return getDelegate().getRootResourceID();
  }

  public Object processPackage(Object value)
  {
    return getDelegate().processPackage(value);
  }

  public boolean isSupportingEcore()
  {
    return getDelegate().isSupportingEcore();
  }

  public boolean isEnsuringReferentialIntegrity()
  {
    return getDelegate().isEnsuringReferentialIntegrity();
  }

  public void setType(Type type)
  {
    getDelegate().setType(type);
  }

  public long waitForCommit(long timeout)
  {
    return getDelegate().waitForCommit(timeout);
  }

  public void setState(State state)
  {
    getDelegate().setState(state);
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    return getDelegate().loadBranches(startID, endID, branchHandler);
  }

  public Semaphore getPackageRegistryCommitLock()
  {
    return getDelegate().getPackageRegistryCommitLock();
  }

  public CDOCommitInfoHandler[] getCommitInfoHandlers()
  {
    return getDelegate().getCommitInfoHandlers();
  }

  public void addCommitInfoHandler(CDOCommitInfoHandler handler)
  {
    getDelegate().addCommitInfoHandler(handler);
  }

  public void removeCommitInfoHandler(CDOCommitInfoHandler handler)
  {
    getDelegate().removeCommitInfoHandler(handler);
  }

  public InternalCDOCommitInfoManager getCommitInfoManager()
  {
    return getDelegate().getCommitInfoManager();
  }

  public Set<Handler> getHandlers()
  {
    return getDelegate().getHandlers();
  }

  public void setInitialPackages(EPackage... initialPackages)
  {
    getDelegate().setInitialPackages(initialPackages);
  }

  public InternalLockManager getLockingManager()
  {
    return getDelegate().getLockingManager();
  }

  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    return getDelegate().createCommitContext(transaction);
  }

  public long[] forceCommitTimeStamp(long timestamp, OMMonitor monitor)
  {
    return getDelegate().forceCommitTimeStamp(timestamp, monitor);
  }

  public void endCommit(long timeStamp)
  {
    getDelegate().endCommit(timeStamp);
  }

  public void failCommit(long timeStamp)
  {
    getDelegate().failCommit(timeStamp);
  }

  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo)
  {
    getDelegate().sendCommitNotification(sender, commitInfo);
  }

  public void setRootResourceID(CDOID rootResourceID)
  {
    getDelegate().setRootResourceID(rootResourceID);
  }

  public void setLastCommitTimeStamp(long commitTimeStamp)
  {
    getDelegate().setLastCommitTimeStamp(commitTimeStamp);
  }

  public void ensureChunks(InternalCDORevision revision)
  {
    getDelegate().ensureChunks(revision);
  }

  public void replicate(CDOReplicationContext context)
  {
    getDelegate().replicate(context);
  }

  public CDOReplicationInfo replicateRaw(CDODataOutput out, int lastReplicatedBranchID, long lastReplicatedCommitTime)
      throws IOException
  {
    return getDelegate().replicateRaw(out, lastReplicatedBranchID, lastReplicatedCommitTime);
  }

  public CDOChangeSetData getChangeSet(CDOBranchPoint startPoint, CDOBranchPoint endPoint)
  {
    return getDelegate().getChangeSet(startPoint, endPoint);
  }

  public Set<CDOID> getMergeData(CDORevisionAvailabilityInfo targetInfo, CDORevisionAvailabilityInfo sourceInfo,
      CDORevisionAvailabilityInfo targetBaseInfo, CDORevisionAvailabilityInfo sourceBaseInfo, String[] nsURIs,
      OMMonitor monitor)
  {
    return getDelegate().getMergeData(targetInfo, sourceInfo, targetBaseInfo, sourceBaseInfo, nsURIs, monitor);
  }

  public void queryLobs(List<byte[]> ids)
  {
    getDelegate().queryLobs(ids);
  }

  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    getDelegate().handleLobs(fromTime, toTime, handler);
  }

  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    getDelegate().loadLob(id, out);
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    getDelegate().handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, handler);
  }

  public boolean isSkipInitialization()
  {
    return getDelegate().isSkipInitialization();
  }

  public void setSkipInitialization(boolean skipInitialization)
  {
    getDelegate().setSkipInitialization(skipInitialization);
  }

  public void initSystemPackages()
  {
    getDelegate().initSystemPackages();
  }

  public void initMainBranch(InternalCDOBranchManager branchManager, long timeStamp)
  {
    getDelegate().initMainBranch(branchManager, timeStamp);
  }

  public LockObjectsResult lock(InternalView view, LockType type, List<CDORevisionKey> keys, boolean recursive,
      long timeout)
  {
    return getDelegate().lock(view, type, keys, recursive, timeout);
  }

  public UnlockObjectsResult unlock(InternalView view, LockType type, List<CDOID> ids, boolean recursive)
  {
    return getDelegate().unlock(view, type, ids, recursive);
  }
}
