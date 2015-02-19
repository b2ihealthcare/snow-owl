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
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 */
public abstract class BufferProvider extends Lifecycle implements IBufferProvider.Introspection
{
  private short bufferCapacity;

  private long providedBuffers;

  private long retainedBuffers;

  public BufferProvider(short bufferCapacity)
  {
    this.bufferCapacity = bufferCapacity;
  }

  public final long getProvidedBuffers()
  {
    return providedBuffers;
  }

  public final long getRetainedBuffers()
  {
    return retainedBuffers;
  }

  public final short getBufferCapacity()
  {
    return bufferCapacity;
  }

  public final IBuffer provideBuffer()
  {
    ++providedBuffers;
    return doProvideBuffer();
  }

  public final void retainBuffer(IBuffer buffer)
  {
    ++retainedBuffers;
    doRetainBuffer(buffer);
  }

  @Override
  public String toString()
  {
    return "BufferProvider[capacity=" + bufferCapacity + "]"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  protected abstract IBuffer doProvideBuffer();

  protected abstract void doRetainBuffer(IBuffer buffer);
}
