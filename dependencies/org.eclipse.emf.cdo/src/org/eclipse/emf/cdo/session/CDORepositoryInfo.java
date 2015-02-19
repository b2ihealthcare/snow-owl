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
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.CDOCommonRepository;

/**
 * Describes a model repository a {@link CDOSession session} is connected to.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORepositoryInfo extends CDOCommonRepository
{
  /**
   * Returns the approximate current time of this repository by optionally refreshing the approximation from the server.
   * 
   * @see CDOCommonRepository#getTimeStamp()
   */
  public long getTimeStamp(boolean forceRefresh);
}
