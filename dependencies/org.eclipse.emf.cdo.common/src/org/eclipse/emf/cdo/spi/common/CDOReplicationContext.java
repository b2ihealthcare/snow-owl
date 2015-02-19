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
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOReplicationContext extends CDOReplicationInfo, CDOBranchHandler, CDOCommitInfoHandler,
    IDurableLockingManager.LockArea.Handler
{
}
