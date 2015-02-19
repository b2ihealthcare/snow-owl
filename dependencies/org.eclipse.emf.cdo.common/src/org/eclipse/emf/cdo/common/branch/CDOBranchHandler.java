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

/**
 * A call-back interface that indicates the ability to <i>handle</i> branches that are passed from other entities.
 * 
 * @see CDOBranchManager#getBranches(int, int, CDOBranchHandler)
 * @author Eike Stepper
 * @since 3.0
 * @apiviz.uses {@link CDOBranch} - - handles
 */
public interface CDOBranchHandler
{
  /**
   * A call-back method that other entities can pass branches to.
   */
  public void handleBranch(CDOBranch branch);
}
