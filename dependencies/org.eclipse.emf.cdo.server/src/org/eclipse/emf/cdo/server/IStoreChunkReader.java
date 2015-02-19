/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 210868
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;

/**
 * Reads {@link Chunk chunks} of
 * {@link org.eclipse.emf.cdo.session.CDOSession.Options#setCollectionLoadingPolicy(CDOCollectionLoadingPolicy)
 * partially loaded} lists from a physical data storage backend.
 * 
 * @author Eike Stepper
 * @apiviz.uses {@link IStoreChunkReader.Chunk} - - reads
 */
public interface IStoreChunkReader
{
  /**
   * @since 2.0
   */
  public IStoreAccessor getAccessor();

  public CDORevision getRevision();

  /**
   * @since 2.0
   */
  public EStructuralFeature getFeature();

  public void addSimpleChunk(int index);

  /**
   * @param fromIndex
   *          Inclusive value.
   * @param toIndex
   *          Exclusive value.
   */
  public void addRangedChunk(int fromIndex, int toIndex);

  public List<Chunk> executeRead();

  /**
   * Represents a {@link List#subList(int, int) sublist} of consecutive elements that are subject to <i>partial
   * collection loading</i>.
   * 
   * @author Eike Stepper
   */
  public static class Chunk
  {
    private int startIndex;

    private Object ids;

    public Chunk(int startIndex)
    {
      this.startIndex = startIndex;
    }

    public Chunk(int startIndex, int size)
    {
      this(startIndex);
      ids = new Object[size];
    }

    public int getStartIndex()
    {
      return startIndex;
    }

    public int size()
    {
      return ids instanceof Object[] ? ((Object[])ids).length : 1;
    }

    /**
     * @since 2.0
     */
    public Object get(int indexInChunk)
    {
      if (ids instanceof Object[])
      {
        return ((Object[])ids)[indexInChunk];
      }

      if (indexInChunk == 0)
      {
        return ids;
      }

      throw new ArrayIndexOutOfBoundsException(indexInChunk);
    }

    /**
     * @since 2.0
     */
    public void add(int indexInChunk, Object value)
    {
      if (ids instanceof Object[])
      {
        ((Object[])ids)[indexInChunk] = value;
      }
      else
      {
        if (indexInChunk == 0)
        {
          ids = value;
          return;
        }

        throw new ArrayIndexOutOfBoundsException(indexInChunk);
      }
    }
  }
}
