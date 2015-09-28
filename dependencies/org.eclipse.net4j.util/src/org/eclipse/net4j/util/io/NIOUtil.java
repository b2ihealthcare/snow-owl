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

import org.eclipse.net4j.util.ImplementationError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author Eike Stepper
 */
public final class NIOUtil
{
  private NIOUtil()
  {
  }

  /**
   * TODO Look at {@link #copy(File, File, boolean)}
   */
  public static void copyFile(File source, File target)
  {
    // http://www.javalobby.org/java/forums/t17036.html
    // http://java.sun.com/developer/JDCTechTips/2002/tt0507.html#tip1
    FileChannel sourceChannel = null;
    FileChannel targetChannel = null;

    try
    {
      if (!target.getParentFile().exists())
      {
        target.getParentFile().mkdirs();
      }

      if (!target.exists())
      {
        target.createNewFile();
      }

      sourceChannel = new FileInputStream(source).getChannel();
      targetChannel = new FileOutputStream(target).getChannel();

      long size = sourceChannel.size();
      long transfered = sourceChannel.transferTo(0, size, targetChannel);
      if (transfered != size)
      {
        throw new ImplementationError("Seems as if a loop must be implemented here"); //$NON-NLS-1$
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(sourceChannel);
      IOUtil.closeSilent(targetChannel);
    }
  }

  /**
   * Copy source file to destination. If destination is a path then source file name is appended. If destination file
   * exists then: overwrite=true - destination file is replaced; overwite=false - exception is thrown.
   * 
   * @param src
   *          source file
   * @param dst
   *          destination file or path
   * @param overwrite
   *          overwrite destination file
   * @exception IOException
   *              I/O problem
   */
  @SuppressWarnings("unused")
  private static void copy(final File src, File dst, final boolean overwrite) throws IOException
  {
    if (!src.isFile() || !src.exists())
    {
      throw new IllegalArgumentException("Source file '" + src.getAbsolutePath() + "' not found!"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (dst.exists())
    {
      if (dst.isDirectory())
      {
        // name
        dst = new File(dst, src.getName());
      }
      else if (dst.isFile())
      {
        if (!overwrite)
        {
          throw new IllegalArgumentException("Destination file '" + dst.getAbsolutePath() + "' already exists!"); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
      else
      {
        throw new IllegalArgumentException("Invalid destination object '" + dst.getAbsolutePath() + "'!"); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    File dstParent = dst.getParentFile();
    if (!dstParent.exists())
    {
      if (!dstParent.mkdirs())
      {
        throw new IOException("Failed to create directory " + dstParent.getAbsolutePath()); //$NON-NLS-1$
      }
    }

    long fileSize = src.length();
    if (fileSize > 20971520l)
    { // for larger files (20Mb) use streams
      FileInputStream in = new FileInputStream(src);
      FileOutputStream out = new FileOutputStream(dst);
      try
      {
        int doneCnt = -1, bufSize = 32768;
        byte buf[] = new byte[bufSize];
        while ((doneCnt = in.read(buf, 0, bufSize)) >= 0)
        {
          if (doneCnt == 0)
          {
            Thread.yield();
          }
          else
          {
            out.write(buf, 0, doneCnt);
          }
        }

        out.flush();
      }
      finally
      {
        try
        {
          in.close();
        }
        catch (IOException e)
        {
        }

        try
        {
          out.close();
        }
        catch (IOException e)
        {
        }
      }
    }
    else
    { // smaller files, use channels
      FileInputStream fis = new FileInputStream(src);
      FileOutputStream fos = new FileOutputStream(dst);
      FileChannel in = fis.getChannel(), out = fos.getChannel();

      try
      {
        long offs = 0, doneCnt = 0, copyCnt = Math.min(65536, fileSize);
        do
        {
          doneCnt = in.transferTo(offs, copyCnt, out);
          offs += doneCnt;
          fileSize -= doneCnt;
        }

        while (fileSize > 0);
      }
      finally
      { // cleanup
        try
        {
          in.close();
        }
        catch (IOException e)
        {
        }

        try
        {
          out.close();
        }
        catch (IOException e)
        {
        }

        try
        {
          fis.close();
        }
        catch (IOException e)
        {
        }

        try
        {
          fos.close();
        }
        catch (IOException ex)
        {
        }
      }
    }
  }
}
