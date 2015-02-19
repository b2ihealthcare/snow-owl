/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionContainer;
import org.eclipse.emf.cdo.util.CDOUpdatable;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.options.IOptionsEvent;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

/**
 * Represents and controls the connection to a model repository in addition to the inherited {@link CDOView view}
 * management functions.
 * <p>
 * A session has the following responsibilities:
 * <ul>
 * <li> {@link CDOSession#getRepositoryInfo() CDORepositoryInfo information}
 * <li> {@link CDOSession#getPackageRegistry() Package registry}
 * <li> {@link CDOSession#getRevisionManager() Data management}
 * <li> {@link CDOSession#getViews() View management}
 * </ul>
 * <p>
 * Note that in order to retrieve, access and store {@link EObject objects} a {@link CDOView view} is needed. The
 * various <code>openXYZ</code> methods are provided for this purpose.
 * <p>
 * A session can fire the following events:
 * <ul>
 * <li> {@link CDOSessionInvalidationEvent} after {@link Options#setPassiveUpdateEnabled(boolean) commit notifications}
 * have been received and processed.
 * <li> {@link CDOSessionLocksChangedEvent} after {@link CDOLock locks} have been acquired or released.
 * </ul>
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link org.eclipse.emf.cdo.session.CDORepositoryInfo}
 * @apiviz.has {@link org.eclipse.emf.cdo.common.model.CDOPackageRegistry}
 * @apiviz.has {@link org.eclipse.emf.cdo.common.branch.CDOBranchManager}
 * @apiviz.has {@link org.eclipse.emf.cdo.common.revision.CDORevisionManager}
 * @apiviz.has {@link org.eclipse.emf.cdo.view.CDOFetchRuleManager}
 * @apiviz.has {@link org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager}
 * @apiviz.has {@link org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager}
 * @apiviz.has {@link org.eclipse.emf.cdo.common.id.CDOIDGenerator}
 * @apiviz.has {@link CDOSession.Options}
 * @apiviz.has {@link CDOSession.ExceptionHandler}
 * @apiviz.has {@link CDOSession.ExceptionHandler}
 * @apiviz.composedOf {@link org.eclipse.emf.cdo.view.CDOView} - - views
 * @apiviz.composedOf {@link org.eclipse.emf.cdo.transaction.CDOTransaction} - - transactions
 * @apiviz.uses {@link CDOSessionInvalidationEvent} - - fires
 * @apiviz.uses {@link CDOSessionLocksChangedEvent} - - fires
 * @apiviz.exclude .*\.CDOTransactionContainer
 * @apiviz.exclude .*\.CDOUpdatable
 */
public interface CDOSession extends CDOCommonSession, CDOUpdatable, CDOTransactionContainer
{
  /**
   * Returns an instance of {@link CDORepositoryInfo} that describes the model repository this {@link CDOSession
   * session} is connected to.
   * 
   * @since 3.0
   */
  public CDORepositoryInfo getRepositoryInfo();

  /**
   * Returns the EMF {@link Registry package registry} that is used by all {@link EObject objects} of all
   * {@link CDOView views} of this session.
   * <p>
   * This registry is managed by the {@link CDOPackageUnit package unit manager} of this session. All {@link EPackage
   * packages} that are already persisted in the repository of this session are automatically registered with this
   * registry. New packages can be locally registered with this registry and are committed to the repository through a
   * {@link CDOTransaction transaction}, if needed.
   */
  public CDOPackageRegistry getPackageRegistry();

  /**
   * Returns the CDO {@link CDOBranchManager branch manager} that manages the {@link CDOBranch branches} of the
   * repository of this session.
   * 
   * @since 3.0
   */
  public CDOBranchManager getBranchManager();

  /**
   * Returns the CDO {@link CDORevisionManager revision manager} that manages the {@link CDORevision revisions} of the
   * repository of this session.
   * 
   * @since 3.0
   */
  public CDORevisionManager getRevisionManager();

  /**
   * @since 3.0
   */
  public CDOFetchRuleManager getFetchRuleManager();

  /**
   * Returns the CDO {@link CDORemoteSessionManager remote session manager} that keeps track of the other remote
   * sessions served by the repository of this local session.
   */
  public CDORemoteSessionManager getRemoteSessionManager();

  /**
   * Returns the CDO {@link CDOCommitInfoManager commit info manager} of this session.
   * 
   * @since 3.0
   */
  public CDOCommitInfoManager getCommitInfoManager();

  public CDOSession.ExceptionHandler getExceptionHandler();

  /**
   * @since 4.1
   */
  public CDOIDGenerator getIDGenerator();

  /**
   * Refreshes the object caches of all (non-historical) {@link CDOView views}.
   * 
   * @since 3.0
   */
  public long refresh();

  /**
   * Equivalent to calling {@link CDOView#waitForUpdate(long)} on each of this session's views. That is, this blocks the
   * calling thread until all of this session's views have incorporated a commit operation with the given time stamp (or
   * higher).
   */
  public void waitForUpdate(long updateTime);

