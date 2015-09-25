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
import java.io.InputStream;

/**
 * A <code>DelegatingInputStream</code> contains some other input stream, which it uses as its basic source of data,
 * possibly transforming the data along the way or providing additional functionality. The class
 * <code>DelegatingInputStream</code> itself simply overrides all (see note below) methods of <code>InputStream</code>
 * with versions that pass all requests to the contained input stream. Subclasses of <code>DelegatingInputStream</code>
 * may further override some of these methods and may also provide additional methods and fields.
 * <p>
 * <b>Note:</b> The only difference to {@link java.io.FilterInputStream} is that <code>DelegatingInputStream</code> does
 * <b>not</b> override {@link #read(byte[])} or {@link #read(byte[], int, int)} but rather exposes the original
 * implementations of <code>InputStream</code> which call {@link #read()} instead of their delegate counterparts.
 * 
 * @author Eike Stepper
 */
public class DelegatingInputStream extends InputStream
{
  /**
   * The input stream to be filtered.
   */
  protected volatile InputStream in;

  /**
   * Creates a <code>DelegatingInputStream</code> by assigning the argument <code>in</code> to the field
   * <code>this.in</code> so as to remember it for later use.
   * 
   * @param in
   *          the underlying input stream, or <code>null</code> if this instance is to be created without an underlying
   *          stream.
   */
  protected DelegatingInputStream(InputStream in)
  {
    this.in = in;
  }

  public InputStream getDelegate()
  {
    return in;
  }

  /**
   * Reads the next byte of data from this input stream. The value byte is returned as an <code>int</code> in the range
   * <code>0</code> to <code>255</code>. If no byte is available because the end of the stream has been reached, the
   * value <code>-1</code> is returned. This method blocks until input data is available, the end of the stream is
   * detected, or an exception is thrown.
   * <p>
   * This method simply performs <code>in.read()</code> and returns the result.
   * 
   * @return the next byte of data, or <code>-1</code> if the end of the stream is reached.
   * @exception IOException
   *              if an I/O error occurs.
   * @see DelegatingInputStream#in
   */
  @Override
  public int read() throws IOException
  {
    return in.read();
  }

  /**
   * Skips over and discards <code>n</code> bytes of data from the input stream. The <code>skip</code> method may, for a
   * variety of reasons, end up skipping over some smaller number of bytes, possibly <code>0</code>. The actual number
   * of bytes skipped is returned.
   * <p>
   * This method simply performs <code>in.skip(n)</code>.
   * 
   * @param n
   *          the number of bytes to be skipped.
   * @return the actual number of bytes skipped.
   * @exception IOException
   *              if an I/O error occurs.
   */
  @Override
  public long skip(long n) throws IOException
  {
    return in.skip(n);
  }

  /**
   * Returns the number of bytes that can be read from this input stream without blocking.
   * <p>
   * This method simply performs <code>in.available()</code> and returns the result.
   * 
   * @return the number of bytes that can be read from the input stream without blocking.
   * @exception IOException
   *              if an I/O error occurs.
   * @see DelegatingInputStream#in
   */
  @Override
  public int available() throws IOException
  {
    return in.available();
  }

  /**
   * Closes this input stream and releases any system resources associated with the stream. This method simply performs
   * <code>in.close()</code>.
   * 
   * @exception IOException
   *              if an I/O error occurs.
   * @see DelegatingInputStream#in
   */
  @Override
  public void close() throws IOException
  {
    in.close();
  }

  /**
   * Marks the current position in this input stream. A subsequent call to the <code>reset</code> method repositions
   * this stream at the last marked position so that subsequent reads re-read the same bytes.
   * <p>
   * The <code>readlimit</code> argument tells this input stream to allow that many bytes to be read before the mark
   * position gets invalidated.
   * <p>
   * This method simply performs <code>in.mark(readlimit)</code>.
   * 
   * @param readlimit
   *          the maximum limit of bytes that can be read before the mark position becomes invalid.
   * @see DelegatingInputStream#in
   * @see DelegatingInputStream#reset()
   */
  @Override
  public synchronized void mark(int readlimit)
  {
    in.mark(readlimit);
  }

  /**
   * Repositions this stream to the position at the time the <code>mark</code> method was last called on this input
   * stream.
   * <p>
   * This method simply performs <code>in.reset()</code>.
   * <p>
   * Stream marks are intended to be used in situations where you need to read ahead a little to see what's in the
   * stream. Often this is most easily done by invoking some general parser. If the stream is of the type handled by the
   * parse, it just chugs along happily. If the stream is not of that type, the parser should toss an exception when it
   * fails. If this happens within readlimit bytes, it allows the outer code to reset the stream and try another parser.
   * 
   * @exception IOException
   *              if the stream has not been marked or if the mark has been invalidated.
   * @see DelegatingInputStream#in
   * @see DelegatingInputStream#mark(int)
   */
  @Override
  public synchronized void reset() throws IOException
  {
    in.reset();
  }

  /**
   * Tests if this input stream supports the <code>mark</code> and <code>reset</code> methods. This method simply
   * performs <code>in.markSupported()</code>.
   * 
   * @return <code>true</code> if this stream type supports the <code>mark</code> and <code>reset</code> method;
   *         <code>false</code> otherwise.
   * @see DelegatingInputStream#in
   * @see java.io.InputStream#mark(int)
   * @see java.io.InputStream#reset()
   */
  @Override
  public boolean markSupported()
  {
    return in.markSupported();
  }
}
