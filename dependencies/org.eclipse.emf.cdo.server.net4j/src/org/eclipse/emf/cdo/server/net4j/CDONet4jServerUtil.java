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
package org.eclipse.emf.cdo.server.net4j;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.internal.net4j.protocol.CDOServerProtocolFactory;
import org.eclipse.emf.cdo.spi.server.ContainerRepositoryProvider;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * Various static methods that may help with the server-side setup to support Net4j-specific CDO {@link CDOCommonSession sessions}.
 *
 * @author Eike Stepper
 */
public final class CDONet4jServerUtil
{
  private CDONet4jServerUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container, IRepositoryProvider repositoryProvider)
  {
    container.registerFactory(new CDOServerProtocolFactory(repositoryProvider));
    container.registerFactory(new FailoverMonitor.Factory());
    container.registerFactory(new FailoverMonitor.AgentProtocol.Factory(container));
    container.registerFactory(new FailoverMonitor.ClientProtocol.Factory(container));
  }

  public static void prepareContainer(IManagedContainer container)
  {
    prepareContainer(container, new ContainerRepositoryProvider(container));
  }
}
