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
 * Provides clients with the ability to obtain and retain {@link IBuffer}s.
 * 
 * @author Eike Stepper
 */
public interface IBufferProvider
{
  /**
   * Returns the capacity of the buffers provided by {@link #provideBuffer()} .
   */
  public short getBufferCapacity();

  /**
   * Provides a buffer from this <code>BufferProvider</code>.
   */
  public IBuffer provideBuffer();

  /**
   * Retains a buffer to this <code>BufferProvider</code>.
   */
  public void retainBuffer(IBuffer buffer);

  /**
   * Offers additional introspection features for {@link IBufferProvider}s.
   * 
   * @author Eike Stepper
   */
  public interface Introspection extends IBufferProvider
  {
    /**
     * Returns the number of buffers that have already been provided by this <code>BufferProvider</code>.
     */
    public long getProvidedBuffers();

    /**
     * Returns the number of buffers that have already been retained to this <code>BufferProvider</code>.
     */
    public long getRetainedBuffers();
  }
}
