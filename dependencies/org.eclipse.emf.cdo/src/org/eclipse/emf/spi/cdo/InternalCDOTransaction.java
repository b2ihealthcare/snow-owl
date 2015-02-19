/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOTransaction extends CDOTransaction, InternalCDOUserTransaction, InternalCDOView
{
  public InternalCDOCommitContext createCommitContext();

  /**
   * @since 3.0
   */
  public InternalCDOSavepoint setSavepoint();

  /**
   * @since 4.1
   */
  public InternalCDOSavepoint getFirstSavepoint();

  /**
   * @since 3.0
   */
  public InternalCDOSavepoint getLastSavepoint();

  /**
   * @since 3.0
   */
  public InternalCDOSavepoint handleSetSavepoint();

  /**
   * @since 3.0
   */
  public void handleRollback(InternalCDOSavepoint savepoint);

  public CDOTransactionStrategy getTransactionStrategy();

  public void setTransactionStrategy(CDOTransactionStrategy transactionStrategy);

  /**
   * @return never <code>null</code>;
   */
  public CDOResourceFolder getOrCreateResourceFolder(List<String> names);

  public void detachObject(InternalCDOObject object);

  /**
   * @deprecated {@link #createIDForNewObject(EObject)} is called since 4.1.
   */
  @Deprecated
  public CDOIDTemp getNextTemporaryID();

  /**
   * @since 4.1
   */
  public CDOID createIDForNewObject(EObject object);

  /**
   * @since 4.0
   */
  public void registerAttached(InternalCDOObject object, boolean isNew);

  public void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta);

  public void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta);

  public void registerRevisionDelta(CDORevisionDelta revisionDelta);

  public void setConflict(InternalCDOObject object);

  /**
   * @param source
   *          May be <code>null</code> if changeSetData does not result from a
   *          {@link #merge(CDOBranchPoint, org.eclipse.emf.cdo.transaction.CDOMerger) merge} or if the merge was not in
   *          a {@link CDOBranch#isLocal() local} branch.
   * @since 4.0
   * @deprecated Use
   *             {@link #applyChangeSet(CDOChangeSetData, CDORevisionProvider, CDORevisionProvider, CDOBranchPoint, boolean)}
   */
  @Deprecated
  public Pair<CDOChangeSetData, Pair<Map<CDOID, CDOID>, List<CDOID>>> applyChangeSetData(
      CDOChangeSetData changeSetData, CDORevisionProvider ancestorProvider, CDORevisionProvider targetProvider,
      CDOBranchPoint source);

  /**
   * @param source
   *          May be <code>null</code> if changeSetData does not result from a
   *          {@link #merge(CDOBranchPoint, org.eclipse.emf.cdo.transaction.CDOMerger) merge} or if the merge was not in
   *          a {@link CDOBranch#isLocal() local} branch.
   * @since 4.1
   */
  public ApplyChangeSetResult applyChangeSet(CDOChangeSetData changeSetData, CDORevisionProvider ancestorProvider,
      CDORevisionProvider targetProvider, CDOBranchPoint source, boolean keepVersions)
      throws ChangeSetOutdatedException;

  /**
   * @since 4.0
   */
  public Map<InternalCDOObject, InternalCDORevision> getCleanRevisions();

  /**
  * @since 4.0.1
  */
  public void applyChangeSetData(CDOChangeSetData changeSetData);

  /**
   * @since CDO 4.1
   * @since Snow Owl 2.7
   */
  public void revertChangeSetData(final CDOChangeSetData changeSetData);

  //
  // /**
  // * @since 4.0.1
  // */
  // public void applyChangeSetData(CDOTransaction dirtyTransaction);

  /**
   * Provides a context for a commit operation.
   * 
   * @author Simon McDuff
   * @noimplement This interface is not intended to be implemented by clients.
   * @noextend This interface is not intended to be extended by clients.
   */
  public interface InternalCDOCommitContext extends CDOCommitContext
  {
    public InternalCDOTransaction getTransaction();

    public void preCommit();

    public void postCommit(CommitTransactionResult result);
  }

  /**
   * @author Eike Stepper
   * @since 4.1
   */
  public final class ApplyChangeSetResult
  {
    private CDOChangeSetData changeSetData = new CDOChangeSetDataImpl();

    private Map<CDOID, CDOID> idMappings = new HashMap<CDOID, CDOID>();

    private List<CDOID> adjustedObjects = new ArrayList<CDOID>();

    public ApplyChangeSetResult()
    {
    }

    public CDOChangeSetData getChangeSetData()
    {
      return changeSetData;
    }

    public Map<CDOID, CDOID> getIDMappings()
    {
      return idMappings;
    }

    public List<CDOID> getAdjustedObjects()
    {
      return adjustedObjects;
    }
  }

  /**
   * @author Eike Stepper
   * @since 4.1
   */
  public static final class ChangeSetOutdatedException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public ChangeSetOutdatedException()
    {
      super("Change set is outdated");
    }
  }
}
