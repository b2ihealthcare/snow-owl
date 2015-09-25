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
package org.eclipse.emf.cdo.common.revision;

import java.util.List;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Provides access to {@link CDORevision revisions} in a CDO {@link CDOCommonRepository repository} by demand loading
 * and caching them.
 * <p>
 * Revisions are generally queried by:
 * <p>
 * <ul>
 * <li>their object {@link CDOID ID} <b>and</b>
 * <li>their {@link CDOBranch branch} plus <b>either</b>:
 * <ul>
 * <li>a timestamp <b>or</b>
 * <li>a version
 * </ul>
 * </ul>
 * <p>
 * If querying by timestamp it's also possible to ask for multiple revisions (identified by a list of object IDs) in one
 * round trip (to the server if this revision manager is contained by a {@link CDOCommonSession session} or to the
 * backend store if it is contained by a {@link CDOCommonRepository repository}.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link CDORevisionFactory}
 * @apiviz.has {@link CDORevisionCache}
 * @apiviz.uses {@link CDORevision} - - loads
 */
public interface CDORevisionManager
{
  /**
   * Returns the {@link CDORevision#getEClass() type} of an object if a revision for that object is in the revision
   * cache, <code>null</code> otherwise.
   * <p>
   * Same as calling {@link #getObjectType(CDOID, CDOBranchManager) getObjectType(id, null)}.
   * 
   * @see EObject#eClass()
   * @see #getObjectType(CDOID, CDOBranchManager)
   */
  public EClass getObjectType(CDOID id);

  /**
   * Returns the {@link CDORevision#getEClass() type} of an object.
   * <p>
   * If no revision for that object is found in the revision cache the following is tried:
   * <ol>
   * <li>If <code>branchManagerForLoadOnDemand</code> is not <code>null</code> the first revision of the object in the
   * main branch is loaded from the server and its type is returned.
   * <li>Otherwise (i.e., if <code>branchManagerForLoadOnDemand</code> is <code>null</code> or the object does not
   * exist) <code>null</code> is returned.
   * </ol>
   * <p>
   * A {@link CDOBranchManager branch manager} is required instead of just a boolean value to specify whether to
   * demand-load or not because this revision manager must be able to access the
   * {@link CDOBranchManager#getMainBranch() main branch} while demand-loading.
   * 
   * @see EObject#eClass()
   * @see #getObjectType(CDOID)
   * @since 4.1
   */
  public EClass getObjectType(CDOID id, CDOBranchManager branchManagerForLoadOnDemand);

  /**
   * Returns <code>true</code> if the {@link CDORevisionCache revision cache} contains a {@link CDORevision revision}
   * with the given {@link CDOID ID} at the given {@link CDOBranchPoint branch point} (branch + timestamp),
   * <code>false</code> otherwise.
   * 
   * @see CDORevisionManager#getRevision(CDOID, CDOBranchPoint, int, int, boolean)
   * @see CDORevisionManager#getRevisions(List, CDOBranchPoint, int, int, boolean)
   */
  public boolean containsRevision(CDOID id, CDOBranchPoint branchPoint);

  /**
   * Returns the {@link CDORevision revision} with the given {@link CDOID ID} at the given {@link CDOBranchPoint branch
   * point} (branch + timestamp), optionally demand loading it if it is not already in the {@link CDORevisionCache
   * cache}.
   * 
   * @param referenceChunk
   *          The number of target {@link CDOID IDs} to load for each many-valued reference in the returned revision, or
   *          {@link CDORevision#UNCHUNKED} for all such list elements (IDs).
   * @param prefetchDepth
   *          The number of nested containment levels to load revisions for in one round trip. Use the symbolic
   *          constants {@link CDORevision#DEPTH_INFINITE} to prefetch all contained revisions or
   *          {@link CDORevision#DEPTH_NONE} to not prefetch anything. Only the explicitely requested revision is
   *          returned by this method. If additional revisions are prefetched they are placed in the revision cache to
   *          speed up subsequent calls to this method.
   * @param loadOnDemand
   *          If the requested revision is not contained in the revision cache it depends on this parameter's value
   *          whether the revision is loaded from the server or <code>null</code> is returned.
   * @see #getRevisions(List, CDOBranchPoint, int, int, boolean)
   * @see #getRevisionByVersion(CDOID, CDOBranchVersion, int, boolean)
   */
  public CDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth,
      boolean loadOnDemand);

  /**
   * Returns the {@link CDORevision revisions} with the given {@link CDOID IDs} at the given {@link CDOBranchPoint
   * branch point} (branch + timestamp), optionally demand loading them if they are not already in the
   * {@link CDORevisionCache cache}.
   * 
   * @param referenceChunk
   *          The number of target {@link CDOID IDs} to load for each many-valued reference in the returned revisions,
   *          or {@link CDORevision#UNCHUNKED} for all such list elements (IDs).
   * @param prefetchDepth
   *          The number of nested containment levels to load revisions for in one round trip. Use the symbolic
   *          constants {@link CDORevision#DEPTH_INFINITE} to prefetch all contained revisions or
   *          {@link CDORevision#DEPTH_NONE} to not prefetch anything. Only the explicitely requested revisions are
   *          returned by this method. If additional revisions are prefetched they are placed in the revision cache to
   *          speed up subsequent calls to this method.
   * @param loadOnDemand
   *          If one or more of the requested revisions is/are not contained in the revision cache it depends on this
   *          parameter's value whether the revision(s) is/are loaded from the server or <code>null</code> is placed in
   *          the list that is returned.
   * @see #getRevision(CDOID, CDOBranchPoint, int, int, boolean)
   */
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk,
      int prefetchDepth, boolean loadOnDemand);

  /**
   * Returns <code>true</code> if the {@link CDORevisionCache revision cache} contains a {@link CDORevision revision}
   * with the given {@link CDOID ID} at the given {@link CDOBranchVersion branch version} (branch + version),
   * <code>false</code> otherwise.
   * 
   * @see #getRevisionByVersion(CDOID, CDOBranchVersion, int, boolean)
   */
  public boolean containsRevisionByVersion(CDOID id, CDOBranchVersion branchVersion);

  /**
   * Returns the {@link CDORevision revision} with the given {@link CDOID ID} at the given {@link CDOBranchVersion
   * branch version} (branch + version), optionally demand loading it if it is not already in the
   * {@link CDORevisionCache cache}.
   * <p>
   * Prefetching of nested containment levels is not support by this method because the version of a particular revision
   * can not serve as a reasonable baseline criterium for a consistent graph of multiple revisions.
   * 
   * @param referenceChunk
   *          The number of target {@link CDOID IDs} to load for each many-valued reference in the returned revision, or
   *          {@link CDORevision#UNCHUNKED} for all such list elements (IDs).
   * @param loadOnDemand
   *          If the requested revision is not contained in the revision cache it depends on this parameter's value
   *          whether the revision is loaded from the server or <code>null</code> is returned.
   * @see #getRevision(CDOID, CDOBranchPoint, int, int, boolean)
   */
  public CDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk,
      boolean loadOnDemand);
}