  /**
   * Equivalent to calling {@link CDOView#waitForUpdate(long)} on each of this session's views. That is, this blocks the
   * calling thread until all of this session's views have incorporated a commit operation with the given time stamp (or
   * higher) or the given total timeout has expired.
   */
  public boolean waitForUpdate(long updateTime, long timeoutMillis);

  /**
   * @since 4.0
   * @since Snow Owl 2.6
   * @param nsURIs namespace URI restriction
   */
  public CDOChangeSetData compareRevisions(CDOBranchPoint source, CDOBranchPoint target, String... nsURIs);

  /**
   * Returns the {@link Options options} of this session.
   */
  public Options options();

  /**
   * @since 4.0.1
   */
  public CDOView openView(CDOBranch branch, long timeStamp, boolean shouldInvalidate);

  /**
   * Encapsulates a set of notifying {@link CDOSession session} configuration options.
   * <p>
   * The session options can fire the following events:
   * <ul>
   * <li> {@link GeneratedPackageEmulationEvent} after the {@link #setGeneratedPackageEmulationEnabled(boolean) generated
   * package emulation mode} has changed.
   * <li> {@link CollectionLoadingPolicyEvent} after the {@link #setCollectionLoadingPolicy(CDOCollectionLoadingPolicy)
   * collection loading policy} has changed.
   * <li> {@link LobCacheEvent} after the {@link #setLobCache(CDOLobStore) large object cache} has changed.
   * </ul>
   * 
   * @author Simon McDuff
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @apiviz.has {@link CDOCollectionLoadingPolicy}
   * @apiviz.has {@link org.eclipse.emf.cdo.common.lob.CDOLobStore} oneway - - lobCache
   * @apiviz.uses {@link CDOSession.Options.GeneratedPackageEmulationEvent} - - fires
   * @apiviz.uses {@link CDOSession.Options.CollectionLoadingPolicyEvent} - - fires
   * @apiviz.uses {@link CDOSession.Options.LobCacheEvent} - - fires
   */
  public interface Options extends CDOCommonSession.Options
  {
    /**
     * Returns the {@link CDOSession session} of this options object.
     * 
     * @since 4.0
     */
    public CDOSession getContainer();

    public boolean isGeneratedPackageEmulationEnabled();

    public void setGeneratedPackageEmulationEnabled(boolean generatedPackageEmulationEnabled);

    /**
     * The {@link CDOCollectionLoadingPolicy collection loading policy} of this {@link CDOSession session} controls how
     * a list gets populated. By default, when an object is fetched, all its elements are filled with the proper values.
     * <p>
     * This could be time-consuming, especially if the reference list does not need to be accessed. In CDO it is
     * possible to partially load collections. The default list implementation that is shipped with CDO makes a
     * distinction between the two following situations:
     * <ol>
     * <li>How many CDOIDs to fill when an object is loaded for the first time;
     * <li>Which elements to fill with CDOIDs when the accessed element is not yet filled.
     * </ol>
     * Example:
     * <p>
     * <code>CDOUtil.createCollectionLoadingPolicy(initialElements, subsequentElements);</code>
     * <p>
     * The user can also provide its own implementation of the CDOCollectionLoadingPolicy interface.
     */
    public CDOCollectionLoadingPolicy getCollectionLoadingPolicy();

    /**
     * Sets the {@link CDOCollectionLoadingPolicy collection loading} to be used by this session.
     */
    public void setCollectionLoadingPolicy(CDOCollectionLoadingPolicy policy);

    /**
     * Returns the {@link CDOLobStore large object cache} currently being used by this session.
     * 
     * @since 4.0
     */
    public CDOLobStore getLobCache();

    /**
     * Sets the {@link CDOLobStore large object cache} to be used by this session.
     * 
     * @since 4.0
     */
    public void setLobCache(CDOLobStore lobCache);

    /**
     * An {@link IOptionsEvent options event} fired when the
     * {@link Options#setGeneratedPackageEmulationEnabled(boolean) generated package emulation enabled} option of a
     * {@link CDOSession session} has changed.
     * 
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface GeneratedPackageEmulationEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired when the
     * {@link Options#setCollectionLoadingPolicy(CDOCollectionLoadingPolicy) collection loading policy} option of a
     * {@link CDOSession session} has changed.
     * 
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface CollectionLoadingPolicyEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired when the {@link Options#setLobCache(CDOLobStore) large object cache}
     * option of a {@link CDOSession session} has changed.
     * 
     * @author Eike Stepper
     * @since 4.0
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface LobCacheEvent extends IOptionsEvent
    {
    }
  }

  /**
   * Handles {@link CDOSessionProtocol protocol} exceptions if
   * {@link CDOSessionConfiguration#setExceptionHandler(CDOSession.ExceptionHandler) configured} before the session has
   * been opened.
   * 
   * @author Eike Stepper
   */
  public interface ExceptionHandler
  {
    public void handleException(CDOSession session, int attempt, Exception exception) throws Exception;
  }
}
