/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.session.CDOSession.Options;

/**
 * A {@link CDOViewEvent view event} fired when {@link CDOLockChangeInfo lock changes} are being received from a remote
 * repository.
 * {@link Options#setLockNotificationMode(org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode) Lock
 * notifications} must be enabled for this event to be fired.
 * 
 * @author Caspar De Groot
 * @since 4.1
 */
public interface CDOViewLocksChangedEvent extends CDOViewEvent, CDOLockChangeInfo
{
  /**
   * Returns the view that caused the lock changes if this view is local, or <code>null</code> if the view was remote.
   */
  public CDOView getSender();
}
