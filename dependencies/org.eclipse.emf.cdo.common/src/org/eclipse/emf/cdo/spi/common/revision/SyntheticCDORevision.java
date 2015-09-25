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
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.ecore.EClass;

/**
 * A synthetic revision that represents the initial period of an object in a {@link CDOBranch branch} when the object is
 * still associated with a revision from one of the baseline branches. It always has {@link #getVersion() version}
 * {@link CDOBranchVersion#UNSPECIFIED_VERSION zero} and can only appear in branches below the
 * {@link CDOBranch#isMainBranch() main} branch.
 * <p>
 * Synthetic revisions are used for two slightly different purposes:
 * <ol>
 * <li>For {@link CDORevisionCache cache} optimization.
 * <li>As a persistent "detach marker" indicating that the first modification of an object in a branch is its deletion.
 * </ol>
 * <p>
 * Instances of this marker revision are not supposed to be exposed outside of a revision {@link CDORevisionManager
 * manager}. They are mainly used in the communication between a revision manager and its associated revision
 * {@link InternalCDORevisionManager.RevisionLoader loader}.
 * 
 * @author Eike Stepper
 * @since 3.0
 */
public abstract class SyntheticCDORevision extends StubCDORevision
{
  private CDOID id;

  private CDOBranch branch;

  public SyntheticCDORevision(EClass eClass, CDOID id, CDOBranch branch)
  {
    super(eClass);
    this.id = id;
    this.branch = branch;
  }

  @Override
  public CDOID getID()
  {
    return id;
  }

  @Override
  public CDOBranch getBranch()
  {
    return branch;
  }

  @Override
  public void freeze()
  {
    // Do nothing
  }
}
