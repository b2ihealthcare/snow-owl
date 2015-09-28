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
package org.eclipse.emf.cdo.common.commit;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

/**
 * A {@link CDOChangeKindProvider change kind provider} with detailed information about {@link #getNewObjects() new},
 * {@link #getChangedObjects() changed} and {@link #getDetachedObjects() detached} objects.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOChangeSetData extends CDOChangeKindProvider
{
  /**
   * Returns <code>true</code>, if this change set data does not contain any changes, <code>false</code> otherwise.
   */
  public boolean isEmpty();

  /**
   * Returns a deep copy of this change set data.
   * 
   * @since 4.0
   */
  public CDOChangeSetData copy();

  /**
   * Changes the internal state of this change set data by adding the changes of the given change set data.
   * 
   * @since 4.0
   */
  public void merge(CDOChangeSetData changeSetData);

  /**
   * Returns a collection of keys denoting which revisions have been added in the context of a commit operation.
   * Depending on various conditions like change subscriptions particular elements can also be full {@link CDORevision
   * revisions}.
   */
  public List<CDOIDAndVersion> getNewObjects();

  /**
   * Returns a collection of revision keys denoting which (original) revisions have been changed in the context of a
   * commit operation. Depending on various conditions like change subscriptions particular elements can also be full
   * {@link CDORevisionDelta revision deltas}.
   */
  public List<CDORevisionKey> getChangedObjects();

  /**
   * Returns a collection of keys denoting which revisions have been revised (corresponds to detached objects) in the
   * context of a commit operation. Depending on various conditions the version part of particular elements can be
   * {@link CDOBranchVersion#UNSPECIFIED_VERSION unspecified}.
   */
  public List<CDOIDAndVersion> getDetachedObjects();

  /**
   * @since 4.1
   */
  public Map<CDOID, CDOChangeKind> getChangeKinds();
}
