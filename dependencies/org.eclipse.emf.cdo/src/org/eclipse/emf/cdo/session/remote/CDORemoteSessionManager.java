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

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionEvent.MessageReceived;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.Collection;
import java.util.Set;

/**
 * Provides collaborative access to the {@link #getRemoteSessions() remote sessions} that are connected to the same
 * repository as the {@link #getLocalSession() local session}. A CDORemoteSessionManager can be subscribed or
 * unsubscribed to changes in the set of remote sessions. It is subscribed if at least one is true:
 * <ol>
 * <li>At least one {@link IListener listener} is registered with this remote session manager.
 * <li>{@link #isForceSubscription() Force subscription} is <code>true</code>.
 * </ol>
 * If this remote session manager is subscribed it eventually fires the following {@link IEvent events} to
 * {@link #addListener(IListener) registered} listeners:
 * <ul>
 * <li> {@link IContainerEvent} with {@link CDORemoteSession} as generic type argument to reflect opened or closed remote
 * sessions.
 * <li> {@link CDORemoteSessionEvent.SubscriptionChanged} to reflect the ability of the remote session to receive and
 * possibly handle remote messages from other sessions.
 * <li> {@link CDORemoteSessionEvent.MessageReceived} to deliver custom data
 * {@link CDORemoteSession#sendMessage(CDORemoteSessionMessage) sent} from other sessions .
 * </ul>
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORemoteSessionManager extends IContainer<CDORemoteSession>
{
  /**
   * Returns the {@link CDOSession local session} this CDORemoteSessionManager belongs to.
   */
  public CDOSession getLocalSession();

  /**
   * Returns the set of {@link CDORemoteSession remote sessions} that are connected to the same repository as the
   * {@link #getLocalSession() local session}. If this CDORemoteSessionManager itself is {@link #isSubscribed()
   * subscribed} the result is returned from a local cache for remote sessions, otherwise it is requested from the
   * server each time this method is called.
   */
  public CDORemoteSession[] getRemoteSessions();

  /**
   * Returns <code>true</code> if this CDORemoteSessionManager is subscribed to changes in the set of remote sessions
   * and delivers {@link MessageReceived custom data events}, <code>false</code> otherwise. It is subscribed if at least
   * one is true:
   * <ol>
   * <li>At least one {@link IListener listener} is registered with this remote session manager.
   * <li>{@link #isForceSubscription() Force subscription} is <code>true</code>.
   * </ol>
   * 
   * @see #addListener(IListener)
   * @see #setForceSubscription(boolean)
   */
  public boolean isSubscribed();

  /**
   * Returns <code>true</code> if this CDORemoteSessionManager shall be subscribed to changes in the set of remote
   * sessions and delivers {@link MessageReceived custom data events} even if no {@link IListener listener} is
   * registered, <code>false</code> otherwise.
   * 
   * @see #addListener(IListener)
   * @see #setForceSubscription(boolean)
   */
  public boolean isForceSubscription();

  /**
   * Enables or disables subscription to changes in the set of remote sessions even if no {@link IListener listener} is
   * registered.
   * 
   * @see #addListener(IListener)
   * @see #setForceSubscription(boolean)
   */
  public void setForceSubscription(boolean forceSubscription);

  /**
   * Sends a multicast message to the subscribed recipients.
   * 
   * @return The set of {@link CDORemoteSession recipients} that the message has been forwarded to by the server.
   *         <b>Note:</b> No assumption must be made on whether a recipient session received the message and was able to
   *         handle it adequately!
   * @since 3.0
   */
  public Set<CDORemoteSession> sendMessage(CDORemoteSessionMessage message, CDORemoteSession... recipients);

  /**
   * Same as {@link #sendMessage(CDORemoteSessionMessage, CDORemoteSession...)} but with a recipients {@link Collection
   * collection}.
   * 
   * @since 3.0
   */
  public Set<CDORemoteSession> sendMessage(CDORemoteSessionMessage message, Collection<CDORemoteSession> recipients);

  /**
   * An {@link IEvent event} that is fired by a {@link #getSource() remote session manager} after the
   * {@link CDORemoteSessionManager#isSubscribed() subscription mode} of the
   * {@link CDORemoteSessionManager#getLocalSession() local session} changed.
   * 
   * @author Eike Stepper
   * @since 3.0
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface LocalSubscriptionChangedEvent extends IEvent
  {
    /**
     * Returns the remote session manager that fired this event.
     */
    public CDORemoteSessionManager getSource();

    /**
     * Returns <code>true</code> if the {@link #getSource() remote session manager} was subscribed, <code>false</code>
     * otherwise.
     */
    public boolean isSubscribed();
  }

  /**
   * A default adapter for all kinds of {@link CDORemoteSession remote session} related events.
   * 
   * @author Eike Stepper
   * @since 3.0
   */
  public static class EventAdapter extends ContainerEventAdapter<CDORemoteSession>
  {
    public EventAdapter()
    {
    }

    /**
     * Called if the {@link CDORemoteSessionManager#isSubscribed() subscription mode} of the local session changed.
     */
    protected void onLocalSubscriptionChanged(boolean subscribed)
    {
    }

    /**
     * Called if the local session is {@link CDORemoteSessionManager#isSubscribed() subscribed} and a new remote session
     * was opened to the same repository as the local session.
     */
    protected void onOpened(CDORemoteSession remoteSession)
    {
    }

    /**
     * Called if the local session is {@link CDORemoteSessionManager#isSubscribed() subscribed} and a remote session to
     * the same repository as the local session was closed.
     */
    protected void onClosed(CDORemoteSession remoteSession)
    {
    }

    /**
     * Called if the local session is {@link CDORemoteSessionManager#isSubscribed() subscribed} and a remote session
     * {@link CDORemoteSession#isSubscribed() subscribed} to the same repository as the local session.
     */
    protected void onSubscribed(CDORemoteSession remoteSession)
    {
    }

    /**
     * Called if the local session is {@link CDORemoteSessionManager#isSubscribed() subscribed} and a remote session
     * {@link CDORemoteSession#isSubscribed() unsubscribed} from the same repository as the local session.
     */
    protected void onUnsubscribed(CDORemoteSession remoteSession)
    {
    }

    /**
     * Called if the local session is {@link CDORemoteSessionManager#isSubscribed() subscribed} and a remote session has
     * {@link CDORemoteSession#sendMessage(CDORemoteSessionMessage) sent} custom data to the local session.
     */
    protected void onMessageReceived(CDORemoteSession remoteSession, CDORemoteSessionMessage message)
    {
    }

    @Override
    protected void notifyOtherEvent(IEvent event)
    {
      if (event instanceof LocalSubscriptionChangedEvent)
      {
        LocalSubscriptionChangedEvent e = (LocalSubscriptionChangedEvent)event;
        onLocalSubscriptionChanged(e.isSubscribed());
      }
      else if (event instanceof CDORemoteSessionEvent.SubscriptionChanged)
      {
        CDORemoteSessionEvent.SubscriptionChanged e = (CDORemoteSessionEvent.SubscriptionChanged)event;
        if (e.isSubscribed())
        {
          onSubscribed(e.getRemoteSession());
        }
        else
        {
          onUnsubscribed(e.getRemoteSession());
        }
      }
      else if (event instanceof CDORemoteSessionEvent.MessageReceived)
      {
        CDORemoteSessionEvent.MessageReceived e = (CDORemoteSessionEvent.MessageReceived)event;
        onMessageReceived(e.getRemoteSession(), e.getMessage());
      }
      else
      {
        super.notifyOtherEvent(event);
      }
    }

    @Override
    protected final void onAdded(IContainer<CDORemoteSession> container, CDORemoteSession element)
    {
      onOpened(element);
    }

    @Override
    protected final void onRemoved(IContainer<CDORemoteSession> container, CDORemoteSession element)
    {
      onClosed(element);
    }
  }
}
