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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.util.collection.Pair;

import java.util.List;
import java.util.Map;

/**
 * A strategy used to customize the default conflict resolution behaviour of {@link CDOTransaction transactions}.
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public interface CDOConflictResolver2 extends CDOConflictResolver
{
  /**
   * Resolves conflicts after remote invalidations arrived for objects that are locally dirty or detached.
   * <p>
   * Depending on the decisions taken to resolve the conflict, it may be necessary to adjust the notifications that will
   * be sent to the adapters in the current transaction. This can be achieved by adjusting the {@link CDORevisionDelta}
   * in <code>deltas</code>.
   * 
   * @param conflicts
   *          A map that contains the local objects with conflicts as the keys. Each value in this map is a {@link Pair
   *          pair} that <b>optionally</b> contains the old remote revision (<i>ancestor</i>) as element1 and the remote
   *          delta as element2. Any of the pair elements can be <code>null</code> if it is not possible to determine it
   *          locally (depends on local revision caching and server behaviour regarding transmission of deltas instead
   *          of invalidations).
   */
  public void resolveConflicts(Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts,
      List<CDORevisionDelta> allRemoteDeltas);
}
