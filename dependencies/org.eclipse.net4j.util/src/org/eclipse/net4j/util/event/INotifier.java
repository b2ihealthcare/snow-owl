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
package org.eclipse.net4j.util.event;

/**
 * An entity that a number of {@link IListener listeners} can be registered with and that can fire {@link IEvent events}
 * to these registered listeners.
 * <p>
 * Implementors are encouraged to document the event types that they are able to fire and that their listeners may want
 * to receive and handle.
 * <p>
 * Implementors may want to extend {@link Notifier} instead of implementing this interface directly.
 * 
 * @author Eike Stepper
 * @apiviz.landmark
 * @apiviz.owns {@link IListener} - - listeners
 * @apiviz.uses {@link IEvent} - - fires
 */
public interface INotifier
{
  /**
   * Adds a listener to this notifier.
   * <p>
   * Depending on the implementation duplicate listeners may lead to duplicate event delivery or not. Implementors are
   * encouraged to prevent events from being delivered more than once to the same listener,
   */
  public void addListener(IListener listener);

  /**
   * Removes a listener from this notifier.
   */
  public void removeListener(IListener listener);

  /**
   * Returns <code>true</code> if one or more listeners are registered with this notifier, <code>false</code> otherwise.
   * 
   * @since 3.0
   */
  public boolean hasListeners();

  /**
   * Returns the listeners that are registered with this notifier.
   * <p>
   * Depending on the implementation duplicate listeners may be contained in the returned array.
   * 
   * @since 3.0
   */
  public IListener[] getListeners();
}
