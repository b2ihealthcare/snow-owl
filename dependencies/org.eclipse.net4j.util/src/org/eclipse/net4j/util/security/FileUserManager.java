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
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class FileUserManager extends UserManager
{
  protected String fileName;

  public FileUserManager()
  {
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (fileName == null)
    {
      throw new IllegalStateException("fileName == null"); //$NON-NLS-1$
    }

    File file = new File(fileName);
    if (file.exists())
    {
      if (!file.isFile())
      {
        throw new IllegalStateException("Not a file: " + fileName); //$NON-NLS-1$
      }
    }
    else
    {
      throw new FileNotFoundException("User manager file not found: " + fileName);
    }
  }

  @Override
  protected void load(Map<String, char[]> users) throws IORuntimeException
  {
    File file = new File(fileName);
    if (!file.exists())
    {
      return;
    }

    FileInputStream stream = IOUtil.openInputStream(new File(fileName));
    try
    {
      load(users, stream);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(stream);
    }
  }

  protected void load(Map<String, char[]> users, InputStream stream) throws IOException
  {
    Properties properties = new Properties();
    properties.load(stream);
    for (Entry<Object, Object> entry : properties.entrySet())
    {
      String userID = (String)entry.getKey();
      char[] password = ((String)entry.getValue()).toCharArray();
      users.put(userID, password);
    }
  }

  @Override
  protected void save(Map<String, char[]> users) throws IORuntimeException
  {
    File file = new File(fileName);
    if (!file.exists())
    {
      return;
    }

    FileOutputStream stream = IOUtil.openOutputStream(new File(fileName));
    try
    {
      save(users, stream);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(stream);
    }
  }

  protected void save(Map<String, char[]> users, FileOutputStream stream) throws IOException
  {
    Properties properties = new Properties();
    for (Entry<String, char[]> entry : users.entrySet())
    {
      properties.put(entry.getKey(), new String(entry.getValue()));
    }

    String comment = MessageFormat.format("User database {0,date} {0,time,HH:mm:ss:SSS}", System.currentTimeMillis());
    properties.store(stream, comment);
  }
}
