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

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Eike Stepper
 */
public class GZIPStreamWrapper implements IStreamWrapper
{
  public GZIPStreamWrapper()
  {
  }

  public GZIPInputStream wrapInputStream(InputStream in) throws IOException
  {
    if (in instanceof GZIPInputStream)
    {
      return (GZIPInputStream)in;
    }

    return new GZIPInputStream(in);
  }

  public GZIPOutputStream wrapOutputStream(OutputStream out) throws IOException
  {
    if (out instanceof GZIPOutputStream)
    {
      return (GZIPOutputStream)out;
    }

    return new GZIPOutputStream(out);
  }

  public void finishInputStream(InputStream in) throws IOException
  {
  }

  public void finishOutputStream(OutputStream out) throws IOException
  {
    ((GZIPOutputStream)out).finish();
  }

  /**
   * TODO Move or remove me
   */
  public static void main(String[] args) throws Exception
  {
    final PipedOutputStream pos = new PipedOutputStream();
    final PipedInputStream pis = new PipedInputStream(pos);

    final GZIPOutputStream gos = new GZIPOutputStream(pos);
    final byte[] out = "eike".getBytes(); //$NON-NLS-1$

    Thread thread = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          GZIPInputStream gis = new GZIPInputStream(pis);

          byte[] in = new byte[out.length];
          gis.read(in);
          gis.close();
        }
        catch (IOException ex)
        {
          throw new IORuntimeException(ex);
        }
      }
    };

    thread.start();
    ConcurrencyUtil.sleep(1000);

    gos.write(out);
    gos.close();

    ConcurrencyUtil.sleep(2000);
  }
}
