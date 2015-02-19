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
package org.eclipse.net4j.util.om.monitor;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface OMMonitor extends OMMonitorProgress
{
  /**
   * @since 2.0
   */
  public static final int THOUSAND = 1000;

  /**
   * @since 2.0
   */
  public static final double DEFAULT_TIME_FACTOR = THOUSAND;

  /**
   * @since 2.0
   */
  public boolean isCanceled();

  /**
   * @since 2.0
   */
  public void checkCanceled() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public boolean hasBegun() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public OMMonitor begin(double totalWork) throws MonitorCanceledException;

  /**
   * Same as calling <code>begin(ONE)</code>.
   * 
   * @since 2.0
   */
  public OMMonitor begin() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public void worked(double work) throws MonitorCanceledException;

  /**
   * Same as calling <code>worked(ONE)</code>.
   * 
   * @since 2.0
   */
  public void worked() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public OMMonitor fork(double work);

  /**
   * Same as calling <code>fork(ONE)</code>.
   * 
   * @since 2.0
   */
  public OMMonitor fork();

  /**
   * @since 2.0
   */
  public Async forkAsync(double work);

  /**
   * Same as calling <code>forkAsync(ONE)</code>.
   * 
   * @since 2.0
   */
  public Async forkAsync();

  /**
   * @since 2.0
   */
  public void done();

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public interface Async
  {
    public void stop();
  }
}
