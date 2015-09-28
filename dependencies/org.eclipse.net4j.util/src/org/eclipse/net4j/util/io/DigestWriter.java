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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.MessageDigest;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class DigestWriter extends FilterWriter
{
  private boolean on = true;

  /**
   * The message digest associated with this stream.
   */
  protected MessageDigest digest;

  /**
   * Creates a digest writer, using the specified writer and message digest.
   * 
   * @param writer
   *          the writer.
   * @param digest
   *          the message digest to associate with this writer.
   */
  public DigestWriter(Writer writer, MessageDigest digest)
  {
    super(writer);
    setMessageDigest(digest);
  }

  /**
   * Returns the message digest associated with this writer.
   * 
   * @return the message digest associated with this writer.
   * @see #setMessageDigest(java.security.MessageDigest)
   */
  public MessageDigest getMessageDigest()
  {
    return digest;
  }

  /**
   * Associates the specified message digest with this writer.
   * 
   * @param digest
   *          the message digest to be associated with this writer.
   * @see #getMessageDigest()
   */
  public void setMessageDigest(MessageDigest digest)
  {
    this.digest = digest;
  }

  @Override
  public void write(int c) throws IOException
  {
    if (on)
    {
      updateDigest(c);
    }

    out.write(c);
  }

  @Override
  public void write(char cbuf[], int off, int len) throws IOException
  {
    if (on)
    {
      int end = off + len;
      for (int i = off; i < end; i++)
      {
        updateDigest(cbuf[i]);
      }
    }

    out.write(cbuf, off, len);
  }

  @Override
  public void write(String str, int off, int len) throws IOException
  {
    if (on)
    {
      int end = off + len;
      for (int i = off; i < end; i++)
      {
        updateDigest(str.charAt(i));
      }
    }

    out.write(str, off, len);
  }

  private void updateDigest(int c)
  {
    digest.update((byte)(c >>> 8 & 0xFF));
    digest.update((byte)(c >>> 0 & 0xFF));
  }

  /**
   * Turns the digest function on or off. The default is on. When it is on, a call to one of the <code>write</code>
   * methods results in an update on the message digest. But when it is off, the message digest is not updated.
   * 
   * @param on
   *          true to turn the digest function on, false to turn it off.
   */
  public void on(boolean on)
  {
    this.on = on;
  }

  /**
   * Prints a string representation of this digest output stream and its associated message digest object.
   */
  @Override
  public String toString()
  {
    return "[Digest Writer] " + digest.toString();
  }
}
