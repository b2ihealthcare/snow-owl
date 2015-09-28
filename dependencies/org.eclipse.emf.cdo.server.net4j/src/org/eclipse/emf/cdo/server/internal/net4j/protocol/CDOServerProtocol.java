/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233273
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.io.StringCompressor;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocol extends SignalProtocol<InternalSession> implements ISessionProtocol
{
  public static final long DEFAULT_NEGOTIATION_TIMEOUT = 15 * 1000;

  private long negotiationTimeout = DEFAULT_NEGOTIATION_TIMEOUT;

  private IRepositoryProvider repositoryProvider;

  private StringIO packageURICompressor = StringCompressor.BYPASS ? StringIO.DIRECT : new StringCompressor(false);

  public CDOServerProtocol(IRepositoryProvider repositoryProvider)
  {
    super(CDOProtocolConstants.PROTOCOL_NAME);
    this.repositoryProvider = repositoryProvider;
  }

  public InternalSession getSession()
  {
    return getInfraStructure();
  }

  public IRepositoryProvider getRepositoryProvider()
  {
    return repositoryProvider;
  }

  public StringIO getPackageURICompressor()
  {
    return packageURICompressor;
  }

  public long getNegotiationTimeout()
  {
    return negotiationTimeout;
  }

  public void setNegotiationTimeout(long negotiationTimeout)
  {
    this.negotiationTimeout = negotiationTimeout;
  }

  public CDOAuthenticationResult sendAuthenticationChallenge(byte[] randomToken) throws Exception
  {
    return new AuthenticationRequest(this, randomToken).send(negotiationTimeout);
  }

  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType)
      throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new RepositoryTypeNotificationRequest(this, oldType, newType).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState)
      throws Exception
  {
    sendRepositoryStateNotification(oldState, newState, null);
  }

  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState,
      CDOID rootResourceID) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new RepositoryStateNotificationRequest(this, oldState, newState, rootResourceID).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  public void sendBranchNotification(InternalCDOBranch branch) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new BranchNotificationRequest(this, branch).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  public void sendCommitNotification(CDOCommitInfo commitInfo) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new CommitNotificationRequest(this, commitInfo).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  public void sendRemoteSessionNotification(InternalSession sender, byte opcode) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new RemoteSessionNotificationRequest(this, sender, opcode).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  public void sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new RemoteMessageNotificationRequest(this, sender, message).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  public void sendLockNotification(CDOLockChangeInfo lockChangeInfo) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new LockNotificationRequest(this, lockChangeInfo).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  protected void handleInactiveSession()
  {
    OM.LOG.warn("Session channel is inactive: " + this); //$NON-NLS-1$
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case CDOProtocolConstants.SIGNAL_OPEN_SESSION:
      return new OpenSessionIndication(this);

    case CDOProtocolConstants.SIGNAL_OPEN_VIEW:
      return new OpenViewIndication(this);

    case CDOProtocolConstants.SIGNAL_SWITCH_TARGET:
      return new SwitchTargetIndication(this);

    case CDOProtocolConstants.SIGNAL_CLOSE_VIEW:
      return new CloseViewIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_PACKAGES:
      return new LoadPackagesIndication(this);

    case CDOProtocolConstants.SIGNAL_CREATE_BRANCH:
      return new CreateBranchIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_BRANCH:
      return new LoadBranchIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_SUB_BRANCHES:
      return new LoadSubBranchesIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_BRANCHES:
      return new LoadBranchesIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_REVISIONS:
      return new LoadRevisionsIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION:
      return new LoadRevisionByVersionIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_CHUNK:
      return new LoadChunkIndication(this);

    case CDOProtocolConstants.SIGNAL_QUERY_LOBS:
      return new QueryLobsIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_LOB:
      return new LoadLobIndication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION:
      return new CommitTransactionIndication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_DELEGATION:
      return new CommitDelegationIndication(this);

    case CDOProtocolConstants.SIGNAL_XA_COMMIT_TRANSACTION_PHASE1:
      return new CommitXATransactionPhase1Indication(this);

    case CDOProtocolConstants.SIGNAL_XA_COMMIT_TRANSACTION_PHASE2:
      return new CommitXATransactionPhase2Indication(this);

    case CDOProtocolConstants.SIGNAL_XA_COMMIT_TRANSACTION_PHASE3:
      return new CommitXATransactionPhase3Indication(this);

    case CDOProtocolConstants.SIGNAL_XA_COMMIT_TRANSACTION_CANCEL:
      return new CommitXATransactionCancelIndication(this);

    case CDOProtocolConstants.SIGNAL_QUERY:
      return new QueryIndication(this);

    case CDOProtocolConstants.SIGNAL_QUERY_CANCEL:
      return new QueryCancelIndication(this);

    case CDOProtocolConstants.SIGNAL_REFRESH_SESSION:
      return new RefreshSessionIndication(this);

    case CDOProtocolConstants.SIGNAL_DISABLE_PASSIVE_UPDATE:
      return new DisablePassiveUpdateIndication(this);

    case CDOProtocolConstants.SIGNAL_SET_PASSIVE_UPDATE_MODE:
      return new SetPassiveUpdateModeIndication(this);

    case CDOProtocolConstants.SIGNAL_CHANGE_SUBSCRIPTION:
      return new ChangeSubscriptionIndication(this);

    case CDOProtocolConstants.SIGNAL_REPOSITORY_TIME:
      return new RepositoryTimeIndication(this);

    case CDOProtocolConstants.SIGNAL_LOCK_OBJECTS:
      return new LockObjectsIndication(this);

    case CDOProtocolConstants.SIGNAL_UNLOCK_OBJECTS:
      return new UnlockObjectsIndication(this);

    case CDOProtocolConstants.SIGNAL_LOCK_DELEGATION:
      return new LockDelegationIndication(this);

    case CDOProtocolConstants.SIGNAL_UNLOCK_DELEGATION:
      return new UnlockDelegationIndication(this);

    case CDOProtocolConstants.SIGNAL_OBJECT_LOCKED:
      return new ObjectLockedIndication(this);

    case CDOProtocolConstants.SIGNAL_LOCK_AREA:
      return new LockAreaIndication(this);

    case CDOProtocolConstants.SIGNAL_GET_REMOTE_SESSIONS:
      return new GetRemoteSessionsIndication(this);

    case CDOProtocolConstants.SIGNAL_UNSUBSCRIBE_REMOTE_SESSIONS:
      return new UnsubscribeRemoteSessionsIndication(this);

    case CDOProtocolConstants.SIGNAL_REMOTE_MESSAGE:
      return new RemoteMessageIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_COMMIT_INFOS:
      return new LoadCommitInfosIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_COMMIT_DATA:
      return new LoadCommitDataIndication(this);

    case CDOProtocolConstants.SIGNAL_REPLICATE_REPOSITORY:
      return new ReplicateRepositoryIndication(this);

    case CDOProtocolConstants.SIGNAL_REPLICATE_REPOSITORY_RAW:
      return new ReplicateRepositoryRawIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_CHANGE_SETS:
      return new LoadChangeSetsIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_MERGE_DATA:
      return new LoadMergeDataIndication(this);

    case CDOProtocolConstants.SIGNAL_HANDLE_REVISIONS:
      return new HandleRevisionsIndication(this);

    case CDOProtocolConstants.SIGNAL_LOCK_STATE:
      return new LockStateIndication(this);

    case CDOProtocolConstants.SIGNAL_ENABLE_LOCK_NOTIFICATION:
      return new EnableLockNotificationIndication(this);

    case CDOProtocolConstants.SIGNAL_SET_LOCK_NOTIFICATION_MODE:
      return new SetLockNotificationModeIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }
}
