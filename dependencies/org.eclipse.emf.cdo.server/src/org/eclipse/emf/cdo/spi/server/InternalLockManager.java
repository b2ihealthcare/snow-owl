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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.server.ILockingManager;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.concurrent.IRWOLockManager;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The type of the to-be-locked objects is either {@link CDOIDAndBranch} or {@link CDOID}, depending on whether
 * branching is supported by the repository or not.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalLockManager extends IRWOLockManager<Object, IView>, ILockingManager
{
  public InternalRepository getRepository();

  public void setRepository(InternalRepository repository);

  /**
   * @since 4.0
   */
  public Object getLockEntryObject(Object key);

  /**
   * @since 4.0
   */
  public Object getLockKey(CDOID id, CDOBranch branch);

  /**
   * @since 4.0
   */
  public CDOID getLockKeyID(Object key);

  /**
   * @since 4.0
   */
  public Map<CDOID, LockGrade> getLocks(IView view);

  /**
   * @since 4.0
   */
  @Deprecated
  public void lock(boolean explicit, LockType type, IView context, Collection<? extends Object> objects, long timeout)
      throws InterruptedException;

  /**
   * @since 4.1
   */
  public List<LockState<Object, IView>> lock2(boolean explicit, LockType type, IView context,
      Collection<? extends Object> objects, boolean recursive, long timeout) throws InterruptedException;

  /**
   * Attempts to release for a given locktype, view and objects.
   * 
   * @throws IllegalMonitorStateException
   *           Unlocking objects without lock.
   * @since 4.0
   */
  @Deprecated
  public void unlock(boolean explicit, LockType type, IView context, Collection<? extends Object> objects);

  /**
   * @since 4.1
   */
  public List<LockState<Object, IView>> unlock2(boolean explicit, LockType type, IView context,
      Collection<? extends Object> objects, boolean recursive);

  /**
   * Attempts to release all locks(read and write) for a given view.
   * 
   * @since 4.0
   */
  @Deprecated
  public void unlock(boolean explicit, IView context);

  /**
   * @since 4.1
   */
  public List<LockState<Object, IView>> unlock2(boolean explicit, IView context);

  /**
   * @since 4.0
   */
  public LockArea createLockArea(InternalView view);

  /**
   * @since 4.1
   */
  public LockArea createLockArea(InternalView view, String lockAreaID);

  /**
   * @since 4.1
   */
  // TODO (CD) I've also added this to DurableLocking2 Refactoring opportunity?
  public void updateLockArea(LockArea lockArea);

  /**
   * @since 4.0
   */
  public IView openView(ISession session, int viewID, boolean readOnly, String durableLockingID);

  /**
   * @since 4.1
   */
  public LockGrade getLockGrade(Object key);

  /**
   * @since 4.1
   */
  public LockState<Object, IView> getLockState(Object key);

  /**
   * @since 4.1
   */
  public void setLockState(Object key, LockState<Object, IView> lockState);

  /**
   * @since 4.1
   */
  public void reloadLocks();
}
