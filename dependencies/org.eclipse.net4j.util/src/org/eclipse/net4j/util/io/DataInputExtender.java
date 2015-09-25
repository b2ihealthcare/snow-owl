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

import java.io.DataInput;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class DataInputExtender implements ExtendedDataInput
{
  private DataInput input;

  public DataInputExtender(DataInput input)
  {
    this.input = input;
  }

  public boolean readBoolean() throws IOException
  {
    return input.readBoolean();
  }

  public byte readByte() throws IOException
  {
    return input.readByte();
  }

  public char readChar() throws IOException
  {
    return input.readChar();
  }

  public double readDouble() throws IOException
  {
    return input.readDouble();
  }

  public float readFloat() throws IOException
  {
    return input.readFloat();
  }

  public void readFully(byte[] b, int off, int len) throws IOException
  {
    input.readFully(b, off, len);
  }

  public void readFully(byte[] b) throws IOException
  {
    input.readFully(b);
  }

  public int readInt() throws IOException
  {
    return input.readInt();
  }

  public String readLine() throws IOException
  {
    return input.readLine();
  }

  public long readLong() throws IOException
  {
    return input.readLong();
  }

  public short readShort() throws IOException
  {
    return input.readShort();
  }

  public int readUnsignedByte() throws IOException
  {
    return input.readUnsignedByte();
  }

  public int readUnsignedShort() throws IOException
  {
    return input.readUnsignedShort();
  }

  public String readUTF() throws IOException
  {
    return input.readUTF();
  }

  public byte[] readByteArray() throws IOException
  {
    return ExtendedIOUtil.readByteArray(input);
  }

  public Object readObject() throws IOException
  {
    return ExtendedIOUtil.readObject(input);
  }

  public Object readObject(ClassLoader classLoader) throws IOException
  {
    return ExtendedIOUtil.readObject(input, classLoader);
  }

  public Object readObject(ClassResolver classResolver) throws IOException
  {
    return ExtendedIOUtil.readObject(input, classResolver);
  }

  public String readString() throws IOException
  {
    return ExtendedIOUtil.readString(input);
  }

  /**
   * @since 3.0
   */
  public <T extends Enum<?>> T readEnum(Class<T> type) throws IOException
  {
    return ExtendedIOUtil.readEnum(input, type);
  }

  public int skipBytes(int n) throws IOException
  {
    return input.skipBytes(n);
  }
}
