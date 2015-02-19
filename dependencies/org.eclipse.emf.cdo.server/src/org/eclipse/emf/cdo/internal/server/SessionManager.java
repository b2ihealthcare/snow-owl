/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 202725
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.NotAuthenticatedException;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.IRandomizer;
import org.eclipse.net4j.util.security.IUserManager;
import org.eclipse.net4j.util.security.NegotiationException;
import org.eclipse.net4j.util.security.Randomizer;
import org.eclipse.net4j.util.security.SecurityUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class SessionManager extends Container<ISession> implements InternalSessionManager
{
  public static final int DEFAULT_TOKEN_LENGTH = 1024;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, SessionManager.class);

  private InternalRepository repository;

  @ExcludeFromDump
  private String encryptionAlgorithmName = SecurityUtil.PBE_WITH_MD5_AND_DES;

  @ExcludeFromDump
  private byte[] encryptionSaltBytes = SecurityUtil.DEFAULT_SALT;

  @ExcludeFromDump
  private int encryptionIterationCount = SecurityUtil.DEFAULT_ITERATION_COUNT;

  private int tokenLength = DEFAULT_TOKEN_LENGTH;

  private IRandomizer randomizer;

  private IUserManager userManager;

  private IPermissionManager permissionManager;

  private final Map<Integer, InternalSession> sessions = new HashMap<Integer, InternalSession>();

  private final AtomicInteger lastSessionID = new AtomicInteger();

  /**
   * @since 2.0
   */
  public SessionManager()
  {
  }

  /**
   * @since 2.0
   */
  public InternalRepository getRepository()
  {
    return repository;
  }

  /**
   * @since 2.0
   */
  public void setRepository(InternalRepository repository)
  {
    checkInactive();
    this.repository = repository;
  }

  public String getEncryptionAlgorithmName()
  {
    return encryptionAlgorithmName;
  }

  public void setEncryptionAlgorithmName(String encryptionAlgorithmName)
  {
    checkInactive();
    this.encryptionAlgorithmName = encryptionAlgorithmName;
  }

  public byte[] getEncryptionSaltBytes()
  {
    return encryptionSaltBytes;
  }

  public void setEncryptionSaltBytes(byte[] encryptionSaltBytes)
  {
    checkInactive();
    this.encryptionSaltBytes = encryptionSaltBytes;
  }

  public int getEncryptionIterationCount()
  {
    return encryptionIterationCount;
  }

  public void setEncryptionIterationCount(int encryptionIterationCount)
  {
    checkInactive();
    this.encryptionIterationCount = encryptionIterationCount;
  }

  public int getTokenLength()
  {
    return tokenLength;
  }

  public void setTokenLength(int tokenLength)
  {
    checkInactive();
    this.tokenLength = tokenLength;
  }

  public IRandomizer getRandomizer()
  {
    return randomizer;
  }

  public void setRandomizer(IRandomizer randomizer)
  {
    checkInactive();
    this.randomizer = randomizer;
  }

  public IUserManager getUserManager()
  {
    return userManager;
  }

  public void setUserManager(IUserManager userManager)
  {
    this.userManager = userManager;
  }

  public IPermissionManager getPermissionManager()
  {
    return permissionManager;
  }

  public void setPermissionManager(IPermissionManager permissionManager)
  {
    this.permissionManager = permissionManager;
  }

  public InternalSession[] getSessions()
  {
    synchronized (sessions)
    {
      return sessions.values().toArray(new InternalSession[sessions.size()]);
    }
  }

  /**
   * @since 2.0
   */
  public InternalSession getSession(int sessionID)
  {
    checkActive();
    synchronized (sessions)
    {
      return sessions.get(sessionID);
    }
  }

  public InternalSession[] getElements()
  {
    return getSessions();
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (sessions)
    {
      return sessions.isEmpty();
    }
  }

  /**
   * @since 2.0
   */
  public InternalSession openSession(ISessionProtocol sessionProtocol)
  {
    if (sessionProtocol != null)
    {
      ensureRootResourceInitialized();
    }

    int id = lastSessionID.incrementAndGet();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Opening session " + id); //$NON-NLS-1$
    }

    String userID = authenticateUser(sessionProtocol);
    InternalSession session = createSession(id, userID, sessionProtocol);
    LifecycleUtil.activate(session);

    synchronized (sessions)
    {
      sessions.put(id, session);
    }

    fireElementAddedEvent(session);
    sendRemoteSessionNotification(session, CDOProtocolConstants.REMOTE_SESSION_OPENED);
    return session;
  }

  protected void ensureRootResourceInitialized()
  {
    for (int i = 0; i < 20; i++)
    {
      CDOID rootResourceID = repository.getRootResourceID();
      if (!CDOIDUtil.isNull(rootResourceID))
      {
        return;
      }

      try
      {
        Thread.sleep(100);
      }
      catch (InterruptedException ex)
      {
        break;
      }
    }

    throw new IllegalStateException("Root resource has not been initialized in " + repository);
  }

  protected InternalSession createSession(int id, String userID, ISessionProtocol protocol)
  {
    return new Session(this, protocol, id, userID);
  }

  public void sessionClosed(InternalSession session)
  {
    int sessionID = session.getSessionID();
    InternalSession removeSession = null;
    synchronized (sessions)
    {
      removeSession = sessions.remove(sessionID);
    }

    if (removeSession != null)
    {
      fireElementRemovedEvent(session);
      sendRemoteSessionNotification(session, CDOProtocolConstants.REMOTE_SESSION_CLOSED);
    }
  }

  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType)
  {
    for (InternalSession session : getSessions())
    {
      try
      {
        session.sendRepositoryTypeNotification(oldType, newType);
      }
      catch (Exception ex)
      {
        handleNotificationProblem(session, ex);
      }
    }
  }

  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState)
  {
    sendRepositoryStateNotification(oldState, newState, null);
  }

  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState,
      CDOID rootResourceID)
  {
    for (InternalSession session : getSessions())
    {
      try
      {
        session.sendRepositoryStateNotification(oldState, newState, rootResourceID);
      }
      catch (Exception ex)
      {
        handleNotificationProblem(session, ex);
      }
    }
  }

  public void sendBranchNotification(InternalSession sender, InternalCDOBranch branch)
  {
    for (InternalSession session : getSessions())
    {
      if (session != sender)
      {
        try
        {
          session.sendBranchNotification(branch);
        }
        catch (Exception ex)
        {
          handleNotificationProblem(session, ex);
        }
      }
    }
  }

  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo)
  {
    for (InternalSession session : getSessions())
    {
      if (session != sender)
      {
        try
        {
          session.sendCommitNotification(commitInfo);
        }
        catch (Exception ex)
        {
          handleNotificationProblem(session, ex);
        }
      }
    }
  }

  public void sendLockNotification(InternalSession sender, CDOLockChangeInfo lockChangeInfo)
  {
    for (InternalSession session : getSessions())
    {
      if (session == sender || session.options().getLockNotificationMode() == LockNotificationMode.OFF)
      {
        continue;
      }

      try
      {
        session.sendLockNotification(lockChangeInfo);
      }
      catch (Exception ex)
      {
        handleNotificationProblem(session, ex);
      }
    }
  }

  /**
   * @since 2.0
   */
  public void sendRemoteSessionNotification(InternalSession sender, byte opcode)
  {
    try
    {
      for (InternalSession session : getSessions())
      {
        if (session != sender && session.isSubscribed())
        {
          try
          {
            session.sendRemoteSessionNotification(sender, opcode);
          }
          catch (Exception ex)
          {
            handleNotificationProblem(session, ex);
          }
        }
      }
    }
    catch (Exception ex)
    {
      OM.LOG.warn("A problem occured while notifying other sessions", ex);
    }
  }

  public List<Integer> sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message,
      int[] recipients)
  {
    List<Integer> result = new ArrayList<Integer>();
    for (int i = 0; i < recipients.length; i++)
    {
      InternalSession recipient = getSession(recipients[i]);

      try
      {
        if (recipient != null && recipient.isSubscribed())
        {
          recipient.sendRemoteMessageNotification(sender, message);
          result.add(recipient.getSessionID());
        }
      }
      catch (Exception ex)
      {
        handleNotificationProblem(recipient, ex);
      }
    }

    return result;
  }

  protected void handleNotificationProblem(InternalSession session, Throwable t)
  {
    OM.LOG.warn("A problem occured while notifying session " + session, t);
  }

  protected String authenticateUser(ISessionProtocol protocol) throws SecurityException
  {
    if (protocol == null)
    {
      return null;
    }

    if (userManager == null)
    {
      return null;
    }

    try
    {
      byte[] randomToken = createRandomToken();
      CDOAuthenticationResult result = protocol.sendAuthenticationChallenge(randomToken);
      if (result == null)
      {
        throw new NotAuthenticatedException();
      }

      String userID = result.getUserID();

      byte[] cryptedToken = encryptToken(userID, randomToken);
      boolean success = Arrays.equals(result.getCryptedToken(), cryptedToken);
      if (success)
      {
        return userID;
      }

      throw new SecurityException("Access denied"); //$NON-NLS-1$
    }
    catch (SecurityException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      Throwable cause = ex.getCause();
      if (cause instanceof SecurityException)
      {
        throw (SecurityException)cause;
      }

      throw new SecurityException(ex);
    }
  }

  protected byte[] createRandomToken()
  {
    byte[] token = new byte[tokenLength];
    randomizer.nextBytes(token);
    return token;
  }

  protected byte[] encryptToken(String userID, byte[] token) throws NegotiationException
  {
    try
    {
      return userManager.encrypt(userID, token, getEncryptionAlgorithmName(), getEncryptionSaltBytes(),
          getEncryptionIterationCount());
    }
    catch (Exception ex)
    {
      OM.LOG.error("Token encryption failed", ex); //$NON-NLS-1$
      return null;
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    if (randomizer == null)
    {
      randomizer = new Randomizer();
    }

    LifecycleUtil.activate(randomizer);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    InternalSession[] activeSessions = getSessions();
    for (int i = 0; i < activeSessions.length; i++)
    {
      LifecycleUtil.deactivate(activeSessions[i]);
    }

    super.doDeactivate();
  }
}
