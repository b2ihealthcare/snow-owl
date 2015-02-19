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
package org.eclipse.emf.cdo.spi.common.branch;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

/**
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOBranch extends CDOBranch
{
  public boolean isProxy();

  public InternalCDOBranchManager getBranchManager();

  public InternalCDOBranch[] getBranches();

  /**
   * @since 4.0
   */
  public InternalCDOBranch[] getBranches(boolean loadOnDemand);

  public InternalCDOBranch getBranch(String path);

  public InternalCDOBranch createBranch(String name, long timeStamp);

  public InternalCDOBranch createBranch(String name);

  public BranchInfo getBranchInfo();

  public void setBranchInfo(String name, InternalCDOBranch baseBranch, long baseTimeStamp);

  public void addChild(InternalCDOBranch branch);
}
