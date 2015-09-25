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
package org.eclipse.emf.cdo.common;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.options.IOptions;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.options.IOptionsEvent;

/**
 * Abstracts the information about CDO views that is common to both client and server side.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link CDOCommonView.Options}
 */
public interface CDOCommonView extends CDOLockOwner, CDOBranchPoint, CDORevisionProvider, IOptionsContainer, Closeable
{
  public int getViewID();

  /**
   * @since 3.0
   */
  public boolean isReadOnly();

  public CDOCommonSession getSession();

  /**
   * @since 4.0
   */
  public String getDurableLockingID();

  /**
   * Returns the {@link Options options} of this view.
   * 
   * @since 4.1
   */
  public Options options();

  /**
   * Encapsulates the configuration options of CDO views that are common to both client and server side.
   * 
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @since 4.1
   * @apiviz.uses {@link CDOCommonView.Options.LockNotificationEvent} - - fires
   */
  public interface Options extends IOptions
  {
    /**
     * Returns <code>true</code> if this view will notify its {@link IListener listeners} about changes to the
     * {@link CDOLockState lock states} of the objects in this view (due to lock operations in <i>other</i> views),
     * <code>false</code> otherwise.
     * 
     * @see CDOLockState
     */
    public boolean isLockNotificationEnabled();

    /**
     * Specifies whether this view will notify its {@link IListener listeners} about changes to the {@link CDOLockState
     * lock states} of the objects in this view (due to lock operations in <i>other</i> views), or not.
     * 
     * @see CDOLockState
     */
    public void setLockNotificationEnabled(boolean enabled);

    /**
     * An {@link IOptionsEvent options event} fired from common view {@link CDOCommonView#options() options} when the
     * {@link Options#setLockNotificationEnabled(boolean) lock notification enabled} option has changed.
     * 
     * @author Caspar De Groot
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     * @since 4.1
     */
    public interface LockNotificationEvent extends IOptionsEvent
    {
      boolean getEnabled();
    }
  }
}
