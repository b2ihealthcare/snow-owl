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
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * A {@link CDOSessionEvent session event} fired when lock-change notifications are received from a remote repository.
 * For this event to be fired,
 * {@link CDOSession.Options#setLockNotificationMode(org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode)
 * the lock notification mode} must either be set to ALWAYS, or it must be set to IF_REQUIRED_BY_VIEWS <i>and</i> at
 * least one of this sessions' views must have
 * {@link org.eclipse.emf.cdo.common.CDOCommonView.Options#setLockNotificationEnabled(boolean) its lock-notification
 * enablement} set to <code>true</code>.
 * 
 * @author Caspar De Groot
 * @since 4.1
 */
public interface CDOSessionLocksChangedEvent extends CDOSessionEvent, CDOLockChangeInfo
{
  /**
   * Returns the view that caused the lock changes if this view is local, or <code>null</code> if the view was remote.
   */
  public CDOView getSender();
}
