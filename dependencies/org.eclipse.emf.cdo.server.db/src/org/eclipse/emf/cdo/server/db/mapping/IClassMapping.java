/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Set;

/**
 * Basic interface for class mappings.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public interface IClassMapping
{
  /**
   * @since 3.0
   */
  public EClass getEClass();

  /**
   * Returns all DB tables which are used by this class and all its contained features.
   * 
   * @return a collection of all tables of this class and all its contained features.
   * @since 3.0
   */
  public List<IDBTable> getDBTables();

  /**
   * Get the mapping of the many-valued feature.
   * 
   * @param feature
   *          the feature for which the mapping should be returned. <code>feature.isMany()</code> has to be
   *          <code>true</code>.
   * @return the list mapping corresponding to the feature.
   */
  public IListMapping getListMapping(EStructuralFeature feature);

  /**
   * @since 3.0
   */
  public List<IListMapping> getListMappings();

  /**
   * @since 4.0
   */
  public List<ITypeMapping> getValueMappings();

  /**
   * Read a revision. The branch and timestamp to be read are derived from the branchPoint which is set to the Revision.
   * Note that non-audit stores only support {@link CDOBranchPoint#UNSPECIFIED_DATE} and non-branching stores only
   * support the main branch.
   * 
   * @param accessor
   *          the accessor to use.
   * @param revision
   *          the revision object into which the data should be read. The revision has to be have its ID set to the
   *          requested object's ID. The version is ignored, as the version parameter is used to determine the version
   *          to be read.
   * @param listChunk
   *          the chunk size to read attribute lists.
   * @return <code>true</code>, if the revision has been found and read correctly. <code>false</code> if the revision
   *         could not be found. In this case, the content of <code>revision</code> is undefined.
   */
  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk);

  /**
   * Write the revision data to the database.
   * 
   * @param accessor
   *          the accessor to use.
   * @param revision
   *          the revision to write.
   * @param mapType
   *          <code>true</code> if the type of the object is supposed to be mapped, <code>false</code> otherwise.
   * @param revise
   *          <code>true</code> if the previous revision is supposed to be revised, <code>false</code> otherwise.
   * @param monitor
   *          the monitor to indicate progress.
   * @since 4.0
   */
  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, boolean mapType, boolean revise,
      OMMonitor monitor);

  /**
   * Detaches (deletes) a CDO object leaving a "ghost" revision behind.
   * 
   * @param accessor
   *          the accessor to use.
   * @param id
   *          the id to revise.
   * @param version
   *          the last valid version.
   * @param timeStamp
   *          the timestamp of detach.
   * @param monitor
   *          the monitor to indicate progress.
   * @since 3.0
   */
  public void detachObject(IDBStoreAccessor accessor, CDOID id, int version, CDOBranch branch, long timeStamp,
      OMMonitor monitor);

  /**
   * Create a prepared statement which returns all IDs of instances of the corresponding class.
   * 
   * @param accessor
   *          the accessor to use to create the statement
   * @return the prepared statement ready to be executed using <code>result.executeQuery()</code>.
   * @since 3.0
   */
  public PreparedStatement createObjectIDStatement(IDBStoreAccessor accessor);

  /**
   * Create a prepared statement which returns all IDs of instances of the corresponding class.
   * 
   * @param accessor
   *          the accessor to use to create the statement
   * @param folderId
   *          the ID of the containing folder. <code>0</code> means none.
   * @param name
   *          the name of the resource node to look up
   * @param exactMatch
   *          if <code>true</code>, <code>name</code> must match exactly, otherwise all resource nodes starting with
   *          <code>name</code> are returned.
   * @param branchPoint
   *          a branchPoint (branch and timestamp). A timestamp in the past if past versions should be looked up. In
   *          case of no audit support, this must be {@link CDORevision#UNSPECIFIED_DATE}. In case of non branching
   *          support the branch id must be equal to {@link CDOBranch#MAIN_BRANCH_ID}.
   * @return the prepared statement ready to be executed using <code>result.executeQuery()</code>.
   * @throws ImplementationError
   *           if called on a mapping which does not map an <code>EClass instanceof CDOResourceNode</code>.
   * @since 3.0
   */
  public PreparedStatement createResourceQueryStatement(IDBStoreAccessor accessor, CDOID folderId, String name,
      boolean exactMatch, CDOBranchPoint branchPoint);

  /**
   * Passes all revisions of the store to the {@link CDORevisionHandler handler} if <b>all</b> of the following
   * conditions are met:
   * <ul>
   * <li>The <code>branch</code> parameter is <code>null</code> or equal to <code>revision.getBranch()</code>.
   * <li>The <code>timeStamp</code> parameter is {@link CDOBranchPoint#UNSPECIFIED_DATE} or equal to
   * <code>revision.getTimeStamp()</code>.
   * </ul>
   * 
   * @see IMappingStrategy#handleRevisions(IDBStoreAccessor, org.eclipse.emf.ecore.EClass, CDOBranch, long, boolean,
   *      CDORevisionHandler)
   * @since 4.0
   */
  public void handleRevisions(IDBStoreAccessor accessor, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler);

  /**
   * Returns a set of CDOIDs that have at least one revision in any of the passed branches and time ranges.
   * DetachedCDORevisions must also be considered!
   * 
   * @see IStoreAccessor#readChangeSet(OMMonitor, CDOChangeSetSegment...)
   * @since 3.0
   */
  public Set<CDOID> readChangeSet(IDBStoreAccessor accessor, CDOChangeSetSegment[] segments);

  /**
   * Retrieve cross-references from DB
   * 
   * @param idString
   *          a string of the form "(id1, id2, id3, ...)" which can be used directly in SQL to form the where-part
   *          "SELECT * FROM foobar WHERE foobar.target IN [idString]".
   * @see IStoreAccessor#queryXRefs(QueryXRefsContext)
   * @since 4.0
   */
  public boolean queryXRefs(IDBStoreAccessor accessor, QueryXRefsContext context, String idString);
}
