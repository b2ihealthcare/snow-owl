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
package org.eclipse.emf.cdo.session.remote;

import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionEvent.MessageReceived;

/**
 * Represents a remote session that is connected to the same repository as the
 * {@link CDORemoteSessionManager#getLocalSession() local session} that the {@link #getManager() remote session manager}
 * points to.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORemoteSession extends Comparable<CDORemoteSession>
{
  /**
   * Returns the remote session manager that manages this remote session.
   */
  public CDORemoteSessionManager getManager();

  /**
   * Returns the session ID of this remote session.
   */
  public int getSessionID();

  /**
   * Returns the user ID of this remote session.
   */
  public String getUserID();

  /**
   * Returns <code>true</code> if this remote session is subscribed to changes in the set of remote sessions and
   * delivers {@link MessageReceived custom data events}, <code>false</code> otherwise.
   */
  public boolean isSubscribed();

  /**
   * Sends a unicast message to this remote session if it is subscribed.
   * 
   * @return <code>true</code> if the server received the custom data message, <code>false</code> otherwise.
   *         <b>Note:</b> No assumption must be made on whether the recipient session received the message and was able
   *         to handle it adequately!
   * @throws CDOException
   *           if this remote session is not subscribed.
   * @see #isSubscribed()
   * @since 3.0
   */
  public boolean sendMessage(CDORemoteSessionMessage message);
}
