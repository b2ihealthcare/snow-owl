/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.net4j;

import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.signal.ISignalProtocol;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

/**
 * A Net4j-specific CDO {@link CDOSession session}.
 * 
 * @since 4.1
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link CDONet4jSession.Options}
 */
public interface CDONet4jSession extends org.eclipse.emf.cdo.session.CDOSession
{
  /**
   * Returns the {@link Options options} of this session.
   */
  public Options options();

  /**
   * Encapsulates a set of notifying {@link CDONet4jSession session} configuration options.
   * 
   * @since 4.1
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Options extends org.eclipse.emf.cdo.session.CDOSession.Options
  {
    /**
     * Returns the Net4j {@link CDOSessionProtocol protocol} instance that represents the underlying
     * <em>signalling connection</em> to the repository of this session.
     */
    public ISignalProtocol<CDONet4jSession> getNet4jProtocol();

    /**
     * Returns the timeout for commit operations in <b>seconds</b>.
     */
    public int getCommitTimeout();

    /**
     * Sets the timeout for commit operations in <b>seconds</b>.
     */
    public void setCommitTimeout(int commitTimeout);

    /**
     * Returns the interval for progress reports of commit operations in <b>seconds</b>.
     */
    public int getProgressInterval();

    /**
     * Sets the interval for progress reports of commit operations in <b>seconds</b>.
     */
    public void setProgressInterval(int progressInterval);
  }
}
