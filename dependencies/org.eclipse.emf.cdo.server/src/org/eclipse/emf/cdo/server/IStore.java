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
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.util.om.monitor.ProgressDistributor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Represents the physical data storage back-end of a CDO {@link IRepository repository}, such as a database or a file
 * system folder.
 * 
 * @author Eike Stepper
 * @apiviz.landmark
 * @apiviz.has {@link IStore.ChangeFormat}
 * @apiviz.has {@link IStore.RevisionTemporality}
 * @apiviz.has {@link IStore.RevisionParallelism}
 * @apiviz.uses {@link IStoreAccessor} - - creates
 */
public interface IStore
{
  /**
   * @since 2.0
   */
  public IRepository getRepository();

  /**
   * @since 2.0
   */
  public String getType();

  /**
   * @since 3.0
   */
  public Set<CDOID.ObjectType> getObjectIDTypes();

  /**
   * @since 4.0
   */
  public CDOID createObjectID(String val);

  /**
   * @since 2.0
   */
  public Set<ChangeFormat> getSupportedChangeFormats();

  /**
   * @since 2.0
   */
  public Set<RevisionTemporality> getSupportedRevisionTemporalities();

  /**
   * @since 2.0
   */
  public Set<RevisionParallelism> getSupportedRevisionParallelisms();

  /**
   * @since 2.0
   */
  public RevisionTemporality getRevisionTemporality();

  /**
   * @since 2.0
   */
  public RevisionParallelism getRevisionParallelism();

  /**
   * Returns <code>true</code>if this store was activated for the first time, <code>false</code> otherwise.
   * 
   * @since 4.0
   */
  public boolean isFirstStart();

  /**
   * Returns the store creation time.
   * 
   * @since 2.0
   */
  public long getCreationTime();

  /**
   * Returns the id of the last branch that has been created in this store.
   * 
   * @since 3.0
   */
  public int getLastBranchID();

  /**
   * Returns the id of the last local branch that has been created in this store.
   * 
   * @since 3.0
   */
  public int getLastLocalBranchID();

  /**
   * Returns the time stamp of the last successful commit operation.
   * 
   * @since 3.0
   */
  public long getLastCommitTime();

  /**
   * Returns the time stamp of the last successful commit operation to a non-local {@link CDOBranch branch}.
   * 
   * @since 3.0
   */
  public long getLastNonLocalCommitTime();

  /**
   * Returns a map filled with the property entries for the requested property <code>names</code> if names is not
   * <code>null</code> and not {@link Collection#isEmpty() empty}, all existing property entries otherwise.
   * 
   * @since 4.0
   */
  public Map<String, String> getPersistentProperties(Set<String> names);

  /**
   * @since 4.0
   */
  public void setPersistentProperties(Map<String, String> properties);

  /**
   * @since 4.0
   */
  public void removePersistentProperties(Set<String> names);

  /**
   * Returns a reader that can be used to read from this store in the context of the given session.
   * 
   * @param session
   *          The session that should be used as a context for read access or <code>null</code>. The store implementor
   *          is free to interpret and use the session in a manner suitable for him or ignore it at all. It is meant
   *          only as a hint. Implementor can use it as a key into a cache and/or register a
   *          {@link org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter LifecycleEventAdapter} with it to intercept
   *          cleanup on session close. Note however that the session can be <code>null</code>, for example during
   *          startup of the server while the repositories are initialized but before any user session has been opened.
   * @return a reader that can be used to read from this store in the context of the given session, never
   *         <code>null</code>.
   * @since 2.0
   */
  public IStoreAccessor getReader(ISession session);

  /**
   * Returns a writer that can be used to write to this store in the context of the given view. The given view is always
   * marked as a transaction.
   * 
   * @param transaction
   *          The view that must be used as a context for write access. The store implementor is free to interpret and
   *          use the view in a manner suitable for him or ignore it at all. It is meant only as a hint. Implementor can
   *          use it as a key into a cache and/or register a
   *          {@link org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter LifecycleEventAdapter} with it to intercept
   *          cleanup on view close.
   * @return a writer that can be used to write to this store in the context of the given view, never <code>null</code>.
   * @since 2.0
   */
  public IStoreAccessor getWriter(ITransaction transaction);

  /**
   * @since 2.0
   */
  public ProgressDistributor getIndicatingCommitDistributor();

  /**
   * Enumerates the possible data formats a {@link IStore store} can accept for commit operations.
   * 
   * @author Eike Stepper
   * @since 2.0
   */
  public enum ChangeFormat
  {
    /**
     * An indication that the store accepts full {@link CDORevision revisions} for dirty objects.
     */
    REVISION,

    /**
     * An indication that the store accepts incremental {@link CDORevisionDelta revision deltas} for dirty objects.
     */
    DELTA
  }

  /**
   * Enumerates the possible history recording options a {@link IStore store} can accept.
   * 
   * @author Eike Stepper
   * @since 2.0
   */
  public enum RevisionTemporality
  {
    /**
     * An indication that the store can work <b>without</b> <i>auditing</i>.
     */
    NONE,

    /**
     * An indication that the store can work <b>with</b> <i>auditing</i>.
     */
    AUDITING
  }

  /**
   * Enumerates the possible branching options a {@link IStore store} can accept.
   * 
   * @author Eike Stepper
   * @since 2.0
   */
  public enum RevisionParallelism
  {
    /**
     * An indication that the store can work <b>without</b> <i>branching</i>.
     */
    NONE,

    /**
     * An indication that the store can work <b>with</b> <i>branching</i>.
     */
    BRANCHING
  }

  /**
   * A marker interface for {@link IStore stores} that can handle {@link CDOID IDs} assigned by a
   * {@link IDGenerationLocation#CLIENT client}, typically {@link ObjectType#UUID UUIDs}.
   * 
   * @author Eike Stepper
   * @since 4.1
   * @apiviz.exclude
   */
  public interface CanHandleClientAssignedIDs
  {
  }
}
