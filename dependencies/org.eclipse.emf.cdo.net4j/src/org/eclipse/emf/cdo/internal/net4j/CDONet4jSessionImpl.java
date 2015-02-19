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
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.NotAuthenticatedException;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionConfigurationImpl.RepositoryInfo;
import org.eclipse.emf.cdo.internal.net4j.protocol.CDOClientProtocol;
import org.eclipse.emf.cdo.internal.net4j.protocol.CommitTransactionRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.session.DelegatingSessionProtocol;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.IStreamWrapper;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.OpenSessionResult;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("deprecation")
public class CDONet4jSessionImpl extends CDOSessionImpl implements org.eclipse.emf.cdo.net4j.CDOSession
{
  private IStreamWrapper streamWrapper;

  private IConnector connector;

  private String repositoryName;

  private long signalTimeout = SignalProtocol.DEFAULT_TIMEOUT;

  public CDONet4jSessionImpl()
  {
  }

  public IStreamWrapper getStreamWrapper()
  {
    return streamWrapper;
  }

  public void setStreamWrapper(IStreamWrapper streamWrapper)
  {
    this.streamWrapper = streamWrapper;
  }

  public IConnector getConnector()
  {
    return connector;
  }

  public void setConnector(IConnector connector)
  {
    this.connector = connector;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  public long getSignalTimeout()
  {
    return signalTimeout;
  }

  public void setSignalTimeout(long signalTimeout)
  {
    this.signalTimeout = signalTimeout;

    // Deal with the possibility that the sessionProtocol has already been created.
    CDOClientProtocol clientProtocol = getClientProtocol();
    if (clientProtocol != null)
    {
      clientProtocol.setTimeout(this.signalTimeout);
    }
  }

  @Override
  public OptionsImpl options()
  {
    return (OptionsImpl)super.options();
  }

  @Override
  protected OptionsImpl createOptions()
  {
    return new OptionsImpl();
  }

  @Override
  protected void doActivate() throws Exception
  {
    OpenSessionResult result = openSession();
    if (result == null)
    {
      throw new NotAuthenticatedException();
    }

    super.doActivate();

    InternalCDOPackageRegistry packageRegistry = getPackageRegistry();
    if (packageRegistry == null)
    {
      packageRegistry = new CDOPackageRegistryImpl();
      setPackageRegistry(packageRegistry);
    }

    packageRegistry.setPackageProcessor(this);
    packageRegistry.setPackageLoader(this);
    packageRegistry.activate();

    InternalCDORevisionManager revisionManager = getRevisionManager();
    if (revisionManager == null)
    {
      revisionManager = (InternalCDORevisionManager)CDORevisionUtil.createRevisionManager();
      setRevisionManager(revisionManager);
    }

    revisionManager.setSupportingAudits(getRepositoryInfo().isSupportingAudits());
    revisionManager.setSupportingBranches(getRepositoryInfo().isSupportingBranches());
    revisionManager.setRevisionLoader(getSessionProtocol());
    revisionManager.setRevisionLocker(this);
    revisionManager.activate();

    InternalCDOBranchManager branchManager = getBranchManager();
    if (branchManager == null)
    {
      branchManager = CDOBranchUtil.createBranchManager();
      setBranchManager(branchManager);
    }

    branchManager.setBranchLoader(getSessionProtocol());
    branchManager.setTimeProvider(getRepositoryInfo());
    branchManager.initMainBranch(isMainBranchLocal(), getRepositoryInfo().getCreationTime());
    branchManager.activate();

    InternalCDOCommitInfoManager commitInfoManager = getCommitInfoManager();
    if (commitInfoManager == null)
    {
      commitInfoManager = CDOCommitInfoUtil.createCommitInfoManager();
      setCommitInfoManager(commitInfoManager);
    }

    commitInfoManager.setCommitInfoLoader(getSessionProtocol());
    commitInfoManager.activate();

    for (InternalCDOPackageUnit packageUnit : result.getPackageUnits())
    {
      getPackageRegistry().putPackageUnit(packageUnit);
    }

    getRepositoryInfo().getTimeStamp(true);
  }

  private CDOClientProtocol createProtocol()
  {
    CDOClientProtocol protocol = new CDOClientProtocol();
    protocol.setTimeout(signalTimeout);
    protocol.setInfraStructure(this);
    if (streamWrapper != null)
    {
      protocol.setStreamWrapper(streamWrapper);
    }

    protocol.open(connector);
    return protocol;
  }

  /**
   * Gets the CDOClientProtocol instance, which may be wrapped inside a DelegatingSessionProtocol
   */
  private CDOClientProtocol getClientProtocol()
  {
    CDOSessionProtocol protocol = getSessionProtocol();
    CDOClientProtocol clientProtocol;
    if (protocol instanceof DelegatingSessionProtocol)
    {
      clientProtocol = (CDOClientProtocol)((DelegatingSessionProtocol)protocol).getDelegate();
    }
    else
    {
      clientProtocol = (CDOClientProtocol)protocol;
    }

    return clientProtocol;
  }

  protected OpenSessionResult openSession()
  {
    CDOClientProtocol protocol = createProtocol();
    setSessionProtocol(protocol);
    hookSessionProtocol();

    // TODO (CD) The next call is on the CDOClientProtocol; shouldn't it be on the DelegatingSessionProtocol instead?
    OpenSessionResult result = protocol.openSession(repositoryName, options().isPassiveUpdateEnabled(), options()
        .getPassiveUpdateMode(), options().getLockNotificationMode());

    if (result == null)
    {
      // Skip to response because the user has canceled the authentication
      return null;
    }

    setSessionID(result.getSessionID());
    setUserID(result.getUserID());
    setLastUpdateTime(result.getLastUpdateTime());
    setRepositoryInfo(new RepositoryInfo(repositoryName, result, this));
    return result;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();

    getCommitInfoManager().deactivate();
    getRevisionManager().deactivate();
    getBranchManager().deactivate();
    getPackageRegistry().deactivate();
  }

  /**
   * @author Eike Stepper
   */
  protected class OptionsImpl extends org.eclipse.emf.internal.cdo.session.CDOSessionImpl.OptionsImpl implements
      org.eclipse.emf.cdo.net4j.CDOSession.Options
  {
    private int commitTimeout = CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS;

    private int progressInterval = CommitTransactionRequest.DEFAULT_MONITOR_PROGRESS_SECONDS;

    public OptionsImpl()
    {
    }

    @Override
    public CDONet4jSession getContainer()
    {
      return (CDONet4jSession)super.getContainer();
    }

    public ISignalProtocol<org.eclipse.emf.cdo.net4j.CDONet4jSession> getNet4jProtocol()
    {
      CDOSessionProtocol protocol = getSessionProtocol();
      if (protocol instanceof DelegatingSessionProtocol)
      {
        protocol = ((DelegatingSessionProtocol)protocol).getDelegate();
      }

      @SuppressWarnings("unchecked")
      ISignalProtocol<CDONet4jSession> signalProtocol = (ISignalProtocol<CDONet4jSession>)protocol;
      return signalProtocol;
    }

    public ISignalProtocol<CDOSession> getProtocol()
    {
      @SuppressWarnings("unchecked")
      ISignalProtocol<CDOSession> net4jProtocol = (ISignalProtocol<CDOSession>)(ISignalProtocol<?>)getNet4jProtocol();
      return net4jProtocol;
    }

    public int getCommitTimeout()
    {
      return commitTimeout;
    }

    public synchronized void setCommitTimeout(int commitTimeout)
    {
      this.commitTimeout = commitTimeout;
    }

    public int getProgressInterval()
    {
      return progressInterval;
    }

    public synchronized void setProgressInterval(int progressInterval)
    {
      this.progressInterval = progressInterval;
    }
  }
}
