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

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.net4j.util.container.IContainer;

/**
 * Represents a <i>stream of changes</i> that is isolated from other streams of changes.
 * <p>
 * A branch starts at a fixed {@link #getBase() base} point and ends at a floating {@link #getHead() head} point.
 * Between these two points there can be a number of other {@link CDOBranchPoint branch points}:
 * <ul>
 * <li> {@link CDOCommitInfo Commit infos} are points in a branch that represent commit operations.
 * <li> {@link CDOBranchTag Branch tags} are named points in a branch.
 * <li> {@link #getBase() Base points } of sub branches of a branch.
 * </ul>
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link CDOBranchPoint} oneway - - base
 * @apiviz.composedOf {@link CDOBranch} - - subBranches
 */
public interface CDOBranch extends IContainer<CDOBranch>, Comparable<CDOBranch>
{
  /**
   * The fixed ID of the {@link CDOBranchManager#getMainBranch() main branch}.
   */
  public static final int MAIN_BRANCH_ID = 0;

  /**
   * The fixed name of the {@link CDOBranchManager#getMainBranch() main branch}.
   */
  public static final String MAIN_BRANCH_NAME = "MAIN"; //$NON-NLS-1$

  /**
   * The string used to separate the segments of branch paths.
   * 
   * @see #getPathName()
   * @see #getBranch(String)
   * @see CDOBranchManager#getBranch(String)
   */
  public static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

  /**
   * Returns <code>true</code> if this branch is the {@link CDOBranchManager#getMainBranch() main branch},
   * <code>false</code> otherwise.
   */
  public boolean isMainBranch();

  /**
   * Returns <code>true</code> if this branch is a local branch, <code>false</code> otherwise.
   * <p>
   * Local branches are created on the fly when committing to a
   * {@link org.eclipse.emf.cdo.common.CDOCommonRepository.Type#CLONE clone} repository while it is in
   * {@link State#OFFLINE offline} state and they do not participate in repository replication. They can not be created
   * manually and they have negative {@link #getID() IDs}.
   */
  public boolean isLocal();

  /**
   * Returns the ID of this branch.
   * <p>
   * The {@link CDOBranchManager#getMainBranch() main branch} has the fixed ID 0 (zero), {@link #isLocal() Local
   * branches} have negative IDs and normal branches have positive IDs.
   */
  public int getID();

  /**
   * Returns the name of this branch as specified when it was created with {@link #createBranch(String, long)
   * createBranch()} or {@link #MAIN_BRANCH_NAME} if this branch is the {@link CDOBranchManager#getMainBranch() main
   * branch}.
   */
  public String getName();

  /**
   * Returns the fully qualified path name of this branch, a concatenation of the names of all branches from the
   * {@link CDOBranchManager#getMainBranch() main branch} to this branch, separated by {@link #PATH_SEPARATOR slashes}
   * ("/" characters). Example: "MAIN/team1/smith".
   */
  public String getPathName();

  /**
   * Returns an array of the {@link #getBase() base} branch points starting from the base of the
   * {@link CDOBranchManager#getMainBranch() main branch} down to and including the base of this branch.
   */
  public CDOBranchPoint[] getBasePath();

  /**
   * Returns the immutable base branch point of this branch, the point in the parent branch that marks the creation of
   * this branch.
   * <p>
   * The base of the {@link CDOBranchManager#getMainBranch() main branch} marks the creation of the
   * {@link CDOCommonRepository repository}.
   * 
   * @see CDOBranch#getHead()
   * @see #getPoint(long)
   */
  public CDOBranchPoint getBase();

  /**
   * Returns the floating <i>end point</i> of this branch, a pair of this branch and the fixed special time stamp <i>
   * {@link CDOBranchPoint#UNSPECIFIED_DATE unspecified}</i>.
   * 
   * @see CDOBranch#getBase()
   * @see #getPoint(long)
   */
  public CDOBranchPoint getHead();

  /**
   * Returns the branch point in this branch with the given time stamp.
   * <p>
   * This factory method never returns <code>null</code>.
   * 
   * @see CDOBranch#getBase()
   * @see CDOBranch#getHead()
   * @see #getVersion(int)
   */
  public CDOBranchPoint getPoint(long timeStamp);

  /**
   * Returns the branch version in this branch with the given version number.
   * <p>
   * This factory method never returns <code>null</code>.
   * 
   * @see #getPoint(long)
   */
  public CDOBranchVersion getVersion(int version);

  /**
   * Returns the branch manager that manages this branch, never <code>null</code>.
   */
  public CDOBranchManager getBranchManager();

  /**
   * Returns an array of the sub branches of this branch, never <code>null</code>.
   */
  public CDOBranch[] getBranches();

  /**
   * Returns the sub branch of this branch with the given relative path, or <code>null</code> if no sub branch with this
   * path exists in this branch.
   * <p>
   * The path name is the concatenation of the names of all branches from a direct sub branch of this branch, separated
   * by {@link #PATH_SEPARATOR slashes} ("/" characters). Example: "team1/smith".
   */
  public CDOBranch getBranch(String path);

  /**
   * Creates a sub branch of this branch with the given name, {@link #getBase() based} at the {@link CDOBranchPoint
   * branch point} in this branch with the given time stamp.
   * <p>
   * 
   * @param name
   *          The name of the sub branch to be created. It must not contain the {@link #PATH_SEPARATOR path separator}
   *          character (slash).
   * @param timeStamp
   *          The time stamp in this branch that the sub branch to be created is supposed to be {@link #getBase() based
   *          at}. It must not be before the base time stamp of this branch and it must be different from the fixed
   *          special time stamp <i> {@link CDOBranchPoint#UNSPECIFIED_DATE unspecified}</i>
   * @see #createBranch(String)
   */
  public CDOBranch createBranch(String name, long timeStamp);

  /**
   * Creates a sub branch of this branch with the given name, {@link #getBase() based} at the {@link CDOTimeProvider
   * current time}.
   */
  public CDOBranch createBranch(String name);
}
