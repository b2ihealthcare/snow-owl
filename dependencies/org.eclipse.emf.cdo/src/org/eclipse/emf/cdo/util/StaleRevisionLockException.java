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
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.CheckUtil;

/**
 * An unchecked exception being thrown when attempting to
 * {@link CDOView#lockObjects(java.util.Collection, org.eclipse.net4j.util.concurrent.IRWLockManager.LockType, long)
 * lock} <i>stale</i> objects.
 * <p>
 * An {@link CDOObject object} is considered stale if its {@link CDORevision revision} is older than the latest server
 * revision in the same {@link CDOBranch branch}.
 * 
 * @author Caspar De Groot
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class StaleRevisionLockException extends CDOException
{
  private static final long serialVersionUID = 5821185370877023119L;

  private final CDORevisionKey[] staleRevisions;

  public StaleRevisionLockException(CDORevisionKey[] staleRevisions)
  {
    CheckUtil.checkArg(staleRevisions, "staleRevisions");
    this.staleRevisions = staleRevisions;
  }

  public CDORevisionKey[] getStaleRevisions()
  {
    return staleRevisions;
  }
}
