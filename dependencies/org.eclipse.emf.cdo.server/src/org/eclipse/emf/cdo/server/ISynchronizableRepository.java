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
package org.eclipse.emf.cdo.server;

/**
 * A repository with the ability to {@link IRepositorySynchronizer synchronize} its content with another repository.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link IRepositorySynchronizer}
 * @apiviz.has {@link ISession} oneway - - replicatorSession
 */
public interface ISynchronizableRepository extends IRepository
{
  public IRepositorySynchronizer getSynchronizer();

  public ISession getReplicatorSession();

  public int getLastReplicatedBranchID();

  public long getLastReplicatedCommitTime();

  /**
   * @since 4.1
   */
  public void goOnline();

  /**
   * @since 4.1
   */
  public void goOffline();
}
