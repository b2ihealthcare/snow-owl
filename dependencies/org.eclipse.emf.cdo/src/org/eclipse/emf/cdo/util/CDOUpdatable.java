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
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.session.CDOSession.Options;

/**
 * Represents facilities that can receive
 * {@link org.eclipse.emf.cdo.session.CDOSession.Options#setPassiveUpdateEnabled(boolean) passive updates}.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOUpdatable
{
  public static final long NO_TIMEOUT = -1;

  /**
   * Returns the time stamp of the last commit operation. May not be accurate if
   * {@link Options#isPassiveUpdateEnabled() passive updates} are disabled.
   */
  public long getLastUpdateTime();

  /**
   * Blocks the calling thread until a commit operation with the given time stamp (or higher) has occured.
   */
  public void waitForUpdate(long updateTime);

  /**
   * Blocks the calling thread until a commit operation with the given time stamp (or higher) has occured or the given
   * timeout has expired.
   * 
   * @return <code>true</code> if the specified commit operation has occured within the given timeout period,
   *         <code>false</code> otherwise.
   */
  public boolean waitForUpdate(long updateTime, long timeoutMillis);
}
