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
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetDataProvider;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * One in a sequence of possibly several points in time of a {@link CDOTransaction transaction} that encapsulates the
 * changes to transactional objects and that later changes can be {@link #rollback() rolled back} to.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSavepoint extends CDOUserSavepoint, CDOChangeSetDataProvider
{
  /**
   * @since 3.0
   */
  public CDOTransaction getTransaction();

  public CDOSavepoint getNextSavepoint();

  public CDOSavepoint getPreviousSavepoint();

  /**
   * @since 3.0
   */
  public boolean wasDirty();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDORevision> getBaseNewObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getNewObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getDetachedObjects();

  /**
   * Bug 283985 (Re-attachment)
   * 
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getReattachedObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getDirtyObjects();

  /**
   * @since 3.0
   */
  public ConcurrentMap<CDOID, CDORevisionDelta> getRevisionDeltas();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDORevision> getAllBaseNewObjects();

  /**
   * Return the list of new objects from this point without objects that are removed.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllNewObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllDetachedObjects();

  /**
   * Return the list of new objects from this point.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllDirtyObjects();

  /**
   * Return the list of all deltas without objects that are removed.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDORevisionDelta> getAllRevisionDeltas();

  /**
   * @since 4.0
   */
  public CDOChangeSetData getAllChangeSetData();
}
