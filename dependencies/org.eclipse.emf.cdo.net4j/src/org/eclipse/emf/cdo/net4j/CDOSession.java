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
package org.eclipse.emf.cdo.net4j;

import org.eclipse.net4j.signal.ISignalProtocol;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

/**
 * Deprecated, use {@link org.eclipse.emf.cdo.net4j.CDONet4jSession CDONet4jSession}.
 * 
 * @since 2.0
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @deprecated Use {@link org.eclipse.emf.cdo.net4j.CDONet4jSession CDONet4jSession}.
 */
@Deprecated
public interface CDOSession extends CDONet4jSession
{
  /**
   * Returns the {@link Options options} of this session.
   * 
   * @deprecated Use {@link org.eclipse.emf.cdo.net4j.CDONet4jSession#options() CDONet4jSession.options()}.
   */
  @Deprecated
  public Options options();

  /**
   * Deprecated, use {@link org.eclipse.emf.cdo.net4j.CDONet4jSession.Options CDONet4jSession.Options}.
   * 
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @deprecated Use {@link org.eclipse.emf.cdo.net4j.CDONet4jSession.Options CDONet4jSession.Options}.
   */
  @Deprecated
  public interface Options extends CDONet4jSession.Options
  {
    /**
     * Returns the Net4j {@link CDOSessionProtocol protocol} instance that represents the underlying
     * <em>signalling connection</em> to the repository of this session.
     * 
     * @see #getNet4jProtocol()
     * @deprecated Kept for 4.0 compatibility. Newer code should call {@link #getNet4jProtocol()}.
     */
    @Deprecated
    public ISignalProtocol<CDOSession> getProtocol();
  }
}
