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

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class StoreChunkReader implements IStoreChunkReader
{
  private IStoreAccessor accessor;

  private CDORevision revision;

  private EStructuralFeature feature;

  private List<Chunk> chunks = new ArrayList<Chunk>(0);

  public StoreChunkReader(IStoreAccessor accessor, CDORevision revision, EStructuralFeature feature)
  {
    this.accessor = accessor;
    this.revision = revision;
    this.feature = feature;
  }

  public IStoreAccessor getAccessor()
  {
    return accessor;
  }

  public CDORevision getRevision()
  {
    return revision;
  }

  public EStructuralFeature getFeature()
  {
    return feature;
  }

  public List<Chunk> getChunks()
  {
    return chunks;
  }

  public void addSimpleChunk(int index)
  {
    chunks.add(new Chunk(index));
  }

  public void addRangedChunk(int fromIndex, int toIndex)
  {
    chunks.add(new Chunk(fromIndex, toIndex - fromIndex));
  }
}
