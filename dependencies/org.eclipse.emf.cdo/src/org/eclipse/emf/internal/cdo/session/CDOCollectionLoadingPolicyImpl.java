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
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.collection.MoveableList;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOCollectionLoadingPolicyImpl implements CDOCollectionLoadingPolicy
{
  private CDOSession session;

  private int initialChunkSize;

  private int resolveChunkSize;

  public CDOCollectionLoadingPolicyImpl(int initialChunkSize, int resolveChunkSize)
  {
    this.resolveChunkSize = resolveChunkSize <= 0 ? CDORevision.UNCHUNKED : resolveChunkSize;
    this.initialChunkSize = initialChunkSize < 0 ? resolveChunkSize : initialChunkSize;
  }

  public CDOSession getSession()
  {
    return session;
  }

  public void setSession(CDOSession session)
  {
    this.session = session;
  }

  public int getInitialChunkSize()
  {
    return initialChunkSize;
  }

  public int getResolveChunkSize()
  {
    return resolveChunkSize;
  }

  public void resolveAllProxies(CDORevision revision, EStructuralFeature feature)
  {
    doResolveProxy(revision, feature, 0, 0, Integer.MAX_VALUE);
  }

  public Object resolveProxy(CDORevision rev, EStructuralFeature feature, int accessIndex, int serverIndex)
  {
    int chunkSize = resolveChunkSize;
    if (chunkSize == CDORevision.UNCHUNKED)
    {
      // Can happen if CDOSession.setReferenceChunkSize() was called meanwhile
      chunkSize = Integer.MAX_VALUE;
    }

    return doResolveProxy(rev, feature, accessIndex, serverIndex, chunkSize);
  }

  private Object doResolveProxy(CDORevision rev, EStructuralFeature feature, int accessIndex, int serverIndex,
      int chunkSize)
  {
    // Get proxy values
    InternalCDORevision revision = (InternalCDORevision)rev;
    int fetchIndex = serverIndex;

    MoveableList<Object> list = revision.getList(feature);
    int size = list.size();
    int fromIndex = accessIndex;
    int toIndex = accessIndex;
    boolean minReached = false;
    boolean maxReached = false;
    boolean alternation = false;
    for (int i = 0; i < chunkSize; i++)
    {
      if (alternation)
      {
        // XXX (apeteri): Check indexes in reference proxies when extending to the right
        if (!maxReached && toIndex < size - 1 && canExtendToItem(list, toIndex + 1, accessIndex, serverIndex))
        {
          ++toIndex;
        }
        else
        {
          maxReached = true;
        }

        if (!minReached)
        {
          alternation = false;
        }
      }
      else
      {
        // XXX (apeteri): Check indexes in reference proxies when extending to the left
        if (!minReached && fromIndex > 0 && canExtendToItem(list, fromIndex - 1, accessIndex, serverIndex))
        {
          --fromIndex;
        }
        else
        {
          minReached = true;
        }

        if (!maxReached)
        {
          alternation = true;
        }
      }

      if (minReached && maxReached)
      {
        break;
      }
    }

    CDOSessionProtocol protocol = ((InternalCDOSession)session).getSessionProtocol();
    return protocol.loadChunk(revision, feature, accessIndex, fetchIndex, fromIndex, toIndex);
  }

  /**
   * @since 4.1.1
   */
  private boolean canExtendToItem(MoveableList<Object> list, int runningIndex, int accessIndex, int serverIndex)
  {
    final Object candidateObject = list.get(runningIndex);

    if (!(candidateObject instanceof CDOElementProxy))
    {
      return false;
    }

    final int candidateIndex = ((CDOElementProxy)candidateObject).getIndex();
    return candidateIndex == serverIndex - accessIndex + runningIndex;
  }
}
