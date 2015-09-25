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

import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public final class TMPUtil
{
  /**
   * @since 3.0
   */
  public static final String TEMP_FOLDER_PROPERTY = "org.eclipse.net4j.util.io.tmpdir";

  /**
   * @since 2.0
   */
  public static final String SYSTEM_TEMP_FOLDER = OMPlatform.INSTANCE.getProperty("java.io.tmpdir"); //$NON-NLS-1$

  private static File tempFolder;

  private TMPUtil()
  {
  }

  /**
   * @since 3.0
   */
  public synchronized static File getTempFolder()
  {
    if (tempFolder == null)
    {
      String path = OMPlatform.INSTANCE.getProperty(TEMP_FOLDER_PROPERTY);
      if (path == null)
      {
        path = getPathFromUserHome();
        if (path == null)
        {
          path = SYSTEM_TEMP_FOLDER;
        }
      }

      tempFolder = new File(path);

      if (!tempFolder.exists())
      {
        tempFolder.mkdirs();
      }
    }

    return tempFolder;
  }

  private static String getPathFromUserHome()
  {
    File home = new File(OMPlatform.INSTANCE.getProperty("user.home"));
    File file = new File(home, TEMP_FOLDER_PROPERTY);
    if (file.exists() && file.isFile())
    {
      InputStream in = null;

      try
      {
        in = new FileInputStream(file);

        Properties properties = new Properties();
        properties.load(in);

        return properties.getProperty("path");
      }
      catch (Exception ignore)
      {
      }
      finally
      {
        IOUtil.closeSilent(in);
      }
    }

    return null;
  }

  /**
   * @since 3.0
   */
  public static void setTempFolder(String tempFolder)
  {
    TMPUtil.tempFolder = new File(tempFolder);
  }

  public static File createTempFolder() throws IORuntimeException
  {
    return createTempFolder("tmp"); //$NON-NLS-1$
  }

  public static File createTempFolder(String prefix) throws IORuntimeException
  {
    return createTempFolder(prefix, ""); //$NON-NLS-1$
  }

  public static File createTempFolder(String prefix, String suffix) throws IORuntimeException
  {
    return createTempFolder(prefix, suffix, getTempFolder());
  }

  public static File createTempFolder(String prefix, String suffix, File directory) throws IORuntimeException
  {
    try
    {
      File tmp = File.createTempFile(prefix, suffix, directory);
      String tmpPath = tmp.getAbsolutePath();
      tmp.delete();
      tmp = new File(tmpPath);
      tmp.mkdirs();
      return tmp;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * @since 3.1
   */
  public static File createTempFile() throws IORuntimeException
  {
    return createTempFile("tmp"); //$NON-NLS-1$
  }

  /**
   * @since 3.1
   */
  public static File createTempFile(String prefix) throws IORuntimeException
  {
    return createTempFile(prefix, ""); //$NON-NLS-1$
  }

  /**
   * @since 3.1
   */
  public static File createTempFile(String prefix, String suffix) throws IORuntimeException
  {
    return createTempFile(prefix, suffix, getTempFolder());
  }

  /**
   * @since 3.1
   */
  public static File createTempFile(String prefix, String suffix, File directory) throws IORuntimeException
  {
    try
    {
      return File.createTempFile(prefix, suffix, directory);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }
}
