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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;

/**
 * A pair of a {@link #getBranch() branch} and an integer {@link #getVersion() version} number.
 * <p>
 * It is often used in the context of
 * {@link CDORevisionManager#getRevisionByVersion(CDOID, CDOBranchVersion, int, boolean)
 * CDORevisionManager.getRevisionByVersion()}.
 * 
 * @see CDOBranch#getVersion(int)
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.has {@link CDOBranch}
 */
public interface CDOBranchVersion
{
  /**
   * The fixed special version number <i>unspecified</i>.
   */
  public static final int UNSPECIFIED_VERSION = 0;

  /**
   * The fixed version number that is assigned to the first {@link CDORevision revision} of an {@link CDOID object} that
   * is committed to a particular branch .
   */
  public static final int FIRST_VERSION = 1;

  /**
   * Returns the branch of this branch version.
   */
  public CDOBranch getBranch();

  /**
   * Returns the version number of this branch version.
   */
  public int getVersion();
}
