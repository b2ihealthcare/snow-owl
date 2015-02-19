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

import org.eclipse.net4j.util.event.IEvent;

/**
 * A generic {@link IEvent event} fired from a {@link CDORemoteSessionManager remote session manager} to indicate
 * {@link CDORemoteSession remote session} activities.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORemoteSessionEvent extends IEvent
{
  /**
   * @since 3.0
   */
  public CDORemoteSessionManager getSource();

  public CDORemoteSession getRemoteSession();

  /**
   * A {@link CDORemoteSessionEvent remote session event} fired from a {@link CDORemoteSessionManager remote session
   * manager} when the {@link CDORemoteSession#isSubscribed() subscription state} of a {@link CDORemoteSession remote
   * session} has changed.
   * 
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface SubscriptionChanged extends CDORemoteSessionEvent
  {
    public boolean isSubscribed();
  }

  /**
   * A {@link CDORemoteSessionEvent remote session event} fired from a {@link CDORemoteSessionManager remote session
   * manager} when a {@link MessageReceived#getMessage() message} from a {@link CDORemoteSession remote session} has
   * been received.
   * 
   * @author Eike Stepper
   * @since 3.0
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface MessageReceived extends CDORemoteSessionEvent
  {
    public CDORemoteSessionMessage getMessage();
  }
}
