/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lock;

import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

/**
 * A client-side representation of <i>all</i> the locks on a single CDOObject.
 * <p>
 * As an individual lock is always owned by view, which in turn is owned by a session, the methods on this interface
 * return instances of {@link CDOLockOwner} which carry that information.
 * <p>
 * 
 * @author Caspar De Groot
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link java.lang.Object} oneway - - lockedObject
 * @apiviz.owns {@link CDOLockOwner} - - readLockOwners
 * @apiviz.has {@link CDOLockOwner} oneway - - writeLockOwner
 * @apiviz.has {@link CDOLockOwner} oneway - - writeOptionOwner
 */
public interface CDOLockState
{
  /**
   * Gets a unique identifier for the object that is locked; typically a {@link CDOID} or a {@link CDOIDAndBranch},
   * depending on whether branching support is enabled or not
   * 
   * @return the identifier
   */
  public Object getLockedObject();

  /**
   * If the 'others' argument is <code>false</code>, this method returns <code>true</code> if this lock is currently
   * held by the <i>requesting</i> CDOView, <code>false</code> otherwise.
   * <p>
   * If the 'others' argument is <code>true</code>, this method returns <code>true</code> if this lock is currently held
   * by <i>another</i> view (i.e. any view different from the requesting one), <code>false</code> otherwise.
   */
  public boolean isLocked(LockType lockType, CDOLockOwner lockOwner, boolean others);

  public Set<CDOLockOwner> getReadLockOwners();

  public CDOLockOwner getWriteLockOwner();

  public CDOLockOwner getWriteOptionOwner();
}
