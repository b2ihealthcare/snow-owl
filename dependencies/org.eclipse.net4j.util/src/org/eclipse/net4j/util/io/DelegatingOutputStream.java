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

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is the superclass of all classes that filter output streams. These streams sit on top of an already
 * existing output stream (the <i>underlying</i> output stream) which it uses as its basic sink of data, but possibly
 * transforming the data along the way or providing additional functionality.
 * <p>
 * The class <code>DelegatingOutputStream</code> itself simply overrides all methods of <code>OutputStream</code> with
 * versions that pass all requests to the underlying output stream. Subclasses of <code>DelegatingOutputStream</code>
 * may further override some of these methods as well as provide additional methods and fields.
 * <p>
 * <b>Note:</b> The only difference to {@link java.io.FilterOutputStream} is that <code>DelegatingOutputStream</code>
 * does <b>not</b> override {@link #write(byte[])} or {@link #write(byte[], int, int)} but rather exposes the original
 * implementations of <code>InputStream</code> which call {@link #write(int)} instead of their delegate counterparts.
 * 
 * @author Eike Stepper
 */
public class DelegatingOutputStream extends OutputStream
{
  /**
   * The underlying output stream to be filtered.
   */
  protected OutputStream out;

  /**
   * Creates an output stream filter built on top of the specified underlying output stream.
   * 
   * @param out
   *          the underlying output stream to be assigned to the field <tt>this.out</tt> for later use, or
   *          <code>null</code> if this instance is to be created without an underlying stream.
   */
  public DelegatingOutputStream(OutputStream out)
  {
    this.out = out;
  }

  public OutputStream getDelegate()
  {
    return out;
  }

  /**
   * Writes the specified <code>byte</code> to this output stream.
   * <p>
   * The <code>write</code> method of <code>DelegatingOutputStream</code> calls the <code>write</code> method of its
   * underlying output stream, that is, it performs <tt>out.write(b)</tt>.
   * <p>
   * Implements the abstract <tt>write</tt> method of <tt>OutputStream</tt>.
   * 
   * @param b
   *          the <code>byte</code>.
   * @exception IOException
   *              if an I/O error occurs.
   */
  @Override
  public void write(int b) throws IOException
  {
    out.write(b);
  }

  /**
   * Flushes this output stream and forces any buffered output bytes to be written out to the stream.
   * <p>
   * The <code>flush</code> method of <code>DelegatingOutputStream</code> calls the <code>flush</code> method of its
   * underlying output stream.
   * 
   * @exception IOException
   *              if an I/O error occurs.
   * @see DelegatingOutputStream#out
   */
  @Override
  public void flush() throws IOException
  {
    out.flush();
  }

  /**
   * Closes this output stream and releases any system resources associated with the stream.
   * <p>
   * The <code>close</code> method of <code>DelegatingOutputStream</code> calls its <code>flush</code> method, and then
   * calls the <code>close</code> method of its underlying output stream.
   * 
   * @exception IOException
   *              if an I/O error occurs.
   * @see DelegatingOutputStream#flush()
   * @see DelegatingOutputStream#out
   */
  @Override
  public void close() throws IOException
  {
    try
    {
      flush();
    }
    catch (IOException ignored)
    {
    }

    out.close();
  }
}
