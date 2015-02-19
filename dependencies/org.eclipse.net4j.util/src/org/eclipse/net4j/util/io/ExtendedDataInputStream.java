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
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.io.ExtendedIOUtil.ClassResolver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eike Stepper
 */
public class ExtendedDataInputStream extends DataInputStream implements ExtendedDataInput
{
  public ExtendedDataInputStream(InputStream in)
  {
    super(in);
  }

  public byte[] readByteArray() throws IOException
  {
    return ExtendedIOUtil.readByteArray(this);
  }

  public String readString() throws IOException
  {
    return ExtendedIOUtil.readString(this);
  }

  public Object readObject() throws IOException
  {
    return ExtendedIOUtil.readObject(this);
  }

  /**
   * @since 3.0
   */
  public <T extends Enum<?>> T readEnum(Class<T> type) throws IOException
  {
    return ExtendedIOUtil.readEnum(this, type);
  }

  public Object readObject(ClassLoader classLoader) throws IOException
  {
    return ExtendedIOUtil.readObject(this, classLoader);
  }

  public Object readObject(ClassResolver classResolver) throws IOException
  {
    return ExtendedIOUtil.readObject(this, classResolver);
  }

  public static ExtendedDataInputStream wrap(InputStream stream)
  {
    if (stream instanceof ExtendedDataInputStream)
    {
      return (ExtendedDataInputStream)stream;
    }

    return new ExtendedDataInputStream(stream);
  }

  public static InputStream unwrap(InputStream stream)
  {
    if (stream instanceof ExtendedDataInputStream)
    {
      return ((ExtendedDataInputStream)stream).in;
    }

    return stream;
  }
}
