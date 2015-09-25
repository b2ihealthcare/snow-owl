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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public final class BufferUtil
{
  private static final byte FALSE = (byte)0;

  private static final byte TRUE = (byte)1;

  public static final String UTF8_CHAR_SET_NAME = "UTF-8"; //$NON-NLS-1$

  private BufferUtil()
  {
  }

  public static byte[] toUTF8(String str)
  {
    if (str == null)
    {
      return new byte[0];
    }

    try
    {
      byte[] bytes = str.getBytes(UTF8_CHAR_SET_NAME);
      String test = new String(bytes, UTF8_CHAR_SET_NAME);
      if (!str.equals(test))
      {
        throw new IllegalArgumentException("String not encodable: " + str); //$NON-NLS-1$
      }

      return bytes;
    }
    catch (UnsupportedEncodingException ex)
    {
      // This should really not happen
      throw new RuntimeException(ex);
    }
  }

  public static String fromUTF8(byte[] bytes)
  {
    try
    {
      return new String(bytes, UTF8_CHAR_SET_NAME);
    }
    catch (UnsupportedEncodingException ex)
    {
      // This should really not happen
      throw new RuntimeException(ex);
    }
  }

  public static void putObject(ByteBuffer byteBuffer, Object object) throws IOException
  {
    if (object != null)
    {
      byteBuffer.put(TRUE);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream stream = new ObjectOutputStream(baos);
      stream.writeObject(object);

      byte[] array = baos.toByteArray();
      putByteArray(byteBuffer, array);
    }
    else
    {
      byteBuffer.put(FALSE);
    }
  }

  public static Object getObject(ByteBuffer byteBuffer) throws IOException, ClassNotFoundException
  {
    boolean nonNull = byteBuffer.get() == TRUE;
    if (nonNull)
    {
      byte[] array = getByteArray(byteBuffer);
      ByteArrayInputStream bais = new ByteArrayInputStream(array);
      ObjectInputStream stream = new ObjectInputStream(bais);
      return stream.readObject();
    }

    return null;
  }

  public static void putByteArray(ByteBuffer byteBuffer, byte[] array)
  {
    byteBuffer.putShort((short)array.length);
    if (array.length != 0)
    {
      byteBuffer.put(array);
    }
  }

  public static byte[] getByteArray(ByteBuffer byteBuffer)
  {
    short length = byteBuffer.getShort();
    byte[] array = new byte[length];
    if (length != 0)
    {
      byteBuffer.get(array);
    }

    return array;
  }

  public static void putUTF8(ByteBuffer byteBuffer, String str)
  {
    byte[] bytes = BufferUtil.toUTF8(str);
    if (bytes.length > byteBuffer.remaining())
    {
      throw new IllegalArgumentException("String too long: " + str); //$NON-NLS-1$
    }

    putByteArray(byteBuffer, bytes);
  }
}
