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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationInfo;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader2;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager.CommitInfoLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageProcessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager.RevisionLoader;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
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
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalRepository extends IRepository, PackageProcessor, PackageLoader, BranchLoader2, RevisionLoader,
    CommitInfoLoader
{
  public void setName(String name);

  public void setType(Type type);

  public void setState(State state);

  public InternalStore getStore();

  public void setStore(InternalStore store);

  public void setProperties(Map<String, String> properties);

  public InternalCDOBranchManager getBranchManager();

  public void setBranchManager(InternalCDOBranchManager branchManager);

  /**
   * @since 4.1
   */
  public Semaphore getPackageRegistryCommitLock();

  /**
   * Same as calling {@link #getPackageRegistry(boolean) getPackageRegistry(true)}.
   */
  public InternalCDOPackageRegistry getPackageRegistry();

  public InternalCDOPackageRegistry getPackageRegistry(boolean considerCommitContext);

  public InternalCDORevisionManager getRevisionManager();

  public void setRevisionManager(InternalCDORevisionManager revisionManager);

  public InternalCDOCommitInfoManager getCommitInfoManager();

  public InternalSessionManager getSessionManager();

  public void setSessionManager(InternalSessionManager sessionManager);

  /**
   * @deprecated As of 4.1 use {@link #getLockingManager()}.
   */
  @Deprecated
  public InternalLockManager getLockManager();

  /**
   * @since 4.1
   */
  public InternalLockManager getLockingManager();

  public InternalQueryManager getQueryManager();

  public void setQueryHandlerProvider(IQueryHandlerProvider queryHandlerProvider);

  public InternalCommitManager getCommitManager();

  public InternalCommitContext createCommitContext(InternalTransaction transaction);

  /**
   * Returns a commit time stamp that is guaranteed to be unique in this repository. At index 1 of the returned
   * <code>long</code> array is the previous commit time.
   *
   * @since 4.0
   */
  public long[] createCommitTimeStamp(OMMonitor monitor);

  /**
   * Like {@link #createCommitTimeStamp(OMMonitor)}, but forces the repository to use the timestamp value passed in as
   * the argument. This should be called only to force the timestamp of the first commit of a new repository to be equal
   * to its creation time.
   *
   * @since 4.0
   */
  public long[] forceCommitTimeStamp(long timestamp, OMMonitor monitor);

  /**
   * Notifies the repository of the completion of a commit. The value passed in must be a value obtained earlier through
   * {@link #createCommitTimeStamp(OMMonitor)}
   *
   * @since 4.0
   */
  public void endCommit(long timeStamp);

  /**
   * Notifies the repository of the failure of a commit. The value passed in must be a value obtained earlier through
   * {@link #createCommitTimeStamp(OMMonitor)}
   *
   * @since 4.0
   */
  public void failCommit(long timeStamp);

  /**
   * @since 4.0
   */
  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo);

  public void setRootResourceID(CDOID rootResourceID);

  /**
   * @since 4.0
   */
  public void setLastCommitTimeStamp(long commitTimeStamp);

  /**
   * @since 4.1
   */
  public void ensureChunks(InternalCDORevision revision);

  public IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature, int chunkStart,
      int chunkEnd);

  public void notifyReadAccessHandlers(InternalSession session, CDORevision[] revisions,
      List<CDORevision> additionalRevisions);

  public void notifyWriteAccessHandlers(ITransaction transaction, IStoreAccessor.CommitContext commitContext,
      boolean beforeCommit, OMMonitor monitor);

  public void rollbackWriteAccessHandlers(ITransaction transaction, IStoreAccessor.CommitContext commitContext);

  public void replicate(CDOReplicationContext context);

  public CDOReplicationInfo replicateRaw(CDODataOutput out, int lastReplicatedBranchID, long lastReplicatedCommitTime)
      throws IOException;

  public CDOChangeSetData getChangeSet(CDOBranchPoint startPoint, CDOBranchPoint endPoint);

  /**
   * @since 4.0
   * @sine Snow Owl 2.6
   * @param nsURIs - namespace URI restriction
   */
  public Set<CDOID> getMergeData(CDORevisionAvailabilityInfo targetInfo, CDORevisionAvailabilityInfo sourceInfo,
      CDORevisionAvailabilityInfo targetBaseInfo, CDORevisionAvailabilityInfo sourceBaseInfo, String[] nsURIs,
      OMMonitor monitor);

  /**
   * @since 4.0
   */
  public void queryLobs(List<byte[]> ids);

  /**
   * @since 4.0
   */
  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException;

  /**
   * @since 4.0
   */
  public void loadLob(byte[] id, OutputStream out) throws IOException;

  /**
   * @since 4.0
   */
  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler);

  /**
   * @since 4.0
   */
  public boolean isSkipInitialization();

  /**
   * @since 4.0
   */
  public void setSkipInitialization(boolean skipInitialization);

  /**
   * @since 4.0
   */
  public void initSystemPackages();

  /**
   * @since 4.0
   */
  public void initMainBranch(InternalCDOBranchManager branchManager, long timeStamp);

  /**
   * @since 4.1
   */
  public LockObjectsResult lock(InternalView view, LockType type, List<CDORevisionKey> keys, boolean recursive,
      long timeout);

  /**
   * @since 4.1
   */
  public UnlockObjectsResult unlock(InternalView view, LockType type, List<CDOID> ids, boolean recursive);
}
