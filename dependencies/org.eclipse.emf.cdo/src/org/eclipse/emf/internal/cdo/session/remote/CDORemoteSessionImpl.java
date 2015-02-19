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
package org.eclipse.emf.internal.cdo.session.remote;

import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSession;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDORemoteSessionImpl implements InternalCDORemoteSession
{
  private InternalCDORemoteSessionManager manager;

  private int sessionID;

  private String userID;

  private boolean subscribed;

  public CDORemoteSessionImpl(InternalCDORemoteSessionManager manager, int sessionID, String userID)
  {
    this.manager = manager;
    this.sessionID = sessionID;
    this.userID = userID;
  }

  public InternalCDORemoteSessionManager getManager()
  {
    return manager;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public String getUserID()
  {
    return userID;
  }

  public boolean isSubscribed()
  {
    return subscribed;
  }

  public void setSubscribed(boolean subscribed)
  {
    this.subscribed = subscribed;
  }

  public boolean sendMessage(CDORemoteSessionMessage message)
  {
    if (!isSubscribed())
    {
      return false;
    }

    return manager.sendMessage(message, this).size() == 1;
  }

  public int compareTo(CDORemoteSession obj)
  {
    int result = userID.compareTo(obj.getUserID());
    if (result == 0)
    {
      result = Integer.valueOf(sessionID).compareTo(obj.getSessionID());
    }

    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDORemoteSession)
    {
      CDORemoteSession that = (CDORemoteSession)obj;
      return manager == that.getManager() && sessionID == that.getSessionID();
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return manager.hashCode() ^ sessionID;
  }

  @Override
  public String toString()
  {
    String repo = manager.getLocalSession().getRepositoryInfo().getName();
    String user = userID == null ? repo : userID + "@" + repo;
    return MessageFormat.format("{0} ({1})", user, sessionID); //$NON-NLS-1$
  }
}
