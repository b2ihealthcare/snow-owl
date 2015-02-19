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
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.InternalBuffer;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class BufferFactory extends BufferProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER, BufferFactory.class);

  public BufferFactory(short bufferCapacity)
  {
    super(bufferCapacity);
  }

  @Override
  protected IBuffer doProvideBuffer()
  {
    IBuffer buffer = new Buffer(this, getBufferCapacity());
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + buffer); //$NON-NLS-1$
    }

    return buffer;
  }

  @Override
  protected void doRetainBuffer(IBuffer buffer)
  {
    if (buffer instanceof InternalBuffer)
    {
      ((InternalBuffer)buffer).dispose();
    }

    buffer = null;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("BufferFactory[{0}]", getBufferCapacity()); //$NON-NLS-1$ 
  }
}
