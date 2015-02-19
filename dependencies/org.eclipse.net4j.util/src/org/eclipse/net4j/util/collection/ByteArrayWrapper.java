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
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.CheckUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public final class ByteArrayWrapper
{
  private final byte[] data;

  public ByteArrayWrapper(byte[] data)
  {
    CheckUtil.checkArg(data, "data");
    this.data = data;
  }

  public byte[] getData()
  {
    return data;
  }

  @Override
  public boolean equals(Object other)
  {
    if (other instanceof ByteArrayWrapper)
    {
      return Arrays.equals(data, ((ByteArrayWrapper)other).data);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return Arrays.hashCode(data);
  }

  @Override
  public String toString()
  {
    return data.toString();
  }

  public static Set<byte[]> toByteArray(Set<ByteArrayWrapper> wrappers)
  {
    if (wrappers == null)
    {
      return null;
    }

    Set<byte[]> result = new HashSet<byte[]>();
    for (ByteArrayWrapper wrapper : wrappers)
    {
      result.add(wrapper.getData());
    }

    return result;
  }
}
