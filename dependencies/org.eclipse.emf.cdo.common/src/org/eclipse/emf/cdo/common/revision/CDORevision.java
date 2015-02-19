/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - delta support
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.security.CDOPermissionProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Encapsulates the immutable system information of a <b>single</b> CDO {@link EObject object} between two
 * {@link CDOCommitInfo commits} in a {@link CDOBranch branch} and provides access to its modeled
 * {@link CDORevisionData data}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link CDORevisionData}
 */
public interface CDORevision extends CDORevisionKey, CDORevisable
{
  /**
   * @since 3.0
   */
  public static final int MAIN_BRANCH_ID = CDOBranch.MAIN_BRANCH_ID;

  /**
   * @since 3.0
   */
  public static final int FIRST_VERSION = CDOBranchVersion.FIRST_VERSION;

  public static final int UNCHUNKED = -1;

  /**
   * @since 3.0
   */
  public static final int DEPTH_NONE = 0;

  /**
   * @since 3.0
   */
  public static final int DEPTH_INFINITE = -1;

  /**
   * @since 4.1
   */
  public static final CDOPermissionProvider PERMISSION_PROVIDER = new CDOPermissionProvider()
  {
    public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext)
    {
      return revision.getPermission();
    }
  };

  /**
   * @since 2.0
   */
  public EClass getEClass();

  /**
   * Returns <code>true</code> exactly if {@link #getTimeStamp()} does not return {@link #UNSPECIFIED_DATE},
   * <code>false</code> otherwise.
   *
   * @since 3.0
   */
  public boolean isHistorical();

  public boolean isValid(long timeStamp);

  /**
   * @since 4.0
   */
  public boolean isValid(CDOBranchPoint branchPoint);

  /**
   * @since 2.0
   */
  public boolean isResourceNode();

  /**
   * @since 2.0
   */
  public boolean isResourceFolder();

  public boolean isResource();

  /**
   * @since 2.0
   */
  public CDORevisionData data();

  public CDORevisionDelta compare(CDORevision origin);

  public void merge(CDORevisionDelta delta);

  /**
   * @since 2.0
   */
  public CDORevision copy();

  /**
   * @since 4.1
   */
  public CDOPermission getPermission();

  /**
   * @since 4.1
   */
  public boolean isReadable();

  /**
   * @since 4.1
   */
  public boolean isWritable();
}
