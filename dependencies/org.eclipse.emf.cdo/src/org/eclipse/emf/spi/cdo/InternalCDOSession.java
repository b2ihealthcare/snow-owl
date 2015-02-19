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
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageProcessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager.RevisionLocker;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOSession extends CDOSession, PackageProcessor, PackageLoader, RevisionLocker, ILifecycle
{
  public CDOSessionProtocol getSessionProtocol();

  /**
   * @since 3.0
   */
  public void setSessionProtocol(CDOSessionProtocol sessionProtocol);

  /**
   * @since 4.0
   */
  public CDOAuthenticator getAuthenticator();

  /**
   * @since 4.0
   */
  public void setAuthenticator(CDOAuthenticator authenticator);

  public InternalCDOPackageRegistry getPackageRegistry();

  /**
   * @since 4.0
   */
  public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry);

  /**
   * @since 3.0
   */
  public InternalCDOBranchManager getBranchManager();

  /**
   * @since 4.0
   */
  public void setBranchManager(InternalCDOBranchManager branchManager);

  /**
   * @since 3.0
   */
  public InternalCDORevisionManager getRevisionManager();

  /**
   * @since 4.0
   */
  public void setRevisionManager(InternalCDORevisionManager revisionManager);

  /**
   * @since 3.0
   */
  public InternalCDOCommitInfoManager getCommitInfoManager();

  /**
   * @since 4.0
   */
  public void setCommitInfoManager(InternalCDOCommitInfoManager commitInfoManager);

  /**
   * @since 3.0
   */
  public InternalCDORemoteSessionManager getRemoteSessionManager();

  /**
   * @since 3.0
   */
  public void setRemoteSessionManager(InternalCDORemoteSessionManager remoteSessionManager);

  /**
   * @since 4.0
   */
  public CDOLobStore getLobStore();

  public void setExceptionHandler(CDOSession.ExceptionHandler exceptionHandler);

  /**
   * @since 4.1
   */
  public void setIDGenerator(CDOIDGenerator idGenerator);

  /**
   * @since 3.0
   */
  public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager);

  /**
   * @since 3.0
   */
  public void setRepositoryInfo(CDORepositoryInfo repositoryInfo);

  /**
   * @since 3.0
   */
  public void setSessionID(int sessionID);

  public void setUserID(String userID);

  /**
   * @since 3.0
   */
  public void setLastUpdateTime(long lastUpdateTime);

  public void viewDetached(InternalCDOView view);

  /**
   * @since 3.0
   */
  public Object resolveElementProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex);

  /**
   * @since 4.0
   */
  public void resolveAllElementProxies(CDORevision revision);

  /**
   * @since 3.0
   */
  public void handleRepositoryTypeChanged(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType);

  /**
   * @since 3.0
   */
  public void handleRepositoryStateChanged(CDOCommonRepository.State oldState, CDOCommonRepository.State newState);

  /**
   * @since 3.0
   */
  public void handleBranchNotification(InternalCDOBranch branch);

  /**
   * @since 3.0
   */
  public void handleCommitNotification(CDOCommitInfo commitInfo);

  /**
   * @since 4.1
   */
  public void handleLockNotification(CDOLockChangeInfo lockChangeInfo, InternalCDOView sender);

  /**
   * @since 3.0
   */
  public void invalidate(CDOCommitInfo commitInfo, InternalCDOTransaction sender);

  /**
   * @since 3.0
   */
  public void processRefreshSessionResult(RefreshSessionResult result, CDOBranch branch,
      List<InternalCDOView> branchViews, Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions);

  /**
   * @since 4.0
   */
  public boolean isSticky();

  /**
   * @since 4.0
   */
  public CDOBranchPoint getCommittedSinceLastRefresh(CDOID id);

  /**
   * @since 4.0
   */
  public void setCommittedSinceLastRefresh(CDOID id, CDOBranchPoint branchPoint);

  /**
   * @since 4.0
   */
  public void clearCommittedSinceLastRefresh();

  /**
   * @since 4.0
   */
  public void setMainBranchLocal(boolean mainBranchLocal);

  /**
   * @since 4.0
   */
  public CDORevisionAvailabilityInfo createRevisionAvailabilityInfo(CDOBranchPoint branchPoint);

  /**
   * @since 4.0
   */
  public void cacheRevisions(CDORevisionAvailabilityInfo info);
}
