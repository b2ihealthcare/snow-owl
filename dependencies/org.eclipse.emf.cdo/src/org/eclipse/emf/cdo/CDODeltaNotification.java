/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import java.util.Collection;

/**
 * This class behaves like the usual EMF Notification except for the following:
 * <ul>
 * <li>It doesn't provide the old value, only the new index or new value.
 * <li>{@link Notification#REMOVE_MANY REMOVE_MANY} indicates that {@link Collection#clear() clear()} was called.
 * <li>{@link Notification#ADD_MANY Add_MANY} is not used.
 * </ul>
 * 
 * @since 2.0
 * @author Simon McDuff
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDODeltaNotification extends CDONotification
{
  /**
   * Informs the adapter if another notification is going to be sent (notifications often have a list of notifications,
   * see {@link NotificationChain}).
   */
  public boolean hasNext();

  /**
   * Returns the {@link CDORevisionDelta} associated with this notification.
   */
  public CDORevisionDelta getRevisionDelta();
}
