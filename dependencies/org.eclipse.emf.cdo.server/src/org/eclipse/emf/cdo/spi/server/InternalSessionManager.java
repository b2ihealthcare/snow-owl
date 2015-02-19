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

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;

import org.eclipse.net4j.util.security.IUserManager;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalSessionManager extends ISessionManager
{
  public InternalRepository getRepository();

  public void setRepository(InternalRepository repository);

  /**
   * @since 4.1
   */
  public IUserManager getUserManager();

  public void setUserManager(IUserManager userManager);

  /**
   * @since 4.1
   */
  public IPermissionManager getPermissionManager();

  /**
   * @since 4.1
   */
  public void setPermissionManager(IPermissionManager permissionManager);

  public InternalSession[] getSessions();

  public InternalSession getSession(int sessionID);

  /**
   * @return Never <code>null</code>
   */
  public InternalSession openSession(ISessionProtocol sessionProtocol);

  public void sessionClosed(InternalSession session);

  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType);

  /**
   * @deprecated use
   *             {@link #sendRepositoryStateNotification(org.eclipse.emf.cdo.common.CDOCommonRepository.State, org.eclipse.emf.cdo.common.CDOCommonRepository.State, CDOID)}
   *             instead
   */
  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState);

  /**
   * @since 4.1
   */
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState,
      CDOID rootResourceID);

  public void sendBranchNotification(InternalSession sender, InternalCDOBranch branch);

  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo);

  /**
   * @since 4.1
   */
  public void sendLockNotification(InternalSession sender, CDOLockChangeInfo lockChangeInfo);

  public void sendRemoteSessionNotification(InternalSession sender, byte opcode);

  public List<Integer> sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message,
      int[] recipients);
}
