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

import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.options.IOptions;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.options.IOptionsEvent;
import org.eclipse.net4j.util.security.IUserAware;

/**
 * Abstracts the information about CDO sessions that is common to both client and server side.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.composedOf {@link CDOCommonView} - - views
 * @apiviz.has {@link CDOCommonSession.Options}
 * @apiviz.uses {@link CDOCommonRepository} - - connectsTo
 */
public interface CDOCommonSession extends IUserAware, IOptionsContainer, Closeable
{
  public int getSessionID();

  public CDOCommonView[] getViews();

  public CDOCommonView getView(int viewID);

  /**
   * Returns the {@link Options options} of this session.
   */
  public Options options();

  /**
   * Encapsulates the configuration options of CDO sessions that are common to both client and server side.
   * 
   * @author Simon McDuff
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @apiviz.has CDOCommonSession.Options.PassiveUpdateMode
   * @apiviz.has CDOCommonSession.Options.LockNotificationMode
   */
  public interface Options extends IOptions
  {
    /**
     * Returns the {@link CDOCommonSession session} of this options object.
     * 
     * @since 4.0
     */
    public CDOCommonSession getContainer();

    public boolean isPassiveUpdateEnabled();

    /**
     * Specifies whether objects will be invalidated due by other users changes.
     * <p>
     * Example:
     * <p>
     * <code>session.setPassiveUpdateEnabled(false);</code>
     * <p>
     * By default this property is enabled. If this property is disabled the latest versions of objects can still be
     * obtained by calling refresh().
     * <p>
     * Passive update can be disabled in cases where more performance is needed and/or more control over when objects
     * will be refreshed.
     * <p>
     * When enabled again, a refresh will be automatically performed to be in sync with the server.
     * 
     * @since 3.0
     */
    public void setPassiveUpdateEnabled(boolean enabled);

    /**
     * @since 3.0
     */
    public PassiveUpdateMode getPassiveUpdateMode();

    /**
     * @since 3.0
     */
    public void setPassiveUpdateMode(PassiveUpdateMode mode);

    /**
     * @since 4.1
     */
    public LockNotificationMode getLockNotificationMode();

    /**
     * @since 4.1
     */
    public void setLockNotificationMode(LockNotificationMode mode);

    /**
     * Enumerates the possible {@link CDOCommonSession.Options#getPassiveUpdateMode() passive update modes} of a CDO
     * session.
     * 
     * @author Eike Stepper
     * @since 3.0
     */
    public enum PassiveUpdateMode
    {
      /**
       * This mode delivers change deltas only for change subscriptions, invalidation information for all other objects.
       */
      INVALIDATIONS,

      /**
       * This mode delivers change deltas for all changed objects, whether they have change subscriptions or not.
       * Revisions for new objects are not delivered.
       */
      CHANGES,

      /**
       * This mode delivers change deltas for all changed objects, whether they have change subscriptions or not. In
       * addition full revisions for new objects are delivered.
       */
      ADDITIONS
    }

    /**
     * Enumerates the possible {@link CDOCommonSession.Options#getLockNotificationMode() lock notification modes} of a
     * CDO session.
     * 
     * @since 4.1
     */
    public enum LockNotificationMode
    {
      /**
       * This mode delivers no lock notifications
       */
      OFF,

      /**
       * This mode delivers lock notifications if one or more views have enabled them.
       */
      IF_REQUIRED_BY_VIEWS,

      /**
       * This mode always delivers lock notifications, even if no views have them enabled, and even if no views are
       * open.
       */
      ALWAYS
    }

    /**
     * An {@link IOptionsEvent options event} fired when the {@link PassiveUpdateMode passive update mode} of a CDO
     * session has changed.
     * 
     * @author Eike Stepper
     * @since 3.0
     */
    public interface PassiveUpdateEvent extends IOptionsEvent
    {
      public boolean getOldEnabled();

      public boolean getNewEnabled();

      public PassiveUpdateMode getOldMode();

      public PassiveUpdateMode getNewMode();
    }

    /**
     * An {@link IOptionsEvent options event} fired when the {@link LockNotificationMode lock notification mode} of a
     * CDO session has changed.
     * 
     * @author Caspar De Groot
     * @since 4.1
     */
    public interface LockNotificationModeEvent extends IOptionsEvent
    {
      public LockNotificationMode getOldMode();

      public LockNotificationMode getNewMode();
    }
  }
}
