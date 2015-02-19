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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalRepositorySynchronizer extends IRepositorySynchronizer, ILifecycle
{
  public InternalSynchronizableRepository getLocalRepository();

  public void setLocalRepository(InternalSynchronizableRepository localRepository);

  public void setRemoteSessionConfigurationFactory(CDOSessionConfigurationFactory remoteSessionConfigurationFactory);

  public InternalCDOSession getRemoteSession();
}
