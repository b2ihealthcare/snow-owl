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
package org.eclipse.emf.cdo.common.branch;

import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

/**
 * A range between the {@link CDOBranch#getBase() base} of the {@link CDOBranchManager#getMainBranch() main branch} and
 * the {@link CDOBranch#getHead() head} of any branch that is demarkated by a {@link #getStartPoint() start point} and
 * an {@link #getEndPoint() end point}.
 * <p>
 * The start point and the end point of a branch point range may have different branches or not.
 * <p>
 * Branch point ranges are usually created with {@link CDOBranchUtil#createRange(CDOBranchPoint, CDOBranchPoint)
 * CDOBranchUtil.createRange()} and often used in the context of {@link CDOChangeSet change sets}.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.has {@link CDOBranchPoint} oneway - - start
 * @apiviz.has {@link CDOBranchPoint} oneway - - end
 */
public interface CDOBranchPointRange
{
  /**
   * Returns the start point of this branch point range, never <code>null</code>.
   */
  public CDOBranchPoint getStartPoint();

  /**
   * Returns the end point of this branch point range, never <code>null</code>.
   */
  public CDOBranchPoint getEndPoint();
}
