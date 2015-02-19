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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

/**
 * A {@link #getTimeStamp() point in time} in a particular {@link #getBranch() branch}.
 * 
 * @see CDOCommitInfo
 * @see CDOBranchTag
 * @see CDOBranch#getBase()
 * @see CDOBranch#getHead()
 * @see CDOBranch#getPoint(long)
 * @see CDOBranchUtil#copyBranchPoint(CDOBranchPoint)
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.has {@link CDOBranch}
 */
public interface CDOBranchPoint
{
  /**
   * The fixed special time stamp <i>unspecified</i>.
   * 
   * @see CDOBranch#getHead()
   */
  public static final long UNSPECIFIED_DATE = 0;

  /**
   * The fixed special time stamp <i>invalid</i>.
   * 
   * @since 4.0
   */
  public static final long INVALID_DATE = -1;

  /**
   * Returns the branch of this branch point, or <code>null</code> if this branch point is the
   * {@link CDOBranch#getBase() base} of the {@link CDOBranchManager#getMainBranch() main branch}.
   */
  public CDOBranch getBranch();

  /**
   * Returns the time stamp of this branch point, or the fixed special time stamp <i>
   * {@link CDOBranchPoint#UNSPECIFIED_DATE unspecified}</i> if this branch point marks the {@link CDOBranch#getHead()
   * head} of a branch.
   */
  public long getTimeStamp();
}
