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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;

/**
 * @author Eike Stepper
 */
public class DBRevisionHandler implements CDORevisionHandler
{
  private CDORevisionHandler delegate;

  public DBRevisionHandler(CDORevisionHandler delegate)
  {
    this.delegate = delegate;
  }

  public boolean handleRevision(CDORevision revision)
  {
    if (revision.getVersion() < CDOBranchVersion.FIRST_VERSION - 1)
    {
      revision = new DetachedCDORevision(revision.getEClass(), revision.getID(), revision.getBranch(),
          -revision.getVersion(), revision.getTimeStamp(), revision.getRevised());
    }

    return delegate.handleRevision(revision);
  }
}
