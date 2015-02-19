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

import org.eclipse.net4j.util.event.INotifier;

/**
 * Manages a tree of {@link CDOBranch branches} and notifies about changes in this branch tree.
 * <p>
 * The branch tree is represented by a {@link #getMainBranch() main} branch, which, like all
 * {@link CDOBranch#getBranches() sub} branches, offers the major part of the branching functionality. A branch manager
 * provides additional methods to find branches by their unique integer ID or by their fully qualified path name, as
 * well as asynchronous bulk queries.
 * <p>
 * A branch manager can fire the following events:
 * <ul>
 * <li> {@link CDOBranchCreatedEvent} after a new branch has been created.
 * </ul>
 * <p>
 * Branch managers are usually associated with the following entities:
 * <ul>
 * <li> <code>org.eclipse.emf.cdo.session.CDOSession</code>
 * <li> <code>org.eclipse.emf.cdo.server.IRepository</code>
 * </ul>
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link CDOBranch} oneway - - mainBranch
 * @apiviz.uses {@link CDOBranchCreatedEvent} - - fires
 */
public interface CDOBranchManager extends INotifier
{
  /**
   * Returns the main branch of the branch tree managed by this branch manager.
   * <p>
   * The main branch has the fixed {@link CDOBranch#MAIN_BRANCH_NAME name} "MAIN" and the fixed
   * {@link CDOBranch#MAIN_BRANCH_ID ID} 0 (zero).
   */
  public CDOBranch getMainBranch();

  /**
   * Returns the branch with the given unique integer ID.
   * <p>
   * Note that this method never returns <code>null</code>. Due to the lazy loading nature of branch managers this
   * method returns a transparent <i>branch proxy</i> if the branch is not already loaded in the internal <i>branch
   * cache</i>. This can result in unchecked exceptions being thrown from calls to arbitrary branch methods if the ID
   * that the proxy was created with does not exist in the branch tree.
   */
  public CDOBranch getBranch(int branchID);

  /**
   * Returns the branch with the given absolute path.
   * 
   * @param path
   *          A concatenation of the names of all branches from the {@link #getMainBranch() main branch} to the
   *          requested branch, separated by {@link CDOBranch#PATH_SEPARATOR slashes} ("/" characters). Example:
   *          "MAIN/team1/smith".
   */
  public CDOBranch getBranch(String path);

  /**
   * Passes all branches with IDs in the given range to the given {@link CDOBranchHandler#handleBranch(CDOBranch) branch
   * handler} and returns the number of handler invocations.
   * <p>
   * This is a blocking call.
   */
  public int getBranches(int startID, int endID, CDOBranchHandler handler);
}
