/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 215688
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 *    Gonzague Reydet - bug 298334
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonTransaction;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetDataProvider;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.object.CDOObjectMerger;

import org.eclipse.net4j.util.options.IOptionsEvent;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

/**
 * A read-write view to the <em>current</em> (i.e. latest) state of the object graph in the repository.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOTransaction extends CDOView, CDOCommonTransaction, CDOUserTransaction, CDOChangeSetDataProvider
{
  /**
   * Returns <code>true</code> if this transaction is not closed and contains uncommitted changes, <code>false</code>
   * otherwise.
   */
  public boolean isDirty();

  /**
   * Returns <code>true</code> if this transaction contains local modifications that are conflicting with remote
   * modifications, <code>false</code> otherwise.
   */
  public boolean hasConflict();

  public Set<CDOObject> getConflicts();

  /**
   * @since 3.0
   */
  public CDOChangeSetData merge(CDOBranchPoint source, CDOMerger merger);

  /**
   * @since 4.0
   */
  public CDOChangeSetData merge(CDOBranchPoint source, CDOBranchPoint sourceBase, CDOMerger merger);

  /**
   * @since 4.0
   */
  public CDOResourceFolder createResourceFolder(String path);

  /**
   * @since 4.0
   */
  public CDOResourceFolder getOrCreateResourceFolder(String path);

  /**
   * @see ResourceSet#createResource(URI)
   */
  public CDOResource createResource(String path);

  public CDOResource getOrCreateResource(String path);

  /**
   * @since 4.0
   */
  public void addTransactionHandler(CDOTransactionHandlerBase handler);

  /**
   * @since 4.0
   */
  public void removeTransactionHandler(CDOTransactionHandlerBase handler);

  /**
   * @since 3.0
   */
  public CDOTransactionHandler[] getTransactionHandlers();

  /**
   * @since 4.0
   */
  public CDOTransactionHandler1[] getTransactionHandlers1();

  /**
   * @since 4.0
   */
  public CDOTransactionHandler2[] getTransactionHandlers2();

  /**
   * @since 3.0
   */
  public CDOSavepoint setSavepoint();

  /**
   * @since 4.1
   */
  public CDOSavepoint getFirstSavepoint();

  /**
   * @since 3.0
   */
  public CDOSavepoint getLastSavepoint();

  public Map<CDOID, CDOObject> getNewObjects();

  /**
   * @since 2.0
   */
  public Map<CDOID, CDOObject> getDetachedObjects();

  public Map<CDOID, CDOObject> getDirtyObjects();

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas();

  /**
   * @since 3.0
   */
  public CDOSavepoint[] exportChanges(OutputStream out) throws IOException;

  /**
   * @since 3.0
   */
  public CDOSavepoint[] importChanges(InputStream in, boolean reconstructSavepoints) throws IOException;

  /**
   * @since 4.0.1
   */
  public CDOSavepoint[] importChanges(InputStream in, boolean reconstructSavepoints, final CDOObjectMerger objectMerger)
      throws IOException;

  public long getLastCommitTime();

  /**
   * Returns the comment to be used in the next commit operation.
   * 
   * @see CDOCommitInfo#getComment()
   * @since 3.0
   */
  public String getCommitComment();

  /**
   * Sets the comment to be used in the next commit operation.
   * 
   * @see CDOCommitInfo#getComment()
   * @since 3.0
   */
  public void setCommitComment(String comment);

  /**
   * @since 4.0
   */
  public void setCommittables(Set<? extends EObject> committables);

  /**
   * @since 4.0
   */
  public Set<? extends EObject> getCommittables();

  /**
   * @since 4.0
   */
  public CDOQuery createQuery(String language, String queryString, boolean considerDirtyState);

  /**
   * @since 4.0
   */
  public CDOQuery createQuery(String language, String queryString, Object context, boolean considerDirtyState);

  public Options options();

  /**
   * Encapsulates a set of notifying {@link CDOTransaction transaction} configuration options.
   * 
   * @author Simon McDuff
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Options extends CDOView.Options
  {
    /**
     * Returns the {@link CDOTransaction transaction} of this options object.
     * 
     * @since 4.0
     */
    public CDOTransaction getContainer();

    /**
     * Returns a copy of the conflict resolver list of this transaction.
     */
    public CDOConflictResolver[] getConflictResolvers();

    /**
     * Sets the conflict resolver list of this transaction.
     */
    public void setConflictResolvers(CDOConflictResolver[] resolvers);

    /**
     * Adds a conflict resolver to the list of conflict resolvers of this transaction.
     */
    public void addConflictResolver(CDOConflictResolver resolver);

    /**
     * Removes a conflict resolver from the list of conflict resolvers of this transaction.
     */
    public void removeConflictResolver(CDOConflictResolver resolver);

    /**
     * Returns true if locks in this view will be removes when {@link CDOTransaction#commit()} or
     * {@link CDOTransaction#rollback()} is called.
     * <p>
     * Default value is true.
     */
    public boolean isAutoReleaseLocksEnabled();

    /**
     * Specifies whether locks in this view will be removed when {@link CDOTransaction#commit()} or
     * {@link CDOTransaction#rollback()} is called.
     * <p>
     * If false all locks are kept.
     * <p>
     * Default value is true.
     */
    public void setAutoReleaseLocksEnabled(boolean on);

    /**
     * An {@link IOptionsEvent options event} fired from transaction {@link CDOTransaction#options() options} when the
     * {@link Options#addConflictResolver(CDOConflictResolver) conflict resolvers} option has changed.
     * 
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface ConflictResolversEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from transaction {@link CDOTransaction#options() options} when the
     * {@link Options#setAutoReleaseLocksEnabled(boolean) auto release locks} option has changed.
     * 
     * @author Eike Stepper
     * @since 3.0
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface AutoReleaseLocksEvent extends IOptionsEvent
    {
    }
  }
}
