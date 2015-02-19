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

import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfoHandler;
import org.eclipse.emf.cdo.server.ISynchronizableRepository;
import org.eclipse.emf.cdo.spi.common.CDORawReplicationContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;

/**
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalSynchronizableRepository extends ISynchronizableRepository, InternalRepository,
    CDOReplicationContext, CDORawReplicationContext, CDOLockChangeInfoHandler
{
  public InternalRepositorySynchronizer getSynchronizer();

  public void setSynchronizer(InternalRepositorySynchronizer synchronizer);

  public InternalSession getReplicatorSession();

  public void setLastReplicatedBranchID(int lastReplicatedBranchID);

  public void setLastReplicatedCommitTime(long lastReplicatedCommitTime);
}
