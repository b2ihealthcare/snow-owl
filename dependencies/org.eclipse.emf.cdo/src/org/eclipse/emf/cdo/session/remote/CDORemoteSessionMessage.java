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
package org.eclipse.emf.cdo.session.remote;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * A message from a {@link CDORemoteSession remote session}.
 * 
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDORemoteSessionMessage
{
  private String type;

  private Priority priority;

  private byte[] data;

  public CDORemoteSessionMessage(String type, Priority priority, byte[] data)
  {
    CheckUtil.checkArg(type, "type");
    CheckUtil.checkArg(priority, "priority");

    this.type = type;
    this.priority = priority;
    this.data = data;
  }

  public CDORemoteSessionMessage(String type, Priority priority)
  {
    this(type, priority, null);
  }

  public CDORemoteSessionMessage(String type, byte[] data)
  {
    this(type, Priority.NORMAL, data);
  }

  public CDORemoteSessionMessage(String type)
  {
    this(type, Priority.NORMAL, null);
  }

  public CDORemoteSessionMessage(ExtendedDataInput in) throws IOException
  {
    type = in.readString();
    priority = Priority.values()[in.readByte()];
    data = in.readByteArray();
  }

  public synchronized void write(ExtendedDataOutput out) throws IOException
  {
    out.writeString(type);
    out.writeByte(priority.ordinal());
    out.writeByteArray(data);
  }

  /**
   * Returns the type of this message that enables {@link IListener message handlers} to decide whether to react on this
   * message or not.
   * 
   * @return the message type, never <code>null</code>.
   */
  public String getType()
  {
    return type;
  }

  /**
   * Returns the priority of this message.
   * 
   * @return the message priority, never <code>null</code>.
   */
  public Priority getPriority()
  {
    return priority;
  }

  /**
   * Returns a copy of this message's data as a byte array. Thread-safety for the message data is ensured internally.
   */
  public synchronized byte[] getData()
  {
    return copyData(data);
  }

  /**
   * Sets the data of this message as a byte array. Thread-safety for the message data is ensured internally.
   */
  public synchronized void setData(byte[] data)
  {
    this.data = copyData(data);
  }

  /**
   * Returns a copy of this message's data as an extended input stream. Thread-safety for the message data is ensured
   * internally, but not for the wrapping stream!
   */
  public ExtendedDataInputStream getInputStream()
  {
    byte[] copy;
    synchronized (this)
    {
      copy = copyData(data);
      if (copy == null)
      {
        return null;
      }
    }

    return new ExtendedDataInputStream(new ByteArrayInputStream(copy));
  }

  /**
   * Sets the data of this message as an extended output stream. Thread-safety for the message data is ensured
   * internally, but not for the wrapping stream!
   */
  public ExtendedDataOutputStream getOutputStream()
  {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    return new ExtendedDataOutputStream(baos)
    {
      @Override
      public void close() throws IOException
      {
        super.close();
        setData(baos.toByteArray());
      }
    };
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Message[type={0}, priority={1}, data={3}]", type, priority, HexUtil.bytesToHex(data));
  }

  private byte[] copyData(byte[] src)
  {
    if (src == null)
    {
      return null;
    }

    byte[] copy = new byte[src.length];
    System.arraycopy(src, 0, copy, 0, src.length);
    return copy;
  }

  /**
   * Enumerates the possible {@link CDORemoteSessionMessage remote session message} priorities.
   * 
   * @author Eike Stepper
   */
  public enum Priority
  {
    VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH
  }
}
