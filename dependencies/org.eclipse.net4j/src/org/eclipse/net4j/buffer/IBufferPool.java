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
package org.eclipse.net4j.buffer;

/**
 * Provides clients with the ability to obtain and retain pooled {@link IBuffer}s.
 * 
 * @author Eike Stepper
 */
public interface IBufferPool extends IBufferProvider
{
  /**
   * Tries to remove a single buffer from this <code>BufferPool</code> and {@link IBuffer#release() release} it.
   * 
   * @return <code>true</code> if a buffer could be evicted, <code>false</code> otherwise.
   */
  public boolean evictOne();

  /**
   * Tries to remove as many buffers from this <code>BufferPool</code> and {@link IBuffer#release() release} them as are
   * needed to let a given maximum number of buffers survive in the pool.
   * 
   * @return The number of buffers that could be evicted.
   */
  public int evict(int survivors);

  /**
   * Offers additional introspection features for {@link IBufferPool}s.
   * 
   * @author Eike Stepper
   */
  public interface Introspection extends IBufferPool, IBufferProvider.Introspection
  {
    /**
     * Returns the number of buffers that are currently pooled in this <code>BufferPool</code>.
     */
    public int getPooledBuffers();
  }
}
